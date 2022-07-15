package de.deroq.nicksystem.game.commands;

import de.deroq.nicksystem.api.NickSystem;
import de.deroq.nicksystem.game.utils.Constants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author deroq
 * @since 10.07.2022
 */

public class NickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if(!(commandSender instanceof Player)) {
            return true;
        }

        Player player = (Player) commandSender;
        if(!player.isOp()) {
            return true;
        }

        NickSystem.getNickAPI().getNickUser(player.getUniqueId()).thenAcceptAsync(nickUser -> {
            if(nickUser == null) {
                return;
            }

            if(nickUser.getNickname() == null) {
                player.sendMessage(Constants.PREFIX + "Du bist aktuell nicht genickt");
                return;
            }

            NickSystem.getGameAPI().unnickUser(nickUser, true);
            player.sendMessage(Constants.PREFIX + "Du hast dich entnickt");
        });

        return false;
    }
}
