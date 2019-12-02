package ga.tumgaming.tumine.autoupdater;

import java.io.File;

public class Version implements Comparable<Version> {

    private int major;
    private int minor;
    private int patch;

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static Version parse(String input) throws VersionParsingException {
        String[] dotSplit = input.split(".");

        if (dotSplit.length != 3) {
            throw new VersionParsingException("Incorrect formatting or length. Want <MAJOR>");
        }

        try {
            int major = Integer.parseInt(dotSplit[0]);
            int minor = Integer.parseInt(dotSplit[1]);
            int patch = Integer.parseInt(dotSplit[2]);

            return new Version(major, minor, patch);
        } catch (NumberFormatException ex) {
            throw new VersionParsingException("NumberFormatException: " + ex.getMessage());
        }
    }

    @Override
    public int compareTo(Version o) {
        // comparing the majors
        if (this.getMajor() > o.getMajor()) {
            return 1;
        } else if (this.getMajor() < o.getMajor()) {
            return -1;
        }

        // comparing the minors
        if (this.getMinor() > o.getMinor()) {
            return 1;
        } else if (this.getMinor() < o.getMinor()) {
            return -1;
        }

        // comparing the patch
        if (this.getPatch() > o.getPatch()) {
            return 1;
        } else if (this.getPatch() < o.getPatch()) {
            return -1;
        }

        // all three are the same
        return 0;
    }

    /**
     * Parses the version of a file whose name looks like "smth-1.2.3.jar"
     * if couldn't read returns Version(-1,-1,-1) or VersionParsingException
     *
     * @param pl the plugin file
     * @return Version parsed from File.getName()
     * @throws VersionParsingException when encountered an bad formatted version
     */
    public static Version getFileVersion(File pl) throws VersionParsingException {
        if (pl == null) return new Version(-1, -1, -1);
        if (pl.isFile() && pl.getName().endsWith(".jar")) {

            // abc-1.2.3.jar
            String v = pl.getName();

            // abc-1.2.3
            v = v.replaceFirst("[.][^.]+$", "");


            // 1.2.3
            String[] splitDash = v.split("-");
            if (splitDash.length != 2) {
                throw new VersionParsingException("Name of file is not correctly formatted!");
            }

            v = splitDash[1];

            // Version (1, 2, 3)
            return Version.parse(v);
        } else {
            throw new VersionParsingException();
        }
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }
}
