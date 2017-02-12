package nl.riebie.mcclans.api.events;

import nl.riebie.mcclans.api.ClanPlayer;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.NamedCause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

/**
 * Created by riebie on 29/01/2017.
 * <p>
 * An event which is fired when a clan home is set.
 */
public class ClanSetHomeEvent extends CancellableClanEvent {

    private final Location<World> location;

    private ClanSetHomeEvent(Cause cause, Location<World> location) {
        super("Clan set home cancelled by an external plugin", cause);
        this.location = location;
    }

    public Location<World> getLocation() {
        return location;
    }

    /**
     * A user command was used to set the clan home.
     */
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

    /**
     * An admin command was used to set the clan home.
     */
    public static class Admin extends ClanSetHomeEvent {

        public Admin(CommandSource commandSource, Location<World> location) {
            super(Cause.of(NamedCause.source(commandSource)), location);
        }
    }

    /**
     * An external plugin set the clan home.
     */
    public static class Plugin extends ClanSetHomeEvent {

        public Plugin(Location<World> location) {
            super(Cause.of(NamedCause.source(location)), location);
        }
    }
}
