package io.github.alathra.boltux.gui;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.hook.Hook;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.popcraft.bolt.util.Group;

import java.util.*;
import java.util.stream.Collectors;

public final class GuiHelper {
    public static Set<UUID> getSuggestedPlayers(Player player, UUID owner) {
        // player, priority
        Map<UUID, Integer> players = new HashMap<>();

        // Get nearby players
        for (Entity entity : player.getNearbyEntities(Settings.getNearbyPlayersRange(), Settings.getNearbyPlayersRange(), Settings.getNearbyPlayersRange())) {
            if (entity instanceof Player nearbyPlayer) {
                if (!nearbyPlayer.getUniqueId().equals(owner))
                    players.put(nearbyPlayer.getUniqueId(), 1);
            }
        }

        // Get players in town and nation
        if (Hook.Towny.isLoaded()) {
            TownyAPI townyAPI = TownyAPI.getInstance();
            Resident playerResident = townyAPI.getResident(player.getUniqueId());
            if (playerResident != null) {
                Town playerTown = playerResident.getTownOrNull();
                if (playerTown != null) {
                    for (Resident resident : playerTown.getResidents()) {
                        if (!resident.getUUID().equals(owner)) {
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(resident.getUUID());
                            if (offlinePlayer.getName() != null) {
                                players.put(resident.getUUID(), 2);
                            }
                        }
                    }
                    Nation playerNation = playerTown.getNationOrNull();
                    if (playerNation != null) {
                        for (Town town : playerNation.getTowns()) {
                            if (town != playerTown) {
                                for (Resident resident : town.getResidents()) {
                                    if (!resident.getUUID().equals(owner)) {
                                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(resident.getUUID());
                                        if (offlinePlayer.getName() != null) {
                                            players.put(resident.getUUID(), 3);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Get online players
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (!onlinePlayer.getUniqueId().equals(owner)) {
                players.put(onlinePlayer.getUniqueId(), 4);
            }
        }

        // Sort the players first by priority, then by name
        Set<UUID> sortedPlayers = new TreeSet<>((p1, p2) -> {
            // Compare by priority
            int priorityComparison = players.get(p1).compareTo(players.get(p2));
            if (priorityComparison != 0) {
                return priorityComparison;
            }
            // If priority is the same, compare by player name (alphabetically)
            return Objects.requireNonNull(Bukkit.getOfflinePlayer(p1).getName()).compareToIgnoreCase(Objects.requireNonNull(Bukkit.getOfflinePlayer(p2).getName()));
        });

        Iterator<UUID> iterator = players.keySet().iterator();
        while (iterator.hasNext()) {
            UUID playerUUID = iterator.next();
            if (Bukkit.getOfflinePlayer(playerUUID).getName() == null) {
                iterator.remove();
                continue;
            }
            sortedPlayers.add(playerUUID);
        }

        return sortedPlayers;
    }

    public static Set<Town> getSuggestedTowns(Player player) {
        Set<Town> suggestedTowns = new HashSet<>();
        Resident resident = TownyAPI.getInstance().getResident(player);
        if (resident == null) {
            return suggestedTowns;
        }
        Town residentTown = resident.getTownOrNull();
        if (residentTown != null) {
            suggestedTowns.add(residentTown);
            Nation nation = resident.getNationOrNull();
            if (nation != null) {
                suggestedTowns.addAll(nation.getTowns());
            }
        }
        return suggestedTowns;
    }

    public static String getGroupMemberNames(Group group) {
        return String.join(", ",
            group.getMembers().stream()
                .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                .collect(Collectors.toSet())
        );
    }
}
