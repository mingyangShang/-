package com.levelup.draw.customview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Paint.Style;
import android.graphics.Rect;

/*
 * 主view类。
 * 该类类似于一个接口，子类都继承实现了主类的这些方法
 */
public class DrawFree {
	
	public Paint paint;//声明画笔
	private Path path = new Path();
	private float mX, mY;
	
	public DrawFree()
	{
		// 设置画笔
		paint = new Paint();
		paint.setStyle(Style.STROKE);// 设置非填充
		paint.setStrokeWidth(8);// 笔宽10像素
		paint.setColor(Color.BLACK);
		paint.setAntiAlias(true);// 锯齿不显示
		
	}
	
	//如果选择橡皮，则需要给画笔重新赋值
	public DrawFree(int i) 
	{

		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setColor(Color.BLUE);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.SQUARE);
		paint.setStrokeWidth(70);
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

	}
	
	public void onTouchUp(Point point,Rect re) {
		
	}
	
	public void onTouchDown(Point point,Rect re) {
		point.x += re.left;
		point.y += re.top;
		path.moveTo(point.x, point.y);
		mX = point.x;
		mY = point.y;
	}

	public void onTouchMove(Point point,Rect re) {
		point.x += re.left;
		point.y += re.top;
		
		float dx = Math.abs(point.x - mX);
		float dy = Math.abs(point.y - mY);
		if (dx > 0 || dy > 0) {
			path.quadTo(mX, mY, (point.x + mX) / 2, (point.y + mY) / 2);
			mX = point.x;
			mY = point.y;
		} else if (dx == 0 || dy == 0) {
			path.quadTo(mX, mY, (point.x + 1 + mX) / 2, (point.y + 1 + mY) / 2);
			mX = point.x + 1;
			mY = point.y + 1;
		}
	}

	
	public void onDraw(Canvas canvas) {
		canvas.drawPath(path, paint);
	}

	public int getPaintColor()
	{
		return paint.getColor();
	}
	
	public int getPaintWidth()  //5,8,15,20
	{
		switch(  (int)paint.getStrokeWidth() )
		{
		case 5:
			return 1;
		case 8:
			return 2;
		case 13:
			return 3;
		case 18:
			return 4;
		default:
			return -1;
		}
	}
	
	public void setPaintColor(int color)
	{
		paint.setColor(color);
	}
	
	public void setPaintWidth(int width)
	{
		switch(  width )
		{
		case 1:
			paint.setStrokeWidth(5);
			break;
		case 2:
			paint.setStrokeWidth(8);
			break;
		case 3:
			paint.setStrokeWidth(13);
			break;
		case 4:
			paint.setStrokeWidth(18);
			break;
		default:
			break;
		}
		
	}
}