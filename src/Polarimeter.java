import java.util.logging.Logger;
import java.util.Random;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;

/**
 * La classe Polarimeter est utilisée pour mesurer la polarisation des opinions des utilisateurs sur un sujet donné. 
 * Elle utilise un Timer pour effectuer des mesures périodiques.
 *
 * <p>
 * Les opinions des utilisateurs sont regroupées en bins, et la polarisation est calculée en fonction de la distribution des opinions dans ces bins.
 * </p>
 */
public class Polarimeter {
    private static final Logger logger = Logger.getLogger(Polarimeter.class.getName());
    private final List<User> users;
    private final String topic;
    private final long delay;

    /**
     * Constructeur de la classe Polarimeter.
     *
     * @param users La liste des utilisateurs participant à la mesure.
     * @param topic Le sujet sur lequel la polarisation est mesurée.
     * @param delay Le délai entre chaque mesure de la polarisation (en millisecondes).
     */
    public Polarimeter(List<User> users, String topic, long delay) {
        this.users = users;
        this.topic = topic;
        this.delay = delay;
    }

    /**
     * Démarre le processus de mesure de la polarisation à intervalles réguliers.
     */
    public void start() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                measurePolarization();
            }
        }, 0, delay);
    }

    /**
     * Mesure la polarisation des opinions des utilisateurs et enregistre le résultat dans les logs.
     */
    private void measurePolarization() {
        int[] bins = new int[5];
        double[] midpoints = {0.1, 0.3, 0.5, 0.7, 0.9};
        double alpha = 1.6;
        double K = 1.0;

        for (User user : users) {
            double opinion = user.getOpinion();
            int binIndex = (int) (opinion * 5);
            if (binIndex == 5) binIndex = 4; // gérer le cas limite pour opinion = 1.0
            bins[binIndex]++;
        }

        double polarization = 0.0;
        for (int i = 0; i < bins.length; i++) {
            for (int j = 0; j < bins.length; j++) {
                polarization += Math.pow(bins[i], 1 + alpha) * bins[j] * Math.abs(midpoints[i] - midpoints[j]);
            }
        }
        polarization *= K;
        logger.info("Polarization on topic " + topic + " is " + polarization);
    }

    /**
     * Méthode principale pour exécuter Polarimeter. Initialise les utilisateurs avec des opinions
     * et des influences aléatoires et démarre le processus de mesure de la polarisation sur un sujet
     * spécifié.
     *
     * @param args Arguments de la ligne de commande au format :
     *             --users=<user1,user2,...> --topic=<topic> --delay=<delay> --serverIp=<serverIp> --serverPort=<serverPort> --port=<startPort>
     */
    public static void main(String[] args) {
        if (args.length < 6) {
            System.out.println("Utilisation : java Polarimeter --users=<user1,user2,...> --topic=<topic> --delay=<delay> --serverIp=<serverIp> --serverPort=<serverPort> --port=<startPort>");
            return;
        }

        String[] userIds = args[0].split("=")[1].split(",");
        String topic = args[1].split("=")[1];
        long delay = Long.parseLong(args[2].split("=")[1]);
        String serverIp = args[3].split("=")[1];
        int serverPort = Integer.parseInt(args[4].split("=")[1]);
        int startPort = Integer.parseInt(args[5].split("=")[1]);

        Random random = new Random();
        List<User> users = new ArrayList<>();
        for (int i = 0; i < userIds.length; i++) {
            double opinion = random.nextDouble();
            double influence = random.nextDouble();
            users.add(new User(userIds[i], opinion, influence, serverIp, serverPort, startPort + i));
        }

        Polarimeter polarimeter = new Polarimeter(users, topic, delay);
        polarimeter.start();
    }
}