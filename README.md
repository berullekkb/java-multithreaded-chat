# Java Multithreaded Chat

Application de chat multi-client développée en Java à partir de sockets TCP et de threads.

L'application permet à plusieurs utilisateurs de se connecter simultanément à un serveur, de choisir un pseudonyme unique, d'échanger des messages en temps réel et de quitter proprement la conversation.

Le projet met principalement en pratique les notions suivantes :

- programmation réseau avec `ServerSocket` et `Socket`
- communication TCP
- entrées / sorties avec `BufferedReader` et `PrintWriter`
- programmation multi-thread
- gestion concurrente de plusieurs clients
- gestion des déconnexions
- partage sécurisé de données entre threads

---

## Fonctionnalités

- Connexion de plusieurs clients simultanément
- Attribution d'un pseudonyme unique
- Diffusion des messages à tous les utilisateurs connectés
- Notification lorsqu'un utilisateur rejoint la conversation
- Notification lorsqu'un utilisateur quitte la conversation
- Déconnexion volontaire avec la commande `exit`
- Gestion d'une déconnexion imprévue d'un client
- Un thread serveur dédié à chaque client
- Deux threads côté client :
  - un thread pour envoyer les messages
  - un thread pour recevoir les messages
- Gestion thread-safe de la liste des clients et des pseudonymes

---

## Architecture

L'application repose sur une architecture client / serveur.

### Côté serveur

Le serveur principal utilise un `ServerSocket` pour écouter les nouvelles connexions.

Lorsqu'un client se connecte :

1. le serveur accepte la connexion ;
2. un objet `ClientHandler` est créé ;
3. un thread dédié au client est lancé ;
4. le client choisit un pseudonyme ;
5. le serveur vérifie que ce pseudonyme est disponible ;
6. le client rejoint la conversation ;
7. les messages reçus sont ensuite diffusés aux clients connectés.

Architecture simplifiée :

```text
                         Serveur
                            |
                      ServerSocket
                            |
                         accept()
                            |
          +-----------------+-----------------+
          |                 |                 |
     ClientHandler     ClientHandler     ClientHandler
          |                 |                 |
       Client 1          Client 2          Client 3
```

Chaque `ClientHandler` fonctionne dans son propre thread.

Cela permet au serveur de continuer à accepter de nouveaux clients pendant que les utilisateurs déjà connectés échangent des messages.

### Côté client

Chaque client utilise deux threads :

```text
                         Client
                           |
                         Socket
                           |
               +-----------+-----------+
               |                       |
        MessageSender            MessageReceiver
               |                       |
        saisie clavier             serveur
               |                       |
             envoi                  réception
```

- `MessageSender` lit les messages saisis dans la console et les transmet au serveur.
- `MessageReceiver` écoute continuellement les messages envoyés par le serveur et les affiche dans la console.

Cette séparation permet d'envoyer et de recevoir des messages simultanément.

---

## Structure du projet

```text
java-multithreaded-chat/
│
├── src/
│   └── main/
│       └── java/
│           └── chat/
│               │
│               ├── server/
│               │   ├── Serveur.java
│               │   └── ClientHandler.java
│               │
│               └── client/
│                   ├── Client.java
│                   ├── MessageSender.java
│                   └── MessageReceiver.java
│
├── .gitignore
└── README.md
```

### Rôle des classes

#### `Serveur.java`

Classe principale côté serveur.

Elle :

- ouvre le port du serveur ;
- attend les nouvelles connexions ;
- conserve les clients connectés ;
- conserve les pseudonymes utilisés ;
- diffuse les messages à l'ensemble des clients.

#### `ClientHandler.java`

Thread serveur associé à un client.

Il :

- récupère le pseudonyme du client ;
- vérifie son unicité ;
- écoute les messages envoyés par le client ;
- demande leur diffusion au serveur ;
- détecte la déconnexion du client ;
- libère les ressources associées.

#### `Client.java`

Point d'entrée côté client.

Il :

- établit la connexion avec le serveur ;
- initialise les flux de communication ;
- gère le choix du pseudonyme ;
- lance les threads d'envoi et de réception.

#### `MessageSender.java`

Thread chargé de lire les messages saisis dans la console et de les envoyer au serveur.

#### `MessageReceiver.java`

Thread chargé d'écouter les messages reçus depuis le serveur et de les afficher dans la console.

