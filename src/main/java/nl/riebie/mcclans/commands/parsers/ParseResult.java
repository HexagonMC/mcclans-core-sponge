package nl.riebie.mcclans.commands.parsers;

/**
 * Created by Mirko on 19/01/2016.
 */
public class ParseResult<T> {
    private boolean success;
    private String errorMessage;
    private T item;

    public static <U> ParseResult<U> newErrorResult(String message, String... args) {
        ParseResult<U> result = new ParseResult<>();
        result.success = false;
        result.errorMessage = String.format(message, args);
        return result;
    }

    public static <U> ParseResult<U> newSuccessResult(U item) {
        ParseResult<U> result = new ParseResult<>();
        result.success = true;
        result.item = item;
        return result;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getItem() {
        return item;
    }
}
