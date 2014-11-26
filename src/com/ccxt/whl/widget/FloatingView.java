package com.ccxt.whl.widget;

import java.util.List;


import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ccxt.whl.R;  

public class FloatingView extends View {
	private static final String TAG = "TableShowView";


	public static final int STAT_READY_GENERAL = 1; // 表示已进入普通通话状态
	public static final int STAT_OPER_BUILD = 3; // 开始建立安全信道
	public static final int STAT_OPER_CHECK = 4; // 开始验证身份
	public static final int STAT_READY_SECURE = 5; // 表示进入加密通话状态
	public static final int STAT_OPER_DES = 6;
	
	Context mContext;
	WindowManager mWindowManager; // WindowManager
	ActivityManager mActivityManager; // 根据当前Activity来处理控制界面的隐藏
	List<ActivityManager.RunningTaskInfo> initTaskInfo;
	OnCtrlViewTouchListener mCtrlViewTouchListener;
	UpdateStatHander mUpdateStatusHandler; // 用于更新当前通话状态的Handler
	View mCtrlView;
	View mDetailView;

	long operDur; // 用于记录用户点击控件的时间，如果Touch控件的时间较短，就认为这Touch是一次点击
	public FloatingView(Context context) {
		super(context);
		mContext = context;
		mWindowManager = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
	}

	public void showFloatingBtn() {
		// 设置载入view WindowManager参数
		initTaskInfo = mActivityManager.getRunningTasks(1);
		
		mCtrlViewTouchListener = new OnCtrlViewTouchListener(mWindowManager,
				showCtrlView(mWindowManager));
		// mCtrlViewLayoutParams = showCtrlView(mWindowManager);
		mCtrlView.setOnTouchListener(mCtrlViewTouchListener);
		mUpdateStatusHandler = new UpdateStatHander();
		
		removeCtrlViewByTopActivityChag();
	}

	private class OnCtrlViewTouchListener implements OnTouchListener {
		private static final long MAX_MILLI_TREAT_AS_CLICK = 100;  //当用户触控控制按钮的时间小于该常量毫秒时，就算控制按钮的位置发生了变化，也认为这是一次点击事件
		
		private WindowManager mWindowManager;
		private WindowManager.LayoutParams mLayoutParams;
		// 触屏监听
		float mLastX, mLastY;

		int mOldOffsetX, mOldOffsetY;
		int mRecordFlag = 0; // 用于重新记录CtrlView位置的标志
		long mTouchDur;  //记录用户触控控制按钮的时间
		
		boolean hasShowedDetail = false;

		public OnCtrlViewTouchListener(WindowManager windowManager,
				WindowManager.LayoutParams layoutParams) {
			mWindowManager = windowManager;
			mLayoutParams = layoutParams;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			final int action = event.getAction();

			float x = event.getX();
			float y = event.getY();

			if (mRecordFlag == 0) {
				mOldOffsetX = mLayoutParams.x; // 偏移量
				mOldOffsetY = mLayoutParams.y; // 偏移量
			}

			if (action == MotionEvent.ACTION_DOWN) {
				mLastX = x;
				mLastY = y;
				mTouchDur = System.currentTimeMillis();

			} else if (action == MotionEvent.ACTION_MOVE) {
				mLayoutParams.x += (int) (x - mLastX); // 偏移量
				mLayoutParams.y += (int) (y - mLastY); // 偏移量

				mRecordFlag = 1;
				mWindowManager.updateViewLayout(mCtrlView, mLayoutParams);
			}

			else if (action == MotionEvent.ACTION_UP) {
				mTouchDur =  System.currentTimeMillis() - mTouchDur;
				int newOffsetX = mLayoutParams.x;
				int newOffsetY = mLayoutParams.y;
				if (mTouchDur < MAX_MILLI_TREAT_AS_CLICK || (mOldOffsetX == newOffsetX && mOldOffsetY == newOffsetY)) {
					if (hasShowedDetail == false) {
						if (mDetailView == null) {
							showDetailView(mWindowManager);
						} else {
							mDetailView.setVisibility(VISIBLE);
						}
						hasShowedDetail = true;
					} else {
						mDetailView.setVisibility(INVISIBLE);
						hasShowedDetail = false;
					}
				} else {
					mRecordFlag = 0;
				}
			}
			return true;
		}

		/**
		 * 该方法会显示详情视图
		 * 
		 * @param windowManager
		 *            用于控制通话状态标示出现的初始位置（默认居中）、大小以及属性
		 * @return 会返回所创建的控制按钮的WindowManager.LayoutParams型对象。
		 */
		private WindowManager.LayoutParams showDetailView(
				WindowManager windowManager) {
			mDetailView = LayoutInflater.from(mContext).inflate(
					R.layout.detail_window, null);
			mDetailView.setBackgroundColor(Color.TRANSPARENT);

			setDetailBtnsListener();
						
			WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
			//layoutParams.type = 2003; // type是关键，这里的2002表示系统级窗口，你也可以试试2003。
			layoutParams.type = 99; // type是关键，这里的2002表示系统级窗口，你也可以试试2003。
			layoutParams.flags = 40;// 这句设置桌面可控
			layoutParams.format = -3; // 透明
			layoutParams.width = 400;
			layoutParams.height = 230;
			windowManager.addView(mDetailView, layoutParams);
			return layoutParams;
		}

