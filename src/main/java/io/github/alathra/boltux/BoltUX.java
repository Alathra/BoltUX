package io.github.alathra.boltux;

import com.github.milkdrinkers.colorparser.ColorParser;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import io.github.alathra.boltux.command.CommandHandler;
import io.github.alathra.boltux.config.ConfigHandler;
import io.github.alathra.boltux.hook.*;
import io.github.alathra.boltux.listener.ListenerHandler;
import io.github.alathra.boltux.translation.TranslationManager;
import io.github.alathra.boltux.updatechecker.UpdateChecker;
import io.github.alathra.boltux.utility.Logger;

import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.popcraft.bolt.BoltAPI;

/**
 * Main class.
 */
public class BoltUX extends JavaPlugin {
    private static BoltUX instance;
    private ConfigHandler configHandler;
    private TranslationManager translationManager;
    private CommandHandler commandHandler;
    private ListenerHandler listenerHandler;
    private UpdateChecker updateChecker;

    // Hooks
    private static BStatsHook bStatsHook;
    private static VaultHook vaultHook;
    private static PAPIHook papiHook;
    private static ItemsAdderHook itemsAdderHook;
    private static NexoHook nexoHook;
    private static OraxenHook oraxenHook;

    // Internal
    private static BoltAPI boltAPI;
    private static PacketEventsAPI<?> packetEventsAPI;

    /**
     * Gets plugin instance.
     *
     * @return the plugin instance
     */
    public static BoltUX getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        instance = this;
        configHandler = new ConfigHandler(instance);
        translationManager = new TranslationManager(instance);
        commandHandler = new CommandHandler(instance);
        listenerHandler = new ListenerHandler(instance);
        updateChecker = new UpdateChecker();
        bStatsHook = new BStatsHook(instance);
        vaultHook = new VaultHook(instance);
        papiHook = new PAPIHook(instance);
        itemsAdderHook = new ItemsAdderHook(instance);
        nexoHook = new NexoHook(instance);
        oraxenHook = new OraxenHook(instance);

        configHandler.onLoad();
        translationManager.onLoad();
        commandHandler.onLoad();
        listenerHandler.onLoad();
        updateChecker.onLoad();
        bStatsHook.onLoad();
        vaultHook.onLoad();
        papiHook.onLoad();
        itemsAdderHook.onLoad();
        nexoHook.onLoad();
        oraxenHook.onLoad();

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        configHandler.onEnable();
        translationManager.onEnable();
        commandHandler.onEnable();
        listenerHandler.onEnable();
        updateChecker.onEnable();
        bStatsHook.onEnable();
        vaultHook.onEnable();
        papiHook.onEnable();
        itemsAdderHook.onEnable();
        nexoHook.onEnable();
        oraxenHook.onEnable();

        PacketEvents.getAPI().init();
        packetEventsAPI = PacketEvents.getAPI();
        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(this);
        APIConfig settings = new APIConfig(PacketEvents.getAPI())
            .tickTickables()
            .trackPlatformEntities()
            .usePlatformLogger();
        EntityLib.init(platform, settings);

        boltAPI = Bukkit.getServer().getServicesManager().load(BoltAPI.class);

        if (vaultHook.isHookLoaded()) {
            Logger.get().info(ColorParser.of("<green>Vault has been found on this server. Vault support enabled.").build());
        } else {
            Logger.get().warn(ColorParser.of("<yellow>Vault is not installed on this server. Vault support has been disabled.").build());
        }

        if (itemsAdderHook.isHookLoaded()) {
            Logger.get().info(ColorParser.of("<green>ItemsAdder has been found on this server. ItemsAdder support enabled.").build());
        }

        if (nexoHook.isHookLoaded()) {
            Logger.get().info(ColorParser.of("<green>Nexo has been found on this server. Nexo support enabled.").build());
        }

        if (oraxenHook.isHookLoaded()) {
            Logger.get().info(ColorParser.of("<green>Oraxen has been found on this server. Oraxen support enabled.").build());
        }
    }

    @Override
    public void onDisable() {
        configHandler.onDisable();
        translationManager.onDisable();
        commandHandler.onDisable();
        listenerHandler.onDisable();
        updateChecker.onDisable();
        bStatsHook.onDisable();
        vaultHook.onDisable();
        papiHook.onDisable();
        itemsAdderHook.onDisable();
        nexoHook.onDisable();
        oraxenHook.onDisable();

        PacketEvents.getAPI().terminate();
    }

    public BoltAPI getBoltAPI() {
        return boltAPI;
    }

    /**
     * Gets config handler.
     *
     * @return the config handler
     */
    @NotNull
    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    @NotNull
    public TranslationManager getTranslationManager() {
        return translationManager;
    }

    @NotNull
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }

    @NotNull
    public static BStatsHook getBStatsHook() {
        return bStatsHook;
    }

    @NotNull
    public static VaultHook getVaultHook() {
        return vaultHook;
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
}
