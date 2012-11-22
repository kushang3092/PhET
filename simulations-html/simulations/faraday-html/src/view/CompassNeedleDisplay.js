// Copyright 2002-2012, University of Colorado

/**
 * A compass needle.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel' ],
        function ( Easel ) {

            /**
             * @param {Dimension2D} size
             * @param {Number} orientation in degrees
             * @constructor
             */
            function CompassNeedleDisplay( size ) {

                // constructor stealing
                Easel.Container.call( this );

                var northShape = new Easel.Shape();
                northShape.graphics.beginFill( 'red' );
                northShape.graphics.moveTo( 0, -size.height / 2 )
                        .lineTo( size.width / 2, 0 )
                        .lineTo( 0, size.height / 2 )
                        .closePath();

                var southShape = new Easel.Shape();
                southShape.graphics.beginFill( 'white' );
                southShape.graphics.moveTo( 0, -size.height / 2 )
                        .lineTo( -size.width / 2, 0 )
                        .lineTo( 0, size.height / 2 )
                        .closePath();

                this.addChild( northShape );
                this.addChild( southShape );
            }

            // prototype chaining
            CompassNeedleDisplay.prototype = new Easel.Container();

            return CompassNeedleDisplay;
        } );