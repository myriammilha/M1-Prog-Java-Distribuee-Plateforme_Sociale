import java.util.Random;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;

/**
 * La classe CriticalThinker est une extension de la classe User, qui ajoute une vérification critique des opinions reçues avant de les accepter. 
 * Cette classe utilise un critère de validation pour décider si une opinion doit être acceptée ou rejetée.
 *
 * <p>
 * La classe utilise java.util.Random pour générer des valeurs aléatoires pour les opinions et les influences des utilisateurs lors de l'initialisation. 
 * La méthode principale permet de créer une instance de CriticalThinker avec les arguments de la ligne de commande. 
 * Les messages sont envoyés et reçus en utilisant le protocole TCP pour assurer la fiabilité de la transmission.
 * </p>
 */
public class CriticalThinker extends User {

    /**
     * Constructeur de la classe CriticalThinker.
     *
     * @param id L'identifiant de l'utilisateur.
     * @param opinion L'opinion initiale de l'utilisateur.
     * @param influence L'influence initiale de l'utilisateur.
     * @param serverIp L'adresse IP du serveur.
     * @param serverPort Le port du serveur.
     * @param port Le port de l'utilisateur.
     */
    public CriticalThinker(String id, double opinion, double influence, String serverIp, int serverPort, int port) {
        super(id, opinion, influence, serverIp, serverPort, port);
    }

    /**
     * Reçoit un message contenant une opinion sur un sujet. 
     * Si l'opinion est validée, l'utilisateur met à jour son opinion en conséquence. 
     * Sinon, le message est rejeté.
     *
     * @param topic Le sujet du message.
     * @param opinion L'opinion contenue dans le message.
     */
    @Override
    public void receiveMessage(String topic, double opinion) {
        if (validateOpinion(opinion)) {
            super.receiveMessage(topic, opinion);
        } else {
            logger.info("CriticalThinker " + getId() + " a rejeté le message sur le sujet " + topic);
        }
    }

    /**
     * Valide l'opinion reçue en utilisant un critère spécifique.
     *
     * @param opinion L'opinion à valider.
     * @return true si l'opinion est validée, false sinon.
     */
    private boolean validateOpinion(double opinion) {
        return ((int) (opinion * 100) % 7) == 0;
    }

    /**
     * Méthode principale pour exécuter CriticalThinker. 
     * Initialise l'utilisateur avec une opinion et une influence aléatoires et crée une instance de CriticalThinker avec les arguments de la ligne de commande.
     * Les messages sont envoyés et reçus en utilisant le protocole TCP.
     *
     * @param args Arguments de la ligne de commande au format :
     *             --id=<id> --serverIp=<serverIp> --serverPort=<serverPort> --port=<port>
     */
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Utilisation : java CriticalThinker --id=<id> --serverIp=<serverIp> --serverPort=<serverPort> --port=<port>");
            return;
        }

        String id = args[0].split("=")[1];
        String serverIp = args[1].split("=")[1];
        int serverPort = Integer.parseInt(args[2].split("=")[1]);
        int port = Integer.parseInt(args[3].split("=")[1]);

        Random random = new Random();
        double opinion = random.nextDouble();
        double influence = random.nextDouble();

        new CriticalThinker(id, opinion, influence, serverIp, serverPort, port);
    }
}