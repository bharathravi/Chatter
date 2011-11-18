package chatter.server;

import common.*;

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
  private BroadcastService broadcastService;

  ChatterServer() {}

  public void startListening() {
    this.isStopped = false;
    final ClientCountMonitor clientCountMonitor = new ClientCountMonitor();
    broadcastService = new BroadcastService();


    try {
      serverSocket = new ServerSocket(Constants.PORT);
    } catch (IOException e) {
      System.out.println("Error starting up server");
      e.printStackTrace();
      return;
    }


    try {
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

          new Thread(
              new ClientHandler(new EncryptedSocket(clientSocket),
                  clientCountMonitor, broadcastService)
          ).start();
        }
      }
    }catch (CryptoException e) {
      System.out.println(ErrorConstants.ERROR_ENCRYPTION);
    } catch (DiffieHellmanException e) {
      System.out.println(ErrorConstants.ERROR_ENCRYPTION_SETUP);
    } catch (IOException e) {
      System.out.println(ErrorConstants.ERROR_CLIENT_CONNECTION);
      e.printStackTrace();
    } finally {
      performShutdownCleanUp();
    }
  }


  private void rejectClient(Socket clientSocket) {
    try {
      OutputStream output = clientSocket.getOutputStream();
      output.write((Message.createQuitMessage() + "\n").getBytes());
      clientSocket.close();
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

  public void shutdown() {
    // Add in any termination cases here.
    isStopped = true;
  }

  private void performShutdownCleanUp() {
    try {
      broadcastService.sendShutdown();
    } catch (CryptoException e) {
      System.out.println("Error while shutting down");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("Error while shutting down");
      e.printStackTrace();
    }

    closeServerSocket();
  }


  private boolean isMaxedOut(ClientCountMonitor monitor) {
    return monitor.getClientCount() >= Constants.MAXCLIENTS;
  }
}
