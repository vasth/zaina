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
package com.ccxt.whl;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.util.Log;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaApplication;
import com.baidu.frontia.api.FrontiaPush;
import com.baidu.frontia.api.FrontiaStatistics;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.utils.CoordinateConvert;
import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;
import com.ccxt.whl.activity.ChatActivity;
import com.ccxt.whl.activity.MainActivity;
import com.ccxt.whl.activity.BaiduMapActivity.MyLocationListenner;
import com.ccxt.whl.activity.BaiduMapActivity.NotifyLister;
import com.ccxt.whl.db.DbOpenHelper;
import com.ccxt.whl.db.UserDao;
import com.ccxt.whl.domain.User;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.HttpRestClient;
import com.ccxt.whl.utils.JsonToMapList;
import com.ccxt.whl.utils.MyLogger;
import com.ccxt.whl.utils.PreferenceUtils;
import com.easemob.util.EMLog;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;


public class DemoApplication extends FrontiaApplication {

	public static Context applicationContext;
	private static DemoApplication instance;
	// login user name
	public final String PREF_USERNAME = "username";
	public final String PREF_USERNICKNAME = "usernickname";
	private String userName = null;
	private String userNickName = null;
	// login password
	private static final String PREF_PWD = "pwd";
	private String password = null;
	private Map<String, User> contactList;
		/**
	 * 当前用户nickname,为了苹果推送不是userid而是昵称
	 */
	public static String currentUserNick="";
	/******************************百度地图相关参数***************************************/ 
	 // 定位相关
	public LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	public NotifyLister mNotifyer = null;

	// 百度定位SDK可以返回三种坐标系，分别是bd09, bd09ll和gcj02，其中bd09ll能无偏差地显示在百度地图上。
	// gcj02是测局制定的。
	//private static final String COOR_TYPE = "gcj02";
	private static final String COOR_TYPE = "bd09ll";
	private static final String BAIDU_LOCAL_SDK_SERVICE_NAME = "com.baidu.location.service_v2.9";
	// 定位sdk提供2种定位模式，定时定位和app主动请求定位。
	// setScanSpan < 1000 则为 app主动请求定位；
	// setScanSpan>=1000,则为定时定位模式（setScanSpan的值就是定时定位的时间间隔））
	// 定时定位模式中，定位sdk会按照app设定的时间定位进行位置更新，定时回调定位结果。
	// 此种定位模式适用于希望获得连续定位结果的情况。
	// 对于单次定位类应用，或者偶尔需要一下位置信息的app，可采用app主动请求定位这种模式。
	private static final int SCAN_SPAN_TIME = 500;

	//private static final String PRODUCT_NAME = "com.youku.paike";
 
	// LocationData locData = null;
	public static BDLocation lastLocation = null;
	private static MyLogger Log = MyLogger.yLog();  
	/******************************百度地图相关参数end***************************************/ 

