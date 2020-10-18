package fr.xen0xys.minedventure.plugin.events;

import fr.xen0xys.minedventure.databases.AccountsDatabase;
import fr.xen0xys.minedventure.databases.TempBanDatabase;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class OnAsyncPlayerPreLogin implements Listener {
    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent e){
        String minecraft_name = e.getName();
        if(!new AccountsDatabase().isMinecraftUserExist(minecraft_name) || !new TempBanDatabase().isPlayerTempBan(minecraft_name)){
            e.setKickMessage(ChatColor.RED + "Vous n'êtes pas autorisé à vous connecter!");
            e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST);
        }
    }
}
