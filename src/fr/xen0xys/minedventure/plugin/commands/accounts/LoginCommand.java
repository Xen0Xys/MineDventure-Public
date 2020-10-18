package fr.xen0xys.minedventure.plugin.commands.accounts;

import fr.xen0xys.minedventure.MineDventure;
import fr.xen0xys.minedventure.databases.SecurityDatabase;
import fr.xen0xys.minedventure.models.User;
import fr.xen0xys.minedventure.utils.PluginUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LoginCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player){
            if(strings.length >= 1){
                executeLogin((Player) commandSender, strings[0]);
                return true;
            }else{
                commandSender.sendMessage(ChatColor.RED + "Pas assez d'arguments: /login <mot de passe>");
            }
        }else{
            System.out.println(ChatColor.RED + "You need to execute this command as player");
        }
        return false;
    }

    public static void executeLogin(Player player, String password){
        String minecraft_name = player.getName();
        User user = MineDventure.getUsers().get(minecraft_name);
        String ip = PluginUtils.getPlayerIP(player);
        SecurityDatabase s_database = new SecurityDatabase();
        if(!user.isLogged()){
            if(s_database.checkPassword(minecraft_name, password)){
                if(s_database.loginUser(minecraft_name, ip)){
                    player.sendMessage(ChatColor.GREEN + "Vous êtes connecté avec succès");
                }else{
                    player.sendMessage(ChatColor.GOLD + "Vous êtes connecté mais une erreur est survenue");
                }
                user.setIsLogged(true);
            }else{
                player.kickPlayer(ChatColor.RED + "Mot de passe incorrect");
            }
        }else{
            player.sendMessage(ChatColor.RED + "Vous êtes déjà connecté");
        }
    }


}