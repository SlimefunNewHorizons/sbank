package com.spearforge.sBank.utils;

import com.spearforge.sBank.SBank;
import com.spearforge.sBank.model.Bank;
import com.spearforge.sBank.model.Debt;
import com.spearforge.sBank.model.MoneyPile;
import com.spearforge.sBank.modules.DebtModule;
import com.spearforge.sBank.modules.InterestModule;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TextUtils {

    public static List<String> formatLore(List<String> lore, Map<String, String> replacements) {
        return lore.stream()
                .map(line -> replacePlaceholders(line, replacements))
                .map(TextUtils::formatColors)
                .collect(Collectors.toList());
    }


    private static String replacePlaceholders(String line, Map<String, String> replacements) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            line = line.replace(entry.getKey(), entry.getValue());
        }
        return line;
    }

    private static String formatColors(String line) {
        return ChatColor.translateAlternateColorCodes('&', line);
    }

    public static List<String> replacePlaceholders(List<String> lore, @Nullable Bank bank, @Nullable Debt debt, @Nullable MoneyPile moneyPile){
        String interest = "";
        String currencySymbol = SBank.getPlugin().getConfig().getString("currency-symbol");
        if (SBank.getPlugin().getConfig().getBoolean("interest.enabled")){
            double _interest = SBank.getPlugin().getConfig().getDouble("interest.default-interest-rate",
                    SBank.getPlugin().getConfig().getDouble("interest.interest-rate"));
            interest = "" + _interest + "%";
            if(bank != null) {
                Player p = Bukkit.getPlayer(bank.getUsername());
                interest = "" + MiscUtils.getInterest(p, _interest) + "%";
            }
        }

        Map<String, String> replacements = new HashMap<>();
        replacements.put("%min_loan%", MiscUtils.formatBalance(SBank.getPlugin().getConfig().getInt("loan.min-loan")));
        replacements.put("%max_loan%", MiscUtils.formatBalance(SBank.getPlugin().getConfig().getInt("loan.max-loan")));
        replacements.put("%interest_rate%", SBank.getPlugin().getConfig().getInt("loan.loan-interest") + "%");
        replacements.put("%term%", String.valueOf(SBank.getPlugin().getConfig().getInt("loan.loan-term")));
        replacements.put("%payment_interval%", String.valueOf(SBank.getPlugin().getConfig().getInt("loan.debt-time")));

        if (moneyPile != null){
            replacements.put("%money_pile%", MiscUtils.formatBalance(moneyPile.getAmount()) + currencySymbol);
        }

        if (bank!= null) {
            replacements.put("%balance%", MiscUtils.formatBalance(bank.getBalance()) + currencySymbol);
            replacements.put("%interest_rate%", interest);
            replacements.put("%bank_name%", bank.getBankname());
            replacements.put("%time_to_next_interest%", InterestModule.getTimeUntilNextInterest());
        } else if (debt != null){
            replacements.put("%total_debt%", MiscUtils.formatBalance(debt.getTotal()) + currencySymbol);
            replacements.put("%remaining_debt%", MiscUtils.formatBalance(debt.getRemaining()) + currencySymbol);
            replacements.put("%every_payment%", MiscUtils.formatBalance(debt.getDaily()) + currencySymbol);
            if (debt.getLastPaymentDate() != null) {
                replacements.put("%last_payment_date%", debt.getLastPaymentDate());
                replacements.put("%time_to_next_deduction%", DebtModule.getTimeUntilNextPayment(debt.getKey()));
            }
            replacements.put("%payment_interval%", String.valueOf(SBank.getPlugin().getConfig().getInt("loan.debt-time")));
        }

        return TextUtils.formatLore(lore, replacements);
    }

    public static void sendMessageWithPrefix(org.bukkit.command.CommandSender sender, String message){
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', SBank.getPlugin().getConfig().getString("messages.prefix")) + ChatColor.translateAlternateColorCodes('&',message));
    }
}
