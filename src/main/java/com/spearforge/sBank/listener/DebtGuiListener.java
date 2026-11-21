package com.spearforge.sBank.listener;

import com.spearforge.sBank.SBank;
import com.spearforge.sBank.utils.TextUtils;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.HashMap;

public class DebtGuiListener implements Listener {

    @Getter
    private static HashMap<String, Boolean> debtPayment = new HashMap<>();

    @EventHandler
    public void onClickDebtGui(InventoryClickEvent e){

        String title = SBank.getPlugin().getConfig().getString("gui.debt-page-title");
        Player player = (Player) e.getWhoClicked();

        if (e.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', title))){
            e.setCancelled(true);
            if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta().getDisplayName() != null){
                if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', SBank.getGuiConfig().getString("gui.loan.pay.name")))){
                    if (SBank.getEcon().getBalance(player) > 0){
                        debtPayment.put(player.getName(), true);
                        TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.enter-amount"));
                        player.closeInventory();
                    } else {
                        TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.not-enough-money"));
                    }
                } else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', SBank.getGuiConfig().getString("gui.close-button.name")))) {
                    player.closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onDragDebtGui(InventoryDragEvent e) {
        String title = SBank.getPlugin().getConfig().getString("gui.debt-page-title");
        if (title != null && e.getView().getTitle().equals(ChatColor.translateAlternateColorCodes('&', title))) {
            e.setCancelled(true);
        }
    }

}
