/* MainWindow.java */

// CHECKSTYLE:OFF

package droplauncher;

import droplauncher.config.ConfigFile;
import droplauncher.bwheadless.BwHeadless;
import droplauncher.filedroplist.FileDropList;
import droplauncher.tools.FileArray;
import droplauncher.tools.MainTools;
import droplauncher.tools.MemoryFile;
import droplauncher.tools.ProcessPipe;
import droplauncher.tools.TokenArray;

import filedrop.FileDrop;
import java.awt.Color;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Class for main window.
 *
 * @author Adakite Systems
 * @author adakitesystems@gmail.com
 */
public class MainWindow extends JFrame {

  private static final Logger LOGGER =
      Logger.getLogger(MainWindow.class.getName());
  private static final boolean CLASS_DEBUG = (MainTools.DEBUG && true);

  public static MainWindow mainWindow;

  /**
   * Creates new form MainWindow.
   */
  /* Changed from public to private. */
  private MainWindow() {
    initComponents();
  }

  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the NetBeans Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    btngrpGameType = new javax.swing.ButtonGroup();
    btngrpRace = new javax.swing.ButtonGroup();
    btnEject = new javax.swing.JButton();
    boxDropFiles = new javax.swing.JLabel();
    btnLaunch = new javax.swing.JButton();
    txtBotName = new javax.swing.JTextField();
    lblBotName = new javax.swing.JLabel();
    rbZerg = new javax.swing.JRadioButton();
    rbTerran = new javax.swing.JRadioButton();
    rbRandom = new javax.swing.JRadioButton();
    rbProtoss = new javax.swing.JRadioButton();
    rbLocalPC = new javax.swing.JRadioButton();
    rbUDP = new javax.swing.JRadioButton();
    btnStarcraftDir = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    btnEject.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
    btnEject.setText("Eject");
    btnEject.setEnabled(false);

    boxDropFiles.setBackground(new java.awt.Color(138, 0, 0));
    boxDropFiles.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
    boxDropFiles.setForeground(new java.awt.Color(255, 255, 255));
    boxDropFiles.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    boxDropFiles.setText("Drop bot files here");
    boxDropFiles.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
    boxDropFiles.setOpaque(true);

    btnLaunch.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
    btnLaunch.setText("Launch");
    btnLaunch.setEnabled(false);

