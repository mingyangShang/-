package com.levelup.draw.tranfer;

import org.json.JSONObject;

import android.R.integer;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.levelup.draw.data.MyInfo;
import com.levelup.draw.data.MyPath;
import com.levelup.draw.utils.JsonManager;

public class ServerPlayer extends CientPlayer {

	public ServerPlayer(TranferService service) {
		super(service);
		// TODO Auto-generated constructor stub
	}

	private String drawer_ip;
	private static int round = 1; // 圈数
	private static int round_index = 0; // 现在画的人顺序号
	private static int roundTotal = 2;// 总的圈数
	private static int rightNumber = 0; // 答对的人数
	private Thread thread = null; // 一个倒计时的线程
	private Thread thread_time = null;

	// 每收到一个人发来的消息，就把所有的人的信息发给别人
	@Override
	protected void handleInfo(String ip, String name, int width, int height) {
		// TODO Auto-generated method stub
		// 在这里可以将得到的ip和name存储起来
		TranferService.infos.add(new MyInfo(ip, name, width, height));
		service.getHandler().sendEmptyMessage(TranferController.TYPE_INFO);
		sendAllInfos();
	}

	@Override
	protected void handleBack(String ip, boolean isServer) {
		// 将退出的人在socket列表中删除
		for (ChatManager cm : service.getServerSocket().chats) {
			if (cm.getLocalIp().equals(ip)) {
				service.getServerSocket().chats.remove(cm);
				break;
			}
		}

		// 从信息表中删除退出的人的ip
		for (MyInfo info : TranferService.infos) {
			if (info.getIP().equals(ip)) {
				TranferService.infos.remove(info);
				break;
			}
		}
		// 发送所有人的信息
		sendAllInfos();
	}

	@Override
	protected void handleAnswer(String answer, String hint1, String hint2) {
		System.out.println("服务器收到了题目消息"); // int
		/*
		 * Message msgMessage = service.getHandler().obtainMessage(
		 * TranferController.TYPE_ANSWER); Bundle dataBundle = new Bundle();
		 * dataBundle.putString("answer", answer); dataBundle.putString("hint1",
		 * hint1); dataBundle.putString("hint2", hint2);
		 * msgMessage.setData(dataBundle);
		 * service.getHandler().sendMessage(msgMessage);
		 */
		super.handleAnswer(answer, hint1, hint2);

		// 在更新自己界面的基础上将题目信息发给不是画画的所有人
		String clientip = "";
		for (ChatManager cm : service.getServerSocket().chats) {
			clientip = cm.getSocket().getInetAddress().toString().substring(1);

			// 将题目信息发给那些既不是自己又不是画画的人
			if (!clientip.equals(drawer_ip)) {
				cm.write(JsonManager.createQuestionInfo(
						TranferController.TYPE_ANSWER, answer, hint1, hint2));
			}
		}
	}

	@Override
	protected void handlePath(JSONObject myPathObject) {
		// TODO Auto-generated method stub
		super.handlePath(myPathObject);
		/*
		 * int length = TranferService.infos.size(); String draweripString =
		 * TranferService.infos.get(round_index % length) .getIP(); // 画的人的ip
		 */String clientip = "";
		for (ChatManager cm : service.getServerSocket().chats) {
			clientip = cm.getSocket().getInetAddress().toString().substring(1);
			if (!clientip.equals(drawer_ip)) {
				cm.write(JsonManager.createPath(TranferController.TYPE_MYPATH,
						myPathObject));
			}
		}
	}

	@Override
	protected void handleRight(String ip) {
		// TODO Auto-generated method stub
		super.handleRight(ip);
		// 在更新自己界面的基础上将答对人的信息发给别人
		String clientIpString = "";
		for (ChatManager cm : service.getServerSocket().chats) {
			clientIpString = cm.getSocket().getInetAddress().toString()
					.substring(1);
			if (!clientIpString.equals(ip)) {
				cm.write(JsonManager.createRightJson(
						TranferController.TYPE_RIGHT_IP, ip, 0));// *************暂时把得分设成了0
			}
		}

		// 判断如果所有人都答对了的话，发一个回合结束的信息
		rightNumber++;
		if (rightNumber == TranferService.infos.size() - 1) {
			// 将计时器停止
			if (thread != null) {
				thread.interrupt();
				thread = null;
			}
			// 所有人都已经答对了
			sendRoundOver(TranferController.ALL_RIGHT);
			// 给自己发一个回合结束的信息
			super.handleRoundOver(TranferController.ALL_RIGHT);
		}
	}

