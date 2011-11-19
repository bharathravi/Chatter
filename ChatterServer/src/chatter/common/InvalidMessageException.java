package chatter.common;

/**
 * @author Bharath Ravi
 * @author Kapil Goel
 * @author Alban
 *
 */
public class InvalidMessageException extends Exception {
  public InvalidMessageException(Exception e, String message) {
    super(message,e);
  }

  public InvalidMessageException(String message) {
    super(message);
  }
}