    txtBotName.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
    txtBotName.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        txtBotNameFocusLost(evt);
      }
    });
    txtBotName.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        txtBotNameKeyReleased(evt);
      }
    });

    lblBotName.setText(" Custom bot name (max 24 chars):");

    btngrpRace.add(rbZerg);
    rbZerg.setText("Zerg");
    rbZerg.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        rbZergActionPerformed(evt);
      }
    });

    btngrpRace.add(rbTerran);
    rbTerran.setText("Terran");
    rbTerran.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        rbTerranActionPerformed(evt);
      }
    });

    btngrpRace.add(rbRandom);
    rbRandom.setText("Random");
    rbRandom.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        rbRandomActionPerformed(evt);
      }
    });

    btngrpRace.add(rbProtoss);
    rbProtoss.setText("Protoss");
    rbProtoss.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        rbProtossActionPerformed(evt);
      }
    });

    btngrpGameType.add(rbLocalPC);
    rbLocalPC.setText("Local PC");
    rbLocalPC.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        rbLocalPCActionPerformed(evt);
      }
    });

    btngrpGameType.add(rbUDP);
    rbUDP.setText("Local Area Network (UDP)");
    rbUDP.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        rbUDPActionPerformed(evt);
      }
    });

    btnStarcraftDir.setText("<StarCraft Directory Not Set>");
    btnStarcraftDir.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnStarcraftDirActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addComponent(btnStarcraftDir, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addContainerGap())
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(boxDropFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addComponent(rbUDP, javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(rbLocalPC, javax.swing.GroupLayout.Alignment.LEADING))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(btnEject, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(btnLaunch, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 211, Short.MAX_VALUE)
                  .addComponent(txtBotName)
                  .addComponent(lblBotName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
              .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(rbZerg)
                  .addComponent(rbTerran))
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(rbRandom)
                  .addComponent(rbProtoss))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap(170, Short.MAX_VALUE)
        .addComponent(btnStarcraftDir)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(lblBotName)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(txtBotName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addComponent(rbLocalPC)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(rbUDP)))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
          .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(layout.createSequentialGroup()
                .addComponent(rbTerran)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rbZerg))
              .addGroup(layout.createSequentialGroup()
                .addComponent(rbProtoss)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rbRandom)))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(btnEject, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(9, 9, 9)
            .addComponent(btnLaunch, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(boxDropFiles, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        .addGap(7, 7, 7))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  private void rbLocalPCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbLocalPCActionPerformed
    BwHeadless.INSTANCE.setGameType(BwHeadless.GameType.localpc);
  }//GEN-LAST:event_rbLocalPCActionPerformed

  private void rbUDPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbUDPActionPerformed
    BwHeadless.INSTANCE.setGameType(BwHeadless.GameType.lan);
  }//GEN-LAST:event_rbUDPActionPerformed

  private void txtBotNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBotNameKeyReleased
    String text = txtBotName.getText();
    BwHeadless.INSTANCE.setBotName(text);
  }//GEN-LAST:event_txtBotNameKeyReleased

  private void rbTerranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbTerranActionPerformed
    BwHeadless.INSTANCE.setBotRace(BwHeadless.Race.Terran);
  }//GEN-LAST:event_rbTerranActionPerformed

  private void rbZergActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbZergActionPerformed
    BwHeadless.INSTANCE.setBotRace(BwHeadless.Race.Zerg);
  }//GEN-LAST:event_rbZergActionPerformed

  private void rbProtossActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbProtossActionPerformed
    BwHeadless.INSTANCE.setBotRace(BwHeadless.Race.Protoss);
  }//GEN-LAST:event_rbProtossActionPerformed

  private void rbRandomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbRandomActionPerformed
    BwHeadless.INSTANCE.setBotRace(BwHeadless.Race.Random);
  }//GEN-LAST:event_rbRandomActionPerformed

  private void btnStarcraftDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStarcraftDirActionPerformed
    JFileChooser fc = new JFileChooser();
    if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
      File file = fc.getSelectedFile();
      try {
        String filename = file.getCanonicalPath();
        if (!filename.toLowerCase().endsWith(".exe")) {
          if (CLASS_DEBUG) {
            LOGGER.log(Level.WARNING, "invalid StarCraft.exe");
          }
          MainTools.showWindowMessage("Invalid path to StarCraft.exe:\n\n" + filename);
          return;
        }
        BwHeadless.INSTANCE.setStarcraftExe(filename);
        btnStarcraftDir.setText(filename);
      } catch (IOException ex) {
        if (CLASS_DEBUG) {
          LOGGER.log(Level.SEVERE, null, ex);
        }
      }
    }
  }//GEN-LAST:event_btnStarcraftDirActionPerformed

  private void txtBotNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtBotNameFocusLost
    String str = txtBotName.getText();
    if (MainTools.isEmpty(str)) {
      txtBotName.setText(BwHeadless.DEFAULT_BOT_NAME);
    }
  }//GEN-LAST:event_txtBotNameFocusLost

  /**
   * Main function called when main window is displayed.
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    /* Set the Nimbus look and feel if available. */
    try {
      for (UIManager.LookAndFeelInfo info :
          UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (ClassNotFoundException
        | InstantiationException
        | IllegalAccessException
        | UnsupportedLookAndFeelException ex) {
      LOGGER.log(Level.SEVERE, null, ex);
    }

    /* Create and display the form. */
    mainWindow = new MainWindow();
    EventQueue.invokeLater(new Runnable() {
      @Override
      public void run() {
        /* Create or check for settings config file. */
        if (!MainTools.doesFileExist(BwHeadless.DEFAULT_CFG_FILE)) {
          BwHeadless.INSTANCE.createDefaultConfig();
        }

        mainWindow.updateInfo();
        mainWindow.enableLaunch(true);
        mainWindow.enableEject(true);

        mainWindow.setTitle(DropLauncher.PROGRAM_NAME);
        mainWindow.setResizable(false);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
      }
    });

    /*
     * Add FileDrop Listener. All valid dropped files are added to the
     * static container object named FileDropList.
     */
    FileDrop fileDrop = new FileDrop(
        mainWindow.boxDropFiles,
        new FileDrop.Listener() {
          @Override
          public void filesDropped(File[] files) {
            for (File file : files) {
              BwHeadless.INSTANCE.dropFile(file);
            }
          }
    });

    /* DEBUGGING --- start */
//    ArrayList<String> argsList = new ArrayList<>();
//    argsList.add("a");
//    argsList.add("b");
//    argsList.add("c");
//    argsList.add("d");
//    Object[] argsArray = argsList.toArray();
//    String[] argsArrayArray = (String[])argsArray;
//    int len = argsArray.length;
//    for (int i = 0; i < len; i++)
//    {
//      System.out.println(argsArray[i]);
//    }
//    ProcessBuilder p = new ProcessBuilder(argsArrayArray);
//    p.start();



//    ProcessPipe pipe = new ProcessPipe();
//    String path = "bwheadless_newer.exe";
//    TokenArray pipeArgs = new TokenArray();
////    pipeArgs.add("-e \"S:\\install\\StarCraft\\StarCraft.exe\"");
//    pipeArgs.add("-e");
//    pipeArgs.add("\"S:\\install\\StarCraft\\StarCraft.exe\"");
//    pipeArgs.add("-j");
////    pipeArgs.add("-n IronBot");
//    pipeArgs.add("-n");
//    pipeArgs.add("IronBot");
////    pipeArgs.add("-r Terran");
//    pipeArgs.add("-r");
//    pipeArgs.add("Terran");
////    pipeArgs.add("-l BWAPI.dll");
//    pipeArgs.add("-l");
//    pipeArgs.add("BWAPI.dll");
//    pipeArgs.add("--lan");
////    pipeArgs.add("--installpath \"S:\\install\\StarCraft\"");
//    pipeArgs.add("--installpath");
//    pipeArgs.add("\"S:\\install\\StarCraft\"");
//    if (!pipe.open(path, pipeArgs.toStringArray())) {
//      System.out.println("error opening pipe");
//    }
//    System.out.println(path + " " + pipeArgs.toString());



//    MemoryFile mf = new MemoryFile();
//    mf.readIntoMemory("bwapi.ini");
//    int index = mf.getIndexStartsWith("ai");
//    String newDll;
//    String tmpLine;
//    TokenArray ta = new TokenArray();
//    if (index >= 0) {
//      System.out.println(mf.getLines().get(index));
//      tmpLine = mf.getLines().get(index);
//      newDll = tmpLine.substring(tmpLine.indexOf("=") + 2, tmpLine.length());
//      newDll = MainTools.getParentDirectory(newDll) + "\\NewBot.dll";
//      System.out.println("ai = " + newDll);
////      mf.writeToDisk(mf.getPath());
//    }
//    mf.printToConsole();



//      ConfigFile cf = new ConfigFile();
//      if (cf.open("bwapi.ini")) {
////        cf.setVariable("ai", "S:\\install\\StarCraft\\bwapi-data\\AI\\LetaBot.dll");
////        cf.setVariable("holiday", "ON");
////        System.out.println("ai = " + cf.getValue("ai"));
////        System.out.println("holiday = " + cf.getValue("holiday"));
////        cf.enableVariable("ai");
////        System.out.println("ai = " + cf.getValue("ai"));
//        System.out.println(cf.getValue("ai"));
//        cf.disableVariable("ai");
//        System.out.println(cf.getValue("ai"));
//        cf.enableVariable("ai");
//        cf.setVariable("ai", "S:\\install\\StarCraft\\bwapi-data\\AI\\Iron.dll");
//        System.out.println(cf.getValue("ai"));
//      } else {
//        System.out.println("error");
//      }

//    System.out.println(BwHeadless.INSTANCE.bwapiDllChecksums.getValue("BWAPI.dll 4.1.0b"));
//    System.out.println(BwHeadless.INSTANCE.bwapiDllChecksums.getName("5d5128709ba714aa9c6095598bcf4624"));
    /* DEBUGGING --- end */
  }

  public void updateInfo() {
    BwHeadless.GameType gameType = BwHeadless.INSTANCE.getGameType();
    switch (gameType) {
      case localpc:
        mainWindow.rbLocalPC.setSelected(true);
        break;
      case lan:
        mainWindow.rbUDP.setSelected(true);
        break;
      default:
        break;
    }

    BwHeadless.Race race = BwHeadless.INSTANCE.getBotRace();
    switch (race) {
      case Terran:
        mainWindow.rbTerran.setSelected(true);
        break;
      case Zerg:
        mainWindow.rbZerg.setSelected(true);
        break;
      case Protoss:
        mainWindow.rbProtoss.setSelected(true);
        break;
      case Random:
        mainWindow.rbRandom.setSelected(true);
        break;
      default:
        break;
    }

    mainWindow.txtBotName.setText(BwHeadless.INSTANCE.getBotName());
  }

  public void enableLaunch(boolean status) {
    mainWindow.btnLaunch.setEnabled(status);
  }

  public void enableEject(boolean status) {
    mainWindow.btnEject.setEnabled(status);
  }

  public void setBoxDropFile(boolean status) {
    if (status) {
      mainWindow.boxDropFiles.setBackground(new Color(255, 255, 255));
    } else {
      mainWindow.boxDropFiles.setBackground(new Color(100, 0, 0));
    }
  }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel boxDropFiles;
  private javax.swing.JButton btnEject;
  private javax.swing.JButton btnLaunch;
  private javax.swing.JButton btnStarcraftDir;
  private javax.swing.ButtonGroup btngrpGameType;
  private javax.swing.ButtonGroup btngrpRace;
  private javax.swing.JLabel lblBotName;
  private javax.swing.JRadioButton rbLocalPC;
  private javax.swing.JRadioButton rbProtoss;
  private javax.swing.JRadioButton rbRandom;
  private javax.swing.JRadioButton rbTerran;
  private javax.swing.JRadioButton rbUDP;
  private javax.swing.JRadioButton rbZerg;
  private javax.swing.JTextField txtBotName;
  // End of variables declaration//GEN-END:variables

}
