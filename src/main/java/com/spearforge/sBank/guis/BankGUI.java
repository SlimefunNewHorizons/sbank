package com.spearforge.sBank.guis;

import com.spearforge.sBank.SBank;
import com.spearforge.sBank.model.Bank;
import com.spearforge.sBank.model.Debt;
import com.spearforge.sBank.utils.MiscUtils;
import com.spearforge.sBank.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;


public class BankGUI {


    public static Inventory getBankHomePage(Player player){
        Bank pBank = SBank.bankOf(player);
        Inventory bankGUI = Bukkit.createInventory(player, 27, ChatColor.translateAlternateColorCodes('&', SBank.getPlugin().getConfig().getString("gui.bank-home-title")));

        if (pBank != null){
            bankGUI.setItem(11, createButton("gui.deposit-button", pBank, null));
            bankGUI.setItem(15, createButton("gui.withdraw-button", pBank, null));
            if(SBank.getPlugin().getConfig().getBoolean("physical-money.enabled")){
                bankGUI.setItem(16, createButton("gui.physical-withdraw-button", pBank, null));
            }
            bankGUI.setItem(8, createButton("gui.bank-set-name", pBank, null));
            bankGUI.setItem(13, createButton("gui.bank-details", pBank, null));
            if (SBank.getPlugin().getConfig().getBoolean("loan.enabled")){
                bankGUI.setItem(0, createButton("gui.loan.loan-gui", pBank, null));
            }
            bankGUI.setItem(26, createButton("gui.close-button", pBank, null));

        } else {
            player.sendMessage(SBank.getPlugin().getConfig().getString("messages.no-bank-account"));
        }
        return bankGUI;
    }


    public static ItemStack createButton(String configPath, @Nullable Bank pBank, @Nullable Debt pDebt) {
        ItemStack button = MiscUtils.getMaterialOrHead(configPath);
        ItemMeta meta = button.getItemMeta();

        if (pBank != null) {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', SBank.getGuiConfig().getString(configPath + ".name")));
            meta.setLore(TextUtils.replacePlaceholders(SBank.getGuiConfig().getStringList(configPath + ".lore"), pBank, null, null));
        } else {
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', SBank.getGuiConfig().getString(configPath + ".name")));
            meta.setLore(TextUtils.replacePlaceholders(SBank.getGuiConfig().getStringList(configPath + ".lore"), null, pDebt, null));
        }

        button.setItemMeta(meta);
        return button;
    }



}
