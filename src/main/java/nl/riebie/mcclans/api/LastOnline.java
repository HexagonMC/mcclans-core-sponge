package nl.riebie.mcclans.api;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public interface LastOnline {

    /**
     * Get the time in milliseconds passed since the player was last online
     *
     * @return time passed in milliseconds
     */
    public long getTime();

    /**
     * Get a user friendly representation of the time passed since the player was last online
     *
     * @return user friendly representation of the time passed
     */
    public String getDifferenceInText();
}
