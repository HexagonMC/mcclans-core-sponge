package nl.riebie.mcclans.permissions;

import nl.riebie.mcclans.api.permissions.ClanPermission;
import nl.riebie.mcclans.api.permissions.ClanPermissionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by riebie on 10/04/2016.
 */
public class ClanPermissionManagerImpl implements ClanPermissionManager {

    private boolean initialized;
    private Map<String, ClanPermission> clanPermissionList = new HashMap<>();

    public ClanPermissionManagerImpl() {
        registerClanPermission("none", "Non-usable permission");
        registerClanPermission("home", "Teleport to the clan home");
        registerClanPermission("sethome", "Set the clan home location");
        registerClanPermission("invite", "Invite players to the clan");
        registerClanPermission("remove", "Remove clan members");
        registerClanPermission("disband", "Disband the clan");
        registerClanPermission("friendlyfire", "Change the clan's friendly fire setting");
        registerClanPermission("coords", "See the location of clan members");
        registerClanPermission("tag", "Modify the clan tag");
        registerClanPermission("rank", "Create, change or remove ranks");
        registerClanPermission("setrank", "Assign ranks to clan members");
        registerClanPermission("ally", "Invite or remove allies of the clan");
        registerClanPermission("clanchat", "Talk in clan chat");
        registerClanPermission("allychat", "Talk in ally chat");
        registerClanPermission("deposit", "Deposit currency in the clan bank");
        registerClanPermission("withdraw", "Withdraw currency from the clan bank");
    }

    public void setInitialized() {
        initialized = true;
    }

    @Override
    public boolean registerClanPermission(String name, String description) {
        if (initialized) {
            throw new IllegalStateException("ClanPermissions can only be registered during the initialization of the server");
        }
        name = name.toLowerCase();
        if (clanPermissionList.containsKey(name)) {
            return false;
        }
        clanPermissionList.put(name, new ClanPermissionImpl(name, description));
        return true;
    }

    @Override
    public boolean isActiveClanPermission(String name) {
        return clanPermissionList.containsKey(name.toLowerCase());
    }

    @Override
    public ClanPermission getClanPermission(String name) {
        return clanPermissionList.get(name.toLowerCase());
    }

    @Override
    public List<ClanPermission> getClanPermissions() {
        return new ArrayList<>(clanPermissionList.values().stream().filter(c -> !c.getName().equals("none")).collect(Collectors.toList()));
    }
}
