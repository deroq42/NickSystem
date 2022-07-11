package de.deroq.nicksystem.lobby.listeners;

import de.deroq.nicksystem.api.NickSystem;
import de.deroq.nicksystem.lobby.utils.Constants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * @author deroq
 * @since 10.07.2022
 */

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        NickSystem.getNickAPI().createNickUser(
                player.getUniqueId(),
                player.getName());

        player.getInventory().setItem(4, Constants.AUTO_NICK_ITEM);
    }
}