	@Override
	protected void handleWrong(String ip, String wronganswer) {
		// TODO Auto-generated method stub
		super.handleWrong(ip, wronganswer);
		// 在更新自己界面的基础上将答错人的信息发给别人
		String clientIpString = "";
		for (ChatManager cm : service.getServerSocket().chats) {
			clientIpString = cm.getSocket().getInetAddress().toString()
					.substring(1);
			if (!clientIpString.equals(ip)) {
				cm.write(JsonManager.createWrongJson(
						TranferController.TYPE_WRONG_IP, ip, wronganswer));
			}
		}
	}

	@Override
	protected void handleGiveup() {
		// TODO Auto-generated method stub
		super.handleRoundOver(TranferController.TYPE_GIVEUP);
		sendRoundOver(TranferController.TYPE_GIVEUP);
	}

	@Override
	public void handleExit(String ip, boolean isServer, boolean isDrawer) {
		// 如果退出的人是画的人的话，
		// 将退出的人在socket列表中删除
		for (ChatManager cm : service.getServerSocket().chats) {
			if (cm.getLocalIp().equals(ip)) {
				service.getServerSocket().chats.remove(cm);
				break;
			}
		}
		/*
		 * //将退出的人在信息中删除 for(MyInfo info:TranferService.infos){
		 * if(info.getIP().equals(ip)){ TranferService.infos.remove(info);
		 * break; } }
		 */
		if (isDrawer) {
			sendRoundOver(TranferController.DRAWER_EXIT); // 原因是画的人退出了
		} else {
			sendExit(ip, false, false);
		}
		// 调用父类的方法去提醒activity更新
		super.handleExit(ip, isServer, isDrawer);
	}

