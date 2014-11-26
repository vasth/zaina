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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.w3c.dom.Comment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaUser;
import com.baidu.frontia.api.FrontiaAuthorization;
import com.baidu.frontia.api.FrontiaAuthorization.MediaType;
import com.baidu.frontia.api.FrontiaAuthorizationListener.AuthorizationListener;
import com.baidu.frontia.api.FrontiaAuthorizationListener.UserInfoListener;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.ccxt.whl.Constant;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.db.UserDao;
import com.ccxt.whl.domain.User;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.DeviceUuidFactory;
import com.ccxt.whl.utils.HttpRestClient;
import com.ccxt.whl.utils.JsonToMapList;
import com.ccxt.whl.utils.MD5;
import com.ccxt.whl.utils.PreferenceUtils;
import com.easemob.util.HanziToPinyin;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

/**
 * 登陆页面
 * 
 */
public class LoginActivity extends BaseActivity {
	private EditText usernameEditText;
	private EditText passwordEditText;

	private boolean progressShow;
	private FrontiaAuthorization mAuthorization;//百度社会化登录
	private String name = null;
	private String access_token = null;
	private String sex = null;
	private String birthday = null;
	private String headurl = null;
	private String province = null;
	private String city = null;
	private String meida_type = null;
	private String meida_uid = null;
	private static String uid = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		usernameEditText = (EditText) findViewById(R.id.username);
		usernameEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);//设置限制邮箱格式
		passwordEditText = (EditText) findViewById(R.id.password);
		// 如果用户名密码都有，直接进入主页面
