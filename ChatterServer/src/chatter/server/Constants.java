package chatter.server;

import javax.sound.sampled.Port;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/11/11
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
public class Constants {
  public static final int PORT=50154;
  public static final String HOST="localhost";
  public static final int MAXCLIENTS=4;
  public static final int INITIAL_TIMEOUT = 10000;
  public static final int CHAT_TIMEOUT = 300000;
  public static final int TEXT_LIMIT = 10000;
  public static final char PASSWORD_SEPARATOR = ',';
}