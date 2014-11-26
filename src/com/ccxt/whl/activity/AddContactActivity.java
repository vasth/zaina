/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ccxt.whl.activity;

import java.util.Map;

import org.apache.http.Header;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import com.ccxt.whl.Constant;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.HttpRestClient;
import com.ccxt.whl.utils.ImageOptions;
import com.ccxt.whl.utils.JsonToMapList;
import com.ccxt.whl.utils.PreferenceUtils;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AddContactActivity extends BaseActivity{
	private EditText editText;
	private LinearLayout searchedUserLayout;
	private TextView nameText;
	private Button searchBtn;
	private ImageView avatar;
	private InputMethodManager inputMethodManager;
	private String toAddUsername;
	private ProgressDialog progressDialog;
	
	private BaseJsonHttpResponseHandler responseHandler;
	private String sendaddname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		
		editText = (EditText) findViewById(R.id.edit_note);
		searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
		nameText = (TextView) findViewById(R.id.name);
		searchBtn = (Button) findViewById(R.id.search);
		avatar = (ImageView) findViewById(R.id.avatar);
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		/*************************http请求处理***********************/
		responseHandler = new BaseJsonHttpResponseHandler() {
			   
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String rawJsonResponse, Object response) {
				// TODO Auto-generated method stub 
				Log.d("setting_qes"+rawJsonResponse );
				progressDialog.dismiss();
				if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
					Toast.makeText(AddContactActivity.this, "您的网络不稳定,请检查网络！", 0).show();
					return;
				}
				
				Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
        		if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
        			Toast.makeText(AddContactActivity.this, "更新成功", 0).show();
        			Log.d("log message=="+lm.get("message").toString());
        			if(!CommonUtils.isNullOrEmpty(lm.get("result").toString())){
        				Map<String, Object> lmres = JsonToMapList.getMap(lm.get("result").toString());
        				String nickname = lmres.get("nickname").toString();
        				//String age = lmres.get("age").toString();
        				//String sex = lmres.get("sex").toString();
        				String headurl = lmres.get("headurl").toString();
        				sendaddname = lmres.get("user").toString();
        				
        				if(CommonUtils.isNullOrEmpty(sendaddname)){ 
        					Toast.makeText(AddContactActivity.this, "没有找到该用户！", 0).show();
        					return;
        				}
        				
        				if(!CommonUtils.isNullOrEmpty(nickname)){
        					//tv_user_nicheng.setText(nickname);
        					searchedUserLayout.setVisibility(View.VISIBLE);
        					nameText.setText(nickname);
        					PreferenceUtils.getInstance(AddContactActivity.this).setSettingUserNickName(nickname); 
        				}else if(!CommonUtils.isNullOrEmpty(headurl)){
        					//tv_user_xingbie.setText(sex);
        					//更新头像
        				ImageLoader.getInstance().displayImage(headurl, avatar, ImageOptions.getOptions());
        				 
        				}
        			    				
        			}
        		}else{
        			if(!CommonUtils.isNullOrEmpty(lm.get("message").toString()))
        			Toast.makeText(AddContactActivity.this, lm.get("message").toString(), 0).show();
					return;
        		}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, String rawJsonData,
					Object errorResponse) {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				Toast.makeText(AddContactActivity.this, "网络请求失败,请检查网络！", 0).show();
				return; 
			}
			
			 @Override
			protected Object parseResponse(String rawJsonData,
					boolean isFailure) throws Throwable {
				// TODO Auto-generated method stub
				return null;
			}

	    };
	}
	
	
	/**
	 * 查找contact
	 * @param v
	 */
	public void searchContact(View v) {
		final String name = editText.getText().toString();
		String saveText = searchBtn.getText().toString();
		
		if (getString(R.string.button_search).equals(saveText)) {
			toAddUsername = name;
			if(TextUtils.isEmpty(name)) {
				startActivity(new Intent(this, AlertDialog.class).putExtra("msg", "请输入用户名"));
				return;
			}
			
		RequestParams params = new RequestParams(); 
		params.add("email", toAddUsername); 
        HttpRestClient.get(Constant.USER_URL_E, params, responseHandler);
        progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在发送请求...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
			// TODO 从服务器获取此contact,如果不存在提示不存在此用户
			//服务器存在此用户，显示此用户和添加按钮
			searchedUserLayout.setVisibility(View.VISIBLE);
			nameText.setText(toAddUsername);
			
		} 
	}	
	
	/**
	 *  添加contact
	 * @param view
	 */
	public void addContact(View view){
		/*String sendstr = data.getStringExtra("edittext");
		if(CommonUtils.isNullOrEmpty(sendstr)){
			sendstr = "打招呼";
		}
		//addUserToBlacklist(deleteMsg.getFrom());
		addContact(deleteMsg.getFrom(),sendstr);
		*/
		//if(DemoApplication.getInstance().getUserName().equals(nameText.getText().toString())){
		if(DemoApplication.getInstance().getUser().equals(nameText.getText().toString())){
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg", "不能添加自己"));
			return;
		}
		
		if(DemoApplication.getInstance().getContactList().containsKey(nameText.getText().toString())){
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg", "此用户已是你的好友"));
			return;
		}
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在发送请求...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
		
		new Thread(new Runnable() {
			public void run() {
				
				try {
					//demo写死了个reason，实际应该让用户手动填入
					EMContactManager.getInstance().addContact(toAddUsername, "加个好友呗");
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "发送请求成功,等待对方验证", 1).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "请求添加好友失败:" + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}
	
	public void back(View v) {
		finish();
	}
}
