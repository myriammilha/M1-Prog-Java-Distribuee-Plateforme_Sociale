````md
# Distributed Social Network – Java TCP (Master 1 - Programmation Java Distribuée)

## Description
Ce projet simule une **plateforme sociale distribuée** en Java.  
Chaque utilisateur est un processus indépendant qui communique avec les autres via **TCP (sockets)**.
Les opinions évoluent au fil des interactions et la polarisation peut être mesurée.

## Architecture
- **Server** : serveur central pour l’enregistrement des utilisateurs et la diffusion des sujets
- **RegistrationHandler** : traite les requêtes reçues par le serveur (register, get info, proposer)
- **ServerProxy** : client côté utilisateur pour parler au serveur
- **UserInfo** : objet simple contenant l’IP et le port d’un utilisateur
- **User** : agent social de base (envoi/réception de messages)
- **MessageHandler** : traite les messages entrants d’un utilisateur
- **Influencer** : diffuse des opinions à plusieurs utilisateurs
- **CriticalThinker** : filtre les opinions reçues
- **Proposer** : propose de nouveaux sujets
- **ConsensusFinder** : tente de rapprocher les opinions
- **Polarimeter** : mesure la polarisation globale


## Technologies
- Java
- TCP (`Socket`, `ServerSocket`)
- Threads
- Java Logging

## Compilation
Depuis le dossier `src` :
```bash
javac *.java
````

## Exécution

### Lancer le serveur

```bash
java Server --port=12345
```

### Lancer des utilisateurs

```bash
java User --id=user1 --serverIp=127.0.0.1 --serverPort=12345 --port=5001
java User --id=user2 --serverIp=127.0.0.1 --serverPort=12345 --port=5002
```

## Fonctionnement

* Les utilisateurs s’enregistrent auprès du serveur central
* Chaque utilisateur écoute sur un port TCP
* Les messages échangés modifient les opinions selon un modèle d’influence
* Des agents spécialisés permettent d’étudier consensus et polarisation

## Remarque

TCP est utilisé comme **mécanisme de communication fiable**, la logique sociale est implémentée au niveau applicatif.

```