package chatter.server;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/12/11
 * Time: 1:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class Main {
  public static void main(String[] args) {
    ChatterServer server = new ChatterServer();
    server.startListening();

    //TODO(bharath) Figure out ^C handling
  }
}
