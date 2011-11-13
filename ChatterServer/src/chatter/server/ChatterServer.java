package chatter.server;

import chatter.common.Constants;
import chatter.common.DiffieHellmanException;

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

  ChatterServer() {}

  public void startListening() {
    final ClientCountMonitor clientCountMonitor = new ClientCountMonitor();
    final BroadcastService broadcastService = new BroadcastService();

    try {
      ServerSocket serverSocket = new ServerSocket(Constants.PORT);
      while(true) {
        System.out.println("Current client count is " +
            clientCountMonitor.getClientCount());
        Socket clientSocket = serverSocket.accept();
        if (isMaxedOut(clientCountMonitor)) {
          rejectClient(clientSocket);
        } else {
          // Increment the clientCount at this point, so that we don't even bother
          // authenticating if there are too many clients connected.
          clientCountMonitor.incrementClientCount();
          new Thread(
              new ClientHandler(clientSocket, clientCountMonitor, broadcastService)
          ).start();
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (DiffieHellmanException e) {
      System.out.println("Could not create encrypted socket.");
      e.printStackTrace();
    }
  }

  private void rejectClient(Socket clientSocket) throws IOException {
    OutputStream output = clientSocket.getOutputStream();
    System.out.println("Rejecting client because client count is above limit");
    output.write("I'm full. Bye.\0".getBytes());
    clientSocket.close();
  }

  private boolean isMaxedOut(ClientCountMonitor monitor) {
    return monitor.getClientCount() >= Constants.MAXCLIENTS;
  }
}
