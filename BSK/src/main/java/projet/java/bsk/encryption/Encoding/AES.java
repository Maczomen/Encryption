package projet.java.bsk.encryption.Encoding;

import projet.java.bsk.encryption.Encryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

public class AES extends Encryption {

    private String initVector = "AAAAAAAAAAAAAAAAAAAAAA==";
    private final String salt = "12345678";
    private SecretKey key;
    private String algorithmType = "AES/CBC/PKCS5Padding";

    public AES(String secretKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        key = getKeyFromPassword(secretKey);
    }
    public AES(String secretKey, String algorithmType) throws NoSuchAlgorithmException, InvalidKeySpecException {
        key = getKeyFromPassword(secretKey);
        this.algorithmType = algorithmType;
    }
    public AES(String secretKey, String algorithmType, String iv) throws NoSuchAlgorithmException, InvalidKeySpecException {
        key = getKeyFromPassword(secretKey);
        initVector = iv;
        this.algorithmType = algorithmType;
    }

    public static String generateKeyString() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        SecretKey key = keyGenerator.generateKey();
        return encode(key.getEncoded());
    }

    public SecretKey getKeyFromPassword(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 65536, 256);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec)
                .getEncoded(), "AES");
        return secret;
    }

    public IvParameterSpec generateIv() {
        return new IvParameterSpec(decode(initVector));
    }

    public static String generateIvString() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return encode(iv);
    }

    public String encrypt(String algorithm, String input, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        if(algorithm == "AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE,key);
        else
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);


        byte[] cipherText = cipher.doFinal(input.getBytes());
        return encode(cipherText);
    }

    public String decrypt(String algorithm, String cipherText, SecretKey key, IvParameterSpec iv) throws Exception{
        Cipher cipher = Cipher.getInstance(algorithm);
        if(algorithm == "AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE,key);
        else
            cipher.init(Cipher.DECRYPT_MODE, key, iv);
        byte[] plainText = cipher.doFinal(decode(cipherText));
        return new String(plainText);
    }

    public String encrypt(String message, String algorithm) throws Exception{
        IvParameterSpec ivParameterSpec = generateIv();
        return encrypt(algorithm, message, key, ivParameterSpec);
    }

    public String encrypt(String message) throws Exception{
        return encrypt(message, algorithmType);
    }

    public String decrypt(String message, String algorithm) throws Exception{
        IvParameterSpec ivParameterSpec = generateIv();
        return decrypt(algorithm, message, key, ivParameterSpec);
    }

    public String decrypt(String message) throws Exception{
        return decrypt(message, algorithmType);
    }


    public static void encryptFile(String algorithm, SecretKey key, IvParameterSpec iv,
                                   File inputFile, File outputFile) throws Exception {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[16384];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }
    public void encryptFile(File inputFile, File outputFile) throws Exception {
        IvParameterSpec ivParameterSpec = generateIv();
        encryptFile(algorithmType, key,ivParameterSpec,inputFile,outputFile);
    }

    public static void decryptFile(String algorithm, SecretKey key, IvParameterSpec iv,
                                   File inputFile, File outputFile) throws Exception {

        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        byte[] buffer = new byte[16384];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byte[] output = cipher.update(buffer, 0, bytesRead);
            if (output != null) {
                outputStream.write(output);
            }
        }
        byte[] outputBytes = cipher.doFinal();
        if (outputBytes != null) {
            outputStream.write(outputBytes);
        }
        inputStream.close();
        outputStream.close();
    }
    public void decryptFile(File inputFile, File outputFile) throws Exception {
        IvParameterSpec ivParameterSpec = generateIv();
        decryptFile(algorithmType, key,ivParameterSpec,inputFile,outputFile);
    }
}
