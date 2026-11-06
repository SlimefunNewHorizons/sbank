package com.spearforge.sBank.listener;

import com.spearforge.sBank.SBank;
import com.spearforge.sBank.model.Bank;
import com.spearforge.sBank.model.Debt;
import com.spearforge.sBank.utils.MiscUtils;
import com.spearforge.sBank.utils.TextUtils;
import net.milkbowl.vault.economy.EconomyResponse;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e){
        int percentage = SBank.getPlugin().getConfig().getInt("after-death-percentage");
        double balance = SBank.getEcon().getBalance(e.getEntity());
        double cut = balance * percentage / 100;
        Player p = e.getEntity();
        if (SBank.getPlugin().getConfig().getBoolean("lose-money-after-death")){
            if (e.getEntity().getKiller() == null){
                if (!p.hasPermission("sbank.dontlosemoney")){
                    if (balance > 0) {
                        EconomyResponse response = SBank.getEcon().withdrawPlayer(e.getEntity(), cut);
                        if (response.transactionSuccess()) {
                            double bank = SBank.bankOf(p).getBalance();
                            SBank.getAuditLogger().record("DEATH_PENALTY", p.getName(), p.getUniqueId().toString(), cut,
                                    balance, SBank.getEcon().getBalance(p), bank, bank, "environmental-death");
                            TextUtils.sendMessageWithPrefix(p, SBank.getPlugin().getConfig().getString("after-death-message").replaceAll("%money%", MiscUtils.formatBalance(cut)));
                        }
                    }
                }
            }
        }
    }

    @SneakyThrows
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        SBank.bankOf(e.getPlayer());
    }

    /** Carga la cuenta de la modalidad destino para que el placeholder y la GUI la tengan lista. */
    @EventHandler
    public void onWorldChange(org.bukkit.event.player.PlayerChangedWorldEvent e) {
        SBank.bankOf(e.getPlayer());
    }

    @SneakyThrows
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Player p = e.getPlayer();
        String prefix = p.getName() + "@";
        for (Bank bank : new java.util.ArrayList<>(SBank.getBanks().values())) {
            if (bank.getKey().startsWith(prefix)) {
                SBank.getDb().updateBankInDatabase(bank);
                SBank.getBanks().remove(bank.getKey());
            }
        }
        for (Debt debt : SBank.getDebts().values()) {
            if (debt.getKey().startsWith(prefix)) {
                SBank.getDb().updateDebtInDatabase(debt);
            }
        }

        List<Map<String, ?>> maps = Arrays.asList(
                LoanGuiListener.getLoanAmount(),
                LoanGuiListener.getLoanAgree(),
                DebtGuiListener.getDebtPayment(),
                BankGuiListener.getCustomDepAmount(),
                BankGuiListener.getCustomWithAmount(),
                BankGuiListener.getCustomPhysicalWithAmount(),
                BankGuiListener.getSetName()
        );

        for (Map<String, ?> map : maps) {
            map.remove(p.getName());
        }
    }

}
