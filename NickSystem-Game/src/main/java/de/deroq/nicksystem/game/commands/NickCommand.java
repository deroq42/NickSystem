package de.deroq.nicksystem.game.commands;

import de.deroq.nicksystem.api.NickSystem;
import de.deroq.nicksystem.game.utils.Constants;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NickCommand extends Command {

    public NickCommand(String name) {
        super(name);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] args) {
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