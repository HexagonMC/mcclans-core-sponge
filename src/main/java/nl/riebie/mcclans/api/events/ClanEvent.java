package nl.riebie.mcclans.api.events;

import org.spongepowered.api.event.Event;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.util.annotation.NonnullByDefault;

/**
 * Created by K.Volkers on 19-1-2016.
 */
public abstract class ClanEvent extends AbstractEvent {
//    private static final HandlerList handlers = new HandlerList();
//
//    @Override
//    public HandlerList getHandlers() {
//        return handlers;
//    }
//
//    public static HandlerList getHandlerList() {
//        return handlers;
//    }

    @Override
    public Cause getCause() {
        return Cause.of(
                NamedCause.of("ClanEvent", this)
        );
    }


    // TODO SPONGE: https://docs.spongepowered.org/en/plugin/event/custom.html

}
