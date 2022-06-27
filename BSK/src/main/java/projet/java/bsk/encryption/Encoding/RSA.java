package projet.java.bsk.encryption.Encoding;
import projet.java.bsk.encryption.Encryption;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Scanner;

public class RSA extends Encryption {

    private PrivateKey privateKey;
    public PublicKey publicKey;

    private boolean shouldBeSaved = true;

    public RSA() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            KeyPair pair = generator.generateKeyPair();
            privateKey = pair.getPrivate();
            publicKey = pair.getPublic();
        } catch (Exception ignored) {}
    }

    public RSA(String login, String password) {
        if(!retriveKeys(login,password)){
            try {
                KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
                generator.initialize(1024);
                KeyPair pair = generator.generateKeyPair();
                privateKey = pair.getPrivate();
                publicKey = pair.getPublic();

                if (shouldBeSaved){
                    saveKeys(login, password);
                }

            } catch (Exception ignored) {}
        }
    }

    private String location(String login, boolean isPrivate){return "Users\\" + login + (isPrivate ?  "private.txt" : "public.txt");}

    private String retriveKey(String location) throws FileNotFoundException{
        File file = new File(location);
        Scanner myReader = new Scanner(file);
        return  myReader.nextLine();
    }

    private boolean retriveKeys(String login, String password){
        try{
            String privateKeyEncrypted = retriveKey(location(login,true));
            String publicKeyDecrypted = retriveKey(location(login,false));

            String passwordHash = getHash(decode(password));
            AES aes = new AES(passwordHash);

            String privateKeyDecrypted = privateKeyEncrypted;
            try {
                privateKeyDecrypted = aes.decrypt(privateKeyEncrypted);
            } catch (Exception e) {
                System.out.println("Failed to decrypt privateKey");
            }

            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(publicKeyDecrypted));
            PKCS8EncodedKeySpec keySpecPrivate = new PKCS8EncodedKeySpec(decode(privateKeyDecrypted));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            publicKey = keyFactory.generatePublic(keySpecPublic);
            privateKey = keyFactory.generatePrivate(keySpecPrivate);

            System.out.println("PrivateKey: "+ encode(privateKey.getEncoded()));
            System.out.println("PublicKey: "+ encode(publicKey.getEncoded()));
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("New user");
            return false;
        } catch (Exception e) {
            System.out.println("Error after retriving private and public keys");
            shouldBeSaved = false;
            return true;
        }
    }

    private void saveKey(String location, String content){
        try {
            File keyFile = new File(location);
            keyFile.createNewFile();
            FileWriter myWriter = new FileWriter(location);
            myWriter.write(content);
            myWriter.close();
        } catch (IOException e){
            System.out.println("Error while saving " + location);
        }
    }

    private void saveKeys(String login, String password){
        String passwordHash = getHash(decode(password));
        String privateKeyEncrypted = null;
        try {
            AES aes = new AES(passwordHash);
            privateKeyEncrypted = aes.encrypt(encode(privateKey.getEncoded()));
        } catch (Exception e) {
            System.out.println("Failed to encrypt privateKey");
        }

        saveKey(location(login,true),privateKeyEncrypted);
        saveKey(location(login,false),encode(publicKey.getEncoded()));
        System.out.println("PrivateKey: "+ encode(privateKey.getEncoded()));
        System.out.println("PublicKey: "+ encode(publicKey.getEncoded()));
    }

    private PublicKey publicKeyFromString(String publicKeyString){
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpecPublic = new X509EncodedKeySpec(decode(publicKeyString));

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");

            publicKey = keyFactory.generatePublic(keySpecPublic);
        }catch (Exception ignored){}
        return publicKey;
    }

    //@Override
    public String encrypt(String message, String publicKeyString) throws Exception{
        byte[] messageToBytes = message.getBytes();
        return encrypt(messageToBytes,publicKeyString);
    }

    public String encrypt(byte[] messageToBytes, String publicKeyString) throws Exception{
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        PublicKey publicKey = publicKeyFromString(publicKeyString);
        cipher.init(Cipher.ENCRYPT_MODE,publicKey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }

    //@Override
    public String decrypt(String encryptedMessage) throws Exception{
        byte[] encryptedBytes = decode(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE,privateKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
        return new String(decryptedMessage,"UTF8");
    }
}
