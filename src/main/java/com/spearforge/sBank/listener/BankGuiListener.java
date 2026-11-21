package com.spearforge.sBank.listener;

import com.spearforge.sBank.SBank;
import com.spearforge.sBank.guis.DebtGui;
import com.spearforge.sBank.utils.TextUtils;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class BankGuiListener implements Listener {

    @Getter
    private static HashMap<String, Boolean> setName = new HashMap<>();
    @Getter
    private static HashMap<String, Boolean> customDepAmount = new HashMap<>();
    @Getter
    private static HashMap<String, Boolean> customWithAmount = new HashMap<>();
    @Getter
    private static HashMap<String, Boolean> customPhysicalWithAmount = new HashMap<>();

    private final String homeTitle = ChatColor.translateAlternateColorCodes('&', SBank.getPlugin().getConfig().getString("gui.bank-home-title"));

    @EventHandler
    public void onClickBankGui(InventoryClickEvent e){


        if (e.getView().getTitle().equalsIgnoreCase(homeTitle)){
            e.setCancelled(true);

            Player player = (Player) e.getWhoClicked();

            List<String> homeButtons = Arrays.asList(
                    ChatColor.translateAlternateColorCodes('&', SBank.getGuiConfig().getString("gui.deposit-button.name")),
                    ChatColor.translateAlternateColorCodes('&', SBank.getGuiConfig().getString("gui.withdraw-button.name")),
                    ChatColor.translateAlternateColorCodes('&', SBank.getGuiConfig().getString("gui.physical-withdraw-button.name")),
                    ChatColor.translateAlternateColorCodes('&', SBank.getGuiConfig().getString("gui.bank-set-name.name")),
                    ChatColor.translateAlternateColorCodes('&', SBank.getGuiConfig().getString("gui.bank-details.name")),
                    ChatColor.translateAlternateColorCodes('&', SBank.getGuiConfig().getString("gui.loan.loan-gui.name")),
                    ChatColor.translateAlternateColorCodes('&', SBank.getGuiConfig().getString("gui.close-button.name"))
            );

            if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta().getDisplayName() != null) {
                if (e.getClick().isLeftClick()){
                    if (e.getCurrentItem().getItemMeta().getDisplayName().equals(homeButtons.get(0))){
                        player.closeInventory();
                        customDepAmount.put(player.getName(), true);
                        TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.enter-amount"));
                    } else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(homeButtons.get(1))){
                        player.closeInventory();
                        customWithAmount.put(player.getName(), true);
                        TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.enter-amount"));
                    } else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(homeButtons.get(2))){
                        player.closeInventory();
                        customPhysicalWithAmount.put(player.getName(), true);
                        TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.enter-amount"));
                    }else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(homeButtons.get(3))){
                        player.closeInventory();
                        TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.setting-bank-name"));
                        setName.put(player.getName(), true);
                    } else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(homeButtons.get(5))){
                        player.closeInventory();
                        player.openInventory(DebtGui.openDebtPage(player));
                    } else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(homeButtons.get(6))){
                        player.closeInventory();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDragBankGui(InventoryDragEvent e) {
        if (e.getView().getTitle().equalsIgnoreCase(homeTitle)) {
            e.setCancelled(true);
        }
    }

}

