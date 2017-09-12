package adakite.process;

import adakite.debugging.Debugging;
import adakite.util.AdakiteUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class for building objects to pass to process objects
 * (e.g. passing arguments to a ProcessBuilder object).
 */
public class CommandBuilder {

  private Path path;
  private List<String> args;

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
  public CommandBuilder setPath(Path path) {
    if (path == null) {
      throw new IllegalArgumentException(Debugging.nullObject("path"));
    }
    this.path = path;
    return this;
  }

  /**
   * Returns a String array of the command and arguments.
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
   * Adds the specified argument to the list of arguments.
   *
   * @param args specified argument
   */
  public CommandBuilder addArg(String... args) {
    if (args == null || args.length < 1) {
      throw new IllegalArgumentException(Debugging.nullObject("args"));
    }
    for (String arg : args) {
      this.args.add(arg);
    }
    return this;
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

  /**
   * Replaces the current argument list with the specified argument list.
   *
   * @param args specified argument list.
   */
  public CommandBuilder setArgs(String[] args) {
    clearArgs();
    this.args.addAll(Arrays.asList(args));
    return this;
  }

  /**
   * Clears the current argument list.
   */
  public CommandBuilder clearArgs() {
    this.args.clear();
    return this;
  }

  /**
   * Returns the command and arguments as one string.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (this.path == null) {
      sb.append("null");
    } else {
      sb.append(this.path.toString());
    }
    this.args.forEach((arg) -> {
      sb.append(" ").append(arg);
    });
    return sb.toString();
  }

}
