package fr.xen0xys.minedventure.databases;

import fr.xen0xys.minedventure.MineDventure;
import fr.xen0xys.minedventure.utils.PluginUtils;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TempBanDatabase {

    private final String account_table_name = "minedventure_tempbans";
    private final String bank_table_name = "";
    private final DatabaseProvider database_provider;
    private final int ban_time = 3 * 60 * 1000;
    private final int extended_ban_time = 2 * 60 * 1000;

    public TempBanDatabase(){
        database_provider = MineDventure.getDatabaseProvider();
    }

    public TempBanDatabase(DatabaseProvider database_provider) {
        this.database_provider = database_provider;
    }


    public void initializeTableIfNotInitialized(){
        if(!this.database_provider.isTableInitialised(this.account_table_name)){
            this.database_provider.initializeTable(this.account_table_name, "id INT PRIMARY KEY AUTO_INCREMENT, killer_minedventure_account_id INT, victim_minedventure_account_id INT, unban_timestamp BIGINT, is_extended BOOLEAN, FOREIGN KEY (killer_minedventure_account_id) REFERENCES minedventure_accounts(id) ON DELETE CASCADE ON UPDATE NO ACTION, FOREIGN KEY (victim_minedventure_account_id) REFERENCES minedventure_accounts(id) ON DELETE CASCADE ON UPDATE NO ACTION");
        }
    }

    public boolean pardonPlayer(String killer_name, String victim_name){
        if(this.isVictim(killer_name, victim_name)){
            String query = String.format("DROP from %s WHERE killer_minedventure_account_id=%d;", this.account_table_name, new AccountsDatabase().getIdFromMinecraftName(killer_name));
            return this.database_provider.executeUpdateQuery(query);
        }
        return false;
    }

    public boolean banPlayer(String killer_name, String victim_name){
        // Need work
        AccountsDatabase accounts_database = new AccountsDatabase();
        int killer_id = accounts_database.getIdFromMinecraftName(killer_name);
        int victim_id = accounts_database.getIdFromMinecraftName(victim_name);
        if(killer_id != -1 && victim_id != -1){
            try {
                String query = String.format("INSERT INTO %s VALUES (NULL, ?, ?, ?, ?);", this.account_table_name);
                PreparedStatement ps = this.database_provider.getPreparedStatement(query);
                ps.setInt(1, killer_id);
                ps.setInt(2, victim_id);
                ps.setLong(3, PluginUtils.getCurrentTimestamp() + ban_time);
                ps.setBoolean(4, false);
                return this.database_provider.executeUpdatePreparedStatement(ps);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public long getBanTimestamps(String minecraft_name){
        int killer_id = new AccountsDatabase().getIdFromMinecraftName(minecraft_name);
        if(killer_id != -1){
            try {
                String query = String.format("SELECT unban_timestamp FROM %s WHERE killer_minedventure_account_id=%d;", this.account_table_name, killer_id);
                ResultSet rs = this.database_provider.executeQuery(query);
                if(rs.next()){
                    return rs.getLong("unban_timestamp");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public boolean isPlayerTempBan(String killer_name){
        // Add timestamps check
        try {
            String query = String.format("SELECT id FROM %s WHERE killer_minedventure_account_id=%d", this.account_table_name, new AccountsDatabase().getIdFromMinecraftName(killer_name));
            ResultSet rs = this.database_provider.executeQuery(query);
            if(rs.next()){
                long timestamps = this.getBanTimestamps(killer_name);
                if(timestamps <= PluginUtils.getCurrentTimestamp()){
                    String query2 = String.format("DELETE FROM %s WHERE killer_minedventure_account_id=%d;", this.account_table_name, new AccountsDatabase().getIdFromMinecraftName(killer_name));
                    this.database_provider.executeUpdateQuery(query2);
                    return true;
                }else{
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public boolean isVictim(String killer_name, String victim_name){
        AccountsDatabase accounts_database = new AccountsDatabase();
        int killer_id = accounts_database.getIdFromMinecraftName(killer_name);
        int victim_id = accounts_database.getIdFromMinecraftName(victim_name);
        if(killer_id != -1 && victim_id != -1 && this.isPlayerTempBan(killer_name)){
            String query = String.format("SELECT id FROM %s WHERE killer_minedventure_accounts_id=%d AND victim_minedventure_account_id=%d", this.account_table_name, killer_id, victim_id);
            ResultSet rs = this.database_provider.executeQuery(query);
            try {
                return rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean extendBan(String killer_name, String victim_name){
        int killer_id = new AccountsDatabase().getIdFromMinecraftName(killer_name);
        if(killer_id != -1 && this.isPlayerTempBan(killer_name)){
            long timestamps = this.getBanTimestamps(killer_name);
            if(timestamps != -1){
                String query = String.format("UPDATE %s SET unban_timestamp=%o WHERE killer_minedventure_account_id=%d", this.account_table_name, timestamps + this.extended_ban_time, killer_id);
                this.database_provider.executeUpdateQuery(query);
                return true;
            }
        }
        return false;
    }
}
