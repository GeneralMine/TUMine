package ga.tumgaming.tumine.autoupdater;

import ga.tumgaming.tumine.TUMain;
import java.io.File;

public class Updater implements Runnable {

    private File versions;

    public Updater() {
        this.versions = new File(TUMain.getPlugin().getDataFolder() + "/newVersions");
    }

    @Override
    public void run() {
        //TODO: check
        //TODO: update
        //TODO: restart
    }

}
