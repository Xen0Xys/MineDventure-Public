package fr.xen0xys.minedventure.utils;

import com.google.common.hash.Hashing;
import net.minecraft.server.v1_16_R2.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public abstract class PluginUtils {
    public static String getPlayerIP(Player player){
        return player.getAddress().getAddress().toString().replace("/", "");
    }

    public static long getCurrentTimestamp(){
        return new Timestamp(System.currentTimeMillis()).getTime();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static String encryptPassword(String password){
        return Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
    }

    public static String getDoubleFormat(double value){
        String suffix = "";
        if(value >= 1000000000){
            suffix = "Md";
            value = value / 1000000000;
        }
        else if(value >= 1000000){
            suffix = "M";
            value = value / 1000000;
        }
        else if(value >= 1000){
            suffix = "K";
            value = value / 1000;
        }
        NumberFormat formatter = new DecimalFormat("#0.00");
        return String.format("%s%s", formatter.format(value), suffix);
    }

    public static int getTps(){
        return (int) MinecraftServer.getServer().recentTps[0];
    }
}
