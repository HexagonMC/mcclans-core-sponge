/*
 * Copyright (c) 2016 riebie, Kippers <https://bitbucket.org/Kippers/mcclans-core-sponge>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package nl.riebie.mcclans.utils;

import nl.riebie.mcclans.MCClans;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.service.user.UserStorageService;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Kippers on 13/02/2016.
 */
public class UUIDUtils {

    private UserStorageService service;

    private static class Holder {
        private static final UUIDUtils INSTANCE = new UUIDUtils();
    }

    private UUIDUtils() {
        service = MCClans.getPlugin().getServiceHelper().userStorageService;
    }

    @Nullable
    public static UUID getUUID(String name) {
        if (Holder.INSTANCE.service == null || name.length() < 3 || name.length() > 16) {
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
