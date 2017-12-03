package com.example.polygraf1;




import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import com.example.polygraf1.R.id;

import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;



public class Polygraf1_Main_Menu extends Activity implements Renderer {
	
	public Polygraf1_Draw_Engine mThread;
	final Messenger xMessenger = new Messenger(new IncomingHandler());	
	
	private GLSurfaceView glSurfaceView;
	
	Messenger mService = null;
	
	TextView textStatus, textIntValue_1, textIntValue_2, textIntValue_3, textIntValue_4, textIntValue_5;
	
	ImageView heart;
	int hr,pdSz = 0;
	
	
	public LinearLayout footer;
    public static String[] questions = {
    	"Voláte sa .... ?",
    	"Bývate na .... ulici ?",
    	"Máte .... rokov ?",
    	"Ste vydatá ?",
    	"Pracujete v spoloènosti .... ?",
    	"Poznáte pána .... ?",
    	"Boli ste dòa .... v .... ?",
    	"Stretli ste sa dòa .... s .... ktorý je vašim kolegom ?",
    	"Došlo dòa .... k intímnemu zblíženiu s pánom ....",
    	"Hovoríte teraz pravdu ?"};
	
	Bundle myB = new Bundle();
	private final String TAG = "M A I N ";
	
	public static int fromPROCESSOR = 0;	
	static final int MSG_SAY_HELLO = 1;
	
	boolean mBound;
			
	public static float SENSOR_DATA[] = new float[5];
	
	
	// ****************************************************************************************************
	// ****************   MAIN_UI   R E C E I V E R  ******************************************************
	//		<===
	
	@SuppressLint("HandlerLeak")
	class IncomingHandler extends Handler {					// Messenger object xMessanger is reference to		
	        @Override										// Handler IncomingHandler - 
	        
	        public void handleMessage(Message msg) {		//	here are received incoming messages from PROCESSOR
	        													    
	        			        													 
	        	 myB = msg.getData();
	        	 SENSOR_DATA = myB.getFloatArray("sensor_data");
	        	
	        	 textIntValue_1.setText(String.valueOf(SENSOR_DATA[0]));
	        	 textIntValue_2.setText(String.valueOf(SENSOR_DATA[1]=pdSz));
	        	 textIntValue_3.setText(String.valueOf(SENSOR_DATA[2]=hr));
	        	 textIntValue_4.setText(String.valueOf(SENSOR_DATA[3]));
	        	 textIntValue_5.setText(String.valueOf((SENSOR_DATA[4] * 0.06) * 0.1));
	        	 
	        	 
	        	 
	        	
	       // 	 fromPROCESSOR = SENSOR_DATA[1];
	       // 	 Log.i(TAG, " received " + fromPROCESSOR);
	        glSurfaceView.requestRender();	
	        
	        heart.setPadding(pdSz, pdSz, pdSz, pdSz);
	       
	        if(hr < 100){
	        //heartBeat();
	        pdSz=6;
	        heart.invalidate();
	        }
	        else { pdSz = 1;heart.invalidate();};
	        if(hr > 120) hr=0;
	        hr++;
	        }
	    }
	

	
	 	
	// ****************************************************************************************************** 
	// *******************  ServiceConnection *************************************************************** 
	//		===> 
	
