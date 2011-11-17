package client;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import common.*;

import java.io.*;
import java.net.*;

/**
 * The basic client algorithm is as follows:
 * 1. Create a Socket to the server
 * 2. Use the socket to create an EncryptedSocket.
 * 3. All further messaging should be done using only the encrypted socket.
 * 4. First authenticate using uname + passwd.
 * 5. Start listening for either:
 *     a. Chats from server
 *     b. Input from client
 * 6. If user says "\quit" then send a QUIT message to server and close.
 */
public class ChatterClient {
  private static Thread serverThread;
  private static EncryptedSocket connection;


  public ChatterClient() {

  }

  public void start() throws CryptoException {
    try {
      InetAddress address = InetAddress.getByName(Constants.HOST);
      connection = new EncryptedSocket(new Socket(address, Constants.PORT));
    } catch (ConnectException e) {
      System.out.println("Connection refused");
      return;
    } catch (UnknownHostException e) {
      System.out.println("Unknown server.");
      e.printStackTrace();
      return;
    } catch (DiffieHellmanException e) {
      System.out.println("Error setting up an encrypted channel");
      e.printStackTrace();
      return;
    } catch (IOException e) {
      System.out.println("Cannot connect to server");
      //e.printStackTrace();
      return;
    }

    try {
      if (authenticateClient()) {
        startChatLoop();
      }
    } catch (IOException e) {
      System.out.println("Cannot connect to the server.");
      e.printStackTrace();
    } catch (InvalidMessageException e) {
      System.out.println("Invalid message received.");
      e.printStackTrace();
    } finally {
      shutdown();
    }
  }

  private void startChatLoop() throws IOException, CryptoException {
    System.out.println("Logged in!");
    connection.setTimeout(Constants.CHAT_TIMEOUT);

    createServerListener();
    serverThread.start();
    InputStreamReader inputStreamReader = new InputStreamReader(System.in);
    BufferedReader readChat = new BufferedReader(inputStreamReader);

    while (true) {
      String chatText = readChat.readLine();
      if (chatText.equals(Constants.QUIT_MESSAGE)){
        connection.sendLine(Message.createQuitMessage());
        shutdown();
        return;
      } else {
        connection.sendLine(Message.createChatMessage(chatText));
      }
    }
  }

  public void shutdown() {

    if(serverThread !=null) {
      serverThread.interrupt();
    }

    System.out.println("Disconnecting...");

    try {
      if (connection !=null && !connection.isClosed()) {
        connection.sendLine(Message.createQuitMessage());
      }
    } catch (CryptoException e) {
      System.out.println("Cryptography exception while shutting down.");
    } catch (IOException e) {
      System.out.println("Could not send QUIT to server");
    }

    if (connection!= null && !connection.isClosed()) {
      try {
        connection.close();
      } catch (IOException e) {
        System.out.println("Error while closing socket");
      }
    }
  }

  private boolean authenticateClient() throws IOException, CryptoException, InvalidMessageException {

    connection.setTimeout(Constants.AUTHENTICATION_TIMEOUT);
    InputStreamReader inputStreamReader = new InputStreamReader(System.in);
    BufferedReader readChat = new BufferedReader(inputStreamReader);
    System.out.print("UserName : ");
    String username = readChat.readLine();
    System.out.print("Password : ");
    String password = readChat.readLine();

    connection.sendLine(Message.createAuthMessage(username, password));
    String response = connection.readLine();
    Message msg = new Message(response);

    switch (msg.type) {
      case QUIT:
        System.out.println("Incorrect uname/passwd");
        return false;
      case OKAY:
        System.out.println("Logged in");
        // connection.sendLine(Message.createQuitMessage());
        return true;
      default:
        System.out.println("No clue what the heck happened");
        return false;
    }
  }

  /**
   * Creates a thread that endlessly loops waiting for chat messages from
   * the server and printing them when it receives any.
   */
  private void createServerListener() {
    final EncryptedSocket finalConnection = connection;
    serverThread = new Thread(new Runnable() {
      public void run() {
        try {
          // ChatterServerMain while loop of the client. Endlessly wait for messages from server
          // and print them out.
          boolean quit = false;

          while (!quit) {
            String responseLine = null;
            responseLine = finalConnection.readLine();
            Message msg = new Message(responseLine);
            switch (msg.type) {
              case QUIT:
                System.out.println("Server has quit.");
                quit = true;
                break;
              case CHAT:
                System.out.println(msg.messageContent);
                break;
              default:
                System.out.println("I have no clue what the heck just happened,\n" +
                    "but I'm going to nod and smile like I understood.");
            }
          }
          shutdown();
        } catch (SocketException e) {
          onServerThreadError(e);
        } catch (IOException e) {
          onServerThreadError(e);
        } catch (InvalidMessageException e) {
          onServerThreadError(e);
        } catch (CryptoException e) {
          e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
      }
    });
  }

  private void onServerThreadError(Exception e) {
    shutdown();
  }
}
