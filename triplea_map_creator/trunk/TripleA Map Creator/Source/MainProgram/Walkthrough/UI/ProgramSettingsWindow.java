/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ProgramAboutBox.java
 *
 * Created on May 19, 2010, 11:52:06 PM
 */

package MainProgram.Walkthrough.UI;

import Global.ProgramSettings;
import MainProgram.Console.ErrorConsole;
import MainProgram.ProgramLaunchers.ProgramLauncher;
import com.sun.java.swing.plaf.motif.MotifLookAndFeel;
import com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel;
import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import org.jdesktop.application.Action;

/**
 *
 * @author Stephen
 */
public class ProgramSettingsWindow extends javax.swing.JDialog
{
    /** Creates new form ProgramAboutBox */
    public ProgramSettingsWindow(WalkthroughWindow window)
    {
        initComponents();
        setModalityType(ModalityType.APPLICATION_MODAL);
        setIconImage(ProgramLauncher.getProgramIcon(this));
        InitControls(ProgramSettings.LoadSettings());
        m_walkthroughWindow = window;
    }
    WalkthroughWindow m_walkthroughWindow = null;
    private void InitControls(ProgramSettings settings)
    {
        //General
        v_maxBackupsSpinner.setValue(settings.MaxAutoBackupSize);
        v_showConfirmationBoxesForDataEditingCB.setSelected(settings.ShowConfirmationBoxesForDataEditing);
        v_showErrorConsoleWhenErrorOccursCB.setSelected(settings.ShowErrorConsoleWhenErrorOccurs);
        
        //Graphics
        v_programLandFCB.setSelectedIndex(settings.ProgramLookAndFeelIndex);
        v_toolPreviewWhileMovingMouse.setSelected(settings.ToolPreviewWhileMovingMouse);
        
        //Advanced
        v_maxJavaHeapSizeSP.setValue(settings.MaxJavaHeapSize);
        v_maxStepsSpinner.setValue(settings.StepHistoryMaxSize);
        v_maxPolygonCyclesSpinner.setValue(settings.MaxPolyMultiSearchCycles);
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        v_closeButton1 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        v_showConfirmationBoxesForDataEditingCB = new javax.swing.JCheckBox();
        v_showErrorConsoleWhenErrorOccursCB = new javax.swing.JCheckBox();
        v_maxBackupsSpinner = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        v_programLandFCB = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        v_toolPreviewWhileMovingMouse = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        v_maxStepsSpinner = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        v_maxPolygonCyclesSpinner = new javax.swing.JSpinner();
        jPanel7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        v_maxJavaHeapSizeSP = new javax.swing.JSpinner();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        v_cancelButton = new javax.swing.JButton();
        v_saveButton = new javax.swing.JButton();
        v_settingsDetails = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance().getContext().getResourceMap(ProgramSettingsWindow.class);
        setTitle(resourceMap.getString("Form.title")); // NOI18N
        setName("Form"); // NOI18N
        setResizable(false);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance().getContext().getActionMap(ProgramSettingsWindow.class, this);
        v_closeButton1.setAction(actionMap.get("RestoreDefaults")); // NOI18N
        v_closeButton1.setText(resourceMap.getString("v_closeButton1.text")); // NOI18N
        v_closeButton1.setName("v_closeButton1"); // NOI18N

        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        v_showConfirmationBoxesForDataEditingCB.setSelected(true);
        v_showConfirmationBoxesForDataEditingCB.setText(resourceMap.getString("v_showConfirmationBoxesForDataEditingCB.text")); // NOI18N
        v_showConfirmationBoxesForDataEditingCB.setName("v_showConfirmationBoxesForDataEditingCB"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        jPanel1.add(v_showConfirmationBoxesForDataEditingCB, gridBagConstraints);

        v_showErrorConsoleWhenErrorOccursCB.setSelected(true);
        v_showErrorConsoleWhenErrorOccursCB.setText(resourceMap.getString("v_showErrorConsoleWhenErrorOccursCB.text")); // NOI18N
        v_showErrorConsoleWhenErrorOccursCB.setName("v_showErrorConsoleWhenErrorOccursCB"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        jPanel1.add(v_showErrorConsoleWhenErrorOccursCB, gridBagConstraints);

        v_maxBackupsSpinner.setModel(new javax.swing.SpinnerNumberModel(10, 0, 100, 1));
        v_maxBackupsSpinner.setName("v_maxBackupsSpinner"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        jPanel1.add(v_maxBackupsSpinner, gridBagConstraints);

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setToolTipText(resourceMap.getString("jLabel4.toolTipText")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 0);
        jPanel1.add(jLabel4, gridBagConstraints);

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        jPanel1.add(jLabel6, gridBagConstraints);

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        jPanel1.add(jLabel7, gridBagConstraints);

        jPanel8.setName("jPanel8"); // NOI18N

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 99.0;
        gridBagConstraints.weighty = 99.0;
        jPanel1.add(jPanel8, gridBagConstraints);

        jTabbedPane1.addTab(resourceMap.getString("jPanel1.TabConstraints.tabTitle"), jPanel1); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setLayout(new java.awt.GridBagLayout());

        v_programLandFCB.setMaximumRowCount(10);
        v_programLandFCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Platform Independent", "Nimbus (Recommended)", "Metal", "Motif", "System", "Windows", "Windows Classic", "Substance Autumn", "Substance Challenger Deep", "Substance Creme Coffee", "Substance Creme", "Substance Dust Coffee", "Substance Dust", "Substance Emerald Dusk", "Substance Magma", "Substance Mist Aqua", "Substance Moderate", "Substance Nebula", "Substance Raven Graphite Glass", "Substance Raven Graphite", "Substance Raven", "Substance Twighlight" }));
        v_programLandFCB.setSelectedItem(v_programLandFCB.getItemAt(1));
        v_programLandFCB.setName("v_programLandFCB"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 32;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        jPanel2.add(v_programLandFCB, gridBagConstraints);

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 0);
        jPanel2.add(jLabel5, gridBagConstraints);

        v_toolPreviewWhileMovingMouse.setSelected(true);
        v_toolPreviewWhileMovingMouse.setText(resourceMap.getString("v_toolPreviewWhileMovingMouse.text")); // NOI18N
        v_toolPreviewWhileMovingMouse.setName("v_toolPreviewWhileMovingMouse"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        jPanel2.add(v_toolPreviewWhileMovingMouse, gridBagConstraints);

        jPanel3.setName("jPanel3"); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 99.0;
        gridBagConstraints.weighty = 99.0;
        jPanel2.add(jPanel3, gridBagConstraints);

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        jPanel2.add(jLabel2, gridBagConstraints);

        jTabbedPane1.addTab(resourceMap.getString("jPanel2.TabConstraints.tabTitle"), jPanel2); // NOI18N

        jPanel4.setName("jPanel4"); // NOI18N
        jPanel4.setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        jPanel4.add(jLabel1, gridBagConstraints);

        v_maxStepsSpinner.setModel(new javax.swing.SpinnerNumberModel(10, 0, 100, 1));
        v_maxStepsSpinner.setName("v_maxStepsSpinner"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        jPanel4.add(v_maxStepsSpinner, gridBagConstraints);

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 5, 0);
        jPanel4.add(jLabel3, gridBagConstraints);

        v_maxPolygonCyclesSpinner.setModel(new javax.swing.SpinnerNumberModel(9, 1, 500, 1));
        v_maxPolygonCyclesSpinner.setName("v_maxPolygonCyclesSpinner"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        jPanel4.add(v_maxPolygonCyclesSpinner, gridBagConstraints);

        jPanel7.setName("jPanel7"); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 99.0;
        gridBagConstraints.weighty = 99.0;
        jPanel4.add(jPanel7, gridBagConstraints);

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 0);
        jPanel4.add(jLabel8, gridBagConstraints);

        v_maxJavaHeapSizeSP.setModel(new javax.swing.SpinnerNumberModel(750, 50, 25000, 50));
        v_maxJavaHeapSizeSP.setName("v_maxJavaHeapSizeSP"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        jPanel4.add(v_maxJavaHeapSizeSP, gridBagConstraints);

        jTabbedPane1.addTab(resourceMap.getString("jPanel4.TabConstraints.tabTitle"), jPanel4); // NOI18N

        jPanel5.setName("jPanel5"); // NOI18N
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jPanel6.setName("jPanel6"); // NOI18N

        v_cancelButton.setAction(actionMap.get("Cancel")); // NOI18N
        v_cancelButton.setText(resourceMap.getString("v_cancelButton.text")); // NOI18N
        v_cancelButton.setName("v_cancelButton"); // NOI18N

        v_saveButton.setAction(actionMap.get("Close")); // NOI18N
        v_saveButton.setText(resourceMap.getString("v_saveButton.text")); // NOI18N
        v_saveButton.setName("v_saveButton"); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(v_saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(v_cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
            .addComponent(v_saveButton, javax.swing.GroupLayout.DEFAULT_SIZE, 33, Short.MAX_VALUE)
            .addComponent(v_cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel5.add(jPanel6, new java.awt.GridBagConstraints());

        v_settingsDetails.setText(resourceMap.getString("v_settingsDetails.text")); // NOI18N
        v_settingsDetails.setName("v_settingsDetails"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(v_closeButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(v_settingsDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 634, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(v_settingsDetails, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, 0, 35, Short.MAX_VALUE)
                            .addComponent(v_closeButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-670)/2, (screenSize.height-383)/2, 670, 383);
    }// </editor-fold>//GEN-END:initComponents

    @Action
    public void Close()
    {
        ProgramSettings settings = ProgramSettings.LoadSettings();
        if (settings == null)
        {
            settings = new ProgramSettings();
        }
        if (settings.ProgramLookAndFeelIndex != v_programLandFCB.getSelectedIndex())
        {
            try
            {
                UIManager.setLookAndFeel(GetLAFForIndex(v_programLandFCB.getSelectedIndex()));
                SwingUtilities.updateComponentTreeUI(m_walkthroughWindow);
                SwingUtilities.updateComponentTreeUI(m_walkthroughWindow.GetMainMenu());
            }
            catch (Exception ex)
            {
                ErrorConsole.getConsole().appendError(ex);
            }
        }
        
        //General
        settings.MaxAutoBackupSize = Integer.parseInt(v_maxBackupsSpinner.getValue().toString());
        settings.ShowErrorConsoleWhenErrorOccurs = v_showErrorConsoleWhenErrorOccursCB.isSelected();
        settings.ShowConfirmationBoxesForDataEditing = v_showConfirmationBoxesForDataEditingCB.isSelected();
        
        //Graphics
        settings.ProgramLookAndFeelClassName = GetLAFForIndex(v_programLandFCB.getSelectedIndex());
        settings.ProgramLookAndFeelIndex = v_programLandFCB.getSelectedIndex();
        settings.ToolPreviewWhileMovingMouse = v_toolPreviewWhileMovingMouse.isSelected();
                
        //Advanced
        settings.MaxJavaHeapSize = Integer.parseInt(v_maxJavaHeapSizeSP.getValue().toString());
        settings.StepHistoryMaxSize = Integer.parseInt(v_maxStepsSpinner.getValue().toString());        
        settings.MaxPolyMultiSearchCycles = Integer.parseInt(v_maxPolygonCyclesSpinner.getValue().toString());
        
        ProgramSettings.SaveSettings(settings);
        this.dispose();
    }
    private String GetLAFForIndex(int index)
    {
        if(index == 0)
            return UIManager.getCrossPlatformLookAndFeelClassName();
        else if(index == 1)
            return NimbusLookAndFeel.class.getName();
        else if(index == 2)
            return MetalLookAndFeel.class.getName();
        else if(index == 3)
            return MotifLookAndFeel.class.getName();
        else if (index == 4)
            return UIManager.getSystemLookAndFeelClassName();
        else if(index == 5)
            return WindowsLookAndFeel.class.getName();
        else if(index == 6)
            return WindowsClassicLookAndFeel.class.getName();
        else if(index == 21)
            return "org.jvnet.substance.skin.SubstanceTwilightLookAndFeel";
        else
            return "org.jvnet.substance.skin." + v_programLandFCB.getItemAt(index).toString().replace(" ", "") + "LookAndFeel";
    }

    @Action
    public void RestoreDefaults()
    {
        int result = JOptionPane.showConfirmDialog(rootPane, "Are you sure you want to reset all program settings?", "Restore Default Settings", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.OK_OPTION)
        {
            ProgramSettings dSet = new ProgramSettings();
            InitControls(dSet);
            JOptionPane.showMessageDialog(rootPane, "Default settings restored.\r\n\r\n(If you don't want to keep these default settings, just hit cancel)", "Default Settings Restored", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Action
    public void Cancel()
    {
        this.dispose();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton v_cancelButton;
    private javax.swing.JButton v_closeButton1;
    private javax.swing.JSpinner v_maxBackupsSpinner;
    private javax.swing.JSpinner v_maxJavaHeapSizeSP;
    private javax.swing.JSpinner v_maxPolygonCyclesSpinner;
    private javax.swing.JSpinner v_maxStepsSpinner;
    private javax.swing.JComboBox v_programLandFCB;
    private javax.swing.JButton v_saveButton;
    private javax.swing.JButton v_settingsDetails;
    private javax.swing.JCheckBox v_showConfirmationBoxesForDataEditingCB;
    private javax.swing.JCheckBox v_showErrorConsoleWhenErrorOccursCB;
    private javax.swing.JCheckBox v_toolPreviewWhileMovingMouse;
    // End of variables declaration//GEN-END:variables

}