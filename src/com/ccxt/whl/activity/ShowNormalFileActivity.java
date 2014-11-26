package com.ccxt.whl.activity;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ccxt.whl.R;
import com.easemob.chat.EMChatConfig;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.FileMessageBody;
import com.easemob.cloud.CloudOperationCallback;
import com.easemob.cloud.HttpFileManager;
import com.easemob.util.FileUtils;

public class ShowNormalFileActivity extends BaseActivity {
	private ProgressBar progressBar;
	private File file;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_file);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);

		final FileMessageBody messageBody = getIntent().getParcelableExtra("msgbody");
		file = new File(messageBody.getLocalUrl());
		//set head map
		final Map<String, String> maps = new HashMap<String, String>();
		//maps.put("Authorization", "Bearer " + EMChatConfig.getInstance().AccessToken);
		maps.put("Authorization", "Bearer " + EMChatManager.getInstance().getAccessToken());
		if (!TextUtils.isEmpty(messageBody.getSecret())) {
			maps.put("share-secret", messageBody.getSecret());
		}
		maps.put("Accept", "application/octet-stream");
		
		//下载文件
		new Thread(new Runnable() {
			public void run() {
				HttpFileManager fileManager = new HttpFileManager(ShowNormalFileActivity.this, EMChatConfig.getInstance().getStorageUrl());
				fileManager.downloadFile(messageBody.getRemoteUrl(), messageBody.getLocalUrl(), EMChatConfig.getInstance().APPKEY,maps,
						new CloudOperationCallback() {
							
							@Override
							public void onSuccess(String result) {
								runOnUiThread(new Runnable() {
									public void run() {
										FileUtils.openFile(file, ShowNormalFileActivity.this);
										finish();
									}
								});
							}
							
							@Override
							public void onProgress(final int progress) {
								runOnUiThread(new Runnable() {
									public void run() {
										progressBar.setProgress(progress);
									}
								});
							}
							
							@Override
							public void onError(final String msg) {
								runOnUiThread(new Runnable() {
									public void run() {
										if(file != null && file.exists())
											file.delete();
										Toast.makeText(ShowNormalFileActivity.this, "下载文件失败: "+msg, 0).show();
										finish();
									}
								});
							}
						});

			}
		}).start();
		
	}
}
