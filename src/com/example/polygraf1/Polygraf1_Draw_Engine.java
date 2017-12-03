package com.example.polygraf1;


import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.os.IBinder;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class Polygraf1_Draw_Engine extends Service implements Runnable{
	
	
	public static Thread Draw_Engine_Thread;
	public static Context context;
	
	static final int MSG_SET_INT_VALUE = 3;
	static final int MSG_REGISTER_CLIENT = 1;
	
	final Messenger mMessenger = new Messenger(new engineIncomingHandler());
	final Messenger qMessenger = new Messenger(new IncomingHandler());
	
	Messenger dService = null;
	int fromMessenger = 5;
	public static int fromConnector = 0;
	
	static final int MSG_SAY_HELLO = 1;
	boolean mBound;
	
	public Messenger Address = null;
	
	
	Bundle myB = new Bundle();
		
	private int RAW_DATA[] = new int[5];
	//private static float [] float_DATA = new float[5];
	//private float _DATA[] = new float[5];
  
	int teeest = 56;
	
	private Timer countClock = new Timer();
	
	private Timer HeartBeatTimer = new Timer();
	private int HBTimerTick;
	
	private Timer mainRunClock = new Timer();
	
	private int refVAL;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return qMessenger.getBinder();
	}
	
    private static float dat[] = {0.00f, 13.2f};
    static int refIteration, refCurrent = 0;
  
    
    
