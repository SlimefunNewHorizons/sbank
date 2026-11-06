package com.spearforge.sBank.listener;

import com.spearforge.sBank.SBank;
import com.spearforge.sBank.model.Bank;
import com.spearforge.sBank.model.MoneyPile;
import com.spearforge.sBank.utils.MiscUtils;
import com.spearforge.sBank.utils.TextUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MoneyPileListener implements Listener {

    @EventHandler
    public void onMoneyPileClick(PlayerInteractEvent e){
        if (e.getHand() == null || !e.getHand().name().equals("HAND")
                || (e.getAction() != Action.RIGHT_CLICK_AIR && e.getAction() != Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (!SBank.getPlugin().getConfig().getBoolean("physical-money.enabled", false)) {
            return;
        }
        Player player = e.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || !item.hasItemMeta()){
            return;
        }

        ItemMeta meta = item.getItemMeta();
        String configuredName = SBank.getPlugin().getConfig().getString("physical-money.item.name");
        if (configuredName == null || meta == null || meta.getDisplayName() == null) {
            return;
        }
        String expectedName = ChatColor.translateAlternateColorCodes('&', configuredName);
        if (!meta.getDisplayName().equals(expectedName)) {
            return;
        }

        double amount = MiscUtils.getVerifiedPhysicalMoney(meta);
        if (amount <= 0 || item.getAmount() != 1){
            TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.invalid-money-pile"));
            return;
        }

        e.setCancelled(true);
        Bank pBank = SBank.bankOf(player);
        double bankBefore = pBank.getBalance();
        double wallet = SBank.getEcon().getBalance(player);
        pBank.setBalance(bankBefore + amount);
        if (!SBank.persistBank(pBank)) {
            pBank.setBalance(bankBefore);
            TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.transaction-failed"));
            return;
        }
        player.getInventory().setItemInMainHand(null);
        SBank.getAuditLogger().record("PHYSICAL_REDEEM", player.getName(), player.getUniqueId().toString(), amount,
                wallet, wallet, bankBefore, pBank.getBalance(), "right-click-item");
        TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.money-pile-redeemed").replaceAll("%money_pile%", MiscUtils.formatBalance(amount)));


    }

}
