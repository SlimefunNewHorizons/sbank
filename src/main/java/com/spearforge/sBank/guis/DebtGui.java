package com.spearforge.sBank.guis;

import com.spearforge.sBank.SBank;
import com.spearforge.sBank.model.Debt;
import com.spearforge.sBank.utils.MiscUtils;
import com.spearforge.sBank.utils.TextUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class DebtGui {

    public static Inventory openDebtPage(Player player){

        Inventory loanGUI;

        if (SBank.getDebts().containsKey(SBank.key(player))) {
            loanGUI = Bukkit.createInventory(player, 9, ChatColor.translateAlternateColorCodes('&', SBank.getPlugin().getConfig().getString("gui.debt-page-title")));
            Debt pDebt = SBank.getDebts().get(SBank.key(player));
            loanGUI.setItem(2, createButton("gui.loan.details", pDebt));
            loanGUI.setItem(4, createButton("gui.loan.pay", pDebt));
            loanGUI.setItem(6, createButton("gui.close-button", pDebt));
        } else {
            loanGUI = Bukkit.createInventory(player, 9, ChatColor.translateAlternateColorCodes('&', SBank.getPlugin().getConfig().getString("gui.loan-page-title")));
            loanGUI.setItem(0, createButton("gui.loan.available-loan", null));
        }

        return loanGUI;
    }

    public static Inventory openAgreementPage(Player player, Debt debt){
        Inventory agreementGUI = Bukkit.createInventory(player, 9, ChatColor.translateAlternateColorCodes('&', SBank.getPlugin().getConfig().getString("gui.agree-page-title")));

        agreementGUI.setItem(4, createButton("gui.loan.agreement", debt));
        agreementGUI.setItem(2, createButton("gui.loan.agree", debt));
        agreementGUI.setItem(6, createButton("gui.loan.disagree", debt));

        return agreementGUI;

    }

    public static ItemStack createButton(String configPath, Debt pDebt) {
        ItemStack button = MiscUtils.getMaterialOrHead(configPath);
        ItemMeta meta = button.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', SBank.getGuiConfig().getString(configPath + ".name")));
        List<String> detailsLore = SBank.getGuiConfig().getStringList(configPath + ".lore");

        if (pDebt != null){
            meta.setLore(TextUtils.replacePlaceholders(detailsLore, null, pDebt, null));
        } else {
            meta.setLore(TextUtils.replacePlaceholders(detailsLore, null, null, null));
        }

        button.setItemMeta(meta);
        return button;
    }



}
