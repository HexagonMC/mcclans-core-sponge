package nl.riebie.mcclans.commands.constraints;

/**
 * Created by riebie on 26/06/2016.
 */
public class PositiveNumberConstraint extends ParameterConstraint {
    @Override
    public int getMinimalLength() {
        return 0;
    }
}
