package fr.xen0xys.minedventure.plugin.commands;

import fr.xen0xys.minedventure.databases.TempBanDatabase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ExtendKillerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player){
            if(strings.length >= 1){
                if(new TempBanDatabase().extendBan(strings[0], commandSender.getName())){
                    commandSender.sendMessage(ChatColor.GREEN + "L'opération a bien été effectué");
                }else{
                    commandSender.sendMessage(ChatColor.RED + "Une erreur est survenue durant l'opération");
                }
            }
        }
        return false;
    }
}
