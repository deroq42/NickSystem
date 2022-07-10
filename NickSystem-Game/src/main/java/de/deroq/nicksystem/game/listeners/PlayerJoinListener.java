package de.deroq.nicksystem.game.listeners;

import de.deroq.nicksystem.api.NickSystem;
import de.deroq.nicksystem.game.utils.Constants;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(null);

        if(!player.isOp()) {
            return;
        }

        NickSystem.getNickAPI().getNickUser(player.getUniqueId()).thenAcceptAsync(nickUser -> {
            if(nickUser == null) {
                return;
            }

            if(nickUser.isAutoNick()) {
                NickSystem.getGameAPI().nickUser(nickUser);
                player.sendMessage(Constants.PREFIX + "Dein aktueller Nickname: §6" + nickUser.getNickname());
            }
        });
    }
}
