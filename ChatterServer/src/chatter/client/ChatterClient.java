package chatter.client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import chatter.common.Constants;
import chatter.common.EncryptedSocket;
import chatter.common.InvalidMessageException;
import chatter.common.Message;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/11/11
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChatterClient {
  public static void main(String[] args) {
    EncryptedSocket connection = null;
    try {
      InetAddress address = InetAddress.getByName(Constants.HOST);
      connection = new EncryptedSocket(new Socket(address, Constants.PORT));

      System.out.println(Message.MessageType.AUTH);
      // if(connection.readLine().equals("Connection Established"))

      //  connection.sendLine("Hello There");
      InputStreamReader inputStreamReader = new InputStreamReader(System.in);
      BufferedReader readChat = new BufferedReader(inputStreamReader);
      System.out.println("UserName : ");
      String username = readChat.readLine();
      System.out.println("Password : ");
      String password = readChat.readLine();

      connection.sendLine(Message.createAuthMessage(username, password));


      String response = connection.readLine();
      Message msg = new Message(response);

      switch (msg.type) {
        case QUIT:
          System.out.println("Incorrect uname/passwd");
          break;
        case OKAY:
          System.out.println("Logged in");
         // connection.sendLine(Message.createQuitMessage());
          break;
        default:
          System.out.println("No clue what the heck happened");
      }

      while(true);


//
//          System.out.println("Password : ");

//
//        String responseLine;
//        String optext;
//        while (true) {
//          responseLine = connection.readLine();
//          msg = new Message(responseLine);
//
//          switch (msg.type) {
//            case QUIT:
//              System.out.println("Server has quit.");
//          }
//
//          System.out.println("Server: " + responseLine);
//          if (responseLine.indexOf("Ok") != -1) {
//            break;
//          }
//          optext = readChat.readLine();
//          connection.sendLine(optext);
//        }


     // connection.close();
    } catch (UnknownHostException e) {
      System.out.println("Unknown address");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("IO Exception while creating socket");
      e.printStackTrace();
    } catch (InvalidMessageException e) {
      e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
    } finally {
      try {
        connection.close();
      } catch (IOException e) {
        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
    }

  }
}
