package com.levelup.draw.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class DBHelper extends SQLiteOpenHelper {  
    private static final String DB_NAME = "draw.db";  
    private static final String TBL_NAME = "drawAnswer";  
    private static final String CREATE_TBL = " create table "  
            + " drawAnswer(answer text,hint text) ";  
      
    private SQLiteDatabase db;
    
    public DBHelper(Context c) {  
        super(c, DB_NAME, null, 1);
        System.out.println("----------DBHelper-----------");
    }
    
    @Override  
    public void onCreate(SQLiteDatabase db) {  
    	
    	if(db == null )
    		System.out.println("onCreate  DB = null");
    	else
    		System.out.println("onCreate  DB != null");
    	this.db = db;
        db.execSQL(CREATE_TBL);
        
//        insert("西红柿", "水果");
//        insert("跳远", "体育");
//        insert("猪八戒背媳妇", "歇后语");
//        insert("羊驼", "动物");
//        insert("热裤", "服饰");
//        insert("鱼子酱", "调味料");
//        insert("棉花糖", "食物");
//        insert("毛巾", "日常用品");
//        insert("花篮", "庆祝用品");
//        insert("看电影", "娱乐");
//        insert("愤怒", "感觉");
//        insert("日月潭", "风景名胜");
//        insert("左轮手枪", "武器");
//        insert("浏览器", "网络相关");
//        insert("赤脚大仙", "神话人物");
//        insert("歌手", "职业");
//        insert("吸毒", "犯罪");
//        insert("爱因斯坦", "科学家");
//        insert("解说", "体育相关");
//        insert("二胡", "乐器");
//        insert("魂斗罗", "游戏");
//        insert("卡卡西", "动漫人物");
//        insert("书中自有颜如玉", "俗语");
//        insert("糖醋排骨", "菜肴");
//        insert("海贼王", "漫画");
//        insert("乌鸦", "动物");
//        insert("西红柿炒蛋", "菜肴");
//        insert("豆浆", "饮料");
//        insert("地铁", "交通工具");
        insert("朋友绘", "应用程序");
        insert("键盘", "电子设备");
        insert("香蕉", "水果");
        insert("手机", "电子设备");
        insert("水杯", "生活用品");
        insert("乌龟", "动物");
        insert("喜羊羊", "卡通人物");
        insert("仙人球", "植物");
        
    }
    
    public void insert(String answer ,String hint) {
        ContentValues values = new ContentValues();
        values.put("answer", answer);  
        values.put("hint", hint);  
        db.insert(TBL_NAME, null, values);
    }
    
    public void insertOnce(String answer ,String hint) {
        SQLiteDatabase db = getWritableDatabase();
        
        ContentValues values = new ContentValues();
        
        values.put("answer", answer);  
        values.put("hint", hint);  
        db.insert(TBL_NAME, null, values);
        db.close();  
    }
    
    public Cursor query() {  
        SQLiteDatabase db = getWritableDatabase();  
        Cursor c = db.query(TBL_NAME, null, null, null, null, null, null);  
        return c;  
    }
    
    
    public void close() 
    {
        if (db != null)  
            db.close();  
    }
    
    public void show()
    {
    	System.out.println("-----------Show Start-----------");
    	Cursor c = this.query();
    	for(int i = 0; i < c.getCount(); i++)
    	{
    		c.moveToPosition(i);
    		System.out.println( c.getString(0)+"    "+c.getString(1)+c.getString(0).getBytes().length );
    		
    	}
    	System.out.println("-----------Show  Over-----------");
    }
    
    public String[] getRandomAnswerHint()
    {
    	Cursor c = this.query();
    	int pos = (int) (Math.random() * 1000);
    	pos = pos % c.getCount();
    	c.moveToPosition(pos);
    	
    	String[] s = new String[2];
    	s[0] = c.getString(0);
    	s[1] = c.getString(1);
    	return s;
    }
    
    @Override  
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {  
    }  
} 