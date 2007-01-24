/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.control;

import edu.colorado.phet.common.view.util.SimStrings;
import edu.colorado.phet.dischargelamps.model.DischargeLampModel;
import edu.colorado.phet.dischargelamps.DischargeLampModule;
import edu.colorado.phet.quantum.model.ElectronSource;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import java.text.DecimalFormat;

/**
 * ElectronProductionControl
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ElectronProductionControl extends JPanel {
    private DischargeLampModel model;
    private double currentDisplayFactor = 1E-3;
    private DischargeLampModule module;
    private double maxCurrent;
    private boolean firstTimeContinuous = true;
    private double initialContinuousCurrent = maxCurrent * .2;

    //--------------------------------------------------------------------------------------------------
    // Class fields and methods
    //--------------------------------------------------------------------------------------------------

    public static class ProductionType {
        private ProductionType() {
        }
    }

    public static final ProductionType CONTINUOUS = new ProductionType();
    public static final ProductionType SINGLE_SHOT = new ProductionType();

    //--------------------------------------------------------------------------------------------------
    // Instance fields and methods
    //--------------------------------------------------------------------------------------------------

    private CurrentSlider heaterControlSlider;
    private JComponent heaterControl;
    private JComponent modeSelectorControl;
    private JComponent fireElectronButton;

    public ElectronProductionControl(DischargeLampModule module, double maxCurrent ) {
        this.module = module;
        this.maxCurrent = maxCurrent;
        this.model = ((DischargeLampModel) module.getModel());
        fireElectronButton = createFireElectronBtn();
        heaterControl = createHeaterControl();
        modeSelectorControl = createModeSelectorControl();

//        setBorder(new TitledBorder(SimStrings.get("Controls.ElectronProduction")));
        setBorder( new LineBorder( Color.black, 1) );


        layoutPanel();
    }


    public void setMaxCurrent(double maxCurrent) {
        heaterControlSlider.setMaxCurrent(maxCurrent);
    }

    public void setProductionType(ElectronProductionControl.ProductionType type) {
        if (type == CONTINUOUS) {
            heaterControl.setVisible(true);
            fireElectronButton.setVisible(false);
            model.setElectronProductionMode(ElectronSource.CONTINUOUS_MODE);
            if (firstTimeContinuous) {
                model.setCurrent(maxCurrent * .2, currentDisplayFactor);
                firstTimeContinuous = false;
            } else {
                model.setCurrent(heaterControlSlider.getValue(), currentDisplayFactor);
            }
        }
        if (type == SINGLE_SHOT) {
            heaterControl.setVisible(false);
            fireElectronButton.setVisible(true);
            // todo: this should set the total current to 0
            model.getLeftHandPlate().setCurrent(0);
            model.setElectronProductionMode(ElectronSource.SINGLE_SHOT_MODE);
        }
        module.setProductionType(type);
    }

    private JComponent createModeSelectorControl() {

        // Radio buttons to choose between single-shot and continuous modes
        JRadioButton continuousRB = new JRadioButton(new AbstractAction(SimStrings.get("Controls.Continuous")) {
            public void actionPerformed(ActionEvent e) {
                setProductionType(ElectronProductionControl.CONTINUOUS);
            }
        });
        JRadioButton singleShotRB = new JRadioButton(new AbstractAction(SimStrings.get("Controls.Single")) {
            public void actionPerformed(ActionEvent e) {
                setProductionType(ElectronProductionControl.SINGLE_SHOT);
            }
        });
        ButtonGroup electronProductionBtnGrp = new ButtonGroup();
        electronProductionBtnGrp.add(continuousRB);
        electronProductionBtnGrp.add(singleShotRB);
        singleShotRB.setSelected(true);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(
                GridBagConstraints.RELATIVE,
                0,
                1, 1, 0, 0,
                GridBagConstraints.WEST,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0);
        panel.add(singleShotRB, gbc);
        panel.add(continuousRB, gbc);

        singleShotRB.setSelected(true);
        setProductionType(ElectronProductionControl.SINGLE_SHOT);

        return panel;
    }

    private JComponent createFireElectronBtn() {
        final JButton singleShotBtn = new JButton(SimStrings.get("Controls.FireElectron"));
        singleShotBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (model.getVoltage() > 0) {
                    model.getLeftHandPlate().produceElectron();
                } else if (model.getVoltage() < 0) {
                    model.getRightHandPlate().produceElectron();
                }
            }
        });
        return singleShotBtn;
    }

    private JComponent createHeaterControl() {
        heaterControlSlider = new CurrentSlider(maxCurrent);
        heaterControlSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                model.setCurrent(heaterControlSlider.getValue(), currentDisplayFactor);
            }
        });
//        model.setCurrent(100, currentDisplayFactor);

        final JTextField readout = new JTextField(20);
        readout.setHorizontalAlignment(JTextField.RIGHT);
        final DecimalFormat format = new DecimalFormat("##0%");
        heaterControlSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                double displayNumber = heaterControlSlider.getPctMax();
                readout.setText(format.format(displayNumber));
            }
        });
        double displayNumber = heaterControlSlider.getPctMax();
        readout.setText(format.format(displayNumber));

        model.addChangeListener(new DischargeLampModel.ChangeListenerAdapter() {
            public void currentChanged(DischargeLampModel.ChangeEvent event) {
                heaterControlSlider.setValue((int)(model.getCurrent()/ currentDisplayFactor));
                double displayNumber = heaterControlSlider.getPctMax();
                readout.setText(format.format(displayNumber));
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0), 0, 0);
        panel.add(Box.createHorizontalStrut(175), gbc);
        panel.add(heaterControlSlider, gbc);
        gbc.gridx++;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(Box.createHorizontalStrut(50), gbc);
        panel.add(readout, gbc);
        return panel;
    }

    /**
     * Lay out the components in the panel
     */
    private void layoutPanel() {
        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(
                0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                new Insets(3, 0, 3, 0), 0, 0);

        JLabel title = new JLabel(SimStrings.get("Controls.ElectronProduction"));
        gbc.gridwidth = 2;
        add( title, gbc );

        gbc.gridwidth = 1;
        gbc.gridy++;


        add(modeSelectorControl, gbc);
        gbc.gridx++;
        add(Box.createHorizontalStrut(300), gbc);
//        add(Box.createVerticalStrut(70), gbc);
        add(fireElectronButton, gbc);
        add(heaterControl, gbc);

    }
}
