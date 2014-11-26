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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.TextMessageBody;
import com.baidu.kirin.CheckUpdateListener;
import com.baidu.kirin.PostChoiceListener;
import com.baidu.kirin.StatUpdateAgent;
import com.baidu.kirin.objects.KirinCheckState;
import com.baidu.mobstat.StatService;
 
import com.ccxt.whl.Constant;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.activity.ZainaFragment.OnMySelectedListener;
import com.ccxt.whl.db.InviteMessgeDao;
import com.ccxt.whl.db.UserDao;
import com.ccxt.whl.domain.InviteMessage;
import com.ccxt.whl.domain.User;
import com.ccxt.whl.domain.InviteMessage.InviteMesageStatus;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.HttpRestClient;
import com.ccxt.whl.utils.JsonToMapList;
import com.ccxt.whl.utils.MyLogger;
import com.ccxt.whl.utils.UpdateDialog;
import com.easemob.util.EMLog;
import com.easemob.util.HanziToPinyin;
import com.easemob.util.NetUtils; 
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MainActivity extends FragmentActivity implements OnMySelectedListener
{
//, CheckUpdateListener,PostChoiceListener{

	protected static final String TAG = "MainActivity";
	// 未读消息textview
	private TextView unreadLabel;
	// 未读通讯录textview
	private TextView unreadAddressLable;

	private Button[] mTabs;
	private ZainaFragment zainaFrament;//在哪 页面
	private Zaina_gushi_Fragment gushiFrament;//故事页面
//	private ChatHistoryFragment chatHistoryFragment;
	private ChatAllHistoryFragment chatHistoryFragment;//所有会话界面
	private ContactlistFragment contactListFragment;//联系人页面 
	private SettingsFragment settingFragment;//设置界面
	private Fragment[] fragments;
	private int index;
	private RelativeLayout[] tab_containers;
	// 当前fragment的index
	private int currentTabIndex;
	private NewMessageBroadcastReceiver msgReceiver;
	// 账号在别处登录

	private boolean isConflict = false;
	private String nickname;
	private String headurl;
	
	/***loadcontact方法使用****/
	private String nickname_tmp = "";
	private String headurl_tmp = "";
	
	private final static int ZAINAFRAMENT = 0;
	private final static int GUSHIFRAMENT = 1;
	private final static int CHATHISTORYFRAGMENT = 2;
	private final static int CONTACTLISTFRAGMENT = 3;
	private final static int SETTINGFRAGMENT = 4;
	
	// 小流量发布相关
	private UpdateDialog utestUpdate;
//	private CheckUpdateListener mCheckUpdateResponse;
//	private PostChoiceListener mPostUpdateChoiceListener;
	
	private static MyLogger Log = MyLogger.yLog();
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//设置推送昵称
		DemoApplication.currentUserNick = DemoApplication.getInstance().getUsernNickName(); 
		boolean updatenick = EMChatManager.getInstance()
				.updateCurrentUserNick(DemoApplication.currentUserNick);
		if (!updatenick) {
			EMLog.e("LoginActivity", "update current user nick fail");
		}
		//设置自动升级检测
		StatService.setAppKey("32f5355664");
		StatService.setAppChannel("内测");
//		mCheckUpdateResponse = this;
//		mPostUpdateChoiceListener = this;
//		utestUpdate = new  UpdateDialog(this, "版本升级",
//				mPostUpdateChoiceListener);
		utestUpdate = new  UpdateDialog(this, "版本升级");
// 		StatUpdateAgent.setTestMode(); // 打开小流量调试模式，在该模式下，不受更新频率设置的影响。如果不设置测试模式，那么请求间隔默认每天会请求一次
 		//StatUpdateAgent.checkUpdate(arg0, arg1, arg2)
//		StatUpdateAgent.checkUpdate(MainActivity.this, false,
//				mCheckUpdateResponse);
		
		initView();
		inviteMessgeDao = new InviteMessgeDao(this);
		userDao = new UserDao(this);
		
		zainaFrament = new ZainaFragment();
		gushiFrament = new Zaina_gushi_Fragment();
		//这个fragment只显示好友和群组的聊天记录
//		chatHistoryFragment = new ChatHistoryFragment();
		//显示所有人消息记录的fragment
		chatHistoryFragment = new ChatAllHistoryFragment();
		contactListFragment = new ContactlistFragment();
		settingFragment = new SettingsFragment();
		//fragments = new Fragment[] { chatHistoryFragment, contactListFragment, settingFragment };
		fragments = new Fragment[] { zainaFrament , gushiFrament , chatHistoryFragment, contactListFragment, settingFragment };
		
		// 添加显示第一个fragment
	/*	getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, chatHistoryFragment)
				.add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(chatHistoryFragment)
				.commit();*/
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, zainaFrament)
		.add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(chatHistoryFragment)
		.commit();

		// 注册一个接收消息的BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		registerReceiver(msgReceiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance()
				.getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		registerReceiver(ackMessageReceiver, ackMessageIntentFilter);

		// 注册一个离线消息的BroadcastReceiver
		IntentFilter offlineMessageIntentFilter = new IntentFilter(EMChatManager.getInstance()
				.getOfflineMessageBroadcastAction());
		registerReceiver(offlineMessageReceiver, offlineMessageIntentFilter);
		
		// setContactListener监听联系人的变化等
		EMContactManager.getInstance().setContactListener(new MyContactListener());
		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
		// 注册群聊相关的listener
		EMGroupManager.getInstance().addGroupChangeListener(new MyGroupChangeListener());
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
	    /****新增****/
		loadcontact();
		check_update();//检查版本更新
		//showFloatingButton();暂时注释
	}

	/**
	 * 接loginactivity获取好友列表并存入db
	 */
	private void loadcontact() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			public void run() {
				Map<String, User> userlists = new HashMap<String, User>();
				userlists = DemoApplication.getInstance().getContactList();
				if(userlists.size()<2){
					return;
				}
				final UserDao dao = new UserDao(MainActivity.this);
				List<User> users = new ArrayList<User>(userlists.values());
				//if(users.l)
				for (final User user : users) {
					if (user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME) || user.getUsername().equals(Constant.GROUP_USERNAME)) {
						 dao.saveContact(user);
						 Log.d("NEW_FRIENDS_USERNAME-pass");
						 continue;//跳过
					}
					User local_user_is = dao.getUser(user.getUsername());
					//判断本地是否存在
					if(!CommonUtils.isNullOrEmpty(local_user_is.toString()) ){ 
						if(local_user_is.getHeaderurl()!=null&&local_user_is.getNick()!=null&&local_user_is.getIs_stranger().equals("2")){
							Log.d("local_user_is-pass");
							continue;//跳过
						}
					}
					 //判断头像、昵称、唯一id是否
					if(CommonUtils.isNullOrEmpty(user.getUsername())||
							CommonUtils.isNullOrEmpty(user.getHeaderurl())||
								CommonUtils.isNullOrEmpty(user.getNick())){
						get_add_info(user.getUsername(),true);
						/*
						String httpUrl = Constant.BASE_URL
								+ Constant.USER_URL_C + "user="
								+ user.getUsername();
						// 创建httpRequest对象
						HttpGet httpRequest = new HttpGet(httpUrl);
						try {
							// 取得HttpClient对象
							HttpClient httpclient = new DefaultHttpClient();
							// 请求HttpClient，取得HttpResponse
							HttpResponse httpResponse = httpclient
									.execute(httpRequest);
							// 请求成功
							if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
								// 取得返回的字符串
								String strResult = EntityUtils.toString(httpResponse.getEntity());
								if(!CommonUtils.isNullOrEmpty(strResult)){
									Map<String, Object> lm = JsonToMapList.getMap(strResult);
									if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
										if(!CommonUtils.isNullOrEmpty(lm.get("result"))){ 
											Map<String, Object> result = JsonToMapList.getMap(lm.get("result").toString());
											nickname_tmp = result.get("nickname").toString();
											headurl_tmp = result.get("headurl").toString(); 
											Log.d(TAG,"nickname_tmp and headurl_tmp "+nickname_tmp+" "+headurl_tmp);
											 
										} 
									} 
								} 
								
								if(!CommonUtils.isNullOrEmpty(nickname_tmp)&&!CommonUtils.isNullOrEmpty(headurl_tmp)){
							    	User user_temp =  new User();
							    	user_temp.setUsername(user.getUsername());
							    	user_temp.setNick(nickname_tmp);
							    	user_temp.setHeaderurl(headurl_tmp);
									
							    	dao.saveContact(user_temp);
							    	Log.d(TAG,"saveContact have");
							    } 
							}  
							
						} catch (ClientProtocolException e) {
							Log.d(TAG, e.getMessage().toString());
						} catch (IOException e) {
							Log.d(TAG, e.getMessage().toString());
						} catch (Exception e) {
							Log.d(TAG, e.getMessage().toString());
						}
						 */
					}
				}
					  
					 
				} 
			 
		}).start();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		unreadLabel = (TextView) findViewById(R.id.unread_msg_number);
		unreadAddressLable = (TextView) findViewById(R.id.unread_address_number);
	 
		mTabs = new Button[5];
		mTabs[0] = (Button) findViewById(R.id.btn_conversation_zaina);//在哪按钮
		mTabs[1] = (Button) findViewById(R.id.btn_gushi);//故事按钮
		mTabs[2] = (Button) findViewById(R.id.btn_conversation);//所有会话按钮
		mTabs[3] = (Button) findViewById(R.id.btn_address_list);//联系人按钮
		mTabs[4] = (Button) findViewById(R.id.btn_setting);//设置按钮
		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);
		
	}

	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
		switch (view.getId()) {
	 
		case R.id.btn_conversation_zaina:
			index = 0;
			break;
		case R.id.btn_gushi:
			index = 1;
			break;
		case R.id.btn_conversation:
			index = 2;
			break;
		case R.id.btn_address_list:
			index = 3;
			break;
		case R.id.btn_setting:
			index = 4;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
		 
	}
	
	/**
	 * 切换fragment
	 * @param index
	 */
	public void change_f(int index){

	 
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销广播接收者
		try {
			unregisterReceiver(msgReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(ackMessageReceiver);
		} catch (Exception e) {
		}
		try {
			unregisterReceiver(offlineMessageReceiver);
		} catch (Exception e) {
		}

		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}

	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			unreadLabel.setText(String.valueOf(count));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 刷新申请与通知消息数
	 */
	public void updateUnreadAddressLable() {
		runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				if (count > 0) {
					unreadAddressLable.setText(String.valueOf(count));
					unreadAddressLable.setVisibility(View.VISIBLE);
				} else {
					unreadAddressLable.setVisibility(View.INVISIBLE);
				}
			}
		});

	}

	/**
	 * 获取未读申请与通知消息
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		if (DemoApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME) != null)
			unreadAddressCountTotal = DemoApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME)
					.getUnreadMsgCount();
		return unreadAddressCountTotal;
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}

	/**
	 * 新消息广播接收者
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			
			//根据消息id获取message
			//EMMessage message = EMChatManager.getInstance().getMessage(msgId);
			//获取自定义的属性，第2个参数为返回的默认值
			//message.getStringAttribute("attribute1",null);
			//message.getBooleanAttribute("attribute2", false);
			
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			// EMMessage message =
			// EMChatManager.getInstance().getMessage(msgId);
			final String toChatUsername = intent.getStringExtra("from"); 
			
			 
			final UserDao userdao =  new UserDao(context);
			final User local_user = userdao.getUser(toChatUsername);
			//判断本地是否存在
			if(CommonUtils.isNullOrEmpty(local_user.toString())){ //new 的对象不为null
				RequestParams params = new RequestParams(); 
				params.add("user", toChatUsername);
				HttpRestClient.get(Constant.USER_URL_C, params, new BaseJsonHttpResponseHandler(){
					//private String nickname;
					//private String headurl;
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							String rawJsonResponse, Object response) {
						// TODO Auto-generated method stub
						Log.d("rawJsonResponse"+ rawJsonResponse); 
						if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
							nickname = "陌生人";
							headurl = "";
						} 
						Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
						if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
							if(!CommonUtils.isNullOrEmpty(lm.get("result").toString())){ 
								Map<String, Object> result = JsonToMapList.getMap(lm.get("result").toString());
								nickname = result.get("nickname").toString();
								headurl = result.get("headurl").toString();
								local_user.setUsername(toChatUsername);
								local_user.setNick(nickname);
								local_user.setHeaderurl(headurl);
								userdao.saveContact_m(local_user);
								/*******************0901添加**********************/
								if (currentTabIndex == 2) {
									// 当前页面如果为聊天历史页面，刷新此页面
									if (chatHistoryFragment != null) {
										chatHistoryFragment.refresh();
									}
								}
								/*********************0901添加********************/
							}else{
								nickname = "陌生人";
								headurl = "";
							}
						}else{
							nickname = "陌生人";
							headurl = "";
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, String rawJsonData,
							Object errorResponse) {
						// TODO Auto-generated method stub
						nickname = "陌生人";
						headurl = "";
					}

					@Override
					protected Object parseResponse(String rawJsonData,
							boolean isFailure) throws Throwable {
						// TODO Auto-generated method stub
						return null;
					}
					
				});
			}else{//存在就直接从数据库中取
				Log.d("local_user"+local_user.toString());
				//nickname = "陌生人";
				//headurl = "";
			}
			/****************************************获取消息的用户*****************************************/

			// 刷新bottom bar消息未读数
			updateUnreadLabel();
			if (currentTabIndex == CHATHISTORYFRAGMENT) {
				// 当前页面如果为聊天历史页面，刷新此页面
				if (chatHistoryFragment != null) {
					chatHistoryFragment.refresh();
				}
			}
			Log.d("mainactivity NewMessageBroadcastReceiver==================");
			// 注销广播，否则在ChatActivity中会收到这个广播
			abortBroadcast();
		}
	}

	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isAcked = true;
				}
			}
			abortBroadcast();
		}
	};

	/**
	 * 离线消息BroadcastReceiver
	 * sdk 登录后，服务器会推送离线消息到client，这个receiver，是通知UI 有哪些人发来了离线消息
	 * UI 可以做相应的操作，比如下载用户信息
	 */
	private BroadcastReceiver offlineMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String[] users = intent.getStringArrayExtra("fromuser");
			String[] groups = intent.getStringArrayExtra("fromgroup");
			if (users != null) {
				for (String user : users) {
					Log.d("收到user离线消息：" + user);
				 
					if(DemoApplication.getInstance().getContactList().containsKey(user)){
						get_add_info(user,true); 
					 }else{
						get_add_info(user,false);
					 }
						  
				}
			}
			if (groups != null) {
				for (String group : groups) {
					Log.d("收到group离线消息：" + group);
				}
			}
			abortBroadcast();
		} 
	};
	
	
	 /**
	  * MainActivity 类 里面共用的添加用户方法
	  * @param toChatUsername  要添加的唯一id
	  * @param is_fran  声明是否是陌生人或者好友 : true则添加好友，false则添加为陌生人
	  */
	public void get_add_info(final String toChatUsername, final boolean is_fran) {
		new Thread(new Runnable() {
			public void run() {
				UserDao dao = new UserDao(MainActivity.this);
				String httpUrl = Constant.BASE_URL
						+ Constant.USER_URL_C + "user="
						+ toChatUsername;
				// 创建httpRequest对象
				HttpGet httpRequest = new HttpGet(httpUrl);
				try {
					// 取得HttpClient对象
					HttpClient httpclient = new DefaultHttpClient();
					// 请求HttpClient，取得HttpResponse
					HttpResponse httpResponse = httpclient
							.execute(httpRequest);
					// 请求成功
					if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
						// 取得返回的字符串
						String strResult = EntityUtils.toString(httpResponse.getEntity());
						if(!CommonUtils.isNullOrEmpty(strResult)){
							Map<String, Object> lm = JsonToMapList.getMap(strResult);
							if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
								if(!CommonUtils.isNullOrEmpty(lm.get("result").toString())){ 
									Map<String, Object> result = JsonToMapList.getMap(lm.get("result").toString());
									nickname_tmp = result.get("nickname").toString();
									headurl_tmp = result.get("headurl").toString(); 
									Log.d("nickname_tmp and headurl_tmp "+nickname_tmp+" "+headurl_tmp);
									 
								} 
							} 
						} 
						
						if(!CommonUtils.isNullOrEmpty(nickname_tmp)&&!CommonUtils.isNullOrEmpty(headurl_tmp)){
							User user_temp =  new User();
					    	user_temp.setUsername(toChatUsername);
					    	user_temp.setNick(nickname_tmp);
					    	user_temp.setHeaderurl(headurl_tmp); 
					    	if(is_fran){
					    		dao.saveContact(user_temp);
							}else{
								dao.saveContact_m(user_temp);
							} 
					    	//dao.saveContact(user_temp);
					    	Log.d("saveContact have");
					    } 
					}  
					
				} catch (ClientProtocolException e) {
					Log.e( e.getMessage().toString());
				} catch (IOException e) {
					Log.e(e.getMessage().toString());
				} catch (Exception e) {
					Log.e( e.getMessage().toString());
				}
				 
			
		// TODO Auto-generated method stub
		/****************************************获取消息的用户****************************************** /
		//final String toChatUsername = intent.getStringExtra("from"); 
		final UserDao userdao =  new UserDao(MainActivity.this);
		final User local_user = userdao.getUser(toChatUsername);
		//判断本地是否存在
		if(CommonUtils.isNullOrEmpty(local_user.toString())){ 
			RequestParams params = new RequestParams(); 
			params.add("user", toChatUsername);
			HttpRestClient.get(Constant.USER_URL_C, params, new BaseJsonHttpResponseHandler(){
				 
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						String rawJsonResponse, Object response) {
					// TODO Auto-generated method stub
					Log.d("rawJsonResponse", rawJsonResponse); 
					if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
						nickname = "陌生人";
						headurl = "";
					} 
					Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
					if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
						if(!CommonUtils.isNullOrEmpty(lm.get("result"))){ 
							Map<String, Object> result = JsonToMapList.getMap(lm.get("result").toString());
							nickname = result.get("nickname").toString();
							headurl = result.get("headurl").toString();
							local_user.setUsername(toChatUsername);
							local_user.setNick(nickname);
							local_user.setHeaderurl(headurl);
							if(is_fran){
								userdao.saveContact(local_user);
							}else{
								userdao.saveContact_m(local_user);
							} 
							/*********************0902添加********************** /
							if (currentTabIndex != CHATHISTORYFRAGMENT) {
								FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
								trx.hide(fragments[currentTabIndex]);
								if (!fragments[CHATHISTORYFRAGMENT].isAdded()) {
									trx.add(R.id.fragment_container, fragments[CHATHISTORYFRAGMENT]);
								}
								trx.show(fragments[CHATHISTORYFRAGMENT]).commit();
							}
							/*****************0901添加****************** /
							//if (currentTabIndex == 2) {
								// 当前页面如果为聊天历史页面，刷新此页面
								if (chatHistoryFragment != null) {
									chatHistoryFragment.refresh();
								}
							//}
							/******************0901添加end***************** /
						}else{
							nickname = "陌生人";
							headurl = "";
						}
					}else{
						nickname = "陌生人";
						headurl = "";
					}
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, String rawJsonData,
						Object errorResponse) {
					// TODO Auto-generated method stub
					nickname = "陌生人";
					headurl = "";
				}

				@Override
				protected Object parseResponse(String rawJsonData,
						boolean isFailure) throws Throwable {
					// TODO Auto-generated method stub
					return null;
				}
				
			});
		}else{//存在就直接从数据库中取
			Log.d("local_user", ""+local_user.toString());
			nickname = "陌生人";
			headurl = "";
		}
		/****************************************获取消息的用户*****************************************/
	  }}).start();
	}
	
	
	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;

	/***
	 * 联系人变化listener
	 * 
	 */
	private class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			// 保存增加的联系人
			Map<String, User> localUsers = DemoApplication.getInstance().getContactList();
			Map<String, User> toAddUsers = new HashMap<String, User>();
			for (String username : usernameList) {
				User user = setUserHead(username);
				// 暂时有个bug，添加好友时可能会回调added方法两次
				if (!localUsers.containsKey(username)) {
					get_add_info(username,true);
					//userDao.saveContact(user); 暂时屏蔽（查看运行效果什么样）
				}
				toAddUsers.put(username, user);
			}
			localUsers.putAll(toAddUsers);
			// 刷新ui
			if (currentTabIndex == CONTACTLISTFRAGMENT)
				contactListFragment.refresh();

		}

		@Override
		public void onContactDeleted(final List<String> usernameList) {
			// 被删除
			Map<String, User> localUsers = DemoApplication.getInstance().getContactList();
			for (String username : usernameList) {
				localUsers.remove(username);
				userDao.deleteContact(username);
				inviteMessgeDao.deleteMessage(username);
			}
			// 刷新ui
			/*if (currentTabIndex == 1)
				contactListFragment.refresh();
			updateUnreadLabel();*/
			runOnUiThread(new Runnable(){
				@Override
				public void run() {
					//如果正在与此用户的聊天页面
					if (ChatActivity.activityInstance != null && usernameList.contains(ChatActivity.activityInstance.getToChatUsername())) {
						Toast.makeText(MainActivity.this, ChatActivity.activityInstance.getToChatUsername()+"已把你从他好友列表里移除", 1).show();
						ChatActivity.activityInstance.finish();
					}
					updateUnreadLabel();
									
				}
			});
			// 刷新ui
			if (currentTabIndex == CONTACTLISTFRAGMENT)
				contactListFragment.refresh();

		}

		@Override
		public void onContactInvited(String username, String reason) {
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不要重复提醒
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			//获取对方数据
			get_add_info(username, false);
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setReason(reason);
			Log.d( username + "请求加你为好友,reason: " + reason);
			// 设置相应status
			msg.setStatus(InviteMesageStatus.BEINVITEED);
			notifyNewIviteMessage(msg);

		}

		@Override
		public void onContactAgreed(String username) {
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			//获取对方数据
			//get_add_info(username);
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			Log.d( username + "同意了你的好友请求");
			msg.setStatus(InviteMesageStatus.BEAGREED);
			notifyNewIviteMessage(msg);

		}

		@Override
		public void onContactRefused(String username) {
			// 参考同意，被邀请实现此功能,demo未实现

		}
		
		/*
		public void get_add_info(final String toChatUsername,final boolean is_fran ){
			new Thread(new Runnable() {
				public void run() {
			/****************************************获取消息的用户****************************************** /
			//final String toChatUsername = intent.getStringExtra("from"); 
			final UserDao userdao =  new UserDao(MainActivity.this);
			final User local_user = userdao.getUser(toChatUsername);
			//判断本地是否存在
			if(CommonUtils.isNullOrEmpty(local_user.toString())){ 
				RequestParams params = new RequestParams(); 
				params.add("user", toChatUsername);
				HttpRestClient.get(Constant.USER_URL_C, params, new BaseJsonHttpResponseHandler(){
					 
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							String rawJsonResponse, Object response) {
						// TODO Auto-generated method stub
						Log.d("rawJsonResponse", rawJsonResponse); 
						if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
							nickname = "陌生人";
							headurl = "";
						} 
						Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
						if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
							if(!CommonUtils.isNullOrEmpty(lm.get("result"))){ 
								Map<String, Object> result = JsonToMapList.getMap(lm.get("result").toString());
								nickname = result.get("nickname").toString();
								headurl = result.get("headurl").toString();
								local_user.setUsername(toChatUsername);
								local_user.setNick(nickname);
								local_user.setHeaderurl(headurl);
								if(is_fran){
									userdao.saveContact(local_user);
								}else{
									userdao.saveContact_m(local_user);
								} 
							}else{
								nickname = "陌生人";
								headurl = "";
							}
						}else{
							nickname = "陌生人";
							headurl = "";
						}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, String rawJsonData,
							Object errorResponse) {
						// TODO Auto-generated method stub
						nickname = "陌生人";
						headurl = "";
					}

					@Override
					protected Object parseResponse(String rawJsonData,
							boolean isFailure) throws Throwable {
						// TODO Auto-generated method stub
						return null;
					}
					
				});
			}else{//存在就直接从数据库中取
				Log.d("local_user", ""+local_user.toString());
				nickname = "陌生人";
				headurl = "";
			}
			/****************************************获取消息的用户***************************************** /
		  }}).start();
		}*/

	}

	/**
	 * 保存提示新消息
	 * 
	 * @param msg
	 */
	private void notifyNewIviteMessage(InviteMessage msg) {
		saveInviteMsg(msg);
		// 提示有新消息
		EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

		// 刷新bottom bar消息未读数
		updateUnreadAddressLable();
		// 刷新好友页面ui
		if (currentTabIndex == CONTACTLISTFRAGMENT)
			contactListFragment.refresh();
	}
	/**
	 * 保存邀请等msg
	 * @param msg
	 */
	private void saveInviteMsg(InviteMessage msg) {
		// 保存msg
		inviteMessgeDao.saveMessage(msg);
		// 未读数加1
		User user = DemoApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME);
		user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
	}
	
	
	/**
	 * set head
	 * @param username
	 * @return
	 */
	User setUserHead(String username) {
		User user = new User();
		user.setUsername(username);
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
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(
					0, 1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
		return user;
	}

	/**
	 * 连接监听listener
	 * 
	 */
	private class MyConnectionListener implements ConnectionListener {

		@Override
		public void onConnected() {
			
			if (fragments[2].isAdded()) {//判断绘画界面是否被添加
				chatHistoryFragment.errorItem.setVisibility(View.GONE);
			}
			//chatHistoryFragment.errorItem.setVisibility(View.GONE);
		}

		@Override
		public void onDisConnected(String errorString) {
			if (errorString != null && errorString.contains("conflict")) {
				// 显示帐号在其他设备登陆dialog
				showConflictDialog();
			} else {
				//if (chatHistoryFragment != null) {
				if (fragments[2].isAdded()) {//判断绘画界面是否被添加 
					chatHistoryFragment.errorItem.setVisibility(View.VISIBLE);
					if(NetUtils.hasNetwork(MainActivity.this))
					chatHistoryFragment.errorText.setText("连接不到聊天服务器");
					else
					chatHistoryFragment.errorText.setText("当前网络不可用，请检查网络设置");
				}
			
			}
		}

		@Override
		public void onReConnected() {
			//if (chatHistoryFragment != null) {
			if (fragments[2].isAdded()) {//判断绘画界面是否被添加 
				chatHistoryFragment.errorItem.setVisibility(View.GONE);
			}
			//chatHistoryFragment.errorItem.setVisibility(View.GONE);
		}

		@Override
		public void onReConnecting() {
		}

		@Override
		public void onConnecting(String progress) {
		}

	}

	/**
	 * MyGroupChangeListener
	 */
	private class MyGroupChangeListener implements GroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
			boolean hasGroup = false;
			for(EMGroup group : EMGroupManager.getInstance().getAllGroups()){
				if(group.getGroupId().equals(groupId)){
					hasGroup = true;
					break;
				}
			}
			if(!hasGroup)
				return;
			
			// 被邀请
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(inviter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(inviter + "邀请你加入了群聊"));
			// 保存邀请消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();

			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					if (currentTabIndex == CHATHISTORYFRAGMENT)
						chatHistoryFragment.refresh();
					if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});

		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter, String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee, String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			// 提示用户被T了，demo省略此步骤
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					try {
						updateUnreadLabel();
						if (currentTabIndex == CHATHISTORYFRAGMENT)
							chatHistoryFragment.refresh();
						if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
							GroupsActivity.instance.onResume();
						}
					} catch (Exception e) {
						Log.e( "refresh exception " + e.getMessage());
					}

				}
			});
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {
			// 群被解散
			// 提示用户群被解散,demo省略
			// 刷新ui
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					if (currentTabIndex == CHATHISTORYFRAGMENT)
						chatHistoryFragment.refresh();
					if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});

		}

		@Override
		public void onApplicationReceived(String groupId, String groupName, String applyer, String reason) {
			// 用户申请加入群聊
			InviteMessage msg = new InviteMessage();
			msg.setFrom(applyer);
			msg.setTime(System.currentTimeMillis());
			msg.setGroupId(groupId);
			msg.setGroupName(groupName);
			msg.setReason(reason);
			Log.d( applyer + " 申请加入群聊：" + groupName);
			msg.setStatus(InviteMesageStatus.BEAPPLYED);
			notifyNewIviteMessage(msg);
		}

		@Override
		public void onApplicationAccept(String groupId, String groupName, String accepter) {
			//加群申请被同意
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(accepter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(accepter + "同意了你的群聊申请"));
			// 保存同意消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			EMNotifier.getInstance(getApplicationContext()).notifyOnNewMsg();
			
			runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					if (currentTabIndex == CHATHISTORYFRAGMENT)
						chatHistoryFragment.refresh();
					if (CommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});
		}

		@Override
		public void onApplicationDeclined(String groupId, String groupName, String decliner, String reason) {
			//加群申请被拒绝，demo未实现
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isConflict) {
			try {//增加 try 模块
				updateUnreadLabel();
				updateUnreadAddressLable();
				EMChatManager.getInstance().activityResumed();
				
			} catch (Exception e) {
				// TODO: handle exception
			}
				 
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private android.app.AlertDialog.Builder conflictBuilder;
	private boolean isConflictDialogShow;

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {
		isConflictDialogShow = true;
		DemoApplication.getInstance().logout();

		if (!MainActivity.this.isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(MainActivity.this);
				conflictBuilder.setTitle("下线通知");
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						finish();
						startActivity(new Intent(MainActivity.this, LoginActivity.class));
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				Log.e("---------color conflictBuilder error" + e.getMessage());
			}

		}

	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(getIntent().getBooleanExtra("conflict", false) && !isConflictDialogShow)
			showConflictDialog();
	}

	@Override
	public void onMySelected(int i) {
		// TODO Auto-generated method stub
		change_f(i);//回调切换fragment
	}
	
	@Override
	public void onrefresh(String adr) {
		// TODO Auto-generated method stub
		//change_f(i);//回调切换fragment
		if (fragments[4].isAdded()) {//判断设置界面是否被添加
			settingFragment.tv_user_zainadongtai.setText(adr);
		}
		//(settingFragment)findViewById(R.id.tv_user_zainadongtai);
	}
	
	   public void showFloatingButton(){  
	        //按钮被点击  
	       this.startService(new Intent(this, com.ccxt.whl.service.FloatService.class));  
	// new TableShowView(this).fun(); 如果只是在activity中启动   
	// 当activity跑去后台的时候[暂停态，或者销毁态] 我们设置的显示到桌面的view也会消失  
	// 所以这里采用的是启动一个服务，服务中创建我们需要显示到table上的view，并将其注册到windowManager上
//	       finish();
	    }

	/**********************************检查更新********************************* /
	@Override
	public void PostUpdateChoiceResponse(JSONObject arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void checkUpdateResponse(KirinCheckState state,
			HashMap<String, String> dataContainer) {
		// TODO Auto-generated method stub
System.out.println("==============================================================================");
		// TODO Auto-generated method stub
		if (state == KirinCheckState.ALREADY_UP_TO_DATE) {
			Log.d("demodemo", "stat == KirinCheckState.ALREADY_UP_TO_DATE");
			// KirinAgent.postUserChoice(getApplicationContext(),
			// choice);//choice 几种升级类型：0-未更新，1-不更新，2-稍后更新，3-手动更新，4-强制更新
		} else if (state == KirinCheckState.ERROR_CHECK_VERSION) {
			Log.d("demodemo", "KirinCheckState.ERROR_CHECK_VERSION");
		} else if (state == KirinCheckState.NEWER_VERSION_FOUND) {
			Log.d("demodemo", "KirinCheckState.NEWER_VERSION_FOUND"
					+ dataContainer.toString());

			String isForce = dataContainer.get("updatetype");
			String noteInfo = dataContainer.get("note");
			String publicTime = dataContainer.get("time");
			String appUrl = dataContainer.get("appurl");
			String appName = dataContainer.get("appname");
			String newVersionName = dataContainer.get("version");
			String newVersionCode = dataContainer.get("buildid");
			String attachInfo = dataContainer.get("attach");

			// 这些信息都是在mtj.baidu.com上您选择的小流量定制信息
			utestUpdate.doUpdate(appUrl, noteInfo);
		}
	
	}  
	 /**********************************检查更新*********************************/
	 /**
	  * MainActivity 类 里面共用的添加用户方法
	  * @param toChatUsername  要添加的唯一id
	  * @param is_fran  声明是否是陌生人或者好友 : true则添加好友，false则添加为陌生人
	  */
	public void check_update() {
		 
			
		// TODO Auto-generated method stub
		/****************************************获取消息的用户******************************************/
		 
			RequestParams params = new RequestParams(); 
			params.add("versioncode", String.valueOf(DemoApplication.getInstance().getVersionCode()));
			HttpRestClient.get(Constant.CHECK_UPDATE, params, new BaseJsonHttpResponseHandler(){
				 
				@Override
				public void onSuccess(int statusCode, Header[] headers,
						String rawJsonResponse, Object response) {
					// TODO Auto-generated method stub
					Log.d("rawJsonResponse"+ rawJsonResponse); 
					if(!CommonUtils.isNullOrEmpty(rawJsonResponse)){
						Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
						if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
							if(!CommonUtils.isNullOrEmpty(lm.get("result").toString())){ 
								Map<String, Object> result = JsonToMapList.getMap(lm.get("result").toString());
								String versioncode = result.get("versioncode").toString();
								String content = result.get("content").toString();
								String downurl = result.get("downurl").toString();
								int thisversion = DemoApplication.getInstance().getVersionCode();
								if(thisversion < Integer.parseInt(versioncode)){
									// 这些信息都是在mtj.baidu.com上您选择的小流量定制信息
									utestUpdate.doUpdate(downurl, content);
								}
								 
							} 
						} 
					} 
					 
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, String rawJsonData,
						Object errorResponse) {
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
}
