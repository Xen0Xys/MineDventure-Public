package fr.xen0xys.minedventure.plugin.commands.economy;

import fr.xen0xys.minedventure.databases.EconomyDatabase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class SetMoneyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length >= 2){
            if(executeSetMoney(strings[0], Integer.parseInt(strings[1]))){
                commandSender.sendMessage(ChatColor.GREEN + "Transaction has been executed successful");
            }else{
                commandSender.sendMessage(ChatColor.RED + "Error occurred in transaction");
            }
        }else{
            commandSender.sendMessage(ChatColor.RED + "Command usage: /setmoney <username> <amount>");
        }
        return false;
    }

    public static boolean executeSetMoney(String minecraft_name, int value){
        EconomyDatabase economy_database = new EconomyDatabase();
        if(economy_database.isPlayerHasMoneyAccount(minecraft_name)){
            return economy_database.setPlayerBalance(minecraft_name, value);
        }
        return false;
    }
}