//    private final AtomicInteger counter;
	
	//	Polygraf1_Draw_Engine(Messenger Address) {
	//        this.Address = Address;
	//    }
	
	
	// **********************************************************************************************************	 
	// ***************************  RECEIVER  FROM  M A I N _ U I  **********************************************
	//	...	===>
	
	class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SAY_HELLO:
                	Log.v("WAS HERE : ", "SECOND HANDLER GOT MSG..");
                	Address = msg.replyTo;
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
	
	
    
    
    
	void bindToConnector(){
    Intent C = new Intent(Polygraf1_Draw_Engine.this, Polygraf1_Connector.class);		
	bindService(C, mConnection, Context.BIND_AUTO_CREATE);	
	Log.v("WAS HERE : ", "bindToConnector");
	 	}
	
	 	 
	 	 
	 	 
	 	 
	// **********************************************************************************************************	 
	// *****************   RECEIVER  FROM      C O N N E C T O R  ***********************************************
	//		<=== ...
	
	 	 
	@SuppressLint("HandlerLeak")
	class engineIncomingHandler extends Handler {								
        @Override
        public void handleMessage(Message msg) {
      
        	//Log.v("FROM <=== dat[] = ",  String.valueOf(Polygraf1_Draw_Engine.dat[0]));
           
        	
        	myB = msg.getData();
        	RAW_DATA = myB.getIntArray("raw_data");
            refIteration++;  
      //  	Log.v("FROM HANDLER   RAW_DATA = ",  String.valueOf(RAW_DATA[3]));
        	        	
            sendMessageToUI(RAW_DATA);
         
           
        }
    }
	
	
	
 	
	// ****************************************************************************************************** 
	// *******************  ServiceConnection *************************************************************** 
	//	...	===>
	
	private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            dService = new Messenger(service);				// USED AS FIRST TOUCH SEEDER ...FOR CONNECTOR !!!						 			 
          														 
            try {
                Message msg = Message.obtain(null, Polygraf1_Connector.MSG_REGISTER_CLIENT);
                
                msg.replyTo = mMessenger;
                
                dService.send(msg);           				// REFERENCE TO  MESSAGE HANDLER WAS SENT TO CONNECTOR !!!    
                mBound = true;
            } catch (RemoteException e) {														
                
            }
        }
        
        public void onServiceDisconnected(ComponentName className) {
           
            dService = null;            
            mBound = false;
        }
        
	};
	
	
	
	
	// ****************************************************************************************************** 
	// *******************  SENDING MESSAGESS   TO    M A I N   U_I  **************************************** 
	//	  ... <===
	
	public void sendMessageToUI(int DATA[])  {					 
       		
           Message msg = Message.obtain();
           
           
            float[] float_DATA; 
         //  final float[] FLOAT_DATA = new float[5];
                        
        //   for (int i =0; i < 5; i++)
        //   {
        //   	FLOAT_DATA[i] = DATA[i];
        //   }
           
            float_DATA = count(DATA);
            
       //     Log.v("FROM sendMessageToUI  = ",  String.valueOf(DATA[2]));
          
            myB.putFloatArray("sensor_data", float_DATA);
           
            msg.setData(myB);
            
            	try {
            		if (Address != null)
            		Address.send(msg);
				} catch (RemoteException e) {
					
					Log.v("FROM sendMessageToUI ADDRESS e exception  = ",  "koko");
					e.printStackTrace();
				} 	
            	
    }    
	
	
	
	 
		
		@Override	
		public void onCreate() {
		super.onCreate();
		
			 
		 Log.v("TEST OnCreate Engine STARTED : ", String.valueOf(fromMessenger));
		 
		 bindToConnector();
		 
		 new Thread(new Polygraf1_Draw_Engine()).start();
		}
		
	
		
	
	
		@Override
		public void run() {						
		 android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		 Thread.currentThread();
		 
		// Looper.prepare(); 
		// Looper.loop();	 
     
		 
		 
		 countClock.scheduleAtFixedRate(new TimerTask(){ public void run() {

			
			 
			 Polygraf1_Draw_Engine.refIteration++;
			 
		 
		 }}, 0L, 5L);
		 		 
		 /*
		 
		// Log.v("FROM RUNNNN dat[] = ",  String.valueOf(Polygraf1_Draw_Engine.dat[0]));  
		    
		 
		 
		      for(int i=0; i < 200; i++){
	            Log.v("Engine work Simulation", String.valueOf(i));
	           
	            Log.v("from Connector : ", String.valueOf(fromConnector));
	            
	            
	            Polygraf1_Draw_Engine.dat[0]--;
	            
	            try {
	                Thread.sleep(100);
	            } catch (InterruptedException e) {
	              
	                e.printStackTrace();
	            }
	        }    
				           
		     */  		
		}
		
		public int refBefore,refBefore2;
		public int refCount,i,in = 0;
		public int[] tickVals = new int[100];
		
		public int[] peaks = new int[6];
		public int time,storedTime,timeResult;
		
		
		
		
		public float[] count(int[] rawDATA)
		{					
		
		int refAfter = rawDATA[4];		
		
		if (refBefore < refAfter)
		{
		
		peaks[i] = rawDATA[4];
		
			if(i >= 2 && peaks[i] < peaks[i-1] && peaks[i] < peaks[i-2])
			{
				
			
			storedTime = time;	
					
			timeResult = Polygraf1_Draw_Engine.refIteration - storedTime;
			time = Polygraf1_Draw_Engine.refIteration;	
			Polygraf1_Draw_Engine.refIteration = i=0;
			
			}
			
		i++;		
		}else{i=0;}
		if (i==6) i = 0;
		refBefore = refAfter;
		
		
		
		
		/*
		if(refBefore < refAfter) 
		{ 
		   refCount++;  
		   if(refCount > 2)
		   { 							// RISING EDGE FOUND
			tickVals[i] = Polygraf1_Draw_Engine.refIteration; 
			refCount = 0;
		//	refBefore = 0;
			
				switch (i) {
				case 1 : rawDATA[0] = 111;break;
				case 3 : rawDATA[0] = 222;break;
				case 5 : {rawDATA[0] = 333;i=0;Polygraf1_Draw_Engine.refIteration =0;}break;
			//	case 6 : i=0;Polygraf1_Draw_Engine.refIteration =0;break;
			//	default : rawDATA[0] = 111;break;
				}
			i++;
			
		   };
		   
		}else {refCount=0;rawDATA[0] = 888;};
		
		in++;		
		refBefore = refAfter;				
		*/		
		
		rawDATA[0] = timeResult;
		
		float[] RESULT = new float[5];
				
		for (int g =0; g<5; g++){			
			RESULT[g] = rawDATA[g];	
					}	  
			
		return RESULT;
		}
		
		
		

		
	    protected void onStop() {
	        
	        
	        if (mBound) {
	            unbindService(mConnection);
	            mBound = false;
	        }
	    }
			
		
	    public void onDestroy() {
	        super.onDestroy();
	        
	        if (mBound) {
	            unbindService(mConnection);
	            mBound = false;
	            android.os.Process.killProcess(android.os.Process.myPid());
	        }
	    }
	
	
	
	
	
}
	



