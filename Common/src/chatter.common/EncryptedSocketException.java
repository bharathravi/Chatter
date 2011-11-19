package chatter.common;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/15/11
 * Time: 5:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class EncryptedSocketException extends Exception {
  public EncryptedSocketException(Exception e) {
    super(e);
  }
}
