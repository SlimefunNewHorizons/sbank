package com.spearforge.sBank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.spearforge.sBank.utils.MoneyMath;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bank {

    private String username;
    private String uuid;
    private String bankname;
    private double balance;
    private String modality = Modality.SURVIVAL;

    public String getKey() {
        return Modality.key(username, modality);
    }

    public String getUsername() {
        return username;
    }

    public String getUniqueId() {
        return uuid;
    }
    
    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Bank balance cannot be negative");
        }
        this.balance = MoneyMath.normalize(balance);
    }

}
