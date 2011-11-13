package chatter.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/11/11
 * Time: 5:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserDatabase {
  HashMap<String, User> database = new HashMap<String, User>();

  UserDatabase() throws NoSuchAlgorithmException {
    MessageDigest md5 = MessageDigest.getInstance("MD5");
    database.put("bharath",
        new User("bharath", "7616b81196ee6fe328497da3f1d9912d"));
  }
}
