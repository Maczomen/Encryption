package projet.java.bsk.encryption;

import projet.java.bsk.encryption.Encoding.RSA;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayDeque;

public class ReciveMessage implements Runnable{

    private final RSA rsa;
    private final ServerSocket ss;
    private ArrayDeque<Thread> threads = new ArrayDeque<>();

    public ReciveMessage(ServerSocket serverSockets, RSA rsa){
        this.ss = serverSockets;
        this.rsa = rsa;
    }

    @Override
    public void run() {
        try {
            while (true){
                Socket s = null;
                try {
                    s = ss.accept();
                    for (Thread thread : threads) {
                        if (!thread.isAlive()) {
                            threads.remove(thread);
                        }
                    }

                    Thread t = new Thread(new ReciveMessageHandler(s, rsa));
                    threads.add(t);
                    t.start();
                }
                catch (IOException ignored){}
            }
        }catch (Exception ignored){}

    }
}
