package edu.colorado.phet.densityandbuoyancy.components {
import edu.colorado.phet.densityandbuoyancy.model.DensityObject;
import edu.colorado.phet.densityandbuoyancy.model.Material;
import edu.colorado.phet.densityandbuoyancy.model.NumericProperty;
import edu.colorado.phet.densityandbuoyancy.view.units.Unit;

public class DensityEditor extends PropertyEditor {
    private var densityObject: DensityObject;

    public function DensityEditor( property: NumericProperty, minimum: Number, maximum: Number, unit: Unit, dataTipClamp: Function, bounds: Bounds, sliderWidth: Number ) {
        super( property, minimum, maximum, unit, dataTipClamp, bounds, sliderWidth );
        this.densityObject = densityObject;
        textField.enabled = false; // direct density changes are now disabled
        setStyle( "paddingTop", 10 ); // give us a bit more padding to compensate for the labeled tickmarks
    }

    override protected function createSlider( property: NumericProperty, minimum: Number, maximum: Number, unit: Unit, dataTipClamp: Function, bounds: Bounds ): SliderDecorator {
        const slider: SliderDecorator = super.createSlider( property, minimum, maximum, unit, dataTipClamp, bounds );
        for each ( var material: Material in Material.LABELED_DENSITY_MATERIALS ) {
            slider.addTick( unit.fromSI( material.getDensity() ), material.tickColor, material.name )
        }
        slider.enabled = false; // direct density changes are now disabled
        return slider;
    }

    protected override function getSliderThumbClass(): Class {
        return DensitySliderThumb;
    }
}
}