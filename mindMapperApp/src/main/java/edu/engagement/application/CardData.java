package edu.engagement.application;

/*
 *  Data representation for each individual card for each cardView
 */
public class CardData {

	private double average;

	/* We want to express the variance in terms of percentage */
	private double variance;
	
	private String location;

	/*
	 * A special message. Example would be
	 * "This is your most attentive location!"
	 */
	private String label;

	/*
	 * Constructor
	 */
	public CardData() {
		this.average = 0;
		this.variance = 0;
		this.location = "";
		this.label = "";
	}

	/*
	 * Constructor
	 */
	public CardData(double av, double var, String loc, String lab) {
		this.average = av;
		this.variance = var;
		this.location = loc;
		this.label = lab;
	}

	public double getAverage() {
		return this.average;
	}

	public double getVariance() {
		return this.variance;
	}

	public String getLocation() {
		return this.location;
	}

	public String getLabel() {
		return this.label;
	}

}
