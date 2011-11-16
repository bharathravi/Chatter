package chatter.server;

import common.CryptoException;

import java.io.IOException;

/**
 * @author
 *
 * Describes a class that can subsribe to a broadcast service that supports
 * two options:
 * 1. A aimple broadcast message
 * 2. A special shutdown message to indicate that the broadcast service is
 *    shutting down.
 */
public interface BroadcastListener {
  public void onBroadcast(String message) throws IOException, CryptoException;

  void onBroadcastShutdown() throws IOException, CryptoException;
}
