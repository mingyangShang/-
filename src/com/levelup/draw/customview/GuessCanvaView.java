package com.levelup.draw.customview;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.levelup.draw.data.MyPath;
import com.levelup.draw.utils.DisplayUtil;

public class GuessCanvaView extends View {

	public ArrayList<MyPath> myPathList  = new ArrayList<MyPath>();
	
	public Bitmap  surfaceBitmap;
	private Canvas surfaceCanvas;
	private int screenWidth;
	private int screenHeight;
	
	private int otherScreenWidth;
	private int otherScreenHeight;
	
	private DrawFree draw = null;
	
	private float scale = 1.5f;
	
	public GuessCanvaView(Context context,int width,int height) {
		super(context);
		// TODO Auto-generated constructor stub
		otherScreenWidth = width;
		otherScreenHeight = height;
		screenWidth = DisplayUtil.getScreenWidth(context);
		screenHeight = DisplayUtil.getScreenHeight(context);
		
		surfaceBitmap = Bitmap.createBitmap((int)(otherScreenWidth*scale), (int)(otherScreenHeight*scale), Bitmap.Config.ARGB_8888);
		surfaceCanvas = new Canvas(surfaceBitmap);
		surfaceCanvas.drawColor(Color.TRANSPARENT);
		invalidate();
	}

	public void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		for(MyPath path:myPathList)
		{
			if(path.getPaintWidth() == -1)
			{
				draw  = new DrawFree(1);
			}
			else
			{
				draw = new DrawFree();
				draw.setPaintColor(path.getPaintColor());
				draw.setPaintWidth(path.getPaintWidth());
			}
			
			List<Point>  pointArray = path.getPointArray();
			Rect re = path.getRect();
			draw.onTouchDown(pointArray.get(0), re);
			
			for(int i = 1 ; i<pointArray.size() ;i++ )
			{
				draw.onTouchMove(pointArray.get(i), re);
				draw.onDraw(surfaceCanvas);
				
			}
		}
		canvas.drawBitmap(surfaceBitmap,new Rect(0, 0,(int)( otherScreenWidth*scale), 
				(int)(otherScreenHeight*scale) ), new RectF(0,0,screenWidth,screenHeight-55), null);
		
	}
	
	public void addPath(MyPath p)
	{
		myPathList.add(p);
		invalidate();
	}
}