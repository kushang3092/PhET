//View and Controller of ball in TableView
//BallImage has 6 sprite layers,
// 1: colored ball, on bottom, not grabbable
// 1a: momentum arrow, not grabbable
// 2: velocity arrow, not grabbable
// 2a: ball number label
// 3: arrowHead indicator (shows location of arrowHead when arrow length is small
// 4: transparent disk for dragging ball, to set position
// 5: transparent arrow head, for dragging velocity arrow, to set velocity, on top

package edu.colorado.phet.collisionlab.view {
import edu.colorado.phet.collisionlab.constants.CLConstants;
import edu.colorado.phet.collisionlab.control.DataTable;
import edu.colorado.phet.collisionlab.model.Ball;
import edu.colorado.phet.collisionlab.model.Model;
import edu.colorado.phet.collisionlab.util.TwoVector;

import flash.display.*;
import flash.events.*;
import flash.filters.*;
import flash.geom.Point;
import flash.text.*;

public class BallImage extends Sprite {
    public var myModel: Model;
    public var myTableView: TableView;
    public var myBall: Ball;
    public var ballIndex: int;			//index labels ball 1, 2, 3,
    public var ballBody: Sprite;
    public var pArrowImage: Arrow;				//momentum arrow, not grabbable
    public var vArrowImage: Arrow;				//velocity arrow, not grabbable
    public var ballHandle: Sprite;
    public var arrowHeadIndicator: Sprite; 		//shows user where tip of arrow head is
    public var arrowHeadHandle: Sprite;			//user grabs this handle to set velocity with mouse
    public var arrowShown: Boolean;				//true if velocity arrow visible
    public var tFormat: TextFormat;				//format for ball label text
    public var tFormat2: TextFormat;			//format for ball position and velocity readouts
    public var tFieldBallNbr: TextField;		//label = ball number
    public var xEqString: String;				//"x = "  All text must be programmatically set for internationalization
    public var yEqString: String;				//"y = "


    public function BallImage( myModel: Model, indx: int, myTableView: TableView ) {
        this.myModel = myModel;
        this.myTableView = myTableView;
        this.ballIndex = indx;
        this.myBall = this.myModel.ball_arr[this.ballIndex];
        this.ballBody = new Sprite();
        this.vArrowImage = new Arrow( indx );
        this.vArrowImage.setScale( 100 );  //normal scale is 50
        this.vArrowImage.setColor( 0x00ff00 );
        this.pArrowImage = new Arrow( indx );
        this.pArrowImage.setScale( 110 );
        this.pArrowImage.setColor( 0xffff00 );
        this.pArrowImage.setShaftWidth( 13 );
        this.pArrowImage.setMaxHeadLength( 20 );
        this.showPArrow( false );
        this.ballHandle = new Sprite();
        this.arrowHeadIndicator = new Sprite();
        this.arrowHeadHandle = new Sprite();
        this.tFieldBallNbr = new TextField();
        var outline: GlowFilter = new GlowFilter( 0x000000, 1.0, 2.0, 2.0, 10 ); //outline for ball number text for better visibility
        outline.quality = BitmapFilterQuality.MEDIUM;
        var ballNbr: String = String( 1 + this.ballIndex );
        this.tFieldBallNbr.text = ballNbr;
        this.tFieldBallNbr.filters = [outline];
        this.xEqString = "x = ";
        this.yEqString = "y = ";
        this.tFormat = new TextFormat();
        tFormat.font = "Arial";
        tFormat.bold = true;
        tFormat.color = 0xffffff;
        tFormat.size = 20;
        this.tFormat2 = new TextFormat();
        tFormat2.bold = true;
        tFormat2.font = "Arial";
        tFormat2.color = 0x000000;
        tFormat2.size = 14;
        this.tFieldBallNbr.defaultTextFormat = tFormat;
        this.setLayerDepths();
        this.drawLayer1();
        this.drawLayer1a();
        this.drawLayer2();
        this.drawLayer2a();
        this.drawLayer3();
        this.drawLayer4();
        this.drawLayer5();
        this.makeBallDraggable();
        this.makeArrowDraggable();
        this.arrowShown = true;
    }

    private function setLayerDepths(): void {
        this.myTableView.canvas.addChild( this );
        this.addChild( this.ballBody );
        this.addChild( this.pArrowImage );
        this.addChild( this.vArrowImage );
        this.addChild( this.tFieldBallNbr );
        this.addChild( this.ballHandle );
        this.addChild( this.arrowHeadIndicator );
        this.addChild( this.arrowHeadHandle );
    }

    public function drawLayer1(): void {
        var g: Graphics = this.ballBody.graphics;
        var currentColor: uint = this.myTableView.ballColor_arr[this.ballIndex];
        var r: Number = this.myBall.getRadius();
        g.clear();
        g.lineStyle( 1, 0x000000, 1, false );
        g.beginFill( currentColor );
        g.drawCircle( 0, 0, r * CLConstants.PIXELS_PER_METER );
        g.endFill();
    }

    public function drawLayer1a(): void {
        this.pArrowImage.setArrow( this.myModel.ball_arr[this.ballIndex].momentum );
        //trace("velocityY: "+this.myModel.ball_arr[this.ballIndex].velocity.getY());
        this.pArrowImage.setText( "" );
    }

    public function drawLayer2(): void {
        this.vArrowImage.setArrow( this.myModel.ball_arr[this.ballIndex].velocity );
        //trace("velocityY: "+this.myModel.ball_arr[this.ballIndex].velocity.getY());
        this.vArrowImage.setText( "" );
    }

    public function drawLayer2a(): void {
        var ballNbr: int = this.ballIndex + 1;
        var ballNbr_str: String = String( ballNbr );
        this.tFieldBallNbr.text = ballNbr_str;
        this.tFieldBallNbr.autoSize = TextFieldAutoSize.LEFT;
        this.tFieldBallNbr.height = 15;
        this.tFieldBallNbr.x = -this.tFieldBallNbr.width / 2;
        this.tFieldBallNbr.y = -this.tFieldBallNbr.height / 2;
    }

    public function drawLayer3(): void {
        var g: Graphics = this.arrowHeadIndicator.graphics;
        var rInPix: Number = 10;
        g.clear();
        g.lineStyle( 1, 0x000000 );
        g.drawCircle( 0, 0, rInPix );
        this.arrowHeadIndicator.visible = false;
    }

    public function drawLayer4(): void {    //ballHandle
        var g: Graphics = this.ballHandle.graphics;
        var currentColor: uint = 0xffff00;
        var alpha1: Number = 0;
        var r: Number = this.myBall.getRadius();
        g.clear();
        g.beginFill( currentColor, alpha1 );
        g.drawCircle( 0, 0, r * CLConstants.PIXELS_PER_METER );
        g.endFill();
    }

    public function drawLayer5(): void {  //arrowHeadHandle
        var g: Graphics = this.arrowHeadHandle.graphics;
        var currentColor: uint = 0xffffff;
        var alpha1: Number = 0;
        var r: Number = 10;
        g.clear();
        g.beginFill( currentColor, alpha1 );
        //g.lineStyle(1,0x000000);
        g.drawCircle( 1, 0, r );
        g.endFill();
        this.arrowHeadHandle.x = this.vArrowImage.getHeadCenterX();
        this.arrowHeadHandle.y = this.vArrowImage.getHeadCenterY();
    }


    public function makeBallDraggable(): void {
        var target: Sprite = this.ballHandle;
        var thisBallImage: BallImage = this;
        target.buttonMode = true;
        var indx: int = ballIndex;
        var modelRef: Model = this.myModel;
        var H: Number = modelRef.borderHeight;
        var W: Number = modelRef.borderWidth;
        var ballX: Number;	//current ball coordinates in meters
        var ballY: Number;

        //target.addEventListener(MouseEvent.MOUSE_OVER, bringToTop);
        target.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        target.stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        target.stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        target.addEventListener( MouseEvent.MOUSE_OVER, highlightPositionTextFields );
        target.addEventListener( MouseEvent.MOUSE_OUT, unHighlightPositionTextFields );
        var theStage: Object = thisBallImage.myTableView.canvas;//target.parent;
        var clickOffset: Point;

        //function bringToTop(evt:MouseEvent):void{
        //thisBallImage.myTableView.canvas.addChild(thisBallImage);
        //}

        function startTargetDrag( evt: MouseEvent ): void {
            //next two lines bring selected ball to top, so velocity arrow visible
            //and bring C.M. icon to top, so not hidden behind any ball
            thisBallImage.myTableView.canvas.addChild( thisBallImage );
            thisBallImage.myTableView.canvas.addChild( thisBallImage.myTableView.CM );
            //problem with localX, localY if sprite is rotated.
            clickOffset = new Point( evt.localX, evt.localY );
            //trace("evt.localX: "+evt.localX);
            //trace("evt.localY: "+evt.localY);
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            //trace("stop dragging");
            if ( clickOffset != null ) {
                clickOffset = null;
                //trace("before separateAllBalls(), index = "+indx+ "thisBallImage.x = "+thisBallImage.x);
                thisBallImage.myModel.separateAllBalls();
                //trace("after separateAllBalls(), index = "+indx+ "thisBallImage.x = "+thisBallImage.x);
            }

        }

        function dragTarget( evt: MouseEvent ): void {
            if ( clickOffset != null ) {  //if dragging
                //adjust x position
                thisBallImage.x = theStage.mouseX - clickOffset.x;
                ballX = thisBallImage.x / CLConstants.PIXELS_PER_METER;
                //edges of border, beyond which center of ball may not go
                var leftEdge: Number = thisBallImage.myBall.getRadius();
                var rightEdge: Number = W - thisBallImage.myBall.getRadius();
                var topEdge: Number = H / 2 - thisBallImage.myBall.getRadius();
                var bottomEdge: Number = -H / 2 + thisBallImage.myBall.getRadius();
                if ( modelRef.borderOn ) {
                    if ( ballX < leftEdge ) {
                        ballX = leftEdge;
                    }
                    else {
                        if ( ballX > rightEdge ) {
                            ballX = rightEdge;
                        }
                    }
                }
                modelRef.setX( indx, ballX );
                //if not in 1DMode, adjust y position
                if ( !thisBallImage.myModel.oneDMode ) {
                    thisBallImage.y = theStage.mouseY - clickOffset.y;
                    ballY = H / 2 - thisBallImage.y / CLConstants.PIXELS_PER_METER;
                    if ( modelRef.borderOn ) {
                        if ( ballY < bottomEdge ) {
                            ballY = bottomEdge;
                        }
                        else {
                            if ( ballY > topEdge ) {
                                ballY = topEdge;
                            }
                        }
                    }
                    modelRef.setY( indx, ballY );
                }
                if ( modelRef.atInitialConfig ) {
                    modelRef.initPos[indx].setXY( ballX, ballY );
                }
                modelRef.updateViews();
                evt.updateAfterEvent();
            }
        }

        //following produced dataTable = null  maybe due to startup order?
        //var dataTable:DataTable = thisBallImage.myTableView.myMainView.myDataTable;

        function highlightPositionTextFields(): void {
            //trace("BallImage.myTableView.myMainView.myDataTable:"+thisBallImage.myTableView.myMainView.myDataTable);
            var dataTable: DataTable = thisBallImage.myTableView.myMainView.myDataTable;
            //dataTable.text_arr[thisBallImage.ballIndex+1][2].background = true;
            //dataTable.text_arr[thisBallImage.ballIndex+1][3].background = true;
            dataTable.text_arr[thisBallImage.ballIndex + 1][2].backgroundColor = 0xffff33;
            dataTable.text_arr[thisBallImage.ballIndex + 1][3].backgroundColor = 0xffff33;
        }

        function unHighlightPositionTextFields(): void {
            var dataTable: DataTable = thisBallImage.myTableView.myMainView.myDataTable;
            dataTable.text_arr[thisBallImage.ballIndex + 1][2].backgroundColor = 0xffffff;
            dataTable.text_arr[thisBallImage.ballIndex + 1][3].backgroundColor = 0xffffff;
            //dataTable.text_arr[thisBallImage.ballIndex+1][2].background = false;
            //dataTable.text_arr[thisBallImage.ballIndex+1][3].background = false;

        }


    }


    public function makeArrowDraggable(): void {
        var target: Sprite = this.arrowHeadHandle;
        var thisBallImage: BallImage = this;
        var thisArrowImage: Arrow = this.vArrowImage;

        target.buttonMode = true;
        var indx: int = ballIndex;
        var modelRef: Model = this.myModel;
        var H: Number = modelRef.borderHeight;

        target.addEventListener( MouseEvent.MOUSE_DOWN, startTargetDrag );
        target.stage.addEventListener( MouseEvent.MOUSE_UP, stopTargetDrag );
        target.stage.addEventListener( MouseEvent.MOUSE_MOVE, dragTarget );
        target.addEventListener( MouseEvent.MOUSE_OVER, showVelocity );
        target.addEventListener( MouseEvent.MOUSE_OUT, unshowVelocity );
        var theStage: Object = thisBallImage;//target.parent;
        var clickOffset: Point;


        function startTargetDrag( evt: MouseEvent ): void {
            //problem with localX, localY if sprite is rotated.
            thisBallImage.myTableView.canvas.addChild( thisBallImage );
            clickOffset = new Point( evt.localX, evt.localY );
            //trace("evt.localX: "+evt.localX);
            //trace("evt.localY: "+evt.localY);
        }

        function stopTargetDrag( evt: MouseEvent ): void {
            //trace("stop dragging");
            clickOffset = null;
        }

        function dragTarget( evt: MouseEvent ): void {
            if ( clickOffset != null ) {  //if dragging
                //adjust x-component of velocity
                //following line is ratio of arrowHeadIndicator position to tip-of-arrow position, measured from origin at tail of arrow.
                //keeps the handle on the center of the arrow head rather than on the tip of the arrow head
                var ratio: Number = (thisArrowImage.lengthInPix + thisArrowImage.headL) / (thisArrowImage.lengthInPix + 0.2 * thisArrowImage.headL);
                //trace("ratio before trap: "+ratio);
                if ( isNaN( ratio ) ) {
                    ratio = 1;
                    //trace("ratio set to 1 because is was NaN");
                }
                //trace("ratio after trap: "+ratio);
                target.x = theStage.mouseX;// - clickOffset.x;
                //thisBallImage.arrowHeadHandle.x = target.x;
                thisBallImage.arrowHeadIndicator.x = target.x;
                var velocityX: Number = (target.x * ratio) / thisBallImage.vArrowImage.scale;
                //trace("velocityX: "+velocityX);

                modelRef.setVX( indx, velocityX );
                //if not in 1DMode, set y-component of velocity
                if ( !modelRef.oneDMode ) {
                    target.y = theStage.mouseY;// - clickOffset.y;
                    //thisBallImage.arrowHeadHandle.y = target.y;
                    thisBallImage.arrowHeadIndicator.y = target.y;
                    var velocityY: Number = -(target.y * ratio) / thisBallImage.vArrowImage.scale;
                    modelRef.setVY( indx, velocityY );
                }
                else {
                    target.y = 0;// - clickOffset.y;
                    thisBallImage.arrowHeadHandle.y = target.y;
                    //thisBallImage.arrowHeadIndicator.y = target.y;
                    velocityY = -(target.y * ratio) / thisBallImage.vArrowImage.scale;
                    modelRef.setVY( indx, velocityY );
                }
                thisBallImage.setVisibilityOfArrowHeadIndicator();
                /*
                 var distInPix:Number = Math.sqrt(target.x*target.x + target.y*target.y);
                 var rInPix:Number = thisBallImage.pixelsPerMeter*thisBallImage.myBall.getRadius();
                 //trace("distInPix: "+distInPix+"   r:"+rInPix);
                 if(distInPix < rInPix){
                 //trace("inside");
                 thisBallImage.arrowHeadIndicator.visible = true;
                 }else{
                 //trace("outside");
                 thisBallImage.arrowHeadIndicator.visible = false;
                 }
                 */
                //trace("velocityX: "+velocityX+"    velocityY: "+velocityY);
                //modelRef.ball_arr[indx].velocity.setXY(velocityX, velocityY);
                if ( modelRef.atInitialConfig ) {
                    modelRef.initVel[indx].setXY( velocityX, velocityY );
                }
                modelRef.updateViews();
                thisBallImage.vArrowImage.setArrow( modelRef.ball_arr[indx].velocity );
                evt.updateAfterEvent();
            }
        }


        function showVelocity( evt: MouseEvent ): void {
            //trace("showVelocity rollover " +indx);
            var dataTable: DataTable = thisBallImage.myTableView.myMainView.myDataTable;
            dataTable.text_arr[thisBallImage.ballIndex + 1][4].backgroundColor = 0xffff33;
            dataTable.text_arr[thisBallImage.ballIndex + 1][5].backgroundColor = 0xffff33;
        }

        function unshowVelocity( evt: MouseEvent ): void {
            //trace("showVelocity rollout" + indx);
            var dataTable: DataTable = thisBallImage.myTableView.myMainView.myDataTable;
            dataTable.text_arr[thisBallImage.ballIndex + 1][4].backgroundColor = 0xffffff;
            dataTable.text_arr[thisBallImage.ballIndex + 1][5].backgroundColor = 0xffffff;
        }
    }


    public function setVisibilityOfArrowHeadIndicator(): void {
        var ballRadiusInPix: Number = CLConstants.PIXELS_PER_METER * this.myBall.getRadius();
        var velInPix: Number = this.vArrowImage.lengthInPix//Math.sqrt(target.x*target.x + target.y*target.y);
        //var rInPix:Number = thisBallImage.pixelsPerMeter*thisBallImage.myBall.getRadius();
        //trace("distInPix: "+distInPix+"   r:"+rInPix);
        this.arrowHeadIndicator.visible = velInPix < ballRadiusInPix && this.arrowShown;
    }

    public function showArrow( tOrF: Boolean ): void {
        if ( tOrF ) {  //if arrows shown
            this.arrowShown = true;
            this.vArrowImage.visible = true;
            this.setVisibilityOfArrowHeadIndicator();
            this.arrowHeadHandle.visible = true;
        }
        else {  //if arrow not shown
            this.arrowShown = false;
            this.vArrowImage.visible = false;
            this.arrowHeadIndicator.visible = false;
            this.arrowHeadHandle.visible = false;
        }
    }

    public function showPArrow( tOrF: Boolean ): void {
        if ( tOrF ) {  //if arrows shown
            //this.pArrowShown = true;
            this.pArrowImage.visible = true;
        }
        else {  //if arrow not shown
            //this.pArrowShown = false;
            this.pArrowImage.visible = false;
        }
    }

    //update both velocity and momentum arrows on ball images
    public function updateVelocityArrow(): void {
        var vel: TwoVector = this.myModel.ball_arr[this.ballIndex].velocity;
        var mom: TwoVector = this.myModel.ball_arr[this.ballIndex].getMomentum(); //momentum;
        //if(this.ballIndex == 0){
        //trace("ballImage.myModel.ball_arr[0].velocity.y = "+this.myModel.ball_arr[0].velocity.getY());
        //}
        //this.myModel.updateViews();
        this.vArrowImage.setArrow( vel );
        this.pArrowImage.setArrow( mom );
        var scaleFactor: Number = this.vArrowImage.scale;
        //following line is ratio of arrowHeadIndicator position to tip-of-arrow position, measured from origin at tail of arrow.
        var thisArrowImage: Arrow = this.vArrowImage;
        var ratio: Number = (thisArrowImage.lengthInPix + thisArrowImage.headL) / (thisArrowImage.lengthInPix + 0.2 * thisArrowImage.headL);
        if ( isNaN( ratio ) ) {ratio = 1;}
        //trace("on updateVelocityArrow(), ratio is "+ratio);
        this.arrowHeadIndicator.x = scaleFactor * vel.getX() / ratio;
        this.arrowHeadIndicator.y = -scaleFactor * vel.getY() / ratio;
        this.arrowHeadHandle.x = scaleFactor * vel.getX() / ratio;
        this.arrowHeadHandle.y = -scaleFactor * vel.getY() / ratio;
        this.setVisibilityOfArrowHeadIndicator();
    }

}
}