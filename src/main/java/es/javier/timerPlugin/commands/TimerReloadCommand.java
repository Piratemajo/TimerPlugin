package es.javier.timerPlugin.commands;

import es.javier.timerPlugin.TimerPlugin;
import es.javier.timerPlugin.data.TimerData;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static es.javier.timerPlugin.TimerPlugin.updateDisplay;

public class TimerReloadCommand implements CommandExecutor {
    private final Map<Location, TimerData> activeTimers = new ConcurrentHashMap<>();
    private FileConfiguration config;
    private File configFile;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
         if (command.getName().equalsIgnoreCase("timerreload")) {
            if (!sender.hasPermission("timerplugin.reload")) {
                sender.sendMessage(ChatColor.RED + "No tienes permiso para recargar la configuración.");
                return true;
            }
            reloadConfiguration();
            sender.sendMessage(ChatColor.GREEN + "Configuración del TimerPlugin recargada correctamente.");
            return true;
        }
        return false;
    }

    private void reloadConfiguration() {
        config = YamlConfiguration.loadConfiguration(configFile);

        // Actualizar displays existentes
        for (Location location : activeTimers.keySet()) {
            if (location.getWorld().isChunkLoaded(location.getBlockX() >> 4, location.getBlockZ() >> 4)) {
                TimerData timerData = activeTimers.get(location);
                updateDisplay(location, timerData.getTimeLeft());
            }
        }
    }
}
