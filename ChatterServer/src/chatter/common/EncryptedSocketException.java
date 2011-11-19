package chatter.common;

/**
 * @author Bharath Ravi
 * @author Kapil Gole
 * @author Alban Dumouilla
 *
 */
public class EncryptedSocketException extends Exception {
  public EncryptedSocketException(Exception e) {
    super(e);
  }
}
