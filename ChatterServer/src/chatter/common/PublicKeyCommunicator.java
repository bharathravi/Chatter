package chatter.common;

/**
 * @author Bharath Ravi
 * @author Kapil Gole
 * @author Alban Dumouilla
 *
 * This interface defines a class that can send and receive Public Key
 * bytes to/from a third party
 */
public interface PublicKeyCommunicator {

  public byte[] getPublicKeyBytes() throws PublicKeyCommunicationException;

  public void sendPublicKeyBytes(byte[] publicKey) throws PublicKeyCommunicationException;
}
