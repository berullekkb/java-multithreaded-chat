package chat.client;

import java.io.PrintWriter;
import java.util.Scanner;

public class MessageSender extends Thread {

    private final PrintWriter sortie;
    private final Scanner clavier;


    public MessageSender(
            PrintWriter sortie,
            Scanner clavier) {

        this.sortie = sortie;
        this.clavier = clavier;
    }


    @Override
    public void run() {

        try {

            while (true) {

                String message =
                        clavier.nextLine();


                sortie.println(message);


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