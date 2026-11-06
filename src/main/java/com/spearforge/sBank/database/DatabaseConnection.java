package com.spearforge.sBank.database;

import com.spearforge.sBank.model.Bank;
import com.spearforge.sBank.model.Debt;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public abstract class DatabaseConnection {

    protected Connection connection;

    public abstract Connection getConnection() throws SQLException;

    public abstract void initializeDatabase() throws SQLException;

    public abstract void loadOnlinePlayersBanks() throws SQLException;

    public abstract Bank getBank(String username, String modality) throws SQLException;

    /** Returns every persisted account so scheduled economy rules include offline players. */
    public abstract List<Bank> getAllBanks() throws SQLException;

    public abstract boolean hasBank(String username, String modality) throws SQLException;

    public abstract void loadDebtsFromDatabase() throws SQLException;

    public abstract void applyInterest(double _interest) throws SQLException;

    public abstract void setBankInDatabase(Bank bank) throws SQLException;

    public abstract void updateBankInDatabase(Bank bank) throws SQLException;

    public abstract void setDebtToDatabase(Debt debt) throws SQLException;

    public abstract void updateDebtInDatabase(Debt debt) throws SQLException;

    public abstract void removeDebt(String username, String modality) throws SQLException;

}
