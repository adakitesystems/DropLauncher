package adakite.windows.registry;

public class RegEntry {

  public enum Type {
    REG_SZ,       /* String */
    REG_BINARY,   /* Binary */
    REG_DWORD,    /* DWORD (32-bit) */
    REG_QWORD,    /* QWORD (64-bit) */
    REG_MULTI_SZ, /* Multi-String */
    REG_EXPAND_SZ /* Expandable String */
  }

}
