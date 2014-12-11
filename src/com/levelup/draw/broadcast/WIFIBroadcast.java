package com.levelup.draw.broadcast;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

/**
 * 监听wifi热点变化
 * 
 * @author smy
 * 
 */
public class WIFIBroadcast extends BroadcastReceiver{

	public static ArrayList<EventHandler> ehList = new ArrayList<EventHandler>();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		//搜索到wifi热点结果的广播:  "android.net.wifi.SCAN_RESULTS"
		if(intent.getAction().endsWith(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
			Log.i("WTScanResults---->扫描到了可用网络---->", "android.net.wifi.SCAN_RESULTS");
			//遍历通知各个监听接口
			((EventHandler)ehList.get(0)).scanResultsAvaiable();
			
		//wifi打开或关闭状态变化   "android.net.wifi.WIFI_STATE_CHANGED"
		}else if(intent.getAction().endsWith(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			Log.i("WTScanResults----->wifi状态变化--->", "android.net.wifi.WIFI_STATE_CHANGED");
			//这里不需要连接一个SSID（wifi名称）
			for(int j = 0; j < ehList.size(); j++) {
				((EventHandler)ehList.get(j)).wifiStatusNotification();
			}
			
		//连接上一个SSID后发出的广播，(注：与android.net.wifi.WIFI_STATE_CHANGED的区别)  
		}else if(intent.getAction().endsWith(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			Log.i("WTScanResults----->网络状态变化---->", "android.net.wifi.STATE_CHANGE");
			System.out.println("网络状态变化");
			  Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			  if(parcelableExtra!=null){
				  NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;    
	                State state = networkInfo.getState();  
	                if(state==State.CONNECTED){//当然，这边可以更精确的确定状态
	                	((EventHandler)ehList.get(0)).handleConnectChange();
	                }
	                	
	                }
			
		}
	}
	/**
	 * 事件监听接口
	 * @author ZHF
	 *
	 */
	public static abstract interface EventHandler {
		/**处理连接变化事件**/
		public abstract void handleConnectChange();
		/**扫描结果是有效事件**/
		public abstract void scanResultsAvaiable();
		/**wifi状态变化事件**/
		public abstract void wifiStatusNotification();
	}
	
}