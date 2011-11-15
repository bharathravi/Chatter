package chatter.server;

import chatter.common.CryptoException;
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

  private boolean isStopped = false;
  private ServerSocket serverSocket = null;

  ChatterServer() {}

  public void startListening() {
    this.isStopped = false;
    final ClientCountMonitor clientCountMonitor = new ClientCountMonitor();
    final BroadcastService broadcastService = new BroadcastService();

    try {
      serverSocket = new ServerSocket(Constants.PORT);
      while (!isStopped) {
        System.out.println("Current client count is " +
            clientCountMonitor.getClientCount());
        Socket clientSocket = serverSocket.accept();
        if (isMaxedOut(clientCountMonitor)) {
          rejectClient(clientSocket);
        } else {
          // Increment the clientCount at this point, so that we don't even bother
          // authenticating if there are too many clients connected.
          clientCountMonitor.incrementClientCount();
          try {
            new Thread(
                new ClientHandler(clientSocket, clientCountMonitor, broadcastService)
            ).start();
          } catch (CryptoException e) {
            System.out.println("Error in encryption. Closing client socket");
            closeClientConnection(clientSocket);
          } catch (DiffieHellmanException e) {
            System.out.println("Unable to create an encrypted connection.");
            closeClientConnection(clientSocket);
          } catch (IOException e) {
            System.out.println("Unable to create an encrypted connection.");
            closeClientConnection(clientSocket);
          }
        }
      }
    } catch (IOException e) {
      System.out.println("Error Setting up server socket.");
    } finally {
      closeServerSocket();
    }
  }


  private void rejectClient(Socket clientSocket) {
    OutputStream output = null;
    try {
      output = clientSocket.getOutputStream();
      System.out.println("Rejecting client because client count is above limit");
      output.write("I'm full. Bye.\0".getBytes());
      closeClientConnection(clientSocket);
    } catch (IOException e) {
      System.out.println("Error closing client socket. " +
          "Ignoring and continuing execution");
    }
  }

  private void closeClientConnection(Socket clientSocket) {
    try {
      if (clientSocket!=null && !clientSocket.isClosed()) {
        clientSocket.close();
      }
    } catch (IOException e) {
      System.out.println("Error closing client socket. " +
          "Ignoring and continuing execution");
    }
  }

  private void closeServerSocket() {
    if (serverSocket != null && !serverSocket.isClosed()) {
      try {
        serverSocket.close();
      } catch (IOException e) {
        System.out.println("Error closing server socket");
      }
    }
  }

  public void stopServer() {
    // Add in any other termination cases here.
    isStopped = true;
  }


  private boolean isMaxedOut(ClientCountMonitor monitor) {
    return monitor.getClientCount() >= Constants.MAXCLIENTS;
  }
}