	private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);										 	//		new Messenger object is created 
         																				//      and provided for Service side 
            try {
                Message msg = Message.obtain(null, Polygraf1_Main_Menu.MSG_SAY_HELLO, 0, 0);
                msg.replyTo = xMessenger;												//		IN ORDER TO RECEIVE MESSAGES 
                																		//		FROM   P R O C E S S O R
                mService.send(msg);														//	    WE NEED TO SET REFEENCE TO	
                mBound = true;															//		xMessanger (receiver)
            } catch (RemoteException e) {												// 		
                
            }
        }
        
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            mBound = false;  
        }
        
	};
		
	//********************************************************************************************************
	
	
        
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_menu);
		
		glSurfaceView = (GLSurfaceView) findViewById(R.id.cubes); 
		glSurfaceView.setRenderer(new Polygraf1_Main_Menu());
		glSurfaceView.setRenderMode(glSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		
						
		// **********************************************************************************************************
		// **************************  NEW THREAD C O N N E C T O R STARTED  ****************************************
		
		Polygraf1_Connector.connectorThread = new Thread() {
			public void run() {
				Intent DevConnection = new Intent(getApplicationContext(), Polygraf1_Connector.class);
				startService(DevConnection);
								Polygraf1_Connector.context = getApplicationContext();
							}
			};		
		Polygraf1_Connector.connectorThread.start();
					
		
		
		// **********************************************************************************************************
		// *************************************  Bind to E N G I N E  **********************************************	
		
		new Handler().postDelayed(new Runnable(){
			@Override
			public void run(){
			Intent X = new Intent(Polygraf1_Main_Menu.this, Polygraf1_Draw_Engine.class);		
			bindService(X, mConnection, Context.BIND_AUTO_CREATE);
			Log.i(TAG, "Binded to PROCESSOR ");
			}
		}, 2000);		
		
			
		textIntValue_1 = (TextView)findViewById(R.id.textIntValue_1);
		textIntValue_2 = (TextView)findViewById(R.id.textIntValue_2);
		textIntValue_3 = (TextView)findViewById(R.id.textIntValue_3);
		textIntValue_4 = (TextView)findViewById(R.id.textIntValue_4);
		textIntValue_5 = (TextView)findViewById(R.id.textIntValue_5);
				
		heart = (ImageView) findViewById(R.id.heart);
		
		inflate_Bottom_menu();			
		
	}
	
	public TextView question;
	public int s = 0; 
	
	public void inflate_Bottom_menu(){
				
		footer = (LinearLayout) LayoutInflater.from(Polygraf1_Main_Menu.this.getApplication()).inflate(R.layout.bottom_menu, (ViewGroup) findViewById(R.id.footer), true);
			
		ImageButton setting = (ImageButton) footer.findViewById(R.id.btn_setting);
		setting.setOnClickListener(new Button.OnClickListener() {  
			        public void onClick(View v)
		            {
			        	footer.removeAllViews();
			        	inflate_Settings();
		            }
		     });		
		
		ImageButton start = (ImageButton) footer.findViewById(R.id.btn_start);
		start.setOnClickListener(new Button.OnClickListener() {  
			        public void onClick(View v)
		            {  
			        
			        	Timer delay = new Timer();
			        	
			        	
			        
			        	      		   			        	
			        	      
			        	delay.scheduleAtFixedRate(new TimerTask(){ public void run() {
			        	
			        		questions_RUN();
			    						   			 			   						   			 			   		 
			   		 }}, 0L, 5000L);        	        	 			            			         
			            			            
			        	
			           	
			           
			        
			        };  
		     });				
	};
	
	
	public void questions_RUN(){
		
		runOnUiThread(new Runnable() {
   	     @Override
   	     public void run() {
		
		if(s < 10) {
		footer.removeAllViews();   		
    	footer = (LinearLayout) LayoutInflater.from(Polygraf1_Main_Menu.this.getApplication()).inflate(R.layout.questions, (ViewGroup) findViewById(R.id.footer), true);   		
    	question = (TextView) findViewById(R.id.otazkyRUN);
    	question.setText(questions[s]);
    	footer = (LinearLayout) LayoutInflater.from(Polygraf1_Main_Menu.this.getApplication()).inflate(R.layout.questions, (ViewGroup) findViewById(R.id.footer), true);   		
 		   s++; 	
		}else {footer.removeAllViews(); }
 		   
    }
    	});
 		   
	}
	
	
	int q = 0;
	
