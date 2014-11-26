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

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.ccxt.whl.Constant;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.DeviceUuidFactory;
import com.ccxt.whl.utils.HttpRestClient;
import com.ccxt.whl.utils.JsonToMapList;
import com.ccxt.whl.utils.MD5;
import com.ccxt.whl.utils.PreferenceUtils;
import com.easemob.chat.EMChatConfig;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EMNetworkUnconnectedException;
import com.easemob.exceptions.EaseMobException;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 注册页
 * 
 */
public class RegisterActivity extends BaseActivity {
	private EditText emailEditText;
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;
	
	private String sex = null;
	
	private String uid = null;
	
    RadioGroup rg;
    RadioButton b1;
    RadioButton b2;
	    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		DeviceUuidFactory uuid = new DeviceUuidFactory(this); 
		uid = uuid.getDeviceUuid().toString(); 
		
		emailEditText = (EditText)findViewById(R.id.email);
		emailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);//设置限制邮箱格式
		userNameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText = (EditText) findViewById(R.id.confirm_password);
		
		rg=(RadioGroup)findViewById(R.id.sex);
        b1=(RadioButton)findViewById(R.id.male);
        b2=(RadioButton)findViewById(R.id.female);
        
        rg.setOnCheckedChangeListener(new OnCheckedChangeListener(){
        	 
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if(checkedId==b1.getId()){
                	sex = "1";
                    Toast.makeText(RegisterActivity.this,"男", Toast.LENGTH_LONG).show();
                }
                if(checkedId==b2.getId()){
                	sex = "2";
                    Toast.makeText(RegisterActivity.this,"女", Toast.LENGTH_LONG).show();
                }
                 
            }
 
        });
	}

	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		final String email = emailEditText.getText().toString().trim();
		final String username = userNameEditText.getText().toString().trim();
		final String pwd = passwordEditText.getText().toString().trim();
		String confirm_pwd = confirmPwdEditText.getText().toString().trim();
		 if (TextUtils.isEmpty(email)) {
			Toast.makeText(this, "邮箱不能为空！", Toast.LENGTH_SHORT).show();
			emailEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(username)) {
			Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(pwd)) {
			Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		} else if (TextUtils.isEmpty(confirm_pwd)) {
			Toast.makeText(this, "确认密码不能为空！", Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		} else if (!pwd.equals(confirm_pwd)) {
			Toast.makeText(this, "两次输入的密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
			return;
		}else if (sex==null) {
			Toast.makeText(this, "请选择您的性别！", Toast.LENGTH_SHORT).show();
			return;
		}

		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage("正在注册...");
			pd.show();
			RequestParams params = new RequestParams();
			params.add("tel_email", email);
			params.add("nickname", username);
			params.add("pwd", MD5.MD5Hash(pwd));
			params.add("sex", sex); 
			params.add("uid", uid); 
			
			HttpRestClient.get(Constant.REGISTER_URL, params, new BaseJsonHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						String rawJsonResponse, Object response) {
					Log.d("login_res_json"+rawJsonResponse);
					 
					if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
						Toast.makeText(getApplicationContext(), "您的网络不稳定,请检查网络！", 0).show();
						return;
					}
                		Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
                		
                	if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
                		Map<String, Object> result = JsonToMapList.getMap(lm.get("result").toString());
		                String resultStr = "status:" + lm.get("status") + "\n"
								+ "message:" + lm.get("message") + "\n"
								+ "result:" + lm.get("result") + "\n"
								+ "user:" + result.get("user") + "\n"
								+ "pwd:" + result.get("pwd") + "\n";
								
		                Log.d("login_res_obj"+ resultStr); 
						pd.dismiss();
						
						 //Map<String, Object> result = JsonToMapList.getMap(lm.get("result").toString());
						//reg(result.get("user").toString(),result.get("pwd").toString());
						Toast.makeText(getApplicationContext(), lm.get("message").toString(), 0).show();
                	}else{
                		Toast.makeText(getApplicationContext(), lm.get("message").toString(), 0).show();
                		pd.dismiss();
                	}  
				
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, String rawJsonData,
						Object errorResponse) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "您的网络不稳定,请检查网络！", 0).show();
				}

				@Override
				protected Object parseResponse(String rawJsonData,
						boolean isFailure) throws Throwable {
					// TODO Auto-generated method stub
					return null;
				}
				
			});

		}
	}

	public void back(View view) {
		finish();
	}
	
	public void reg(final String username,final String pwd){
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage("正在注册...");
			pd.show();
			new Thread(new Runnable() {
				public void run() {
					try {
						// 调用sdk注册方法
						EMChatManager.getInstance().createAccountOnServer(username, pwd);
						runOnUiThread(new Runnable() {
							public void run() {
								if (!RegisterActivity.this.isFinishing())
									pd.dismiss();
								RequestParams params = new RequestParams();
								params.add("user", username);
								if (DemoApplication.getInstance().getlastloc() != null) {
									  double Latitude = DemoApplication.getInstance().getlastloc().getLatitude()  ; 
									  double Longitude = DemoApplication.getInstance().getlastloc().getLongitude() ;
									  String adr = DemoApplication.getInstance().getlastloc().getAddrStr(); 
									  params.add("jiedao", adr);
								}
								HttpRestClient.get(Constant.REGISTER_URL_HUIZHI, params, new BaseJsonHttpResponseHandler() {

									@Override
									public void onSuccess(int statusCode,
											Header[] headers,
											String rawJsonResponse,
											Object response) {
										// TODO Auto-generated method stub
										Log.d("login_res_json"+ rawJsonResponse);
										 
										if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
											Toast.makeText(getApplicationContext(), "您的网络不稳定,请检查网络！", 0).show();
											return;
										}
					                		Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
					                		
					                	if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
					                		Map<String, Object> result = JsonToMapList.getMap(lm.get("result").toString());
							                String resultStr = "status:" + lm.get("status") + "\n"
													+ "message:" + lm.get("message") + "\n"
													+ "result:" + lm.get("result") + "\n";
													
							                Log.d("login_res_obj"+ resultStr);
						                	  
											pd.dismiss();
											// 保存用户名
//											DemoApplication.getInstance().setUserName(username);
											DemoApplication.getInstance().setUser(username);
											Toast.makeText(getApplicationContext(), "注册成功", 0).show();
											finish();
					                	}else{
					                		Toast.makeText(getApplicationContext(), lm.get("message").toString(), 0).show();
					                		pd.dismiss();
					                	}  
										
									}

									@Override
									public void onFailure(int statusCode,
											Header[] headers,
											Throwable throwable,
											String rawJsonData,
											Object errorResponse) {
										// TODO Auto-generated method stub
										Toast.makeText(getApplicationContext(), "请检查网络是否开启", 0).show();
									}

									@Override
									protected Object parseResponse(
											String rawJsonData,
											boolean isFailure) throws Throwable {
										// TODO Auto-generated method stub
										return null;
									}
								
								});
								
								 
							}
						});
					} catch (final Exception e) {
						runOnUiThread(new Runnable() {
							public void run() {
								if (!RegisterActivity.this.isFinishing())
									pd.dismiss();
								if (e != null && e.getMessage() != null) {
									String errorMsg = e.getMessage();
									if (errorMsg.indexOf("EMNetworkUnconnectedException") != -1) {
										Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", 0).show();
									} else if (errorMsg.indexOf("conflict") != -1) {
										//用户名重复
										 Toast.makeText(getApplicationContext(), "未知错误，请重试！", 0).show();
									} else if (errorMsg.indexOf("not support the capital letters") != -1) {
										Toast.makeText(getApplicationContext(), "用户名不支持大写字母！", 0).show();
									} else {
										Toast.makeText(getApplicationContext(), "注册失败: " + e.getMessage(), 1).show();
									}

								} else {
									Toast.makeText(getApplicationContext(), "注册失败: 未知异常", 1).show();
								}
							}
						});
					}
				}
			}).start();

		}
	}

}
