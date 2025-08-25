package io.github.alathra.boltux.packets;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.UserLoginEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

/**
 * Creates all scoreboard teams used by the plugin for each player on login.
 * @author darksaid98
 */
public class TeamsPacketListener implements PacketListener {
    @Override
    public void onUserLogin(UserLoginEvent event) {
        final Player player = event.getPlayer();

        // Pre-create teams used by the plugin
        TeamsPacketUtil.createTeam(player, NamedTextColor.GREEN);
        TeamsPacketUtil.createTeam(player, NamedTextColor.RED);
    }
}
