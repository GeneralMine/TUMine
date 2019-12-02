package ga.tumgaming.tumine.autoupdater;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.DigestInputStream;
import java.security.NoSuchAlgorithmException;

public class Hashing {

    public static String sha(String filepath) throws IOException {
        try {
        checksum(filepath, MessageDigest.getInstance("SHA-256"));
        } catch (IOException ex) {
            throw ex;
        } catch (NoSuchAlgorithmException ex) {
            // Will not happen since the algorithms name is hardcoded!
            return "";
        }
    }


    private static String checksum(String filepath, MessageDigest md) throws IOException {
        // file hashing with DigestInputStream
        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(filepath), md)) {
            while (dis.read() != -1) ; //empty loop to clear the data
            md = dis.getMessageDigest();
        }

        /* bytes to hex */
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

}
