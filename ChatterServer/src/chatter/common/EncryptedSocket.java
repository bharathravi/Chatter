package chatter.common;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * @author Bharath Ravi
 *
 * An "encrypted socket" that handles sending and receiving of encrypted data
 * over a standard socket. Its implementation is specifc to our usecase, since
 * this supports only two operations:
 * 1. readLine(), that reads a line of text from the socket.
 * 2. sendLine(), that sends a line of text over the socket.
 */
public class EncryptedSocket {

  private Socket socket;

  public EncryptedSocket(Socket socket) {
    this.socket = socket;
  }

  public void sendLine(String line) throws IOException {
    // TODO(bharath): Apply suitable encryption before sending.
    OutputStream output = socket.getOutputStream();
    line += '\n';
    output.write(line.getBytes());
  }

  public String readLine() throws IOException {
    // TODO(bharath): Apply suitable decryption after reading.
    String line = "";
    try {

      BufferedInputStream inputStream = new BufferedInputStream(
          socket.getInputStream());

      InputStreamReader inputReader =
          new InputStreamReader(inputStream, "US-ASCII");

      int c =-1;
      while((c=inputReader.read()) != '\n') {
        line += (char)c;
        if (line.length() >= Constants.TEXT_LIMIT) {
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
