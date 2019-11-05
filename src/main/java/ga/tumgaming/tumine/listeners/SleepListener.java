package ga.tumgaming.tumine.listeners;

import ga.tumgaming.tumine.TUMain;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;

import java.util.ArrayList;
import java.util.List;

public class SleepListener implements Listener {

    private static List<Player> inBed = new ArrayList<>();
    private BossBar bar = Bukkit.createBossBar("Schlafende Spieler", BarColor.BLUE, BarStyle.SOLID);

    private static boolean inizialisiert;

    @EventHandler
    public void onPlayerEnterBed(PlayerBedEnterEvent event) {
        // if player cant enter bed return
        if(event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) return;

        Player player = event.getPlayer();
        inBed.add(player);

        /* BossBar */
        bar.setProgress(getProgress());
        bar.setVisible(true);
        Bukkit.getOnlinePlayers().forEach((onlinePlayer) -> bar.addPlayer(player));

        if(checkForDay()) {
            inizialisiert = true;
            Bukkit.getScheduler().runTaskLater(TUMain.getPlugin(), () -> {
                setTimeToDay();
                inBed.clear();
                Bukkit.broadcastMessage("§8>> §7Die Zeit wird auf Tag gesetzt");
                inizialisiert = false;
                removeBar();
            }, 20);
        }
    }

    @EventHandler
    public void onPlayerLeaveBed(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();
        if(inBed.contains(player)) inBed.remove(player);

        /* BossBar */
        if(inizialisiert) return;
        bar.setProgress(getProgress());
        bar.setVisible(true);
        removeBar();
    }

    private void removeBar() {
        if(getProgress() == 0) bar.removeAll();
    }

    private static boolean checkForDay() {
        return inBed.size() >= (Bukkit.getOnlinePlayers().size()/2);
    }

    private static void setTimeToDay() {
        Bukkit.getWorlds().forEach((world) -> world.setTime(0));
    }

    private static double getProgress() {
        return (double) inBed.size() / (Bukkit.getOnlinePlayers().size()/2);
    }



}
