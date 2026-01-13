import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
/**
 * La classe MessageHandler gère les messages reçus par l'utilisateur. 
 * Elle lit les messages depuis le socket et appelle les méthodes appropriées pour mettre à jour l'état de l'utilisateur.
 */
public class MessageHandler implements Runnable {
    private final Socket socket;
    private final User user;

    public MessageHandler(Socket socket, User user) {
        this.socket = socket;
        this.user = user;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String topic = in.readLine();
            if (topic != null) {
                String opinionStr = in.readLine();
                if (opinionStr != null) {
                    double opinion = Double.parseDouble(opinionStr);
                    user.receiveMessage(topic, opinion);
                } else {
                    User.logger.warning("Received null opinion");
                }
            } else {
                User.logger.warning("Received null topic");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
