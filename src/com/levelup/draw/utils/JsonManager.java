package com.levelup.draw.utils;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Point;
import android.graphics.Rect;

import com.levelup.draw.data.MyInfo;
import com.levelup.draw.data.MyPath;
import com.levelup.draw.tranfer.TranferController;

public class JsonManager {


	/**
	 * 创建自己信息的字符串
	 * @param ip 自己的ip
	 * @param type 表示自己信息数据类型
	 * @param name 自己的名字
	 * @return 一个json格式的字符串
	 */
	public static String createMyInfo(int type, String ip, String name,int screenWidth,int screenHeight) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("ip", ip);
			jsonObject.put("type", type);
			jsonObject.put("name", name);
			jsonObject.put("screenwidth", screenWidth);
			jsonObject.put("screenheight", screenHeight);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	/**
	 * 创建一个发送所有人信息的json格式的字符串
	 * 
	 * @param ip
	 * @param type
	 *            数据类型对应的整数
	 * @param ips
	 *            ip的集合
	 * @param names
	 *            name的集合
	 * @return json格式的字符串
	 */
	public static String createInfosJson(int type, List<MyInfo> infos) {
		JSONObject jsonObject = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			jsonObject.put("type", type);
			if (infos != null) {
				for (MyInfo info : infos) {
					JSONObject json = new JSONObject();
					json.put("ip", info.getIP());
					json.put("name", info.getName());
					json.put("screenwidth", info.getScreenWidth());
					json.put("screenheight", info.getScreenHeight());
					jsonArray.put(json);
				}
			}
			jsonObject.put("infos", jsonArray);
		} catch (JSONException exception) {
			exception.printStackTrace();
		}
		return jsonObject.toString();
	}

	/**
	 * 创建回合的信息字符串
	 * 
	 * @param type
	 *            数据类型
	 * @param round
	 *            回合信息
	 * @param nextip
	 *            下一个画的人的ip
	 * @return
	 */
	public static String createRoundJson(int type, int round, int roundTotal,
			String nextip) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
			jsonObject.put("nextip", nextip);
			jsonObject.put("round", round);
			jsonObject.put("roundTotal", roundTotal);
		} catch (JSONException exception) {
			exception.printStackTrace();
		}
		return jsonObject.toString();
	}

	/**
	 * 创建一个答错的json串
	 * 
	 * @param ip
	 *            答错人的ip
	 * @param type
	 *            表示答错的数据类型对应的整数
	 * @param wronganswer
	 *            答的错误答案
	 * @return 一个答错的json格式的字符串
	 */
	public static String createWrongJson(int type, String ip, String wronganswer) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
			jsonObject.put("wrongip", ip);
			jsonObject.put("wronganswer", wronganswer);
		} catch (JSONException exception) {
			exception.printStackTrace();
		}
		return jsonObject.toString();
	}

	/**
	 * 创建一个答对信息的字符串
	 * 
	 * @param type
	 *            数据类型
	 * @param ip
	 *            答对的人的ip
	 * @param score
	 *            答对的人得到的分数
	 * @return
	 */
	public static String createRightJson(int type, String ip, int score) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
			jsonObject.put("rightip", ip);
			jsonObject.put("score", score);
		} catch (JSONException exception) {
			exception.printStackTrace();
		}
		return jsonObject.toString();
	}

	/**
	 * 创建问题信息的字符串
	 * 
	 * @param type
	 *            表示问题信息数据类型的整数
	 * @param answer
	 *            问题的答案
	 * @param hint1
	 *            问题的第一个提示
	 * @param hint2
	 *            问题的第二个提示
	 * @return 问题信息的json格式的字符串
	 */
	public static String createQuestionInfo(int type, String answer,
			String hint1, String hint2) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
			jsonObject.put("answer", answer);
			jsonObject.put("hint1", hint1);
			jsonObject.put("hint2", hint2);
		
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}

	/**
	 * 创建路径的字符串
	 * 
	 * @param type
	 *            数据类型
	 * @param myPath
	 *            路径
	 * @return
	 */
	public static String createPath(int type, MyPath myPath) {
		JSONObject jsonObject = new JSONObject();
		JSONObject pathObject = new JSONObject();
		JSONObject rect = new JSONObject();
		JSONArray pintsArray = new JSONArray();
		try {
			Rect rect_path = myPath.getRect();
			// 矩形
			rect.put("top", rect_path.top);
			rect.put("right", rect_path.right);
			rect.put("bottom", rect_path.bottom);
			rect.put("left", rect_path.left);

			// 点的集合
			List<Point> points = myPath.getPointArray();
			for (Point p : points) {
				pintsArray.put(p.x);
				pintsArray.put(p.y);
			}

			// 颜色
			pathObject.put("color", myPath.getPaintColor());
			// 宽度
			pathObject.put("width", myPath.getPaintWidth());
			// d矩形
			pathObject.put("rect", rect);
			// 点
			pathObject.put("points", pintsArray);

			jsonObject.put("type", type);
			jsonObject.put("path", pathObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	/**
	 * 组装一个数据为mypath的json串
	 * @param type 类型
	 * @param jsonObject 一个已经能够包装好的mypath的jsonobeject
	 * @return
	 */
	public static String createPath(int type,JSONObject jsonObject){
		JSONObject json = new JSONObject();
		try{
			json.put("type", type);
			json.put("path", jsonObject);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return json.toString();
	}

	public static MyPath parseJsonToMyPath(JSONObject pathObject) {
		MyPath path = new MyPath();
		try {
			// 获得线的宽度和颜色
			path.setPaintColorWidth(pathObject.getInt("color"),
					pathObject.getInt("width"));
			// 获得矩形
			JSONObject rectObject = pathObject.getJSONObject("rect");
			Rect rect = new Rect(rectObject.getInt("left"),
					rectObject.getInt("top"), rectObject.getInt("right"),
					rectObject.getInt("bottom"));
			path.setRect(rect);
			// 获得点的集合
			List<Point> points = path.getPointArray();
			JSONArray pointsArray = pathObject.getJSONArray("points");
			for (int i = 0; i < pointsArray.length(); i += 2) {
				points.add(new Point(pointsArray.getInt(i), pointsArray
						.getInt(i + 1)));
			}
		} catch (JSONException jsonException) {
			jsonException.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return path;
	}

	/**
	 * 发送回合结束
	 * 
	 * @param type
	 * @return
	 */
	public static String createRoundOver(int type,int reason) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
			jsonObject.put("reason", reason);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	/**
	 * 游戏结束
	 * @param type
	 * @return
	 */
	public static String createGameOver(int type) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	/**
	 * 回合结束后的动画
	 * @param type 数据类型
	 * @param ip 发送动画的人的ip
	 * @param animation_type 动画的类型
	 * @return 
	 */
	public static String createAnimation(int type,String ip,int animation_type) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("type", type);
			jsonObject.put("ip", ip);
			jsonObject.put("animation", animation_type);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	/**
	 * 创建一个玩家退出的json串
	 * @param type
	 * @param ip
	 * @return
	 */
	public static String createExit(int type,String ip,boolean isServer,boolean isDrawer){
		JSONObject jsonObject = new JSONObject();
		try{
			jsonObject.put("type", TranferController.TYPE_EXIT);
			jsonObject.put("ip", ip);
			jsonObject.put("isserver", isServer);
			jsonObject.put("isdrawer", isDrawer);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	/**
	 * 创建一个退出房间的json串
	 * @param type 类型
	 * @param ip 自己的ip
	 * @param isServer 是不是房主的标记
	 * @return
	 */
	public static String createBack(int type,String ip,boolean isServer){
		JSONObject jsonObject = new JSONObject();
		try{
			jsonObject.put("type", TranferController.TYPE_BACK);
			jsonObject.put("ip", ip);
			jsonObject.put("isserver", isServer);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	
	/**
	 * 创建一个用户放弃的json串
	 * @param type 放弃的类型
	 * @param ip 放弃的人的ip
	 * @return 用户放弃的字符串
	 */
	public static String createGiveup(int type){
		JSONObject jsonObject = new JSONObject();
		try{
			jsonObject.put("type", TranferController.TYPE_GIVEUP);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	/**
	 * 创建一个清屏的json字符串
	 * @param type
	 * @return
	 */
	public static String createClear(int type){
		JSONObject jsonObject = new JSONObject();
		try{
			jsonObject.put("type", TranferController.TYPE_CLEAR);
		}catch(JSONException e){
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
}
