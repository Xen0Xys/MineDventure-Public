package fr.xen0xys.minedventure.plugin.commands.economy;

import fr.xen0xys.minedventure.databases.EconomyDatabase;
import fr.xen0xys.minedventure.utils.PluginUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MoneyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player){
            commandSender.sendMessage(String.format(ChatColor.GREEN + "Vous avez %s €", PluginUtils.getDoubleFormat(new EconomyDatabase().getPlayerBalance(commandSender.getName()))));
        }else{
            if(strings.length >= 1){
                commandSender.sendMessage(String.format(ChatColor.GREEN + "Player %s has %s €", strings[0], PluginUtils.getDoubleFormat(new EconomyDatabase().getPlayerBalance(strings[0]))));
            }else{
                commandSender.sendMessage(ChatColor.RED + "Command usage: /money <player name>");
            }
        }
        return false;
    }
}
