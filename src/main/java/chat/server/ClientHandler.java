package chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private Socket socket;

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

            /*
             * Flux permettant de recevoir
             * les messages venant du client.
             */
            entree =
                    new BufferedReader(
                            new InputStreamReader(
                                    socket.getInputStream()
                            )
                    );


            /*
             * Flux permettant d'envoyer
             * des messages au client.
             */
            sortie =
                    new PrintWriter(
                            socket.getOutputStream(),
                            true
                    );


            /*
             * Première étape :
             * récupérer un pseudonyme unique.
             */
            gererPseudo();


            /*
             * Une fois le pseudo accepté,
             * on annonce l'arrivée.
             */
            System.out.println(
                    pseudo + " est connecté."
            );


            Serveur.broadcast(
                    pseudo
                    + " a rejoint la conversation."
            );


            String message;


            /*
             * IMPORTANT :
             *
             * readLine() est bloquant.
             *
             * Ce thread reste ici jusqu'à ce que
             * CE client envoie quelque chose.
             *
             * Mais les autres clients ont leurs
             * propres threads.
             */
            while (
                    (message = entree.readLine())
                    != null
            ) {

                System.out.println(
                        pseudo
                        + " -> "
                        + message
                );


                /*
                 * Si le client écrit exit,
                 * on arrête sa conversation.
                 */
                if (
                        message.equalsIgnoreCase(
                                "exit"
                        )
                ) {

                    break;
                }


                /*
                 * Diffusion du message à tous.
                 */
                Serveur.broadcast(
                        pseudo
                        + " a dit : "
                        + message
                );
            }


        } catch (IOException e) {

            /*
             * Exemple :
             * client fermé brutalement,
             * perte de connexion, etc.
             */
            System.out.println(
                    "Connexion perdue avec "
                    + (
                        pseudo != null
                        ? pseudo
                        : "un client"
                    )
            );

        } finally {

            /*
             * IMPORTANT :
             *
             * Le nettoyage doit se produire
             * même s'il y a une exception.
             */
            deconnecter();
        }
    }


    /*
     * Demande un pseudo jusqu'à obtenir
     * un pseudo valide et unique.
     */
    private void gererPseudo()
            throws IOException {

        while (true) {

            /*
             * Le serveur demande un pseudo.
             */
            sortie.println("PSEUDO");


            String proposition =
                    entree.readLine();


            /*
             * Si le client disparaît
             * avant même d'envoyer son pseudo.
             */
            if (proposition == null) {

                throw new IOException(
                        "Client déconnecté."
                );
            }


            proposition =
                    proposition.trim();


            /*
             * Pseudo vide interdit.
             */
            if (proposition.isEmpty()) {

                sortie.println(
                        "ERREUR:Le pseudo ne peut pas être vide."
                );

                continue;
            }


            /*
             * Le serveur est responsable
             * de vérifier l'unicité.
             */
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


    /*
     * Envoie un message à CE client.
     */
    public void envoyerMessage(
            String message) {

        if (sortie != null) {

            sortie.println(message);
        }
    }


    /*
     * Nettoyage du client.
     */
    private void deconnecter() {

        /*
         * Empêche une double déconnexion.
         */
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

            // Rien à faire
        }


        if (sortie != null) {

            sortie.close();
        }


        try {

            if (
                    socket != null
                    && !socket.isClosed()
            ) {

                socket.close();
            }

        } catch (IOException e) {

            // Rien à faire
        }
    }
}
