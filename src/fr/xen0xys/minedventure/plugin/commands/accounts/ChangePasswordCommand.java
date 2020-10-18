package fr.xen0xys.minedventure.plugin.commands.accounts;

import fr.xen0xys.minedventure.MineDventure;
import fr.xen0xys.minedventure.databases.SecurityDatabase;
import fr.xen0xys.minedventure.models.User;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ChangePasswordCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player){
            if(strings.length >= 2){
                if(strings[0].equals(strings[1])){
                    executeChangePassword((Player) commandSender, strings[0]);
                }else{
                    commandSender.sendMessage(ChatColor.RED + "Les 2 mots de passe doivent être identiques");
                }
            }else{
                commandSender.sendMessage(ChatColor.RED + "Utilisation: /changepassword <mot de passe> <mot de passe>");
            }
        }else{
            System.out.println(ChatColor.RED + "Cette commande doit être entré en tant que joueur");
        }
        return false;
    }

    public static void executeChangePassword(Player player, String new_password){
        String minecraft_name = player.getName();
        User user = MineDventure.getUsers().get(minecraft_name);
        if(user.isLogged()){
            if(new SecurityDatabase().setPassword(minecraft_name, new_password)){
                player.sendMessage(ChatColor.GREEN + "Votre mot de passe à été changé avec succès");
            }else{
                player.sendMessage(ChatColor.RED + "Une erreur est survenue lors du changement de votre mot de passe");
            }
        }else{
            player.kickPlayer(ChatColor.RED + "Vous n'avez pas le droit d'executer cette commande sans être connecter");
        }
    }
}