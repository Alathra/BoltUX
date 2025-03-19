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

    public static Set<UUID> getSuggestedPlayers(Player player) {
        // player, priority
        Map<UUID, Integer> players = new HashMap<>();

        // Get nearby players
        for (Entity entity : player.getNearbyEntities(Settings.getNearbyPlayersRange(), Settings.getNearbyPlayersRange(), Settings.getNearbyPlayersRange())) {
            if (entity instanceof Player nearbyPlayer) {
                players.put(nearbyPlayer.getUniqueId(), 1);
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
                                    if (!resident.getUUID().equals(player.getUniqueId())) {
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
            if (!onlinePlayer.getUniqueId().equals(player.getUniqueId())) {
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

        sortedPlayers.addAll(players.keySet());

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

    public static GuiItem playerToRemovableAccessIcon(PaginatedGui gui, Protection protection, OfflinePlayer player) {
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setOwningPlayer(player);
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

    public static GuiItem playerToAddableAccessIcon(PaginatedGui gui, Protection protection, OfflinePlayer player) {
        ItemStack skullItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta skullMeta = (SkullMeta) skullItem.getItemMeta();
        skullMeta.setOwningPlayer(player);
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
        skullMeta.setOwningPlayer(player);
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
        skullMeta.setOwningPlayer(player);
        skullMeta.displayName(ColorParser.of("<green>" + player.getName()).build().decoration(TextDecoration.ITALIC, false));
        skullMeta.lore(List.of(
            ColorParser.of("<gray>Player").build().decoration(TextDecoration.ITALIC, false)
        ));
        skullItem.setItemMeta(skullMeta);
        return ItemBuilder.from(skullItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
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
        skullMeta.setOwningPlayer(player);
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
        ItemStack groupItem = new ItemStack(Material.CHEST);
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
        ItemStack groupItem = new ItemStack(Material.CHEST);
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
        ItemStack groupItem = new ItemStack(Material.CHEST);
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
        ItemStack groupItem = new ItemStack(Material.CHEST);
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

    public static GuiItem townToRemovableAccessIcon(PaginatedGui gui, Protection protection, Town town) {
        ItemStack townItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta townMeta = townItem.getItemMeta();
        townMeta.displayName(ColorParser.of("<green>" + town.getName()).build().decoration(TextDecoration.ITALIC, false));
        townMeta.lore(List.of(
            ColorParser.of("<gray>Town").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Click to revoke access").build().decoration(TextDecoration.ITALIC, false)
        ));
        townItem.setItemMeta(townMeta);
        return ItemBuilder.from(townItem).asGuiItem(event -> {
            protection.getAccess().remove("town:" + town.getName());
            BoltUX.getBoltPlugin().saveProtection(protection);
            townItem.setAmount(0);
            final int slot = event.getSlot();
            gui.updateItem(slot, townItem);
        });
    }

    public static GuiItem townToAddableAccessIcon(PaginatedGui gui, Protection protection, Town town) {
        ItemStack townItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta townMeta = townItem.getItemMeta();
        townMeta.displayName(ColorParser.of("<green>" + town.getName()).build().decoration(TextDecoration.ITALIC, false));
        townMeta.lore(List.of(
            ColorParser.of("<gray>Town").build().decoration(TextDecoration.ITALIC, false),
            ColorParser.of("<gray>Click to grant access").build().decoration(TextDecoration.ITALIC, false)
        ));
        townItem.setItemMeta(townMeta);
        return ItemBuilder.from(townItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            protection.getAccess().put("town:" + town.getName(), "normal");
            BoltUX.getBoltPlugin().saveProtection(protection);
            townItem.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
            townItem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            townMeta.lore(List.of(
                ColorParser.of("<gray>Town").build().decoration(TextDecoration.ITALIC, false),
                ColorParser.of("<green>Access has been granted").build().decoration(TextDecoration.ITALIC, false)

            ));
            townItem.setItemMeta(townMeta);
            gui.updateItem(slot, townItem);
        });
    }

    public static GuiItem townToRemovableTrustIcon(PaginatedGui gui, Player viewer, Town town) {
        ItemStack townItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta townMeta = townItem.getItemMeta();
        townMeta.displayName(ColorParser.of("<green>" + town.getName()).build().decoration(TextDecoration.ITALIC, false));
        townMeta.lore(List.of(
            ColorParser.of("<gray>Town").build().decoration(TextDecoration.ITALIC, false)
        ));
        townItem.setItemMeta(townMeta);
        return ItemBuilder.from(townItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
            store.loadAccessList(viewer.getUniqueId()).thenAccept(accessList -> {
                accessList.getAccess().remove("town:" + town.getName());
                store.saveAccessList(accessList);
                townItem.setAmount(0);
                gui.updateItem(slot, townItem);
            });
        });
    }

    public static GuiItem townToAddableTrustIcon(PaginatedGui gui, Player viewer, Town town) {
        ItemStack townItem = new ItemStack(Material.ENDER_CHEST);
        ItemMeta townMeta = townItem.getItemMeta();
        townMeta.displayName(ColorParser.of("<green>" + town.getName()).build().decoration(TextDecoration.ITALIC, false));
        townMeta.lore(List.of(
            ColorParser.of("<gray>Town").build().decoration(TextDecoration.ITALIC, false)
        ));
        townItem.setItemMeta(townMeta);
        return ItemBuilder.from(townItem).asGuiItem(event -> {
            final int slot = event.getSlot();
            final Store store = BoltUX.getBoltPlugin().getBolt().getStore();
            store.loadAccessList(viewer.getUniqueId()).thenAccept(accessList -> {
                accessList.getAccess().put("town:" + town.getName(), "normal");
                store.saveAccessList(accessList);
                townItem.addUnsafeEnchantment(Enchantment.BINDING_CURSE, 1);
                townItem.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                townMeta.lore(List.of(
                    ColorParser.of("<gray>Town").build().decoration(TextDecoration.ITALIC, false),
                    ColorParser.of("<green>Trust has been granted").build().decoration(TextDecoration.ITALIC, false)
                ));
                townItem.setItemMeta(townMeta);
                gui.updateItem(slot, townItem);
            });
        });
    }

}
