package projet.java.bsk.encryption.Message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.apache.commons.io.FilenameUtils;
import projet.java.bsk.encryption.Encoding.AES;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

@Getter
@Setter
@ToString
@EqualsAndHashCode
//@SuperBuilder
public class FileMessage extends Message<byte[]>{

    private String fileName;

    @Override
    public void encrypt(AES aes) throws Exception {
        this.setOwner(aes.encrypt(this.getOwner()));
        this.setFileName(aes.encrypt(this.getFileName()));
        this.setNumberOfPartS(aes.encrypt(String.valueOf(this.getNumberOfPart())));
    }

    @Override
    public void decrypt(AES aes) throws Exception {
        this.setOwner(aes.decrypt(this.getOwner()));
        this.setFileName(aes.decrypt(this.getFileName()));
        this.setNumberOfPart(Integer.valueOf(aes.decrypt(numberOfPartS)));
    }

    @Override
    public void merge(ArrayList<Message<byte[]>> messages, AES aes) {
        File encryptedFile = new File("tempFiles\\encryptedFile" + getFileName());
        try {
            encryptedFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream("tempFiles\\encryptedFile" + getFileName());
            messages.sort((a , b) -> {
                return a.getNumberOfPart() - b.getNumberOfPart();
            });
            for (Message<byte[]>message : messages) {
                if (message.getContent() != null) {
                    outputStream.write(message.getContent());
                }
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        File decryptedFile = new File("ReceivedFiles\\" + getFileName());
        try {
            decryptedFile.createNewFile();
            aes.decryptFile(encryptedFile, decryptedFile);
            encryptedFile.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.setOwner(messages.get(0).getOwner());
    }

    @Override
    public void show() {
        JFrame frame = new JFrame();
        frame.setTitle("Received message");
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setSize(400,400);

        JLabel loginLabel = new JLabel("<html>Otrzymales plik od: " + getOwner() +"<br/><br/> " + fileName + "</html>");
        loginLabel.setBounds(0,0,400,400);
        loginLabel.setHorizontalAlignment(JLabel.CENTER);
        loginLabel.setVerticalAlignment(JLabel.CENTER);

        frame.add(loginLabel);
        frame.setVisible(true);
    }

}
