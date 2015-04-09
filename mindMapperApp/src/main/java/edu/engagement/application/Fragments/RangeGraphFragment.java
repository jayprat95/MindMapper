/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.engagement.application.Fragments;

import java.math.BigDecimal;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import edu.engagement.application.MainActivity;
import edu.engagement.application.R;
import edu.engagement.application.Database.DataFilter;
import edu.engagement.application.R.id;
import edu.engagement.application.R.layout;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

/**
 * Temperature demo range chart.
 */
public class RangeGraphFragment extends AbstractGraphFragment {

  private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();			// dataset includes all series that go into chart
  private XYMultipleSeriesRenderer mainRenderer = new XYMultipleSeriesRenderer();	// main renderer includes all renderers customizing chart
  private RangeCategorySeries currentSeries;										// most recently added series
  private SimpleSeriesRenderer currentSeriesRenderer;								// most recently created renderer, customizing the current series
  private GraphicalView mChartView;													// chart that displays the data
  
  private MainActivity listener;

  // Called when the Fragment is attached to its parent Activity.
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    // Get a reference to the parent Activity.
    listener = (MainActivity) activity;
  }

  // Called to do the initial creation of the Fragment.
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Initialize the Fragment.
    setRetainInstance(false);

    if(savedInstanceState != null){
        dataset = (XYMultipleSeriesDataset) savedInstanceState.getSerializable("range_dataset");
        mainRenderer = (XYMultipleSeriesRenderer) savedInstanceState.getSerializable("range_renderer");
        currentSeries = (RangeCategorySeries) savedInstanceState.getSerializable("range_current_series");
        currentSeriesRenderer = (SimpleSeriesRenderer) savedInstanceState.getSerializable("range_current_renderer");
    }
  }

  // Called once the Fragment has been created in order for it to
  // create its user interface.
  @Override
  public View onCreateView(LayoutInflater inflater, 
                           ViewGroup container,
                           Bundle savedInstanceState) {
    // Create, or inflate the Fragment's UI, and return it. 
    // If this Fragment has no UI then return null.
	  
	  View view = inflater.inflate(R.layout.xy_chart, container, false);

	  buildMainRenderer();
      loadData();
      
      if (mChartView == null) {
          initChartView(view);
      }
      mChartView.repaint();
      
      return view;
  }
 
  public void buildMainRenderer(){
      
      int[] colors = new int[] { Color.CYAN };
      mainRenderer = buildBarRenderer(colors);
      setChartSettings(mainRenderer, "", "Day", "", 0.5, 12.5,
          -30, 45, Color.GRAY, Color.LTGRAY);
      mainRenderer.setBarSpacing(0.5);
      mainRenderer.setXLabels(5);
      mainRenderer.setYLabels(0);
      
      mainRenderer.setApplyBackgroundColor(true);
      mainRenderer.setBackgroundColor(Color.BLACK);
      mainRenderer.setAxisTitleTextSize(16);
      mainRenderer.setChartTitleTextSize(20);
      mainRenderer.setLabelsTextSize(17);
      mainRenderer.setLegendTextSize(17);

      mainRenderer.setZoomButtonsVisible(false);
      mainRenderer.setPointSize(3);
      
      mainRenderer.setMargins(new int[] {20, -1, 10, 20});
      mainRenderer.setYLabelsAlign(Align.LEFT);
      mainRenderer.setYLabelsPadding(-20);
      
  }
  // Called once the parent Activity and the Fragment's UI have 
  // been created.
  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // Complete the Fragment initialization particularly anything
    // that requires the parent Activity to be initialized or the 
    // Fragment's view to be fully inflated.

    if(savedInstanceState != null){
        dataset = (XYMultipleSeriesDataset) savedInstanceState.getSerializable("range_dataset");
        mainRenderer = (XYMultipleSeriesRenderer) savedInstanceState.getSerializable("range_renderer");
        currentSeries = (RangeCategorySeries) savedInstanceState.getSerializable("range_current_series");
        currentSeriesRenderer = (SimpleSeriesRenderer) savedInstanceState.getSerializable("range_current_renderer");
    }
  }

  // Called at the start of the visible lifetime.
  @Override
  public void onStart(){
    super.onStart();
    // Apply any required UI change now that the Fragment is visible.
  }

  // Called at the start of the active lifetime.
  @Override
  public void onResume(){
    super.onResume();
    // Resume any paused UI updates, threads, or processes required
    // by the Fragment but suspended when it became inactive.
  }

  // Called at the end of the active lifetime.
  @Override
  public void onPause(){
    // Suspend UI updates, threads, or CPU intensive processes
    // that don't need to be updated when the Activity isn't
    // the active foreground activity.
    // Persist all edits or state changes
    // as after this call the process is likely to be killed.
    super.onPause();
  }

  // Called to save UI state changes at the
  // end of the active lifecycle.
  @Override
  public void onSaveInstanceState(Bundle savedInstanceState) {
    // Save UI state changes to the savedInstanceState.
    // This bundle will be passed to onCreate, onCreateView, and
    // onCreateView if the parent Activity is killed and restarted.
    super.onSaveInstanceState(savedInstanceState);

    savedInstanceState.putSerializable("range_dataset", dataset);
    savedInstanceState.putSerializable("range_renderer", mainRenderer);
    savedInstanceState.putSerializable("range_current_series", currentSeries);
    savedInstanceState.putSerializable("range_current_renderer", currentSeriesRenderer);
  }

  // Called at the end of the visible lifetime.
  @Override
  public void onStop(){
    // Suspend remaining UI updates, threads, or processing
    // that aren't required when the Fragment isn't visible.
    super.onStop();
  }

  // Called when the Fragment's View has been detached.
  @Override
  public void onDestroyView() {
    // Clean up resources related to the View.
    super.onDestroyView();
  }

  // Called at the end of the full lifetime.
  @Override
  public void onDestroy(){
    // Clean up any resources including ending threads,
    // closing database connections etc.
    super.onDestroy();
  }

  // Called when the Fragment has been detached from its parent Activity.
  @Override
  public void onDetach() {
    super.onDetach();
  }
  
  //========================================================================
  //========================================================================

  public void initChartView(View parent){
	  LinearLayout layout = (LinearLayout) parent.findViewById(R.id.graph_list);
	  
      mChartView = ChartFactory.getRangeBarChartView(this.getActivity(), dataset, mainRenderer,Type.DEFAULT);
      // enable the chart click events
      mainRenderer.setClickEnabled(true);
      mainRenderer.setSelectableBuffer(10);
      mChartView.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          // handle the click event on the chart
          SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();

        }
      });
      layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
          LayoutParams.FILL_PARENT));
      
  }
  
  public void createSeries(){
	  String seriesTitle = "Day " + (dataset.getSeriesCount() + 1);
	  
      // create a new series of data
	  RangeCategorySeries series = new RangeCategorySeries(seriesTitle);
      
      currentSeries = series;
      
      dataset.addSeries(currentSeries.toXYSeries());
      
      currentSeriesRenderer = mainRenderer.getSeriesRendererAt(0);
      currentSeriesRenderer.setDisplayChartValues(true);
      currentSeriesRenderer.setChartValuesTextSize(12);
      currentSeriesRenderer.setChartValuesSpacing(3);
      currentSeriesRenderer.setGradientEnabled(true);
      currentSeriesRenderer.setGradientStart(20, Color.BLUE);
      currentSeriesRenderer.setGradientStop(100, Color.RED);
      
      if(mChartView != null)
    	  mChartView.repaint();
  }
 
  public void addPoint(double x, double y){
    
      // add a new data point to the current series
      currentSeries.add(x, y);
      
      // repaint the chart such as the newly added point to be visible
      if(mChartView != null)
    	  mChartView.repaint();
  }


  /**
   * Returns the chart name.
   * 
   * @return the chart name
   */
  public String getName() {
    return "Engagement Range Graph";
  }

  /**
   * Returns the chart description.
   * 
   * @return the chart description
   */
  public String getDesc() {
    return "";
  }

public void loadData(){
//	  createSeries();
//	  
//	  DataFilter.setSource(this.getClass().toString());
//	  List<double[]> results = listener.getData();
//	  for(int i = 0; i < results.size(); i++){
//
//			/*
//			 * 0 - Timestamp
//			 * 1 - Latitude
//			 * 2 - Longitude
//			 * 3 - Attention
//			 * 4 - Count
//			 */
//		double min = results.get(i)[3];
//		double max = results.get(i)[4];
//		addPoint(listener.round(min, 2, BigDecimal.ROUND_HALF_UP), listener.round(max, 2, BigDecimal.ROUND_HALF_UP));
//
//	  }
//      dataset.clear();
//      dataset.addSeries(currentSeries.toXYSeries());
	}

	public void redraw(){
		loadData();
		if(mChartView != null)
			mChartView.repaint();
	}

}


