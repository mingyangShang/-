package com.levelup.draw.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

public class MyColorWidth extends View {

	private Paint paintBG = new Paint(Paint.ANTI_ALIAS_FLAG); 
	private Paint paintColorPoint = new Paint(Paint.ANTI_ALIAS_FLAG);   
	private Paint paintColorCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintWidthPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Paint paintWidthCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	private double zoom = 1.0;
	private int blockWidth = 100;
	private int colorPoinrR = 30;
	
	public int currentColor = 0xFF000000;
	public int currentWidth = 2;
	
	private int BG = 0xFFFFFFCC;
	private int black = 0xFF000000;
	private int gray =  0xFF999999;
	private int white = 0xFFFFFFFF;
	private int red =   0xFFFF0000;
	private int orange = 0xFFFFCC00;
	private int yellow = 0xFFFFFF00;
	private int green = 0xFF00FF00;
	private int lightBlue = 0xFF00CCFF;
	private int darkBlue = 0xFF0033FF;
	private int pink = 0xFFFF99CC;
	private int purple = 0xFF9900CC;
	private int brown = 0xFF996600;
	
	private int color[] = new int[]{black,gray,white,red,orange,yellow,green,lightBlue,darkBlue,pink,purple,brown};
	
	public MyColorWidth(Context context,int color,int width,double zoom) {
		super(context);
		// TODO Auto-generated constructor stub
		this.zoom = zoom;
		blockWidth = (int) (blockWidth*zoom);
		colorPoinrR = (int) (colorPoinrR*zoom);
		
		currentColor  = color;
		currentWidth = width;
		
		paintBG.setColor(BG);
		paintBG.setStrokeWidth((float) (32 * zoom));  
	
		paintColorPoint.setColor(black);  
		paintColorPoint.setStrokeWidth((float) (10 * zoom));
		
		paintColorCircle.setColor(black);
		paintColorCircle.setStyle(Paint.Style.STROKE);
		paintColorCircle.setStrokeWidth((float)(5*zoom));
		
		paintWidthPoint.setColor(black);
		paintWidthPoint.setStrokeWidth((float)(5*zoom));
		
		paintWidthCircle.setColor(black);
		paintWidthCircle.setStyle(Paint.Style.STROKE);
		paintWidthCircle.setStrokeWidth((float)(5*zoom));
		
	}
	
    @Override  
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)  
    {  
    	setMeasuredDimension(4*blockWidth, 4*blockWidth);
    }
	
    @Override  
    protected void onDraw(Canvas canvas)  
    {  
    	canvas.drawRect(new Rect(0,0,4*blockWidth,4*blockWidth),paintBG );
    	for(int i = 0; i < 3; i++ )
    	{
    		for(int j = 0; j < 4 ; j++)
    		{
    			paintColorPoint.setColor(color[i*4+j]);
    			canvas.drawCircle( blockWidth*(j+0.5f), blockWidth*(i+0.5f), colorPoinrR, paintColorPoint);
    			if( color[i*4+j] == currentColor )
    			{
    				paintColorCircle.setColor(currentColor);
    				canvas.drawCircle(blockWidth*(j+0.5f), blockWidth*(i+0.5f), colorPoinrR+5, paintColorCircle);
    			}
    		
    		}
    	}
    	
    	paintWidthPoint.setColor(currentColor);
    	paintWidthCircle.setColor(currentColor);
    	canvas.drawCircle(blockWidth*0.5f, blockWidth*3.5f, (float) (3*zoom), paintWidthPoint);
    	canvas.drawCircle(blockWidth*1.5f, blockWidth*3.5f, (float) (5*zoom), paintWidthPoint);
    	canvas.drawCircle(blockWidth*2.5f, blockWidth*3.5f, (float) (8*zoom), paintWidthPoint);
    	canvas.drawCircle(blockWidth*3.5f, blockWidth*3.5f, (float) (12*zoom), paintWidthPoint);
    	
    	canvas.drawCircle(blockWidth*(currentWidth-0.5f), blockWidth*3.5f, 17, paintWidthCircle);
    }
    
    @Override  
    public boolean onTouchEvent(MotionEvent event)  
    {
    	switch(event.getAction())
    	{
    		case MotionEvent.ACTION_DOWN:
    		{
    			int x = (int)event.getX();
    			int y = (int)event.getY();
    			System.out.println(x+"  "+y);
    			x = x / blockWidth;
    			y = y / blockWidth;
    			if( y == 3 )
    			{
    				currentWidth = x+1;
    			}
    			else
    			{
    				currentColor = color[y*4+x];
    			}
    			invalidate();
    			break;
    		}
    		default:
    			break;
    	}
		return true;  
    }

}
