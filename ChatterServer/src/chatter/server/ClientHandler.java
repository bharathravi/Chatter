package chatter.server;

import java.io.*;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/11/11
 * Time: 3:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientHandler implements Runnable {
  private Socket clientSocket;
  private ServerGlobalData globalData;

  public ClientHandler(Socket clientSocket, ServerGlobalData globalData) {
    this.clientSocket = clientSocket;
    this.globalData = globalData;
  }


  public void run() {
    try {
      // Set a timeout of TIMEOUT seconds.
      clientSocket.setSoTimeout(Constants.INITIAL_TIMEOUT);
      System.out.println("Client Connected");
      if(authenticateClient()) {
        clientSocket.setSoTimeout(Constants.INITIAL_TIMEOUT);
        // If the client was able to authenticate itself, then
        // proceed with the chat stuff.
        InputStream input = clientSocket.getInputStream();
      }

    } catch (InterruptedIOException e) {
      System.out.println("Closing client due to time-out");
    } catch (IOException e) {
      System.out.println("Closing client due to long text");
    } catch (TextTooLongException e) {

    } finally {
      globalData.setConnectedClients(globalData.getConnectedClients() - 1);
      System.out.println("Client disconnected. Client count is:" +
          globalData.getConnectedClients());
      try {
        clientSocket.close();
      } catch (IOException e) {
        System.out.println("Unable to close socket.");
        e.printStackTrace();
      }
    }
  }

  private boolean authenticateClient() throws IOException, TextTooLongException {
    OutputStream output = clientSocket.getOutputStream();
    output.write("PASS\n".getBytes());

    BufferedInputStream inputStream = new BufferedInputStream(
        clientSocket.getInputStream());

    // TODO(rbharath): The encoding will no longer be ASCII
    // once we start using encryption
    InputStreamReader inputReader =
        new InputStreamReader(inputStream, "US-ASCII");

    int c =-1;
    String unamePasswd = "";
    while((c=inputReader.read()) != '\n') {
      unamePasswd += c;
      if (unamePasswd.length() == Constants.TEXT_LIMIT) {
        // Something fishy is going on. Simply break.
        break;
      }

    }


    ClientAuthenticator auth = new ClientAuthenticator(unamePasswd);
    return auth.authenticate();
  }
}
