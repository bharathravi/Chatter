package chatter.common;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

import static chatter.common.Constants.*;

/**
 * @author Bharath Ravi
 *
 * An "encrypted socket" that handles sending and receiving of encrypted data
 * over a standard socket. Its implementation is specifc to our usecase, since
 * this supports only two operations:
 * 1. readLine(), that reads a line of text from the socket.
 * 2. sendLine(), that sends a line of text over the socket.
 */
public class EncryptedSocket implements PublicKeyCommunicator{

  private Socket socket;
  private SecretKey secretKey;
  private CryptoService cryptoService;

  public EncryptedSocket(Socket socket)
      throws DiffieHellmanException, IOException, CryptoException {
    this.socket = socket;
    setTimeout(Constants.AUTHENTICATION_TIMEOUT);

    // Actual work in constructor is not great. But do it anyway :-/
    DiffieHelmanKeyGenerator dhGenerator = new DiffieHelmanKeyGenerator(this);
    this.secretKey = dhGenerator.generate();
    this.cryptoService = new CryptoService(secretKey);
    setTimeout(Constants.CHAT_TIMEOUT);
  }


  public byte[] getPublicKeyBytes() throws IOException {
    DataInputStream dis = new DataInputStream(socket.getInputStream());
    int length = dis.readInt();
    byte[] bytes = new byte[length];
    dis.read(bytes, 0, length);

    return bytes;
  }

  public void sendLine(String line) throws IOException, CryptoException {
    OutputStream output = socket.getOutputStream();
    String encryptedLine = cryptoService.encrypt(line) + '\n';
    output.write(encryptedLine.getBytes());
  }

  public void sendPublicKeyBytes(byte[] bytes) throws IOException {
    OutputStream output = socket.getOutputStream();
    DataOutputStream dos = new DataOutputStream(output);
    dos.writeInt(bytes.length);
    dos.write(bytes);
  }

  public String readLine() throws IOException, CryptoException {
    // TODO(bharath): Apply suitable decryption after reading.
    String line = "";
    try {
      BufferedInputStream inputStream = new BufferedInputStream(
          socket.getInputStream());
      InputStreamReader inputReader =
          new InputStreamReader(inputStream, "US-ASCII");

      int c;
      while((c=inputReader.read()) != '\n') {
        if (line.length() < TEXT_LIMIT) {
          // For too long a line, ignore the rest, even if it means failing to
          // decrypt something.
          line += (char)c;
        }
      }
    } catch (SocketException e) {
      throw e;
    }

   // System.out.println("enc:" + line);
   // System.out.println("dec:" + cryptoService.decrypt(line));

    return cryptoService.decrypt(line);
  }

  public void setTimeout(int timeout) throws SocketException {
    socket.setSoTimeout(timeout);
  }

  public void close() throws IOException {
    socket.close();
  }

  public boolean isClosed() {
    return socket.isClosed();
  }

}
