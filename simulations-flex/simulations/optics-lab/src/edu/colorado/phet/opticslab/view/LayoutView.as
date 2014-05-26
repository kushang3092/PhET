/*
 * Copyright 2002-2012, University of Colorado
 */

/**
 * Created with IntelliJ IDEA.
 * User: Dubson
 * Date: 5/21/14
 * Time: 4:51 PM
 * To change this template use File | Settings | File Templates.
 */
package edu.colorado.phet.opticslab.view {
import edu.colorado.phet.opticslab.model.OpticsModel;
import edu.colorado.phet.opticslab.util.Util;

import flash.display.Sprite;

//View of primary play area on which light sources and lenses, etc are "laid out".
//This view acts as the optics bench on which all components are arranged by the user.

public class LayoutView extends Sprite {
    private var myMainView: MainView;
    private var myOpticsModel: OpticsModel;
    private var _pixPerMeter: Number;
    private var testSource: LightSourceView;           //for testing graphics
    private var source_arr:Array;       //array of light sources

    public function LayoutView( myMainView: MainView, myOpticsModel: OpticsModel ) {
        this.myMainView = myMainView;
        this.myOpticsModel = myOpticsModel;
        this.myOpticsModel.registerView( this );
        this.myOpticsModel.updateViews();
        this._pixPerMeter = 1000;  //play area is approximately 1 meter across
        this.source_arr = new Array();
        this.init();
    }

    private function init():void{
        //testSource = new LightSourceView( myOpticsModel, this );
        //this.addChild( testSource );
        //this.testSource.makeGrabbable();
        //Util.makeClipDraggable( testSource ) ;  //fails! Why?
        //testSource.x = testSource.y = 50;
        //testSource.x = 50;
        //testSource.y = 50;
    }

    public function createNewLightSourceView( index: uint ):void{
        var newSourceView: LightSourceView = new LightSourceView( myOpticsModel, this, index ); //automatically added to LayoutView
        source_arr.push( newSourceView );
        //myOpticsModel.source_arr[ index ].view = newSourceView;
    }

    public function update():void{

    }//end of update()

    public function get pixPerMeter():Number {
        return _pixPerMeter;
    }

}  //end of class
}  //end of package
