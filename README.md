🕹️ Morpion Multijoueur – Java (Swing + Socket)
Ce projet est un jeu de morpion multijoueur codé en Java, jouable à deux sur le même réseau local. Il utilise :

des sockets TCP pour la communication client-serveur,

une interface graphique moderne en Swing,

une architecture client / serveur / commun bien séparée.

📁 Structure du projet

morpion-multijoueur/
├── client/                → Code de l’interface utilisateur et du client réseau
│   ├── AccueilUI.java     → Boîte de dialogue de connexion (pseudo, symbole)
│   ├── ClientSwingUI.java → Interface du plateau graphique
│   └── TicTacToeClient.java → Logique du client et communication avec le serveur
├── server/                → Code du serveur
│   ├── PlayerHandler.java → Thread de gestion d’un joueur
│   └── TicTacToeServer.java → Lancement du serveur et gestion des parties
├── common/                → Classes partagées entre client et serveur
│   ├── Board.java         → Représentation du plateau
│   ├── Move.java          → Représentation d’un coup
│   └── Message.java       → Génération des messages réseau

🚀 Lancer le projet

1. Compilation
Assure-toi d’être dans le dossier morpion-multijoueur, puis :

javac common/*.java server/*.java client/*.java

2. Lancer le serveur
Dans un premier terminal :

java server.TicTacToeServer

📡 Le serveur attend deux joueurs sur le port 5001.

3. Lancer les clients
Dans un second terminal :

java client.TicTacToeClient

Fais-le deux fois (ou sur deux machines) pour connecter les deux joueurs.

Fonctionnalités
Interface graphique moderne (Java Swing)

Jeu en temps réel à 2 joueurs

Choix du pseudo et du symbole (X ou O)

Réseau local via socket TCP

Gestion des tours, victoire, défaite et égalité

Fermeture automatique des clients en fin de partie

Dépendances
Java 11 ou supérieur

Aucun framework externe requis (fonctionne avec javac / java natif)

Améliorations possibles
Système de salle d’attente / matchmaking

Statistiques de victoire / défaite

IA locale pour jouer en solo

Version web avec JavaFX ou HTML5/Socket.IO

Auteur
Développé par Nowak Patrick, Morvan Thibaud dans le cadre d’un projet Java.

