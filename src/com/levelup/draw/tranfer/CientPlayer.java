package com.levelup.draw.tranfer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;

import com.levelup.draw.data.MyInfo;
import com.levelup.draw.data.MyPath;
import com.levelup.draw.utils.DisplayUtil;
import com.levelup.draw.utils.JsonManager;

public class CientPlayer {
	
	public CientPlayer(TranferService service) {
		this.service = service;
	}

	protected TranferService service;
	
	public TranferService getService() {
		return service;
	}


	/**
	 * 处理收到的消息
	 * @param json
	 */
	public void onReceive(String json){
		JSONObject jsonObject = null;
		try{
			jsonObject = new JSONObject(json);
			int type = jsonObject.getInt("type");
			switch(type){
			case TranferController.TYPE_INFO: //得到的是一个人的信息
				handleInfo(jsonObject.getString("ip"),jsonObject.getString("name"),
						jsonObject.getInt("screenwidth"),jsonObject.getInt("screenheight"));
				break;
			
			case TranferController.TYPE_INFOS: //得到的是所有人的个人信息
				handleInfos(jsonObject.getJSONArray("infos"));
				break;
			
			case TranferController.TYPE_ROUND_AND_IP: //得到的是游戏开始的信息
				handleRound(jsonObject.getInt("round"),jsonObject.getString("nextip"),jsonObject.getInt("roundTotal"));
				break;
				
			case TranferController.TYPE_ANSWER: //得到的是关于题目的信息
				System.out.println("收到了题目消息");
				handleAnswer(jsonObject.getString("answer"),jsonObject.getString("hint1"),jsonObject.getString("hint2"));
				break;
				
			case TranferController.TYPE_MYPATH: //得到的是画的路径的信息
//				System.out.println("路径length:"+);
				handlePath( jsonObject.getJSONObject("path"));
				break;
				
			case TranferController.TYPE_RIGHT_IP: //得到的是有人答对的消息
				handleRight(jsonObject.getString("rightip"));//,jsonObject.getInt("score"));
				break;
				
			case TranferController.TYPE_WRONG_IP: //得到的是有人答错的消息
				handleWrong(jsonObject.getString("wrongip"),jsonObject.getString("wronganswer"));
				break;
			case TranferController.TYPE_GIVEUP: //得到的时放弃的消息
				handleGiveup();
				break;
				
			case TranferController.TYPE_ROUND_OVER: //得到的是回合结束的消息
				handleRoundOver(jsonObject.getInt("reason"));
				break;
				
			case TranferController.TYPE_ANIMATION: //得到的是动画的类型
				handleAnimation(jsonObject.getString("ip"),jsonObject.getInt("animation"));
				break;
				
			case TranferController.TYPE_CLEAR: //得到是清屏的信息
				handleClear();
				break;
			case TranferController.TYPE_GAME_OVER: //得到的是游戏结束的信息
				handleGameOver();
				break;
				
			case TranferController.TYPE_EXIT: //得到是有人退出的消息
				handleExit(jsonObject.getString("ip"),jsonObject.getBoolean("isserver"),jsonObject.getBoolean("isdrawer")); 
				break;
				
			case TranferController.TYPE_BACK: //得到的时有人返回上一界面的消息
				handleBack(jsonObject.getString("ip"),jsonObject.getBoolean("isserver"));
				break;
			}
		} catch(JSONException exception){
			exception.printStackTrace();
		}
	}

	/**
	 * 处理别人发来的个人信息
	 * @param ip 一个人的ip
	 * @param name 对应的名字
	 */
	protected void  handleInfo(String ip,String name,int width,int height) {
		
	}
	
