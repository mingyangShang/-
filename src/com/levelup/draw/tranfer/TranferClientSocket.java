package com.levelup.draw.tranfer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import android.os.Handler;
import android.util.Log;

import com.draw.activities.DrawApplication;
import com.draw.activities.FirstActivity;
import com.levelup.draw.utils.JsonManager;


public class TranferClientSocket extends Thread{

	private static final String TAG = "ClientSocketHandler";
    private Handler handler;
    private ChatManager chat;
    private String mAddress;
    private boolean run = true;
    Socket socket = null;

    public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public TranferClientSocket(Handler handler, String groupOwnerAddress) {
    	System.out.println("创建了一个clientsocket线程");
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
    }

    public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}

	@Override
    public void run() {
        socket = new Socket();
        try {
        	socket.setReuseAddress(true);
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress,
                    FirstActivity.SERVER_PORT), 5*1000);
            Log.d(TAG, "Launching the I/O handler");
            chat = new ChatManager(socket, handler);
            new Thread(chat).start();
            
            //在这里把自己的信息发送给服务器
            DrawApplication application = DrawApplication.getApplicationInstance();
            if(chat!=null){
            	chat.write(JsonManager.createMyInfo(TranferController.TYPE_INFO, chat.getLocalIp(),
            			application.getUsername(), application.getScreenWidth(), application.getScreenHeight()));	

            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }

    public ChatManager getChat() {
        return chat;
    }

	public boolean isRun() {
		return run;
	}
	public void setRun(boolean run) {
		this.run = run;
	}

}