	@Override
	public void onCreate() {
		super.onCreate();
		/**********************新增2014-08-08*************************/
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(myListener);
		setLocationOption();
		startLocate();
		//百度Frontia初始化
		Frontia.init(this.getApplicationContext(), "yHMQouqcDqWPR5uEZ7GbW6w4");
		//统计代码段
		FrontiaStatistics stat = Frontia.getStatistics();
		stat.setReportId("32f5355664");
		stat.setAppDistributionChannel("小型升级");
		stat.enableExceptionLog();
		stat.start(SendStrategyEnum.SET_TIME_INTERVAL, 0, 10, false);
		//初始化图片下载
		initImageLoader(this);
		FrontiaPush mPush = Frontia.getPush();
		boolean isWorking = mPush.isPushWorking();
		if(isWorking){
			Log.d("mpush is runing --> stop");
			mPush.stop();
		} 
		/**********************新增2014-08-08*************************/
		int pid = android.os.Process.myPid();
		String processAppName = getAppName(pid);
		//如果使用到百度地图或者类似启动remote service的第三方库，这个if判断不能少
		if (processAppName == null || processAppName.equals("")) {
			// workaround for baidu location sdk 
			// 百度定位sdk，定位服务运行在一个单独的进程，每次定位服务启动的时候，都会调用application::onCreate
			// 创建新的进程。
			// 但环信的sdk只需要在主进程中初始化一次。 这个特殊处理是，如果从pid 找不到对应的processInfo
			// processName，
			// 则此application::onCreate 是被service 调用的，直接返回
			return;
		}
		applicationContext = this;
		instance = this;
		// 初始化环信SDK,一定要先调用init()
		Log.d("EMChat Demo initialize EMChat SDK");
		EMChat.getInstance().init(applicationContext);
		// debugmode设为true后，就能看到sdk打印的log了
		EMChat.getInstance().setDebugMode(false);

		// 获取到EMChatOptions对象
		EMChatOptions options = EMChatManager.getInstance().getChatOptions();
		// 默认添加好友时，是不需要验证的，改成需要验证
		options.setAcceptInvitationAlways(false);
		// 设置收到消息是否有新消息通知，默认为true
		options.setNotificationEnable(PreferenceUtils.getInstance(applicationContext).getSettingMsgNotification());
		// 设置收到消息是否有声音提示，默认为true
		options.setNoticeBySound(PreferenceUtils.getInstance(applicationContext).getSettingMsgSound());
		// 设置收到消息是否震动 默认为true
		options.setNoticedByVibrate(PreferenceUtils.getInstance(applicationContext).getSettingMsgVibrate());
		// 设置语音消息播放是否设置为扬声器播放 默认为true
		options.setUseSpeaker(PreferenceUtils.getInstance(applicationContext).getSettingMsgSpeaker());
		
		//设置notification消息点击时，跳转的intent为自定义的intent
		options.setOnNotificationClickListener(new OnNotificationClickListener() {
			
			@Override
			public Intent onNotificationClick(EMMessage message) {
				Intent intent = new Intent(applicationContext, ChatActivity.class);
				ChatType chatType = message.getChatType();
				if(chatType == ChatType.Chat){ //单聊信息
					intent.putExtra("userId", message.getFrom());
					intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
				}else{ //群聊信息
					//message.getTo()为群聊id
					intent.putExtra("groupId", message.getTo());
					intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
				}
				return intent;
			}
		});
		//设置一个connectionlistener监听账户重复登陆
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
		//取消注释，app在后台，有新消息来时，状态栏的消息提示换成自己写的
//		options.setNotifyText(new OnMessageNotifyListener() {
//			
//			@Override
//			public String onNewMessageNotify(EMMessage message) {
//				//可以根据message的类型提示不同文字，demo简单的覆盖了原来的提示
//				return "你的好基友" + message.getFrom() + "发来了一条消息哦";
//			}
//			
//			@Override
//			public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
//				return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
//			}
//		});
		
		options.setNotifyText(new OnMessageNotifyListener() {
			
			@Override
			public String onNewMessageNotify(EMMessage message) {
				UserDao dao = new UserDao(applicationContext);
				User user = dao.getUser(message.getFrom());   
				
				//判断本地是否存在
				if(!CommonUtils.isNullOrEmpty(user.toString()) ){ 
					if(user.getHeaderurl()!=null&&user.getNick()!=null){
						 Log.d("application local_user_is-pass");
						//continue;//跳过
						 String nick = "";
							if(CommonUtils.isNullOrEmpty(user.getNick())){
								nick = "联系人";
							}
							nick = ""+user.getNick();
						return nick + "发来了一条消息";
					}
				}
				
				/***********后台运行时请求来消息的陌生人信息***********/
				String nickname_tmp = "";
				String headurl_tmp = "";
				 //判断头像、昵称、唯一id是否
				if(CommonUtils.isNullOrEmpty(user.getUsername())||
						CommonUtils.isNullOrEmpty(user.getHeaderurl())||
							CommonUtils.isNullOrEmpty(user.getNick())){
			
					String httpUrl = Constant.BASE_URL
							+ Constant.USER_URL_C + "user="
							+ message.getFrom();
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
										Log.d("NotifyListener nickname_tmp and headurl_tmp "+nickname_tmp+" "+headurl_tmp);
										 
									} 
								} 
							} 
							
							if(!CommonUtils.isNullOrEmpty(nickname_tmp)&&!CommonUtils.isNullOrEmpty(headurl_tmp)){
						    	User user_temp =  new User();
						    	user_temp.setUsername(message.getFrom());
						    	user_temp.setNick(nickname_tmp);
						    	user_temp.setHeaderurl(headurl_tmp);
								
						    	dao.saveContact_m(user_temp);
						    	Log.d("NotifyListener saveContact_m have");
						    } 
						}  
						
					} catch (ClientProtocolException e) {
						Log.e("application"+ e.getMessage().toString());
					} catch (IOException e) {
						Log.e("application"+ e.getMessage().toString());
					} catch (Exception e) {
						Log.e("application"+ e.getMessage().toString());
					}
				}
				
				return nickname_tmp + "发来了一条消息";
				
			}
			
			@Override
			public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
				return fromUsersNum + "个联系人，发来了" + messageNum + "条消息";
			}

			@Override
			public String onSetNotificationTitle(EMMessage message) {
				// TODO Auto-generated method stub
				UserDao dao = new UserDao(applicationContext);
				User user = dao.getUser(message.getFrom()); 
				//判断本地是否存在
				if(!CommonUtils.isNullOrEmpty(user.toString()) ){ 
					if(user.getHeaderurl()!=null&&user.getNick()!=null){
						 Log.d("application  local_user_is-pass");
						//continue;//跳过
						 String nick = "";
							if(CommonUtils.isNullOrEmpty(user.getNick())){
								nick = "联系人";
							}
							nick = ""+user.getNick();
						return nick ;
					}
				}
				return "微话聊";
			}
		});
		
		
		//MobclickAgent.onError(applicationContext);
	}

	public static DemoApplication getInstance() {
		return instance;
	}
	
