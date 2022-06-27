package projet.java.bsk.encryption.GUI;

import org.apache.commons.io.FilenameUtils;
import projet.java.bsk.encryption.Encoding.AES;
import projet.java.bsk.encryption.Encoding.RSA;
import projet.java.bsk.encryption.Message.FileMessage;
import projet.java.bsk.encryption.Message.HeaderMessage;
import projet.java.bsk.encryption.Message.TextMessage;
import projet.java.bsk.encryption.ReciveMessage;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.ArrayList;

public class ClientFrame implements ActionListener {
    private JFrame frame = new JFrame();
    private ServerSocket serverSocket;
    private RSA rsa;
    private Thread reciveMessageThread;

    public static String login;

    public static final int FRAME_WIDTH = 400;
    public static final int FRAME_HEIGHT = 600;

    private JPanel chooseMessageDestinationPanel = new JPanel();
    private JPanel sendTextMessagePanel = new JPanel();
    private JPanel sendFileMessagePanel = new JPanel();

    private JTextField destinationTextField;

    private int destination;
    private String decidedEncryption = "AES/CBC/PKCS5Padding";

    private JRadioButton cbcEncryptionButton;
    private JRadioButton ecbEncryptionButton;
    private File file = null;


    public ClientFrame(ServerSocket ss, RSA rsa, String login){
        this.serverSocket = ss;
        this.rsa = rsa;
        this.login = login;
        reciveMessageThread = new Thread(new ReciveMessage(ss, rsa));
        reciveMessageThread.start();

        frame.setTitle("Main app");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setResizable(false);
        frame.setLayout(null);
        frame.setSize(FRAME_WIDTH,FRAME_HEIGHT);


        createChooseMessageDestinationPanel();
        createSendTextMessagePanel();
        createSendFileMessagePanel();


        frame.add(chooseMessageDestinationPanel);
        frame.setVisible(true);
    }

    private void createChooseMessageDestinationPanel(){
        chooseMessageDestinationPanel.setBounds(0,0, FRAME_WIDTH, FRAME_HEIGHT);
        chooseMessageDestinationPanel.setLayout(null);

        JLabel loginLabel = new JLabel("<html>Listening on port: " + serverSocket.getLocalPort() +"<br/>Login: " + login + "</html>");
        loginLabel.setBounds(0,0,FRAME_WIDTH,FRAME_HEIGHT/3);
        loginLabel.setHorizontalAlignment(JLabel.CENTER);
        loginLabel.setVerticalAlignment(JLabel.CENTER);
        chooseMessageDestinationPanel.add(loginLabel);

        JLabel destinationLabel = new JLabel("Wyślij wiadomość do:");
        destinationLabel.setBounds(0,FRAME_HEIGHT/3,FRAME_WIDTH/3*2,FRAME_HEIGHT/3);
        destinationLabel.setHorizontalAlignment(JLabel.CENTER);
        destinationLabel.setVerticalAlignment(JLabel.CENTER);
        chooseMessageDestinationPanel.add(destinationLabel);

        destinationTextField = new JTextField();
        destinationTextField.setBounds(FRAME_WIDTH*2/3,(FRAME_HEIGHT/3 + FRAME_HEIGHT/3/2-8),FRAME_WIDTH/3-25,20);
        chooseMessageDestinationPanel.add(destinationTextField);

        JLabel encryptionTypeLabel = new JLabel("Typ szyfrowania:");
        encryptionTypeLabel.setBounds(0,310,FRAME_WIDTH,40);
        encryptionTypeLabel.setHorizontalAlignment(JLabel.CENTER);
        encryptionTypeLabel.setVerticalAlignment(JLabel.CENTER);
        chooseMessageDestinationPanel.add(encryptionTypeLabel);

        cbcEncryptionButton = new JRadioButton("AES/CBC");
        cbcEncryptionButton.setSelected(true);
        cbcEncryptionButton.setBounds(50, 350,100,60);
        ecbEncryptionButton = new JRadioButton("AES/ECB");
        ecbEncryptionButton.setBounds(200, 350,100,60);
        ButtonGroup encryptionButton = new ButtonGroup();
        encryptionButton.add(cbcEncryptionButton);
        encryptionButton.add(ecbEncryptionButton);
        chooseMessageDestinationPanel.add(ecbEncryptionButton);
        chooseMessageDestinationPanel.add(cbcEncryptionButton);

        JButton sendTextMessageButton = new JButton("Send Text");
        sendTextMessageButton.setBounds(FRAME_WIDTH/24,FRAME_HEIGHT/3*2+FRAME_HEIGHT/24,FRAME_WIDTH/2 - FRAME_WIDTH/24*2-20,FRAME_HEIGHT/6);
        sendTextMessageButton.addActionListener(e ->{
            if(!destinationTextField.getText().isEmpty()){
                destination = Integer.valueOf(destinationTextField.getText());
                frame.remove(chooseMessageDestinationPanel);
                frame.add(sendTextMessagePanel);
                frame.revalidate();
                frame.repaint();
            }
        });
        sendTextMessageButton.setFocusable(false);
        chooseMessageDestinationPanel.add(sendTextMessageButton);

        JButton sendFileMessageButton = new JButton("Send File");
        sendFileMessageButton.setBounds(FRAME_WIDTH/24 + FRAME_WIDTH/2,FRAME_HEIGHT/3*2+FRAME_HEIGHT/24,FRAME_WIDTH/2 - FRAME_WIDTH/24*2 - 20,FRAME_HEIGHT/6);
        sendFileMessageButton.addActionListener(e ->{
            if(!destinationTextField.getText().isEmpty()){
                this.destination = Integer.valueOf(destinationTextField.getText());
                frame.remove(chooseMessageDestinationPanel);
                frame.add(sendFileMessagePanel);
                frame.revalidate();
                frame.repaint();
            }
        });
        sendFileMessageButton.setFocusable(false);
        chooseMessageDestinationPanel.add(sendFileMessageButton);
    }

