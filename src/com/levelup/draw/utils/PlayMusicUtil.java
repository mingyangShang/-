package com.levelup.draw.utils;


import com.draw.activities.DrawApplication;
import com.example.draw.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class PlayMusicUtil {
	
	private  static SoundPool soundPool;
	
	
	public static final int MUSIC_BUTTON_CLICK = 1;
	public static final int MUSIC_BUTTON_CHANGE = 2;
	public static final int MUSIC_BUTTON_OPEN = 3;
	public static final int MUSIC_GIF_FLOWER = 4;
	public static final int MUSIC_GIF_KISS = 5;
	public static final int MUSIC_GIF_EGG = 6;
	public static final int MUSIC_GIF_SHOE = 7;
	public static final int MUSIC_ROOM_ENTER = 8;
	public static final int MUSIC_GET_RIGHT = 9;
	
	
	public static void playMusic(Context con,int i)
	{
		if( !DrawApplication.getApplicationInstance().getVolume() )
			return;
		if(soundPool == null)
		{
			soundPool = new SoundPool(5,AudioManager.STREAM_MUSIC, 5);
			soundPool.load(con, R.raw.button1, 4);
			soundPool.load(con, R.raw.button2, 4);
			soundPool.load(con, R.raw.button3, 4);
			soundPool.load(con, R.raw.flower, 2);
			soundPool.load(con, R.raw.kiss, 2);
			soundPool.load(con, R.raw.egg, 2);
			soundPool.load(con, R.raw.shoe, 2);
			soundPool.load(con, R.raw.room_enter, 2);
			soundPool.load(con, R.raw.get_right, 2);
			
			playMusic(con, i);
		}
		else
		{
			soundPool.play(i,1, 1, 0, 0, 1);
		}
	}

}
