package droplauncher.tools;

import droplauncher.debugging.Debugging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;

public class MD5Checksum {

  public static final MD5Checksum INSTANCE = new MD5Checksum();

  private static final Logger LOGGER = LogManager.getRootLogger();

  public static final String EMPTY_MD5_CHECKSUM = "00000000000000000000000000000000";

  private static MessageDigest md;

  private MD5Checksum() {
    try {
      md = MessageDigest.getInstance("MD5");
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
  }

  /**
   * Returns the MD5 checksum of the specified file.
   *
   * @param path specified path to file
   * @return
   *     the MD5 checksum of the specified file,
   *     otherwise {@link #EMPTY_MD5_CHECKSUM} if an error occurred
   */
  public static String getMD5Checksum(String path) {
    if (!MainTools.doesFileExist(path)) {
      LOGGER.warn(Debugging.EMPTY_STRING);
      return EMPTY_MD5_CHECKSUM;
    }
    try {
      md.update(Files.readAllBytes(Paths.get(path)));
      byte[] digest = md.digest();
      String checksum = DatatypeConverter.printHexBinary(digest).toLowerCase();
      return checksum;
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
    return EMPTY_MD5_CHECKSUM;
  }

}
