package com.example.polygraf1;

import android.app.Application;

public class Polygraf1_Data_Storage extends Application  {

	private int dataMess = 33;
	
	
	public int getdataMess(){
		return dataMess;		
	}
	
	//public void setVariable(int dataMess) {
   //     this.dataMess = dataMess;
   // }
	 
	public void setdataMess(int val){
		dataMess = val;		
	}
	
}