//		if (DemoApplication.getInstance().getUserName() != null && DemoApplication.getInstance().getPassword() != null) {
		if (DemoApplication.getInstance().getUser() != null && DemoApplication.getInstance().getPassword() != null) {
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
		// 如果用户名改变，清空密码
		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				passwordEditText.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		mAuthorization = Frontia.getAuthorization();//百度社会化登录
		
		DeviceUuidFactory uuid = new DeviceUuidFactory(this); 
		uid = uuid.getDeviceUuid().toString(); 
	}
	/**
	 * 社会化登陆
	 * 
	 * @param view
	 */
	public void Slogin(View view) {
		mAuthorization.authorize(this,
				FrontiaAuthorization.MediaType.SINAWEIBO.toString(),
				new AuthorizationListener() {

					@Override
					public void onSuccess(FrontiaUser result) {
					    Frontia.setCurrentAccount(result);
						//if (null != mResultTextView) {
							String log = "social id: " + result.getId() + "\n"
                                    + "token: " + result.getAccessToken() + "\n"
                                    + "expired: " + result.getExpiresIn()+ "\n"
                                    + "name: " + result.getName()+ "\n" 
                                   + "MediaUserId: " + result.getMediaUserId();
							Log.d("log nihao ===>"+log);
							
							access_token = result.getAccessToken();
							name  =  result.getName();
							meida_uid = result.getMediaUserId();

							//查看用户资料并提交到服务器
							userinfo(MediaType.SINAWEIBO.toString());
							 
					}

					@Override
					public void onFailure(int errorCode, String errorMessage) {
//						if (null != mResultTextView) {
//							mResultTextView.setText("errCode:" + errorCode
//									+ ", errMsg:" + errorMessage);
//						}
						Log.d("log nihao ===>"+"errCode:" + errorCode
 								+ ", errMsg:" + errorMessage);
						Toast.makeText(getApplicationContext(), "错误代码"+errorCode+":"+errorMessage, 0).show();
						return;
					}

					@Override
					public void onCancel() {
//						if (null != mResultTextView) {
//							mResultTextView.setText("cancel");
//						}
						Log.d("log nihao ===>onCancel");
					}

				});
		
	}
	
	/****
	 * qq社会话登录
	 * @param view
	 */
	public void qqlogin(View view) {
		mAuthorization.authorize(this,
				FrontiaAuthorization.MediaType.QZONE.toString(),
				new AuthorizationListener() {

					@Override
					public void onSuccess(FrontiaUser result) {
					    Frontia.setCurrentAccount(result); 
							String log = "social id: " + result.getId() + "\n"
                                    + "token: " + result.getAccessToken() + "\n"
                                    + "expired: " + result.getExpiresIn()+ "\n"
                                    + "name: " + result.getName()+ "\n" 
                                   + "MediaUserId: " + result.getMediaUserId();
							Log.d("lognihao ===>"+log);
							
							access_token = result.getAccessToken();
							name  =  result.getName();
							meida_uid = result.getMediaUserId();

							//查看用户资料并提交到服务器
							userinfo(MediaType.QZONE.toString());
							 
					}

					@Override
					public void onFailure(int errorCode, String errorMessage) {
 
						Log.d("log nihao ===>"+"errCode:" + errorCode
 								+ ", errMsg:" + errorMessage);
						Toast.makeText(getApplicationContext(), "错误代码"+errorCode+":"+errorMessage, 0).show();
						return;
					}

					@Override
					public void onCancel() { 
						Log.d("log nihao ===>onCancel");
					}

				});
		
	}
	
	 
	
	private void userinfo(String accessToken) {
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("正在请求数据...");
		pd.show();
		
		mAuthorization.getUserInfo(accessToken, new UserInfoListener() {

			@Override
			public void onSuccess(FrontiaUser.FrontiaUserDetail result) {
				//if (null != mResultTextView) {
				Log.d("log useid===>"+result.getId());
				Log.d("log AccessToken===>"+result.getAccessToken());
				Log.d("log BaiduAccessToken===>"+result.getBaiduAccessToken());
				Log.d("log MediaUserId===>"+result.getMediaUserId());
				//Log.d("log MediaUserId===>"+result.get);
				
					String resultStr = "username:" + result.getName() + "\n"
							+ "birthday:" + result.getBirthday() + "\n"
							+ "city:" + result.getCity() + "\n"
							+ "province:" + result.getProvince() + "\n"
							+ "sex:" + result.getSex() + "\n"
							+ "pic url:" + result.getHeadUrl() + "\n";
					Log.d("log userinfo===>"+resultStr);
					
					sex = ""+result.getSex();
					birthday=result.getBirthday();
					headurl=result.getHeadUrl();
					province=result.getProvince();
					city=result.getCity();
					
					RequestParams params = new RequestParams();
					params.add("name", name);
					params.add("access_token", access_token);
					params.add("sex",sex);
					params.add("birthday",birthday);
					params.add("headurl", headurl);
					params.add("province", province);
					params.add("city", city);
					params.add("meida_type", "sinaweibo");
					params.add("meida_uid", meida_uid);
					//新增设备唯一id
					params.add("uid", uid);
					
					/*
					 //Log.d("log",response);
		                	 try {
		                		 Map<String, Object> lm = JsonToMapList.getMap(response);
				                	Map<String, Object> result = JsonToMapList.getMap(lm.get("result").toString());
				                	String resultStr = "status:" + lm.get("status") + "\n"
											+ "message:" + lm.get("message") + "\n"
											+ "result:" + lm.get("result") + "\n"
											+ "uid:" + result.get("uid") + "\n"
											+ "umd5:" + result.get("umd5") + "\n"
											+ "is_res:" + result.get("is_res") + "\n";
				                	Log.d("log",resultStr);
							} catch (Exception e) {
								// TODO: handle exception
							}
		                	
		                	
		                	//String huanxin_username = MD5.MD5Hash(result.getName());
							//String huanxin_pwd = MD5.MD5Hash(result.getName());
							//s_reg_login(huanxin_username, huanxin_pwd);
					 */
					  
					HttpRestClient.get(Constant.S_LOGIN_URL, params, new BaseJsonHttpResponseHandler() {
						
						  
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								String rawJsonResponse, Object response) {
							// TODO Auto-generated method stub
							Log.d("log"+ rawJsonResponse);
							//if(str != null && str.length()!= 0){//判断字符串是否为空	
							//}
							if (!LoginActivity.this.isFinishing())
								pd.dismiss();
							
							if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
								Toast.makeText(getApplicationContext(), "您的网络不稳定,请检查网络！", 0).show(); 
								return;
							}
		                		Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
		                		
		                		if(CommonUtils.isNullOrEmpty(lm.get("result").toString())){
		                			//if(false){
		                			
		                		}else{
					                Map<String, Object> result = JsonToMapList.getMap(lm.get("result").toString());
					                String resultStr = "status:" + lm.get("status") + "\n"
											+ "message:" + lm.get("message") + "\n"
											+ "result:" + lm.get("result") + "\n"
											+ "uid:" + result.get("uid") + "\n"
											+ "umd5:" + result.get("umd5") + "\n"
											+ "is_res:" + result.get("is_res") + "\n"
											+ "headurl:" + result.get("headurl") + "\n"
											+ "name:" + result.get("name") + "\n"
											+ "sex:" + result.get("sex") + "\n"
											+ "age:" + result.get("age") + "\n"
											+ "province:" + result.get("province") + "\n"
											+ "city:" + result.get("city") + "\n"
											+ "pwd:" + result.get("pwd") + "\n";
				                	Log.d("log"+ resultStr);
				                	//提前设置唯一id 防止出现nullsaveinfo Preference的情况
				                	//DemoApplication.getInstance().setUser(result.get("umd5").toString());
				                	//System.out.println("========================"+result.get("umd5").toString());
				                	/****存储个人资料****/
				                	PreferenceUtils.getInstance(getBaseContext()).setSettingUserPic(result.get("headurl").toString()); 
				                	PreferenceUtils.getInstance(getBaseContext()).setSettingUserNickName(result.get("name").toString()); 
				                	PreferenceUtils.getInstance(getBaseContext()).setSettingUserSex(result.get("sex").toString()); 
				                	PreferenceUtils.getInstance(getBaseContext()).setSettingUserAge(result.get("age").toString());  
				                	PreferenceUtils.getInstance(getBaseContext()).setSettingUserArea(result.get("province").toString()+" "+result.get("city").toString()); 
				                	PreferenceUtils.getInstance(getBaseContext()).setSettingUserZhiye(result.get("zhiye").toString());
				                	PreferenceUtils.getInstance(getBaseContext()).setSettingUserQianming(result.get("qianming").toString());
				                	
				                	String huanxin_username = result.get("umd5").toString();
									String huanxin_pwd = result.get("pwd").toString();
									Log.d("log huanxin_username:"+huanxin_username+"|huanxin_pwd:"+huanxin_pwd+"|token:"+access_token);
									//判断是否已注册到聊天服务器
									if(result.get("is_res").toString().equals("yes")){
										login(huanxin_username, huanxin_pwd);
									}else{
										s_reg_login(huanxin_username, huanxin_pwd);
									}
									
		                		}
		                	
		                	
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable throwable, String rawJsonData,
								Object errorResponse) {
							pd.dismiss();
							Toast.makeText(getApplicationContext(), "网络请求失败,请检查网络！", 0).show(); 
							return;
							// TODO Auto-generated method stub
							
						}

						@Override
						protected Object parseResponse(String rawJsonData,
								boolean isFailure) throws Throwable {
							// TODO Auto-generated method stub
							return null;
						}
					}); 
		                
 
			}

			@Override
			public void onFailure(int errCode, String errMsg) { 
					Log.d("log nihao ===>"+"errCode:" + errCode
							+ ", errMsg:" + errMsg); 
					sex = "";
					birthday="";
					headurl="";
					province="";
					city="";
					pd.dismiss();
					Toast.makeText(getApplicationContext(), "获取返回信息失败，请重试！", 0).show();
					return;
			}
			
		});
	}
	
	/**
	 * 社会化登录后处理逻辑
	 */
	public void s_reg_login(final String username,final String pwd){
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
								if (!LoginActivity.this.isFinishing())
									pd.dismiss();
								// 保存用户名
//								DemoApplication.getInstance().setUserName(username);
								DemoApplication.getInstance().setUser(username);
								Toast.makeText(getApplicationContext(), "注册成功", 0).show();
								login(username, pwd);
								//finish();
							}
						});
					} catch (final Exception e) {
						runOnUiThread(new Runnable() {
							public void run() {
								if (!LoginActivity.this.isFinishing())
									pd.dismiss();
								if (e != null && e.getMessage() != null) {
									String errorMsg = e.getMessage();
									if (errorMsg.indexOf("EMNetworkUnconnectedException") != -1) {
										Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", 0).show();
									} else if (errorMsg.indexOf("conflict") != -1) {
										login(username, pwd);
										//Toast.makeText(getApplicationContext(), "用户已存在！", 0).show();
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
	
	/**
	 * 社会化注册后登陆逻辑
	 * 
	 * @param view
	 */
	public void login(final String username,final String password) {
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		//final String username = usernameEditText.getText().toString();
		//final String password = passwordEditText.getText().toString();
		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
			progressShow = true;
			final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
			pd.setCanceledOnTouchOutside(false);
			pd.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					progressShow = false;
				}
			});
			pd.setMessage("正在登陆...");
			pd.show();
			// 调用sdk登陆方法登陆聊天服务器
			EMChatManager.getInstance().login(username, password, new EMCallBack() {

				@Override
				public void onSuccess() {
					/*****************
					 * 添加更新注册状态
					 *****************/
					if (!progressShow) {
						return;
					}
					// 登陆成功，保存用户名密码
//					DemoApplication.getInstance().setUserName(username);
					DemoApplication.getInstance().setUser(username);
					DemoApplication.getInstance().setPassword(password);
					DemoApplication.getInstance().setUserNickName(name);
					runOnUiThread(new Runnable() {
						public void run() {
							pd.setMessage("正在获取好友和群聊列表...");
						}
					});
					try {
						// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
						List<String> usernames = EMChatManager.getInstance().getContactUserNames();
						Map<String, User> userlist = new HashMap<String, User>();
						for (String username : usernames) {
							User user = new User();
							user.setUsername(username);
							setUserHearder(username, user);
							userlist.put(username, user);
						}
						// 添加user"申请与通知"
						User newFriends = new User();
						newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
						newFriends.setNick("申请与通知");
						newFriends.setHeader("");
						userlist.put(Constant.NEW_FRIENDS_USERNAME, newFriends);
						// 添加"群聊"
						User groupUser = new User();
						groupUser.setUsername(Constant.GROUP_USERNAME);
						groupUser.setNick("群聊");
						groupUser.setHeader("");
						userlist.put(Constant.GROUP_USERNAME, groupUser);

						// 存入内存
						DemoApplication.getInstance().setContactList(userlist);
						/***改道MainActivity里面存入db* /
						// 存入db
						UserDao dao = new UserDao(LoginActivity.this);
						List<User> users = new ArrayList<User>(userlist.values());
						dao.saveContactList(users);/**/

						// 获取群聊列表,sdk会把群组存入到EMGroupManager和db中
						EMGroupManager.getInstance().getGroupsFromServer();
						// after login, we join groups in separate threads;
						EMGroupManager.getInstance().joinGroupsAfterLogin();
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (!LoginActivity.this.isFinishing())
						pd.dismiss();
					// 进入主页面
					startActivity(new Intent(LoginActivity.this, MainActivity.class));
					finish();
				}

				@Override
				public void onProgress(int progress, String status) {

				}

				@Override
				public void onError(int code, final String message) {
					if (!progressShow) {
						return;
					}
					//Log.d("log","code"+code);
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							 
								Toast.makeText(getApplicationContext(), "登录失败: " + message, 0).show();
						 

						}
					});
				}
			});
		}
	}
	
	/**
	 * 登陆
	 * 待改
	 * @param view
	 */
	public void login(View view) {
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		final String email = usernameEditText.getText().toString();
		final String password = passwordEditText.getText().toString();
		if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
			progressShow = true;
			final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
			pd.setCanceledOnTouchOutside(false);
			pd.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					progressShow = false;
				}
			});
			pd.setMessage("正在登陆...");
			pd.show();
			// 调用sdk登陆方法登陆聊天服务器
			RequestParams params = new RequestParams();
			params.add("tel_email", email);
			params.add("pwd", MD5.MD5Hash(password));
			params.add("uid", uid);
			HttpRestClient.get(Constant.LOGIN_URL, params, new BaseJsonHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						String rawJsonResponse, Object response) {
					Log.d("login_res_json"+rawJsonResponse);
					pd.dismiss();
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
								+ "uid:" + result.get("uid") + "\n"
								+ "umd5:" + result.get("umd5") + "\n"
								+ "is_res:" + result.get("is_res") + "\n"
								+ "headurl:" + result.get("headurl") + "\n"
								+ "name:" + result.get("name") + "\n"
								+ "sex:" + result.get("sex") + "\n"
								+ "age:" + result.get("age") + "\n"
								+ "province:" + result.get("province") + "\n"
								+ "city:" + result.get("city") + "\n"
								+ "pwd:" + result.get("pwd") + "\n";
		                Log.d("login_res_obj"+ resultStr);
		                //提前设置唯一id 防止出现nullsaveinfo Preference的情况
	                	//DemoApplication.getInstance().setUser(result.get("umd5").toString());
	                	//System.out.println("========================"+result.get("umd5").toString());
	                	/****存储个人资料****/
	                	PreferenceUtils.getInstance(getBaseContext()).setSettingUserPic(result.get("headurl").toString()); 
	                	PreferenceUtils.getInstance(getBaseContext()).setSettingUserNickName(result.get("name").toString()); 
	                	PreferenceUtils.getInstance(getBaseContext()).setSettingUserSex(result.get("sex").toString()); 
	                	PreferenceUtils.getInstance(getBaseContext()).setSettingUserAge(result.get("age").toString());  
	                	PreferenceUtils.getInstance(getBaseContext()).setSettingUserArea(result.get("province").toString()+" "+result.get("city").toString()); 
	                	PreferenceUtils.getInstance(getBaseContext()).setSettingUserZhiye(result.get("zhiye").toString());
	                	PreferenceUtils.getInstance(getBaseContext()).setSettingUserQianming(result.get("qianming").toString());
	                	
	                  
	                	String huanxin_username = result.get("umd5").toString();
						String huanxin_pwd = result.get("pwd").toString();
						Log.d("log huanxin_username:"+huanxin_username+"|huanxin_pwd:"+huanxin_pwd);
						
						//登录到聊天服务器 
						login(huanxin_username, huanxin_pwd);
						
						 /*不注释掉会出现第一次登陆无法登陆第二次才可以的情况
						 progressShow = false;
						 pd.dismiss();*/
						
                	}else{
                		Toast.makeText(getApplicationContext(), lm.get("message").toString(), 0).show();
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

	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		startActivityForResult(new Intent(this, RegisterActivity.class), 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		/*if (DemoApplication.getInstance().getUserName() != null) {
			usernameEditText.setText(DemoApplication.getInstance().getUserName());
		}*/
		if (DemoApplication.getInstance().getUser() != null) {
			//usernameEditText.setText(DemoApplication.getInstance().getUser());
		}
	}

	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}
}
