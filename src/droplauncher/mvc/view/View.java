/*
TODO: Save JFileChooser path after selecting files and load next time a
JFileChooser is displayed.
*/

package droplauncher.mvc.view;

import adakite.utils.AdakiteUtils;
import droplauncher.bwapi.BWAPI;
import droplauncher.mvc.model.Model;
import droplauncher.starcraft.Race;
import droplauncher.util.Constants;
import filedrop.FileDrop;
import java.io.File;
import java.nio.file.Paths;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class View extends JFrame {

  private static final Logger LOGGER = Logger.getLogger(View.class.getName());
  private static final boolean CLASS_DEBUG = (Constants.DEBUG && true);

  private Model model;

  private String jFileChooserDirectory;

  public View() {
    this.jFileChooserDirectory = "";

    /* Set the Nimbus look and feel. */
    try {
      for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    } catch (Exception ex) {
      if (CLASS_DEBUG) {
        LOGGER.log(Constants.DEFAULT_LOG_LEVEL, null, ex);
      }
    }

    initComponents();

    /* Form settings */
    setTitle(Constants.PROGRAM_TITLE);
    setResizable(false);
    setLocationRelativeTo(null);
    setVisible(true);

    FileDrop.Listener fileDropListener = new FileDropListener(this);
    new FileDrop(this.boxDropFiles, fileDropListener);
  }

  public void setModel(Model model) {
    this.model = model;
  }

  /* ************************************************************ */
  /**
   * This method is called from within the constructor to initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor in NetBeans.
   */
  /* ************************************************************ */

  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    btngrpRace = new javax.swing.ButtonGroup();
    boxDropFiles = new javax.swing.JLabel();
    btnLaunch = new javax.swing.JButton();
    rbRaceTerran = new javax.swing.JRadioButton();
    rbRaceZerg = new javax.swing.JRadioButton();
    rbRaceProtoss = new javax.swing.JRadioButton();
    rbRaceRandom = new javax.swing.JRadioButton();
    txtBotName = new javax.swing.JTextField();
    lblBotName = new javax.swing.JLabel();
    lblBwapiDll = new javax.swing.JLabel();
    lblBwapiDllVersion = new javax.swing.JLabel();
    lblBotFile = new javax.swing.JLabel();
    lblBwapiDllText = new javax.swing.JLabel();
    lblBwapiDllVersionText = new javax.swing.JLabel();
    lblBotFileText = new javax.swing.JLabel();
    lblStarcraftExe = new javax.swing.JLabel();
    btnStarcraftExe = new javax.swing.JButton();
    lblStarcraftExeText = new javax.swing.JLabel();

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

    boxDropFiles.setBackground(new java.awt.Color(0, 53, 137));
    boxDropFiles.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
    boxDropFiles.setForeground(new java.awt.Color(204, 204, 204));
    boxDropFiles.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    boxDropFiles.setText("Drop bot files here");
    boxDropFiles.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    boxDropFiles.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    boxDropFiles.setOpaque(true);
    boxDropFiles.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(java.awt.event.MouseEvent evt) {
        boxDropFilesMouseClicked(evt);
      }
    });

    btnLaunch.setText("Launch");
    btnLaunch.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnLaunchActionPerformed(evt);
      }
    });

    btngrpRace.add(rbRaceTerran);
    rbRaceTerran.setText("Terran");
    rbRaceTerran.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        rbRaceTerranActionPerformed(evt);
      }
    });

    btngrpRace.add(rbRaceZerg);
    rbRaceZerg.setText("Zerg");
    rbRaceZerg.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        rbRaceZergActionPerformed(evt);
      }
    });

    btngrpRace.add(rbRaceProtoss);
    rbRaceProtoss.setText("Protoss");
    rbRaceProtoss.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        rbRaceProtossActionPerformed(evt);
      }
    });

    btngrpRace.add(rbRaceRandom);
    rbRaceRandom.setText("Random");
    rbRaceRandom.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        rbRaceRandomActionPerformed(evt);
      }
    });

    txtBotName.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(java.awt.event.KeyEvent evt) {
        txtBotNameKeyReleased(evt);
      }
    });

    lblBotName.setText(" Bot name (max 24 characters):");

    lblBwapiDll.setText("BWAPI.dll:");

    lblBwapiDllVersion.setText("BWAPI Version:");

    lblBotFile.setText("Bot file:");

    lblBwapiDllText.setText(" ");

    lblBwapiDllVersionText.setText(" ");

    lblBotFileText.setText(" ");

    lblStarcraftExe.setText("StarCraft.exe:");

    btnStarcraftExe.setFont(new java.awt.Font("Serif", 1, 12)); // NOI18N
    btnStarcraftExe.setText("...");
    btnStarcraftExe.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnStarcraftExeActionPerformed(evt);
      }
    });

    lblStarcraftExeText.setText(" ");

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
          .addGroup(layout.createSequentialGroup()
            .addGap(261, 261, 261)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(rbRaceTerran)
              .addComponent(rbRaceZerg))
            .addGap(44, 44, 44)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addComponent(rbRaceRandom)
              .addComponent(rbRaceProtoss))
            .addContainerGap(28, Short.MAX_VALUE))
          .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(boxDropFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addComponent(btnLaunch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(txtBotName)
                  .addComponent(lblBotName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
              .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblBwapiDll, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                      .addComponent(lblBotFile, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                      .addComponent(lblBwapiDllVersion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)))
                  .addComponent(lblStarcraftExe))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                  .addComponent(lblBwapiDllText, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE)
                  .addComponent(lblBotFileText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(lblBwapiDllVersionText, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addGroup(layout.createSequentialGroup()
                    .addComponent(btnStarcraftExe, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(lblStarcraftExeText, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)))))
            .addContainerGap())))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addGap(14, 14, 14)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(lblStarcraftExe)
          .addComponent(btnStarcraftExe, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(lblStarcraftExeText))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(lblBwapiDll)
          .addComponent(lblBwapiDllText))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(lblBwapiDllVersion)
          .addComponent(lblBwapiDllVersionText))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(lblBotFile)
          .addComponent(lblBotFileText))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addComponent(lblBotName)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
          .addGroup(layout.createSequentialGroup()
            .addComponent(txtBotName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(rbRaceProtoss)
              .addComponent(rbRaceTerran))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
              .addComponent(rbRaceZerg)
              .addComponent(rbRaceRandom))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(btnLaunch, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE))
          .addComponent(boxDropFiles, javax.swing.GroupLayout.PREFERRED_SIZE, 159, javax.swing.GroupLayout.PREFERRED_SIZE))
        .addGap(7, 7, 7))
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

  /* ************************************************************ */
  /*
    Methods for updating View components or displaying extra components
    such as a file dialog or message box.
   */
  /* ************************************************************ */

  public JLabel getLabelBoxDropFiles() { return this.boxDropFiles; }
  public JButton getButtonLaunch() { return this.btnLaunch; }
  public ButtonGroup getButtonGroupRace() { return this.btngrpRace; }
  public JLabel getLabelBotFile() { return this.lblBotFile; }
  public JLabel getLabelBotFileText() { return this.lblBotFileText; }
  public JLabel getLabelBotName() { return this.lblBotName; }
  public JLabel getLabelBwapiDll() { return this.lblBwapiDll; }
  public JLabel getLabelBwapiDllText() { return this.lblBwapiDllText; }
  public JLabel getLabelBwapiDllVersion() { return this.lblBwapiDllVersion; }
  public JLabel getLabelBwapiDllVersionText() { return this.lblBwapiDllVersionText; }
  public JLabel getLabelStarcraftExe() { return this.lblStarcraftExe; }
  public JButton getButtonStarcraftExe() { return this.btnStarcraftExe; }
  public JLabel getLabelStarcraftExeText() { return this.lblStarcraftExeText; }
  public JRadioButton getRadioButtonProtoss() { return this.rbRaceProtoss; }
  public JRadioButton getRadioButtonRandom() { return this.rbRaceRandom; }
  public JRadioButton getRadioButtonTerran() { return this.rbRaceTerran; }
  public JRadioButton getRadioButtonZerg() { return this.rbRaceZerg; }
  public JTextField getTextFieldBotName() { return this.txtBotName; }

  public int showFileChooser(JFileChooser fc) {
    /* Load previous directory. */
    if (!AdakiteUtils.isNullOrEmpty(this.jFileChooserDirectory)
        && AdakiteUtils.directoryExists(Paths.get(this.jFileChooserDirectory))) {
      fc.setCurrentDirectory(new File(this.jFileChooserDirectory));
    }

    int status = fc.showOpenDialog(fc);

    /* Save current directory. */
    String currentDirectory = fc.getCurrentDirectory().getAbsolutePath();
    if (!AdakiteUtils.isNullOrEmpty(currentDirectory)) {
      this.jFileChooserDirectory = currentDirectory;
    }

    return status;
  }

  public void showMessageBox(int messageType, String str) {
    JOptionPane.showMessageDialog(
        this,
        str,
        Constants.PROGRAM_NAME,
        messageType
    );
  }

  public void update() {
    /* StarCraft.exe */
    if (!AdakiteUtils.isNullOrEmpty(this.model.getBWHeadless().getStarcraftExe(), true)) {
      setText(this.lblStarcraftExeText, this.model.getBWHeadless().getStarcraftExe());
    }

    if (!AdakiteUtils.isNullOrEmpty(this.model.getBWHeadless().getBwapiDll(), true)) {
      /* BWAPI.dll */
      setText(this.lblBwapiDllText, new File(this.model.getBWHeadless().getBwapiDll()).getName());
      /* BWAPI.dll version */
      setText(this.lblBwapiDllVersionText, BWAPI.getBwapiVersion(new File(this.model.getBWHeadless().getBwapiDll())));
    } else {
      setText(this.lblBwapiDllText, "");
      setText(this.lblBwapiDllVersionText, "");
    }

    /* Bot file */
    if (!AdakiteUtils.isNullOrEmpty(this.model.getBWHeadless().getBotDll(), true)) {
      /* .dll */
      setText(this.lblBotFileText, new File(this.model.getBWHeadless().getBotDll()).getName());
    } else if (!AdakiteUtils.isNullOrEmpty(this.model.getBWHeadless().getBotClient(), true)) {
      /* Client */
      setText(this.lblBotFileText, new File(this.model.getBWHeadless().getBotClient()).getName());
    } else {
      setText(this.lblBotFileText, "");
    }

    /* Bot name */
    setText(this.txtBotName, this.model.getBWHeadless().getBotName());

    /* Bot race */
    Race race = this.model.getBWHeadless().getBotRace();
    if (race != null && race != Race.NONE) {
      switch (race) {
        case TERRAN:
          this.rbRaceTerran.setSelected(true);
          break;
        case ZERG:
          this.rbRaceZerg.setSelected(true);
          break;
        case PROTOSS:
          this.rbRaceProtoss.setSelected(true);
          break;
        case RANDOM:
          this.rbRaceRandom.setSelected(true);
          break;
        default:
          break;
      }
    } else {
      clearRaceSelection();
    }
  }

  private void setText(JComponent component, String str) {
    if (AdakiteUtils.isNullOrEmpty(str, true)) {
      str = "";
    }
    if (component instanceof JTextField) {
      JTextField tf = (JTextField) component;
      tf.setText(str);
    } else if (component instanceof JLabel) {
      JLabel l = (JLabel) component;
      l.setText(str);
    }
  }

  private void clearRaceSelection() {
    this.btngrpRace.clearSelection();
  }

  /* ************************************************************ */
  /*
    Detected Events
      When the user clicks a button or changes a radio button, etc.

    Note: These methods are autogenerated by the Form Editor in NetBeans.
   */
  /* ************************************************************ */

  private void btnLaunchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLaunchActionPerformed
    this.model.btnLaunchActionPerformed(evt);
  }//GEN-LAST:event_btnLaunchActionPerformed

  private void rbRaceTerranActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbRaceTerranActionPerformed
    this.model.rbRaceTerranActionPerformed(evt);
  }//GEN-LAST:event_rbRaceTerranActionPerformed

  private void rbRaceProtossActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbRaceProtossActionPerformed
    this.model.rbRaceProtossActionPerformed(evt);
  }//GEN-LAST:event_rbRaceProtossActionPerformed

  private void rbRaceRandomActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbRaceRandomActionPerformed
    this.model.rbRaceRandomActionPerformed(evt);
  }//GEN-LAST:event_rbRaceRandomActionPerformed

  private void rbRaceZergActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbRaceZergActionPerformed
    this.model.rbRaceZergActionPerformed(evt);
  }//GEN-LAST:event_rbRaceZergActionPerformed

  private void txtBotNameKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBotNameKeyReleased
    this.model.txtBotNameKeyReleased(evt);
  }//GEN-LAST:event_txtBotNameKeyReleased

  private void btnStarcraftExeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStarcraftExeActionPerformed
    this.model.btnStarcraftExeActionPerformed(evt);
  }//GEN-LAST:event_btnStarcraftExeActionPerformed

  private void boxDropFilesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_boxDropFilesMouseClicked
    this.model.boxDropFilesMouseClicked(evt);
  }//GEN-LAST:event_boxDropFilesMouseClicked

  /* ************************************************************ */
  /*
    Detected Events

    Note: These methods are NOT autogenerated.
   */
  /* ************************************************************ */

  public void filesDropped(File[] files) {
    this.model.filesDropped(files);
  }

  /* ************************************************************ */
  /*
    Form Component Declarations

    Note: These class members are autogenerated by the Form Editor
    in NetBeans.
   */
  /* ************************************************************ */

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel boxDropFiles;
  private javax.swing.JButton btnLaunch;
  private javax.swing.JButton btnStarcraftExe;
  private javax.swing.ButtonGroup btngrpRace;
  private javax.swing.JLabel lblBotFile;
  private javax.swing.JLabel lblBotFileText;
  private javax.swing.JLabel lblBotName;
  private javax.swing.JLabel lblBwapiDll;
  private javax.swing.JLabel lblBwapiDllText;
  private javax.swing.JLabel lblBwapiDllVersion;
  private javax.swing.JLabel lblBwapiDllVersionText;
  private javax.swing.JLabel lblStarcraftExe;
  private javax.swing.JLabel lblStarcraftExeText;
  private javax.swing.JRadioButton rbRaceProtoss;
  private javax.swing.JRadioButton rbRaceRandom;
  private javax.swing.JRadioButton rbRaceTerran;
  private javax.swing.JRadioButton rbRaceZerg;
  private javax.swing.JTextField txtBotName;
  // End of variables declaration//GEN-END:variables

}
