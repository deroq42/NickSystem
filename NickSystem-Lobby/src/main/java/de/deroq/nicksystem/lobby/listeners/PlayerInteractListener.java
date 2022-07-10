package de.deroq.nicksystem.lobby.listeners;

import de.deroq.nicksystem.api.NickSystem;
import de.deroq.nicksystem.lobby.utils.Constants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (event.getItem().isSimilar(Constants.AUTO_NICK_ITEM)) {
                NickSystem.getNickAPI().getNickUser(player.getUniqueId()).thenAcceptAsync(nickUser -> {
                    boolean autoNick = !nickUser.isAutoNick();
                    NickSystem.getLobbyAPI().setAutoNick(nickUser, autoNick);
                    player.sendMessage(Constants.PREFIX + "Der automatische Nickname ist nun " + (autoNick ? "§aAktiviert" : "§cDeaktiviert"));
                });
            }
        }
    }
}
