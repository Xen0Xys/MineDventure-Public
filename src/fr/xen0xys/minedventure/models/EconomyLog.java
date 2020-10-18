package fr.xen0xys.minedventure.models;

public class EconomyLog {

    private final String minecraft_name;
    private final TransactionType transactionType;
    private final double changed_value;
    private final double new_balance;
    private final long timestamp;

    public EconomyLog(String minecraft_name, TransactionType transaction_type, double changed_value, double new_balance, long timestamp){
        this.minecraft_name = minecraft_name;
        this.transactionType = transaction_type;
        this.changed_value = changed_value;
        this.new_balance = new_balance;
        this.timestamp = timestamp;
    }

    public String getMinecraftName() {
        return minecraft_name;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public double getChangedValue() {
        return changed_value;
    }

    public double getNewBalance() {
        return new_balance;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
