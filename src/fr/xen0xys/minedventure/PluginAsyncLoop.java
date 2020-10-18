package fr.xen0xys.minedventure;

import fr.xen0xys.minedventure.bot.BotUtils;
import fr.xen0xys.minedventure.databases.AccountsDatabase;
import fr.xen0xys.minedventure.databases.EconomyLoggingDatabase;
import fr.xen0xys.minedventure.models.EconomyLog;
import fr.xen0xys.minedventure.utils.PluginUtils;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.managers.ChannelManager;
import net.minecraft.server.v1_16_R2.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PluginAsyncLoop extends BukkitRunnable {

    private boolean is_running = true;
    private final List<EconomyLog> economy_logs = new ArrayList<>();
    private final List<User> discord_users = new ArrayList<>();
    private int round = 0;

    public void addLog(EconomyLog log){
        economy_logs.add(log);
    }

    private void sendLogs(){
        if(this.economy_logs.size() > 0){
            System.out.println("Sending logs");
            EconomyLoggingDatabase economy_logging_database = new EconomyLoggingDatabase();
            this.economy_logs.removeIf(economy_logging_database::uploadLog);
            System.out.println("All logs sent successful");
        }
    }

    private void sendAccountMessageToUnsendUsers(){
        List<User> new_discord_users = BotUtils.getUserWhoReact();
        AccountsDatabase accounts_database = new AccountsDatabase();
        if(new_discord_users != null){
            for(User user: new_discord_users){
                if(!this.discord_users.contains(user)){
                    if(!accounts_database.isDiscordUserExist(user.getIdLong())){
                        BotUtils.sendDM(user, "Vous avez accepté le règlement, vous pouvez maintenant créer votre compte en envoyant la commande **/createaccount <pseudo MC> <mot de passe> <confirmation mot de passe>**.");
                    }
                    this.discord_users.add(user);
                }
            }
        }
    }

    private void actualizeTps(){
        BotUtils.setDiscordServerChannelTopic(String.format("%d/%d joueurs connecté | Tps: %d", Bukkit.getServer().getOnlinePlayers().size(), Bukkit.getServer().getMaxPlayers(), PluginUtils.getTps()));
        // System.out.println("Current TPS: " + PluginUtils.getTps());
    }

    @Override
    public void run() {
        round = 0;
        while(is_running){
            // Code here
            sendLogs();
            sendAccountMessageToUnsendUsers();
            if(round % 60 == 0){
                actualizeTps();
            }
            this.custom_wait();
            if(round < Integer.MAX_VALUE){
                round++;
            }else{
                round = 0;
            }
        }
        round = -1;
    }

    private void custom_wait(){
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        this.is_running = false;
        this.cancel();
    }
}