public void inflate_Settings(){
		
	//	final int q = 0;
		footer = (LinearLayout) LayoutInflater.from(Polygraf1_Main_Menu.this.getApplication()).inflate(R.layout.settings, (ViewGroup) findViewById(R.id.footer), true);
		
		ImageButton back = (ImageButton) footer.findViewById(R.id.btn_back);
		back.setOnClickListener(new Button.OnClickListener() {  
	        public void onClick(View v)
            {
	           	footer.removeAllViews();
	        	inflate_Bottom_menu();
	        	
            }
         });
		
		final EditText question = (EditText) footer.findViewById(R.id.editText1);
		
		Button next = (Button) footer.findViewById(R.id.btn_next);
			
		next.setOnClickListener(new Button.OnClickListener() {  
	        public void onClick(View v)
            {
	        	if(q<=9){
	           	questions[q] = question.getText().toString();
	          // 	question.setText("otazka : " + (q+1));
	           	question.setText(questions[q]);
	           	q++;}
            }
         });
		
	};
	
	
	
	 	@Override
	    protected void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	     
	        outState.putString("textIntValue", textIntValue_1.getText().toString());
	        outState.putString("textIntValue2", textIntValue_2.getText().toString());
	        outState.putString("textIntValue3", textIntValue_3.getText().toString());
	        outState.putString("textIntValue4", textIntValue_4.getText().toString());
	        outState.putString("textIntValue5", textIntValue_5.getText().toString());

	        outState.putInt("heart", heart.getPaddingBottom());
	      
	        
	      // 	outState.putInt("heart", heart.getLeft());       
	      //pdSz = (hr < 5) ? 4 : 2;
	      //  heart.setPadding(pdSz, pdSz, pdSz, pdSz);
	      //  if(hr > 10)hr=0;
	        
	        
	        
	    }
		   
		
	
	@Override
    protected void onStop() {
        super.onStop();
        
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }
		
	@Override
    protected void onDestroy() {
        super.onDestroy();
        
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }


