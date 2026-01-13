import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * La classe RegistrationHandler gère les enregistrements des utilisateurs et les notifications des nouveaux sujets. 
 * Elle traite les messages entrants des utilisateurs et prend les actions appropriées.
 */
public class RegistrationHandler implements Runnable {
    private final Socket socket;
    private final Server server;

    /**
     * Constructeur de la classe RegistrationHandler.
     *
     * @param socket Le socket à travers lequel les messages sont reçus.
     * @param server L'instance du serveur pour enregistrer les utilisateurs et notifier les sujets.
     */
    public RegistrationHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    /**
     * Méthode exécutée par le thread pour gérer les enregistrements et les notifications.
     * Traite les messages entrants pour enregistrer les utilisateurs, notifier les sujets,
     * ou récupérer les informations des utilisateurs.
     */
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String message = in.readLine();
            if (message.startsWith("USER:")) {
                String userId = message.split(":")[1];
                String ipAddress = socket.getInetAddress().getHostAddress();
                int port = Integer.parseInt(in.readLine());
                server.registerUser(userId, ipAddress, port);
                out.println("Registration successful");
            } else if (message.startsWith("PROPOSER:")) {
                String topic = message.split(":")[1];
                server.notifyNewTopic(topic);
                out.println("Topic registered: " + topic);
            } else if (message.startsWith("GET_USER_INFO:")) {
                String userId = message.split(":")[1];
                UserInfo userInfo = server.getUserInfo(userId);
                if (userInfo != null) {
                    out.println(userInfo.getIpAddress());
                    out.println(userInfo.getPort());
                } else {
                    out.println("null");
                    out.println("0");
                }
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}