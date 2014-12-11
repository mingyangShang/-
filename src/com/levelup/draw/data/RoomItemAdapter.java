package com.levelup.draw.data;


import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.draw.activities.FirstActivity;
import com.example.draw.R;
import com.levelup.draw.utils.WifiAdmin;



public class RoomItemAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private List<ScanResult> mList; // 扫描到的网络结果列表
	private FirstActivity mContext;
	private Connectable connectable;

	public RoomItemAdapter(FirstActivity context, List<ScanResult> list,
			Connectable connectable) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mList = list;
		this.mInflater = LayoutInflater.from(context);
		this.connectable = connectable;
	}

	// 新加的一个函数，用来更新数据
	public void setData(List<ScanResult> list) {
		this.mList = list;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// 获取点击向的扫描结果
		final ScanResult localScanResult = mList.get(position);
		// 获取wifi类
		final WifiAdmin wifiAdmin = mContext.m_wiFiAdmin;
		final ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			//***************这里要换成那个item的布局的id
			convertView = mInflater.inflate(R.layout.first_room_item, null);
			//*********************************************
			
			// 加载布局模板控件
			viewHolder.textVName = ((TextView) convertView
					.findViewById(R.id.first_room_name));
			viewHolder.btConnect = ((Button)convertView.findViewById(R.id.first_room_enter));
			viewHolder.pb = ((ProgressBar)convertView.findViewById(R.id.first_room_wait));
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 点击连接处理事件
		viewHolder.btConnect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				
				v.setVisibility(View.GONE);
				viewHolder.pb.setVisibility(View.VISIBLE);
				// 创建wifi网络
				WifiConfiguration localWifiConfiguration = wifiAdmin
						.createWifiInfo(localScanResult.SSID,
								FirstActivity.WIFI_AP_PASSWORD, 3, "wt");
				// 添加到网络
				wifiAdmin.addNetwork(localWifiConfiguration);
				// 点击后3.5s发送消息
				mContext.mHandler.sendEmptyMessage(FirstActivity.m_nWTConnected);
				connectable.changeConnectable();
			}
		});
		/*// 点击断开处理事件
		viewHolder.linearLConnectOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 断开指定wifi热点
				System.out.println("断开wifi");
				wifiAdmin
						.disconnectWifi(wifiAdmin.getWifiInfo().getNetworkId());
				// "断开连接"消失，进度条显示
				viewHolder.textConnect.setVisibility(View.GONE);
				viewHolder.progressBConnecting.setVisibility(View.VISIBLE);
				viewHolder.linearLConnectOk.setVisibility(View.GONE);
				// 点击后3.5s发送消息
				mContext.mHandler.sendEmptyMessage(mContext.m_nWTConnected);

				connectable.changeConnectable();
			}
		});*/

		viewHolder.textVName.setText(localScanResult.SSID); // 显示热点名称

		// 正连接的wifi信息
		WifiInfo localWifiInfo = wifiAdmin.getWifiInfo();
		if (localWifiInfo != null) {
			try {// 正在连接
				if ((localWifiInfo.getSSID() != null)
						&& (localWifiInfo.getSSID()
								.equals(localScanResult.SSID))) {
					viewHolder.btConnect.setClickable(false);
					return convertView;
				}
			} catch (NullPointerException e) {
				e.printStackTrace();
				return convertView;
			}
			viewHolder.btConnect.setClickable(true); //设置俩捏按钮可以使用
		}
		return convertView;
	}

	public final class ViewHolder {
		public Button btConnect;
		public TextView textVName;
		public ProgressBar pb;
	}

	public interface Connectable {
		void changeConnectable();
	}
}