	/**
	 * 处理有人退出的消息
	 * @param ip
	 * @param isServer
	 */
	//这个处理函数中isserver为true
	protected void  handleBack(String ip,boolean isServer) {
		Message msg = service.getHandler().obtainMessage(TranferController.TYPE_BACK);
		Bundle bundle = new Bundle();
		bundle.putInt("type", TranferController.TYPE_BACK);
		msg.setData(bundle);
		service.getHandler().sendMessage(msg);
	}
	/**
	 * 处理收到的所有人的个人信息
	 * @param infos 所有人的个人信息的json数组的字符串
	 */
	protected void handleInfos(JSONArray jsonArray){
		JSONObject jsonObject = null;
		
		//先把之前的信息清除掉，然后重新添加所有人的信息
		TranferService.infos.clear();
		try{
			for(int i=0;i<jsonArray.length();++i){
				jsonObject = jsonArray.getJSONObject(i);
				//将得到的两类数据存到List集合中
				TranferService.infos.add(new MyInfo(jsonObject.getString("ip"), jsonObject.getString("name"), 
						jsonObject.getInt("screenwidth"),jsonObject.getInt("screenheight")));
			}
			
		} catch(JSONException exception){
			exception.printStackTrace();
		}
		//利用Handler将消息更新到activity中
		System.out.println("收到消息"+TranferService.infos.size());
		service.getHandler().sendEmptyMessage(TranferController.TYPE_INFOS);
	}
	
	/**
	 * 处理下个回合开始的信息
	 * @param round 下个回合画的人的ip
	 */
	protected void handleRound(int round,String nextip,int roundTotal){
		Message msg = service.getHandler().obtainMessage(TranferController.TYPE_ROUND_AND_IP);
		Bundle bundle = new Bundle();
		bundle.putInt("round",round);
		bundle.putString("nextip",nextip);
		bundle.putInt("roundTotal", roundTotal);
		msg.setData(bundle);
		service.getHandler().sendMessage(msg);
	}
	
	/**
	 * 处理题目信息
	 * @param answer 题条
	 * @param hint1  提示1
	 * @param hint2 提示2
	 */
	protected void handleAnswer(String answer,String hint1,String hint2){
		Message msgMessage = service.getHandler().obtainMessage(TranferController.TYPE_ANSWER);
		Bundle dataBundle = new Bundle();
		dataBundle.putString("answer", answer);
		dataBundle.putString("hint1", hint1);
		dataBundle.putString("hint2", hint2);
		msgMessage.setData(dataBundle);
		service.getHandler().sendMessage(msgMessage);
	}
	
	/**
	 * 处理路径
	 * @param myPath 路径
	 */
	protected void handlePath(JSONObject jsonObject) {
		System.out.println("接收到了路径");
		Message message = service.getHandler().obtainMessage(TranferController.TYPE_MYPATH);
		Bundle bundle = new Bundle();
		bundle.putParcelable("path", JsonManager.parseJsonToMyPath(jsonObject));
		message.setData(bundle);
		service.getHandler().sendMessage(message);
	}
	
	/**
	 * 处理有人答对
	 * @param ip 答对的人的ip
	 * @param score 答对的人得到的分数
	 */
	protected void handleRight(String ip){ //,int score){
		Message msgMessage = service.getHandler().obtainMessage(TranferController.TYPE_RIGHT_IP);
		//在此处理数据，将此ip的人的分数加上
		
		//将答对人的昵称和得到的分数发送到activity中
		Bundle bundle = new Bundle();
		bundle.putString("ip",ip);
//		bundle.putInt("score", score);
		msgMessage.setData(bundle);
		service.getHandler().sendMessage(msgMessage);
	}
	
	/**
	 * 处理有人答错
	 * @param ip 答错的人的ip
	 * @param wronganswer 答错的人所答的答案
	 */
	protected void handleWrong(String ip , String wronganswer){
		Message msgMessage = service.getHandler().obtainMessage(TranferController.TYPE_WRONG_IP);
		Bundle bundle = new Bundle();
		bundle.putString("wrongip", ip);
		bundle.putString("wronganswer", wronganswer);
		msgMessage.setData(bundle);
		service.getHandler().sendMessage(msgMessage);
		
	}
	/**
	 * 处理画的人放弃
	 */
	protected void handleGiveup(){
//		service.getHandler().sendEmptyMessage(TranferController.TYPE_GIVEUP);
	}
	
	/**
	 * 处理回合结束
	 */
	protected void handleRoundOver(int reason){
		Message msgMessage = service.getHandler().obtainMessage(TranferController.TYPE_ROUND_OVER);
		Bundle bundle = new Bundle();
		bundle.putInt("reason", reason);
		msgMessage.setData(bundle);
		service.getHandler().sendMessage(msgMessage);
	}
	
