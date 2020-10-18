package fr.xen0xys.minedventure.plugin.commands.economy;

import fr.xen0xys.minedventure.databases.EconomyDatabase;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class AddMoneyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length >= 2){
            if(executeAddMoney(strings[0], Double.parseDouble(strings[1]))){
                commandSender.sendMessage(ChatColor.GREEN + "Transaction has been executed successful");
            }else{
                commandSender.sendMessage(ChatColor.RED + "Error in transaction");
            }
        }else{
            commandSender.sendMessage(ChatColor.RED + "Command usage: /addmoney <minecraft_name> <amount>");
        }
        return false;
    }

    public static boolean executeAddMoney(String minecraft_name, double amount){
        EconomyDatabase economy_database = new EconomyDatabase();
        if(economy_database.isPlayerHasMoneyAccount(minecraft_name)){
            EconomyResponse reponse = economy_database.depositPlayer(minecraft_name, amount);
            return reponse.transactionSuccess();
        }
        return false;
    }
}
