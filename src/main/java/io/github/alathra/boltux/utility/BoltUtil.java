package io.github.alathra.boltux.utility;

import io.github.alathra.boltux.BoltUX;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.data.Store;
import org.popcraft.bolt.protection.Protection;
import org.popcraft.bolt.util.Group;

import java.util.*;

public class BoltUtil {

    public static Set<OfflinePlayer> getPlayerAccessSet(Protection protection) {
        final Set<OfflinePlayer> accessPlayerSet = new HashSet<>();
        for (String accessDetails : protection.getAccess().keySet()) {
            if (accessDetails.startsWith("player:")) {
                accessPlayerSet.add(Bukkit.getOfflinePlayer(UUID.fromString(accessDetails.substring(7))));
            }
        }
        return accessPlayerSet;
    }

    public static Set<Group> getGroupAccessSet(Protection protection) {
        final Set<Group> accessGroupSet = new HashSet<>();
        final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
        for (String accessDetails : protection.getAccess().keySet()) {
            if (accessDetails.startsWith("group:")) {
                store.loadGroup(accessDetails.substring(6)).thenAccept(accessGroupSet::add);
            }
        }
        return accessGroupSet;
    }

    public static Set<Group> getGroupsWithoutAccess(Protection protection) {
        final BoltPlugin boltPlugin = BoltUX.getBoltPlugin();
        final Store store = boltPlugin.getBolt().getStore();
        final Set<Group> groups = new HashSet<>();
        // Get all protection owner's groups
        for (String groupName : boltPlugin.getPlayersOwnedGroups(Bukkit.getPlayer(protection.getOwner()))) {
            store.loadGroup(groupName).thenAccept(groups::add);
        }
        // Prune groups that have access already
        for (Group group : getGroupAccessSet(protection)) {
            groups.remove(group);
        }
        return groups;
    }

    public static Set<Group> getTrustedGroups(Player player) {
        final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
        final Set<Group> trustedGroups = new HashSet<>();
        store.loadAccessList(player.getUniqueId()).thenAccept(accessList -> {
            for (String accessDetails : accessList.getAccess().keySet()) {
                if (accessDetails.startsWith("group:")) {
                    store.loadGroup(accessDetails.substring(6)).thenAccept(trustedGroups::add);
                }
            }
        });
        return trustedGroups;
    }

    public static Set<OfflinePlayer> getTrustedPlayers(Player player) {
        final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
        final Set<OfflinePlayer> trustedPlayers = new HashSet<>();
        store.loadAccessList(player.getUniqueId()).thenAccept(accessList -> {
            for (String accessDetails : accessList.getAccess().keySet()) {
                if (accessDetails.startsWith("player:")) {
                    trustedPlayers.add(Bukkit.getOfflinePlayer(UUID.fromString(accessDetails.substring(7))));
                }
            }
        });
        return trustedPlayers;
    }
}
