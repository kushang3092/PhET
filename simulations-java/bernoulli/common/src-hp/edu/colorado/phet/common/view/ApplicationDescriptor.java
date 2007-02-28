/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view;

import edu.colorado.phet.common.view.util.framesetup.FullScreen;
import edu.colorado.phet.common.view.util.framesetup.FrameCenterer;
import edu.colorado.phet.common.view.util.framesetup.FrameSetup;
import edu.colorado.phet.common.view.util.framesetup.AbsoluteFrameSetup;

/**
 * User: Sam Reid
 * Date: Jun 12, 2003
 * Time: 7:38:22 AM
 * Copyright (c) Jun 12, 2003 by Sam Reid
 */
public class ApplicationDescriptor {
    String windowTitle;
    String description;
    String version;
    FrameSetup frameSetup;

    public ApplicationDescriptor(String windowTitle, String description, String version, int width, int height) {
        this(windowTitle, description, version, width, height, false);
    }

    public ApplicationDescriptor(String windowTitle, String description, String version, int x, int y, boolean isInsetSpecified) {
        this(windowTitle, description, version, getFrameSetup(isInsetSpecified, x, y));
    }

    private static FrameSetup getFrameSetup(boolean inset, int x, int y) {
        if (inset)
            return new FrameCenterer(x, y);
        else
            return new AbsoluteFrameSetup(x, y);
    }

    public ApplicationDescriptor(String windowTitle, String description, String version) {
        this(windowTitle, description, version, new FullScreen());
    }

    public ApplicationDescriptor(String windowTitle, String description, String version, FrameSetup frameSetup) {
        this.windowTitle = windowTitle;
        this.description = description;
        this.version = version;
        this.frameSetup = frameSetup;
    }

    public String getWindowTitle() {
        return windowTitle;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public FrameSetup getFrameSetup() {
        return frameSetup;
    }
}
