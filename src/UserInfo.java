/**
 * La classe UserInfo contient les informations d'un utilisateur,
 * notamment son adresse IP et son port.
 */
public class UserInfo {
    private final String ipAddress;
    private final int port;

    /**
     * Constructeur de la classe UserInfo.
     *
     * @param ipAddress L'adresse IP de l'utilisateur.
     * @param port Le port sur lequel l'utilisateur écoute.
     */
    public UserInfo(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }
}
