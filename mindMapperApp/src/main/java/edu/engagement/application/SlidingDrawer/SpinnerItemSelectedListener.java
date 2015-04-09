package edu.engagement.application.SlidingDrawer;

import edu.engagement.application.Database.DataFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

public class SpinnerItemSelectedListener implements OnItemSelectedListener {

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        String selected = parent.getItemAtPosition(pos).toString();
        String num = selected.split(" ")[0];
        DataFilter.setDaysToLoad(Integer.valueOf(num));
    }

    public void onNothingSelected(AdapterView parent) {
        // Do nothing.
    }
}
