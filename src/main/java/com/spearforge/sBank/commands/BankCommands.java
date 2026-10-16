package com.spearforge.sBank.commands;

import com.spearforge.sBank.SBank;
import com.spearforge.sBank.guis.AdminGUI;
import com.spearforge.sBank.guis.BankGUI;
import com.spearforge.sBank.guis.DebtGui;
import com.spearforge.sBank.model.Bank;
import com.spearforge.sBank.utils.MiscUtils;
import com.spearforge.sBank.utils.TextUtils;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;


public class BankCommands implements CommandExecutor {

    @SneakyThrows
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender.hasPermission("sbank.admin")) {
                SBank.getPlugin().reloadConfig();
                SBank.getGuiConfig().reloadConfig();
                TextUtils.sendMessageWithPrefix(sender, SBank.getPlugin().getConfig().getString("messages.plugin-reloaded"));
            } else {
                TextUtils.sendMessageWithPrefix(sender, SBank.getPlugin().getConfig().getString("messages.no-permission"));
            }
            return true;
        }

        // Ajuste administrativo desde consola o con sbank.admin: `sbank set|take|give <jugador> <monto> [motivo]`.
        // Antes el unico camino era la AdminGUI en juego; sin esto no habia forma de corregir un saldo
        // desde consola/SAORI (auditoria de economia 2026-09-20). Queda registrado en el audit log.
        if (args.length >= 3 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("take")
                || args[0].equalsIgnoreCase("give"))) {
            if (!sender.hasPermission("sbank.admin")) {
                TextUtils.sendMessageWithPrefix(sender, SBank.getPlugin().getConfig().getString("messages.no-permission"));
                return true;
            }
            ajusteAdministrativo(sender, args);
            return true;
        }

        if (sender instanceof Player){
            Player player = (Player) sender;
            if (player.hasPermission("sbank.use") || player.hasPermission("sbank.admin")){
                if (args.length == 0){
                    if (SBank.getPlugin().getConfig().getBoolean("npc-bankers.enabled") && !player.hasPermission(SBank.getPlugin().getConfig().getString("npc-bankers.bypass-permission"))){
                        TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("npc-bankers.command-message"));
                    } else {
                        Inventory inventory = BankGUI.getBankHomePage((Player) sender);
                        player.openInventory(inventory);
                    }
                } else if (args.length == 1){
                    if (args[0].equalsIgnoreCase("debt")){
                        if (SBank.getPlugin().getConfig().getBoolean("npc-bankers.enabled") && !player.hasPermission(SBank.getPlugin().getConfig().getString("npc-bankers.bypass-permission"))){
                            TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("npc-bankers.command-message"));
                        } else {
                            if (player.hasPermission("sbank.loan")) {
                                if (SBank.getPlugin().getConfig().getBoolean("loan.enabled")) {
                                    player.openInventory(DebtGui.openDebtPage(player));
                                } else {
                                    TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.loan-disabled"));
                                }
                            } else {
                                TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.no-permission"));
                            }
                        }
                    } else if (Bukkit.getOfflinePlayer(args[0]) != null){ // check if player has a bank
                        if (player.hasPermission("sbank.admin")) {
                            Bank pBank = SBank.getDb().getBank(args[0]);
                            if (pBank != null){
                                player.openInventory(AdminGUI.openPlayerBankGUI(pBank));
                            } else {
                                TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.player-not-found"));
                            }
                        } else {
                            TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.no-permission"));
                        }
                    }
                }
            } else {
                TextUtils.sendMessageWithPrefix(player, SBank.getPlugin().getConfig().getString("messages.no-permission"));
            }
        }


        return true;
    }

    private void ajusteAdministrativo(CommandSender sender, String[] args) {
        String objetivo = args[1];
        double monto;
        try {
            monto = Double.parseDouble(args[2].replace(",", ""));
        } catch (NumberFormatException e) {
            sender.sendMessage("[sBank] Invalid amount: " + args[2]);
            return;
        }
        if (monto < 0) {
            sender.sendMessage("[sBank] Amount cannot be negative.");
            return;
        }
        String motivo = args.length > 3 ? String.join(" ", java.util.Arrays.copyOfRange(args, 3, args.length)) : "no reason";

        // Si el jugador esta conectado el saldo vivo es el de la cache; si no, el de la base.
        Bank bank = SBank.getBanks().get(objetivo);
        if (bank == null) {
            try {
                bank = SBank.getDb().getBank(objetivo);
            } catch (java.sql.SQLException e) {
                sender.sendMessage("[sBank] Error reading database: " + e.getMessage());
                return;
            }
        }
        if (bank == null) {
            sender.sendMessage("[sBank] " + objetivo + " does not have a bank account.");
            return;
        }
        double antes = bank.getBalance();
        double despues;
        switch (args[0].toLowerCase()) {
            case "set" -> despues = monto;
            case "take" -> despues = Math.max(0, antes - monto);
            default -> despues = antes + monto;
        }
        bank.setBalance(despues);
        boolean guardado = SBank.persistBank(bank);
        SBank.getAuditLogger().record("ADMIN_" + args[0].toUpperCase(), bank.getUsername(), bank.getUniqueId(),
                Math.abs(despues - antes), 0, 0, antes, despues, sender.getName() + ": " + motivo);
        sender.sendMessage("[sBank] " + bank.getUsername() + ": " + MiscUtils.formatBalance(antes) + " -> "
                + MiscUtils.formatBalance(despues) + (guardado ? "" : " (NO se pudo guardar en la base)"));
    }

}
