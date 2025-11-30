package io.github.alathra.boltux.utility;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import io.github.alathra.boltux.BoltUX;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.popcraft.bolt.BoltPlugin;
import org.popcraft.bolt.data.Store;
import org.popcraft.bolt.protection.Protection;
import org.popcraft.bolt.source.Source;
import org.popcraft.bolt.source.SourceTypes;
import org.popcraft.bolt.util.Group;

import java.util.*;
import java.util.stream.Collectors;

@ApiStatus.Internal
public final class BoltUtil {
    public static Set<UUID> getPlayerAccessSet(Protection protection) {
        return protection.getAccess().keySet()
            .stream()
            .map(rawSource -> {
                final Source source = Source.parse(rawSource);

                if (!source.getType().equals(SourceTypes.PLAYER))
                    return null;

                final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(source.getIdentifier());
                if (offlinePlayer.getName() == null)
                    return null;

                return UUID.fromString(source.getIdentifier());
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    public static Set<Group> getGroupAccessSet(Protection protection) {
        final Set<Group> accessGroupSet = new HashSet<>();
        final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
        for (String rawSource : protection.getAccess().keySet()) {
            Source source = Source.parse(rawSource);
            if (source.getType().equals(SourceTypes.GROUP)) {
                store.loadGroup(source.getIdentifier()).thenAccept(accessGroupSet::add);
            }
        }
        return accessGroupSet;
    }

    public static Set<Town> getTownAccessSet(Protection protection) {
        final Set<Town> accessTownSet = new HashSet<>();
        TownyAPI townyAPI = TownyAPI.getInstance();
        for (String rawSource : protection.getAccess().keySet()) {
            Source source = Source.parse(rawSource);
            if (source.getType().equals(SourceTypes.TOWN)) {
                if (townyAPI.getTown(source.getIdentifier()) != null) {
                    accessTownSet.add(townyAPI.getTown(source.getIdentifier()));
                }
            }
        }
        return accessTownSet;
    }

    public static Set<UUID> getTrustedPlayers(Player player) {
        final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
        final Set<UUID> trustedPlayers = new HashSet<>();
        store.loadAccessList(player.getUniqueId()).thenAccept(accessList -> {
            for (String rawSource : accessList.getAccess().keySet()) {
                Source source = Source.parse(rawSource);
                if (source.getType().equals(SourceTypes.PLAYER)) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(source.getIdentifier());
                    if (offlinePlayer.getName() == null) {
                        continue;
                    }
                    trustedPlayers.add(UUID.fromString(source.getIdentifier()));
                }
            }
        });
        return trustedPlayers;
    }

    public static Set<Group> getTrustedGroups(Player player) {
        final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
        final Set<Group> trustedGroups = new HashSet<>();
        store.loadAccessList(player.getUniqueId()).thenAccept(accessList -> {
            for (String rawSource : accessList.getAccess().keySet()) {
                Source source = Source.parse(rawSource);
                if (source.getType().equals(SourceTypes.GROUP)) {
                    store.loadGroup(source.getIdentifier()).thenAccept(trustedGroups::add);
                }
            }
        });
        return trustedGroups;
    }

    public static Set<Town> getTrustedTowns(Player player) {
        final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
        final Set<Town> trustedTowns = new HashSet<>();
        TownyAPI townyAPI = TownyAPI.getInstance();
        store.loadAccessList(player.getUniqueId()).thenAccept(accessList -> {
            for (String rawSource : accessList.getAccess().keySet()) {
                Source source = Source.parse(rawSource);
                if (source.getType().equals(SourceTypes.TOWN)) {
                    if (townyAPI.getTown(source.getIdentifier()) != null) {
                        trustedTowns.add(townyAPI.getTown(source.getIdentifier()));
                    }
                }
            }
        });
        return trustedTowns;
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
}
