package com.draw.activities;

import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.draw.R;
import com.levelup.draw.broadcast.WIFIBroadcast;
import com.levelup.draw.broadcast.WIFIBroadcast.EventHandler;
import com.levelup.draw.data.CreateAPProcess;
import com.levelup.draw.data.RoomItemAdapter;
import com.levelup.draw.data.RoomItemAdapter.Connectable;
import com.levelup.draw.data.WFSearchProcess;
import com.levelup.draw.utils.DBHelper;
import com.levelup.draw.utils.SettingUtil;
import com.levelup.draw.utils.WifiAdmin;

public class FirstActivity extends Activity implements OnClickListener,
		EventHandler, Connectable {

	Button name;
	Button search;
	Button create;
	Button add;
	Button volume;
	Button quit;
	View mainBG;

	View contentApp;
	View contentAdd;
	View contentSearch;
	View contentInput;
	DBHelper dbHelper;

	public WifiAdmin m_wiFiAdmin; // Wifi管理类

	public static final String WIFI_AP_HEADER = "朋友绘_";
	public static int SERVER_PORT = 4545;// 服务器的端口号
	// 消息事件
	public static final int m_nWifiSearchTimeOut = 0;// 搜索超时
	public static final int m_nWTScanResult = 1;// 搜索到wifi返回结果
	public static final int m_nWTConnectResult = 2;// 连接上wifi热点
	public static final int m_nCreateAPResult = 3;// 创建热点结果
	public static final int m_nUserResult = 4;// 用户上线人数更新命令(待定)
	public static final int m_nWTConnected = 5;// 点击连接后断开wifi，3.5秒后刷新adapter
	public static final String WIFI_AP_PASSWORD = "smy12345";

	public CreateAPProcess m_createAPProcess; // 创建Wifi热点线程
	public WFSearchProcess m_wtSearchProcess; // WiFi搜索进度条线程

	private RoomItemAdapter m_wTAdapter; // 网络列表适配器
	ArrayList<ScanResult> m_listWifi = new ArrayList<ScanResult>();// 检测到热点信息列表

	private BroadcastReceiver receiver = null;
	private final IntentFilter intentFilter = new IntentFilter();

	private boolean canConnect = true;
	private boolean cancreate = true;

	public Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case m_nWifiSearchTimeOut: // 搜索超时

				findViewById(R.id.first_content_progress).setVisibility(
						View.GONE);
				m_wtSearchProcess.stop();
				m_listWifi.clear(); // 网络列表
				// 设置控件
//				Toast.makeText(getApplicationContext(), "搜索超时", 1000).show();
				break;

			case m_nWTScanResult: // 扫描到结果
				findViewById(R.id.first_content_progress).setVisibility(
						View.GONE);
				m_listWifi.clear();
				if (m_wiFiAdmin.mWifiManager.getScanResults() != null) {
					for (int i = 0; i < m_wiFiAdmin.mWifiManager
							.getScanResults().size(); i++) {
						ScanResult scanResult = m_wiFiAdmin.mWifiManager
								.getScanResults().get(i);
						// 和指定连接热点比较，将其他的过滤掉！
						if (scanResult.SSID.startsWith(WIFI_AP_HEADER)) {
							m_listWifi.add(scanResult);
						}
					}
					if (m_listWifi.size() > 0) {
						m_wtSearchProcess.stop();
						// 更新列表，显示出搜索到的热点
						m_wTAdapter.setData(m_listWifi);
						m_wTAdapter.notifyDataSetChanged();
					}
				}
				break;
			case m_nWTConnectResult: // 连接结果

				m_wTAdapter.notifyDataSetChanged(); // 刷新适配器数据
				// *************************************************
				// 判断是不是自己程序键的热点
				boolean isMySsid = false;
				WifiInfo localWifiInfo = m_wiFiAdmin.getWifiInfo();
				String conSsid = localWifiInfo.getSSID();
				if (conSsid.startsWith("\""))
					conSsid = conSsid.substring(1);
				if (conSsid.endsWith("\""))
					conSsid = conSsid.substring(0, conSsid.length() - 1);
				for (ScanResult sr : m_listWifi) {
					if (conSsid.equals(sr.SSID)) {
						isMySsid = true;
						break;
					}
				}
				if (isMySsid == false) {
					break;
				}

				Intent intent = new Intent(FirstActivity.this,
						GameActivity.class);
				intent.putExtra("isServer", false);
				startActivityForResult(intent, 0);
				System.out.println("start,false");
				break;
			case m_nCreateAPResult: // 创建wifi热点结果
				m_createAPProcess.stop();
				// 旋转进度条消失
				findViewById(R.id.first_content_progress).setVisibility(
						View.GONE);
				if ((m_wiFiAdmin.getWifiApState() == 3 || m_wiFiAdmin
						.getWifiApState() == 13)
						&& (m_wiFiAdmin.getApSSID().startsWith(WIFI_AP_HEADER))
						&& cancreate == true) {
					// 跳转到聊天的界面
					if (cancreate == true) {
						cancreate = false;
						Intent intent2 = new Intent(FirstActivity.this,
								GameActivity.class);
						intent2.putExtra("isServer", true);
						startActivity(intent2);
						System.out.println("start,true");
						break;
					}
				} else {
					Toast.makeText(getApplicationContext(), "热点创建失败", 1000)
							.show();
					cancreate = true;
				}
				break;
			case m_nWTConnected: // 点击连接后断开wifi，3.5s后刷新
				m_wTAdapter.notifyDataSetChanged();
				break;
			}

		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 取消标题
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 全屏
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_first);

		boolean i = SettingUtil.get(getApplicationContext(), "volume", true);
		DrawApplication.getApplicationInstance().setVolume(i);
		
		boolean open = WifiAdmin.getInstance(getApplicationContext()).isWifiOpen();
		DrawApplication.getApplicationInstance().setWifiOpen(open);
		

		ListView m_listVWT = (ListView) findViewById(R.id.first_content_list);

		m_wiFiAdmin = WifiAdmin.getInstance(getApplicationContext());

		m_wTAdapter = new RoomItemAdapter(this, m_listWifi, this);
		m_listVWT.setAdapter(m_wTAdapter);

		// 创建Wifi热点
		m_createAPProcess = new CreateAPProcess(this);
		m_wtSearchProcess = new WFSearchProcess(this);

		intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);

		dbHelper = new DBHelper(getApplicationContext());

		name = (Button) findViewById(R.id.first_name);
		create = (Button) findViewById(R.id.first_create);
		search = (Button) findViewById(R.id.first_search);
		add = (Button) findViewById(R.id.first_add);
		volume = (Button) findViewById(R.id.first_volume);
		quit = (Button) findViewById(R.id.first_quit);
		mainBG = findViewById(R.id.first_bg);

		mainBG.setOnClickListener(this);
		name.setOnClickListener(this);
		create.setOnClickListener(this);
		search.setOnClickListener(this);
		add.setOnClickListener(this);
		volume.setOnClickListener(this);
		quit.setOnClickListener(this);

		contentApp = findViewById(R.id.first_content_app);
		contentAdd = findViewById(R.id.first_content_add);
		contentInput = findViewById(R.id.first_content_input_name);
		contentSearch = findViewById(R.id.first_content_list);

		if (!DrawApplication.getApplicationInstance().getVolume()) {
			volume.setBackgroundResource(R.drawable.first_volume_off);
		}

		String userName = SettingUtil.get(getApplicationContext(), "username",
				"");

		System.out.println("2222" + userName);
		DrawApplication applcApplication = DrawApplication
				.getApplicationInstance();
		applcApplication.setUsername(userName);
		if (userName.equals("")) {
			name.setText("点击输入用户名");
		} else {
			name.setText(userName);
		}
	}

	// 创建房间的函数
	private void createRoom() {

		DrawApplication applcApplication = DrawApplication
				.getApplicationInstance();
		String userName = applcApplication.getUsername();

		if (m_wiFiAdmin.getWifiApState() == 4) { // WIFI_STATE_UNKNOWN
			Toast.makeText(getApplicationContext(), "您的设备不支持热点创建!",
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (m_wiFiAdmin.mWifiManager.isWifiEnabled()) { // 目前连着wifi
			m_wiFiAdmin.closeWifi();
		}
		if (m_wtSearchProcess.running) {
			m_wtSearchProcess.stop(); // 停止线程
		}

		m_wiFiAdmin.createWifiAP(m_wiFiAdmin.createWifiInfo(WIFI_AP_HEADER
				+ userName + "的房间", WIFI_AP_PASSWORD, 3, "ap"), true);
		m_createAPProcess.start(); // 开启创建热点线程

		// 将wifi信息列表设置到listview中
		m_listWifi.clear();
		m_wTAdapter.setData(m_listWifi);
		m_wTAdapter.notifyDataSetChanged();
	}

	// 搜索房间的函数
	private void searchRoom() {
		// 关闭wifi
		m_wiFiAdmin.closeWifi();
		if (!m_wtSearchProcess.running) { // 搜索线程没有开启
			// 1.当前热点或wifi连接着 WIFI_STATE_ENABLED 3 //WIFI_AP_STATE_ENABLED 13
			if (m_wiFiAdmin.getWifiApState() == 3
					|| m_wiFiAdmin.getWifiApState() == 13) {
				Toast.makeText(getApplicationContext(), "现在已建立wifi", 1000)
						.show();
				return;
			}
			// 2.当前没有热点或wifi连接着
			if (!m_wiFiAdmin.mWifiManager.isWifiEnabled()) { // 如果wifi没打开
				m_wiFiAdmin.OpenWifi();
			}
			// 开始搜索wifi
			m_wiFiAdmin.startScan();
			m_wtSearchProcess.start(); // 开启搜索线程
		} else {// 搜索线程开启着，再次点击按钮 重新启动
			m_wtSearchProcess.stop();
			m_wiFiAdmin.startScan(); // 开始搜索wifi
			m_wtSearchProcess.start();
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		receiver = new WIFIBroadcast();
		registerReceiver(receiver, intentFilter);
		WIFIBroadcast.ehList.add(this);
		cancreate = true;
		initContent();
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiver);
		WIFIBroadcast.ehList.remove(this);
	}

	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		//如果玩家在游戏前的wifi状态是打开的话，在游戏关闭时也要打开玩家的wifi
		if(DrawApplication.getApplicationInstance().isWifiOpen()){
			WifiAdmin.getInstance(getApplicationContext()).OpenWifi();
		}else{
			WifiAdmin.getInstance(getApplicationContext()).closeWifi();
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.first_name:
			initContent();
			name.setTextColor(Color.parseColor("#1fb6c8"));
			showInputName();
			break;
		case R.id.first_create:
			String sname = SettingUtil.get(getApplicationContext(), "username",
					"");
			if (sname.equals("")) {// 说明现在还没有名字
				Toast.makeText(getApplicationContext(), "请设置自己的玩家名", 500)
						.show();
				break;
			}
			findViewById(R.id.first_content_app).setVisibility(View.GONE);
			findViewById(R.id.first_content_progress).setVisibility(
					View.VISIBLE);
			createRoom();
			break;

		case R.id.first_search:
			String sname2 = SettingUtil.get(getApplicationContext(),
					"username", "");
			if (sname2.equals("")) {// 说明现在还没有名字
				Toast.makeText(getApplicationContext(), "请设置自己的玩家名", 500)
						.show();
				break;
			}
			initContent();
			search.setBackgroundResource(R.drawable.first_search_u);
			findViewById(R.id.first_content_progress).setVisibility(
					View.VISIBLE);
			showSearch();
			searchRoom();
			break;
		case R.id.first_add:
			initContent();
			add.setBackgroundResource(R.drawable.first_add_u);
			showAdd();
			break;
		case R.id.first_volume:
			if (DrawApplication.getApplicationInstance().getVolume()) {
				DrawApplication.getApplicationInstance().setVolume(false);
				volume.setBackgroundResource(R.drawable.first_volume_off);
				SettingUtil.set(getApplicationContext(), "volume", false);
			} else {
				DrawApplication.getApplicationInstance().setVolume(true);
				volume.setBackgroundResource(R.drawable.first_volume_on);
				SettingUtil.set(getApplicationContext(), "volume", true);
			}
			break;
		case R.id.first_quit:
			this.finish();
			break;
		case R.id.first_bg:
			initContent();
			break;
		default:
			break;
		}

	}

	private void showSearch() {
		// TODO Auto-generated method stub

		contentApp.setVisibility(View.GONE);
		contentSearch.setVisibility(View.VISIBLE);

	}

	private void showAdd() {
		// TODO Auto-generated method stub
		contentApp.setVisibility(View.GONE);
		contentAdd.setVisibility(View.VISIBLE);
		final EditText et1 = (EditText) findViewById(R.id.first_add_citiao);
		final EditText et2 = (EditText) findViewById(R.id.first_add_leibie);

		findViewById(R.id.first_add_ok).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String s1 = et1.getText().toString();
						String s2 = et2.getText().toString();

						if (s1.equals("") || s2.equals("")) {
							Toast.makeText(FirstActivity.this, "内容不能为空",
									Toast.LENGTH_SHORT).show();
						} else {
							dbHelper.insertOnce(s1, s2);
							Toast.makeText(FirstActivity.this, "添加成功",
									Toast.LENGTH_SHORT).show();
						}

						et1.setText("");
						et2.setText("");
						initContent();
					}
				});

		findViewById(R.id.first_add_no).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						et1.setText("");
						et2.setText("");
						initContent();

					}
				});
	}

	private void showInputName() {
		// TODO Auto-generated method stub
		contentApp.setVisibility(View.GONE);
		contentInput.setVisibility(View.VISIBLE);
		final EditText et = (EditText) findViewById(R.id.first_input_name);
		findViewById(R.id.first_input_ok).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String sname = et.getText().toString();

						if (sname.equals("")) {
							Toast.makeText(getApplicationContext(),
									"用户名不能为空,请重新输入", Toast.LENGTH_SHORT).show();
						} else {
							name.setText(sname);
							SettingUtil.set(getApplicationContext(),
									"username", sname);
							DrawApplication.getApplicationInstance()
									.setUsername(sname);
						}
						et.setText("");
						initContent();
					}
				});

		findViewById(R.id.first_input_no).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						et.setText("");
						initContent();

					}
				});
	}

	private void initContent() {
		// TODO Auto-generated method stub
		name.setTextColor(Color.parseColor("#2e0000"));
		search.setBackgroundResource(R.drawable.first_search_n);
		add.setBackgroundResource(R.drawable.first_add_n);
		findViewById(R.id.first_content_progress).setVisibility(View.GONE);
		contentAdd.setVisibility(View.GONE);
		contentInput.setVisibility(View.GONE);
		contentApp.setVisibility(View.VISIBLE);
		contentSearch.setVisibility(View.GONE);
	}

	@Override
	public void handleConnectChange() {
		if (canConnect == false)
			return;
		Message msg = mHandler.obtainMessage(m_nWTConnectResult);
		mHandler.sendMessage(msg);
		canConnect = false;

	}

	@Override
	public void scanResultsAvaiable() {
		Message msg = mHandler.obtainMessage(m_nWTScanResult);
		mHandler.sendMessage(msg);
	}

	@Override
	public void wifiStatusNotification() {
		m_wiFiAdmin.mWifiManager.getWifiState(); // 获取当前wifi状态
	}

	@Override
	public void changeConnectable() {
		// TODO Auto-generated method stub
		this.canConnect = true;
	}
}
