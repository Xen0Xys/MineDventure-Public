package fr.xen0xys.minedventure.models;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class CustomScoreboardManager {

    private Scoreboard board;

    public CustomScoreboardManager(){
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        if(manager != null){
            this.board = manager.getMainScoreboard();

            if(this.board.getObjective("showhealth") == null){
                Objective objective = this.board.registerNewObjective("showhealth", Criterias.HEALTH, ChatColor.DARK_RED + "❤");
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            }

            if(this.board.getObjective("deaths") == null){
                Objective objective2 = this.board.registerNewObjective("deaths", Criterias.DEATHS);
                objective2.setDisplaySlot(DisplaySlot.PLAYER_LIST);
            }
        }else{
            System.out.println(ChatColor.RED + "An error occurred when loading scoreboards");
        }
    }

    public void addPlayer(Player player){
        player.setScoreboard(board);
        player.setHealth(player.getHealth());
    }
}
