define( ['easel', ], function ( createjs ) {

    //id is the string that identifies the tab for this module, used for creating unique ids.
    return function ( id, running, sliderControls ) {

        var canvas = $( '<canvas></canvas>' ).attr( "id", getID( "c" ) ).css( "position", "absolute" );
        var tab = $( "#" + id );
        tab.append( canvas );

        var splineLayer = Spline.createSplineLayer( groundHeight );

        root.addChild( Background.createBackground( groundHeight ) );
        var grid = new Grid( groundY );
        grid.visible = false;
        root.addChild( grid );
        root.addChild( splineLayer );
        var barChart = BarChart.createBarChart( skater );
        barChart.x = 50;
        barChart.y = 50;
        barChart.visible = false;
        root.addChild( barChart );

        root.addChild( skater );

        var fpsText = new createjs.Text( '-- fps', '24px "Lucida Grande",Tahoma', createjs.Graphics.getRGB( 153, 153, 230 ) );
        fpsText.x = 4;
        fpsText.y = 280;
        root.addChild( fpsText );
        var pieChart = new PieChart( skater );
        pieChart.visible = false;
        root.addChild( pieChart );

        var speedometer = Speedometer.createSpeedometer( skater );
        speedometer.visible = false;
        root.addChild( speedometer );

        //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
        var canvas = document.getElementById( getID( "c" ) );
        canvas.onselectstart = function () { return false; }; // IE
        canvas.onmousedown = function () { return false; }; // Mozilla

        var stage = new createjs.Stage( canvas );
        stage.mouseMoveOutside = true;
        stage.addChild( root );

        var frameCount = 0;

        var filterStrength = 20;
        var frameTime = 0, lastLoop = new Date, thisLoop;
        var paused = false;

        function updateFrameRate() {
            frameCount++;

            //Get frame rate but filter transients: http://stackoverflow.com/questions/4787431/check-fps-in-js
            var thisFrameTime = (thisLoop = new Date) - lastLoop;
            frameTime += (thisFrameTime - frameTime) / filterStrength;
            lastLoop = thisLoop;
            if ( frameCount > 30 ) {
                fpsText.text = (1000 / frameTime).toFixed( 1 ) + " fps";// @"+location.href;
            }
        }

        var pauseString = CommonStrings["Common.ClockControlPanel.Pause"];
        var playString = CommonStrings["Common.ClockControlPanel.Play"];
        console.log( "pauseString = " + pauseString + ", playString = " + playString );

        //TODO: use requirejs templating for this (But maybe not since it may not work over file://)
        var templateText = playPauseFlipSwitch( {tab: id, pauseString: "Pause", playString: "Play"} );
        console.log( templateText );
        tab.append( $( templateText ) ).trigger( "create" );

        var slowMotionString = Strings["slow.motion"];
        var normalString = Strings.normal;

        //Class for CSS selector.  Use a class instead of ID so that different tabs don't have to come up with unique ID for the elements.\
        var speedControlClass = "speedControl";
        tab.append( $( speedControl( {elementClass: speedControlClass, slowMotion: slowMotionString, normal: normalString} ) ) ).trigger( "create" );

        var text = controlPanelTemplate( {
                                             barGraph: Strings["plots.bar-graph"],
                                             pieChart: Strings["pieChart"],
                                             grid: Strings["controls.show-grid"],
                                             speedometer: Strings["properties.speed"],
                                             id: id,
                                             sliderControls: sliderControls} );
        tab.append( $( text ) ).trigger( "create" );

        //Wire up the pie chart check box button to the visibility of the pie chart
        tab$( "checkbox1" ).click( function () { barChart.visible = tab$( "checkbox1" ).is( ":checked" ); } );
        tab$( "checkbox2" ).click( function () { pieChart.visible = tab$( "checkbox2" ).is( ":checked" ); } );
        tab$( "checkbox3" ).click( function () { grid.visible = tab$( "checkbox3" ).is( ":checked" ); } );
        tab$( "checkbox4" ).click( function () { speedometer.visible = tab$( "checkbox4" ).is( ":checked" ); } );

        tab$( "returnSkaterButton" ).bind( "click", function () {
            skaterModel.returnSkater();
        } );
        tab$( "resetAllButton" ).bind( "click", function () {

            //This line of code took a long to find.  You cannot simply attr("checked","").
            tab$( "checkbox1" ).removeAttr( "checked" ).checkboxradio( "refresh" );
            tab$( "checkbox2" ).removeAttr( "checked" ).checkboxradio( "refresh" );
            tab$( "checkbox3" ).removeAttr( "checked" ).checkboxradio( "refresh" );
            tab$( "checkbox4" ).removeAttr( "checked" ).checkboxradio( "refresh" );

            barChart.visible = false;
            pieChart.visible = false;
            grid.visible = false;
            speedometer.visible = false;
            skaterModel.returnSkater();
        } );

        tab$( "barGraphLabel" ).find( '> .ui-btn-inner' ).append( '<img class="alignRightPlease" id="barChartIconImage" src="resources/barChartIcon.png" />' );
        tab$( "pieChartLabel" ).find( '> .ui-btn-inner' ).append( '<img class="alignRightPlease" id="pieChartIconImage" src="resources/pieChartIcon.png" />' );
        tab$( "gridLabel" ).find( '> .ui-btn-inner' ).append( '<img class="alignRightPlease" id="pieChartIconImage" src="resources/gridIcon.png" />' );
        tab$( "speedLabel" ).find( '> .ui-btn-inner' ).append( '<img class="alignRightPlease" id="pieChartIconImage" src="resources/speedIcon.png" />' );

        $( '#flip-min' ).val( 'on' ).slider( "refresh" );
        $( "#flip-min" ).bind( "change", function ( event, ui ) { paused = !paused; } );

        var onResize = function () {
            var winW = tab.width();
            var winH = tab.height();
            var scale = Math.min( winW / 1024, winH / 768 );
            var canvasW = scale * 1024;
            var canvasH = scale * 768;

            //Allow the canvas to fill the screen, but still center the content within the window.
            var canvas = $( "#" + getID( "c" ) );
            canvas.attr( 'width', winW );
            canvas.attr( 'height', winH );
            var left = (winW - canvasW) / 2;
            var top = (winH - canvasH) / 2;
            canvas.offset( {left: 0, top: 0} );
            root.scaleX = root.scaleY = scale;
            root.x = left;
            root.y = top;
            stage.update();

            $( "#navBar" ).css( 'top', top + 'px' ).css( 'left', (left + 50) + 'px' ).css( 'width', (canvasW - 100) + 'px' );

            //Scale the control panel up and down using css 2d transform
            //        $( "#controlPanel" ).css( "-webkit-transform", "scale(" + scale + "," + scale + ")" );

            var controlPanel = $( '#' + id + " > .controlPanel" );
            controlPanel.css( 'width', '270px' );
            controlPanel.css( 'top', 30 + 'px' );
            controlPanel.css( 'right', 0 + 'px' );

            //Apply css overrides last (i.e. after other css takes effect.
            //There must be a better way to do this, hopefully this can be improved easily.
            tab$( "slider-fill" ).remove();
            tab$( "frictionSlider" ).remove();

            //TODO: This code actually hits all of the sliders in every tab.  This should be fixed.
            var slider = $( ".ui-slider" );
            slider.css( "width", "100%" );
            slider.css( "marginTop", "0px" );
            slider.css( "marginLeft", "0px" );
            slider.css( "marginBottom", "0px" );
            slider.css( "marginRight", "0px" );

            //TODO: this vertical alignment is a hack that won't work for different settings
            tab$( "barGraphLabel" ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );
            tab$( "pieChartLabel" ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );
            tab$( "gridLabel" ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );
            tab$( "speedLabel" ).find( ".ui-btn-text" ).css( "position", "absolute" ).css( "top", "35%" );

            //TODO: This will need to be made more specific since it will cause problems if it applies to all slider switches
            $( '#' + id + 'containerForPlayPauseFlipSwitch ' ).css( 'position', 'absolute' ).css( 'width', '200px' );
            var leftSideOfPlayPauseButton = (left + canvasW / 2 - $( 'div.ui-slider-switch' ).width() / 2);
            $( '#' + id + 'containerForPlayPauseFlipSwitch ' ).css( 'left', leftSideOfPlayPauseButton + 'px' ).css( 'top', canvasH + top - 100 + 'px' );
            $( "#" + id + " > ." + speedControlClass ).css( 'position', 'absolute' ).css( 'width', '200px' ).css( 'top', canvasH + top - 100 + 'px' ).css( 'left', (leftSideOfPlayPauseButton - 350) + 'px' );

            console.log( "tab 1 resized, width = " + winW );
        };

        //Uses jquery resize plugin "jquery.ba-resize": http://benalman.com/projects/jquery-resize-plugin/
        //TODO: This line is too expensive on ipad, dropping the frame rate by 15FPS
//        $( "#" + id ).resize( onResize );
        $( window ).resize( onResize );
        onResize(); // initial position

        function moduleActive() {return $.mobile.activePage[0] == tab[0];}

        createjs.Ticker.setFPS( 60 );
        createjs.Ticker.addListener( function () {
            if ( moduleActive() ) {

                //make sure the nav bar button is showing as selected
                //http://stackoverflow.com/questions/3105984/how-to-get-element-by-href-in-jquery
                var tab1 = $( 'a[href="#tab1"]' );
                tab1.removeClass( "ui-btn-active" );
                var tab2 = $( 'a[href="#tab2"]' );
                tab2.removeClass( "ui-btn-active" );
                var tab3 = $( 'a[href="#tab3"]' );
                tab3.removeClass( "ui-btn-active" );

                var links = id == "tab1" ? tab1 :
                            id == "tab2" ? tab2 :
                            tab3;

                links.addClass( "ui-btn-active" );

                if ( !paused ) {
                    var dt = 0.02;
                    var subdivisions = 1;
                    for ( var i = 0; i < subdivisions; i++ ) {
                        Physics.updatePhysics( skaterModel, groundHeight, splineLayer, dt / subdivisions );
                    }
                    skater.updateFromModel();
                    updateFrameRate();
                    if ( barChart.visible ) {
                        barChart.tick();
                    }
                    if ( speedometer.visible ) {
                        speedometer.tick();
                    }
                    if ( pieChart.visible ) {
                        pieChart.tick();
                    }
                }
                stage.tick();
            }
        } );

        //Enable touch and prevent default
        createjs.Touch.enable( stage, false, false );

        //Necessary to enable MouseOver events
        stage.enableMouseOver();

        //Paint once after initialization
        stage.update();

        //Hide everything with a cover until the sim is all layed out.  http://stackoverflow.com/questions/9550760/hide-page-until-everything-is-loaded-advanced
        //Hide/Remove don't work everywhere, but the combination seems to work everywhere.
        $( "#cover" ).hide().remove();

        //Create the navbar
        var persist = "ui-btn-active ui-state-persist";
        var class1 = id == "tab" ? persist : "";
        var class2 = id == "tab2" ? persist : "";
        var class3 = id == "tab3" ? persist : "";
        tab.append( $( navBar( {class1: class1, class2: class2, class3: class3} ) ) ).trigger( "create" );
    };
} );