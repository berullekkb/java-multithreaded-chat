package chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private final Socket socket;

    private BufferedReader entree;
    private PrintWriter sortie;

    private String pseudo;

    private boolean deconnecte = false;


    public ClientHandler(Socket socket) {

        this.socket = socket;
    }


    @Override
    public void run() {

        try {

            entree =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );

            sortie =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );


            /*
             * Le client doit d'abord avoir
             * un pseudonyme valide.
             */
            gererPseudo();


            /*
             * Seulement maintenant,
             * il devient un client actif du chat.
             */
            Serveur.ajouterClient(this);


            System.out.println(
                    pseudo + " est connecté."
            );


            Serveur.broadcast(
                    pseudo
                    + " a rejoint la conversation."
            );


            String message;


            while (
                    (message = entree.readLine())
                    != null
            ) {

                System.out.println(
                        pseudo
                        + " -> "
                        + message
                );


                if (
                        message.equalsIgnoreCase(
                                "exit"
                        )
                ) {

                    break;
                }


                Serveur.broadcast(
                        pseudo
                        + " a dit : "
                        + message
                );
            }


        } catch (IOException e) {

            System.out.println(
                    "Connexion perdue avec "
                    + (
                        pseudo != null
                        ? pseudo
                        : "un client"
                    )
            );

        } finally {

            deconnecter();
        }
    }


    private void gererPseudo()
            throws IOException {

        while (true) {

            sortie.println("PSEUDO");


            String proposition =
                    entree.readLine();


            if (proposition == null) {

                throw new IOException(
                        "Client déconnecté."
                );
            }


            proposition =
                    proposition.trim();


            if (proposition.isEmpty()) {

                sortie.println(
                        "ERREUR:Le pseudo ne peut pas être vide."
                );

                continue;
            }


            if (
                    Serveur.ajouterPseudo(
                            proposition
                    )
            ) {

                pseudo = proposition;

                sortie.println("OK");

                break;

            } else {

                sortie.println(
                        "ERREUR:Ce pseudonyme est déjà utilisé."
                );
            }
        }
    }


    public void envoyerMessage(
            String message) {

        if (
                sortie != null
                && !sortie.checkError()
        ) {

            sortie.println(message);
        }
    }


    private synchronized void deconnecter() {

        if (deconnecte) {

            return;
        }

        deconnecte = true;


        Serveur.supprimerClient(this);


        if (pseudo != null) {

            Serveur.supprimerPseudo(pseudo);


            System.out.println(
                    pseudo
                    + " s'est déconnecté."
            );


            Serveur.broadcast(
                    pseudo
                    + " a quitté la conversation."
            );
        }


        try {

            if (entree != null) {

                entree.close();
            }

        } catch (IOException e) {

            System.out.println(
                    "Erreur fermeture flux d'entrée."
            );
        }


        if (sortie != null) {

            sortie.close();
        }


        try {

            if (!socket.isClosed()) {

                socket.close();
            }

        } catch (IOException e) {

            System.out.println(
                    "Erreur fermeture socket."
            );
        }
    }
}