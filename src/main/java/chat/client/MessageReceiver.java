package chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

public class MessageReceiver extends Thread {

    private final Socket socket;
    private final BufferedReader entree;


    public MessageReceiver(
            Socket socket,
            BufferedReader entree) {

        this.socket = socket;
        this.entree = entree;
    }


    @Override
    public void run() {

        try {

            String message;


            while (
                    (message = entree.readLine())
                    != null
            ) {

                System.out.println(message);
            }


            System.out.println(
                    "Le serveur s'est déconnecté."
            );


        } catch (IOException e) {

            if (!socket.isClosed()) {

                System.out.println(
                        "Connexion avec le serveur perdue."
                );
            }
        }
    }
}