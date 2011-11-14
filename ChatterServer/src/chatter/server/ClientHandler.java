package chatter.server;

import chatter.common.*;

import java.io.*;
import java.net.Socket;

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

  public ClientHandler(Socket clientSocket,
                       ClientCountMonitor clientCountMonitor,
                       BroadcastService broadcastService) throws IOException,
      DiffieHellmanException, CryptoException {
    this.clientSocket = new EncryptedSocket(clientSocket);
    this.clientCount = clientCountMonitor;
    this.broadcastService = broadcastService;
    broadcastService.registerListener(this);
  }


  public void run() {
    try {
      // Set a timeout for the socket.
      clientSocket.setTimeout(Constants.AUTHENTICATION_TIMEOUT);
      System.out.println("Client Connected");

      // Authenticate the client
      if(authenticateClient()) {
        clientSocket.setTimeout(Constants.CHAT_TIMEOUT);
        // If the client was able to authenticate itself, then
        // proceed with the chat stuff.
        clientSocket.sendLine(Message.createOkayMessage());
        startChatting();
      } else {
        clientSocket.sendLine(Message.createQuitMessage());
      }
    } catch (InterruptedIOException e) {
      System.out.println("Closing client due to time-out");
    } catch (IOException e) {
      System.out.println("Closing client due IO Exception");
      e.printStackTrace();
    } catch (InvalidMessageException e) {
      System.out.println("Ignoring Invalid/Unexpected message received");
      e.printStackTrace();
    } catch (CryptoException e) {
      System.out.println("Error decrypting or encrypting message");
    } finally {
      disconnect();
    }
  }

  private void disconnect() {
    broadcastService.unregisterListener(this);
    if (!clientSocket.isClosed()) {
      clientCount.decrementClientCount();
      System.out.println("Client disconnected. Client count is:" +
          clientCount.getClientCount());
      try {
        clientSocket.close();
      } catch (IOException e) {
        System.out.println("Unable to close socket.");
        e.printStackTrace();
      }
    }
  }

  private void startChatting() throws IOException, InvalidMessageException {
    while(true) {
      String line = null;
      try {
        line = clientSocket.readLine();
        Message msg = new Message(line);

        System.out.println(thisUser.getUserName() + " says: " + line);
        switch (msg.type) {
          case AUTH: //Ignore, the client is already authenticated.
            break;
          case QUIT:
            System.out.println("User " + thisUser.getUserName() +" has quit");
            return;
          case CHAT:
            broadcastLine(
                Message.createChatMessage(
                    thisUser.getUserName() + ": " + msg.messageContent));
            break;
          default:
            System.out.println("I have no clue what the heck just happened,\n" +
                "but I'm going to nod and smile like I understood.");
        }
      } catch (CryptoException e) {
        // If there was an encryption error, don't stop,
        // just ignore/log it and continue
        e.printStackTrace();
      }

    }
  }

  private void broadcastLine(String line) throws IOException {
    broadcastService.sendBroadcast(line);
  }

  private void setupEncryption() {
    // Add in Diffie Helman key exchange here.
  }

  private boolean authenticateClient() throws IOException,
      InvalidMessageException {
    //clientSocket.sendLine("PASS");
    String line = null;
    try {
      line = clientSocket.readLine();

      Message msg = new Message(line);

      if (msg.type == Message.MessageType.AUTH) {
        System.out.println("Client says: " + msg.messageContent);
        ClientAuthenticator auth = new ClientAuthenticator(msg.messageContent);
        if(auth.authenticate()) {
          thisUser = UserDatabase.getInstance().database.get(auth.uname);
          return true;
        }
      }
    } catch (CryptoException e) {
      e.printStackTrace();
    }

    return false;
  }

  public void onBroadcast(String message) throws IOException {
    System.out.println("Sending message:" + message);
    try {
      clientSocket.sendLine(message);
    } catch (CryptoException e) {
      e.printStackTrace();
    }
  }
}
