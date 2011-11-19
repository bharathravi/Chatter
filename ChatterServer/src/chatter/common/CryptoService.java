package chatter.common;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @author Bharath Ravi
 * @author Kapil Gole
 * @author Alban Dumouilla
 *
 */
class CryptoService {
  Cipher encryptor;
  Cipher decryptor;

  CryptoService(SecretKey key) throws CryptoException {
    try {
      // Use the first 16 bits of the secret to get a 128 but AES key.
      byte[] raw = new byte[16];
      byte[] secret = key.getEncoded();

      for (int i = 0; i < 16; ++i) {
        raw[i] = secret[i];
      }

      SecretKeySpec keySpec =
          new SecretKeySpec(raw, Constants.ENCRYPTION_ALGORITHM);

      encryptor = Cipher.getInstance(Constants.ENCRYPTION_ALGORITHM);
      decryptor = Cipher.getInstance(Constants.ENCRYPTION_ALGORITHM);
      encryptor.init(Cipher.ENCRYPT_MODE, keySpec);
      decryptor.init(Cipher.DECRYPT_MODE, keySpec);
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
      throw new CryptoException(e);
    } catch (NoSuchPaddingException e) {
      e.printStackTrace();
      throw new CryptoException(e);
    } catch (InvalidKeyException e) {
      e.printStackTrace();
      throw new CryptoException(e);
    }
  }

  public String encrypt(String str) throws CryptoException {
    // Encode the string into bytes using utf-8
    byte[] utf8 = new byte[0];
    try {
      utf8 = str.getBytes("UTF8");
      // Encrypt
      byte[] enc = encryptor.doFinal(utf8);

      // Encode bytes to base64 to get a string
      return new sun.misc.BASE64Encoder().encode(enc);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      throw new CryptoException(e);
    } catch (BadPaddingException e) {
      e.printStackTrace();
      throw new CryptoException(e);
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
      throw new CryptoException(e);
    }
  }

  public String decrypt(String str) throws CryptoException {
    // Decode base64 to get bytes
    byte[] dec = new byte[0];
    try {
      dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
      byte[] utf8 = decryptor.doFinal(dec);

      // Decode using utf-8
      return new String(utf8, "UTF8");
    } catch (IOException e) {
      e.printStackTrace();
      throw new CryptoException(e);
    } catch (BadPaddingException e) {
      e.printStackTrace();
      throw new CryptoException(e);
    } catch (IllegalBlockSizeException e) {
      e.printStackTrace();
      throw new CryptoException(e);
    }
  }
}