package projet.java.bsk.encryption.GUI;

import projet.java.bsk.encryption.Encoding.RSA;
import projet.java.bsk.encryption.ReciveMessage;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.ServerSocket;
import java.util.Scanner;

public class LoggingInFrame{
    private JFrame frame = new JFrame();
    private RSA rsa;
    private ServerSocket serverSocket;

    JTextField loginTextField;
    JTextField passwordTextField;

    public LoggingInFrame(ServerSocket ss){
        this.serverSocket = ss;
        frame.setSize(300,600);

        frame.setTitle("Logowanie do aplikacji");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(null);

        JLabel loginLabel = new JLabel("Login: ");
        loginLabel.setBounds(0,0,100,200);
        loginLabel.setHorizontalAlignment(JLabel.CENTER);
        loginLabel.setVerticalAlignment(JLabel.CENTER);
        frame.add(loginLabel);

        loginTextField = new JTextField();
        loginTextField.setBounds(100,92,175,20);
        frame.add(loginTextField);


        JLabel passwordLabel = new JLabel("Haslo: ");
        passwordLabel.setBounds(0,200,100,200);
        passwordLabel.setHorizontalAlignment(JLabel.CENTER);
        passwordLabel.setVerticalAlignment(JLabel.CENTER);
        frame.add(passwordLabel);

        passwordTextField = new JTextField();
        passwordTextField.setBounds(100,292,175,20);
        frame.add(passwordTextField);

        JButton loginInButton = new JButton("Login in");
        loginInButton.setBounds(50,450,200,100);
        loginInButton.addActionListener(e ->{
                    this.rsa = new RSA(loginTextField.getText(), passwordTextField.getText());
                    ClientFrame clientFrame = new ClientFrame(serverSocket, rsa, loginTextField.getText());
                    frame.dispose();
                });
        loginInButton.setFocusable(false);
        frame.add(loginInButton);

        frame.setVisible(true);
    }
}
