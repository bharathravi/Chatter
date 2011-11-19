package chatter.server;

import chatter.common.Constants;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Bharath Ravi
 * @author Kapil Gole
 * @author Alban Dumouilla
 *
 * An authenticator that verifies a username and password
 * by comparing hashed values of the password from a predetermined
 * Database of authentic users and passwords.
 */
public class ClientAuthenticator {
  String uname;
  String passwd;
  UserDatabase database;

  public ClientAuthenticator(String unamePasswd) {
    int separator = unamePasswd.indexOf(Constants.PASSWORD_SEPARATOR);
    uname = unamePasswd.substring(0, separator);
    passwd = unamePasswd.substring(separator + 1, unamePasswd.length());
    database = UserDatabase.getInstance();
  }

  public synchronized boolean authenticate() {
    try {
      MessageDigest sha256 = MessageDigest.getInstance(
          Constants.HASHING_ALGORITHM);
      if (database.database.containsKey(uname)) {
        User user = database.database.get(uname);

        // Update the sha256 sum with the password and the salt.
        sha256.update(user.getSalt().getBytes());
        sha256.update(passwd.getBytes());

        // Compute the sha256 digest.
        byte[] passwdHash = sha256.digest();
        BigInteger number = new BigInteger(1, passwdHash);
        String hashtext = number.toString(16);

        if(!user.isLoggedIn() && user.getPasswordHash().equals(hashtext)) {
          user.setLoggedIn(true);
          return true;
        }
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return false;
  }
}
