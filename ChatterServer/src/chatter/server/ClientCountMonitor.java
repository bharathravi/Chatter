package chatter.server;

import java.util.ArrayList;

/**
 * @author Bharath Ravi
 *
 * This class simply maintains a count of how many clients
 * are currently being processed at the server.
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
