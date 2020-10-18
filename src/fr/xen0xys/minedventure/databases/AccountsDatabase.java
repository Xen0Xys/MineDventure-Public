package fr.xen0xys.minedventure.databases;

import fr.xen0xys.minedventure.MineDventure;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountsDatabase {

    private final String account_table_name = "minedventure_accounts";
    private final DatabaseProvider database_provider;

    public AccountsDatabase() {
        database_provider = MineDventure.getDatabaseProvider();
    }

    public AccountsDatabase(DatabaseProvider database_provider) {
        this.database_provider = database_provider;
    }

    public void initializeTableIfNotInitialized() {
        if (!this.database_provider.isTableInitialised(this.account_table_name)) {
            this.database_provider.initializeTable(this.account_table_name, "id INT PRIMARY KEY AUTO_INCREMENT UNIQUE, discord_id BIGINT NOT NULL, minecraft_name VARCHAR(30) UNIQUE");
        }
    }

    public boolean isDiscordUserExist(long discord_id) {
        this.initializeTableIfNotInitialized();
        String query = String.format("SELECT discord_id FROM %s;", this.account_table_name);
        ResultSet rs = this.database_provider.executeQuery(query);
        try {
            if (rs != null) {
                while (rs.next()) {
                    long _discord_id = rs.getLong("discord_id");
                    if (_discord_id == discord_id) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isMinecraftUserExist(String minecraft_name) {
        this.initializeTableIfNotInitialized();
        String query = String.format("SELECT minecraft_name FROM %s;", this.account_table_name);
        ResultSet rs = this.database_provider.executeQuery(query);
        try {
            if (rs != null) {
                while (rs.next()) {
                    String _minecraft_name = rs.getString("minecraft_name");
                    if (_minecraft_name.equals(minecraft_name)) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean changeUsername(String minecraft_name, String new_minecraft_name){
        this.initializeTableIfNotInitialized();
        if(this.isMinecraftUserExist(minecraft_name) && !this.isMinecraftUserExist(new_minecraft_name)){
            String query = String.format("UPDATE %s SET minecraft_name='%s' WHERE minecraft_name='%s'", this.account_table_name, new_minecraft_name, minecraft_name);
            return this.database_provider.executeUpdateQuery(query);
        }
        return false;
    }

    public boolean createPlayerAccount(long discord_id, String minecraft_name, String password) {
        this.initializeTableIfNotInitialized();
        if (!this.isDiscordUserExist(discord_id)) {
            String query = String.format("INSERT INTO %s VALUES (NULL, ?, '%s');", this.account_table_name, minecraft_name);
            try {
                PreparedStatement ps = this.database_provider.getPreparedStatement(query);
                ps.setLong(1, discord_id);
                ps.executeUpdate();
                // Set password
                new SecurityDatabase().setPassword(minecraft_name, password);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public int getIdFromMinecraftName(String minecraft_name){
        return this.database_provider.getId(this.account_table_name, "minecraft_name", minecraft_name);
    }

    public String getMinecraftNameFromDiscordId(long discord_id){
        try{
            String query = String.format("SELECT minecraft_name FROM %s WHERE discord_id=%d", this.account_table_name, discord_id);
            ResultSet rs = this.database_provider.executeQuery(query);
            if(rs.next()){
                return rs.getString("minecraft_name");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }



}
