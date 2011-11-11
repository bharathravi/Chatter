package chatter.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/11/11
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientHandler implements Runnable {
  private Socket clientSocket;

  public ClientHandler(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public void run() {
    try {
      System.out.println("Client Connected");
      InputStream input = clientSocket.getInputStream();
      OutputStream output = clientSocket.getOutputStream();

      output.write("Hello there\0".getBytes());
      clientSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
