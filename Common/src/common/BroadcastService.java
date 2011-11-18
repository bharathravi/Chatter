package common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/12/11
 * Time: 12:55 AM
 * To change this template use File | Settings | File Templates.
 */
public class BroadcastService {
  private List<BroadcastListener> listeners =
      new ArrayList<BroadcastListener>();

  public BroadcastService() {}

  public void sendBroadcast(String message) throws IOException, CryptoException {
    for (BroadcastListener listener: listeners) {
      listener.onBroadcast(message);
    }
  }

  public void sendShutdown() throws CryptoException, IOException {
    for (BroadcastListener listener: listeners) {
      listener.onBroadcastShutdown();
    }
  }

  public void registerListener(BroadcastListener listener) {
    listeners.add(listener);
  }

  public void unregisterListener(BroadcastListener listener) {
    listeners.remove(listener);
  }

}
