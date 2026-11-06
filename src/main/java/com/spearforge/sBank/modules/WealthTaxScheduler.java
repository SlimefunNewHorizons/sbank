package com.spearforge.sBank.modules;

import com.spearforge.sBank.SBank;
import com.spearforge.sBank.model.Bank;
import com.spearforge.sBank.utils.MiscUtils;
import com.spearforge.sBank.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Applies the configured wealth tax after a full interval, including offline accounts. */
public final class WealthTaxScheduler {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private WealthTaxScheduler() {
    }

    public static boolean isDue() {
        String lastRun = SBank.getPlugin().getConfig().getString("wealth-tax.last-run", "");
        try {
            return Duration.between(LocalDateTime.parse(lastRun, DATE_FORMAT), LocalDateTime.now()).toHours()
                    >= SBank.getPlugin().getConfig().getLong("wealth-tax.interval-hours", 24);
        } catch (RuntimeException exception) {
            markRunNow();
            return false;
        }
    }

    public static void apply() {
        double protectedBalance = SBank.getPlugin().getConfig().getDouble("wealth-tax.protected-balance", 500_000D);
        double maximumCharge = SBank.getPlugin().getConfig().getDouble("wealth-tax.maximum-charge-per-cycle", 250_000D);
        List<WealthTaxModule.Tier> tiers = getTiers();
        List<String> exemptUsers = SBank.getPlugin().getConfig().getStringList("wealth-tax.exempt-usernames");
        int chargedAccounts = 0;
        double totalCollected = 0;

        try {
            for (Bank bank : SBank.getDb().getAllBanks()) {
                Player online = Bukkit.getPlayerExact(bank.getUsername());
                if (exemptUsers.contains(bank.getUsername()) || (online != null && online.hasPermission("sbank.wealthtax.exempt"))) {
                    continue;
                }

                double charge = WealthTaxModule.calculate(bank.getBalance(), protectedBalance, maximumCharge, tiers);
                if (charge <= 0) {
                    continue;
                }

                double before = bank.getBalance();
                bank.setBalance(before - charge);
                SBank.getDb().updateBankInDatabase(bank);
                if (online != null) {
                    SBank.getBanks().put(bank.getKey(), bank);
                    TextUtils.sendMessageWithPrefix(online, SBank.getPlugin().getConfig()
                            .getString("wealth-tax.message", "&6Impuesto bancario: &e%money%")
                            .replace("%money%", MiscUtils.formatBalance(charge)));
                }
                SBank.getAuditLogger().record("WEALTH_TAX", bank.getUsername(), bank.getUuid(), charge,
                        0, 0, before, bank.getBalance(), "protected-balance=" + protectedBalance);
                EconomyReserve.credit(charge);
                chargedAccounts++;
                totalCollected += charge;
            }
            markRunNow();
            SBank.getPlugin().getLogger().info("Wealth tax processed " + chargedAccounts + " account(s), collected "
                    + MiscUtils.formatBalance(totalCollected) + ".");
        } catch (Exception exception) {
            SBank.getPlugin().getLogger().warning("Could not complete wealth tax cycle: " + exception.getMessage());
        }
    }

    private static List<WealthTaxModule.Tier> getTiers() {
        ConfigurationSection section = SBank.getPlugin().getConfig().getConfigurationSection("wealth-tax.tiers");
        List<WealthTaxModule.Tier> tiers = new ArrayList<>();
        if (section == null) {
            return tiers;
        }
        for (String key : section.getKeys(false)) {
            tiers.add(new WealthTaxModule.Tier(section.getDouble(key + ".up-to"), section.getDouble(key + ".rate-percent")));
        }
        tiers.sort(Comparator.comparingDouble(tier -> tier.upperBound() <= 0 ? Double.MAX_VALUE : tier.upperBound()));
        return tiers;
    }

    private static void markRunNow() {
        SBank.getPlugin().getConfig().set("wealth-tax.last-run", DATE_FORMAT.format(LocalDateTime.now()));
        SBank.getPlugin().saveConfig();
    }
}
