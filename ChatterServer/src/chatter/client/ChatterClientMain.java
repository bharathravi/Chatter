package chatter.client;

import chatter.common.CryptoException;

/**
 * @author Bharath Ravi
 * @author Kapil Gole
 * @author Alban Dumouilla
 *
 * The "main" class for the client that handles starting up and shutting down
 * the client thread.
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
