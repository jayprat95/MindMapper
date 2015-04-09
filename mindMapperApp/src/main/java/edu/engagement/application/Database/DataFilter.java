package edu.engagement.application.Database;

import java.util.Date;

public class DataFilter {

	public static final String HIGH_ENGAGEMENT = "HIGH";
	public static final String LOW_ENGAGEMENT = "LOW";
	
	private static double baseline;
	private static int daysToLoad;
	private static String filter;
	private static double attentionCutoff;
	private static String source;
	
	public DataFilter(int days, String f, int base, String src){
		daysToLoad = days;
		filter = f;
		baseline = base;
		source = src;
	}
	
	public static double getBaseline() {
		return baseline;
	}
	public static void setBaseline(double baseline) {
		DataFilter.baseline = baseline;
	}
	public static int getDaysToLoad() {
		return daysToLoad;
	}
	public static void setDaysToLoad(int daysToLoad) {
		DataFilter.daysToLoad = daysToLoad;
	}
	public static String getFilter() {
		return filter;
	}
	public static void setFilter(String filter) {
		DataFilter.filter = filter;
	}

	public static double getAttentionThreshold() {

		double engagementCutoff = 0;
		if (DataFilter.getFilter() != null) {
			// We use both time and engagement filters.
			
			if (DataFilter.getFilter() .equals(DataPointSource.SHOW_HIGH_ENGAGEMENT)) {
				// Query for high engagement points.
				engagementCutoff = (DataFilter.getBaseline() + 200) / 2; // TODO: Replace 200 with max engagement.
			}
			else{
				// Query for low engagement points.
				engagementCutoff = DataFilter.getBaseline()  / 2;
			}
		} 
		setAttentionCutoff(engagementCutoff);
		return attentionCutoff;
	}

	public static void setAttentionCutoff(double attentionCutoff) {
		DataFilter.attentionCutoff = attentionCutoff;
	}

	public static String getSource() {
		return source;
	}

	public static void setSource(String source) {
		DataFilter.source = source;
	}
}
