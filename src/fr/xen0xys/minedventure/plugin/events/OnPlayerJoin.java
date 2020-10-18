package fr.xen0xys.minedventure.plugin.events;

import fr.xen0xys.minedventure.MineDventure;
import fr.xen0xys.minedventure.bot.BotUtils;
import fr.xen0xys.minedventure.bot.embeds.PlayerJoinEmbed;
import fr.xen0xys.minedventure.databases.SecurityDatabase;
import fr.xen0xys.minedventure.models.User;
import fr.xen0xys.minedventure.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class OnPlayerJoin implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();
        player.setOp(true); // Remove after beta
        player.setGameMode(GameMode.SURVIVAL);
        BotUtils.sendEmbed(new PlayerJoinEmbed(player));
        MineDventure.getUsers().put(player.getName(), new User(player, e.getPlayer().getLocation()));
        User user = MineDventure.getUsers().get(player.getName());
        if(user != null){
            if(new SecurityDatabase().hasSession(player.getName(), PluginUtils.getPlayerIP(player))){
                user.setIsLogged(true);
                player.sendMessage(ChatColor.GREEN + "Vous avez été connecté automatiquement (session)");
            }else{
                player.sendMessage(String.format("%s%sVeuillez vous connecter en effectuant la commande /login <mot de passe>", ChatColor.UNDERLINE, ChatColor.DARK_AQUA));
            }
            if(!player.hasPlayedBefore()){
                Location spawn = new Location(Bukkit.getWorld("world"), 0, 70, 0);
                user.setLoginLocation(spawn);
                player.teleport(spawn);
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 9));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 6000, 9));
            }
        }
        MineDventure.getScoreboardManager().addPlayer(player);
    }
}
