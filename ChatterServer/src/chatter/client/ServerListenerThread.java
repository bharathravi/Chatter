package chatter.client;

import chatter.common.*;

import java.io.EOFException;
import java.io.IOException;
import java.net.SocketException;
import java.util.concurrent.TimeoutException;

/**
 * @author Bharath Ravi
 * @author Kapil Gole
 * @author Alban Dumouilla
 *
 */
public class ServerListenerThread extends Thread {

  private EncryptedSocket connection;

  public ServerListenerThread(EncryptedSocket connection) {
    this.connection = connection;
  }

  public void run() {
    try {
      // The "main" while loop of the chatter.client.
      // Endlessly wait for messages from server and print them out.
      while (true) {
        String responseLine = connection.readLine();
        Message msg = new Message(responseLine);
        switch (msg.type) {
          case QUIT:
            System.out.println(ErrorConstants.ERROR_SERVER_QUIT);
            break;
          case CHAT:
            System.out.println(msg.messageContent);
            break;
          default:
            // If an unreadable message was received, quit.
            System.out.println(ErrorConstants.ERROR_CLUELESS);
        }
      }
    } catch (SocketException e) {
      if(!isInterrupted()) {
        System.out.println(ErrorConstants.ERROR_SERVER_DISCONNECT);
      }
    } catch (EOFException e) {
      if (!isInterrupted()) {
        System.out.println(ErrorConstants.ERROR_SERVER_DISCONNECT);
      }
    } catch (IOException e) {
      if (!isInterrupted()) {
        e.printStackTrace();
      }
      System.out.println("IO Exception");
    } catch (InvalidMessageException e) {
      System.out.println(ErrorConstants.INVALID_MESSAGE);
      e.printStackTrace();
    } catch (CryptoException e) {
      System.out.println(ErrorConstants.ERROR_ENCRYPTION);
      e.printStackTrace();
    } catch (TimeoutException e) {
      System.out.println(ErrorConstants.ERROR_SERVER_TIMEOUT);
    } finally {
      //shutdown();
    }
  }

  public void interrupt() {
    super.interrupt();
  }
}
