package nl.riebie.mcclans.utils;

import nl.riebie.mcclans.MCClans;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.ProviderRegistration;
import org.spongepowered.api.service.user.UserStorageService;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Koen on 13/02/2016.
 */
public class UUIDUtils {

    private UserStorageService service;

    private static class Holder {
        private static final UUIDUtils INSTANCE = new UUIDUtils();
    }

    private UUIDUtils() {
        Optional<ProviderRegistration<UserStorageService>> userStorageOpt = Sponge.getServiceManager().getRegistration(UserStorageService.class);
        if (userStorageOpt.isPresent()) {
            service = userStorageOpt.get().getProvider();
        } else {
            MCClans.getPlugin().getLogger().warn("UUIDUtils could not find UserStorageService during initialization!");
        }
    }

    @Nullable
    public static UUID getUUID(String name) {
        if (Holder.INSTANCE.service == null) {
            return null;
        }

        Optional<User> userOpt = Holder.INSTANCE.service.get(name);
        if (userOpt.isPresent()) {
            return userOpt.get().getUniqueId();
        } else {
            return null;
        }
    }

    @Nullable
    public static String getName(UUID uuid) {
        if (Holder.INSTANCE.service == null) {
            return null;
        }

        Optional<User> userOpt = Holder.INSTANCE.service.get(uuid);
        if (userOpt.isPresent()) {
            return userOpt.get().getName();
        } else {
            return null;
        }
    }
}
