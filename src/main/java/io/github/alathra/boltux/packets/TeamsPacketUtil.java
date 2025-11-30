package io.github.alathra.boltux.packets;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

/**
 * A class for managing/interacting with scoreboard teams using PacketEvents.
 *
 * @author darksaid98
 */
final class TeamsPacketUtil {
    /**
     * Generates a team name based on the team color
     *
     * @param color the team color
     * @return team name
     */
    private static String getTeamName(NamedTextColor color) {
        return "boltux_color_" + color.toString();
    }

    /**
     * Generates a team-info object based on the team color
     *
     * @param color the team color
     * @return team info
     */
    private static WrapperPlayServerTeams.ScoreBoardTeamInfo getTeamInfo(NamedTextColor color) {
        return new WrapperPlayServerTeams.ScoreBoardTeamInfo(
            Component.empty(),
            Component.empty(),
            Component.empty(),
            WrapperPlayServerTeams.NameTagVisibility.NEVER,
            WrapperPlayServerTeams.CollisionRule.NEVER,
            color,
            WrapperPlayServerTeams.OptionData.NONE
        );
    }

    /**
     * Creates a team containing the player and team color.
     *
     * @param player the player
     * @param color  the team color
     * @implNote The player is automatically added to this team.
     */
    public static void createTeam(Player player, NamedTextColor color) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(
            player,
            new WrapperPlayServerTeams(
                getTeamName(color),
                WrapperPlayServerTeams.TeamMode.CREATE,
                getTeamInfo(color),
                List.of(String.valueOf(PacketEvents.getAPI().getPlayerManager().getUser(player).getUUID()))
            )
        );
    }

    /**
     * Removes a team.
     *
     * @param player the player
     * @param color  the team color
     */
    public static void removeTeam(Player player, NamedTextColor color) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(
            player,
            new WrapperPlayServerTeams(
                getTeamName(color),
                WrapperPlayServerTeams.TeamMode.REMOVE,
                getTeamInfo(color)
            )
        );
    }

    /**
     * Adds a collection of entity {@link java.util.UUID}s to the team members list.
     *
     * @param player   the player
     * @param color    the team color
     * @param entities the collection of {@link java.util.UUID}s
     */
    public static void addToTeam(Player player, NamedTextColor color, Collection<String> entities) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(
            player,
            new WrapperPlayServerTeams(
                getTeamName(color),
                WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
                getTeamInfo(color),
                entities
            )
        );
    }

    /**
     * Removes a collection of entity {@link java.util.UUID}s from the team members list.
     *
     * @param player   the player
     * @param color    the team color
     * @param entities the collection of {@link java.util.UUID}s
     */
    public static void removeFromTeam(Player player, NamedTextColor color, Collection<String> entities) {
        PacketEvents.getAPI().getPlayerManager().sendPacket(
            player,
            new WrapperPlayServerTeams(
                getTeamName(color),
                WrapperPlayServerTeams.TeamMode.REMOVE_ENTITIES,
                getTeamInfo(color),
                entities
            )
        );
    }
}
