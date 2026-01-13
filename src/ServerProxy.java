import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.logging.Logger;
import java.io.PrintWriter;


/**
 * La classe ServerProxy gère la communication entre un utilisateur et le serveur central.
 * Elle permet l'enregistrement des utilisateurs, la récupération d'informations sur les utilisateurs
 * et la notification de nouveaux sujets au serveur.
 *
 * <p>
 * Les communications avec le serveur sont réalisées en utilisant des sockets TCP pour assurer
 * la fiabilité des transmissions.
 * </p>
 */
public class ServerProxy {
    private static final Logger logger = Logger.getLogger(ServerProxy.class.getName());
    private final String serverIp;
    private final int serverPort;

    /**
     * Constructeur de la classe ServerProxy.
     *
     * @param serverIp L'adresse IP du serveur.
     * @param serverPort Le port du serveur.
     */
    public ServerProxy(String serverIp, int serverPort) {
        this.serverIp = serverIp;
        this.serverPort = serverPort;
    }

    /**
     * Enregistre un utilisateur auprès du serveur central.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @param port Le port sur lequel l'utilisateur écoute les connexions entrantes.
     */
    public void registerUser(String userId, int port) {
        try (Socket socket = new Socket(serverIp, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("USER:" + userId);
            out.println(port);

            String response = in.readLine();
            logger.info("Server response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère les informations d'un utilisateur à partir de son identifiant.
     *
     * @param userId L'identifiant de l'utilisateur.
     * @return Un objet UserInfo contenant l'adresse IP et le port de l'utilisateur, ou null si non trouvé.
     */
    public UserInfo getUserInfo(String userId) {
        try (Socket socket = new Socket(serverIp, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println("GET_USER_INFO:" + userId);
            String ipAddress = in.readLine();
            String portStr = in.readLine();

            if (ipAddress != null && portStr != null) {
                int port = Integer.parseInt(portStr);
                return new UserInfo(ipAddress, port);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Notifie le serveur d'un nouveau sujet proposé.
     *
     * @param topic Le nouveau sujet proposé.
     */
    public void notifyNewTopic(String topic) {
        try (Socket socket = new Socket(serverIp, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println("PROPOSER:" + topic);
            logger.info("Notified server about new topic: " + topic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
