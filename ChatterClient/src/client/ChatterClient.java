package client;

import com.sun.corba.se.impl.orbutil.closure.Constant;
import common.*;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.*;
import java.net.*;
import java.util.concurrent.TimeoutException;

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
  private boolean isStopped = false;


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
      //e.printStackTrace();
    } catch (InvalidMessageException e) {
      System.out.println("Invalid message received.");
      //e.printStackTrace();
    } catch (TimeoutException e) {
      System.out.println("Connection timed out. Shutting down.");
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

    while (!isStopped) {
      String chatText = readChat.readLine();
      if (chatText.equals(Constants.QUIT_MESSAGE)){
        shutdown();
        return;
      } else {
        connection.sendLine(Message.createChatMessage(chatText));
      }
    }
  }

  private void sendQuit() {
     // Quitting is a best effort operation. If there are errors
    // while sending a Quit message, they are simply ignored.
    try {
      connection.sendLine(Message.createQuitMessage());
    } catch (IOException e) {
      // Ignore error
    } catch (CryptoException e) {
      // Ignore error
    }
  }

  public void shutdown() {
    if(serverThread !=null) {
      serverThread.interrupt();
    }

    System.out.println("Disconnecting...");

    if (connection!= null && !connection.isClosed()) {
      try {
        sendQuit();
        connection.close();
      } catch (IOException e) {
        System.out.println("Error while closing socket");
      }
    }
  }

  private boolean authenticateClient() throws IOException, CryptoException,
      InvalidMessageException, TimeoutException {
    connection.setTimeout(Constants.AUTHENTICATION_TIMEOUT);
    InputStreamReader inputStreamReader = new InputStreamReader(System.in);
    BufferedReader readChat = new BufferedReader(inputStreamReader);
    System.out.print("UserName : ");
    String username = readChat.readLine();
    System.out.print("Password : ");
    String password = readChat.readLine();

    System.out.println(Integer.MAX_VALUE);

    connection.sendLine(Message.createAuthMessage(username, password));
    String response = connection.readLine();
    Message msg = new Message(response);

    switch (msg.type) {
      case QUIT:
        System.out.println("Server has quit. This could be because of an " +
            "Incorrect uname/passwd OR because of a timeout.");
        return false;
      case OKAY:
        return true;
      default:
        System.out.println("Oops! An unreadable message was received.");
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
          // The "main" while loop of the client.
          // Endlessly wait for messages from server and print them out.
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
                // If an unreadable message was received, quit.
                System.out.println("I have no clue what the heck just happened,\n" +
                    "but I'm going to nod and smile like I understood.");
                quit = true;
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
          onServerThreadError(e);
        } catch (TimeoutException e) {
          onServerThreadError(e);
        }
      }
    });
  }

  private void onServerThreadError(Exception e) {
    System.out.println("Server closed:");
    e.printStackTrace();
    isStopped = true;
  }
}
