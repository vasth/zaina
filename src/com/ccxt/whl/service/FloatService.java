package com.ccxt.whl.service;

 
import com.ccxt.whl.widget.FloatingView;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FloatService extends Service {  
    //服务  
	//为了在activity点击button后 在开启一个service   
      
    @Override  
    public IBinder onBind(Intent intent) {  
        return null;  
    }  
  
    public void onCreate() {  
    	super.onCreate();  
       
    }  
  
    @Override  
    public int onStartCommand(Intent intent, int flags, int startId) {  
    	//创建service时一个 实例化一个FloatingView对象并且调用他的showFloatingBtn()方法把它注册到windowManager上  
        new FloatingView(this).showFloatingBtn();  
        return super.onStartCommand(intent, flags, startId);  
    }  
}