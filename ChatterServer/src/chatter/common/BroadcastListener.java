package chatter.common;

import java.io.IOException;

/**
 * @author Bharath Ravi
 * @author Kapil Goel
 * @author Alban
 *
 * Describes a class that can subsribe to a broadcast service that supports
 * two options:
 * 1. A simple broadcast message, transmitted by some {@code BroadcastService}
 * 2. A special shutdown message to indicate that the broadcast service is
 *    shutting down.
 */
public interface BroadcastListener {
  public void onBroadcast(String message) throws IOException, CryptoException;

  void onBroadcastShutdown() throws IOException, CryptoException;
}
