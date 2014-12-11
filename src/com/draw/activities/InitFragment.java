package com.draw.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.draw.R;
import com.levelup.draw.tranfer.ServerPlayer;
import com.levelup.draw.tranfer.TranferService;
import com.levelup.draw.utils.PlayMusicUtil;

public class InitFragment extends Fragment implements OnClickListener {

	boolean showButton = false;
	View view = null;

	Button start;
	Button quit;
	Button round;

	TextView name;
	TextView roundText;
	TextView[] player = new TextView[6];
	int roundNum;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		if (bundle != null) {
			showButton = bundle.getBoolean("showButton");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.activity_second, container, false);
		initViews();
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

	}

	private void initViews() {

		roundNum = 0;

		start = (Button) view.findViewById(R.id.second_start);
		quit = (Button) view.findViewById(R.id.second_quit);
		round = (Button) view.findViewById(R.id.second_round_bg);

		name = (TextView) view.findViewById(R.id.second_room_name);
		roundText = (TextView) view.findViewById(R.id.second_round_text);

		player[0] = (TextView) view.findViewById(R.id.second_player1);
		player[1] = (TextView) view.findViewById(R.id.second_player2);
		player[2] = (TextView) view.findViewById(R.id.second_player3);
		player[3] = (TextView) view.findViewById(R.id.second_player4);
		player[4] = (TextView) view.findViewById(R.id.second_player5);
		player[5] = (TextView) view.findViewById(R.id.second_player6);

		start.setOnClickListener(this);
		quit.setOnClickListener(this);
		round.setOnClickListener(this);

		if (!showButton) {
			start.setVisibility(View.GONE);
			roundText.setVisibility(View.GONE);
			round.setClickable(false);
		} else {
			String s = DrawApplication.getApplicationInstance().getUsername();
			name.setText(s + "的房间");
			player[0].setText(s);
		}
		refresh();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.second_start:
			PlayMusicUtil.playMusic(getActivity(),
					PlayMusicUtil.MUSIC_BUTTON_OPEN);
			pushStart();
			break;
		case R.id.second_quit:
			if (TranferService.player != null) {
				PlayMusicUtil.playMusic(getActivity(),
						PlayMusicUtil.MUSIC_BUTTON_CLICK);
				TranferService.player.sendBack(DrawApplication
						.getApplicationInstance().getMyIp());
				// 退回到最开始的程序
				getActivity().finish();
			}
			break;
		case R.id.second_round_bg:
			PlayMusicUtil.playMusic(getActivity(),
					PlayMusicUtil.MUSIC_BUTTON_CLICK);
			createInputRound();
			break;
		default:
			break;
		}

	}

	private void pushStart() {
		if (roundNum == 0) {
			Toast.makeText(getActivity(), "请输入回合数", 500).show();
		} else {// 将回合信息发送过去
			((ServerPlayer) TranferService.player).sendRound(roundNum);
		}
	}

	private void createInputRound() {
		// TODO Auto-generated method stub
		final Dialog d = new Dialog(getActivity(), R.style.MyDialog_NoBG);
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout layout = (RelativeLayout) inflater.inflate(
				R.layout.second_round, null);
		d.setContentView(layout);

		final EditText et = (EditText) layout
				.findViewById(R.id.second_get_round);
		layout.findViewById(R.id.second_dialog_ok).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						PlayMusicUtil.playMusic(getActivity(),
								PlayMusicUtil.MUSIC_BUTTON_CLICK);
						roundText.setText("回合数:" + et.getText().toString());
						roundNum = Integer.parseInt(et.getText().toString()
								.trim());
						et.setText("");
						d.cancel();
					}
				});

		layout.findViewById(R.id.second_dialog_no).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						PlayMusicUtil.playMusic(getActivity(),
								PlayMusicUtil.MUSIC_BUTTON_CLICK);
						d.cancel();
					}
				});
		d.show();
	}

	// 刷新listview
	public void refresh() {
		PlayMusicUtil.playMusic(getActivity(), PlayMusicUtil.MUSIC_ROOM_ENTER);
		if (TranferService.infos.size() != 0)
			name.setText(TranferService.infos.get(0).getName() + "的房间");
		for (int i = 0; i < TranferService.infos.size(); i++) {
			player[i].setText(TranferService.infos.get(i).getName());
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

}
