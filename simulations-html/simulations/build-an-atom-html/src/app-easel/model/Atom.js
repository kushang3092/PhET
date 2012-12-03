// Copyright 2002-2012, University of Colorado
define( [
            'underscore',
            'common/SharedConstants'
        ], function ( _, SharedConstants ) {

    Atom.CONFIG_CHANGE_EVENT = 'configurationChanged';
    Atom.INNER_ELECTRON_SHELL_RADIUS = 80;
    Atom.OUTER_ELECTRON_SHELL_RADIUS = 180;

    function Atom( xPos, yPos ) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.nucleons = [];
        this.electrons = [];
        this.events = $( {} );
    }

    Atom.prototype.addParticle = function ( particle ) {

        // Distinguish nucleons from electrons.
        if ( particle.type === 'proton' || particle.type === 'neutron' ) {
            this.nucleons.push( particle );
            var self = this;
            particle.events.one( 'userGrabbed', function () {
                self.nucleons = _.without( self.nucleons, particle );
                self.reconfigureNucleus( true );
                self.events.trigger( Atom.CONFIG_CHANGE_EVENT );
            } );
            self.reconfigureNucleus( true );
            self.events.trigger( Atom.CONFIG_CHANGE_EVENT );
        }
        else if ( particle.type === 'electron' ) {
            var length = Math.random() > 0.5 ? Atom.INNER_ELECTRON_SHELL_RADIUS : Atom.OUTER_ELECTRON_SHELL_RADIUS;
            var angle = Math.random() * 2 * Math.PI;
            particle.setLocation( { x:( length * Math.cos( angle ) ), y:( length * Math.sin( angle ) ) } );
        }
    };

    Atom.prototype.getNumProtons = function () {
        var numProtons = 0;
        _.each( this.nucleons, function ( nucleon ) {
            if ( nucleon.type === 'proton' ) {
                numProtons++;
            }
        } );
        return numProtons;
    };

    Atom.prototype.getNumNeutrons = function () {
        var numNeutrons = 0;
        _.each( this.nucleons, function ( nucleon ) {
            if ( nucleon.type === 'neutron' ) {
                numNeutrons++;
            }
        } );
        return numNeutrons;
    };

    Atom.prototype.getWeight = function () {
        return this.nucleons.length;
    };

    Atom.prototype.getCharge = function () {
        var protons = this.getNumProtons();
        return protons - this.electrons.length;
    };

    Atom.prototype.reconfigureNucleus = function ( moveImmediately ) {

        // Convenience variables.
        var centerX = this.xPos;
        var centerY = this.yPos;
        var nucleonRadius = SharedConstants.NUCLEON_RADIUS;
        var angle, distFromCenter;

        if ( this.nucleons.length === 0 ) {
            // Nothing to do.
            return;
        }
        else if ( this.nucleons.length === 1 ) {
            // There is only one nucleon present, so place it in the center
            // of the atom.
            this.nucleons[0].setLocation( {x:centerX, y:centerY} );
        }
        else if ( this.nucleons.length === 2 ) {
            // Two nucleons - place them side by side with their meeting point in the center.
            angle = Math.random() * 2 * Math.PI;
            this.nucleons[0].setLocation( { x:centerX + nucleonRadius * Math.cos( angle ), y:centerY + nucleonRadius * Math.sin( angle ) } );
            this.nucleons[1].setLocation( { x:centerX - nucleonRadius * Math.cos( angle ), y:centerY - nucleonRadius * Math.sin( angle ) } );
        }
        else if ( this.nucleons.length === 3 ) {
            // Three nucleons - form a triangle where they all touch.
            angle = Math.random() * 2 * Math.PI;
            distFromCenter = nucleonRadius * 1.155;
            this.nucleons[0].setLocation( { x:centerX + distFromCenter * Math.cos( angle ),
                                              y:centerY + distFromCenter * Math.sin( angle ) } );
            this.nucleons[1].setLocation( { x:centerX + distFromCenter * Math.cos( angle + 2 * Math.PI / 3 ),
                                              y:centerY + distFromCenter * Math.sin( angle + 2 * Math.PI / 3 ) } );
            this.nucleons[2].setLocation( { x:centerX + distFromCenter * Math.cos( angle + 4 * Math.PI / 3 ),
                                              y:centerY + distFromCenter * Math.sin( angle + 4 * Math.PI / 3 ) } );
        }
        else if ( this.nucleons.length === 4 ) {
            // Four nucleons - make a sort of diamond shape with some overlap.
            angle = Math.random() * 2 * Math.PI;
            this.nucleons[0].setLocation( {
                                              x:centerX + nucleonRadius * Math.cos( angle ),
                                              y:centerY + nucleonRadius * Math.sin( angle )} );
            this.nucleons[2].setLocation( {
                                              x:centerX - nucleonRadius * Math.cos( angle ),
                                              y:centerY - nucleonRadius * Math.sin( angle ) } );
            distFromCenter = nucleonRadius * 2 * Math.cos( Math.PI / 3 );
            this.nucleons[1].setLocation( {
                                              x:centerX + distFromCenter * Math.cos( angle + Math.PI / 2 ),
                                              y:centerY + distFromCenter * Math.sin( angle + Math.PI / 2 ) } );
            this.nucleons[3].setLocation( {
                                              x:centerX - distFromCenter * Math.cos( angle + Math.PI / 2 ),
                                              y:centerY - distFromCenter * Math.sin( angle + Math.PI / 2 ) } );
        }
        else if ( this.nucleons.length >= 5 ) {
            // This is a generalized algorithm that should work for five or
            // more nucleons.
            var placementRadius = 0;
            var numAtThisRadius = 1;
            var level = 0;
            var placementAngle = 0;
            var placementAngleDelta = 0;
            for ( var i = 0; i < this.nucleons.length; i++ ) {
                this.nucleons[i].setLocation( {
                                                  x:centerX + placementRadius * Math.cos( placementAngle ),
                                                  y:centerY + placementRadius * Math.sin( placementAngle )} );
                numAtThisRadius--;
                if ( numAtThisRadius > 0 ) {
                    // Stay at the same radius and update the placement angle.
                    placementAngle += placementAngleDelta;
                }
                else {
                    // Move out to the next radius.
                    level++;
                    placementRadius += nucleonRadius * 1.35 / level;
                    placementAngle += Math.PI / 8; // Arbitrary value chosen based on looks.
                    numAtThisRadius = Math.floor( placementRadius * Math.PI / nucleonRadius );
                    placementAngleDelta = 2 * Math.PI / numAtThisRadius;
                }
            }

            //WARNING: THIS IS A SPECIAL CASE FOR HANDLING A CERTAIN ISOTOPE OF LITHIUM
            //Make this isotope of lithium look better, some of the neutrons overlap
            //too much for discerning in the game mode
            // TODO: Integrate this, which was commented out when initially ported from Java.
//            if ( this.nucleons.length == 7 && neutrons.size() == 4 ) {
//                neutron = neutrons.get( neutrons.size() - 1 );
//                neutron.setDestination(
//                        neutron.getDestination().getX(),
//                        neutron.getDestination().getY() - 3 );
//            }
        }

        //If the particles shouldn't be animating, they should immediately move to their destination
//        if ( moveImmediately ) {
//            for ( SphericalParticle nucleon : nucleons
//        )
//            {
//                nucleon.moveToDestination();
//            }
//        }
    };


    return Atom;

} );
