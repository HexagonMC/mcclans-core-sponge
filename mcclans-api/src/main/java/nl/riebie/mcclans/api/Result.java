package nl.riebie.mcclans.api;

/**
 * Result of a method.
 * <p>
 * Contains the result item if the action was successful. Otherwise, it contains an error message.
 * <p>
 * Created by riebie on 29/01/2017.
 */
public interface Result<T> {

    /**
     * @return The result if the action was successful, otherwise null
     */
    T getItem();

    /**
     * @return The user friendly error message if the result is not successful
     */
    String getErrorMessage();

    /**
     * @return {@code true} if the action was successful
     */
    boolean isSuccessful();
}
