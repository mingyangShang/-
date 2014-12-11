package com.draw.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.draw.R;
import com.levelup.draw.gif.GifView;

public class WelcomeActivity extends Activity{

	Timer timer = new Timer();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 //取消标题 
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		//全屏 
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
		                  WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.activity_welcome);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		GifView gifView = (GifView) findViewById(R.id.welcome_bg);
		gifView.setGifImage(R.drawable.welcome);
		gifView.setVisibility(View.VISIBLE);
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				startActivity(new Intent(WelcomeActivity.this,FirstActivity.class));
				WelcomeActivity.this.finish();
			}
		}, (long) (1000*2.6));
	}

	@Override
	protected void onPause(){
		super.onPause();
		if(timer!=null){
			timer.cancel();
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(timer!=null){
			timer.cancel();
		}
		//释放资源
		super.onDestroy();
	}
	
	
	
	

	
	
}
