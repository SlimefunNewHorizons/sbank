package com.spearforge.sBank.guis;

import com.spearforge.sBank.SBank;
import com.spearforge.sBank.model.Bank;
import com.spearforge.sBank.model.Debt;
import com.spearforge.sBank.utils.MiscUtils;
import com.spearforge.sBank.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class AdminGUI {

    public static Inventory openPlayerBankGUI(Bank bank) {
        Inventory inv = Bukkit.createInventory(null, InventoryType.WORKBENCH, bank.getUsername());

        inv.setItem(5, createButton("gui.admin-bank-details", bank, null));
        Debt debt = SBank.getDebts().get(bank.getKey());
        if (debt != null) {
            inv.setItem(0, createButton("gui.loan.admin-details", null, debt));
        } else {
            ItemStack button = new ItemStack(Material.valueOf(SBank.getGuiConfig().getString("gui.loan.admin-details.material")));
            ItemMeta meta = button.getItemMeta();
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', SBank.getGuiConfig().getString("gui.loan.admin-details.name")));
            meta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', SBank.getPlugin().getConfig().getString("messages.no-debt-found"))));
            button.setItemMeta(meta);
            inv.setItem(0, button);
        }

        return inv;
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
