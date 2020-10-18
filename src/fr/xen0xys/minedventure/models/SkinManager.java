package fr.xen0xys.minedventure.models;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import skinsrestorer.bukkit.SkinsRestorer;
import skinsrestorer.bukkit.SkinsRestorerBukkitAPI;

public class SkinManager {


    public SkinManager(){
    }

    public String getPlayerSkinURL(Player player){
        String skin_name = player.getName();
        return String.format("https://mc-heads.net/avatar/%s/100", skin_name);
    }
}
