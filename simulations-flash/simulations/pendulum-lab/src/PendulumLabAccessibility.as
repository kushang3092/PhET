
import edu.colorado.phet.flashcommon.*;

class PendulumLabAccessibility {
    public var pendulum1 : TabEntry;
    public var pendulum2 : TabEntry;

    public var length1Text : TabEntry;
    public var length1Slider : TabEntry;
    public var mass1Text : TabEntry;
    public var mass1Slider : TabEntry;

    public var pendulum2Check : TabEntry;

    public var length2Text : TabEntry;
    public var length2Slider : TabEntry;
    public var mass2Text : TabEntry;
    public var mass2Slider : TabEntry;

    public var tab : TabHandler;

    public function PendulumLabAccessibility() {
        _level0.debug("Initializing PendulumLabAccessibility\n");
        
        tab = _level0.tabHandler;

        pendulum1 = new TabEntry( _level0.pendulum1.pendulum_mc.mass_mc, TabHandler.HIGHLIGHT_LOCAL );
        pendulum1.highlightColor = 0xFF00FF;
        pendulum1.highlightWidth = 5;
        pendulum1.highlightInset = -5;

        pendulum2 = new TabEntry( _level0.pendulum2.pendulum_mc.mass_mc, TabHandler.HIGHLIGHT_LOCAL );
        pendulum2.highlightColor = 0xFF00FF;
        pendulum2.highlightWidth = 5;
        pendulum2.highlightInset = -5;

        length1Text = new TabEntry(_root.panel_mc.length1Slider.value_txt);
        length1Slider = new TabEntry(_root.panel_mc.length1Slider.puck_mc, TabHandler.HIGHLIGHT_LOCAL);
        mass1Text = new TabEntry(_root.panel_mc.mass1Slider.value_txt);
        mass1Slider = new TabEntry(_root.panel_mc.mass1Slider.puck_mc, TabHandler.HIGHLIGHT_LOCAL);

        pendulum2Check = new TabEntry(_root.panel_mc.pendulum2_ch.box_mc, TabHandler.HIGHLIGHT_LOCAL, _root.panel_mc.pendulum2_ch);
        pendulum2Check.buttonlike = true;

        length2Text = new TabEntry(_root.panel_mc.length2Slider.value_txt);
        length2Slider = new TabEntry(_root.panel_mc.length2Slider.puck_mc, TabHandler.HIGHLIGHT_LOCAL);
        mass2Text = new TabEntry(_root.panel_mc.mass2Slider.value_txt);
        mass2Slider = new TabEntry(_root.panel_mc.mass2Slider.puck_mc, TabHandler.HIGHLIGHT_LOCAL);


        tab.insertEntry( pendulum1, 0 );
        tab.insertEntry( length1Text, 1 );
        tab.insertEntry( length1Slider, 2 );
        tab.insertEntry( mass1Text, 3 );
        tab.insertEntry( mass1Slider, 4 );
        tab.insertEntry( pendulum2Check, 5 );
        
    }

    public function showPendulum2() {

        tab.insertEntry( length2Text, 6 );
        tab.insertEntry( length2Slider, 7 );
        tab.insertEntry( mass2Text, 8 );
        tab.insertEntry( mass2Slider, 9 );

        tab.insertEntry( pendulum2, 1 );

    }

    public function hidePendulum2() {
        tab.removeEntry( length2Text );
        tab.removeEntry( length2Slider );
        tab.removeEntry( mass2Text );
        tab.removeEntry( mass2Slider );
        tab.removeEntry( pendulum2 );
    }

    public function initializeSlider( slider : MovieClip ) {
        _level0.debug( "Initializing Slider: " + slider + "\n" );

        var entry : TabEntry;

        if( slider.puck_mc == length1Slider.control ) {
            entry = length1Slider;
        } else if( slider.puck_mc == mass1Slider.control ) {
            entry = mass1Slider;
        } else if( slider.puck_mc == length2Slider.control ) {
            entry = length2Slider;
        } else if( slider.puck_mc == mass2Slider.control ) {
            entry = mass2Slider;
        } else {
            _level0.debug( "Slider not found!!!\n" );
        }

        entry.keys[ 37 ] = slider.puck_mc.pressLeft;
        entry.keys[ 39 ] = slider.puck_mc.pressRight;
    }
}
