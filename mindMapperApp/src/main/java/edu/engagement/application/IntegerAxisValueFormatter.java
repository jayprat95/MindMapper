package edu.engagement.application;

import com.github.mikephil.charting.utils.ValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by alex on 12/2/15.
 */
public class IntegerAxisValueFormatter implements ValueFormatter {

    private DecimalFormat format;

    public IntegerAxisValueFormatter() {
        format = new DecimalFormat("##0");
    }

    @Override
    public String getFormattedValue(float value) {
        return format.format(value);
    }
}
