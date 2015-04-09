package edu.engagement.application;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import edu.engagement.application.Database.DataPointSource;
import edu.engagement.thrift.EngagementInformation;
import edu.engagement.thrift.EngagementService;
import edu.engagement.thrift.EngagementServiceUnavailable;
import android.os.AsyncTask;

public class UploadDataTask extends AsyncTask<DataPointSource, Void, Void> 
{
	
	protected Void doInBackground(DataPointSource... dataSources)
	{
		DataPointSource dataSource = dataSources[0];
		try
		{
			TTransport transport = new TSocket("128.173.237.162", 8080);
			TBinaryProtocol protocol = new TBinaryProtocol(transport);
			EngagementService.Client client = new EngagementService.Client(protocol);
			transport.open();

			EngagementInformation info = new EngagementInformation();
			info.setGoogleId("code.mr.black"); //TODO, get user google ID
			info.setEegPowerMessages(dataSource.getAllDataPointsEEG());
			info.setHeartRateMessages(dataSource.getAllDataPointsHR());
			info.setEegAttentionMessages(dataSource.getAllDataPointsAttention());
			info.setEegRawMessages(dataSource.getAllDataPointsRaw());
			client.syncEngagementInformation(info);
			transport.close();
		} catch (TTransportException e)
		{
			// TODO(mr-black): Fail intelligently.
			e.printStackTrace();
		} catch (EngagementServiceUnavailable e)
		{
			// TODO(mr-black): Fail intelligently.
			e.printStackTrace();
		} catch (TException e)
		{
			// TODO(mr-black): Fail intelligently.
			e.printStackTrace();
		}
		return null;
    }
}
