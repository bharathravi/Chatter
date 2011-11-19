package chatter.server;

/**
 * @author Bharath Ravi
 * @author Kapil Gole
 * @author Alban Dumouilla
 *
 * Represents a single User in the system and stores relevant data.
 * The class stores 4 pieces of information:
 * 1. The username
 * 2. A hash of the user's password (combined with a random salt)
 * 3. A random salt string combined with the cleartext password to
 * generate the hash
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
