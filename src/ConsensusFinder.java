import java.util.logging.Logger;
import java.util.Random;

/**
 * La classe ConsensusFinder est conçue pour simuler le processus de recherche de consensus entre deux utilisateurs sur un sujet donné. 
 * Cette classe utilise des valeurs aléatoires pour déterminer si un consensus est atteint et met à jour les opinions des utilisateurs en conséquence.
 *
 * <p>
 * Cette classe utilise la classe java.util.Random pour simuler l'acceptation aléatoire, et java.util.logging.Logger pour enregistrer le processus de consensus. 
 * La méthode main est utilisée pour initialiser les utilisateurs et démarrer le processus de recherche de consensus avec les arguments de la ligne de commande fournis. 
 * Les messages entre les utilisateurs sont envoyés et reçus en utilisant le protocole TCP pour assurer la fiabilité de la transmission.
 * </p>
 */
public class ConsensusFinder {
    private static final Logger logger = Logger.getLogger(ConsensusFinder.class.getName());

    /**
     * Trouve un consensus entre deux utilisateurs sur un sujet donné. Si un consensus
     * est atteint, met à jour les opinions des deux utilisateurs à la moyenne de leurs
     * opinions actuelles.
     *
     * @param user1 Le premier utilisateur participant au consensus.
     * @param user2 Le deuxième utilisateur participant au consensus.
     * @param topic Le sujet sur lequel le consensus doit être trouvé.
     */
    public void findConsensus(User user1, User user2, String topic) {
        Random random = new Random();
        if (random.nextBoolean() && random.nextBoolean()) { // Simulation de l'acceptation
            double newOpinion = (user1.getOpinion() + user2.getOpinion()) / 2;
            user1.updateOpinion(newOpinion, 1.0);
            user2.updateOpinion(newOpinion, 1.0);
            logger.info("Consensus trouvé entre " + user1.getId() + " et " + user2.getId() + " sur le sujet : " + topic);
        } else {
            logger.info("Consensus non atteint entre " + user1.getId() + " et " + user2.getId() + " sur le sujet : " + topic);
        }
    }

    /**
     * Méthode principale pour exécuter le ConsensusFinder. Initialise les utilisateurs
     * avec des opinions et des influences aléatoires et tente de trouver un consensus
     * sur un sujet spécifié.
     * Les messages entre les utilisateurs sont envoyés et reçus en utilisant le protocole TCP.
     *
     * @param args Arguments de la ligne de commande au format :
     *             --user1=<user1> --user2=<user2> --topic=<topic> --serverIp=<serverIp> --serverPort=<serverPort> --port=<port>
     */
    public static void main(String[] args) {
        if (args.length < 6) {
            System.out.println("Utilisation : java ConsensusFinder --user1=<user1> --user2=<user2> --topic=<topic> --serverIp=<serverIp> --serverPort=<serverPort> --port=<port>");
            return;
        }

        String user1Id = args[0].split("=")[1];
        String user2Id = args[1].split("=")[1];
        String topic = args[2].split("=")[1];
        String serverIp = args[3].split("=")[1];
        int serverPort = Integer.parseInt(args[4].split("=")[1]);
        int port = Integer.parseInt(args[5].split("=")[1]);

        Random random = new Random();
        double opinion1 = random.nextDouble();
        double influence1 = random.nextDouble();
        double opinion2 = random.nextDouble();
        double influence2 = random.nextDouble();

        User user1 = new User(user1Id, opinion1, influence1, serverIp, serverPort, port);
        User user2 = new User(user2Id, opinion2, influence2, serverIp, serverPort, port + 1); // Assurer des ports différents

        ConsensusFinder cf = new ConsensusFinder();
        cf.findConsensus(user1, user2, topic);
    }
}