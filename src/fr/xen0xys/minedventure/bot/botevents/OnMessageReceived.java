package fr.xen0xys.minedventure.bot.botevents;

import fr.xen0xys.minedventure.bot.BotUtils;
import fr.xen0xys.minedventure.MineDventure;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class OnMessageReceived extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        super.onMessageReceived(event);
        if(!event.getAuthor().getId().equals(event.getJDA().getSelfUser().getId())){
            if(event.getChannelType() == ChannelType.TEXT){
                TextChannel channel = (TextChannel) event.getChannel();
                if(Long.parseLong(channel.getId()) == MineDventure.getMainDiscordPluginChannelId()){
                    BotUtils.sendMessage("You speak!");
                }
            }
        }
    }
}
