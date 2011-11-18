package chatter.server;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/12/11
 * Time: 1:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class ChatterServerMain {
  public static void main(String[] args) {
    final ChatterServer server = new ChatterServer();

    // Add a shutdown hook.
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        System.out.println("Shutting down server...");
        server.shutdown();
        System.out.println("Done. Bye!");
      }
    });

    System.out.println("Starting up");
    server.startListening();
  }
}
