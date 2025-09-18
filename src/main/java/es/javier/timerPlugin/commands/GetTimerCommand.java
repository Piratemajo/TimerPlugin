package es.javier.timerPlugin.commands;

import es.javier.timerPlugin.TimerPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GetTimerCommand implements CommandExecutor {



    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("gettimer")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Solo jugadores pueden usar este comando!");
                return true;
            }

            Player player = (Player) sender;
            ItemStack timerItem = TimerPlugin.createTimerItem();
            player.getInventory().addItem(timerItem);
            player.sendMessage(ChatColor.GREEN + "Has recibido un temporizador avanzado!");
            return true;


        }
        return false;
    }
}