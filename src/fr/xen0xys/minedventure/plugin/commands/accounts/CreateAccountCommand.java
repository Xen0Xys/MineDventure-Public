package fr.xen0xys.minedventure.plugin.commands.accounts;

import fr.xen0xys.minedventure.databases.AccountsDatabase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CreateAccountCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(!(commandSender instanceof Player)){
            if(strings.length >= 4){
                if(strings[2].equals(strings[3])){
                    if(executeCreateAccount(Long.parseLong(strings[0]), strings[1], strings[2])){
                        commandSender.sendMessage(ChatColor.GREEN + "Le compte à été crée avec succès");
                    }else{
                        commandSender.sendMessage(ChatColor.RED + "Le compte n'a pas pu être créer");
                    }
                }else{
                    commandSender.sendMessage(ChatColor.RED + "Les 2 mots de passe doivent être identiques");
                }
            }else{
                commandSender.sendMessage(ChatColor.RED + "Utilisation: /createaccount <id discord> <pseudo minecraft> <mot de passe> <confirmation mot de passe>");
            }
        }
        return false;
    }

    public static boolean executeCreateAccount(long discord_id, String minecraft_name, String password){
        return new AccountsDatabase().createPlayerAccount(discord_id, minecraft_name, password);
    }
}