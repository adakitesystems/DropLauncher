/*
TODO: Use a lock for MessageDigest or remove the static attribute from all methods.
*/

package adakite.md5sum;

import adakite.debugging.Debugging;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;

public class MD5Checksum {

  private static final Logger LOGGER = Logger.getLogger(MD5Checksum.class.getName());

  public static final String EMPTY_MD5_CHECKSUM = "00000000000000000000000000000000";

  private static MessageDigest md = null;

  private MD5Checksum() {}

  private static void ensureInit() throws NoSuchAlgorithmException {
    if (md == null) {
      md = MessageDigest.getInstance("MD5");
    }
  }

  public static String get(Path path) throws IOException, NoSuchAlgorithmException {
    ensureInit();
    if (Files.size(path) < 128 * 1024 * 1024) {
      md.update(Files.readAllBytes(path.toAbsolutePath()));
      byte[] digest = md.digest();
      String checksum = DatatypeConverter.printHexBinary(digest).toLowerCase(Locale.US);
      return checksum;
    } else {
      LOGGER.log(Debugging.getLogLevel(), "filesize too large: " + path.toAbsolutePath().toString());
    }
    return MD5Checksum.EMPTY_MD5_CHECKSUM;
  }

}
