package com.levelup.draw.tranfer;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.util.Log;

import com.draw.activities.FirstActivity;


public class TranferServerSocket extends Thread{

	ServerSocket socket = null;
    public void setSocket(ServerSocket socket) {
		this.socket = socket;
	}

	public ServerSocket getSocket() {
		return socket;
	}

	private final int THREAD_COUNT = 6;
    private Handler handler;
    private static final String TAG = "TranferServerSocket";
    private boolean run = true;//是否允许线程继续运行 
    public List<ChatManager> chats = new LinkedList<ChatManager>();//一个盛放聊天管理的集合

    public boolean isRun() {
		return run;
	}

	public void setRun(boolean run) {
		this.run = run;
	}

	public TranferServerSocket(Handler handler) throws IOException {
        try {
            socket = new ServerSocket(FirstActivity.SERVER_PORT);
            this.handler = handler;
            Log.d("GroupOwnerSocketHandler", "Socket Started");
        } catch (IOException e) {
            e.printStackTrace();
            pool.shutdownNow();
            throw e;
        }

    }

    /**
     * A ThreadPool for client sockets.
     */
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            THREAD_COUNT, THREAD_COUNT, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    @Override
    public void run() {
        while (true) {
            try {
            	if(!run)	return ;
            	//创建一个ChatManager，将其键入到list中
            	ChatManager cManager = new ChatManager(socket.accept(), handler);
                pool.execute(cManager);
                chats.add(cManager);
                Log.d(TAG, "创建了一个新的线程接收用户的请求");

            } catch (IOException e) {
                try {
                    if (socket != null && !socket.isClosed())
                        socket.close();
                } catch (IOException ioe) {
                }
                e.printStackTrace();
                pool.shutdownNow();
                break;
            }
        }
    }

}
