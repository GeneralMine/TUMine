package ga.tumgaming.tumine.autoupdater;

import ga.tumgaming.tumine.TUMain;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UpdateCommand implements CommandExecutor {

    private UpdateRunner runner;
    private long reloadMillis;

    /**
     * @param namePrefix   the prefix of the filename the plugin will search for
     * @param reloadMillis the time before the server is reloaded
     */
    public UpdateCommand(String namePrefix, long reloadMillis, UpdateMethod method) {
        this.runner = new UpdateRunner(namePrefix, method);
        this.reloadMillis = reloadMillis;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String lbl, String[] args) {
        // exits if the sender has no permissions to execute
        if (!hasPermission(commandSender)) {
            commandSender.sendMessage(Color.RED + "Nö");
            return true;
        }

        // exits if command has unnecessary arguments and outputs it's syntax
        if (args.length != 0) {
            return false;
        }

        sendToAllWithPermission(Color.AQUA + commandSender.getName() + Color.GREEN + " initiated an update.",
                Color.GREEN + "Getting the updated File");

        // Running the update asynchronously to avoid blocking of any kind
        TUMain.getPlugin().getServer().getScheduler().runTaskAsynchronously(TUMain.getPlugin(), this.runner);

        sendToAllWithPermission(Color.GREEN + "Done. ");
        // puts the Runnable in the Scheduler

        sendToAllWithPermission(Color.GREEN + "Done. Now waiting for reload in " + Color.AQUA + this.reloadMillis + " millis");

        TUMain.getPlugin().getServer().getScheduler().runTaskLaterAsynchronously(
                TUMain.getPlugin(),
                this.runner,
                this.reloadMillis
        );

        return true;
    }

    /**
     * Sends a message to all players allowed to update the server
     * @param msg the message to send
     */
    public static void sendToAllWithPermission(String... msg) {
        for (Player p : TUMain.getPlugin().getServer().getOnlinePlayers()) {
            if (p.hasPermission("update")) {
                for (int i = 0; i < msg.length; i++) {
                    p.sendMessage(msg[i]);
                }
            }
        }
    }

    /**
     * A quick check if the sender has the permission to use this command
     *
     * @param sender the sender to check
     * @return true if the sender is not a player or the sender has the permission to execute the command
     */
    private boolean hasPermission(CommandSender sender) {
        return !(sender instanceof Player) || sender.hasPermission("update");
    }

}
