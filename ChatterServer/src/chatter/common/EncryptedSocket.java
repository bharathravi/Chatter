package chatter.common;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.security.*;

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

  public EncryptedSocket(Socket socket) throws DiffieHellmanException, IOException {
    this.socket = socket;
    setTimeout(Constants.AUTHENTICATION_TIMEOUT);

    // Actual work in constructor is not great. But do it anyway :-/
    DiffieHelmanKeyGenerator dhGenerator = new DiffieHelmanKeyGenerator(this);
    this.secretKey = dhGenerator.generate();
  }

  private void printBytes(byte[] pKeyBytes) {
    for (int i = 0 ; i< pKeyBytes.length; ++i) {
      System.out.print(pKeyBytes[i]);
      System.out.print(' ');
    }
  }

  public byte[] getPublicKeyBytes() throws IOException {
    DataInputStream dis = new DataInputStream(socket.getInputStream());
    int length = dis.readInt();
    byte[] bytes = new byte[length];
    dis.read(bytes, 0, length);

    return bytes;
  }

  public void sendLine(String line) throws IOException {
    // TODO(bharath): Apply suitable encryption before sending.
    OutputStream output = socket.getOutputStream();
    line += '\n';
    output.write(line.getBytes());
  }

  public void sendPublicKeyBytes(byte[] bytes) throws IOException {
    // TODO(bharath): Apply suitable encryption before sending.
    OutputStream output = socket.getOutputStream();
    DataOutputStream dos = new DataOutputStream(output);
    dos.writeInt(bytes.length);
    dos.write(bytes);
  }

  public String readLine() throws IOException {
    // TODO(bharath): Apply suitable decryption after reading.
    String line = "";
    try {
      BufferedInputStream inputStream = new BufferedInputStream(
          socket.getInputStream());
      InputStreamReader inputReader =
          new InputStreamReader(inputStream, "US-ASCII");

      int c;
      while((c=inputReader.read()) != '\n') {
        line += (char)c;
        if (line.length() >= TEXT_LIMIT) {
          // Too long a line. Just return whatever we have up until now.
          break;
        }
      }

    } catch (SocketException e) {
      throw e;
    }

    return line;
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
