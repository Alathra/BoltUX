package io.github.alathra.boltux;

import com.github.milkdrinkers.colorparser.ColorParser;
import com.github.retrooper.packetevents.PacketEvents;
import io.github.alathra.boltux.command.CommandHandler;
import io.github.alathra.boltux.config.ConfigHandler;
import io.github.alathra.boltux.crafting.CraftingHandler;
import io.github.alathra.boltux.hook.Hook;
import io.github.alathra.boltux.hook.HookManager;
import io.github.alathra.boltux.listener.ListenerHandler;

import io.github.alathra.boltux.utility.Logger;
import io.github.alathra.boltux.utility.Reloadable;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.popcraft.bolt.BoltPlugin;

import java.util.List;

public class BoltUX extends JavaPlugin {

    private static BoltUX instance;
    private static BoltPlugin boltPlugin;

    // Handlers/Managers
    private ConfigHandler configHandler;
    private CraftingHandler craftingHandler;
    private HookManager hookManager;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;

    // Handlers list (defines order of load/enable/disable)
    private List<? extends Reloadable> handlers;


    public static BoltUX getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        configHandler = new ConfigHandler(instance);
        craftingHandler = new CraftingHandler(instance);
        hookManager = new HookManager(this);
        commandHandler = new CommandHandler(instance);
        listenerHandler = new ListenerHandler(instance);

        handlers = List.of(
            configHandler,
            craftingHandler,
            hookManager,
            commandHandler,
            listenerHandler
        );

        for (Reloadable handler : handlers)
            handler.onLoad(instance);
    }

    @Override
    public void onEnable() {

        // Bolt initialization
        boltPlugin = (BoltPlugin) Bukkit.getServer().getPluginManager().getPlugin("Bolt");

        for (Reloadable handler : handlers)
            handler.onEnable(instance);

        if (Hook.PacketEvents.isLoaded()) {
            // EntityLib initialization
            SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(this);
            APIConfig settings = new APIConfig(PacketEvents.getAPI())
                .tickTickables()
                .usePlatformLogger();
            EntityLib.init(platform, settings);
        } else {
            Logger.get().warn(ColorParser.of("You are running BoltUX but you do not have PacketEvents installed. Plugin functionality will be limited!").build());
        }
    }

    @Override
    public void onDisable() {
        for (Reloadable handler : handlers.reversed())
            handler.onDisable(instance);
    }

    public static BoltPlugin getBoltPlugin() {
        return boltPlugin;
    }

    @NotNull
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }
}
