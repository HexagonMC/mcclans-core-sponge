package nl.riebie.mcclans.api.events;

import nl.riebie.mcclans.api.ClanPlayer;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by riebie on 29/01/2017.
 */
public class ClanSetHomeEvent extends CancellableClanEvent {

    private final Location<World> location;

    private ClanSetHomeEvent(Cause cause, Location<World> location) {
        super(cause);
        this.location = location;
    }

    public Location<World> getLocation() {
        return location;
    }

    public static class User extends ClanSetHomeEvent {

        private final ClanPlayer clanPlayer;

        public User(ClanPlayer clanPlayer, Location<World> location) {
            super(Cause.of(NamedCause.source(clanPlayer)), location);

            this.clanPlayer = clanPlayer;
        }

        public ClanPlayer getClanPlayer() {
            return clanPlayer;
        }
    }

    public static class Admin extends ClanSetHomeEvent {

        public Admin(CommandSource commandSource, Location<World> location) {
            super(Cause.of(NamedCause.source(commandSource)), location);

        }

    }
}
