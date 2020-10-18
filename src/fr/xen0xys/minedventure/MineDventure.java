package fr.xen0xys.minedventure;

import fr.xen0xys.minedventure.bot.botevents.OnMessageReceived;
import fr.xen0xys.minedventure.bot.botevents.OnPrivateMessageReceived;
import fr.xen0xys.minedventure.bot.botevents.OnReady;
import fr.xen0xys.minedventure.databases.AccountsDatabase;
import fr.xen0xys.minedventure.databases.DatabaseProvider;
import fr.xen0xys.minedventure.economy.CustomEconomyProvider;
import fr.xen0xys.minedventure.models.ConfigurationManager;
import fr.xen0xys.minedventure.models.CustomScoreboardManager;
import fr.xen0xys.minedventure.models.SkinManager;
import fr.xen0xys.minedventure.models.User;
import fr.xen0xys.minedventure.plugin.commands.economy.AddMoneyCommand;
import fr.xen0xys.minedventure.plugin.commands.economy.MoneyCommand;
import fr.xen0xys.minedventure.plugin.commands.accounts.*;
import fr.xen0xys.minedventure.plugin.commands.economy.PayCommand;
import fr.xen0xys.minedventure.plugin.commands.economy.SetMoneyCommand;
import fr.xen0xys.minedventure.plugin.commands.worlds.CreativeCommand;
import fr.xen0xys.minedventure.plugin.commands.worlds.SurvivalCommand;
import fr.xen0xys.minedventure.plugin.events.*;
import fr.xen0xys.minedventure.bot.BotUtils;
import fr.xen0xys.minedventure.utils.ConsoleFilter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.HashMap;

public class MineDventure extends JavaPlugin {

    private static long main_discord_plugin_channel_id = 757865091315138570L;
    private static long react_message_id = 765551558708035586L;
    private static long main_guild_id = 695313216326205491L;
    private static long main_discord_plugin_category_id = 757865004216352808L;
    private static JDA bot;
    private static SkinManager skin_manager;
    private static Economy economy;
    private static final String log_prefix = "[Mine'Dventure]:";
    private static DatabaseProvider database_provider;
    private static final HashMap<String, User> USERS = new HashMap<>();
    private static final PluginAsyncLoop plugin_async_loop = new PluginAsyncLoop();
    private static CustomScoreboardManager scoreboard_manager = null;
    private static ConfigurationManager configuration_manager;

    // 1 Diamond = 10K €

    @Override
    public void onLoad() {
        configuration_manager = new ConfigurationManager(this);
        try {
            buildBot();
        } catch (LoginException e) {
            e.printStackTrace();
        }
        Bukkit.getServer().getServicesManager().register(Economy.class, new CustomEconomyProvider(), JavaPlugin.getPlugin(Vault.class), ServicePriority.High); // Register custom economy provider
        if (!setupEconomy() ) {
            System.out.printf("%s%s Economy can't be initialized%n", ChatColor.RED, log_prefix);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }else{
            System.out.printf("%s%s Economy initialized%n", ChatColor.GREEN, log_prefix);
        }
        database_provider = new DatabaseProvider();
        super.onLoad();
    }

    @Override
    public void onDisable() {
        plugin_async_loop.stop();
        stopBot();
        super.onDisable();
    }

    @Override
    public void onEnable() {
        registerEvents();
        registerCommands();
        registerFilters();
        skin_manager = new SkinManager();
        initializeServerAccounts();
        // Temp line
        // testEconomy();
        plugin_async_loop.runTaskAsynchronously(this);
        this.initializeWorlds();
        initializeCreativeWorld();
        scoreboard_manager = new CustomScoreboardManager();
        super.onEnable();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return true;
    }

    private void buildBot() throws LoginException {
        main_discord_plugin_channel_id = configuration_manager.getMainDiscordPluginChannelId();
        react_message_id = configuration_manager.getReactMessageId();
        main_guild_id = configuration_manager.getMainGuildId();
        main_discord_plugin_category_id = configuration_manager.getMainDiscordPluginCategoryId();
        bot = JDABuilder.createDefault(configuration_manager.getBotToken()).build();
        registerBotEvents();
    }
    private void stopBot(){
        bot.getRegisteredListeners().forEach(bot::removeEventListener);
        // bot.cancelRequests();
        // BotUtils.setDiscordServerChannelTopicQueue("Serveur hors-ligne");
        BotUtils.sendMessage(BotUtils.getServerStopMessage());
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        bot.shutdown();
    }

