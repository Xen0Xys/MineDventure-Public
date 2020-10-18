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

public class CreativeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player){
            User user = MineDventure.getUsers().get(commandSender.getName());
            Player player = (Player) commandSender;
            if(user.isInSurvival()){
                Location survival_location = player.getLocation();
                user.setSurvivalLocation(survival_location);
                user.setInSurvival(false);
                player.teleport(new Location(Bukkit.getWorld("world_creative"), 0, 4, 0));
                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage(ChatColor.GREEN + "Vous venez d'être téléporté dans le monde créatif");
            }else{
                commandSender.sendMessage(ChatColor.RED + "Vous êtes déjà dans le monde creatif");
            }
        }else{
            commandSender.sendMessage(ChatColor.RED + "This command should be executed by a player!");
        }
        return false;
    }
}