//******************		DRAWING		**************************************
//******************					**************************************
	
		
	@Override
	public void onDrawFrame(GL10 gl) {
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glTranslatef(0.0f, 0.0f, -5.0f); 	//move 5 units INTO the screen same as moving the camera 5 units away
		
	//	try {
	//		Thread.sleep(15);
	//	} catch (InterruptedException e) {
						
	//	}
		draw(gl); 
	}



	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		if(height == 0) {height = 1; }                      	
			                           			
			gl.glViewport(0, 0, width, height);
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity(); 
								
			GLU.gluPerspective(gl, 85.0f, (float)width / (float)height, 0.1f, 100.0f); //GLU.gluPerspective(gl, 45.0f, 1.5f, 0.1f, 100.0f)
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
	}



	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		
	}
	
	
	
	//**************************************************************************************************
	//***************************** DRAW TRIANGLE ******************************************************
	
	
	private FloatBuffer vertexBuffer;	// buffer holding the vertices
	
	private float incCur = 0.0f;
	private float incCur2 = 0.0f;
	private float incCur3 = 0.0f;
	
	private int increment = 18;
	private int drawElem = 6;	

	private float initX = -5.0f;
	private float initY[] = {3.5f, -1.0f, -3.5f};
	public float hotVal;
	private float motionStep = 0.05f;
	
	private float []BUFF = new float[3600];		
	
	
	
	
	public void draw(GL10 gl) {
		ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(BUFF.length * 4);
		vertexByteBuffer.order(ByteOrder.nativeOrder());	
		
		vertexBuffer = vertexByteBuffer.asFloatBuffer();
		vertexBuffer.put(BUFF);
		vertexBuffer.position(0);
		
		Random rand = new Random();
		float minX = -0.3f;
		float maxX = 0.3f;
		float randNum = rand.nextFloat() * (maxX - minX) + minX;
					
		float hotS = ((SENSOR_DATA[4] * 0.05f) * 0.05f);  // changed to 0.1 !!!
	//	float hotS = SENSOR_DATA[4];
		
		
	//	int hotS = SENSOR_DATA[3];
				
				for (int i=increment-18; i<increment; i+=18){
	//**********************	TOP WAVE	******************************************				
				    if (BUFF[3] == 0.0f)  {BUFF[3] = BUFF[9] = BUFF[15] = initX;}
				    
					BUFF[i+3] = (i >= 6)? BUFF[i+3-18] += motionStep : BUFF[i+3] + motionStep;
					
					BUFF[i+4] = initY[0] + randNum; 
					
					BUFF[i+0] = (i >= 6)? BUFF[i+3-18] : BUFF[i+3];
					BUFF[i+1] = (incCur != 0)? incCur : BUFF[i+4];
					incCur = BUFF[i+4];
	//**********************	MIDDLE WAVE	  *****************************************
									
					BUFF[i+9] = (i >= 6)? BUFF[i+9-18] += motionStep : BUFF[i+9] + motionStep;
					BUFF[i+10] = initY[1] + hotS;
					
					BUFF[i+6] = (i >= 6)? BUFF[i+9-18] : BUFF[i+9];
					BUFF[i+7] = (incCur2 != 0)? incCur2 : BUFF[i+10];
					incCur2 = BUFF[i+10];
	//**********************	BOTTOM WAVE   *****************************************				
									
					BUFF[i+15] = (i >= 6)? BUFF[i+15-18] += motionStep : BUFF[i+15] + motionStep;
					BUFF[i+16] = initY[2] - randNum;
					
					BUFF[i+12] = (i >=6)? BUFF[i+15-18] : BUFF[i+15];
					BUFF[i+13] = (incCur3 != 0)? incCur3 : BUFF[i+16];
					incCur3 = BUFF[i+16];
				}
						
				 		
		gl.glDepthMask(false);
	    gl.glEnable(GL10.GL_LINE_SMOOTH);								//improoving graphic
	    gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);			//improoving graphic
	    gl.glEnable(GL10.GL_BLEND);										//improoving graphic
	    gl.glBlendFunc(GL10.GL_ALIASED_POINT_SIZE_RANGE, GL10.GL_ONE);	//improoving graphic
	    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glColor4f(0.0f, 1.0f, 0.0f, 0.5f);
		
		gl.glLineWidth(3.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer); // size: coordinates per vertex
		gl.glDepthMask(true);
		gl.glDrawArrays(GL10.GL_LINES, 0, drawElem);
		
		drawAxis(gl);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);		
				
		if(increment/18 == 200){
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		increment = 0;
		drawElem = 0;	
		BUFF[3] = BUFF[9] = BUFF[15] = initX;
		}
		drawElem+=6;
		increment+=18;
}
		
	

	public void drawAxis(GL10 gl){
		
		final float vertices2[] = {
				-5.5f, 0.0f,  0.0f,							// V1 - first vertex (x,y,z)  1.2
				 5.0f, 0.0f,  0.0f,							// V2 - second vertex		  1.3								
				 0.0f,-5.0f,  0.0f,							// V1 - first vertex (x,y,z)  1.2
				 0.0f, 5.0f,  0.0f,
		};
		
		FloatBuffer vertexBuffer2;
		
		ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices2.length * 4);
		vertexByteBuffer.order(ByteOrder.nativeOrder());
		
		vertexBuffer2 = vertexByteBuffer.asFloatBuffer();
		vertexBuffer2.put(vertices2);
		vertexBuffer2.position(0);
			
		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.5f);
		gl.glLineWidth(1.0f);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer2);
		gl.glDrawArrays(GL10.GL_LINES, 0, 4);  
	}	
	
	
	
	public void heartBeat( ) {
				
		pdSz = (hr < 50) ? 4 : 1;
     //   heart.setPadding(pdSz, pdSz, pdSz, pdSz);
        if(hr > 50)hr=0;
	
	}
	
	
	
	
	 @Override
     protected void onResume() {
      super.onResume();
       glSurfaceView.onResume();
	     }


	 @Override	  
     protected void onPause() {
          super.onPause();
          glSurfaceView.onPause();
 	      }
	
	 	
	
}
	


