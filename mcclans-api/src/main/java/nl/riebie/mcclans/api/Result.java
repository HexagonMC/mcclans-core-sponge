package nl.riebie.mcclans.api;

/**
 * Created by riebie on 29/01/2017.
 */
public interface Result<T> {

    T getItem();

    String getErrorMessage();

    boolean isSuccessful();
}
