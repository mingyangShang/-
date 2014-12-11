package com.levelup.draw.tranfer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

import com.draw.activities.DrawApplication;
import com.levelup.draw.data.MyInfo;
import com.levelup.draw.utils.WifiAdmin;

public class TranferService extends Service {
	private Handler handler = null;
	private TranferServerSocket serverSocket = null;

	public static List<MyInfo> infos = new LinkedList<MyInfo>();// 所有人信息的集合

	public static CientPlayer player = null; // 控制类

	public TranferServerSocket getServerSocket() {
		return serverSocket;
	}

	public void setServerSocket(TranferServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	private TranferClientSocket clientSocket = null;

	public TranferClientSocket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(TranferClientSocket clientSocket) {
		this.clientSocket = clientSocket;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		System.out.println("绑定service");
		return new MyBinder();
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("创建service");

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		stopThread();
		System.out.println("service销毁");
		super.onDestroy();
	}

	private void startSocket(Parcel parcel) {
		final int isServer = parcel.readInt();
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				// int isServer = parcel.readInt();
				if (isServer == ChatManager.SERVER_FLAG) {// 启动一个servet线程
					try {
						serverSocket = new TranferServerSocket(getHandler());
						serverSocket.start();

						// 创建控制其管理对象(服务者)
						player = new ServerPlayer(TranferService.this);

						// 将服务器自己的ip和名字加入到链表中，名字应该是自己定义的
						DrawApplication application = DrawApplication.getApplicationInstance();
						infos.add(new MyInfo(
								WifiAdmin.getInstance(getApplicationContext()).getHOstAddress(),
								application.getUsername(),//自己的名字
								application.getScreenWidth(),
								application.getScreenHeight()));

						DrawApplication.getApplicationInstance().setMyIp(WifiAdmin.getInstance(getApplicationContext()).getHOstAddress());

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// 设置服务器标记
					TranferController.isServer = true;
				} else {
					// 创建一个client线程
					String hostAdd = (WifiAdmin
							.getInstance(getApplicationContext())
							.getHOstAddress());
					clientSocket = new TranferClientSocket(getHandler(),
							hostAdd);
					clientSocket.start();

					// 创建客户端控制对象
					player = new CientPlayer(TranferService.this);
					DrawApplication.getApplicationInstance().setMyIp( WifiAdmin.getInstance(getApplicationContext()).getIPAddress());


					// 设置客户端标记
					TranferController.isServer = false;
				}

				// 为transfercontroller的service对象赋值
				TranferController.service = TranferService.this;

			}
		}).start();

	}

	// 自定义的binder
	public class MyBinder extends Binder {

		@Override
		protected boolean onTransact(int code, Parcel parcel, Parcel parcel2,
				int flags) throws RemoteException {
			startSocket(parcel); // 开启服务线程
			return super.onTransact(code, parcel, parcel2, flags);
		}

		public void setMyHandler(Handler handler) {
			setHandler(handler);
		}

		public Handler getMyHandler() {
			return getHandler();
		}

	}

	// 停止线程
	public void stopThread() {
		//释放服务器占用的资源
		if (serverSocket != null) {
			serverSocket.setRun(false);
			if(serverSocket!=null){
				try {
					serverSocket.getSocket().close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}finally{
					serverSocket.setSocket(null);
					serverSocket = null;
				}
			}
			System.out.println("server线程销毁");
			return ;
		}
		if (clientSocket != null) {
			clientSocket.setRun(false);
			try {
				clientSocket.getSocket().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				clientSocket.setSocket(null);
				clientSocket.setRun(false);
				clientSocket = null;
			}
			System.out.println("client线程销毁");
		}
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

}
