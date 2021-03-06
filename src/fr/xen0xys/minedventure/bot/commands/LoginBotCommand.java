package fr.xen0xys.minedventure.bot.commands;

import fr.xen0xys.minedventure.MineDventure;
import fr.xen0xys.minedventure.bot.BotUtils;
import fr.xen0xys.minedventure.databases.AccountsDatabase;
import fr.xen0xys.minedventure.databases.SecurityDatabase;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class LoginBotCommand {
    public LoginBotCommand(User user, Message message){
        String[] args = BotUtils.getCommandArgs(message.getContentRaw());
        if(args.length >= 1){
            String minecraft_name = new AccountsDatabase().getMinecraftNameFromDiscordId(user.getIdLong());
            if(minecraft_name != null){
                Player player = Bukkit.getPlayer(minecraft_name);
                if(player != null){
                    if(new SecurityDatabase().checkPassword(minecraft_name, args[0])){
                        MineDventure.getUsers().get(minecraft_name).setIsLogged(true);
                        player.sendMessage(ChatColor.GREEN + "Vous avez été connecté (discord)");
                        BotUtils.sendDM(user, "Connection réussie");
                        return;
                    }
                }
            }
            BotUtils.sendDM(user, "Une erreur est survenue");
            return;
        }else{
            BotUtils.sendDM(user, "Pas assez d'arguments");
        }
    }
}