//	List<String> list = new ArrayList<String>();
//	list.add("1406713081205");
//	options.setReceiveNotNoifyGroup(list);
	/**
	 * 获取内存中好友user list
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {
//		if(getUserName() != null &&contactList == null)
		if(getUser() != null &&contactList == null)
		{
			UserDao dao = new UserDao(applicationContext);
			// 获取本地好友user list到内存,方便以后获取好友list
			contactList = dao.getContactList();
		}
		return contactList;
	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, User> contactList) {
		this.contactList = contactList;
	}

	public void setStrangerList(Map<String, User> List) {

	}

	/**
	 * 获取当前huanxin登陆用户
	 * 加密字符串
	 * @return
	 */
	//public String getUserName() {
	public String getUser() {
		if (userName == null) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			userName = preferences.getString(PREF_USERNAME, null);
		}
		return userName;
	}

	/**
	 * 获取huanxin密码
	 * 
	 * @return
	 */
	public String getPassword() {
		if (password == null) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			password = preferences.getString(PREF_PWD, null);
		}
		return password;
	}
	/**
	 * 获取当前登陆用户昵称
	 * 
	 * @return
	 */
	public String getUsernNickName() {
		if (userNickName == null) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			userNickName = preferences.getString(PREF_USERNICKNAME, null);
		}
		return userNickName;
	}
	/**
	 * 设置用户昵称
	 * 
	 * @param user
	 */
	public void setUserNickName(String NickName) {
		if (NickName != null) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			SharedPreferences.Editor editor = preferences.edit();
			if (editor.putString(PREF_USERNICKNAME, NickName).commit()) {
				userNickName = NickName;
			}
		}
	}
	/**
	 * 设置huanxin用户名
	 * 加密字符串
	 * @param user
	 */
