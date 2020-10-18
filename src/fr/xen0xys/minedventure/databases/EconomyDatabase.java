package fr.xen0xys.minedventure.databases;

import fr.xen0xys.minedventure.MineDventure;
import fr.xen0xys.minedventure.models.EconomyLog;
import fr.xen0xys.minedventure.models.TransactionType;
import fr.xen0xys.minedventure.utils.PluginUtils;
import net.milkbowl.vault.economy.EconomyResponse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EconomyDatabase {

    private final String account_table_name = "minedventure_economy_accounts";
    private final String bank_table_name = "";
    private final DatabaseProvider database_provider;

    public EconomyDatabase(){
        database_provider = MineDventure.getDatabaseProvider();
    }

    public EconomyDatabase(DatabaseProvider database_provider) {
        this.database_provider = database_provider;
    }


    public void initializeTableIfNotInitialized(){
        if(!this.database_provider.isTableInitialised(this.account_table_name)){
            this.database_provider.initializeTable(this.account_table_name, "id INT PRIMARY KEY AUTO_INCREMENT, minedventure_account_id INT, balance DOUBLE, FOREIGN KEY (minedventure_account_id) REFERENCES minedventure_accounts(id) ON DELETE CASCADE ON UPDATE NO ACTION");
        }
    }

    private boolean initializePlayerMoneyAccountIfNotHas(String minecraft_name){
        if(!this.isPlayerHasMoneyAccount(minecraft_name)){
            return this.createPlayerAccount(minecraft_name);
        }
        return true;
    }

    public boolean createPlayerAccount(String player_name){
        if(new AccountsDatabase().isMinecraftUserExist(player_name)){
            if(!this.isPlayerHasMoneyAccount(player_name)){
                int id = new AccountsDatabase().getIdFromMinecraftName(player_name);
                if(id != -1){
                    String query = String.format("INSERT INTO %s VALUES (NULL, ?, ?);", this.account_table_name);
                    try {
                        PreparedStatement ps = this.database_provider.getPreparedStatement(query);
                        ps.setInt(1, id);
                        ps.setDouble(2, 0.0);
                        ps.executeUpdate();
                        return true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    public boolean isPlayerHasMoneyAccount(String minecraft_name){
        String query = String.format("SELECT %s.id FROM %s INNER JOIN minedventure_accounts ON %s.minedventure_account_id = minedventure_accounts.id WHERE minecraft_name='%s';", this.account_table_name, this.account_table_name, this.account_table_name, minecraft_name);
        ResultSet rs = this.database_provider.executeQuery(query);
        try{
            if(rs != null){
                if(rs.next()){
                    return true;
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public double getPlayerBalance(String minecraft_name){
        this.initializePlayerMoneyAccountIfNotHas(minecraft_name);
        String query = String.format("SELECT balance FROM %s INNER JOIN minedventure_accounts ON %s.minedventure_account_id = minedventure_accounts.id WHERE minecraft_name='%s';", this.account_table_name, this.account_table_name, minecraft_name);
        ResultSet rs = this.database_provider.executeQuery(query);
        try{
            if(rs != null){
                if(rs.next()){
                    return rs.getDouble("balance");
                }
            }
        } catch(SQLException e){
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isPlayerHas(String minecraft_name, double value){
        if(!this.isPlayerHasMoneyAccount(minecraft_name)) {
            return false;
        }
        return this.getPlayerBalance(minecraft_name) - value >= 0;
    }

    public boolean setPlayerBalance(String minecraft_name, double new_balance){
        String query = String.format("UPDATE %s INNER JOIN minedventure_accounts ON %s.minedventure_account_id = minedventure_accounts.id SET balance=? WHERE minecraft_name='%s';", this.account_table_name, this.account_table_name, minecraft_name);
        try {
            PreparedStatement ps = this.database_provider.getPreparedStatement(query);
            ps.setDouble(1, new_balance);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public EconomyResponse withdrawPlayer(String minecraft_name, double value){
        EconomyResponse response = new EconomyResponse(value, 0.0, EconomyResponse.ResponseType.FAILURE, "Error");
        if(this.initializePlayerMoneyAccountIfNotHas(minecraft_name)){
            if(value < 0){
                return response;
            }
            double player_balance = this.getPlayerBalance(minecraft_name);
            if(player_balance - value >= 0){
                if(this.setPlayerBalance(minecraft_name, player_balance - value)){
                    response = new EconomyResponse(value, player_balance - value, EconomyResponse.ResponseType.SUCCESS, "");
                    // Log transaction
                    MineDventure.getPluginAsyncLoop().addLog(new EconomyLog(minecraft_name, TransactionType.WITHDRAW, -value, player_balance - value, PluginUtils.getCurrentTimestamp()));
                    return response;
                }else{
                    response = new EconomyResponse(value, player_balance, EconomyResponse.ResponseType.FAILURE, "Error in transaction");
                }
            }else{
                response = new EconomyResponse(value, player_balance, EconomyResponse.ResponseType.FAILURE, "Player don't has money");
            }
        }
        return response;
    }

    public EconomyResponse depositPlayer(String minecraft_name, double value){
        EconomyResponse response = new EconomyResponse(value, 0.0, EconomyResponse.ResponseType.FAILURE, "Error");
        if(this.initializePlayerMoneyAccountIfNotHas(minecraft_name)){
            if(value < 0){
                return response;
            }
            double player_balance = this.getPlayerBalance(minecraft_name);
                if(this.setPlayerBalance(minecraft_name, player_balance + value)){
                    response = new EconomyResponse(value, player_balance + value, EconomyResponse.ResponseType.SUCCESS, "");
                    // Log transaction
                    MineDventure.getPluginAsyncLoop().addLog(new EconomyLog(minecraft_name, TransactionType.DEPOSIT, value, player_balance + value, PluginUtils.getCurrentTimestamp()));
                    return response;
                }else{
                    response = new EconomyResponse(value, player_balance, EconomyResponse.ResponseType.FAILURE, "Error in transaction");
                }
        }
        return response;
    }
}
