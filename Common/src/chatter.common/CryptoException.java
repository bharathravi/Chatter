package chatter.common;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/13/11
 * Time: 6:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class CryptoException extends Exception {
  public CryptoException(Exception e) {
    super(e);
  }
}
