package de.deroq.nicksystem.game.listeners;

import de.deroq.nicksystem.api.NickSystem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author deroq
 * @since 10.07.2022
 */

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.setQuitMessage(null);

        if(!player.isOp()) {
            return;
        }

        NickSystem.getNickAPI().getNickUser(player.getUniqueId()).thenAcceptAsync(nickUser -> {
            if(nickUser == null) {
                return;
            }

            if(nickUser.getNickname() != null) {
                NickSystem.getGameAPI().unnickUser(nickUser, false);
            }
        });
    }
}
