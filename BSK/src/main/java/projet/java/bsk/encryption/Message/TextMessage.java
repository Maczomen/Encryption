package projet.java.bsk.encryption.Message;

import lombok.*;
import lombok.experimental.SuperBuilder;
import projet.java.bsk.encryption.Encoding.AES;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;


@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public class TextMessage extends Message<String>{

    @Builder.Default
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected String numberOfPartS = "1";
    @Builder.Default
    protected transient int numberOfPart = 1;

    @Override
    public void encrypt(AES aes) throws Exception {
        this.setOwner(aes.encrypt(this.getOwner()));
        this.setContent(aes.encrypt(this.getContent()));
        numberOfPartS = aes.encrypt(String.valueOf(this.getNumberOfPart()));
    }

    @Override
    public void decrypt(AES aes) throws Exception {
        this.setContent(aes.decrypt(this.getContent()));
        this.setOwner(aes.decrypt(this.getOwner()));
        this.setNumberOfPart(Integer.valueOf(aes.decrypt(numberOfPartS)));
    }

    @Override
    public void merge(ArrayList<Message<String>> messages, AES aes) {
        String mergedContent = new String();
        messages.sort((a , b) -> {
            return a.getNumberOfPart() - b.getNumberOfPart();
        });
        for (Message<String>message : messages) {
            mergedContent += message.getContent();
        }
        this.setContent(mergedContent);
        this.setOwner(messages.get(0).getOwner());
    }

    @Override
    public void show() {
        JFrame frame = new JFrame();
        frame.setTitle("Received message");
        //frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        //frame.setResizable(false);
        frame.setLayout(null);
        frame.setSize(400,400);

        JLabel loginLabel = new JLabel("<html>Otrzymales wiadomosc od: " + getOwner() +"<br/><br/> " + getContent() + "</html>");
        loginLabel.setBounds(0,0,400,400);
        loginLabel.setHorizontalAlignment(JLabel.CENTER);
        loginLabel.setVerticalAlignment(JLabel.CENTER);
        frame.add(loginLabel);
        frame.setVisible(true);
    }
}
