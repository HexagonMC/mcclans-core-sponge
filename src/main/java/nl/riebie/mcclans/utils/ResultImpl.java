package nl.riebie.mcclans.utils;

import nl.riebie.mcclans.api.Result;

import java.util.NoSuchElementException;

/**
 * Created by riebie on 29/01/2017.
 */
public class ResultImpl<T> implements Result<T> {

    private final String errorMessage;
    private final T item;

    private ResultImpl(T item) {
        this.item = item;
        errorMessage = null;
    }

    private ResultImpl(boolean success, String errorMessage) {
        item = null;
        this.errorMessage = errorMessage;
    }

    public static <T> Result<T> ofResult(T item) {
        return new ResultImpl<>(item);
    }

    public static <T> Result<T> ofError(String message) {
        return new ResultImpl<>(false, message);
    }

    @Override
    public T getItem() {
        if (item == null) {
            throw new NoSuchElementException("No value present");
        }
        return item;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public boolean isSuccessful() {
        return item != null;
    }
}
