import java.util.logging.Logger;

/**
 * La classe Proposer est utilisée pour proposer de nouveaux sujets au serveur central.
 * Elle utilise un objet ServerProxy pour notifier le serveur du nouveau sujet.
 *
 * <p>
 * Les communications avec le serveur sont réalisées en utilisant des sockets TCP pour assurer
 * la fiabilité des transmissions.
 * </p>
 */
public class Proposer {
    private static final Logger logger = Logger.getLogger(Proposer.class.getName());
    private final String topic;
    private final ServerProxy serverProxy;

    /**
     * Constructeur de la classe Proposer.
     *
     * @param topic Le nouveau sujet proposé.
     * @param serverIp L'adresse IP du serveur.
     * @param serverPort Le port du serveur.
     */
    public Proposer(String topic, String serverIp, int serverPort) {
        this.topic = topic;
        this.serverProxy = new ServerProxy(serverIp, serverPort);
        propose();
    }

    /**
     * Propose un nouveau sujet au serveur.
     */
    public void propose() {
        serverProxy.notifyNewTopic(topic);
        logger.info("Proposer introduced a new topic: " + topic);
    }

    /**
     * Méthode principale pour exécuter Proposer. Initialise un Proposer avec un sujet
     * et les informations de connexion au serveur à partir des arguments de la ligne de commande.
     *
     * @param args Arguments de la ligne de commande au format :
     *             --topic=<topic> --serverIp=<serverIp> --serverPort=<serverPort>
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Utilisation : java Proposer --topic=<topic> --serverIp=<serverIp> --serverPort=<serverPort>");
            return;
        }

        String topic = args[0].split("=")[1];
        String serverIp = args[1].split("=")[1];
        int serverPort = Integer.parseInt(args[2].split("=")[1]);

        new Proposer(topic, serverIp, serverPort);
    }
}