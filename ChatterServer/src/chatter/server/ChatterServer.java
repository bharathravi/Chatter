package chatter.server;

import chatter.common.*;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Bharath Ravi
 * @author Kapil Goel
 * @author Alban
 *
 * This is the main "Controller" class for the Server.
 * This class is a Thread that continuously waits for a connection
 * from a client.
 *
 * For a client attempting to connect, it either:
 * 1. Rejects the client if it already is serving too many.
 * 2. Sets up an encrypted socket, and spawns a new thread to handle the client.
 */
public class ChatterServer extends Thread{
  private ServerSocket serverSocket = null;
  private BroadcastService broadcastService;

  ChatterServer() {}

  public void run() {
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
      while (true) {

        System.out.println("Current chatter.client count is " +
            clientCountMonitor.getClientCount());
        Socket clientSocket = serverSocket.accept();
        if (isMaxedOut(clientCountMonitor)) {
          rejectClient(clientSocket);
        } else {
          // Increment the clientCount at this point, so that we don't even bother
          // authenticating if there are too many clients connected.
          clientCountMonitor.incrementClientCount();

          new ClientHandler(new EncryptedSocket(clientSocket),
              clientCountMonitor, broadcastService).start();
        }
      }
    }catch (CryptoException e) {
      System.out.println(ErrorConstants.ERROR_ENCRYPTION);
    } catch (DiffieHellmanException e) {
      System.out.println(ErrorConstants.ERROR_ENCRYPTION_SETUP);
    } catch (IOException e) {
      if (isInterrupted()) {
        System.out.println("Closing connections...");
      } else{
      System.out.println(ErrorConstants.ERROR_CLIENT_CONNECTION);
      e.printStackTrace();
      }
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
      System.out.println("Error closing chatter.client socket. " +
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

  public synchronized void shutdown() {
    // Add in any termination cases here.
    performShutdownCleanUp();
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

    Thread.currentThread().interrupt();
    closeServerSocket();
  }

  public void interrupt() {
    super.interrupt();

    try {
      serverSocket.close();
    } catch (IOException e) {
      // Ignore error while quitting
    }
  }


  private boolean isMaxedOut(ClientCountMonitor monitor) {
    return monitor.getClientCount() >= Constants.MAXCLIENTS;
  }
}