---

## Prérequis

Pour compiler et exécuter le projet :

- Java JDK installé
- Java 8 minimum
- Java 17 ou version supérieure recommandé
- un terminal PowerShell, CMD, Bash ou équivalent

Vérifier l'installation de Java :

```bash
java -version
```

Vérifier le compilateur Java :

```bash
javac -version
```

---

## Compilation

Depuis la racine du projet :

```text
java-multithreaded-chat/
```

Créer un dossier `bin` :

### Windows PowerShell

```powershell
mkdir bin
```

Compiler ensuite les fichiers :

```powershell
javac -d bin src\main\java\chat\server\*.java src\main\java\chat\client\*.java
```

Après compilation, la structure générée sera similaire à :

```text
bin/
└── chat/
    ├── server/
    │   ├── Serveur.class
    │   └── ClientHandler.class
    │
    └── client/
        ├── Client.class
        ├── MessageSender.class
        └── MessageReceiver.class
```

Le dossier `bin` n'est pas versionné dans Git car il contient uniquement des fichiers générés par compilation.

---

## Exécution

### 1. Démarrer le serveur

Ouvrir un premier terminal à la racine du projet :

```powershell
java -cp bin chat.server.Serveur
```

Résultat attendu :

```text
=================================
       SERVEUR DE CHAT JAVA
=================================
Serveur démarré sur le port 5000
En attente de clients...
```

---

### 2. Démarrer un premier client

Ouvrir un deuxième terminal :

```powershell
java -cp bin chat.client.Client
```

Le client doit obtenir :

```text
Connexion au serveur réussie.
Entrez votre pseudo :
```

Exemple :

```text
Jack
```

Puis :

```text
Pseudo accepté.
Vous pouvez discuter.
Tapez exit pour quitter.
```

---

### 3. Démarrer plusieurs clients

Ouvrir autant de terminaux que nécessaire :

```powershell
java -cp bin chat.client.Client
```

Exemple :

```text
Client 1 : Jack
Client 2 : Sarah
Client 3 : Youcef
```

Lorsqu'un nouveau client rejoint le chat :

```text
Sarah a rejoint la conversation.
```

---

## Exemple de conversation

Client Jack :

```text
Bonjour tout le monde
```

Les clients reçoivent :

```text
Jack a dit : Bonjour tout le monde
```

Sarah peut ensuite répondre :

```text
Salut Jack
```

Les clients reçoivent :

```text
Sarah a dit : Salut Jack
```

---

## Quitter la conversation

Pour quitter proprement le chat :

```text
exit
```

Le serveur retire alors le client de la liste des utilisateurs connectés.

Les autres utilisateurs reçoivent :

```text
Jack a quitté la conversation.
```

---

## Gestion des pseudonymes

Les pseudonymes doivent être uniques.

Le serveur conserve les pseudonymes actifs dans une structure thread-safe :

```java
ConcurrentHashMap.newKeySet()
```

Si un utilisateur essaie de choisir un pseudonyme déjà utilisé :

```text
Ce pseudonyme est déjà utilisé.
```

Le serveur lui demande alors d'en choisir un autre.

La vérification est réalisée côté serveur afin d'éviter que plusieurs clients puissent utiliser le même pseudonyme simultanément.

---

## Gestion de la concurrence

Le serveur manipule plusieurs clients en parallèle.

Les structures principales sont :

```java
CopyOnWriteArrayList<ClientHandler>
```

pour les clients connectés, et :

```java
ConcurrentHashMap.newKeySet()
```

pour les pseudonymes.

Ces structures permettent plusieurs accès concurrents sans utiliser directement une `ArrayList` ou une `HashSet` classique non thread-safe.

Chaque client possède également son propre `ClientHandler`.

Ainsi :

```text
Client Jack   -> Thread Jack
Client Sarah  -> Thread Sarah
Client Youcef -> Thread Youcef
```

Un client qui attend un message ne bloque donc pas les autres clients.

---

## Gestion des déconnexions

Deux situations sont prises en compte.

### Déconnexion normale

Le client saisit :

```text
exit
```

Le serveur :

- arrête le traitement du client ;
- retire son pseudonyme ;
- retire son `ClientHandler` ;
- ferme les flux ;
- ferme le socket ;
- informe les autres clients.

### Déconnexion inattendue

