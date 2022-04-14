
package com.js.stepcounter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.js.stepcounter.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
public class MyMarkerView extends MarkerView {

    private final TextView tvContent, tvstep;
    String chartValue;

    public MyMarkerView(Context context, int layoutResource, String value) {
        super(context, layoutResource);
        chartValue = value;
        tvContent = findViewById(R.id.tvContent);
        tvstep = findViewById(R.id.tvstep);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;

            tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));
        } else {
            tvstep.setText(Utils.formatNumber(e.getX(), 0, true) + "");
            tvContent.setText(Utils.formatNumber(e.getY(), 0, true) + " " + chartValue);
//            tvContent.setText(Utils.formatNumber(e.getY(), 0, true));
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
