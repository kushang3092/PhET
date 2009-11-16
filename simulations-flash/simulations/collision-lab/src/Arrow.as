﻿//general class for arrow graphic, used as momentum arrow
package{
	import flash.display.*;
	import flash.events.*;
	import fl.events.*;
	import flash.text.*;
	
	
	public class Arrow extends Sprite{
		var index:int; //integer labels arrow
		var canvas:Sprite;
		var shaft:Sprite;
		var head:Sprite;
		var scale:Number;	//adjustable scale for graphic
		var shaftW:int;		//width of shaft in pixels
		var shaftL:int;		//length of shaft in pixels
		var tField:TextField;		//textFields cannot be rotated.
		var fillColor:uint;
		var lineColor:uint;
		
		public function Arrow(indx:Number){
			this.index = indx+1;
			this.canvas = new Sprite();
			this.scale = 50;
			this.shaftW = 6;
			this.shaftL = this.scale*50;
			this.shaft = new Sprite();
			this.head = new Sprite();
			this.tField = new TextField();
			var str:String = new String(this.index);
			this.tField.text = str;
			this.tField.selectable = false;
			this.tField.autoSize = TextFieldAutoSize.LEFT;
			fillColor = 0xffff00;
			lineColor = 0x0000ff;
			//this.addChild(this.canvas);
			this.initialize();
			this.addChild(this.canvas);
			this.canvas.addChild(this.shaft);
			this.canvas.addChild(this.head);
			this.addChild(tField);
		}//end of constructor
		
		public function initialize():void{
			//draw shaft
			with(this.shaft.graphics){
				clear();
				lineStyle(1, lineColor);
				beginFill(fillColor);
				moveTo(0,-shaftW/2);
				lineTo(shaftL, -shaftW/2);
				lineTo(shaftL, shaftW/2);
				lineTo(0, shaftW/2);
				lineTo(0, -shaftW/2)
				endFill();
			}
			with(this.head.graphics){
				clear();
				lineStyle(1, fillColor);
				beginFill(fillColor);
				moveTo(0,-shaftW);
				lineTo(2*shaftW, 0);
				lineTo(0, shaftW);
				lineTo(0, -shaftW);
				endFill();
				lineStyle(1, lineColor);
				moveTo(0,-shaftW/2);
				lineTo(0, -shaftW);
				lineTo(2*shaftW, 0);
				lineTo(0, shaftW);
				lineTo(0, shaftW/2);
			}
			this.shaft.x = 0;
			this.shaft.y = 0;
			this.head.x = shaftL;
			this.head.y = 0;
		}//end of initialize
		
		public function setColor(fillColor:uint):void{
			this.fillColor = fillColor;
			this.initialize();
		}
		
		public function setArrow(vector:TwoVector):void{
			var L:Number = vector.getLength();
			var angle:Number = vector.getAngle();
			this.shaft.width = this.scale*L - this.head.width;
			this.head.x = this.shaft.width;
			var angleInDeg:Number = vector.getAngle();
			var angleInRad:Number = angleInDeg*Math.PI/180;
			this.canvas.rotation = -angleInDeg;
			this.tField.x = this.scale*L/2*Math.cos(angleInRad);
			this.tField.y = this.scale*L/2*Math.sin(-angleInRad);
		}
		
		public function getX():Number{
			return 1;
		}
		
	}//end of class
}//end of package