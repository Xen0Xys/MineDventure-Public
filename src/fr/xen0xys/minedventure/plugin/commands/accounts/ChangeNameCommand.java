package fr.xen0xys.minedventure.plugin.commands.accounts;

import fr.xen0xys.minedventure.databases.AccountsDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.UUID;

public class ChangeNameCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player){
            if(strings.length >= 2){
                if(strings[0].equals(strings[1])){
                    executeChangeName(commandSender.getName(), strings[0]);
                }
            }
        }else{
            if(strings.length >= 3){
                if(strings[1].equals(strings[2])){
                    if(executeChangeName(strings[0], strings[1])){
                        commandSender.sendMessage(ChatColor.GREEN + "Player username has been changed successful");
                    }else{
                        commandSender.sendMessage(ChatColor.RED + "An error has occurred when changing player username");
                    }
                }else{
                    commandSender.sendMessage(ChatColor.RED + "Command usage: /changename <target player> <new name> <confirm new_name>");
                }
            }else{
                commandSender.sendMessage(ChatColor.RED + "Command usage: /changename <target player> <new name> <confirm new_name>");
            }
        }
        return false;
    }

    public static boolean executeChangeName(String minecraft_name, String new_minecraft_name){
        AccountsDatabase accounts_database = new AccountsDatabase();
        Player player = Bukkit.getPlayer(minecraft_name);
        if(player != null){
            player.kickPlayer(ChatColor.AQUA + "Votre nom d'utilisateur est entrain d'être change, en cas de refus de connection avec votre nom d'utilisateur, un erreur est alors survenue, veuillez contacter un admin!");
        }
        UUID old_id = Bukkit.getOfflinePlayer(minecraft_name).getUniqueId();
        UUID new_uuid = Bukkit.getOfflinePlayer(new_minecraft_name).getUniqueId();
        if(accounts_database.changeUsername(minecraft_name, new_minecraft_name)){
            File file = new File("world/playerdata/" + old_id + ".dat");
            File file2 = new File("world/playerdata/" + new_uuid + ".dat");
            if(!file.renameTo(file2)){
                accounts_database.changeUsername(new_minecraft_name, minecraft_name);
                return false;
            }
            return true;
        }
        return false;
    }
}
