package com.example.polygraf1;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;



import com.example.polygraf1.Server;
import com.example.polygraf1.AbstractServerListener;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.content.Context;


public class Polygraf1_Connector extends Service {
	public static boolean ConnectorStatusON = false;
	public static Thread connectorThread;
	public static Context context;         
	public Polygraf1_Draw_Engine mThread;
	
	private Timer timer = new Timer();
	private int counter = 0, incrementby = 1;
	
	static final int MSG_REGISTER_CLIENT = 1;
	static final int MSG_SET_INT_VALUE = 3;
   
	//public ArrayList<?> RAW_DATA = new ArrayList<Object>();
	public int RAW_DATA[] = new int[5];
	
    final Messenger mMessenger = new Messenger(new IncomingHandler());	//Igor: new Messenger as method IncomingHandler
    
    
    public Messenger Address2 = null;
    Bundle          myB = new Bundle(); 
    private final   String TAG = "S E R V I C E: Connector"; 
    
    //****************SERVER*********************************************************************************************
    
    Server mServer = null;
  //  private int mLightLevel = 0;
  //  private int mTest_milliseconds = 0;
    
    
    //*******************************************************************************************************************
    
    
    
    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();									//Igor: if binded mMessenger returned 
    }
    
    
   //******************************************************************************************************************** 
   // 		===>
    																	//Igor: INCOMING DATA
    @SuppressLint("HandlerLeak")
	class IncomingHandler extends Handler { 							//Igor: Handling of incoming messages from clients
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                       	
            	case MSG_REGISTER_CLIENT:  	Address2 = msg.replyTo;	break;
                      	
            	case MSG_SET_INT_VALUE:	 incrementby = msg.arg1;	break;
            	
            	default:	super.handleMessage(msg);
            }
        }
    }
    
    
    //****************************************************************************************************************
    //		<===	
    																	//Igor:  OUTGOING DATA  
    private void sendMessageToUI(int DATA[]) {					//       these data are sent to 
        					
                        													      	            	            	
           // 	Address2.send(Message.obtain(null, MSG_SET_INT_VALUE, mTest_milliseconds, 0));
            	
    	
    //	RAW_DATA[0] = 452;
	//	RAW_DATA[1] = 47;
	//	RAW_DATA[2] = 789;
	//	RAW_DATA[3] = 123;
	//	RAW_DATA[4] = 469;
		
    	
    	
    	
            	  Message msg = Message.obtain();
                               
                  myB.putIntArray("raw_data", RAW_DATA);
                  msg.setData(myB);
                  
                           
              try {      
                  
                  Address2.send(msg);          	      
           
                
            } catch (RemoteException e) {
          
            	return;
            }
        
    }    

   //*****************************************************************************************************************
    
    
    
    
	@Override
	public void onCreate() {
		super.onCreate();
		
		 timer.scheduleAtFixedRate(new TimerTask(){ public void run() {onTimerTick();}}, 0, 10L);
		 	
	 		
				
		 Polygraf1_Draw_Engine.Draw_Engine_Thread = new Thread() {
				public void run() {
					Intent ConnectToProcessor = new Intent(getApplicationContext(), Polygraf1_Draw_Engine.class);
					startService(ConnectToProcessor);
					Polygraf1_Draw_Engine.context = getApplicationContext();
								}
				};		
				Polygraf1_Draw_Engine.Draw_Engine_Thread.start();
			
				
	//*********SERVER***************************************************************************************	
				
				try {
					mServer = new Server(4568); // Use ADK port
					mServer.start();
				} catch (IOException e) {
					Log.e(TAG, "Unable to start TCP server", e);
					System.exit(-1);
				}
				
				
				
				mServer.addListener(new AbstractServerListener() {

					@Override
					public void onReceive(com.example.polygraf1.Client client, byte[] data) {

						if (data.length < 10)
							return;
						
				//		RAW_DATA[0] = (data[0] & 0xff) | (data[1] & 0xff);
				//		RAW_DATA[1] = (data[2] & 0xff) | ((data[3] & 0xff) <<16);
				//		RAW_DATA[0] = (data[0] & 0xff)  | ((data[1] & 0xff) << 8);
				//		RAW_DATA[1] = (data[2] & 0xff)  | (data[3] & 0xff) << 8;
						
				//		int value = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
						
				//		RAW_DATA[2] = (data[4] & 0xff) | ((data[5] & 0xff) << 8);
				//		RAW_DATA[3] = (data[6] & 0xff) | ((data[7] & 0xff) << 8);
				//		RAW_DATA[4] = (data[8] & 0xff) | ((data[9] & 0xff) << 8);
				
						
					for(int i=0;i<=4;i++){
						RAW_DATA[i] = (data[i+i] & 0xff) | ((data[i+i+1] & 0xff) << 8);
				 		
					}
							
					}
				});	
							
				
	//*******************************************************************************************************			
																	
		}
		
	
	
	
	
	
	private void onTimerTick() {
   
        try {
            counter += incrementby;
         //   RAW_DATA[1] = incrementby + counter;
            sendMessageToUI(RAW_DATA);
            

        } catch (Throwable t) { 
            Log.e("TimerTick", "Timer Tick Failed.", t);            
        }
    }
	
	
	

	
	
	public int onStartCommand(Intent intent, int flags, int startId){
		return 1;
	}
	
	public void onStart(Intent intent, int startId) {
	}

	public void onStop() {
		
		
	}
	
	public IBinder onUnBind(Intent arg0) {
		return null;		
	}
	
	public void onPause(){
	}
	
	@Override
	public void onDestroy(){
		
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	@Override
	public void onLowMemory(){
	}
	

	
	
		
			
}
