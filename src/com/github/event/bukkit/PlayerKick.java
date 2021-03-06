package com.github.event.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;

import com.github.Ablockalypse;
import com.github.DataContainer;
import com.github.aspect.entity.ZAPlayer;
import com.github.aspect.intelligent.Game;
import com.github.event.PlayerLeaveGameEvent;

public class PlayerKick implements Listener {
    private DataContainer data = Ablockalypse.getData();

    /* Called when a player is kicked from the game.
     * Used mostly to prevent multiple level gains after a player is kicked. */
    @EventHandler(priority = EventPriority.HIGHEST) public void PKE(PlayerKickEvent event) {
        Player p = event.getPlayer();
        if (data.isZAPlayer(p)) {
            ZAPlayer zap = data.getZAPlayer(p);
            Game zag = zap.getGame();
            PlayerLeaveGameEvent GPLE = new PlayerLeaveGameEvent(zap, zag);
            Bukkit.getPluginManager().callEvent(GPLE);
            if (!GPLE.isCancelled()) {
                zag.removePlayer(p);
                zap.remove();
            }
        }
    }
}
