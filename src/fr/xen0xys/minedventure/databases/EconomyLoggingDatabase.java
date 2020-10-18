package fr.xen0xys.minedventure.databases;

import fr.xen0xys.minedventure.MineDventure;
import fr.xen0xys.minedventure.models.EconomyLog;
import fr.xen0xys.minedventure.utils.PluginUtils;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EconomyLoggingDatabase {


    // Types of transactions: WITHDRAW, DEPOSIT

    private final String account_table_name = "minedventure_economy_logs";
    private final DatabaseProvider database_provider;

    public EconomyLoggingDatabase(){
        database_provider = MineDventure.getDatabaseProvider();
    }

    public EconomyLoggingDatabase(DatabaseProvider database_provider) {
        this.database_provider = database_provider;
    }


    public void initializeTableIfNotInitialized(){
        if(!this.database_provider.isTableInitialised(this.account_table_name)){
            this.database_provider.initializeTable(this.account_table_name, "id INT PRIMARY KEY AUTO_INCREMENT, minedventure_account_id INT, transaction_type VARCHAR(30), changed_value DOUBLE, new_balance DOUBLE, timestamp BIGINT, FOREIGN KEY (minedventure_account_id) REFERENCES minedventure_accounts(id) ON DELETE CASCADE ON UPDATE NO ACTION");
        }
    }

    public boolean uploadLog(EconomyLog log) {
        if (new EconomyDatabase().isPlayerHasMoneyAccount(log.getMinecraftName())) {
            try {
                String query = String.format("INSERT INTO %s VALUES (NULL, ?, '%s', ?, ?, '%s')", this.account_table_name, log.getTransactionType(), log.getTimestamp());
                PreparedStatement ps = this.database_provider.getPreparedStatement(query);
                ps.setInt(1, new AccountsDatabase().getIdFromMinecraftName(log.getMinecraftName()));
                ps.setDouble(2, log.getChangedValue());
                ps.setDouble(3, log.getNewBalance());
                this.database_provider.executeUpdatePreparedStatement(ps);
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

}
