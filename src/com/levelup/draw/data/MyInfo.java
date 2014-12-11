package com.levelup.draw.data;


public class MyInfo {
	private String name;
	private String ip;
	private int point;
	private int screenWidth,screenHeight;
	
	public MyInfo(int width,int height)
	{
		name = new String("");
		ip = new String("");
		point = 0;
		screenWidth = width;
		screenHeight = height;
	}
	
	public MyInfo(String ip2,String name2,int width,int height)
	{
		name = name2;
		ip = ip2;
		screenWidth = width;
		screenHeight = height;
		point = 0;
	}
	
	public void setPoint(int p)
	{
		point = p;
	}
	public String getName()
	{
		return name;
	}
	public String getIP()
	{
		return ip;
	}
	public int getPoint()
	{
		return point;
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
}