		private void setDetailBtnsListener() {
			Button chgBtn = (Button) mDetailView
					.findViewById(R.id.btn_chg_stat); // 设置改变通话状态按钮的监听器
			chgBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					changCallingStat((Button) v);
				}
			});
			Button hideBtn = (Button) mDetailView.findViewById(R.id.btn_hide); // 设置隐藏按钮的监听器
			hideBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mDetailView.setVisibility(INVISIBLE);
					hasShowedDetail = false;
				}
			});
			Button removeBtn = (Button) mDetailView.findViewById(R.id.btn_remove);
			removeBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					mWindowManager.removeView(mDetailView);
					mWindowManager.removeView(mCtrlView);
				}
			});
		}
		
		private void changCallingStat(Button button) {
			TextView currStat = (TextView) mDetailView
					.findViewById(R.id.tv_curr_stat);

			if ("".equals(
					currStat.getText().toString())) {
				button.setEnabled(false);
				sendUpdateMsg(mUpdateStatusHandler, STAT_OPER_BUILD, 0);
				sendUpdateMsg(mUpdateStatusHandler, STAT_OPER_CHECK, 2);
				sendUpdateMsg(mUpdateStatusHandler, STAT_READY_SECURE, 3);
			} else {
				button.setEnabled(false);
				sendUpdateMsg(mUpdateStatusHandler, STAT_OPER_DES, 0);
				sendUpdateMsg(mUpdateStatusHandler, STAT_READY_GENERAL, 4);
			}
		}

		/**
		 * 该方法用于向更新通话状态的Handler发送消息
		 * 
		 * @param handler
		 * @param status
		 			  所发送的状态信息
		 * @param seconds
		 *            几秒后向handler发送消息
		 */
		private void sendUpdateMsg(Handler handler, int status, int seconds) {
			final Message msg = Message.obtain(handler);
			msg.what = status;
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					msg.sendToTarget();
				}
			}, seconds * 1000);

		}
	}

	/**
	 * 用于显示控制按钮，该控制按钮可以移动。当点击一次之后会弹出另一个浮动界面，本例中是详情界面
	 * 
	 * @param windowManager
	 *            用于控制通话状态标示出现的初始位置、大小以及属性
	 * @return 会返回所创建的通话状态标示的WindowManager.LayoutParams型对象，该对象会在移动过程中修改。
	 */
	private WindowManager.LayoutParams showCtrlView(WindowManager windowManager) {
		mCtrlView = LayoutInflater.from(mContext).inflate(R.layout.ctrl_window,
				null);
		mCtrlView.setBackgroundColor(Color.TRANSPARENT);

		WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
		layoutParams.type = 2003; // type是关键，这里的2002表示系统级窗口，你也可以试试2003。
		layoutParams.flags = 40;// 这句设置桌面可控
		layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
		layoutParams.x = 60;
		layoutParams.y = 80;
		layoutParams.width = 80;
		layoutParams.height = 80;
		layoutParams.format = -3; // 透明

		windowManager.addView(mCtrlView, layoutParams);// 这句是重点
														// 给WindowManager中丢入刚才设置的值
		// 只有addview后才能显示到页面上去。
		// 注册到WindowManager win是要刚才随便载入的layout，
		// wmParams是刚才设置的WindowManager参数集
		// 效果是将win注册到WindowManager中并且它的参数是wmParams中设置饿
		return layoutParams;
	}

	private class UpdateStatHander extends Handler {

		@Override
		public void handleMessage(Message msg) {
			TextView currStat = (TextView) mDetailView
					.findViewById(R.id.tv_curr_stat);
			Button changButton = (Button) mDetailView
					.findViewById(R.id.btn_chg_stat);
			ImageView statImg = (ImageView) mCtrlView
					.findViewById(R.id.img_stat);
			switch (msg.what) {
			case STAT_READY_GENERAL:
				currStat.setText(mContext.getString(R.string.stat_gen));
				//statImg.setBackgroundResource(R.drawable.stat_gen);
				changButton.setEnabled(true);
				break;
			case STAT_OPER_BUILD:
				currStat.setText(mContext.getString(R.string.stat_build));
				break;
			case STAT_OPER_CHECK:
				currStat.setText(R.string.stat_chk_auth);
				break;
			case STAT_READY_SECURE:
				currStat.setText(mContext.getString(R.string.stat_sec));
				//statImg.setBackgroundResource(R.drawable.stat_sec);
				 changButton.setEnabled(true);
				break;
			case STAT_OPER_DES:
				currStat.setText(mContext.getString(R.string.stat_des));
				break;
			}
		}
	}
	
	/**
	 * 该方法会在第5、10、15秒检测topActivity是否是发生了变化，如果发生了变化就移除浮动控制按钮
	 * 
	 */
	private void removeCtrlViewByTopActivityChag() {
		for(int i = 0; i < 5; i++) {
			
			mUpdateStatusHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					String currPackName= mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName(); //得到正运行的App包名
					String initPackName = initTaskInfo.get(0).topActivity.getPackageName(); //得到显示浮动控制按钮时的App包名
					Log.w(TAG, "oldPackageName: " + initPackName 
							+ "  currPackageName: " + currPackName);
					if(!currPackName.equals(initPackName)) {
						
						if(mDetailView != null) {
							mWindowManager.removeView(mDetailView);
							mDetailView = null;
						}
						if(mCtrlView != null) {
							mWindowManager.removeView(mCtrlView);					
							mCtrlView = null;
						}						
					}					
				}				
			}, 3 * (i + 1) * 1000);
		}
	}
}
