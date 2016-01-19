package nl.riebie.mcclans.enums;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public enum DBMSType {

    MYSQL("mysql"), SQLITE("sqlite"), UNRECOGNISED("unrecognised");

    private String name;

    DBMSType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static DBMSType getType(String name) {
        for (DBMSType type : values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        return UNRECOGNISED;
    }
}
