package com.spearforge.sBank.listener;

import com.spearforge.sBank.SBank;
import com.spearforge.sBank.guis.DebtGui;
import com.spearforge.sBank.model.Debt;
import com.spearforge.sBank.utils.MiscUtils;
import com.spearforge.sBank.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PlayerLoanChatListener implements Listener {

    @EventHandler
    public void onGivingLoanAmount(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!LoanGuiListener.getLoanAmount().containsKey(player.getName())) {
            return;
        }
        if (SBank.getDebts().containsKey(SBank.key(player))) {
            LoanGuiListener.getLoanAmount().remove(player.getName());
            LoanGuiListener.getLoanAgree().remove(player.getName());
            TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.active-debt"));
            return;
        }

        event.setCancelled(true);
        String input = event.getMessage();
        Bukkit.getScheduler().runTask(SBank.getPlugin(), () -> processLoanAmount(player, input));
    }

    private void processLoanAmount(Player player, String input) {
        if (!LoanGuiListener.getLoanAmount().containsKey(player.getName())) {
            return;
        }
        if (input.equalsIgnoreCase("cancel")) {
            LoanGuiListener.getLoanAmount().remove(player.getName());
            TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.loan-cancelled"));
            return;
        }

        if (!MiscUtils.isInteger(input, 10)) {
            sendLoanAmountError(player);
            return;
        }

        double amount = Double.parseDouble(input);
        int minLoan = SBank.getPlugin().getConfig().getInt("loan.min-loan");
        int maxLoan = SBank.getPlugin().getConfig().getInt("loan.max-loan");
        if (amount < minLoan || amount > maxLoan || LoanGuiListener.getLoanAgree().containsKey(player.getName())) {
            sendLoanAmountError(player);
            return;
        }

        LoanGuiListener.getLoanAmount().put(player.getName(), amount);
        Debt debt = new Debt();
        double totalDebt = amount + ((amount * SBank.getPlugin().getConfig().getInt("loan.loan-interest") / 100)
                * SBank.getPlugin().getConfig().getInt("loan.loan-term"));
        debt.setTotal(totalDebt);
        debt.setRemaining(totalDebt);
        debt.setUuid(player.getUniqueId().toString());
        debt.setUsername(player.getName());
        debt.setModality(com.spearforge.sBank.model.Modality.of(player.getWorld().getName()));
        debt.setDaily(totalDebt / SBank.getPlugin().getConfig().getInt("loan.loan-term"));
        LoanGuiListener.getLoanAgree().put(player.getName(), debt);
        player.openInventory(DebtGui.openAgreementPage(player, debt));
    }

    private void sendLoanAmountError(Player player) {
        TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.loan-amount-error")
                .replaceAll("%minloan%", String.valueOf(SBank.getPlugin().getConfig().getInt("loan.min-loan")))
                .replaceAll("%maxloan%", String.valueOf(SBank.getPlugin().getConfig().getInt("loan.max-loan"))));
    }
}