    private void createSendTextMessagePanel(){
        sendTextMessagePanel.setBounds(0,0, FRAME_WIDTH, FRAME_HEIGHT);
        sendTextMessagePanel.setLayout(null);

        JLabel loginLabel = new JLabel("<html>Listening on port: " + serverSocket.getLocalPort() +"<br/>Login: " + login + "</html>");
        loginLabel.setBounds(0,0,FRAME_WIDTH,FRAME_HEIGHT/3);
        loginLabel.setHorizontalAlignment(JLabel.CENTER);
        loginLabel.setVerticalAlignment(JLabel.CENTER);
        sendTextMessagePanel.add(loginLabel);

        JLabel destinationLabel = new JLabel("Tresc wiadomość:");
        destinationLabel.setBounds(0,FRAME_HEIGHT/3,FRAME_WIDTH/3*2,FRAME_HEIGHT/3);
        destinationLabel.setHorizontalAlignment(JLabel.CENTER);
        destinationLabel.setVerticalAlignment(JLabel.CENTER);
        sendTextMessagePanel.add(destinationLabel);

        JTextField messageContentTextField = new JTextField();
        messageContentTextField.setBounds(FRAME_WIDTH*2/3,(FRAME_HEIGHT/3 + FRAME_HEIGHT/3/2-8),FRAME_WIDTH/3-25,20);
        sendTextMessagePanel.add(messageContentTextField);

        JButton sendFileMessageButton = new JButton("Send");
        sendFileMessageButton.setBounds(FRAME_WIDTH/24,FRAME_HEIGHT/3*2+FRAME_HEIGHT/24,FRAME_WIDTH - FRAME_WIDTH/24*2-20,FRAME_HEIGHT/6);
        sendFileMessageButton.addActionListener(e ->{

            try (Socket socket = new Socket("localhost", destination)) {
                try(ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream input = new ObjectInputStream(socket.getInputStream())){

                    String publicKeyPartner = (String) input.readObject();
                    System.out.println("Otrzymano klucz publiczny");

                    String message = messageContentTextField.getText();
                    try{


                        String sesionKey = AES.generateKeyString();
                        String sesionIv = AES.generateIvString();

                        HeaderMessage messageHeader = HeaderMessage.builder()
                                .encryptionType(decidedEncryption)
                                .sesionKey(sesionKey)
                                .initVector(sesionIv)
                                .build();

                        messageHeader.encrypt(rsa, publicKeyPartner);
                        output.writeObject(messageHeader);
                        output.flush();
                        System.out.println("Wysłano header");

                        AES aes = new AES(sesionKey, decidedEncryption, sesionIv);
                        TextMessage encryptedMessage = TextMessage.builder()
                                .owner(login)
                                .content(message)
                                .build();
                        encryptedMessage.encrypt(aes);
                        output.writeObject(encryptedMessage);
                        output.flush();
                        System.out.println("Wysłano wiadomosc");

                    }catch (Exception ingored){
                        throw new IOException();
                    }
                } catch (IOException ignored) {
                    System.out.println("Input, output się nie stworzyły");
                } catch (ClassNotFoundException ignored) {
                    System.out.println("Input się nie dziala jak nalezy");
                }

            } catch (IOException ignored) {
                System.out.println("Socket się nie otworzył");
            }

            messageContentTextField.setText("");
            frame.add(chooseMessageDestinationPanel);
            frame.remove(sendTextMessagePanel);
            frame.revalidate();
            frame.repaint();
        });
        sendFileMessageButton.setFocusable(false);
        sendTextMessagePanel.add(sendFileMessageButton);
    }

