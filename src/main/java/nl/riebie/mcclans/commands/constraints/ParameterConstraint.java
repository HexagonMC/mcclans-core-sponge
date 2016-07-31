package nl.riebie.mcclans.commands.constraints;

/**
 * Created by riebie on 26/06/2016.
 */
public abstract class ParameterConstraint {

    public int getMinimalLength() {
        return -1;
    }

    public int getMaximalLength() {
        return -1;
    }

    public String getRegex() {
        return "";
    }
}
