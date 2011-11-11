package chatter.client;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.Buffer;

import chatter.server.Constants;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/11/11
 * Time: 3:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChatterClient {
  public static void main(String[] args) {
    try {
      InetAddress address = InetAddress.getByName(Constants.HOST);
      Socket connection = new Socket(address, Constants.PORT);
      BufferedInputStream inputStream = new BufferedInputStream(
          connection.getInputStream());

      // TODO(rbharath): The encoding will no longer be ASCII
      // once we start using encryption
      InputStreamReader inputReader =
          new InputStreamReader(inputStream, "US-ASCII");

      OutputStream output = connection.getOutputStream();

      int c =-1;
      while( (c=inputReader.read()) != 0) {
        System.out.print((char)c);
        //output.write("Hello there\0".getBytes());
      }

      while(true) ;

    } catch (UnknownHostException e) {
      System.out.println("Unknown address");
      e.printStackTrace();
    } catch (IOException e) {
      System.out.println("IO Exception while creating socket");
      e.printStackTrace();
    }
  }
}
