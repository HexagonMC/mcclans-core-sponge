package nl.riebie.mcclans.api.exceptions;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public class NotDefaultImplementationException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public NotDefaultImplementationException(Class<?> implementation) {
        super(implementation.getName() + " is not the default implementation for " + implementation.getInterfaces()[0].getName()
                + ", use the API to get the right implementation of this interface");

    }

}
