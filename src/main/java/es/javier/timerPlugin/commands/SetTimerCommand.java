package es.javier.timerPlugin.commands;

import es.javier.timerPlugin.configs.TimerConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetTimerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
         if (command.getName().equalsIgnoreCase("settimer")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Solo jugadores pueden usar este comando!");
                return true;
            }

            Player player = (Player) sender;

            if (args.length < 2) {
                player.sendMessage(ChatColor.RED + "Uso: /settimer <tiempo> <unidad>");
                player.sendMessage(ChatColor.RED + "Unidades: s (segundos), m (minutos), h (horas), d (días)");
                return true;
            }

            try {
                int time = Integer.parseInt(args[0]);
                String unit = args[1].toLowerCase();

                int seconds = convertToSeconds(time, unit);
                if (seconds <= 0) {
                    player.sendMessage(ChatColor.RED + "El tiempo debe ser mayor a 0.");
                    return true;
                }

                playerSelections.put(player.getUniqueId(), new TimerConfig(seconds));
                player.sendMessage(ChatColor.GREEN + "Temporizador configurado para " + time + unit + " (" + formatTime(seconds) + ")");

            } catch (NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "El tiempo debe ser un número válido.");
            }
            return true;
        }
    }
}
