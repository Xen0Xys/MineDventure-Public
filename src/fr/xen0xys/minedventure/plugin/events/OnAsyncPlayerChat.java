package fr.xen0xys.minedventure.plugin.events;

import fr.xen0xys.minedventure.MineDventure;
import fr.xen0xys.minedventure.bot.BotUtils;
import fr.xen0xys.minedventure.models.User;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class OnAsyncPlayerChat implements Listener {
    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e){
        String author = e.getPlayer().getName();
        User user = MineDventure.getUsers().get(author);
        if(user != null && !user.isLogged()){
            e.getPlayer().sendMessage(ChatColor.RED + "Vous devez vous identifier pour faire cela!");
            e.setCancelled(true);
            return;
        }
        String message = String.format("%s >>> %s", author, e.getMessage());
        BotUtils.sendMessage(message);
    }
}
