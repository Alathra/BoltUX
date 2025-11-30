package io.github.alathra.boltux.hook.towny;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import io.github.alathra.boltux.BoltUX;
import io.github.alathra.boltux.config.Settings;
import io.github.alathra.boltux.data.Permissions;
import io.github.alathra.boltux.hook.AbstractHook;
import io.github.alathra.boltux.hook.Hook;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class TownyHook extends AbstractHook {
    private TownyAPI townyAPI;

    public TownyHook(BoltUX plugin) {
        super(plugin);
    }

    @Override
    public boolean isHookLoaded() {
        return isPluginEnabled(Hook.Towny.getPluginName());
    }

    @Override
    public void onLoad(BoltUX plugin) {
        if (!isHookLoaded()) return;
    }

    @Override
    public void onEnable(BoltUX plugin) {
        if (!isHookLoaded()) return;
        townyAPI = TownyAPI.getInstance();
    }

    @Override
    public void onDisable(BoltUX plugin) {
        if (!isHookLoaded()) return;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean canCreateProtection(boolean state, Player player, Location location) {
        if (!Settings.isLockingDisabledInOtherTowns())
            return state;

        if (townyAPI == null)
            return state;

        final Resident resident = townyAPI.getResident(player);
        if (resident == null)
            return state;

        final Town town = townyAPI.getTown(location);
        if (town == null)
            return state;

        return town.hasResident(resident);
    }

    public boolean canAccessProtection(boolean state, Player player, Location location) {
        if (townyAPI == null)
            return state;

        final Resident resident = townyAPI.getResident(player);
        if (resident == null)
            return state;

        final Town town = townyAPI.getTown(location);
        if (town == null)
            return state;

        if (town.hasResident(resident)) {
            if (town.getMayor().equals(resident) && Settings.canMayorsAccessProtections())
                return true;

            return resident.hasPermissionNode(Permissions.ACCESS_OWN_TOWN_LOCKS_PERMISSION);
        }

        return state;
    }
}