    private void createSendFileMessagePanel(){
        sendFileMessagePanel.setBounds(0,0, FRAME_WIDTH, FRAME_HEIGHT);
        sendFileMessagePanel.setLayout(null);

        JLabel loginLabel = new JLabel("<html>Listening on port: " + serverSocket.getLocalPort() +"<br/>Login: " + login + "</html>");
        loginLabel.setBounds(0,0,FRAME_WIDTH,FRAME_HEIGHT/3);
        loginLabel.setHorizontalAlignment(JLabel.CENTER);
        loginLabel.setVerticalAlignment(JLabel.CENTER);
        sendFileMessagePanel.add(loginLabel);

        JLabel destinationLabel = new JLabel("Wybierz plik do przeslania:");
        destinationLabel.setBounds(0,FRAME_HEIGHT/3,FRAME_WIDTH,FRAME_HEIGHT/3);
        destinationLabel.setHorizontalAlignment(JLabel.CENTER);
        destinationLabel.setVerticalAlignment(JLabel.CENTER);
        sendFileMessagePanel.add(destinationLabel);

        JButton chooseFileMessageButton = new JButton("Select File");
        chooseFileMessageButton.setBounds(FRAME_WIDTH/24,FRAME_HEIGHT/2 + 30,FRAME_WIDTH - FRAME_WIDTH/24*2-20,FRAME_HEIGHT/12);
        chooseFileMessageButton.addActionListener(e ->{
            JFileChooser fileChooser = new JFileChooser();
            if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                file = new File(fileChooser.getSelectedFile().getAbsolutePath());
            };
        });
        sendFileMessagePanel.add(chooseFileMessageButton);


        JButton sendFileMessageButton = new JButton("Send");
        sendFileMessageButton.setBounds(FRAME_WIDTH/24,FRAME_HEIGHT/3*2+FRAME_HEIGHT/24,FRAME_WIDTH - FRAME_WIDTH/24*2-20,FRAME_HEIGHT/6);
        sendFileMessageButton.addActionListener(e ->{
            if(file.exists()){
                try (Socket socket = new Socket("localhost", destination)) {
                    try(ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        ObjectInputStream input = new ObjectInputStream(socket.getInputStream())){

                        String publicKeyPartner = (String) input.readObject();
                        System.out.println("Otrzymano klucz publiczny");

                        try{
                            String sesionKey = AES.generateKeyString();
                            String sesionIv = AES.generateIvString();

                            AES aes = new AES(sesionKey, decidedEncryption, sesionIv);
                            File encryptedFile = new File("tempFiles\\encryptedFile." + FilenameUtils.getExtension(file.getAbsolutePath()));
                            encryptedFile.createNewFile();
                            aes.encryptFile(file, encryptedFile);

                            double full_size = encryptedFile.length();
                            HeaderMessage messageHeader = HeaderMessage.builder()
                                    .encryptionType(decidedEncryption)
                                    .sesionKey(sesionKey)
                                    .initVector(sesionIv)
                                    .numberOfParts((int)(Math.ceil(full_size/16384.0)))
                                    .build();
                            messageHeader.encrypt(rsa, publicKeyPartner);
                            output.writeObject(messageHeader);
                            output.flush();
                            System.out.println("Wysłano header");

                            FileInputStream inputStream = new FileInputStream(encryptedFile);
                            byte[] buffer = new byte[16384];
                            int bytesRead;
                            int nrOfMessage = 0;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                FileMessage encryptedMessage = new FileMessage();
                                encryptedMessage.setOwner(login);
                                encryptedMessage.setContent(buffer);
                                encryptedMessage.setNumberOfPart(nrOfMessage);
                                encryptedMessage.setFileName(file.getName());

                                nrOfMessage++;

                                encryptedMessage.encrypt(aes);

                                output.writeObject(encryptedMessage);
                                output.flush();
                                System.out.println("Wysłano wiadomosc" + nrOfMessage);
                            }
                            int k = 1;
                            encryptedFile.delete();
                            inputStream.close();

                        }catch (Exception ingored){
                            throw new IOException();
                        }
                    } catch (IOException ignored) {
                        System.out.println("Input, output się nie stworzyły");
                    } catch (ClassNotFoundException ignored) {
                        System.out.println("Input się nie dziala jak nalezy");
                    }

                } catch (IOException ignored) {
                    System.out.println("Socket się nie otworzył");
                }


                frame.add(chooseMessageDestinationPanel);
                frame.remove(sendFileMessagePanel);
                frame.revalidate();
                frame.repaint();
            }
        });
        sendFileMessageButton.setFocusable(false);
        sendFileMessagePanel.add(sendFileMessageButton);
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == ecbEncryptionButton){
            decidedEncryption = "AES/ECB/PKCS5Padding";
        } else if(e.getSource() == cbcEncryptionButton){
            decidedEncryption = "AES/CBC/PKCS5Padding";
        }
    }
}
