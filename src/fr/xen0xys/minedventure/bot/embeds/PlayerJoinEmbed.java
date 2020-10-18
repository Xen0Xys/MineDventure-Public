package fr.xen0xys.minedventure.bot.embeds;

import fr.xen0xys.minedventure.MineDventure;
import net.dv8tion.jda.api.EmbedBuilder;
import org.bukkit.entity.Player;

import java.awt.*;

public class PlayerJoinEmbed extends EmbedBuilder implements CustomEmbed{
    public PlayerJoinEmbed(Player player){
        // this.setTitle(String.format("%s vient de se connecter!", player.getName()));
        this.setColor(Color.GREEN);
        System.out.println(MineDventure.getSkinManager().getPlayerSkinURL(player));
        this.setAuthor(String.format("%s vient de se connecter!", player.getName()), null, MineDventure.getSkinManager().getPlayerSkinURL(player));
    }
}
