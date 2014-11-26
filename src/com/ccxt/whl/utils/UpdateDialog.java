/**
 * @Title: UpdateDialog.java
 * @Package com.baidu.utest.update
 * @Description: TODO Copyright: Copyright (c) 2013 Company: Baidu
 * @date 2013-7-7 下午6:27:45
 * @version 1.0
 */
package com.ccxt.whl.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.baidu.kirin.KirinConfig;
import com.baidu.kirin.PostChoiceListener;
import com.baidu.kirin.StatUpdateAgent;

public class UpdateDialog {

	private Context mContext = null;
	private String mAppName = null;
//	private final PostChoiceListener mmPostChoiceListener;

	public UpdateDialog(Context context, String appName) {
//			PostChoiceListener _mPostUpdateChoiceListener) {
		mContext = context;
		this.mAppName = appName;
//		mmPostChoiceListener = _mPostUpdateChoiceListener;

	}

	public void doUpdate(String downloadUrl, String content) {
		showNewerVersionFoundDialog(downloadUrl, content);
	}

	private void showNewerVersionFoundDialog(final String downloadUrl,
			String content) {
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle(mAppName);
		builder.setMessage(content);

		builder.setPositiveButton("现在升级", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//将用户选择反馈给服务器
//				StatUpdateAgent.postUserChoice(mContext,
//						KirinConfig.CONFIRM_UPDATE, mmPostChoiceListener);
				Log.d("demodemo", "postUserChoice CONFIRM_UPDATE");
				Uri uri = Uri.parse(downloadUrl);    
				Intent it = new Intent(Intent.ACTION_VIEW, uri);    
				mContext.startActivity(it);  
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("暂不升级", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//将用户选择反馈给服务器
//				StatUpdateAgent.postUserChoice(mContext,
//						KirinConfig.LATER_UPDATE, mmPostChoiceListener);
				Log.d("demodemo", "postUserChoice LATER_UPDATE");
				dialog.dismiss();
			}
		});

		dialog = builder.create();
		dialog.show();
	}
}
