package edu.engagement.application.Database;

public class DataPoint
{
	private int sessionId;
	private long timeStamp;
	private double heartRate;
	private double alpha;
	private double alpha_1;
	private double alpha_2;
	private double beta;
	private double beta_1;
	private double beta_2;
	private double theta;
	private double pope;
	private double attention;
	private double ch1;
	private double ch2;
	private double ch3;
	private double ch4;
	private double ch5;
	private double ch6;
	private double ch7;
	private double ch8;
	private int gpsKey;
	private double lat;
	private double lng;
    private String annotation;
	
	//TODO modification just for the attention database
	private int day;
	private int month;

	public DataPoint(int sessionId, long timeStamp, double hr, double alpha, double alpha_1,double alpha_2,double beta, double beta_1, double beta_2,double theta, double pope, double attention, String annotation, double ch1,
			double ch2, double ch3, double ch4, double ch5, double ch6, double ch7, double ch8, int gpsKey, double lat, double lng)
	{
		this.sessionId = sessionId;
		this.timeStamp = timeStamp;
		this.heartRate = hr;
		this.alpha = alpha;
		this.alpha_1 = alpha_1;
		this.alpha_2 = alpha_2;
		this.beta = beta;
		this.beta_1 = beta_1;
		this.beta_2 = beta_2;
		this.theta = theta;
		this.pope = pope;
		this.attention = attention;
        this.annotation = annotation;
		this.ch1 = ch1;
		this.ch2 = ch2;
		this.ch3 = ch3;
		this.ch4 = ch4;
		this.ch5 = ch5;
		this.ch6 = ch6;
		this.ch7 = ch7;
		this.ch8 = ch8;
		this.gpsKey = gpsKey;
		this.lat = lat;
		this.lng = lng;
	}
	// TODO overload for attention table changes
	public DataPoint(long timeStamp, double hr, double alpha, double beta, double theta, double attention, double ch1,
			double ch2, double ch3, double ch4, double ch5, double ch6, double ch7, double ch8, int gpsKey, double lat, double lng, double accuracy, int day, int month)
	{
		this.timeStamp = timeStamp;
		this.heartRate = hr;
		this.alpha = alpha;
		this.beta = beta;
		this.theta = theta;
		this.attention = attention;
		this.ch1 = ch1;
		this.ch2 = ch2;
		this.ch3 = ch3;
		this.ch4 = ch4;
		this.ch5 = ch5;
		this.ch6 = ch6;
		this.ch7 = ch7;
		this.ch8 = ch8;
		this.gpsKey = gpsKey;
		this.lat = lat;
		this.lng = lng;
		this.day = day;
		this.month = month;
	}

    public int getSessionId(){
        return sessionId;
    }
	public double getAlpha()
	{
		return alpha;
	}

	public void setAlpha(double alpha)
	{
		this.alpha = alpha;
	}

	public double getBeta()
	{
		return beta;
	}

	public void setBeta(double beta)
	{
		this.beta = beta;
	}

	public double getTheta()
	{
		return theta;
	}

	public void setTheta(double theta)
	{
		this.theta = theta;
	}

	public long getTimeStamp()
	{
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp)
	{
		this.timeStamp = timeStamp;
	}

	public double getHeartRate()
	{
		return heartRate;
	}

	public void setHeartRate(double heartRate)
	{
		this.heartRate = heartRate;
	}

	public double getAttention()
	{
		return attention;
	}

	public double getCh1()
	{
		return ch1;
	}

	public double getCh2()
	{
		return ch2;
	}

	public double getCh3()
	{
		return ch3;
	}

	public double getCh4()
	{
		return ch4;
	}

	public double getCh5()
	{
		return ch5;
	}

	public double getCh6()
	{
		return ch6;
	}

	public double getCh7()
	{
		return ch7;
	}

	public double getCh8()
	{
		return ch8;
	}
	
	public int getGpsKey()
	{
		return gpsKey;
	}
	
	public double getLat()
	{
		return lat;
	}
	
	public double getLng()
	{
		return lng;
	}


}