	/**
	 * 处理扔东西的动画
	 * @param animation_type
	 */
	protected  void  handleAnimation(String ip,int animation_type) {
		Message msgMessage = service.getHandler().obtainMessage(TranferController.TYPE_ANIMATION);
		Bundle bundle = new Bundle();
		bundle.putInt("animation", animation_type);
		msgMessage.setData(bundle);
		service.getHandler().sendMessage(msgMessage);
	}
	
	/**
	 * 处理清屏信息
	 */
	protected void handleClear() {
		service.getHandler().sendEmptyMessage(TranferController.TYPE_CLEAR);
	}
	/**
	 * 处理游戏结束
	 */
	protected void handleGameOver() {
		service.getHandler().sendEmptyMessage(TranferController.TYPE_GAME_OVER);
	}
	
	
	/**
	 * 处理有人退出
	 * @param ip 退出人的ip
	 */
	protected void  handleExit(String ip,boolean isServer,boolean isDrawer) {
		Message msgMessage = service.getHandler().obtainMessage(TranferController.TYPE_EXIT);
		Bundle bundle = new Bundle();
		bundle.putString("ip", ip);
		bundle.putBoolean("isserver", isServer);
		bundle.putBoolean("isdrawer", isDrawer);
		msgMessage.setData(bundle);
		service.getHandler().sendMessage(msgMessage);
	}
	
	//发送自己退出的消息
	public void sendExit(String ip,boolean isServer,boolean isDrawer){
		service.getClientSocket().getChat().write(JsonManager.createExit(TranferController.TYPE_EXIT, ip, isServer,isDrawer));
	}
	
	/**
	 * 发送自己的个人信息
	 * @param ip 自己的ip
	 * @param name 自己的名字
	 */
	public void sendMyInfo(String ip,String name) {
		Context context = service.getApplicationContext();
		service.getClientSocket().getChat().write(JsonManager.createMyInfo
				(TranferController.TYPE_INFO, ip, name,DisplayUtil.getScreenWidth(context),DisplayUtil.getScreenHeight(context)));
	}
	
	/**
	 * 发送题目的相关信息
	 * @param answer 词条
	 * @param hint1 提示1
	 * @param hint2 提示2
	 */
	public void sendAnswer(String answer,String hint1,String hint2){
		System.out.println("客户端发送了信息");
		String jsonString = JsonManager.createQuestionInfo(TranferController.TYPE_ANSWER, answer, hint1, hint2);
		service.getClientSocket().getChat().write(jsonString);
	}
	
	/**
	 * 发送自己答对的信息
	 * @param ip 自己的ip
	 * @param score 自己答对的分数
	 */
	public void sendRight(String ip,int score) {
		String jsonString = JsonManager.createRightJson(TranferController.TYPE_RIGHT_IP, ip, score);
		service.getClientSocket().getChat().write(jsonString);
	}

	/**
	 * 发送自己答错的消息
	 * @param ip 自己的ip
	 * @param wrong 自己答错的内容
	 */
	public void sendWrong(String ip,String wrong){
		String jsonString = JsonManager.createWrongJson(TranferController.TYPE_WRONG_IP, ip, wrong);
		service.getClientSocket().getChat().write(jsonString);
	}
	
	/**
	 * 发送画的路径
	 * @param myPath 路径
	 */
	public void sendPath(MyPath myPath){
		String jsonString = JsonManager.createPath(TranferController.TYPE_MYPATH, myPath);
		service.getClientSocket().getChat().write(jsonString);
	}
	
	/**
	 * 发送动画的类型
	 * @param animation_type  动画的类型
	 */
	public void sendAnimation(String ip ,int animation_type){
		String jsonString = JsonManager.createAnimation(TranferController.TYPE_ANIMATION,ip, animation_type);
		service.getClientSocket().getChat().write(jsonString);
	}

	/**
	 * 发送自己返回上一界面的消息
	 * @param ip
	 */
	public void sendBack(String ip){
		String jsonString = JsonManager.createBack(TranferController.TYPE_BACK, ip,false);
		service.getClientSocket().getChat().write(jsonString);
	}
	
	/**
	 * 发送清屏的消息
	 */
	public void sendClear(){
		String string = JsonManager.createClear(TranferController.TYPE_CLEAR);
		service.getClientSocket().getChat().write(string);
	}
	
	public void sendGiveUp(){
		String string = JsonManager.createGiveup(TranferController.TYPE_GIVEUP);
		service.getClientSocket().getChat().write(string);
	}
	
}
