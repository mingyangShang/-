package com.levelup.draw.tranfer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.os.Handler;
import android.util.Log;


public class ChatManager implements Runnable {

	private static final String TAG = "ChatManager";
	
    private Socket socket = null;
    public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public String localIp = "";
	public String getLocalIp() {
		if(localIp.equals("")){
			localIp = socket.getLocalAddress().toString().substring(1);
		}
		return localIp;
	}

	private Handler handler;
    public static final int MY_HANDLE = 0x400 + 1;//自己的标志
    public static final int SERVER_FLAG = 0;
    public static final int CLIENT_FLAG = 1;

    public ChatManager(Socket socket, Handler handler) {
        this.socket = socket;
        this.handler = handler;
        try {
        	oStream = socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, "输出流打开失败", e);
		}
    }

    private InputStream iStream;
    private OutputStream oStream;
   

    @Override
    public void run() {
        try {
            iStream = socket.getInputStream();
           
            byte[] buffer = new byte[1024*10];
            int bytes;
            handler.obtainMessage(MY_HANDLE, this)
                    .sendToTarget();
//            System.out.println("发送了chatmanager");
            while (true) {
                try {
                    bytes = iStream.read(buffer);
                    if (bytes == -1) {
                    	Log.d(TAG, "没有读到数据，读线程退出");
                        break;
                    }
                    System.out.println("rec:"+bytes);
                    Log.d(TAG, "Rec:" + String.valueOf(buffer));
                    //让控制类处理得到的消息
                    TranferService.player.onReceive(new String(buffer,"utf-8"));
                    
                } catch (IOException e) {
                    Log.e(TAG, "读线程没有读成功disconnected", e);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void write(String data) {
    	//新开一个线程执行写操作
    	System.out.println("length:"+data.length()+"");
    	pool.execute(new WriteMsg(data));
    }
    
    private final ThreadPoolExecutor pool = new ThreadPoolExecutor(
            6, 6, 10, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
    
    class WriteMsg implements Runnable{
    	private String msg;
    	public WriteMsg(String msg){
    		this.msg = msg;
    	}
    	public void run(){
    		try {
    			synchronized (oStream) {
					oStream.write(msg.getBytes("utf-8"));
					oStream.flush();
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(Exception e){
				e.printStackTrace();
			}
    	}
    }
   

}
