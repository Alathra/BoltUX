package io.github.alathra.boltux;

import com.github.milkdrinkers.colorparser.ColorParser;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.alathra.boltux.command.CommandHandler;
import io.github.alathra.boltux.config.ConfigHandler;
import io.github.alathra.boltux.crafting.CraftingHandler;
import io.github.alathra.boltux.hook.*;
import io.github.alathra.boltux.listener.ListenerHandler;
import io.github.alathra.boltux.packets.GlowPacketListener;
import io.github.alathra.boltux.utility.Logger;

import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.popcraft.bolt.BoltPlugin;

public class BoltUX extends JavaPlugin {
    private static BoltUX instance;
    private ConfigHandler configHandler;
    private CraftingHandler craftingHandler;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;

    // Hooks
    private static ItemsAdderHook itemsAdderHook;
    private static NexoHook nexoHook;
    private static OraxenHook oraxenHook;
    private static TownyHook townyHook;
    private static MMOItemsHook mmoItemsHook;

    // Internal
    private static BoltPlugin boltPlugin;

    public static BoltUX getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        configHandler = new ConfigHandler(instance);
        craftingHandler = new CraftingHandler(instance);
        commandHandler = new CommandHandler(instance);
        listenerHandler = new ListenerHandler(instance);
        itemsAdderHook = new ItemsAdderHook(instance);
        nexoHook = new NexoHook(instance);
        oraxenHook = new OraxenHook(instance);
        townyHook = new TownyHook(instance);
        mmoItemsHook = new MMOItemsHook(instance);

        configHandler.onLoad();
        craftingHandler.onLoad();
        commandHandler.onLoad();
        listenerHandler.onLoad();
        itemsAdderHook.onLoad();
        nexoHook.onLoad();
        oraxenHook.onLoad();
        townyHook.onLoad();

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        configHandler.onEnable();
        craftingHandler.onEnable();
        itemsAdderHook.onEnable();
        nexoHook.onEnable();
        oraxenHook.onEnable();
        townyHook.onEnable();

        // PacketEvents + EntityLib initialization
        PacketEvents.getAPI().init();
        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(this);
        APIConfig settings = new APIConfig(PacketEvents.getAPI())
            .tickTickables()
            .usePlatformLogger();
        EntityLib.init(platform, settings);
        PacketEvents.getAPI().getEventManager().registerListener(new GlowPacketListener(), PacketListenerPriority.NORMAL);

        boltPlugin = (BoltPlugin) Bukkit.getServer().getPluginManager().getPlugin("Bolt");

        if (itemsAdderHook.isHookLoaded()) {
            Logger.get().info(ColorParser.of("<green>ItemsAdder has been found on this server. ItemsAdder support enabled.").build());
        }

        if (nexoHook.isHookLoaded()) {
            Logger.get().info(ColorParser.of("<green>Nexo has been found on this server. Nexo support enabled.").build());
        }

        if (oraxenHook.isHookLoaded()) {
            Logger.get().info(ColorParser.of("<green>Oraxen has been found on this server. Oraxen support enabled.").build());
        }

        if (townyHook.isHookLoaded()) {
            Logger.get().info(ColorParser.of("<green>Towny has been found on this server. Towny support enabled.").build());
        }

        commandHandler.onEnable();
        listenerHandler.onEnable();
    }

    @Override
    public void onDisable() {
        configHandler.onDisable();
        craftingHandler.onDisable();
        commandHandler.onDisable();
        listenerHandler.onDisable();
        itemsAdderHook.onDisable();
        nexoHook.onDisable();
        oraxenHook.onDisable();
        townyHook.onDisable();

        PacketEvents.getAPI().terminate();
    }

    public static BoltPlugin getBoltPlugin() {
        return boltPlugin;
    }

    @NotNull
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    @NotNull
    public static ItemsAdderHook getItemsAdderHook() {
        return itemsAdderHook;
    }

    @NotNull
    public static NexoHook getNexoHook() {
        return nexoHook;
    }

    @NotNull
    public static OraxenHook getOraxenHook() {
        return oraxenHook;
    }

    @NotNull
    public static TownyHook getTownyHook() {
        return townyHook;
    }

    @NotNull
    public static MMOItemsHook getMMOItemsHook() {
        return mmoItemsHook;
    }
}
