package common;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static common.Constants.*;

/**
 * @author Bharath Ravi
 *
 * An "encrypted socket" that handles sending and receiving of encrypted data
 * over a standard socket. Its implementation is specifc to our usecase, since
 * this supports only two operations:
 * 1. readLine(), that reads a line of text from the socket.
 * 2. sendLine(), that sends a line of text over the socket.
 */
public class EncryptedSocket implements PublicKeyCommunicator {
  private Socket socket;
  private SecretKey secretKey;
  private CryptoService cryptoService;

  public EncryptedSocket(Socket socket)
      throws DiffieHellmanException, CryptoException, IOException {
    this.socket = socket;

    // Actual work in constructor is not great. But do it anyway :-/
    DiffieHelmanKeyGenerator dhGenerator = new DiffieHelmanKeyGenerator(this);
    try {
      this.secretKey = dhGenerator.generate();
    } catch (PublicKeyCommunicationException e) {
      throw new IOException(e);
    } catch (InvalidKeySpecException e) {
      throw new DiffieHellmanException(e);
    } catch (InvalidAlgorithmParameterException e) {
      throw new DiffieHellmanException(e);
    } catch (NoSuchAlgorithmException e) {
      throw new DiffieHellmanException(e);
    } catch (InvalidKeyException e) {
      throw new DiffieHellmanException(e);
    }
    System.out.println(secretKey.getAlgorithm());
    System.out.println(secretKey.getEncoded().length);
    this.cryptoService = new CryptoService(secretKey);
  }


  public byte[] getPublicKeyBytes() throws PublicKeyCommunicationException {
    DataInputStream dis;
    try {
      dis = new DataInputStream(socket.getInputStream());
      int length = dis.readInt();
      byte[] bytes = new byte[length];
      dis.read(bytes, 0, length);

      return bytes;
    } catch (IOException e) {
      throw new PublicKeyCommunicationException(e);
    }
  }

  public void sendLine(String line) throws IOException, CryptoException {
    OutputStream output =  socket.getOutputStream();
    String encryptedLine = cryptoService.encrypt(line) + '\n';
    output.write(encryptedLine.getBytes());
  }

  public void sendPublicKeyBytes(byte[] bytes)
      throws PublicKeyCommunicationException {
    OutputStream output = null;
    try {
      output = socket.getOutputStream();
      DataOutputStream dos = new DataOutputStream(output);
      dos.writeInt(bytes.length);
      dos.write(bytes);
    } catch (IOException e) {
      throw new PublicKeyCommunicationException(e);
    }

  }

  public String readLine() throws CryptoException, IOException {
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
    } catch (UnsupportedEncodingException e) {
      throw new IOException(e);
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
