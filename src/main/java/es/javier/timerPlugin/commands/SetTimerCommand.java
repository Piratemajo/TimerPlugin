package es.javier.timerPlugin.commands;

import es.javier.timerPlugin.configs.TimerConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SetTimerCommand implements CommandExecutor {

    private final Map<UUID, TimerConfig> playerSelections = new ConcurrentHashMap<>();
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
        return false;
    }

    private int convertToSeconds(int time, String unit) {
        switch (unit) {
            case "s": return time;
            case "m": return time * 60;
            case "h": return time * 3600;
            case "d": return time * 86400;
            default: return 60;
        }
    }

    private String formatTime(int seconds) {
        if (seconds < 60) return seconds + " segundos";
        if (seconds < 3600) return (seconds / 60) + " minutos y " + (seconds % 60) + " segundos";
        if (seconds < 86400) return (seconds / 3600) + " horas, " + ((seconds % 3600) / 60) + " minutos";
        return (seconds / 86400) + " días, " + ((seconds % 86400) / 3600) + " horas";
    }
}
