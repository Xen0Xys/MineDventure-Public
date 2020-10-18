package fr.xen0xys.minedventure.plugin.commands.economy;

import fr.xen0xys.minedventure.databases.EconomyDatabase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player){
            if(strings.length >= 2){
                String sender = commandSender.getName();
                String receiver = strings[0];
                double amount = Double.parseDouble(strings[1]);
                if(executePay(sender, receiver, amount)){
                    commandSender.sendMessage(ChatColor.GREEN + "Transaction has been executed successful");
                }else{
                    commandSender.sendMessage(ChatColor.RED + "An error occurred in transaction");
                }
            }else{
                commandSender.sendMessage(ChatColor.RED + "Command usage: /pay <receiver> <amount>");
            }
        }else{
            commandSender.sendMessage(ChatColor.RED + "Command can only be used in game");
        }
        return false;
    }

    public boolean executePay(String sender, String receiver, double amount){
        if(amount > 0){
            EconomyDatabase economy_database = new EconomyDatabase();
            if(economy_database.isPlayerHasMoneyAccount(sender) && economy_database.isPlayerHasMoneyAccount(receiver)){
                while(!economy_database.withdrawPlayer(sender, amount).transactionSuccess());
                while(!economy_database.depositPlayer(receiver, amount).transactionSuccess());
                return true;
            }
        }
        return false;
    }
}
