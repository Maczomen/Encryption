package projet.java.bsk.encryption;

import projet.java.bsk.encryption.Encoding.AES;
import projet.java.bsk.encryption.Encoding.RSA;
import projet.java.bsk.encryption.Message.FileMessage;
import projet.java.bsk.encryption.Message.HeaderMessage;
import projet.java.bsk.encryption.Message.Message;
import projet.java.bsk.encryption.Message.TextMessage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

class ReciveMessageHandler implements Runnable
{
    private final RSA rsa;
    final Socket socket;

    // Constructor
    public ReciveMessageHandler(Socket s, RSA rsa)  {
        this.socket = s;
        this.rsa = rsa;
    }

    @Override
    public void run()
    {
        try{
            try (ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

                output.writeObject(rsa.encode(rsa.publicKey.getEncoded()));
                System.out.println("Wysłano klucz publiczny.");


                HeaderMessage headerMessage = (HeaderMessage) input.readObject();
                headerMessage.decrypt(rsa);
                AES aes = new AES(headerMessage.sesionKey,headerMessage.encryptionType,headerMessage.initVector);

                Message message = null;
                ArrayList<Message> messages = new ArrayList<>();
                for (int i = 0; i < headerMessage.getNumberOfParts(); i++) {
                    message = (Message) input.readObject();
                    message.decrypt(aes);
                    messages.add(message);

                    System.out.println("Odebrano wiadomosc " + (i + 1));
                }

                message.merge(messages, aes);
                message.show();

            } catch (Exception ex) {

            }finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ignored) {}
                }
            }
        } catch (Exception ignored){}

    }

}