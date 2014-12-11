package com.draw.activities;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.draw.R;
import com.levelup.draw.customview.GuessCanvaView;
import com.levelup.draw.data.MyInfo;
import com.levelup.draw.data.MyPath;
import com.levelup.draw.gif.GifView;
import com.levelup.draw.tranfer.TranferController;
import com.levelup.draw.tranfer.TranferService;
import com.levelup.draw.utils.PlayMusicUtil;
import com.levelup.draw.utils.SettingUtil;

public class GuessFragment extends Fragment implements OnClickListener {

	private View view;

	Button answer;
	Button menu;
	EditText answerText;
	TextView round, point, title, time, message;
	RelativeLayout final_layout;
	int first_show_place;

	LinearLayout liner;
	GuessCanvaView myView;

	// 获得的数据
	String drawerIP = "", myIp, rightAnswer = "", hiht1 = "", hiht2 = "";
	int width, height, iround, iroundTotal, myPoints;
	boolean noBodyGetIt = true;

	// 倒计时
	Timer timer = new Timer();
	int time_count = 60; // 时间的流逝计算
	
	Dialog d =null;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		myIp = DrawApplication.getApplicationInstance().getMyIp();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Bundle b = getArguments();
		if (b != null) {
			drawerIP = b.getString("nextip");
			iround = b.getInt("round");
			iroundTotal = b.getInt("roundTotal");
		}
		for (MyInfo info : TranferService.infos) 
		{
			if (info.getIP().equals(drawerIP)) {
				width = info.getScreenWidth();
				height = info.getScreenHeight();
				break;
			}
		}
		for(MyInfo info : TranferService.infos)
		{
			if(info.getIP().equals(myIp) )
			{
				myPoints = info.getPoint();
				break;
			}
		}
		
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				getActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (time != null) {
							time.setText(time_count-- + "");
							if( time_count == 50 )
							{
								String s = title.getText().toString();
								s += ( ","+hiht1 );
								title.setText(s);
							}
							else if(time_count == 40)
							{
								String s = title.getText().toString();
								s += ( ","+hiht2 );
								title.setText(s);
							}
							
							
							if (time_count <= 10) {
								// 执行动画
								Animation animation = AnimationUtils
										.loadAnimation(getActivity(),
												R.anim.bigsmall);
								time.startAnimation(animation);
							}
						}
					}
				});
			}
		}, new Date(), 1000);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.activity_guess, container, false);
		initViews();
		return view;
	}

	private void initViews() {
		myView = new GuessCanvaView(getActivity(), width, height);
		myView.setBackgroundColor(Color.TRANSPARENT);

		liner = (LinearLayout) view.findViewById(R.id.canva);
		liner.removeAllViews();
		liner.addView(myView);

		answer = (Button) view.findViewById(R.id.answer);
		answer.setOnClickListener(this);
		answerText = (EditText) view.findViewById(R.id.answer_text);
		menu = (Button) view.findViewById(R.id.menu);
		menu.setOnClickListener(this);
		message = (TextView) view.findViewById(R.id.message);
		round = (TextView) view.findViewById(R.id.round);
		point = (TextView) view.findViewById(R.id.point);
		title = (TextView) view.findViewById(R.id.title);
		time = (TextView) view.findViewById(R.id.time);

		round.setText("第" + iround + "/" + iroundTotal + "轮");
		point.setText(myPoints + "分");
		time.setText("60");
		for (MyInfo info : TranferService.infos) {
			if (info.getIP().equals(drawerIP)) {
				title.setText("当前由" + info.getName() + "画图");
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.answer:
			PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_CLICK);
			sendAnswer(answerText.getText().toString());
			answerText.setText("");
			break;
		case R.id.menu:
			PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_OPEN);
			createMenu();
			break;
		default:
			final_layout.findViewById(R.id.final_buttons).setVisibility(
					View.GONE);
			int temp = 0;
			switch (v.getId()) {
			case R.id.final_button_flower:
				temp = TranferController.ANIMATION_FLOWER;
				break;
			case R.id.final_button_kiss:
				temp = TranferController.ANIMATION_KISS;
				break;
			case R.id.final_button_egg:
				temp = TranferController.ANIMATION_EGG;
				break;
			case R.id.final_button_shoe:
				temp = TranferController.ANIMATION_SHOES;
				break;
			}

			TranferService.player.sendAnimation(myIp, temp);

			playGIF(temp);
			break;
		}

	}

	public void playGIF(int id) {
		// TODO Auto-generated method stub
		GifView gif1 = (GifView) final_layout.findViewById(R.id.final_gif1);
		GifView gif2 = (GifView) final_layout.findViewById(R.id.final_gif2);
		GifView gif3 = (GifView) final_layout.findViewById(R.id.final_gif3);
		int gifID = 0;
		switch (id) {
		case TranferController.ANIMATION_FLOWER:
			PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_GIF_FLOWER);
			gifID = R.drawable.gif_flower;
			break;
		case TranferController.ANIMATION_KISS:
			PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_GIF_KISS);
			gifID = R.drawable.gif_kiss;
			break;
		case TranferController.ANIMATION_EGG:
			PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_GIF_EGG);
			gifID = R.drawable.gif_egg;
			break;
		case TranferController.ANIMATION_SHOES:
			PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_GIF_SHOE);
			gifID = R.drawable.gif_shoe;
			break;
		default:
			break;
		}
		if (gif1.getVisibility() == View.INVISIBLE) {
			gif1.setGifImage(gifID);
			gif1.setVisibility(View.VISIBLE);
		} else if (gif2.getVisibility() == View.INVISIBLE) {
			gif2.setGifImage(gifID);
			gif2.setVisibility(View.VISIBLE);
		} else if (gif3.getVisibility() == View.INVISIBLE) {
			gif3.setGifImage(gifID);
			gif3.setVisibility(View.VISIBLE);
		} else {
			switch (first_show_place) {
			case 1:

				gif1.setGifImage(gifID);
				first_show_place = 2;
				break;
			case 2:
				gif2.setGifImage(gifID);
				first_show_place = 3;
				break;
			case 3:
				gif3.setGifImage(gifID);
				first_show_place = 1;
				break;
			default:
				break;
			}
		}
	}

	private void createMenu() {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getApplicationContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.scores, null);

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		SimpleAdapter sa = new SimpleAdapter(getActivity()
				.getApplicationContext(), list, R.layout.scores_item,
				new String[] { "name", "point" },
				// 分别对应view 的id
				new int[] { R.id.item_name, R.id.item_point });

		((ListView) layout.findViewById(R.id.scores_list)).setAdapter(sa);

		int size = TranferService.infos.size();
		int[] scores = new int[size];
		for (int i = 0; i < size; i++)
			scores[i] = i;
		for (int i = 0; i < size; i++) {
			for (int j = size - 1; j > i; j--) {
				if (TranferService.infos.get(scores[j]).getPoint() > TranferService.infos
						.get(scores[j - 1]).getPoint()) {
					int temp = scores[j - 1];
					scores[j - 1] = scores[j];
					scores[j] = temp;
				}
			}
		}

		for (int i = 0; i < size; i++) {
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("name", TranferService.infos.get(scores[i]).getName());
			item.put("point", TranferService.infos.get(scores[i]).getPoint()
					+ "分");
			list.add(item);
		}

		sa.notifyDataSetChanged();

		final Dialog d = new Dialog(getActivity(), R.style.MyDialog);
		d.setContentView(layout);
		d.show();

		if( !DrawApplication.getApplicationInstance().getVolume() )
		{
			layout.findViewById(R.id.scores_volume).setBackgroundResource(R.drawable.volume_off);
		}
		
		layout.findViewById(R.id.scores_resume).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_CLICK);
						d.cancel();
					}
				});

		layout.findViewById(R.id.scores_volume).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_CLICK);
						if(DrawApplication.getApplicationInstance().getVolume())
						{
							v.setBackgroundResource(R.drawable.volume_off);
							SettingUtil.set(getActivity(), "volume", false);
							DrawApplication.getApplicationInstance().setVolume(false);
						}
						else
						{
							v.setBackgroundResource(R.drawable.volume_on);
							SettingUtil.set(getActivity(), "volume", false);
							DrawApplication.getApplicationInstance().setVolume(true);
						}
					}
				});

		layout.findViewById(R.id.scores_quit).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_BUTTON_CLICK);
						d.cancel();
					}
				});
	}

	private void sendAnswer(String string) {
		// TODO Auto-generated method stub
		if(!string.equals(rightAnswer)){
			showMessage("您猜了  "+string); 
		}
		if (string.equals(rightAnswer)) {
			int draweradd = 0;
			if (noBodyGetIt) {
				myPoints += 2;
				draweradd = 3;
				noBodyGetIt = false;
			} else {
				myPoints += 1;
				draweradd = 2;
			}
			point.setText(myPoints + "分");
			PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_GET_RIGHT);
			showMessage("您猜对了");
			for (MyInfo info : TranferService.infos) {
				if (info.getIP().equals(myIp)) {
					info.setPoint(myPoints);
				}
				if (info.getIP().equals(drawerIP)) {
					info.setPoint(info.getPoint() + draweradd);
				}
			}

			// 把答对的消息发送出去
			TranferService.player.sendRight(myIp, 0);

		} else {
			// 把答错的信息发出去
			TranferService.player.sendWrong(myIp, string);
		}
	}

	private void showMessage(String string) {
		// 显示一条内容为string的通知
		message.setText(string);
	}
