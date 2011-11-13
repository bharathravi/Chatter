package chatter.server;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/12/11
 * Time: 12:56 AM
 * To change this template use File | Settings | File Templates.
 */
public interface BroadcastListener {
  public void onBroadcast(String message) throws IOException;
}
