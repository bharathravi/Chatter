package chatter.server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/11/11
 * Time: 2:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChatterServer {
  private int currentClients;

  ChatterServer() {
    this.currentClients = 0;
  }

  public static void main(String[] args) {
    startListening();
  }

  private static void startListening() {

    final ServerGlobalData globalData = new ServerGlobalData();


    try {
      ServerSocket serverSocket = new ServerSocket(Constants.PORT);
      while(true) {
        System.out.println("Current client count is " +
              globalData.getConnectedClients());
        Socket clientSocket = serverSocket.accept();
        if (isMaxedOut(globalData)) {
          rejectClient(globalData, clientSocket);
        } else {
          globalData.setConnectedClients(globalData.getConnectedClients() + 1);
          new Thread(
              new ClientHandler(clientSocket, globalData)
          ).start();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void rejectClient(ServerGlobalData globalData, Socket clientSocket) throws IOException {
    OutputStream output = clientSocket.getOutputStream();
    System.out.println("Rejecting client because client count is: " +
        globalData.getConnectedClients());
    output.write("I'm full. Bye.\0".getBytes());
    clientSocket.close();
  }

  private static boolean isMaxedOut(ServerGlobalData globalData) {
    return globalData.getConnectedClients() >= Constants.MAXCLIENTS;
  }
}
