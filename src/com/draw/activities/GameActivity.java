package com.draw.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.draw.R;
import com.levelup.draw.data.MyInfo;
import com.levelup.draw.tranfer.ChatManager;
import com.levelup.draw.tranfer.CientPlayer;
import com.levelup.draw.tranfer.ServerPlayer;
import com.levelup.draw.tranfer.TranferController;
import com.levelup.draw.tranfer.TranferService;
import com.levelup.draw.utils.DBHelper;
import com.levelup.draw.utils.DisplayUtil;
import com.levelup.draw.utils.PlayMusicUtil;
import com.levelup.draw.utils.WifiAdmin;

public class GameActivity extends Activity {

	private Binder binder;
	private boolean isServer = false;


	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case TranferController.TYPE_ANSWER: // 处理发来的关于题目的信息
				handleAboutAnswer(msg.getData());
				break;
			case TranferController.TYPE_MYPATH: // 处理路径信息
				System.out.println("GameActiviity接收到了路径");
				handlePath(msg.getData());
				break;
			case TranferController.TYPE_RIGHT_IP: // 处理有人答对了的信息
				handleRight(msg.getData());
				break;
			case TranferController.TYPE_ROUND_AND_IP: // 处理回合开始的信息
				handleRoundBegin(msg.getData());
				break;
			case TranferController.TYPE_WRONG_IP: // 处理科有人答错了的信息
				handleWrong(msg.getData());
				break;
			case TranferController.TYPE_ANIMATION: // 处理动画的信息
				handleAnimation(msg.getData());
				break;
			case TranferController.TYPE_ROUND_OVER: // 处理回合结束的信息
				handleRoundEnd(msg.getData());
				break;
			case TranferController.TYPE_BACK: // 处理有人返回上一界面的信息
				handleBack(msg.getData());
				break;
			case TranferController.TYPE_GIVEUP: // 处理放弃
				handleGiveup();
				break;
			case TranferController.TYPE_INFO: // 一个人的信息
				handleInfo();
				break;
			case TranferController.TYPE_INFOS: // 所有人的信息
				handleInfo();
				break;
			case TranferController.TYPE_CLEAR: // 清屏
				handleClear();
				break;
			case TranferController.TYPE_GAME_OVER: // 游戏结束
				handleGameOver();
				break;
			case TranferController.TYPE_EXIT: // 有人退出
				handleExit(msg.getData());
				break;
			}
		}

	};

	ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			binder = (Binder) service;
			((TranferService.MyBinder) binder).setMyHandler(handler);
			// 如果作为服务器端的话
			if (isServer) {
				Parcel parcel = Parcel.obtain();
				parcel.writeInt(ChatManager.SERVER_FLAG);
				Parcel parcel2 = Parcel.obtain();
				try {
					if (binder != null) {
						binder.transact(0, parcel, parcel2, BIND_AUTO_CREATE);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				} finally{
					try{
						parcel2.setDataPosition(0);// 还记得这句话的作用么？
						parcel.recycle();
						parcel2.recycle();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				
			} else {// 如果是作为客户端的话
				Parcel parcel = Parcel.obtain();
				parcel.writeInt(ChatManager.CLIENT_FLAG);
				Parcel parcel2 = Parcel.obtain();
				try {
					binder.transact(0, parcel, parcel2, BIND_AUTO_CREATE);
				} catch (RemoteException e) {
					e.printStackTrace();
				}finally{
					try{
						parcel2.setDataPosition(0);
						parcel.recycle();
						parcel2.recycle();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		// 获得屏幕的宽度和高度
		DrawApplication.getApplicationInstance().setScreenWidth(
				DisplayUtil.getScreenWidth(getApplicationContext()));
		DrawApplication.getApplicationInstance().setScreenHeight(
				DisplayUtil.getScreenHeight(getApplicationContext()));

		resolveData();

		InitFragment frag_init = new InitFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("showButton", isServer);
		frag_init.setArguments(bundle);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.game_content, frag_init, "frag_init");
		ft.commit();
	}

	// 根据从上个activity传过来的数据创建服务
	private void resolveData() {
		Intent intent = getIntent();
		isServer = intent.getBooleanExtra("isServer", false);
		Intent intent2 = new Intent();
		intent2.setClass(GameActivity.this, TranferService.class);
		bindService(intent2, conn, BIND_AUTO_CREATE);// 绑定Service
	}

	// 带参数的addDraw
	private void addDraw(Bundle bundle) {
		Fragment frag_draw = new DrawFragment();
		frag_draw.setArguments(bundle);
		FragmentTransaction fTransaction = getFragmentManager()
				.beginTransaction();
		fTransaction.replace(R.id.game_content, frag_draw, "frag_draw");
		fTransaction.commit();
	}

	// 将猜的界面展示到activity中
	private void addGuess(Bundle bundle) {
		Fragment frag_guess = new GuessFragment();
		frag_guess.setArguments(bundle);
		FragmentTransaction fTransaction = getFragmentManager()
				.beginTransaction();
		fTransaction.replace(R.id.game_content, frag_guess, "frag_guess");
		fTransaction.commit();
	}

	// 处理关于题目的信息
	private void handleAboutAnswer(Bundle bundle) {
		// 一定是在猜的界面中
		System.out.println("收到了答案" + bundle.getString("answer"));
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		// 将数据交给猜的界面去处理
		if (fragment != null) {
			((GuessFragment) fragment).handleAnswer(bundle);
		}
	}

	// 处理路径的信息
	private void handlePath(Bundle bundle) {
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		if (fragment != null) {
			System.out.println("GameActivit的GuessFragment接收到了路径数据");
			((GuessFragment) fragment).handlePath(bundle);
			return;
		}
	}

	// 处理有人答对的信息
	private void handleRight(Bundle bundle) {
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");

		PlayMusicUtil.playMusic(this, PlayMusicUtil.MUSIC_GET_RIGHT);
		if (fragment != null) {
			((GuessFragment) fragment).handleRight(bundle);
			return;
		}
		fragment = getFragmentManager().findFragmentByTag("frag_draw");
		if (fragment != null) {
			((DrawFragment) fragment).handleRight(bundle);
		}
	}

	// 处理有人答错了的信息
	private void handleWrong(Bundle bundle) {
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		if (fragment != null) {
			((GuessFragment) fragment).handleWrong(bundle);
			return;
		}
		fragment = getFragmentManager().findFragmentByTag("frag_draw");
		if (fragment != null) {
			((DrawFragment) fragment).handleWrong(bundle);
		}
	}

	// 处理游戏开始的信息
	private void handleRoundBegin(Bundle bundle) {
		String nextIp = bundle.getString("nextip");
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		if (fragment != null) {
			((GuessFragment) fragment).dismissDialog();
		}
		fragment = getFragmentManager().findFragmentByTag("frag_draw");
		if (fragment != null) {
			((DrawFragment) fragment).dismissDialog();
		}

		// 在一开始进入fragment的时候就要开始计时

		// ****************************随机得到题目
		if (nextIp.equals(DrawApplication.getApplicationInstance().getMyIp())) { // 如果下一个画的人是自己的话

			DBHelper db = new DBHelper(getApplicationContext());
			String[] s = db.getRandomAnswerHint();

			int i = s[0].getBytes().length / 3;
			String hint1 = i + "个字";

			// 将题目信息发送出去
			
			System.out.println("发送了题目信息");
			bundle.putString("answer", s[0]);
			bundle.putString("hint1", hint1);
			bundle.putString("hint2", s[1]);
			addDraw(bundle);
		} else {
			addGuess(bundle);
		}
	}

	// 处理一个回个结束
	private void handleRoundEnd(Bundle bundle) {
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		int reason = bundle.getInt("reason");
		if (fragment != null) {
			((GuessFragment) fragment).handleEnd(reason);
		} else {
			fragment = getFragmentManager().findFragmentByTag("frag_draw");
			if (fragment != null) {
				((DrawFragment) fragment).handleEnd(reason);
			}
		}
	}

	// 处理房主退出了房间
	private void handleBack(Bundle bundle) {
		Toast.makeText(this, "房主退出了房间，房间即将关闭", Toast.LENGTH_SHORT).show();
		this.finish();
	}

	private void handleGiveup() {
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		if (fragment != null) {
			((GuessFragment) fragment).handleEnd(TranferController.TYPE_GIVEUP);
		}
	}

	// 处理动画***************************************还未完成
	public void handleAnimation(Bundle bundle) {
		int animation_type = bundle.getInt("animation");
		// 播放动画
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		if (fragment != null) {
			((GuessFragment) fragment).playGIF(animation_type);
			return;
		}
		fragment = getFragmentManager().findFragmentByTag("frag_draw");
		if (fragment != null) {
			((DrawFragment) fragment).playGIF(animation_type);
		}
	}

	// 处理一个人的信息，也就是通知initfragment界面中德listview更新
	private void handleInfo() {
		Fragment fragment = getFragmentManager().findFragmentByTag("frag_init");
		if (fragment != null) {
			// 通知更新界面
			((InitFragment) fragment).refresh();
		}
	}

	// 处理清屏事件
	private void handleClear() {
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		if (fragment != null) {
			// 通知更新界面
			((GuessFragment) fragment).handleClear();
		}
	}

	private void handleGameOver() {

		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		if (fragment != null) {
			((GuessFragment) fragment).dismissDialog();
		}
		fragment = getFragmentManager().findFragmentByTag("frag_draw");
		if (fragment != null) {
			((DrawFragment) fragment).dismissDialog();
		}

		Toast.makeText(getApplicationContext(), "游戏结束", 1000).show();
		if (TranferService.player instanceof ServerPlayer) {
			// 把serverplayer中的东西清除
			ServerPlayer serverPlayer = (ServerPlayer) TranferService.player;
			serverPlayer.clear();
		}
		for (MyInfo info : TranferService.infos) {
			info.setPoint(0);
		}

		InitFragment initFragment = new InitFragment();
		Bundle bundle = new Bundle();
		bundle.putBoolean("showButton", isServer);
		initFragment.setArguments(bundle);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.game_content, initFragment, "frag_init");
		ft.commit();

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		Intent intent = new Intent(this, TranferService.class);
		unbindService(conn);
		stopService(intent);
		// 关闭wifi

		WifiAdmin wifiAdmin = WifiAdmin.getInstance(getApplicationContext());
		if (TranferService.player instanceof ServerPlayer) {
			// 关闭热点
			WifiAdmin m_wiFiAdmin = WifiAdmin
					.getInstance(getApplicationContext());
			m_wiFiAdmin.createWifiAP(m_wiFiAdmin.createWifiInfo(
					m_wiFiAdmin.getApSSID(), FirstActivity.WIFI_AP_PASSWORD, 3,
					"ap"), false);
			// 把serverplayer中的东西清除
			ServerPlayer serverPlayer = (ServerPlayer) TranferService.player;
			serverPlayer.clear();
		} else if (TranferService.player instanceof CientPlayer
				&& wifiAdmin.getNetworkId() != 0) {
			wifiAdmin.disconnectWifi(wifiAdmin.getNetworkId());
		}
		TranferService.infos.clear();
		TranferService.player = null;
		System.out.println("调用finish");
		super.onDestroy();
	}

	// 按下回退键
	// 自己想要退出
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		Fragment frag = getFragmentManager().findFragmentByTag("frag_init");
		if (frag != null && TranferService.player!=null) {
			TranferService.player.sendBack(DrawApplication
					.getApplicationInstance().getMyIp());
			finish();
			return ;
		}
		// 检测自己是不是房主
		boolean isServer = false;
		if (TranferService.player!=null && TranferService.player instanceof ServerPlayer) {
			isServer = true;
		}
		boolean isDrawer = false;
		Fragment fragment = getFragmentManager().findFragmentByTag("frag_draw");
		if (fragment != null) {
			isDrawer = true;
		}
		fragment = null;
		showExit(isServer, isDrawer);
	}

	private void showExit(final boolean isServer, final boolean isDrawer) {
		String hintString = "您确定要退出房间吗";
		// 弹出对话框
		if (isServer) {
			hintString = "您确定要退出吗，这将关闭房间";
		}
		AlertDialog exit = new AlertDialog.Builder(GameActivity.this,
				AlertDialog.THEME_HOLO_LIGHT)
				.setTitle("提示")
				.setTitle(hintString)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						String myip = DrawApplication.getApplicationInstance()
								.getMyIp();
						TranferService.player
								.sendExit(myip, isServer, isDrawer);
						// 等待2秒

						GameActivity.this.finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
					}
				}).create();
		exit.show();
	}

	// 处理有人退出
	private void handleExit(Bundle bundle) {
		// TODO Auto-generated method stub
		Fragment fragment = getFragmentManager()
				.findFragmentByTag("frag_guess");
		String ip = bundle.getString("ip");
		boolean isServer = bundle.getBoolean("isserver");

		if (isServer == true) {
			Toast.makeText(getApplicationContext(), "房主退出，房间关闭`",
					Toast.LENGTH_LONG).show();
			this.finish();
			return;
		}
		if (fragment != null) {
			((GuessFragment) fragment).handleExit(ip);
		} else {
			fragment = getFragmentManager().findFragmentByTag("frag_draw");
			if (fragment != null) {
				((DrawFragment) fragment).handleExit(ip);
			}
		}
		// 从信息表中删除退出的人的信息
		for (MyInfo info : TranferService.infos) {
			if (info.getIP().equals(bundle.getString("ip"))) {
				TranferService.infos.remove(info);
				break;
			}
		}
	}
}
