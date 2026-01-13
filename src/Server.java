import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * La classe Server gère l'enregistrement des utilisateurs et la notification des nouveaux sujets.
 * Elle maintient une liste des utilisateurs enregistrés avec leurs adresses IP et leurs ports.
 * Les communications avec les utilisateurs sont réalisées en utilisant des sockets TCP.
 */
public class Server {
    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final Map<String, UserInfo> users = new ConcurrentHashMap<>();

    /**
     * Enregistre un utilisateur avec son identifiant, son adresse IP et son port.
     * Cette méthode synchronisée assure qu'un seul thread peut enregistrer un utilisateur à la fois,
     * évitant ainsi les problèmes potentiels de concurrence lors de la mise à jour de la carte des utilisateurs (`users`).
     *
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param ipAddress L'adresse IP de l'utilisateur.
     * @param port Le port sur lequel l'utilisateur écoute.
     */
    public synchronized void registerUser(String userId, String ipAddress, int port) {
        users.put(userId, new UserInfo(ipAddress, port));
        logger.info("User " + userId + " registered with IP " + ipAddress + " and port " + port);
    }

    /**
     * Récupère les informations d'un utilisateur à partir de son identifiant.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @return Un objet UserInfo contenant l'adresse IP et le port de l'utilisateur, ou null si non trouvé.
     */
    public UserInfo getUserInfo(String userId) {
        return users.get(userId);
    }

    /**
     * Notifie tous les utilisateurs enregistrés d'un nouveau sujet.
     *
     * <p>
     * Cette méthode synchronisée garantit qu'un seul thread peut notifier les utilisateurs d'un nouveau sujet à la fois.
     * Cela évite les conditions de course où plusieurs notifications pourraient interférer les unes avec les autres.
     * </p>
     *
     * @param topic Le nouveau sujet proposé.
     */
    public synchronized void notifyNewTopic(String topic) {
        for (UserInfo userInfo : users.values()) {
            notifyUser(userInfo, topic);
        }
    }

    /**
     * Notifie un utilisateur spécifique d'un nouveau sujet.
     *
     * @param userInfo Les informations de l'utilisateur à notifier.
     * @param topic Le nouveau sujet proposé.
     */
    private void notifyUser(UserInfo userInfo, String topic) {
        try (Socket socket = new Socket(userInfo.getIpAddress(), userInfo.getPort());
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(topic);
            out.println(0.0); // Envoi d'une opinion fictive
            logger.info("Notified user with IP " + userInfo.getIpAddress() + " of new topic: " + topic);
        } catch (Exception e) {
            logger.warning("Failed to notify user: " + e.getMessage());
        }
    }

    /**
     * Méthode principale pour exécuter le serveur. Initialise le serveur et démarre
     * un thread pour gérer les enregistrements entrants.
     *
     * @param args Arguments de la ligne de commande. Le premier argument peut être
     *             le port du serveur au format --port=<port>.
     */
    public static void main(String[] args) {
        final int port;
        if (args.length > 0) {
            port = Integer.parseInt(args[0].split("=")[1]);
        } else {
            port = 12345;  // Port par défaut
        }

        Server server = new Server();

        // Exécute le serveur dans un thread séparé pour gérer les enregistrements entrants
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                logger.info("Server started on port " + port);
                while (true) {
                    Socket socket = serverSocket.accept();
                    new Thread(new RegistrationHandler(socket, server)).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}