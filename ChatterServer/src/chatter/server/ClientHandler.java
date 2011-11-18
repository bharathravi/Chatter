package chatter.server;

import common.*;

import java.io.*;
import java.util.concurrent.TimeoutException;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/11/11
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientHandler implements Runnable, BroadcastListener {
  private User thisUser;
  private EncryptedSocket clientSocket;
  private ClientCountMonitor clientCount;
  private BroadcastService broadcastService;
  private boolean isStopped = false;

  public ClientHandler(EncryptedSocket clientSocket,
                       ClientCountMonitor clientCountMonitor,
                       BroadcastService broadcastService) {
    this.clientSocket = clientSocket;
    this.clientCount = clientCountMonitor;
    this.broadcastService = broadcastService;
    broadcastService.registerListener(this);
  }


  public void run() {
    try {
      // Set a timeout for the socket.
      System.out.println("Client Connected");

      // Authenticate the client
      if(authenticateClient()) {
        // If the client was able to authenticate itself, then
        // proceed with the chat stuff.
        clientSocket.sendLine(Message.createOkayMessage());
        broadcastLine(Message.createChatMessage(
            thisUser.getUserName() + " has logged in."));
        startChatting();
      }
    } catch (IOException e) {
      System.out.println(ErrorConstants.ERROR_CLIENT_CONNECTION);
      e.printStackTrace();
    } catch (InvalidMessageException e) {
      System.out.println(ErrorConstants.INVALID_MESSAGE);
      //e.printStackTrace();
    } catch (CryptoException e) {
      System.out.println(ErrorConstants.ERROR_ENCRYPTION);
    } catch (TimeoutException e) {
      System.out.println(ErrorConstants.ERROR_CLIENT_TIMEOUT);
      e.printStackTrace();
    } finally {
      disconnect();
    }
  }

  private void sendQuit() {
    // Quitting is a best effort operation. If there are errors
    // while sending a Quit message, they are simply ignored.
    try {
      clientSocket.sendLine(Message.createQuitMessage());
    } catch (IOException e) {
      // Ignore error
    } catch (CryptoException e) {
      // Ignore error
    }
  }

  private void disconnect() {
    broadcastService.unregisterListener(this);
    sendQuit();
    clientCount.decrementClientCount();
    if (!clientSocket.isClosed()) {
      System.out.println("Client disconnected. Client count is:" +
          clientCount.getClientCount());
      try {
        clientSocket.close();
      } catch (IOException e) {
        System.out.println("Error closing socket.");
      }
    }
  }

  private void startChatting() throws IOException, InvalidMessageException, CryptoException, TimeoutException {
    // Set an appropriate time for the chat.
    clientSocket.setTimeout(Constants.CHAT_TIMEOUT);
    while(!isStopped) {
      String line = clientSocket.readLine();
      Message msg = new Message(line);

//      System.out.println(thisUser.getUserName() + " says: " + line);
      switch (msg.type) {
        case AUTH: //Ignore, the client is already authenticated.
          break;
        case QUIT:
          return;
        case CHAT:
          broadcastLine(
              Message.createChatMessage(
                  thisUser.getUserName() + ": " + msg.messageContent));
          break;
        default:
          System.out.println(ErrorConstants.ERROR_CLUELESS);
      }
    }
  }

  private void broadcastLine(String line) throws IOException, CryptoException {
    broadcastService.sendBroadcast(line);
  }

  private boolean authenticateClient() throws IOException,
      InvalidMessageException, CryptoException, TimeoutException {
    // Set an appropriate timeout for authentication.
    clientSocket.setTimeout(Constants.AUTHENTICATION_TIMEOUT);
    String line = clientSocket.readLine();

    Message msg = new Message(line);

    if (msg.type == Message.MessageType.AUTH) {
   //   System.out.println("Client says: " + msg.messageContent);
      ClientAuthenticator auth = new ClientAuthenticator(msg.messageContent);
      if(auth.authenticate()) {
        thisUser = UserDatabase.getInstance().database.get(auth.uname);
        return true;
      }
    }

    return false;
  }

  public void onBroadcast(String message) throws IOException, CryptoException {
   // System.out.println("Sending message:" + message);
    clientSocket.sendLine(message);
  }

  public void onBroadcastShutdown() throws IOException, CryptoException {
    // Safely close the connection.
    isStopped = true;
  }
}
