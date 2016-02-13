package nl.riebie.mcclans.commands.constraints.length;

/**
 * Created by Mirko on 13/02/2016.
 */
public class EmptyLengthConstraint implements LengthConstraint {
    @Override
    public int getMinimalLength() {
        return -1;
    }

    @Override
    public int getMaximalLength() {
        return -1;
    }
}
