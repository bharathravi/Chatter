package chatter.server;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/11/11
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChatterServer {
  public static void main(String[] args) {
    startListening();
  }

  private static void startListening() {

    try {
      ServerSocket serverSocket = new ServerSocket(Constants.PORT);
      while(true) {
        new Thread(
            new ClientHandler(serverSocket.accept())
        ).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
