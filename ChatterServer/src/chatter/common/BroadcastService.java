package chatter.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bharath Ravi
 * @author Kapil Goel
 * @author Alban
 *
 * A Broadcast service that maintains a list of registered
 * {@code BroadcastListener}'s and can be used to send a broadcast message to
 * all registered listeners.
 */
public class BroadcastService {
  private List<BroadcastListener> listeners =
      new ArrayList<BroadcastListener>();

  public BroadcastService() {}

  /**
   * Send a broadcast message to all registered listeners.
   * @param message the string to be broadcast
   */
  public void sendBroadcast(String message) throws IOException, CryptoException {
    for (BroadcastListener listener: listeners) {
      listener.onBroadcast(message);
    }
  }

  /**
   * Send a "shutdown" message to all listeners. This is done when this
   * broadcast service is shutting down.
   */
  public void sendShutdown() throws CryptoException, IOException {
    for (BroadcastListener listener: listeners) {
      listener.onBroadcastShutdown();
    }
  }

  /**
   * Register a listener that wants to receive broadcasts.
   */
  public void registerListener(BroadcastListener listener) {
    listeners.add(listener);
  }

  /**
   * Unregister a listener that is no longer interested in broadcasts.
   */
  public void unregisterListener(BroadcastListener listener) {
    listeners.remove(listener);
  }

}
