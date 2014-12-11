package com.levelup.draw.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;

public class MyPath implements Parcelable{

	/**
	 * 
	 */
	private Rect re = null;
	private int paintColor = 0;
	private int paintWidth = 0;
	private List<Point> pointArray = null;
	
	public MyPath(Parcel parcel){
		paintColor = parcel.readInt();
		paintWidth = parcel.readInt();
		re = parcel.readParcelable(Rect.class.getClassLoader());
		Parcelable[] points = parcel.readParcelableArray(Point.class.getClassLoader());
		pointArray = Arrays.asList(Arrays.asList(points).toArray(new Point[points.length]));
	}
	
	public MyPath()
	{
		pointArray = new ArrayList<Point>();
	}
	
	public void addPoint(Point p)
	{
		pointArray.add(p);
	}
	
	public void setRect(Rect re2)
	{
		re = new Rect(re2);
	}
	
	public void setPaintColorWidth(int color,int width)
	{
		paintColor = color;
		paintWidth = width;
	}
	
	public Rect getRect()
	{
		return re;
	}
	
	public int getPaintColor()
	{
		return paintColor;
	}
	
	public int getPaintWidth()
	{
		return paintWidth;
	}
	
	public List<Point> getPointArray()
	{
		return pointArray;
	}
	
	public void showPoints()
	{
		System.out.println("--------------------------");
		for(Point p:pointArray)
		{
			System.out.println(p);
		}
		System.out.println("--------------------------");
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		parcel.writeInt(paintColor);
		parcel.writeInt(paintWidth);
		parcel.writeParcelable(re, arg1);
		parcel.writeParcelableArray(pointArray.toArray(new Point[pointArray.size()]), arg1);
	}
	
	public static final Parcelable.Creator<MyPath> CREATOR = new Parcelable.Creator<MyPath>() {

		@Override
		public MyPath createFromParcel(Parcel arg0) {
			// TODO Auto-generated method stub
			return new MyPath(arg0);
		}

		@Override
		public MyPath[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return new MyPath[arg0];
		}
		
	};
}
