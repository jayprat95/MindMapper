package edu.engagement.application ;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import zephyr.android.HxMBT.*;

public class NewConnectedListener extends ConnectListenerImpl
{
	private Handler _aNewHandler; 
	private int HR_SPD_DIST_PACKET =0x26;
	
	private final int HEART_RATE = 0x100;
	//private final int INSTANT_SPEED = 0x101;
	//private final int TIME_STAMP = 0x102;
	private HRSpeedDistPacketInfo HRSpeedDistPacket = new HRSpeedDistPacketInfo();
	
	public NewConnectedListener(Handler handler,Handler _NewHandler)
	{
		super(handler, null);
		_aNewHandler = _NewHandler;
	}
	
	public void Connected(ConnectedEvent<BTClient> eventArgs)
	{
		System.out.println(String.format("Connected to BioHarness %s.", eventArgs.getSource().getDevice().getName()));

		//Creates a new ZephyrProtocol object and passes it the BTComms object
		ZephyrProtocol _protocol = new ZephyrProtocol(eventArgs.getSource().getComms());
		_protocol.addZephyrPacketEventListener(new ZephyrPacketListener() {
			public void ReceivedPacket(ZephyrPacketEvent eventArgs)
			{
				ZephyrPacketArgs msg = eventArgs.getPacket();
				if (HR_SPD_DIST_PACKET==msg.getMsgID())
				{
					byte [] DataArray = msg.getBytes();
					
					
					
					//***************Displaying the Heart Rate********************************
					int HRate =  HRSpeedDistPacket.GetHeartRate(DataArray);
					Message text1 = _aNewHandler.obtainMessage(HEART_RATE);
					Bundle b1 = new Bundle();
					b1.putInt("HeartRate", HRate);
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					System.out.println("Heart Rate is "+ HRate);

					/*
					//***************Displaying the Instant Speed********************************
					double InstantSpeed = HRSpeedDistPacket.GetInstantSpeed(DataArray);
					
					text1 = _aNewHandler.obtainMessage(INSTANT_SPEED);
					b1.putString("InstantSpeed", String.valueOf(InstantSpeed));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					System.out.println("Instant Speed is "+ InstantSpeed);
					
					
					//Get the timestamp
					long timeStamp = System.currentTimeMillis();
					text1 = _aNewHandler.obtainMessage(TIME_STAMP);
					b1.putString("TimeStamp", String.valueOf(timeStamp));
					text1.setData(b1);
					_aNewHandler.sendMessage(text1);
					System.out.println("Time stamp is "+ timeStamp);
					*/
					
				}
			}
		});
	}
	
}