    private void initializeServerAccounts(){
        AccountsDatabase a_database = new AccountsDatabase();
        if(!a_database.isMinecraftUserExist("Server")){
            a_database.createPlayerAccount(0L, "Server", "server_password");
        }
        if(!economy.hasAccount("Server")){
            economy.createPlayerAccount("Server");
        }
    }

    private void registerBotEvents(){
        bot.addEventListener(new OnReady());
        bot.addEventListener(new OnMessageReceived());
        bot.addEventListener(new OnPrivateMessageReceived());
    }

    private void registerEvents(){
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new OnAsyncPlayerChat(), this);
        pm.registerEvents(new OnPlayerQuit(), this);
        pm.registerEvents(new OnPlayerJoin(), this);
        pm.registerEvents(new OnAsyncPlayerPreLogin(), this);
        pm.registerEvents(new OnPlayerMove(), this);
        pm.registerEvents(new OnPlayerCommandPreprocess(), this);
        pm.registerEvents(new OnFoodLevelChange(), this);
        pm.registerEvents(new OnEntityDamaged(), this);
        pm.registerEvents(new OnEntityDamagedByEntity(), this);
        pm.registerEvents(new OnPlayerDropItem(), this);
        pm.registerEvents(new OnPlayerInteract(), this);
        pm.registerEvents(new OnBlockBreak(), this);
        pm.registerEvents(new OnPlayerTeleport(), this);

    }

    @SuppressWarnings("all")
    private void registerCommands(){
        this.getCommand("login").setExecutor(new LoginCommand());
        this.getCommand("logout").setExecutor(new LogoutCommand());
        this.getCommand("forcelogin").setExecutor(new ForceLoginCommand());
        this.getCommand("changepassword").setExecutor(new ChangePasswordCommand());
        this.getCommand("createaccount").setExecutor(new CreateAccountCommand());
        this.getCommand("money").setExecutor(new MoneyCommand());
        this.getCommand("setmoney").setExecutor(new SetMoneyCommand());
        this.getCommand("addmoney").setExecutor(new AddMoneyCommand());
        this.getCommand("pay").setExecutor(new PayCommand());
        this.getCommand("changename").setExecutor(new ChangeNameCommand());
        this.getCommand("creative").setExecutor(new CreativeCommand());
        this.getCommand("survival").setExecutor(new SurvivalCommand());
    }

    @SuppressWarnings("all")
    private void initializeWorlds(){
        this.initializeWorld(Bukkit.getWorld("world"));
        this.initializeWorld(Bukkit.getWorld("world_nether"));
        this.initializeWorld(Bukkit.getWorld("world_the_end"));
    }

    private void initializeWorld(World world){
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.NATURAL_REGENERATION, true);
        world.setDifficulty(Difficulty.HARD);
        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(10000);
    }

    private void initializeCreativeWorld(){
        World world = Bukkit.getWorld("world_creative");
        if(world == null){
            world = Bukkit.createWorld(new WorldCreator("world_creative").type(WorldType.FLAT)); // .generatorSettings("minecraft:bedrock,40*minecraft:stone,5*minecraft:dirt,minecraft:grass_block;minecraft:plains;"));
        }
        assert world != null;
        world.setDifficulty(Difficulty.PEACEFUL);
        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(1000);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setSpawnLocation(new Location(Bukkit.getWorld("world_creative"), 0, 0, 0));
    }

    private void registerFilters(){
        Logger logger = (Logger) LogManager.getRootLogger();
        logger.addFilter(new ConsoleFilter());
    }

    public static long getMainDiscordPluginChannelId(){
        return main_discord_plugin_channel_id;
    }
    public static long getReactMessageId(){
        return react_message_id;
    }
    public static long getMainGuildId(){
        return main_guild_id;
    }
    public static long getMainDiscordPluginCategoryId(){
        return main_discord_plugin_category_id;
    }
    public static HashMap<String, User> getUsers(){
        return USERS;
    }

    public static JDA getBot(){
        return bot;
    }
    public static SkinManager getSkinManager(){
        return skin_manager;
    }
    public static DatabaseProvider getDatabaseProvider(){
        return database_provider;
    }
    public static PluginAsyncLoop getPluginAsyncLoop(){
        return plugin_async_loop;
    }
    public static CustomScoreboardManager getScoreboardManager(){
        return scoreboard_manager;
    }
    public static ConfigurationManager getConfigurationManager(){
        return configuration_manager;
    }
}
