import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.Random;


/**
 * La classe User représente un utilisateur dans le système, capable d'envoyer et de recevoir des messages,
 * ainsi que de mettre à jour ses opinions en fonction des messages reçus. Les utilisateurs sont enregistrés
 * auprès d'un serveur central et communiquent entre eux en utilisant des sockets TCP.
 *
 * <p>
 * Les communications entre les utilisateurs et avec le serveur sont réalisées en utilisant des sockets TCP pour
 * assurer la fiabilité des transmissions.
 * </p>
 */
public class User {
    protected static final Logger logger = Logger.getLogger(User.class.getName());
    private final String id;
    private double opinion;
    private final double influence;
    protected final ServerProxy serverProxy;
    private final int port;

    /**
     * Constructeur de la classe User.
     *
     * @param id L'identifiant de l'utilisateur.
     * @param opinion L'opinion initiale de l'utilisateur.
     * @param influence L'influence initiale de l'utilisateur.
     * @param serverIp L'adresse IP du serveur.
     * @param serverPort Le port du serveur.
     * @param port Le port sur lequel l'utilisateur écoute les connexions entrantes.
     */
    public User(String id, double opinion, double influence, String serverIp, int serverPort, int port) {
        this.id = id;
        this.opinion = opinion;
        this.influence = influence;
        this.serverProxy = new ServerProxy(serverIp, serverPort);
        this.port = port;
        registerWithServer();
        startServer();
    }

    public String getId() {
        return id;
    }

    public double getOpinion() {
        return opinion;
    }

    public synchronized void updateOpinion(double newOpinion, double influence) {
        this.opinion = this.opinion + (newOpinion - this.opinion) * influence;
        logger.info("User " + id + " updated opinion to " + this.opinion);
    }

    public double getInfluence() {
        return influence;
    }

    private void registerWithServer() {
        serverProxy.registerUser(id, port); // Enregistrement auprès du serveur avec le port d'écoute
    }

    private void startServer() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                logger.info("User " + id + " started server on port " + port);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new MessageHandler(clientSocket, this)).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Envoie un message à un autre utilisateur sur un sujet donné.
     *
     * @param recipientId L'identifiant du destinataire.
     * @param topic Le sujet du message.
     */
    public void sendMessage(String recipientId, String topic) {
        UserInfo recipientInfo = serverProxy.getUserInfo(recipientId);
        if (recipientInfo != null) {
            try (Socket socket = new Socket(recipientInfo.getIpAddress(), recipientInfo.getPort());
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                out.println(topic);
                out.println(opinion);
                logger.info("User " + id + " sent message to " + recipientId + " on topic " + topic);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reçoit un message contenant une opinion sur un sujet et met à jour l'opinion de l'utilisateur en conséquence.
     *
     * @param topic Le sujet du message.
     * @param opinion L'opinion contenue dans le message.
     */
    public void receiveMessage(String topic, double opinion) {
        updateOpinion(opinion, this.influence);
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.out.println("Utilisation : java User --id=<id> --serverIp=<serverIp> --serverPort=<serverPort> --port=<port>");
            return;
        }

        String id = args[0].split("=")[1];
        String serverIp = args[1].split("=")[1];
        int serverPort = Integer.parseInt(args[2].split("=")[1]);
        int port = Integer.parseInt(args[3].split("=")[1]);

        Random random = new Random();
        double opinion = random.nextDouble();
        double influence = random.nextDouble();

        new User(id, opinion, influence, serverIp, serverPort, port);
    }
}

