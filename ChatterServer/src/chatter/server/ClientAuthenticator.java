package chatter.server;

import chatter.common.Constants;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/11/11
 * Time: 5:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClientAuthenticator {
  String uname;
  String passwd;
  UserDatabase database;

  public ClientAuthenticator(String unamePasswd) {
    int separator = unamePasswd.indexOf(Constants.PASSWORD_SEPARATOR);
    uname = unamePasswd.substring(0, separator);
    passwd = unamePasswd.substring(separator + 1, unamePasswd.length());
    try {
      database = new UserDatabase();
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
  }

  public boolean authenticate() {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      byte[] passwdHash = md5.digest(passwd.getBytes());
      BigInteger number = new BigInteger(1, passwdHash);
      String hashtext = number.toString(16);

      if (database.database.containsKey(uname)) {
        User user = database.database.get(uname);

        if(user.getPasswordHash().equals(hashtext) &&
            !user.isLoggedIn())
          return true;
      }
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }

    return false;
  }
}
