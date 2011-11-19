package chatter.server;

/**
 * @author Bharath Ravi
 * @author Kapil Gole
 * @author Alban Dumouilla
 *
 *
 * This class simply maintains a count of how many clients
 * are currently being served.
 */
public class ClientCountMonitor {
  private int clientCount;

  ClientCountMonitor() {
    clientCount = 0;
  }


  public int getClientCount() {
    return clientCount;
  }

  public synchronized void setClientCount(int clientCount) {
    this.clientCount = clientCount;
  }

  public synchronized void incrementClientCount() {
    this.clientCount++;
  }

  public synchronized void decrementClientCount() {
     clientCount--;
  }
}
