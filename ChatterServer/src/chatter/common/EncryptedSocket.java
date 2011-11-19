package chatter.common;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.TimeoutException;

/**
 * @author Bharath Ravi
 * @author Kapil Goel
 * @author Alban
 *
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
    DataOutputStream output =  new DataOutputStream(socket.getOutputStream());
    String encryptedLine = cryptoService.encrypt(line);
    output.writeInt(encryptedLine.length());
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

  public String readLine() throws CryptoException, IOException, TimeoutException {
    String line = "";
    try {
      DataInputStream inputStream = new DataInputStream(socket.getInputStream());
      int count = inputStream.readInt();

      for (int i = 0; i < count; ++i) {
        line += (char)inputStream.read();
      }
    } catch (SocketTimeoutException e) {
      throw new TimeoutException();
    } catch (UnsupportedEncodingException e) {
      throw new IOException(e);
    }

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
