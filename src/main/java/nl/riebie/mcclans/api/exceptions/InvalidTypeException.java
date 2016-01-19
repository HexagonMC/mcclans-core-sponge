package nl.riebie.mcclans.api.exceptions;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class InvalidTypeException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public InvalidTypeException(String parameter, Class<?> f) {
        super("Parameter " + parameter + " " + f.getName());

    }

}
