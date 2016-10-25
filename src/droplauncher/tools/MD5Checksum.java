package droplauncher.tools;

import droplauncher.debugging.Debugging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import javax.xml.bind.DatatypeConverter;

public class MD5Checksum {

  private static final Logger LOGGER = LogManager.getRootLogger();

  public static final String EMPTY_CHECKSUM =
      "00000000000000000000000000000000";

  private MessageDigest md;
  private File file;

  private MD5Checksum() {}

  /**
   * Constructor which initializes the required MessageDigest object.
   *
   * @param file specified file to process
   */
  public MD5Checksum(File file) {
    this.file = file;
    try {
      this.md = MessageDigest.getInstance("MD5");
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
    }
  }

  /**
   * Returns the MD5 checksum of the specified file.
   *
   * @return
   *     the MD5 checksum of the specified file,
   *     otherwise {@link #EMPTY_CHECKSUM} if an error has occurred
   */
  @Override
  public String toString() {
    if (this.md == null) {
      LOGGER.warn("MessageDigest object is null");
      return MD5Checksum.EMPTY_CHECKSUM;
    } else if (this.file == null) {
      LOGGER.warn(Debugging.nullObject());
      return MD5Checksum.EMPTY_CHECKSUM;
    } else if (!MainTools.doesFileExist(file)) {
      LOGGER.warn(Debugging.fileDoesNotExist(file));
      return MD5Checksum.EMPTY_CHECKSUM;
    }

    try {
      this.md.update(Files.readAllBytes(Paths.get(file.getAbsolutePath())));
      byte[] digest = this.md.digest();
      String checksum = DatatypeConverter.printHexBinary(digest).toLowerCase();
      return checksum;
    } catch (Exception ex) {
      LOGGER.error(ex.getMessage(), ex);
    }

    return MD5Checksum.EMPTY_CHECKSUM;
  }

}
