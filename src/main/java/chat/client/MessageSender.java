package chat.client;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MessageSender extends Thread {

    private Socket socket;

    private PrintWriter sortie;

    private Scanner clavier;


    public MessageSender(
            Socket socket,
            PrintWriter sortie,
            Scanner clavier) {

        this.socket = socket;

        this.sortie = sortie;

        this.clavier = clavier;
    }


    @Override
    public void run() {

        try {

            while (true) {

                /*
                 * Attend que l'utilisateur
                 * saisisse quelque chose.
                 */
                String message =
                        clavier.nextLine();


                /*
                 * Envoi au serveur.
                 */
                sortie.println(message);


                /*
                 * Si l'utilisateur écrit exit,
                 * on arrête ce thread.
                 */
                if (
                        message.equalsIgnoreCase(
                                "exit"
                        )
                ) {

                    break;
                }
            }


        } catch (Exception e) {

            System.out.println(
                    "Fin de l'envoi des messages."
            );
        }
    }
}