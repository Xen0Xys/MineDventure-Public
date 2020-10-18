package fr.xen0xys.minedventure.plugin.events;

import fr.xen0xys.minedventure.MineDventure;
import fr.xen0xys.minedventure.models.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerQuit implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent e){
        Player player = e.getPlayer();
        User user = MineDventure.getUsers().get(player.getName());
        if(user != null){
            if(!user.isLogged())
                player.teleport(user.getLoginLocation());
            else if(user.isInSurvival()){
                Location survival_location = user.getSurvivalLocation();
                if(survival_location != null){
                    player.teleport(user.getSurvivalLocation());
                }else{
                    player.teleport(new Location(Bukkit.getWorld("world"), 0, 70, 0));
                }
            }
        }

        MineDventure.getUsers().remove(player.getName());
    }
}
