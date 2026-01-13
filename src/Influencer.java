import java.util.Random;
import java.util.List;
import java.util.Arrays;
import java.util.Scanner;

/**
 * La classe Influencer est une extension de la classe User, qui permet à l'utilisateur
 * d'envoyer des messages à plusieurs destinataires sur un sujet donné. Cette classe
 * est conçue pour simuler le comportement d'un influenceur qui diffuse des messages
 * à un large public.
 *
 * <p>
 * La classe utilise java.util.Random pour générer des valeurs aléatoires pour les
 * opinions et les influences des utilisateurs lors de l'initialisation. La méthode
 * principale permet de créer une instance d'Influencer avec les arguments de la ligne
 * de commande. Les messages sont envoyés en utilisant le protocole TCP pour assurer
 * la fiabilité de la transmission.
 * </p>
 */
public class Influencer extends User {

    /**
     * Constructeur de la classe Influencer.
     *
     * @param id L'identifiant de l'utilisateur.
     * @param opinion L'opinion initiale de l'utilisateur.
     * @param influence L'influence initiale de l'utilisateur.
     * @param serverIp L'adresse IP du serveur.
     * @param serverPort Le port du serveur.
     * @param port Le port de l'utilisateur.
     */
    public Influencer(String id, double opinion, double influence, String serverIp, int serverPort, int port) {
        super(id, opinion, influence, serverIp, serverPort, port);
    }

    /**
     * Diffuse un message à une liste de destinataires sur un sujet donné en utilisant le protocole TCP.
     *
     * @param recipientIds La liste des identifiants des destinataires.
     * @param topic Le sujet du message.
     */
    public void broadcastMessage(List<String> recipientIds, String topic) {
        for (String recipientId : recipientIds) {
            UserInfo recipientInfo = serverProxy.getUserInfo(recipientId);
            if (recipientInfo != null) {
                sendMessage(recipientId, topic);
            } else {
                logger.warning("User info for " + recipientId + " not found. Skipping message.");
            }
        }
    }

    /**
     * Méthode principale pour exécuter Influencer. Initialise l'utilisateur avec
     * une opinion et une influence aléatoires et crée une instance d'Influencer
     * avec les arguments de la ligne de commande.
     *
     * @param args Arguments de la ligne de commande au format :
     *             --id=<id> --serverIp=<serverIp> --serverPort=<serverPort> --port=<port>
     */
    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Utilisation : java Influencer --id=<id> --serverIp=<serverIp> --serverPort=<serverPort> --port=<port>");
            return;
        }

        String id = args[0].split("=")[1];
        String serverIp = args[1].split("=")[1];
        int serverPort = Integer.parseInt(args[2].split("=")[1]);
        int port = Integer.parseInt(args[3].split("=")[1]);

        Random random = new Random();
        double opinion = random.nextDouble();
        double influence = random.nextDouble();

        Influencer influencer = new Influencer(id, opinion, influence, serverIp, serverPort, port);

        // Utiliser un Scanner pour lire les commandes interactives
        Scanner scanner = new Scanner(System.in);
        System.out.println("Entrez les commandes. Pour diffuser un message, utilisez la commande : broadcast <topic>");

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine();
            if (command.startsWith("broadcast ")) {
                String topic = command.substring(10);
                List<String> recipientIds = Arrays.asList("user1", "user2"); // Destinataires par défaut
                influencer.broadcastMessage(recipientIds, topic);
            } else {
                System.out.println("Commande inconnue : " + command);
            }
        }
    }
}
