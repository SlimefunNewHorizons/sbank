<p align="center">
  <img src="banner.svg" width="920" alt="sBank" />
</p>

# sBank

> ### 🏰 ¡Únete a la Comunidad Oficial de DrakesCraft!
> 
> * 🎮 **IP del Servidor**: `mc.drakescraft.cl` *(Java 1.21.11 & Bedrock)*
> * 💬 **Discord Oficial**: [discord.gg/drakescraft](https://discord.gg/rv3vtXZTk7)
> * 🌐 **Web & Guía**: [web.drakescraft.cl](https://web.drakescraft.cl) — 🛒 **Tienda**: [web.drakescraft.cl/store](https://web.drakescraft.cl/store.html)
> 
> *¡Juega con este banco y más de 80 plugins y addons optimizados en vivo en nuestra network de supervivencia técnica!*

---

Banking plugin for Minecraft servers using Vault. This fork is maintained for DrakesCraft, with emphasis on predictable money movements, transparent records, and operational safety.

## What It Does

- Personal bank accounts backed by SQLite or MySQL.
- Deposits, withdrawals, and optional physical money items.
- Loans, scheduled debt payments, and configurable interest.
- Vault economy integration and Citizens banker support.
- Daily JSONL audit files for deposits, withdrawals, physical money, loans, debt payments, interest, and death penalties.
- Physical money uses internal item data; its visible name and lore are never accepted as proof of value.
- Completed deposits, withdrawals and physical withdrawals persist immediately instead of waiting for autosave.

## Audit Trail

Every bank movement is written to:

```text
plugins/sBank/audit/transactions-YYYY-MM-DD.jsonl
```

Each line includes the timestamp, player UUID, movement type, amount, wallet balances before and after, bank balances before and after, and a short source note. The audit is file-based, so normal operation does not flood the Minecraft console.

Configure it in `config.yml`:

```yaml
audit:
  enabled: true
  directory: "audit"
```

## Commands

| Command | Description |
| --- | --- |
| `/bank` | Opens the personal bank menu. |
| `/bank debt` | Shows the current debt. |
| `/bank <player>` | Opens the administrator view for a player. |
| `/bank reload` | Reloads configuration for administrators. |

## Permissions

| Permission | Description |
| --- | --- |
| `sbank.use` | Open the bank menu. |
| `sbank.loan` | Request loans. |
| `sbank.interest` | Receive configured bank interest. |
| `sbank.interest.<percentage>` | Override the configured interest rate. |
| `sbank.dontlosemoney` | Skip environmental death penalties. |
| `sbank.admin` | Access administration and reload commands. |
| `sbank.bypass.banker` | Use the bank while banker NPC mode is enabled. |

## Runtime Notes

- Vault is required. Citizens is optional.
- Numeric transactions must be finite and greater than zero.
- Vault responses are checked before the corresponding bank balance is changed.
- A failed persistence rolls back the matching Vault movement where possible and never confirms the transaction to the player.
- Interest is disabled in the DrakesCraft default configuration because it mints currency; enable it only as an explicit economy policy.
- Chat-driven actions are returned to the Bukkit main thread before interacting with Vault, inventories, or persistent plugin state.
- Interest creates money according to the configured rate. Treat its rate and interval as economy policy, not a harmless cosmetic setting.

## Build

Use Java 8 or later with Maven:

```powershell
wsl -e bash -lc 'cd /mnt/c/Users/jack/Documents/GitHub/Repositorios\ personales/sBank && mvn clean package'
```

The resulting plugin is generated in `target/`.

## Deployment

1. Back up the current `sBank.jar` and `plugins/sBank/sbank.db`.
2. Replace only the JAR while the server is stopped.
3. Start the server and make one controlled deposit and withdrawal.
4. Check the new audit file before allowing regular traffic.

---

## 📄 License & Upstream Attribution

This project is a sovereign fork maintained by [**JackStar6677-1**](https://github.com/JackStar6677-1) under [**DrakesCraft Labs**](https://github.com/DrakesCraft-Labs).

- **Original Project:** Created by the upstream authors and the open-source community.
- **DrakesCraft Optimizations:** Modernized for Paper/Purpur 1.21.11+, Java 21, high concurrency, asynchronous safety, and exploit/duplication prevention.
- **License:** Distributed under the original **GNU General Public License v3.0 (GPLv3)** (or original upstream license). See the [LICENSE](LICENSE) file for complete terms.