//	public void setUserName(String username) {
	public void setUser(String username) {
		if (username != null) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
			SharedPreferences.Editor editor = preferences.edit();
			if (editor.putString(PREF_USERNAME, username).commit()) {
				userName = username;
			}
		}
	}

	/**
	 * 设置密码
	 * 下面的实例代码 只是demo，实际的应用中需要加password 加密后存入 preference
	 * 环信sdk 内部的自动登录需要的密码，已经加密存储了
	 * @param pwd
	 */
	public void setPassword(String pwd) {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
		SharedPreferences.Editor editor = preferences.edit();
		if (editor.putString(PREF_PWD, pwd).commit()) {
			password = pwd;
		}
	}

	/**
	 * 退出登录,清空数据
	 */
	public void logout() {
		// 先调用sdk logout，在清理app中自己的数据
		EMChatManager.getInstance().logout();
		DbOpenHelper.getInstance(applicationContext).closeDB();
		// reset password to null
		setPassword(null);
		setContactList(null);

	}

	private String getAppName(int pID) {
		String processName = null;
		ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List l = am.getRunningAppProcesses();
		Iterator i = l.iterator();
		PackageManager pm = this.getPackageManager();
		while (i.hasNext()) {
			ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
			try {
				if (info.pid == pID) {
					CharSequence c = pm.getApplicationLabel(pm.getApplicationInfo(info.processName, PackageManager.GET_META_DATA));
					// Log.d("Process", "Id: "+ info.pid +" ProcessName: "+
					// info.processName +"  Label: "+c.toString());
					// processName = c.toString();
					processName = info.processName;
					return processName;
				}
			} catch (Exception e) {
				// Log.d("Process", "Error>> :"+ e.toString());
			}
		}
		return processName;
	}
	
	public String getVersionName(){
		   try {
	          PackageManager manager = this.getPackageManager();
	           PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	          String version = info.versionName;
		          return  version;
		      } catch (Exception e) {
		           e.printStackTrace(); 
		     }
		   return "0";
	   }
	
	public  int getVersionCode()//获取版本号(内部识别号)  
	{  
		  try {
	          PackageManager manager = this.getPackageManager();
	           PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0); 
		          return   info.versionCode;
		      } catch (Exception e) {
		    	  return 0;  
		     }
		  /*
	    try {  
	        PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);  
	        return pi.versionCode;  
	    } catch (NameNotFoundException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	        return 0;  
	    }  */
	}  
	
	/** 初始化ImageLoader */
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				Constant.CACHE_DIR_IMAGE );// 获取到缓存的目录地址
		Log.d("cacheDir"+ cacheDir.getPath() );
		// 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		// 线程池内加载的数量
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				/***新增*** /
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))  
		        .memoryCacheSize(2 * 1024 * 1024)  
		        .memoryCacheSizePercentage(13) // default  
		        /***新增end***/
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}
	
	class MyConnectionListener implements ConnectionListener{
		@Override
		public void onReConnecting() {
		}
		
		@Override
		public void onReConnected() {
		}
		
		@Override
		public void onDisConnected(String errorString) {
			if (errorString != null && errorString.contains("conflict")) {
				Intent intent =new Intent(applicationContext, MainActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("conflict", true);
				startActivity(intent);
			}
			
		}
		
		@Override
		public void onConnecting(String progress) {
			
		}
		
		@Override
		public void onConnected() {
		}
	}
	
	/***************************百度定位相关**********************************/
	// 设置相关参数
		private void setLocationOption() {
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps 
			mLocClient.setLocOption(option);  
			// 设置坐标类型 ,
			option.setCoorType(COOR_TYPE); 
			option.setPoiExtraInfo(true);
			// 可以返回地理位置信息，例如“北京市海淀区海淀大街8号”,必须设置all并且是wifi定位的情况下才可以拿到详细地理位置信息
			option.setAddrType("all");
			option.setScanSpan(SCAN_SPAN_TIME);
			option.setPriority(LocationClientOption.NetWorkFirst); // 设置网络优先,不设置，默认是gps优先
			option.setPoiNumber(10);
			option.disableCache(true);// true表示禁用缓存定位，false表示启用缓存定位
			//option.setProdName(PRODUCT_NAME);
			mLocClient.setLocOption(option);
		}
	 
		/**
		 * 获取当前位置
		 * @return
		 */
		public BDLocation getlastloc() {
			if (lastLocation != null) { 
				return lastLocation; 
			}
			return null;
		}
		/**
		 * 开始定位
		 * @Title: startLocate
		 * @return void
		 * @date 2013-7-2 下午4:32:26
		 */
		public void startLocate() {
			if (mLocClient.isStarted()) {
				mLocClient.requestLocation();
			} else {
				mLocClient.start();
			}
		}

		/**
		 * 想主动结束定位调用这个方法
		 * @Title: stopLocate
		 * @return void
		 * @date 2013-7-2 下午4:31:12
		 */
		public void stopLocate() {
			if (mLocClient.isStarted()) {
				mLocClient.stop();
			}
		}

		/**
		 * 定位返回值的监听
		 * @Package com.baidu.locTest
		 * @ClassName: LocationListenner
		 * @author 
		 * @mail 
		 * @date 2013-7-2 下午4:31:49
		 */
		/**
		 * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
		 */
		public class MyLocationListenner implements BDLocationListener {
			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null) {
					return;
				}
				Log.d("map On location change received:" + location);
				Log.d("map addr:" + location.getAddrStr());
				Log.d("map streed:" + location.getStreet());
				//sendButton.setEnabled(true);
				//if (progressDialog != null) {
				//	progressDialog.dismiss();
				//}

				if (lastLocation != null) {
					if (lastLocation.getLatitude() == location.getLatitude() && lastLocation.getLongitude() == location.getLongitude()) {
						Log.d("map same location, skip refresh");
						stopLocate();
						// mMapView.refresh(); //need this refresh?
						return;
					}
				}

				lastLocation = location;

				GeoPoint gcj02Point = new GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));
				EMLog.d("loc", "GCJ-02 loc:" + gcj02Point);
				//GeoPoint point = CoordinateConvert.fromGcjToBaidu(gcj02Point);
				//EMLog.d("loc", "converted BD-09 loc:" + gcj02Point);
				stopLocate();
			
			}

			public void onReceivePoi(BDLocation poiLocation) {
				if (poiLocation == null) {
					return;
				}
			}
		}
		/***************************百度定位相关**********************************/
		
		/*private void get_add_info(final String toChatUsername, final boolean is_fran) {
			// TODO Auto-generated method stub
			*//****************************************获取消息的用户******************************************//*
			//final String toChatUsername = intent.getStringExtra("from"); 
			final UserDao userdao =  new UserDao(applicationContext);
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
			*//****************************************获取消息的用户*****************************************//*
		}*/
}
