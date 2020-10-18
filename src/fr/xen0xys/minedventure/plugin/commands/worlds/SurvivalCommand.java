package fr.xen0xys.minedventure.plugin.commands.worlds;

import fr.xen0xys.minedventure.MineDventure;
import fr.xen0xys.minedventure.models.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SurvivalCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player){
            User user = MineDventure.getUsers().get(commandSender.getName());
            if(!user.isInSurvival()){
                Location tp_location = user.getSurvivalLocation();
                if(tp_location != null){
                    ((Player) commandSender).teleport(tp_location);
                    commandSender.sendMessage(ChatColor.GREEN + "Vous venez d'être téléporté dans le monde survie");
                }else{
                    ((Player) commandSender).teleport(new Location(Bukkit.getWorld("world"), 0, 70, 0));
                    commandSender.sendMessage(ChatColor.DARK_AQUA + "Vous venez d'être téléporté dans le monde survie mais une erreur est survenue");
                }
                ((Player) commandSender).setGameMode(GameMode.SURVIVAL);
                user.setInSurvival(true);
            }else{
                commandSender.sendMessage(ChatColor.RED + "Vous êtes déjà dans le monde survie");
            }
        }else{
            commandSender.sendMessage(ChatColor.RED + "This command should be executed by a player!");
        }
        return false;
    }
}
