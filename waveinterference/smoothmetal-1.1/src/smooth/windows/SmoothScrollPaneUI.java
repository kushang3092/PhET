package smooth.windows;

import com.sun.java.swing.plaf.windows.WindowsScrollPaneUI;
import smooth.util.SmoothUtilities;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

public class SmoothScrollPaneUI extends WindowsScrollPaneUI {
    public static ComponentUI createUI( JComponent jcomponent ) {
        return new SmoothScrollPaneUI();
    }

    public void paint( Graphics g, JComponent c ) {
        SmoothUtilities.configureGraphics( g );
        super.paint( g, c );
    }
}
