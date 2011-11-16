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

  private static final UserDatabase instance = new UserDatabase();

  public static UserDatabase getInstance() {
    return instance;
  }



  private UserDatabase(){
 /* try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update("n)(3A93nA42a".getBytes());
      md5.update("bharath".getBytes());

      byte[] b = md5.digest();
      for (int i=0; i<b.length; ++i) {
        System.out.print(b[i]));
      }
    } catch (NoSuchAlgorithmException e) {
      System.out.println("Unable to create an MD5 hasher.");
      e.printStackTrace();
    }        */




    database.put("bharath",
        new User("bharath", "42087791f544a461d62ea987de101695","n)(3A93nA42a2946"));
    database.put("alban",
        new User("alban", "46d49b3bc19b9e42979e6806aa4abb44", "(FNXO#ns&fd91956"));
    database.put("kapil",
        new User("kapil", "3b78c7dfa5c11aac1c2644db407ddf5","}{fm09x)ncTT8456"));
  }
}
