package ga.tumgaming.tumine.autoupdater;

import ga.tumgaming.tumine.TUMain;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UpdateRunner implements Runnable {

    private File me;
    private File newVersions;
    private ReloadRunner reloadRunner;
    private UpdateMethod method;

    public UpdateRunner(String namePrefix, UpdateMethod method) throws IllegalStateException {
        this.method = method;
        this.reloadRunner = new ReloadRunner();
        this.newVersions = new File(TUMain.getPlugin().getDataFolder() + "/newVersions");

        if (!isValid()) {
            throw new IllegalStateException("This mustn't happen.");
        }
    }

    @Override
    public void run() {
        try {
            File update = this.getNewestFile();

            if(method == UpdateMethod.OVERRIDE) {
                override(update, this.me);
            }


        } catch (VersionParsingException | IOException e) {
            System.err.println(e);
        }
    }

    public void override(File source, File destination) throws IOException {
        Files.copy(Paths.get(source.toURI()), Paths.get(destination.toURI()));
    }

    /**
     * Searches the plugin folder for
     * @param namePrefix the string the name of the plugin has to start with
     * @return
     */
    private File getSelf(String namePrefix) {
        File pluginFolder = new File("./plugins/");

        for (File f : pluginFolder.listFiles()) {
            if (f.isFile() && f.getName().startsWith(namePrefix)) return f;
        }

        return null;
    }

    /**
     * Checks if the state of the UpdateRunner is valid:
     * - HB
     * @return
     */
    private boolean isValid() {
        if(this.me == null || !this.me.exists()) {
            return false;
        }

        if(!this.newVersions.exists()) {
            if(!this.newVersions.mkdirs()) {
                return false;
                //throw new IllegalStateException("Not allowed to create missing new newVersions folder at:\n" + newVersions.getAbsolutePath());
            }
        }

        if(!this.newVersions.isDirectory()) {
            return false;
            //throw new IllegalStateException("New newVersions folder is not a directory:\n" + this.newVersions.getAbsolutePath());
        }

        return true;
    }

    /**
     * Looks through all the files in the new folder directory
     * @return
     * @throws VersionParsingException
     */
    public File getNewestFile() throws VersionParsingException {
        Version highest = new Version(-1, -1, -1);
        File file = null;

        for (File pl : this.newVersions.listFiles()) {
            if(pl.isFile() && pl.getName().endsWith(".jar")) {
                try {
                    Version v = Version.getFileVersion(pl);
                    if (v.compareTo(highest) == 1 || file == null) {
                        highest = v;
                        file = pl;
                    }
                } catch (VersionParsingException ex) {
                    System.err.println("Couldn't parse name: " + pl.getName());
                }
            }
        }

        return file;
    }

}
