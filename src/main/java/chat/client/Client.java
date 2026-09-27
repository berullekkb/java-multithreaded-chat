package chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static final String HOST =
            "localhost";

    private static final int PORT =
            5000;


    public static void main(String[] args) {

        try (
                Socket socket =
                        new Socket(HOST, PORT);

                Scanner clavier =
                        new Scanner(System.in)
        ) {

            System.out.println(
                    "Connexion au serveur réussie."
            );


            BufferedReader entree =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );


            PrintWriter sortie =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );


            /*
             * Le pseudo est traité AVANT
             * le lancement des threads.
             *
             * Ainsi un seul code lit le socket
             * pendant la phase de connexion.
             */
            choisirPseudo(
                    entree,
                    sortie,
                    clavier
            );


            MessageReceiver reception =
                    new MessageReceiver(
                            socket,
                            entree
                    );


            MessageSender envoi =
                    new MessageSender(
                            sortie,
                            clavier
                    );


            reception.start();
            envoi.start();


            /*
             * On attend d'abord que l'utilisateur
             * termine l'envoi.
             */
            envoi.join();


            /*
             * Après "exit", le serveur ferme
             * normalement le socket.
             *
             * Le thread de réception se terminera alors.
             */
            reception.join();


        } catch (IOException e) {

            System.out.println(
                    "Connexion au serveur impossible : "
                    + e.getMessage()
            );


        } catch (InterruptedException e) {

            Thread.currentThread()
                    .interrupt();
        }
    }


    private static void choisirPseudo(
            BufferedReader entree,
            PrintWriter sortie,
            Scanner clavier
    ) throws IOException {

        while (true) {

            String demande =
                    entree.readLine();


            if (demande == null) {

                throw new IOException(
                        "Serveur déconnecté."
                );
            }


            if (!demande.equals("PSEUDO")) {

                continue;
            }


            System.out.print(
                    "Entrez votre pseudo : "
            );


            String pseudo =
                    clavier.nextLine();


            sortie.println(pseudo);


            String reponse =
                    entree.readLine();


            if (reponse == null) {

                throw new IOException(
                        "Serveur déconnecté."
                );
            }


            if (reponse.equals("OK")) {

                System.out.println(
                        "Pseudo accepté."
                );

                System.out.println(
                        "Vous pouvez discuter."
                );

                System.out.println(
                        "Tapez exit pour quitter."
                );

                break;
            }


            if (
                    reponse.startsWith(
                            "ERREUR:"
                    )
            ) {

                System.out.println(
                        reponse.substring(7)
                );
            }
        }
    }
}