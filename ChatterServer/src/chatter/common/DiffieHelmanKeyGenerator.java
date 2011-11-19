package chatter.common;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 *@author Bharath Ravi
 * @author Kapil Goel
 * @author Alban
 *
 */
public class DiffieHelmanKeyGenerator {
  PublicKeyCommunicator communicator;

  DiffieHelmanKeyGenerator(PublicKeyCommunicator communicator) {
    this.communicator = communicator;
  }

  public SecretKey generate() throws DiffieHellmanException,
      PublicKeyCommunicationException, InvalidKeyException,
      NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeySpecException {

    KeyPair keys = generateKeyPair();
    PublicKey myPublicKey = keys.getPublic();
    communicator.sendPublicKeyBytes(myPublicKey.getEncoded());
    PublicKey theirPublicKey = getOtherPublicKey();

    return generateSecretKey(keys, theirPublicKey);
  }

  private SecretKey generateSecretKey(KeyPair keys, PublicKey theirPublicKey)
      throws NoSuchAlgorithmException, InvalidKeyException {
    // Prepare to generate the secret key with the
    // private key and public key of the other party
    KeyAgreement ka = KeyAgreement.getInstance("DH");
    ka.init(keys.getPrivate());
    ka.doPhase(theirPublicKey, true);

    // Generate the secret key
    return ka.generateSecret(Constants.ENCRYPTION_ALGORITHM);
  }

  private KeyPair generateKeyPair() throws NoSuchAlgorithmException,
      InvalidAlgorithmParameterException {
    KeyPairGenerator keyGen = null;
    KeyPair keypair = null;

    keyGen = KeyPairGenerator.getInstance("DH");

    DHParameterSpec dhSpec = new DHParameterSpec(Constants.P, Constants.G,
        Constants.L);
    keyGen.initialize(dhSpec);
    keypair = keyGen.generateKeyPair();

    return keypair;
  }

  private PublicKey getOtherPublicKey()
      throws InvalidKeySpecException, NoSuchAlgorithmException,
      PublicKeyCommunicationException {
    PublicKey pKey;

    byte[] pKeyBytes = new byte[0];

    pKeyBytes = communicator.getPublicKeyBytes();

    // Convert the public key bytes into a PublicKey object
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pKeyBytes);
    KeyFactory keyFact = KeyFactory.getInstance("DH");
    pKey = keyFact.generatePublic(x509KeySpec);

    return pKey;
  }


}
