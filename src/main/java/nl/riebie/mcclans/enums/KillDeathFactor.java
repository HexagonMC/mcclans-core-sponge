package nl.riebie.mcclans.enums;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public enum KillDeathFactor {
    LOW("Low"), MEDIUM("Medium"), HIGH("High");

    private String userFriendlyName;

    KillDeathFactor(String userFriendlyName) {
        this.userFriendlyName = userFriendlyName;
    }

    public String getUserFriendlyName() {
        return userFriendlyName;
    }
}
