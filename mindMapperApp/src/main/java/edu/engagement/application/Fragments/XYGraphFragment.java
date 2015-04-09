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
import org.achartengine.chart.PointStyle;
import org.achartengine.model.RangeCategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import edu.engagement.application.MainActivity;
import edu.engagement.application.R;
import edu.engagement.application.Database.DataFilter;
import edu.engagement.application.R.id;
import edu.engagement.application.R.layout;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

public class XYGraphFragment extends Fragment {
  private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();		// dataset includes all series that go into chart
  private XYMultipleSeriesRenderer mainRenderer = new XYMultipleSeriesRenderer();	// main renderer includes all renderers customizing chart
  private XYSeries currentSeries;												// most recently added series
  private SimpleSeriesRenderer currentSeriesRenderer;								// most recently created renderer, customizing the current series
  private GraphicalView mChartView;												// chart that displays the data
  
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
        dataset = (XYMultipleSeriesDataset) savedInstanceState.getSerializable("dataset");
        mainRenderer = (XYMultipleSeriesRenderer) savedInstanceState.getSerializable("renderer");
        currentSeries = (XYSeries) savedInstanceState.getSerializable("current_series");
        currentSeriesRenderer = (XYSeriesRenderer) savedInstanceState.getSerializable("current_renderer");
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

      // set some properties on the main renderer
      mainRenderer.setApplyBackgroundColor(true);
      mainRenderer.setBackgroundColor(Color.BLACK);
      mainRenderer.setAxisTitleTextSize(16);
      mainRenderer.setChartTitleTextSize(20);
      mainRenderer.setLabelsTextSize(17);
      mainRenderer.setLegendTextSize(17);
      mainRenderer.setShowCustomTextGrid(true);
      mainRenderer.setGridColor(Color.DKGRAY);

      mainRenderer.setZoomButtonsVisible(false);
      mainRenderer.setPointSize(3);

      if (mChartView == null) {
          initChartView(view);
      }
      
      loadData();
      
      mChartView.repaint();
      
      return view;
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
        dataset = (XYMultipleSeriesDataset) savedInstanceState.getSerializable("dataset");
        mainRenderer = (XYMultipleSeriesRenderer) savedInstanceState.getSerializable("renderer");
        currentSeries = (XYSeries) savedInstanceState.getSerializable("current_series");
        currentSeriesRenderer = (XYSeriesRenderer) savedInstanceState.getSerializable("current_renderer");
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

    savedInstanceState.putSerializable("dataset", dataset);
    savedInstanceState.putSerializable("renderer", mainRenderer);
    savedInstanceState.putSerializable("current_series", currentSeries);
    savedInstanceState.putSerializable("current_renderer", currentSeriesRenderer);
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
      mChartView = ChartFactory.getLineChartView(this.getActivity(), dataset, mainRenderer);
      // enable the chart click events
      mainRenderer.setClickEnabled(true);
      mainRenderer.setSelectableBuffer(10);
      mChartView.setOnClickListener(new View.OnClickListener() {
        public void onClick(View v) {
          // handle the click event on the chart
          SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
//          if (seriesSelection == null) {
//            Toast.makeText(XYGraphFragment.this.getActivity(), "No chart element", Toast.LENGTH_SHORT).show();
//          } else {
//            // display information of the clicked point
//            Toast.makeText(
//                XYGraphFragment.this.getActivity(),
//                "Chart element in series index " + seriesSelection.getSeriesIndex()
//                    + " data point index " + seriesSelection.getPointIndex() + " was clicked"
//                    + " closest point value X=" + seriesSelection.getXValue() + ", Y="
//                    + seriesSelection.getValue(), Toast.LENGTH_SHORT).show();
//          }
        }
      });
      layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT,
          LayoutParams.FILL_PARENT));
  }
  
  public void createSeries(){
	  String seriesTitle = "Day " + (dataset.getSeriesCount() + 1);
	  
      // create a new series of data
      XYSeries series = new XYSeries(seriesTitle);
      dataset.addSeries(series);
      currentSeries = series;
      
      // create a new renderer for the new series
      XYSeriesRenderer renderer = new XYSeriesRenderer();
      mainRenderer.addSeriesRenderer(renderer);
      
      // set some renderer properties
      renderer.setPointStyle(PointStyle.CIRCLE);
      renderer.setFillPoints(true);
      renderer.setDisplayChartValues(false);
      renderer.setDisplayChartValuesDistance(10);
      renderer.setChartValuesTextSize(17);
      renderer.setColor(getColor(dataset.getSeriesCount()));
      currentSeriesRenderer = renderer;
      
      if(mChartView != null)
    	  mChartView.repaint();
  }
  
  public int getColor(int index){
	  switch(index){
	  case 0:
		  return Color.RED;
	  case 1:
		  return Color.WHITE;
	  case 2:
		  return Color.GREEN;
	  case 3:
		  return Color.YELLOW;
	  case 4:
		  return Color.MAGENTA;
	  case 5:
		  return Color.GRAY;
	  case 6:
		  return Color.CYAN;
	  default:
		  return Color.RED;
	  }
  }
  
  public void addPoint(double x, double y){
    
      // add a new data point to the current series
      currentSeries.add(x, y);
      
      // repaint the chart such as the newly added point to be visible
      if(mChartView != null)
    	  mChartView.repaint();
  }
  public void clearGraph(){
	  currentSeries.clear();
	  dataset.clear();
      if(mChartView != null)
    	  mChartView.repaint();
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
//		double timestamp = results.get(i)[0];
//		double latitude = results.get(i)[1];
//		double longitude = results.get(i)[2];
//		double attention = results.get(i)[3];
//		addPoint(i, listener.round(attention, 2, BigDecimal.ROUND_HALF_UP));
//
//	}
//	  dataset.clear();
//	  dataset.addSeries(currentSeries);
  }

	public void redraw(){
		loadData();
		if(mChartView != null)
			mChartView.repaint();
	}

}
