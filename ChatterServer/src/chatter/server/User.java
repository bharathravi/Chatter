package chatter.server;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/11/11
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class User {
  private String userName;
  private String passwordHash;
  private String salt;
  private boolean loggedIn;

  public User(String userName, String passwordHash, String salt) {
    this.userName = userName;
    this.passwordHash = passwordHash;
    this.salt = salt;
    this.loggedIn = false;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public String getSalt() {
    return salt;
  }

  public String getUserName() {
    return userName;
  }

  public boolean isLoggedIn() {
    return loggedIn;
  }

  public void setLoggedIn(boolean loggedIn) {
    this.loggedIn = loggedIn;
  }


}
