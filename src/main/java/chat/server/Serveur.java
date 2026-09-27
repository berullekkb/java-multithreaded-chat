package chat.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Serveur {

    private static final int PORT = 5000;

    private static final CopyOnWriteArrayList<ClientHandler> clients =
            new CopyOnWriteArrayList<>();

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

                Socket socketClient = serveur.accept();

                System.out.println(
                        "\nNouvelle connexion : "
                        + socketClient.getInetAddress()
                );

                ClientHandler client =
                        new ClientHandler(socketClient);

                /*
                 * IMPORTANT :
                 *
                 * On ne met PAS encore le client
                 * dans la liste.
                 *
                 * Il doit d'abord choisir un pseudo valide.
                 */
                client.start();
            }

        } catch (IOException e) {

            System.out.println(
                    "Erreur du serveur : "
                    + e.getMessage()
            );
        }
    }


    public static void ajouterClient(ClientHandler client) {

        clients.add(client);
    }


    public static void supprimerClient(ClientHandler client) {

        clients.remove(client);
    }


    public static void broadcast(String message) {

        for (ClientHandler client : clients) {

            client.envoyerMessage(message);
        }
    }


    public static boolean ajouterPseudo(String pseudo) {

        return pseudos.add(pseudo);
    }


    public static void supprimerPseudo(String pseudo) {

        if (pseudo != null) {

            pseudos.remove(pseudo);
        }
    }
}