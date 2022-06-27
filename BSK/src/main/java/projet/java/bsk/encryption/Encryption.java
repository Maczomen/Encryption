package projet.java.bsk.encryption;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public abstract class Encryption {
    //public abstract String decrypt(String encryptedMessage) throws Exception;
    //public abstract String encrypt(String message, String publicKeyString) throws Exception;

    public static String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

    public static byte[] decode(String data){
        return Base64.getDecoder().decode(data);
    }

    public static String getHash(byte[] input) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(input);
            byte[] digestedBytes = messageDigest.digest();
            String hashValue = encode(digestedBytes);
            return hashValue;
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Hash function is not working");
            return "";
        }
    }
}
