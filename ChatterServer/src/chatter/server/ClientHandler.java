package chatter.server;

import chatter.common.Constants;
import chatter.common.EncryptedSocket;
import chatter.common.InvalidMessageException;
import chatter.common.Message;

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
  private Broadcaster broadcaster;

  public ClientHandler(Socket clientSocket,
                       ClientCountMonitor clientCountMonitor,
                       Broadcaster broadcaster) {
    this.clientSocket = new EncryptedSocket(clientSocket);
    this.clientCount = clientCountMonitor;
    this.broadcaster = broadcaster;
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
      System.out.println("Invalid/Unexpected message received");
      e.printStackTrace();
    } finally {
      disconnect();
    }
  }

  private void disconnect() {
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
    String line = clientSocket.readLine();
    Message msg = new Message(line);

    switch (msg.type) {
      case AUTH: //Ignore, the client is already authenticated.
        break;
      case QUIT:
        System.out.println("The client has quit");
        return;
      case CHAT:
        broadcastLine(thisUser.getUserName() + ": " + line);
      default:
        System.out.println("I have no clue what the heck just happened,\n" +
            "but I'm going to nod and smile like I understood.");
    }


  }

  private void broadcastLine(String line) {
    broadcaster.sendBroadcast(line);
  }

  private void setupEncryption() {
    // Add in Diffie Helman key exchange here.
  }

  private boolean authenticateClient() throws IOException,
      InvalidMessageException {
    //clientSocket.sendLine("PASS");
    String line = clientSocket.readLine();
    Message msg = new Message(line);

    if (msg.type == Message.MessageType.AUTH) {
      System.out.println("Client says: " + msg.messageContent);
      ClientAuthenticator auth = new ClientAuthenticator(msg.messageContent);
      return auth.authenticate();
    }

    return false;
  }

  public void onBroadcast(String message) {
    // TODO(bharath): Encrypt string and send it out.
  }
}
