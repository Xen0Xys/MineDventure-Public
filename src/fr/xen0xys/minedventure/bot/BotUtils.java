package fr.xen0xys.minedventure.bot;

import fr.xen0xys.minedventure.databases.AccountsDatabase;
import fr.xen0xys.minedventure.MineDventure;
import fr.xen0xys.minedventure.bot.embeds.CustomEmbed;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class BotUtils {
    public static void sendMessage(String message){
        TextChannel channel = MineDventure.getBot().getTextChannelById(MineDventure.getMainDiscordPluginChannelId());
        if(channel != null){
            channel.sendTyping().complete();
            channel.sendMessage(message).complete();
        }
    }

    public static void sendEmbed(CustomEmbed embed){
        TextChannel channel = MineDventure.getBot().getTextChannelById(MineDventure.getMainDiscordPluginChannelId());
        if(channel != null){
            channel.sendTyping().complete();
            channel.sendMessage(embed.build()).complete();
        }
    }

    public static String getServerStartMessage(){
        return ":white_check_mark: **Le serveur vient de démarer**";
    }
    public static String getServerStopMessage(){
        return ":octagonal_sign: **Le serveur vient de s'arrêter**";
    }

    public static List<User> getUserWhoReact(){
        Message message = retrieveMessageFromId(MineDventure.getMainDiscordPluginCategoryId(), MineDventure.getReactMessageId());
        if(message != null){
            MessageReaction mr = new MessageReaction(message.getTextChannel(), MessageReaction.ReactionEmote.fromUnicode("✅", message.getJDA()), message.getIdLong(), false, 1000);
            return mr.retrieveUsers().complete();
        }
        return null;
    }

    public static Message retrieveMessageFromId(long category_id, long id){
        Guild guild = MineDventure.getBot().getGuildById(MineDventure.getMainGuildId());
        if(guild == null){
            System.out.println("Guild is null");
            return null;
        }
        Category category = guild.getCategoryById(category_id);
        if(category == null){
            System.out.println("Category is null");
            return null;
        }
        for(TextChannel channel: category.getTextChannels()){
            try{
                return channel.retrieveMessageById(id).complete();
            } catch(ErrorResponseException ignored){}
        }
        return null;
    }

    public static Member getMemberFromUser(User user){
        Guild guild = MineDventure.getBot().getGuildById(MineDventure.getMainGuildId());
        if(guild != null){
            return guild.getMemberById(user.getIdLong());
        }
        return null;
    }

    public static void sendDM(User user, String message){
        if(user.getIdLong() != MineDventure.getBot().getSelfUser().getIdLong()){
            PrivateChannel channel = user.openPrivateChannel().complete();
            if(channel != null){
                channel.sendMessage(message).queue();
            }
        }
    }
    public static void sendDM(User user, CustomEmbed embed){
        if(user.getIdLong() != MineDventure.getBot().getSelfUser().getIdLong()){
            PrivateChannel channel = user.openPrivateChannel().complete();
            if(channel != null){
                channel.sendMessage(embed.build()).queue();
            }
        }
    }

    public static void initializeNoInitializedUsers(List<User> users){
        AccountsDatabase bot_database = new AccountsDatabase();
        for(User user: new ArrayList<>(users)){
            if(!bot_database.isDiscordUserExist(user.getIdLong())){
                sendDM(user, "Vous avez accepté le règlement, vous pouvez maintenant créer votre compte en envoyant la commande **/createaccount <pseudo MC> <mot de passe> <confirmation mot de passe>**.");
            }
        }
    }

    public static TextChannel getDiscordServerChannel(){
        return MineDventure.getBot().getTextChannelById(MineDventure.getMainDiscordPluginChannelId());
    }

    public static void setDiscordServerChannelTopic(String topic){
        setDiscordServerChannelTopicComplete(topic);
    }

    public static void setDiscordServerChannelTopicComplete(String topic){
        try {
            BotUtils.getDiscordServerChannel().getManager().setTopic(topic).complete(false);
        } catch (RateLimitedException e) {
            e.printStackTrace();
        }
    }

    public static void setDiscordServerChannelTopicQueue(String topic){
        BotUtils.getDiscordServerChannel().getManager().setTopic(topic).queue();
    }

    public static String[] getCommandArgs(String command){
        String[] args = command.split(" ").clone();
        if(args.length >= 1) {
            return Arrays.copyOfRange(args, 1, args.length);
        }
        return new String[]{};
    }

}
