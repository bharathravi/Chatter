package chatter.common;

import java.io.IOException;

/**
 * @author Bharath Ravi
 * This interface defines a class that can send and receive Public Key
 * bytes to/from a third party
 */
public interface PublicKeyCommunicator {

  public byte[] getPublicKeyBytes() throws IOException;

  public void sendPublicKeyBytes(byte[] publicKey) throws IOException;
}
