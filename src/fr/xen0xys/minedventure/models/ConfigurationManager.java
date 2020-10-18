package fr.xen0xys.minedventure.models;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("all")
public class ConfigurationManager {

    private final FileConfiguration config;

    public ConfigurationManager(JavaPlugin plugin){
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public long getMainDiscordPluginCategoryId() {
        return (long) this.config.get("bot.main_discord_plugin_category_id");
    }

    public long getMainGuildId() {
        return (long) this.config.get("bot.main_guild_id");
    }

    public long getReactMessageId() {
        return (long) this.config.get("bot.react_message_id");
    }

    public long getMainDiscordPluginChannelId() {
        return (long) this.config.get("bot.main_discord_plugin_channel_id");
    }

    public String getBotToken(){
        return (String) this.config.get("bot.token");
    }

    public String getHost(){
        return (String) this.config.get("database.host");
    }
    public int getPort(){
        return (int) this.config.get("database.port");
    }
    public String getDatabase(){
        return (String) this.config.get("database.database");
    }
    public String getUser(){
        return (String) this.config.get("database.user");
    }
    public String getPassword(){
        return (String) this.config.get("database.password");
    }


}
