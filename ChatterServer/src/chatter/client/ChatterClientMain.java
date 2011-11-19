package chatter.client;

import chatter.common.CryptoException;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/13/11
 * Time: 10:25 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChatterClientMain {

  public static void main(String[] args) throws CryptoException {
    final ChatterClient client = new ChatterClient();

    // Add a shutdown hook.
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        System.out.println("Shutting down...");
        client.interrupt();
        System.out.println("Done. Bye!");
      }
    });

    client.start();

    return;
  }
}
