package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Serveur {

    private static final int PORT = 5000;

    // Liste de tous les clients actuellement connectés
    private static final CopyOnWriteArrayList<ClientHandler> clients =
            new CopyOnWriteArrayList<>();

    // Ensemble des pseudonymes utilisés
    private static final Set<String> pseudos =
            ConcurrentHashMap.newKeySet();


    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("       SERVEUR DE CHAT JAVA");
        System.out.println("=================================");

        try (ServerSocket serveur = new ServerSocket(PORT)) {

            System.out.println("Serveur démarré sur le port " + PORT);
            System.out.println("En attente de clients...");

            while (true) {

                /*
                 * IMPORTANT :
                 *
                 * accept() est bloquant.
                 *
                 * Le serveur reste ici tant qu'un client
                 * ne demande pas une connexion.
                 */
                Socket socketClient = serveur.accept();

                System.out.println(
                        "\nNouvelle connexion : "
                        + socketClient.getInetAddress()
                );


                /*
                 * Création d'un thread spécialement
                 * pour ce client.
                 */
                ClientHandler client =
                        new ClientHandler(socketClient);

                clients.add(client);

                client.start();
            }

        } catch (IOException e) {

            System.out.println(
                    "Erreur du serveur : "
                    + e.getMessage()
            );
        }
    }


    /*
     * Diffuse un message à tous les clients connectés.
     */
    public static void broadcast(String message) {

        for (ClientHandler client : clients) {

            client.envoyerMessage(message);
        }
    }


    /*
     * Essaie d'enregistrer un pseudonyme.
     *
     * add() retourne false si le pseudo existe déjà.
     */
    public static boolean ajouterPseudo(String pseudo) {

        return pseudos.add(pseudo);
    }


    /*
     * Supprime un pseudonyme lorsqu'un client quitte.
     */
    public static void supprimerPseudo(String pseudo) {

        if (pseudo != null) {

            pseudos.remove(pseudo);
        }
    }


    /*
     * Supprime un client de la liste.
     */
    public static void supprimerClient(
            ClientHandler client) {

        clients.remove(client);
    }
}