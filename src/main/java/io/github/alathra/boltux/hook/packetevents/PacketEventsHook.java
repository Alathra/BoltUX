package io.github.alathra.boltux.hook.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.hook.AbstractHook;
import io.github.alathra.boltux.hook.Hook;
import io.github.alathra.boltux.packets.GlowPacketListener;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class PacketEventsHook extends AbstractHook {

    public PacketEventsHook(BoltUX plugin) {
        super(plugin);
    }

    @Override
    public boolean isHookLoaded() {
        return isPluginPresent(Hook.PacketEvents.getPluginName());
    }

    @Override
    public void onLoad(BoltUX plugin) {
        if (!isHookLoaded()) return;
        PacketEvents.getAPI().init();
    }

    @Override
    public void onEnable(BoltUX plugin) {
        if (!isHookLoaded()) return;
        PacketEvents.getAPI().getEventManager().registerListener(new GlowPacketListener(), PacketListenerPriority.NORMAL);
    }

    @Override
    public void onDisable(BoltUX plugin) {
        if (!isHookLoaded()) return;
        PacketEvents.getAPI().terminate();
    }

    public @Nullable ItemStack getLockItem() {
        if (OraxenItems.exists(Settings.getCustomLockItemID())) {
            return (OraxenItems.getItemById(Settings.getCustomLockItemID()).build());
        }
        return null;
    }
}
