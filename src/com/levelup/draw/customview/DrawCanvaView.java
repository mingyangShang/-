package com.levelup.draw.customview;


import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.levelup.draw.data.MyPath;
import com.levelup.draw.tranfer.TranferService;


public class DrawCanvaView extends View{

	private DrawFree draw = null;
	private Point evevtPoint;
	public Bitmap  surfaceBitmap;
	public Canvas surfaceCanvas;
	private int screenWidth;
	private int screenHeight;
	
	private boolean isMove = false;
	private RectF rf = null;
	public Rect re = null;
	
	private Point moveStartPoint = new Point();
	boolean isFullSee = false;
	
	private int paintColor = 0xFF000000;
	private int paintWidth = 2;
	
	public ArrayList<MyPath> myPathList = new ArrayList<MyPath>();
	private MyPath myPath = null;
	
	
	
	private float scale =  1.5f;
	
	public DrawCanvaView(Context context,WindowManager wm) {
		super(context);
		//System.out.println("myView初始化");
		// 初始化drawBS，即drawBS默认为DrawPath类
		draw = new DrawFree();
		myPath = new MyPath();
		evevtPoint = new Point();
		
		screenWidth = wm.getDefaultDisplay().getWidth();
		screenHeight = wm.getDefaultDisplay().getHeight();
		
		rf = new RectF(0, 0, screenWidth, screenHeight-55);
		re = new Rect(0, 0, screenWidth, screenHeight);
		
		//System.out.println(screenWidth+"   "+screenHeight);
		myPath.setRect(new Rect(re));
		myPath.setPaintColorWidth(draw.getPaintColor(), draw.getPaintWidth());
		
		surfaceBitmap = Bitmap.createBitmap((int)(screenWidth*scale), (int)(screenHeight*scale), Bitmap.Config.ARGB_8888);
		surfaceCanvas = new Canvas(surfaceBitmap);
		surfaceCanvas.drawColor(Color.TRANSPARENT);
		

	}

	public void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		if(isFullSee)
		{
			canvas.drawBitmap(surfaceBitmap,new Rect(0, 0,(int)( screenWidth*scale), 
								(int)(screenHeight*scale) ), rf, null);
			
		}
		else
		{
			draw.onDraw(surfaceCanvas);
			canvas.drawBitmap(surfaceBitmap, re, rf, null);
		}

	}

	// 触摸事件。调用相应的画图工具类进行操作
	 
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		evevtPoint.set((int) event.getX(), (int) event.getY());
		//System.out.println("myview OntouchEvent");
		
		if(isFullSee)
		{
			if( event.getAction() == MotionEvent.ACTION_UP )
			{
				isFullSee  = !isFullSee;
				invalidate();
			}
			return true;
		}

		
		if( !isMove )
		{
			//System.out.println(evevtPoint);
			myPath.addPoint(new Point(evevtPoint.x,evevtPoint.y));
			
			//myPath.showPoints();
			
			switch (event.getAction()) 
			{
			case MotionEvent.ACTION_DOWN:
				draw.onTouchDown(evevtPoint,re);
				break;

			case MotionEvent.ACTION_MOVE:
				draw.onTouchMove(evevtPoint,re);
				invalidate();
				break;

			case MotionEvent.ACTION_UP:
				myPathList.add(myPath);
				//*********************************************
//				drawActivity.sendMyPath(myPath);
				TranferService.player.sendPath(myPath);
				
				//将上面这句话改成使用service传输数据，从而将activity释放出来
				//*********************************************
				
				myPath = new MyPath();
				myPath.setRect(new Rect(re));
				myPath.setPaintColorWidth(draw.getPaintColor(), draw.getPaintWidth());
				break;
			default:
				break;
			}
		}
		else
		{
			switch (event.getAction()) 
			{
			case MotionEvent.ACTION_DOWN:
				moveStartPoint.x = evevtPoint.x;
				moveStartPoint.y = evevtPoint.y;
				break;

			case MotionEvent.ACTION_MOVE:
				TouchMove(evevtPoint);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				invalidate();
				break;
			default:
				break;
			}
		}
		return true;
	}

	
	private void TouchMove(Point evevtPoint2) 
	{
		// TODO Auto-generated method stub
		int dx = moveStartPoint.x-evevtPoint2.x;
		int dy = moveStartPoint.y-evevtPoint2.y;
		
		if( ( re.left+dx ) < 0 || (re.right+dx) > screenWidth*scale )
		{
			if( ( re.top+dy ) < 0 || (re.bottom+dy) > screenHeight*scale )
			{
				return;
			}
			else
			{
				re.offset(0, dy);
				moveStartPoint.set(evevtPoint2.x, evevtPoint2.y);
				return;
			}
		}
		else 
		{
			if( ( re.top+dy ) < 0 || (re.bottom+dy) > screenHeight*scale )
			{
				re.offset(dx, 0);
				moveStartPoint.set(evevtPoint2.x, evevtPoint2.y);
				return;
			}
		}
		re.offset(dx, dy);
		myPath.setRect(new Rect(re));
		moveStartPoint.set(evevtPoint2.x, evevtPoint2.y);
	}

	public void setToTool(String s) 
	{
		// 如果需要橡皮。则实例化重新设置画笔的构造方法
		if( s.equals("paint") )
		{
			isFullSee = false;
			isMove = false;
			draw = new DrawFree();
			draw.setPaintWidth(paintWidth);
			draw.setPaintColor(paintColor);
			myPath = new MyPath();
			myPath.setRect(new Rect(re));
			myPath.setPaintColorWidth(draw.getPaintColor(), draw.getPaintWidth());
		}
		else if( s.equals("eraser") )
		{	
			isMove = false;
			isFullSee = false;
			if( draw.getPaintWidth() != -1 )
			{
				paintColor = draw.getPaintColor();
				paintWidth = draw.getPaintWidth();
			}
			draw = new DrawFree(1);// 橡皮
			myPath = new MyPath();
			myPath.setRect(new Rect(re));
			myPath.setPaintColorWidth(draw.getPaintColor(), draw.getPaintWidth());
			
		}
		else if(s.equals("move"))
		{
			isMove = true;
		}
		else if(s.equals("fullSee") )
		{
			isFullSee = true;
			invalidate();
		}
	}

	public int getPaintColor()
	{
		return paintColor;
	}
	
	public int getPaintWidth()
	{
		return paintWidth;
	}
	
	public void setPaintColor(int color)
	{
		paintColor = color;
		draw.setPaintColor(paintColor);
	}
	
	public void setPaintWidth(int width)
	{
		paintWidth = width;
		draw.setPaintWidth(paintWidth);
	}


}
