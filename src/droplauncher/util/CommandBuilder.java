package droplauncher.util;

import adakite.debugging.Debugging;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Class for building objects to pass to process objects such as ProcessBuilder.
 */
public class CommandBuilder {

  private Path path;
  private ArrayList<String> args;

  public CommandBuilder() {
    this.path = null;
    this.args = new ArrayList<>();
  }

  /**
   * Returns the path to the executable.
   */
  public Path getPath() {
    return this.path;
  }

  /**
   * Sets the path to the specified executable.
   *
   * @param path path to the specified executable
   */
  public void setPath(Path path) {
    if (path == null) {
      throw new IllegalArgumentException(Debugging.nullObject("path"));
    }
    this.path = path;
  }

  public void addArg(String arg) {
    if (arg == null) {
      throw new IllegalArgumentException(Debugging.emptyString("arg"));
    }
    this.args.add(arg);
  }

  /**
   * Returns the argument list as a String array.
   */
  public String[] getArgs() {
    if (this.args.size() < 1) {
      return new String[]{};
    }
    String[] args = new String[this.args.size()];
    for (int i = 0; i < this.args.size(); i++) {
      args[i] = this.args.get(i);
    }
    return args;
  }

  public void setArgs(String[] args) {
    this.args.clear();
    for (String arg : args) {
      this.args.add(arg);
    }
  }

  /**
   * Returns a String array of the command and args.
   */
  public String[] get() {
    if (this.path == null) {
      throw new IllegalStateException(Debugging.nullObject("path"));
    }
    String[] command = new String[1 + this.args.size()];
    command[0] = this.path.toString();
    for (int i = 0; i < this.args.size(); i++) {
      command[i + 1] = this.args.get(i);
    }
    return command;
  }

  /**
   * Returns the command and args as one string.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (this.path == null) {
      sb.append("null");
    } else {
      sb.append(this.path.toString());
    }
    for (String arg : this.args) {
      sb.append(" " + arg);
    }
    return sb.toString();
  }

}
