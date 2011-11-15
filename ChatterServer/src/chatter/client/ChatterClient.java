package chatter.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import chatter.common.*;

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
//      try {
//        // Create the parameter generator for a 1024-bit DH key pair
//        AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
//        paramGen.init(1024);
//
//        // Generate the parameters
//        AlgorithmParameters params = paramGen.generateParameters();
//        DHParameterSpec dhSpec
//            = (DHParameterSpec)params.getParameterSpec(DHParameterSpec.class);
//
//        // Return the three values in a string
//        System.out.println(""+dhSpec.getP()+"\n"+dhSpec.getG()+"\n"+dhSpec.getL());
//    } catch (NoSuchAlgorithmException e) {
//    } catch (InvalidParameterSpecException e) {
//    }

      InetAddress address = InetAddress.getByName(Constants.HOST);
      connection = new EncryptedSocket(new Socket(address, Constants.PORT));

      System.out.println(Message.MessageType.AUTH);
      // if(connection.readLine().equals("Connection Established"))

      //  connection.sendLine("Hello There");
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
          return;
        case OKAY:
          System.out.println("Logged in");
          // connection.sendLine(Message.createQuitMessage());
          break;
        default:
          System.out.println("No clue what the heck happened");
      }

      createServerThread();
      serverThread.start();

      while (true) {
        System.out.print("Chat: ");
        try {
          String chatText = readChat.readLine();
          if (chatText.equals(Constants.QUIT_MESSAGE)){
            connection.sendLine(Message.createQuitMessage());
            disconnect();
          } else {
            connection.sendLine(Message.createChatMessage(chatText));
          }
        } catch (IOException e) {
          System.out.println("Error reading chat");
        }
      }
    } catch (UnknownHostException e) {
      System.out.println("Unknown server address. " +
          "Please verify that the hostname of the server is correct.");
    } catch (IOException e) {
      System.out.println("Unable to create a connection to the server:" +
          "The server may be down. Please verify that you can connect to the" +
          "internet");
    } catch (InvalidMessageException e) {
      System.out.println("An invalid message was received from the server.");
    } catch (DiffieHellmanException e) {
      System.out.println("Unable to create an encrypted connection.");
    } finally {
      shutdown();
    }

  }

  public void shutdown() {
    try {
      if (connection!= null && !connection.isClosed()) {
        connection.close();
      }
    } catch (IOException e) {
      System.out.println("Unexpected error while terminating client:");
      e.printStackTrace();
    }

  }

  /**
   * Creates a thread that endlessly loops waiting for chat messages from
   * the server and printing them when it receives any.
   */
  private void createServerThread() {
    final EncryptedSocket finalConnection = connection;
    serverThread = new Thread(new Runnable() {
      public void run() {
        // ChatterServerMain while loop of the client. Endlessly wait for messages from server
        // and print them out.
        boolean quit = false;
        try {
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
          disconnect();
        } catch (SocketException e) {
          System.out.println("Socket exception");
          e.printStackTrace();
          return;
        } catch (IOException e) {
          e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InvalidMessageException e) {
          e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (CryptoException e) {
          e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

      }
    });
  }

  private static void disconnect() throws IOException {
    connection.close();
    serverThread.interrupt();
  }
}
