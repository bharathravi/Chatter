package chatter.server;

/**
 * @author Bharath Ravi
 * @author Kapil Goel
 * @author Alban
 *
 * The "main" class for the Server, that handles startup and shutdown of the
 * Server thread.
 */
public class ChatterServerMain {
  public static void main(String[] args) {
    final ChatterServer server = new ChatterServer();

    // Add a shutdown hook.
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        System.out.println("Shutting down server...");
        server.interrupt();
        System.out.println("Done. Bye!");
      }
    });

    System.out.println("Starting up");
    server.start();
  }
}