/*
	private void showRoundOver() {
		// 显示答案，延时消失
	}

	private void showGameOver() {
		// 显示总的分数表，延时跳转到房间
	}*/

	// 处理问题的信息
	public void handleAnswer(Bundle bundle) {
		// 将关于题目的信息保存下来，方便以后自己判断是不是对
		
		rightAnswer = bundle.getString("answer");
		hiht1 = bundle.getString("hint1");
		hiht2 = bundle.getString("hint2");
		System.out.println("收到了了题目信息"+answer+":"+hiht1+","+hiht2);
	}

	// 处理有人答对了的信息
	public void handleRight(Bundle bundle) {
		String rightIpString = bundle.getString("ip");
		String rightNameString = "";
		// 计算得分
		int padd = 0;
		int dadd = 0;

		if (noBodyGetIt) {
			padd = 2;
			dadd = 3;
			noBodyGetIt = false;
		} else {
			padd = 1;
			dadd = 1;
		}
		for (MyInfo info : TranferService.infos) {
			// 给猜对的人加分
			if (info.getIP().equals(rightIpString)) {
				info.setPoint(padd + info.getPoint());
				rightNameString = info.getName();
			}
			// 给画的人加分
			if (info.getIP().equals(drawerIP)) {
				info.setPoint(dadd + info.getPoint());
			}
		}
		showMessage(rightNameString + "猜对了");
	}

	// 处理有人答错的信息
	public void handleWrong(Bundle bundle) {
		String wrongIpString = bundle.getString("wrongip");
		String wrongANswerString = bundle.getString("wronganswer");

		for (MyInfo info : TranferService.infos) {
			if (info.getIP().equals(wrongIpString)) {
				showMessage(info.getName() + "猜的是" + wrongANswerString);
				break;
			}
		}

	}

	// 处理回合结束
	public void handleEnd(int reason) {
		if (reason == TranferController.ALL_RIGHT) {
			showMessage("所有人都答对了，游戏结束");
		} else if (reason == TranferController.TIME_OVER) {
			showMessage("时间到");
		} else if (reason == TranferController.DRAWER_EXIT) {
			showMessage("画的人退出了，此回合结束");
			//在此删除退出的画的人的信息
			for(MyInfo info:TranferService.infos){
				if(info.getIP().equals(drawerIP)){
					TranferService.infos.remove(info);
					break;
				}
			}
		} else if(reason == TranferController.TYPE_GIVEUP){
			showMessage("画的人放弃，回合结束");
		}
		// 当回合结束的时候停止计时

		timer.cancel();
		showFinal();
	}

	private void showFinal() {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final_layout = (RelativeLayout) inflater.inflate(R.layout.show_final,
				null);
		first_show_place = 1;
		((ImageView) final_layout.findViewById(R.id.final_canva_bg))
				.setImageBitmap(myView.surfaceBitmap);
		d = new Dialog(getActivity(), R.style.MyDialog_NoBG);
		d.setContentView(final_layout);
		d.setCanceledOnTouchOutside(false);
		d.show();

		final_layout.findViewById(R.id.final_button_flower).setOnClickListener(
				this);
		final_layout.findViewById(R.id.final_button_kiss).setOnClickListener(
				this);
		final_layout.findViewById(R.id.final_button_egg).setOnClickListener(
				this);
		final_layout.findViewById(R.id.final_button_shoe).setOnClickListener(
				this);
		
	
		((TextView)final_layout.findViewById(R.id.final_answer)).setText("答案:"+rightAnswer);
		for(MyInfo info:TranferService.infos){
			if(info.getIP().equals(drawerIP)){
				((TextView)final_layout.findViewById(R.id.final_name)).setText("这是"+info.getName()+"画的图");
				break;
			}
		}
	}

	// 处理路径
	public void handlePath(Bundle bundle) {
		MyPath path = bundle.getParcelable("path");
		myView.addPath(path);
	}

	public void handleExit(String ip) {
		// TODO Auto-generated method stub
		for (MyInfo info : TranferService.infos) {
			if (info.getIP().equals(ip)) {
				showMessage(info.getName() + "退出了房间");
				break;
			}
		}
	}
	public void handleClear(){
		//执行清屏事件
		System.out.println("-----------handleClear-------------");
		liner.removeAllViews();
		myView = new GuessCanvaView(getActivity(), width, height);
		myView.setBackgroundColor(Color.TRANSPARENT);
		liner.addView(myView);
	}

	// 消失
	public void dismissDialog() {

		if (d != null && d.isShowing()) {
			d.cancel();
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if( myView.surfaceBitmap != null )
		{	
			myView.surfaceBitmap.recycle();
		}
		try{
			timer.cancel();
		}catch(Exception e){
			e.printStackTrace();
		}
		super.onDestroy();
	}

}
