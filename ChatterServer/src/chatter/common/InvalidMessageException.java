package chatter.common;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/12/11
 * Time: 11:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class InvalidMessageException extends Exception {
  public InvalidMessageException(Exception e) {
    super(e);
  }

  public InvalidMessageException(){
    super();
  }

  public InvalidMessageException(String msg){
    super(msg);
  }
}
