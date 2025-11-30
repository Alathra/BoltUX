package io.github.alathra.boltux.hook.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.hook.AbstractHook;
import io.github.alathra.boltux.hook.Hook;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import me.tofaa.entitylib.APIConfig;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.spigot.SpigotEntityLibPlatform;

/**
 * A hook that enables API for PacketEvents.
 */
public final class PacketEventsHook extends AbstractHook {
    /**
     * Instantiates a new PacketEvents hook.
     *
     * @param plugin the plugin instance
     */
    public PacketEventsHook(BoltUX plugin) {
        super(plugin);
    }

    @Override
    public void onLoad(BoltUX plugin) {
        if (!isPluginPresent(Hook.PacketEvents.getPluginName()))
            return;

        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(getPlugin()));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable(BoltUX plugin) {
        if (!isPluginEnabled(Hook.PacketEvents.getPluginName()))
            return;

        PacketEvents.getAPI().init();

        // EntityLib initialization
        SpigotEntityLibPlatform platform = new SpigotEntityLibPlatform(plugin);
        APIConfig settings = new APIConfig(PacketEvents.getAPI())
            .tickTickables()
            .usePlatformLogger();
        EntityLib.init(platform, settings);
    }

    @Override
    public void onDisable(BoltUX plugin) {
        if (!isPluginEnabled(Hook.PacketEvents.getPluginName()))
            return;

        PacketEvents.getAPI().terminate();
    }

    @Override
    public boolean isHookLoaded() {
        return isPluginPresent(Hook.PacketEvents.getPluginName()) && PacketEvents.getAPI().isLoaded();
    }
}
