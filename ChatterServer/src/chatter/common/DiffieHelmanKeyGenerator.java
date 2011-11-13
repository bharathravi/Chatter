package chatter.common;

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.DHParameterSpec;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static chatter.common.Constants.G;
import static chatter.common.Constants.L;
import static chatter.common.Constants.P;

/**
 * Created by IntelliJ IDEA.
 * User: bharath
 * Date: 11/13/11
 * Time: 6:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class DiffieHelmanKeyGenerator {
  PublicKeyCommunicator communicator;

  DiffieHelmanKeyGenerator(PublicKeyCommunicator communicator) {
    this.communicator = communicator;
  }

  public SecretKey generate() throws DiffieHellmanException {
    try {
      KeyPair keys = generateKeyPair();
      PublicKey myPublicKey = keys.getPublic();
      communicator.sendPublicKeyBytes(myPublicKey.getEncoded());
      PublicKey theirPublicKey = getOtherPublicKey();

      return generateSecretKey(keys, theirPublicKey);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw new DiffieHellmanException(e);
    } catch (InvalidKeyException e) {
      e.printStackTrace();
      throw new DiffieHellmanException(e);
    } catch (InvalidKeySpecException e) {
      e.printStackTrace();
      throw new DiffieHellmanException(e);
    } catch (InvalidAlgorithmParameterException e) {
      e.printStackTrace();
      throw new DiffieHellmanException(e);
    } catch (IOException e) {
      e.printStackTrace();
      throw new DiffieHellmanException(e);
    }
  }

  private SecretKey generateSecretKey(KeyPair keys, PublicKey theirPublicKey) throws NoSuchAlgorithmException, InvalidKeyException {
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

    DHParameterSpec dhSpec = new DHParameterSpec(P, G, L);
    keyGen.initialize(dhSpec);
    keypair = keyGen.generateKeyPair();

    return keypair;
  }

  private PublicKey getOtherPublicKey()
      throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
    PublicKey pKey = null;

    byte[] pKeyBytes =  communicator.getPublicKeyBytes();

    // Convert the public key bytes into a PublicKey object
    X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(pKeyBytes);
    KeyFactory keyFact = KeyFactory.getInstance("DH");
    pKey = keyFact.generatePublic(x509KeySpec);


    return pKey;
  }


}
