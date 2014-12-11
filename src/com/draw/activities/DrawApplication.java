package com.draw.activities;

import android.app.Application;

public class DrawApplication extends Application{

	private static final DrawApplication instance = new DrawApplication();
	private String myIp;
	private String username;
	private int screenWidth,screenHeight;
	public boolean volumeIsOn;
	private boolean isWifiOpen;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	/**
     * 此方法方便在那些没有context对象的类中使用
     * @return MyApp实例
     */
	
    public static DrawApplication getApplicationInstance()
    {
        return instance;
    }
	public String getMyIp() {
		return myIp;
	}
	public void setMyIp(String myIp) {
		this.myIp = myIp;
	}
	
	public boolean getVolume() {
		return volumeIsOn;
	}
	public void setVolume(boolean i) {
		this.volumeIsOn = i;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getScreenWidth() {
		return screenWidth;
	}
	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}
	public int getScreenHeight() {
		return screenHeight;
	}
	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public boolean isWifiOpen() {
		return isWifiOpen;
	}

	public void setWifiOpen(boolean isWifiOpen) {
		this.isWifiOpen = isWifiOpen;
	}
	
}
