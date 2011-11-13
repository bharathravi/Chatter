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
  private boolean loggedIn;

  public User(String userName, String passwordHash) {
    this.userName = userName;
    this.passwordHash = passwordHash;
    this.loggedIn = false;
  }

  public String getPasswordHash() {
    return passwordHash;
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
