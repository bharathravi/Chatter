package chatter.server;

import chatter.common.*;

import java.io.*;
import java.util.concurrent.TimeoutException;

/**
 * @author Bharath Ravi
 * @author Kapil Gole
 * @author Alban Dumouilla
 *
 * Controller class for a single client. This thread handles authentication
 * of the client with a username password, handles incoming and outgoing
 * messages from/to this client.
 *
 * This implements a {@code BroadcastListener} that transmits broadcast
 * chats heard from other connected clients.
 */
public class ClientHandler extends Thread implements BroadcastListener {
  private User thisUser;
  private EncryptedSocket clientSocket;
  private ClientCountMonitor clientCount;
  private BroadcastService broadcastService;

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
      System.out.println("Client Connected");

      // Authenticate the chatter.client
      if(authenticateClient()) {
        // If the chatter.client was able to authenticate itself, then
        // proceed with the chat stuff.
        clientSocket.sendLine(Message.createOkayMessage());
        broadcastLine(Message.createChatMessage(
            thisUser.getUserName() + " has logged in."));
        startChatting();
      } else {
        // If authentication failed, send a QUIT message and return.
        sendQuit();
      }
    } catch (IOException e) {
      if (isInterrupted()) {
        System.out.println("Interrupted. Shutting down");
      } else{
        System.out.println(ErrorConstants.ERROR_CLIENT_CONNECTION);
        e.printStackTrace();
      }
    } catch (InvalidMessageException e) {
      System.out.println(ErrorConstants.INVALID_MESSAGE);
      e.printStackTrace();
    } catch (CryptoException e) {
      System.out.println(ErrorConstants.ERROR_ENCRYPTION);
    } catch (TimeoutException e) {
      try {
        sendQuitMessage();
      } catch (CryptoException e1) {
        e1.printStackTrace();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
      System.out.println(ErrorConstants.ERROR_CLIENT_TIMEOUT);
      //e.printStackTrace();
    } finally {
      disconnect();
    }
  }

  private void sendQuit() {
    // Quitting is a best effort operation. If there are errors
    // while sending a Quit message, they are simply ignored.
    // This is because the other end is fail proof with timeouts anyway,
    // and the QUIT is just a convenience.
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
    while(true) {
      String line = clientSocket.readLine();
      Message msg = new Message(line);

      switch (msg.type) {
        case AUTH: //Ignore, the chatter.client is already authenticated.
          break;
        case QUIT:
          sendQuitMessage();
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

  private void sendQuitMessage() throws CryptoException, IOException {
    // Unregister myself, since my chatter.client has already quit
    broadcastService.unregisterListener(this);

    // Inform the other clients about the quit.
    broadcastLine(Message.createChatMessage(
        thisUser.getUserName() + " has quit"));
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
    // Safely close the connection by closing the socket and interrupting this
    // thread.
    sendQuit();
    shutdown();
  }

  private void shutdown() throws IOException {
    super.interrupt();

    if (clientSocket!=null && !clientSocket.isClosed()) {
      clientSocket.close();
    }
  }
}
