package chatter.common;

import java.math.BigInteger;

/**
 * @author Bharath Ravi
 * @author Kapil Goel
 * @author Alban
 *
 */
public class Constants {
  // Server address and port
  public static final int PORT=50154;
  public static final String HOST="localhost";

  // Maximum number of clients to allow
  public static final int MAXCLIENTS=4;

  // Timeout value (milliseconds) during authentication
  public static final int AUTHENTICATION_TIMEOUT = 300000;

  // Timeout value (milliseconds) during chat
  public static final int CHAT_TIMEOUT = 300000;
  public static final char PASSWORD_SEPARATOR = ',';

  // Quit message that a user types to disconnect.
  public static final String QUIT_MESSAGE = "\\quit";

  // Diffie Helman public components. These are shared between all clients and
  // servers.
  public static final BigInteger P = new BigInteger("115529602823233630672158755882365519342100141357504819670737081924539716828895512891498840689668916836958656957427302202185474382261074814570250959496752527228681272228706898162654174619712741402680404929527074634233500837505091146571964999458669047227608049707279929801629871914634022066697966232479805711627");
  public static final BigInteger G = new BigInteger("21297782867125852211965421360065124617460137303499559381094931685304391685003406453379040479145815123034722781565087951907235444626749305975079465057842189639275312756609155194906728865907677241310941556966388987683709915061271860625168841381797400499944349852651108121179902324271491652816832731399587688291");
  public static final int L = 1023;

  // Algorithm used to encrypt the channel
  public static final String ENCRYPTION_ALGORITHM = "AES";

  // Algorithm used to store hashed passwords.
  public static final String HASHING_ALGORITHM = "SHA-256";
}