package io.github.alathra.boltux.gui;

import com.github.milkdrinkers.colorparser.ColorParser;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.popcraft.bolt.data.Store;
import org.popcraft.bolt.protection.Protection;
import org.popcraft.bolt.util.Group;

import java.util.*;
import java.util.stream.Collectors;

public class GuiHelper {

    public static Set<OfflinePlayer> getSuggestedPlayers(Player player) {
        // player, priority
        Map<OfflinePlayer, Integer> players = new HashMap<>();

        // Get nearby players
        for (Entity entity : player.getNearbyEntities(Settings.getNearbyPlayersRange(), Settings.getNearbyPlayersRange(), Settings.getNearbyPlayersRange())) {
            if (entity instanceof Player nearbyPlayer) {
                players.put(nearbyPlayer, 1);
            }
        }

        // Get players in town and nation
        if (BoltUX.getTownyHook().isHookLoaded()) {
            TownyAPI townyAPI = TownyAPI.getInstance();
            Resident playerResident = townyAPI.getResident(player.getUniqueId());
            if (playerResident != null) {
                Town playerTown = playerResident.getTownOrNull();
                if (playerTown != null) {
                    for (Resident resident : playerTown.getResidents()) {
                        if (!resident.getUUID().equals(player.getUniqueId())) {
                            players.put(Bukkit.getOfflinePlayer(resident.getUUID()), 2);
                        }
                    }
                    Nation playerNation = playerTown.getNationOrNull();
                    if (playerNation != null) {
                        for (Town town : playerNation.getTowns()) {
                            if (town != playerTown) {
                                for (Resident resident : town.getResidents()) {
                                    if (!resident.getUUID().equals(player.getUniqueId())) {
                                        players.put(Bukkit.getOfflinePlayer(resident.getUUID()), 3);
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
            if (!onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
                players.put(onlinePlayer, 4);
            }
        }

        // Sort the players first by priority, then by name
        Set<OfflinePlayer> sortedPlayers = new TreeSet<>((p1, p2) -> {
            // Compare by priority
            int priorityComparison = players.get(p1).compareTo(players.get(p2));
            if (priorityComparison != 0) {
                return priorityComparison;
            }
            // If priority is the same, compare by player name (alphabetically)
            return Objects.requireNonNull(p1.getName()).compareToIgnoreCase(Objects.requireNonNull(p2.getName()));
        });

        sortedPlayers.addAll(players.keySet());

        return sortedPlayers;
    }

    public static GuiItem playerToRemovableAccessIcon(PaginatedGui gui, Protection protection, OfflinePlayer player) {
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setPlayerProfile(player.getPlayerProfile());
        skullMeta.displayName(ColorParser.of("<green>" + player.getName()).build().decoration(TextDecoration.ITALIC, false));
        skullMeta.lore(List.of(
            ColorParser.of("<gray>Player").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Click to revoke access").build().decoration(TextDecoration.ITALIC, false)
        ));
        skullItem.setItemMeta(skullMeta);
        return ItemBuilder.from(skullItem).asGuiItem(event -> {
            protection.getAccess().remove("player:" + player.getUniqueId());
            BoltUX.getBoltPlugin().saveProtection(protection);
            skullItem.setAmount(0);
            final int slot = event.getSlot();
            gui.updateItem(slot, skullItem);
        });
    }

    public static GuiItem playerToAddableAccessIcon(PaginatedGui gui, OfflinePlayer player, Protection protection) {
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setPlayerProfile(player.getPlayerProfile());
        skullMeta.displayName(ColorParser.of("<green>" + player.getName()).build().decoration(TextDecoration.ITALIC, false));
        skullMeta.lore(List.of(
            ColorParser.of("<gray>Player").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Click to grant access").build().decoration(TextDecoration.ITALIC, false)
        ));
        skullItem.setItemMeta(skullMeta);
        return ItemBuilder.from(skullItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            protection.getAccess().put("player:" + player.getUniqueId(), "normal");
            BoltUX.getBoltPlugin().saveProtection(protection);
            skullItem.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
            skullItem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            skullMeta.lore(List.of(
                ColorParser.of("<gray>Player").build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of("<green>Access has been granted").build().decoration(TextDecoration.ITALIC, false)

            ));
            skullItem.setItemMeta(skullMeta);
            gui.updateItem(slot, skullItem);
        });
    }

    public static GuiItem playerToTransferableAccessIcon(PaginatedGui gui, Player viewer, OfflinePlayer player, Protection protection) {
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setPlayerProfile(player.getPlayerProfile());
        skullMeta.displayName(ColorParser.of("<green>" + player.getName()).build().decoration(TextDecoration.ITALIC, false));
        skullMeta.lore(List.of(
            ColorParser.of("<gray>Player").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Click to transfer ownership").build().decoration(TextDecoration.ITALIC, false)
        ));
        skullItem.setItemMeta(skullMeta);
        return ItemBuilder.from(skullItem).asGuiItem(event -> {
            protection.setOwner(player.getUniqueId());
            BoltUX.getBoltPlugin().saveProtection(protection);
            gui.close(viewer);
        });
    }

    public static GuiItem playerToRemovableTrustIcon(PaginatedGui gui, Player viewer, OfflinePlayer player) {
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setPlayerProfile(player.getPlayerProfile());
        skullMeta.displayName(ColorParser.of("<green>" + player.getName()).build().decoration(TextDecoration.ITALIC, false));
        skullMeta.lore(List.of(
            ColorParser.of("<gray>Player").build().decoration(TextDecoration.ITALIC, false)
        ));
        skullItem.setItemMeta(skullMeta);
        return ItemBuilder.from(skullItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            final Store store =BoltUX.getBoltPlugin().getBolt().getStore();
            store.loadAccessList(viewer.getUniqueId()).thenAccept(accessList -> {
                accessList.getAccess().remove("player:" + player.getUniqueId());
                store.saveAccessList(accessList);
                skullItem.setAmount(0);
                gui.updateItem(slot, skullItem);
            });
        });
    }

    public static GuiItem playerToAddableTrustIcon(PaginatedGui gui, Player viewer, OfflinePlayer player) {
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setPlayerProfile(player.getPlayerProfile());
        skullMeta.displayName(ColorParser.of("<green>" + player.getName()).build().decoration(TextDecoration.ITALIC, false));
        skullMeta.lore(List.of(
            ColorParser.of("<gray>Player").build().decoration(TextDecoration.ITALIC, false)
        ));
        skullItem.setItemMeta(skullMeta);
        return ItemBuilder.from(skullItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
            store.loadAccessList(viewer.getUniqueId()).thenAccept(accessList -> {
                accessList.getAccess().put("player:" + player.getUniqueId(), "normal");
                store.saveAccessList(accessList);
                skullItem.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
                skullItem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                skullMeta.lore(List.of(
                    ColorParser.of("<gray>Player").build().decoration(TextDecoration.ITALIC, false),
                    ColorParser.of("<green>Trust has been granted").build().decoration(TextDecoration.ITALIC, false)
                ));
                skullItem.setItemMeta(skullMeta);
                gui.updateItem(slot, skullItem);
            });
        });
    }

    public static GuiItem groupToRemovableAccessIcon(PaginatedGui gui, Protection protection, Group group) {
        ItemStack groupItem = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta groupMeta = groupItem.getItemMeta();
        String groupMemberNames = String.join(", ",
            group.getMembers().stream()
            .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
            .collect(Collectors.toSet())
        );
        groupMeta.displayName(ColorParser.of("<blue>" + group.getName()).build().decoration(TextDecoration.ITALIC, false));
        groupMeta.lore(List.of(
            ColorParser.of("<gray>Group").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Owner: " + Bukkit.getOfflinePlayer(group.getOwner()).getName()).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Members: " + groupMemberNames).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Click to revoke access").build().decoration(TextDecoration.ITALIC, false)
        ));
        groupItem.setItemMeta(groupMeta);
        return ItemBuilder.from(groupItem).asGuiItem(event -> {
            protection.getAccess().remove("group:" + group.getName());
            BoltUX.getBoltPlugin().saveProtection(protection);
            groupItem.setAmount(0);
            final int slot = event.getSlot();
            gui.updateItem(slot, groupItem);
        });
    }

    public static GuiItem groupToAddableAccessIcon(PaginatedGui gui, Protection protection, Group group) {
        ItemStack groupItem = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta groupMeta = groupItem.getItemMeta();
        String groupMemberNames = String.join(", ",
            group.getMembers().stream()
                .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                .collect(Collectors.toSet())
        );
        groupMeta.displayName(ColorParser.of("<blue>" + group.getName()).build().decoration(TextDecoration.ITALIC, false));
        groupMeta.lore(List.of(
            ColorParser.of("<gray>Group").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Owner: " + Bukkit.getOfflinePlayer(group.getOwner()).getName()).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Members: " + groupMemberNames).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Click to grant access").build().decoration(TextDecoration.ITALIC, false)
        ));
        groupItem.setItemMeta(groupMeta);
        return ItemBuilder.from(groupItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            protection.getAccess().put("group:" + group.getName(), "normal");
            BoltUX.getBoltPlugin().saveProtection(protection);
            groupItem.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
            groupItem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            groupMeta.lore(List.of(
                ColorParser.of("<gray>Group").build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of("<gray>Owner: " + Bukkit.getOfflinePlayer(group.getOwner()).getName()).build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of("<gray>Members: " + groupMemberNames).build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of("<green>Access has been granted").build().decoration(TextDecoration.ITALIC, false)
            ));
            groupItem.setItemMeta(groupMeta);
            gui.updateItem(slot, groupItem);
        });
    }

    public static GuiItem groupToRemovableTrustIcon(PaginatedGui gui, Player viewer, Group group) {
        ItemStack groupItem = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta groupMeta = groupItem.getItemMeta();
        String groupMemberNames = String.join(", ",
            group.getMembers().stream()
                .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                .collect(Collectors.toSet())
        );
        groupMeta.displayName(ColorParser.of("<blue>" + group.getName()).build().decoration(TextDecoration.ITALIC, false));
        groupMeta.lore(List.of(
            ColorParser.of("<gray>Group").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Owner: " + Bukkit.getOfflinePlayer(group.getOwner()).getName()).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Members: " + groupMemberNames).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Click to revoke trust").build().decoration(TextDecoration.ITALIC, false)
        ));
        groupItem.setItemMeta(groupMeta);
        return ItemBuilder.from(groupItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
            store.loadAccessList(viewer.getUniqueId()).thenAccept(accessList -> {
                accessList.getAccess().remove("group:" + group.getName());
                store.saveAccessList(accessList);
                groupItem.setAmount(0);
                gui.updateItem(slot, groupItem);
            });
        });
    }

    public static GuiItem groupToAddableTrustIcon(PaginatedGui gui, Player viewer, Group group) {
        ItemStack groupItem = new ItemStack(Material.CRAFTING_TABLE);
        ItemMeta groupMeta = groupItem.getItemMeta();
        String groupMemberNames = String.join(", ",
            group.getMembers().stream()
                .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                .collect(Collectors.toSet())
        );
        groupMeta.displayName(ColorParser.of("<blue>" + group.getName()).build().decoration(TextDecoration.ITALIC, false));
        groupMeta.lore(List.of(
            ColorParser.of("<gray>Group").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Owner: " + Bukkit.getOfflinePlayer(group.getOwner()).getName()).build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Members: " + groupMemberNames).build().decoration(TextDecoration.ITALIC, false)
        ));
        groupItem.setItemMeta(groupMeta);
        return ItemBuilder.from(groupItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
            store.loadAccessList(viewer.getUniqueId()).thenAccept(accessList -> {
                accessList.getAccess().put("group:" + group.getName(), "normal");
                store.saveAccessList(accessList);
                groupItem.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
                groupItem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                groupMeta.lore(List.of(
                    ColorParser.of("<gray>Group").build().decoration(TextDecoration.ITALIC, false),
                    ColorParser.of("<gray>Owner: " + Bukkit.getOfflinePlayer(group.getOwner()).getName()).build().decoration(TextDecoration.ITALIC, false),
                    ColorParser.of("<gray>Members: " + groupMemberNames).build().decoration(TextDecoration.ITALIC, false),
                    ColorParser.of("<green>Trust has been granted").build().decoration(TextDecoration.ITALIC, false)
                ));
                groupItem.setItemMeta(groupMeta);
                gui.updateItem(slot, groupItem);
            });
        });
    }

}
