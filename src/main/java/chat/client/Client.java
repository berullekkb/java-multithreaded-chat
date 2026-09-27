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

        try {

            Socket socket =
                    new Socket(
                            HOST,
                            PORT
                    );


            System.out.println(
                    "Connexion au serveur réussie."
            );


            /*
             * Création des flux AVANT
             * de lancer les threads.
             */
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


            Scanner clavier =
                    new Scanner(System.in);


            /*
             * Gestion du pseudonyme.
             */
            choisirPseudo(
                    entree,
                    sortie,
                    clavier
            );


            /*
             * Une fois connecté,
             * on lance les deux threads.
             */
            ReceptionThread reception =
                    new ReceptionThread(
                            socket,
                            entree
                    );


            EnvoiThread envoi =
                    new EnvoiThread(
                            socket,
                            sortie,
                            clavier
                    );


            reception.start();

            envoi.start();


            reception.join();

            envoi.join();


        } catch (IOException e) {

            System.out.println(
                    "Connexion au serveur impossible."
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


            if (
                    demande == null
            ) {

                throw new IOException(
                        "Serveur déconnecté."
                );
            }


            if (
                    demande.equals("PSEUDO")
            ) {

                System.out.print(
                        "Entrez votre pseudo : "
                );


                String pseudo =
                        clavier.nextLine();


                sortie.println(pseudo);
            }


            String reponse =
                    entree.readLine();


            if (
                    reponse.equals("OK")
            ) {

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