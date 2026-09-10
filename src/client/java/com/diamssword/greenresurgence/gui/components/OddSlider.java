package com.diamssword.greenresurgence.gui.components;

import io.wispforest.owo.ui.component.DiscreteSliderComponent;
import io.wispforest.owo.ui.core.Sizing;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class OddSlider extends DiscreteSliderComponent {
	private final boolean inverted;

	public OddSlider(Sizing horizontalSizing, double min, double max, boolean inverted) {
		super(horizontalSizing, min, max);
		this.inverted = inverted;
	}

	public double discreteValue() {
		return roundValue(new BigDecimal(this.min + this.value * (this.max - this.min)).setScale(this.decimalPlaces, RoundingMode.HALF_UP).intValue());
	}

	@Override
	public DiscreteSliderComponent setFromDiscreteValue(double discreteValue) {
		this.value((roundValue((int) discreteValue) - min) / (max - min));
		return this;
	}

	private int roundValue(int value) {
		if(value % 2 == 0)
			return inverted ? value : (value - 1);
		return inverted ? (value - 1) : value;
	}

}