Un utilisateur peut également :

- fermer brutalement son terminal ;
- perdre sa connexion réseau ;
- arrêter son application ;
- rencontrer une erreur.

Dans ce cas, le serveur détecte la fin du flux ou une `IOException`.

Le nettoyage des ressources est réalisé dans un bloc `finally`, afin que la déconnexion soit traitée même lorsqu'une erreur survient.

---

## Protocole de communication

L'application utilise actuellement un protocole texte simple basé sur des lignes.

Lors de la connexion :

```text
Serveur -> PSEUDO
Client  -> Jack
Serveur -> OK
```

Si le pseudonyme existe déjà :

```text
Serveur -> ERREUR:Ce pseudonyme est déjà utilisé.
```

Pendant la conversation :

```text
Client  -> Bonjour
Serveur -> Jack a dit : Bonjour
```

Pour quitter :

```text
Client -> exit
```

Ce protocole reste volontairement simple afin de concentrer le projet sur les sockets, les flux et le multithreading.

---

## Choix techniques

### TCP

L'application utilise TCP afin de bénéficier :

- d'une connexion établie entre client et serveur ;
- d'une transmission fiable ;
- du respect de l'ordre des données transmises.

### Communication bloquante

Les lectures utilisent des opérations bloquantes comme :

```java
readLine()
```

Un thread peut donc attendre l'arrivée d'un message.

Cette attente ne bloque cependant pas l'ensemble du serveur puisque chaque client possède son propre thread.

### Un thread par client côté serveur

Le serveur utilise un modèle simple :

```text
1 client = 1 ClientHandler = 1 thread
```

Ce modèle facilite la compréhension de la programmation réseau et du multithreading.

Pour une application devant gérer un très grand nombre de connexions simultanées, une architecture basée sur Java NIO pourrait être envisagée.

---

## Limites actuelles

Cette version reste volontairement simple.

Elle ne possède pas encore :

- d'interface graphique ;
- de chiffrement TLS ;
- de système d'authentification ;
- de stockage de l'historique des conversations ;
- de messages privés ;
- de salons de discussion ;
- de base de données ;
- de protocole JSON ;
- de mécanisme de reconnexion automatique.

---

## Améliorations possibles

Plusieurs évolutions pourraient être ajoutées :

- interface graphique avec JavaFX ou Swing ;
- historique des messages ;
- messages privés ;
- salons de discussion ;
- authentification des utilisateurs ;
- chiffrement des communications ;
- utilisation de JSON pour structurer les messages ;
- ajout de logs avec un framework dédié ;
- tests unitaires et tests d'intégration ;
- gestion d'un pool de threads avec `ExecutorService` ;
- migration vers Java NIO pour gérer davantage de connexions ;
- configuration dynamique du port et de l'adresse du serveur.

---

## Objectif du projet

Ce projet a été développé pour approfondir la compréhension de la programmation réseau en Java.

Il permet notamment de mettre en pratique les relations entre :

```text
ServerSocket / Socket
        ↓
Input / Output
        ↓
Threads
        ↓
Clients concurrents
        ↓
Broadcast
        ↓
Gestion des erreurs et déconnexions
```

Il peut également servir de base pour construire une application de communication plus complète.

---

## Contribution

Les contributions sont les bienvenues.

Pour proposer une modification :

1. Forker le dépôt
2. Créer une branche :

```bash
git checkout -b feature/nom-de-la-fonctionnalite
```

3. Réaliser les modifications
4. Créer un commit
5. Push la branche
6. Ouvrir une Pull Request

Les bugs ou propositions d'amélioration peuvent également être signalés via les Issues GitHub.

---

## Licence

Ce projet est destiné à un usage pédagogique et peut être enrichi ou adapté.

Pour une réutilisation publique ou une redistribution, il est recommandé d'ajouter un fichier `LICENSE` au dépôt.

Une licence MIT peut notamment être utilisée pour autoriser la réutilisation, la modification et la redistribution du projet.

---

## Auteur

**Berulle KAMDEM KAMDEM**

Email : [berullekkb@gmail.com](mailto:berullekkb@gmail.com)

Projet Java consacré à la programmation réseau, aux sockets TCP et au multithreading.

Pour toute question, suggestion ou proposition d'amélioration, vous pouvez ouvrir une Issue sur le dépôt ou contacter l'auteur par email.