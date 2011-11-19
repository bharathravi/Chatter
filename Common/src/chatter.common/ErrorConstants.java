package chatter.common;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/11/11
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class ErrorConstants {
  public static final String ERROR_SERVER_TIMEOUT = "The server connection has timed out.";
  public static final String ERROR_SERVER_DISCONNECT = "Unable to connect to server";
  public static final String ERROR_SERVER_UNREACHABLE = "The server is unreachable.";
  public static final String ERROR_SERVER_QUIT = "The server has quit.";
  public static final String ERROR_AUTH =
      "Authentication failed.\n This could be because of an "
          + "Incorrect uname/passwd OR because of a timeout.\n "
          + "Or maybe you are just not worthy.";


  public static final String ERROR_CLIENT_TIMEOUT = "Closing chatter.client due to time-out";
  public static final String ERROR_CLIENT_CONNECTION = "Closing chatter.client due IO Exception";


  public static final String ERROR_ENCRYPTION_SETUP = "Error in setting up and encrypted channel";

  public static final String ERROR_ENCRYPTION = "Error in using the encrypted channel";

  public static final String CONNECTION_REFUSED = "Connection refused by server.";
  public static final String UNKNOWN_HOST = "No server exists at this address";

  public static final String INVALID_MESSAGE = "Oops! An Invalid Message was received!";
  public static final String ERROR_CLUELESS =
      "I have no clue what the heck just happened,\n"
          +  "but I'm going to nod and smile like I understood.";
}