	/**
	 * 把自己答对的信息告诉所有不是自己的人
	 */
	@Override
	public void sendRight(String ip, int score) {
		// TODO Auto-generated method stub
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createRightJson(
					TranferController.TYPE_RIGHT_IP, ip, score));
		}
		// 如果是自己答对的话，判断是不是所有人都答对了
		rightNumber++; // 又多了一个人答对了
		// 如果此时所有人都答对了，发送游戏结束的标记
		if (rightNumber == TranferService.infos.size() - 1) {
			sendRoundOver(TranferController.ALL_RIGHT);
			// 将计时器停止
			if (thread != null) {
				thread.interrupt();
				thread = null;
			}
			// **************发给自己回合结束的信息
			super.handleRoundOver(TranferController.ALL_RIGHT);
		}
	}

	/**
	 * 把自己答错的信息告诉所有不是自己的人
	 */
	@Override
	public void sendWrong(String ip, String wrong) {
		// TODO Auto-generated method stub
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createWrongJson(
					TranferController.TYPE_WRONG_IP, ip, wrong));
		}
	}

	public void sendRound(int number) {
		int length = TranferService.infos.size();
		int index = 0;
		/*
		 * if (length > 0) { r_index = (round++) / length + 1; index =
		 * (round_index++) % length; }
		 */
		if (length > 0) {
			index = round_index++;
			if (index >= length) {
				index = 0;
				round_index = 1;
				round++;
				if (round == roundTotal + 1) {
					// 游戏结束
					for (ChatManager cm : service.getServerSocket().chats) {
						cm.write(JsonManager
								.createGameOver(TranferController.TYPE_GAME_OVER));
					}
					super.handleGameOver();
					// 不再开启等待线程
					return;
				}
			}
		}

		roundTotal = number;
		rightNumber = 0;

		/*
		 * if (r_index > roundTotal) {// 游戏结束
		 * 
		 * }
		 */
		/*
		 * int index = round_index++; if(round_index>=length-1){ round_index=1;
		 * index=0; } if(index>=length-1){ round_index = 0; }
		 */
		drawer_ip = TranferService.infos.get(index).getIP();
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createRoundJson(
					TranferController.TYPE_ROUND_AND_IP, round, roundTotal,
					drawer_ip));
		}

		// 发送给自己，因为不这样的话自己是收不到这个消息的
		Message msg = service.getHandler().obtainMessage(
				TranferController.TYPE_ROUND_AND_IP);
		Bundle bundle = new Bundle();
		bundle.putInt("round", round);
		bundle.putInt("roundTotal", roundTotal);
		bundle.putString("nextip", drawer_ip);
		msg.setData(bundle);
		service.getHandler().sendMessage(msg);

		// 开始计时
		if (thread == null) {// 说明上一个执行完或者强制停止了
			System.out.println("要开启一个新的回合的倒计时线程");
			thread = new TimeCountThread();
			thread.start();
		}
	}

	public void sendRound() {
		sendRound(roundTotal);
	}

	/**
	 * 向所有人发送一个人的回合结束
	 * 
	 * @param reason
	 *            回合结束的原因
	 */
	public void sendRoundOver(int reason) {
		if (service == null) {
			return;
		}
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createRoundOver(
					TranferController.TYPE_ROUND_OVER, reason));
		}

		// 开启一个等待线程
		if (thread_time != null && thread_time.isAlive()) {
			System.out.println("回合间等待线程还活着");
			thread_time.interrupt();
		}
		thread_time = null;
		thread_time = new TimeThread();
		thread_time.start();
	}

	// 处理别人发过来的动画类型
	@Override
	protected void handleAnimation(String ip, int animation_type) {
		// TODO Auto-generated method stub
		// 首先自己要在界面中去处理这个动画
		super.handleAnimation(ip, animation_type);
		// 然后要把这个动画发给别人
		String clientIpString = "";
		for (ChatManager cm : service.getServerSocket().chats) {
			clientIpString = cm.getSocket().getInetAddress().toString()
					.substring(1);
			if (!ip.equals(clientIpString)) {
				cm.write(JsonManager.createAnimation(
						TranferController.TYPE_ANIMATION, ip, animation_type));
			}
		}
	}

	@Override
	protected void handleClear() {
		// TODO Auto-generated method stub
		super.handleClear();
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createClear(TranferController.TYPE_CLEAR));
		}
	}

	// 服务器自己向别人发送动画
	@Override
	public void sendAnimation(String ip, int animation_type) {
		// TODO Auto-generated method stub
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createAnimation(
					TranferController.TYPE_ANIMATION, ip, animation_type));
		}
	}

	/**
	 * 向所有人发送所有人的信息
	 */
	public void sendAllInfos() {
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createInfosJson(TranferController.TYPE_INFOS,
					TranferService.infos));
		}
	}

	/**
	 * 给除了自己的所有人发路径
	 */
	@Override
	public void sendPath(MyPath myPath) {
		// TODO Auto-generated method stub
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createPath(TranferController.TYPE_MYPATH,
					myPath));
		}
	}

	/**
	 * 给所有其他人发关于题目的信息
	 */
	@Override
	public void sendAnswer(String answer, String hint1, String hint2) {
		// TODO Auto-generated method stub
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createQuestionInfo(
					TranferController.TYPE_ANSWER, answer, hint1, hint2));
		}
	}

	/*
	 * 给所有其他的人发送要退出 的信息
	 */
	public void sendExit(String ip, boolean isServer, boolean isDrawer) {
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createExit(TranferController.TYPE_EXIT, ip,
					isServer, isDrawer));
		}
		if (thread_time != null) {
			thread_time.interrupt();
			thread_time = null;
		}
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}

	/**
	 * 给所有人发送房主退出的消息
	 */
	@Override
	public void sendBack(String ip) {
		// TODO Auto-generated method stub
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createBack(TranferController.TYPE_BACK, ip,
					true));
		}
	}

	@Override
	public void sendClear() {
		// TODO Auto-generated method stub
		for (ChatManager cm : service.getServerSocket().chats) {
			cm.write(JsonManager.createClear(TranferController.TYPE_CLEAR));
		}
	}

	@Override
	public void sendGiveUp() {
		// TODO Auto-generated method stub
		super.handleRoundOver(TranferController.TYPE_GIVEUP);
		sendRoundOver(TranferController.TYPE_GIVEUP);
		if (thread != null) {
			thread.interrupt();
			thread = null;
		}
	}

	/**
	 * 清除掉那些静态变量
	 */
	public void clear() {
		round = 0;
		round_index = 0;
		roundTotal = 0;
		rightNumber = 0;
	}

	// 一个回合倒计时的线程
	class TimeCountThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				Thread.sleep(1000 * 60);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("被打断");
				thread = null;
				return;
			}
			// 1分钟时间到，发送游戏结束，allright标志为false
			thread = null;
			System.out.println("发送游戏回合结束：时间到");
			sendRoundOver(TranferController.TIME_OVER);
			// *************************发给自己回合结束的信息
			Message msgMessage = service.getHandler().obtainMessage(
					TranferController.TYPE_ROUND_OVER);
			Bundle bundle = new Bundle();
			bundle.putBoolean("allright", false);
			msgMessage.setData(bundle);
			service.getHandler().sendMessage(msgMessage);

		}
	}

	// 一个等待的线程
	class TimeThread extends Thread {
		public void run() {
			super.run();
			try {
				Thread.sleep(1000 * 5);
			} catch (Exception e) {
				e.printStackTrace();
				return;
			}
			System.out.println("回合间等待线程结束，发下一回合信息");
			sendRound();
			thread_time = null;
		}
	}
}
