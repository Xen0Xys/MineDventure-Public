package fr.xen0xys.minedventure.plugin.events;

import fr.xen0xys.minedventure.MineDventure;
import fr.xen0xys.minedventure.models.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class OnBlockBreak implements Listener {
    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        User user = MineDventure.getUsers().get(e.getPlayer().getName());
        if(user != null && !user.isLogged()){
            e.setCancelled(true);
        }
    }
}
