package projet.java.bsk.encryption;


import org.apache.commons.io.FilenameUtils;
import projet.java.bsk.encryption.Encoding.AES;
import projet.java.bsk.encryption.Encoding.RSA;
import projet.java.bsk.encryption.GUI.LoggingInFrame;
import projet.java.bsk.encryption.Message.Message;
import projet.java.bsk.encryption.Message.TextMessage;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 *
 * @author Piotrek
 */
public class Client
{
    public static byte[] addAll(final byte[] array1, byte[] array2) {
        byte[] joinedArray = Arrays.copyOf(array1, array1.length + array2.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }

    public static void main(String[] args) throws IOException
    {

        ServerSocket ss = new ServerSocket(0);
        new LoggingInFrame(ss);

        System.out.println("listening on port: " + ss.getLocalPort());
    }
} 