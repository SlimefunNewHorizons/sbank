package com.spearforge.sBank.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.spearforge.sBank.utils.MoneyMath;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Debt {

    private String username;
    private String uuid;
    private double total;
    private double remaining;
    private double daily;
    private String lastPaymentDate;
    private String modality = Modality.SURVIVAL;

    public String getKey() {
        return Modality.key(username, modality);
    }

    public void setTotal(double total) {
        this.total = MoneyMath.normalize(total);
    }

    public void setRemaining(double remaining) {
        this.remaining = MoneyMath.normalize(remaining);
    }

    public void setDaily(double daily) {
        this.daily = MoneyMath.normalize(daily);
    }

    @Override
    public String toString() {
        return "Debt{" +
                "username='" + username + '\'' +
                ", uuid='" + uuid + '\'' +
                ", total=" + total +
                ", remaining=" + remaining +
                ", daily=" + daily +
                ", lastPaymentDate=" + lastPaymentDate +
                '}';
    }
}
