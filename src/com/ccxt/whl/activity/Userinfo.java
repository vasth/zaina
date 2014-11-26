 
package com.ccxt.whl.activity;

import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ccxt.whl.Constant;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.db.UserDao;
import com.ccxt.whl.domain.User;
import com.ccxt.whl.gushi.User_gushi_Activity;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.DeviceUuidFactory;
import com.ccxt.whl.utils.HttpRestClient;
import com.ccxt.whl.utils.ImageOptions;
import com.ccxt.whl.utils.JSONHelper;
import com.ccxt.whl.utils.JsonToMapList;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.easemob.exceptions.EaseMobException;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 设置界面
 * 
 * @author Administrator
 * 
 */
public class Userinfo extends BaseActivity implements OnClickListener {
 
	/**********************************新增用户信息*****************************************/  
	/**
	 * 用户头像imageView
	 */
	private ImageView iv_userinfo_photo;
	
	/**
	 * 用户昵称
	 */
	private TextView tv_userinfo_nicheng;
	/**
	 * 用户性别
	 */
	private TextView tv_userinfo_xingbie;
	/**
	 * 用户年龄
	 */
	private TextView tv_userinfo_nianling;
	/**
	 * 用户地区
	 */
	private TextView tv_userinfo_chengshi;
	/**
	 * 用户职业
	 */
	private TextView tv_userinfo_zhiye;
	/**
	 * 用户签名
	 */
	private TextView tv_userinfo_qianming;
	/**
	 * 用户在哪动态
	 */
	private TextView tv_userinfo_zainadongtai;
	
	private String UserPic = null;
	private String UserNickName = null;
	private String UserSex = null;
	private String UserAge = null;
	private String UserArea = null;
	private String UserZaina = null;
	private String UserZhiye = null;
	private String UserQianming = null;
	
	private Button btn_huihua;
	private Button btn_add_f;
	private Button btn_lahei;
	private Button btn_jubao;
	
	private String userId = null;//用户唯一id
	
	public static final int REQUEST_CODE_ADD_TO_CONTACT = 226;
	public static final int REQUEST_CODE_ADD_TO_BLACKLIST = 225;
	private ProgressDialog progressDialog;
	
	private LinearLayout about_gushi;
	private TextView ta_tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfo);
		 
		iv_userinfo_photo = (ImageView)findViewById(R.id.iv_userinfo_photo);
		tv_userinfo_nicheng = (TextView)findViewById(R.id.tv_userinfo_nicheng);
		tv_userinfo_xingbie = (TextView)findViewById(R.id.tv_userinfo_xingbie);
		tv_userinfo_nianling = (TextView)findViewById(R.id.tv_userinfo_nianling);
		tv_userinfo_chengshi = (TextView)findViewById(R.id.tv_userinfo_chengshi);
		tv_userinfo_zainadongtai = (TextView)findViewById(R.id.tv_userinfo_zainadongtai);
		//--
		tv_userinfo_zhiye = (TextView)findViewById(R.id.tv_userinfo_zhiye);
		tv_userinfo_qianming = (TextView)findViewById(R.id.tv_userinfo_qianming);
		
		btn_huihua = (Button)findViewById(R.id.btn_huihua);
		btn_add_f = (Button)findViewById(R.id.btn_add_f);
		btn_lahei = (Button)findViewById(R.id.btn_lahei);
		btn_jubao = (Button)findViewById(R.id.btn_jubao);
		
		about_gushi = (LinearLayout)findViewById(R.id.about_gushi);
		ta_tv = (TextView)findViewById(R.id.ta_tv);
		
		userId = getIntent().getStringExtra("userId");
		//判断是否是自己
		if(userId.equals(DemoApplication.getInstance().getUser())){
			ta_tv.setText("我的故事");
			btn_huihua.setVisibility(View.GONE);
			btn_add_f.setVisibility(View.GONE);
			btn_lahei.setVisibility(View.GONE);
			btn_jubao.setVisibility(View.GONE);
		}
		//判断是否是好友，如果是就把加好友隐藏
		if(DemoApplication.getInstance().getContactList().containsKey(userId)){
			btn_add_f.setVisibility(View.GONE);
		}
		//判断是否是小客服或者是管理员，如果是就把加好友 和拉黑隐藏
		if(userId.equals(Constant.KEFU)||userId.equals("admin")){
			btn_add_f.setVisibility(View.GONE);
			btn_lahei.setVisibility(View.GONE);
			btn_jubao.setVisibility(View.GONE);
		}
		
		
		about_gushi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(Userinfo.this , User_gushi_Activity.class).putExtra("userId", userId));
			}
		});
		
		UserPic = getIntent().getStringExtra("headurl");
		UserNickName = getIntent().getStringExtra("nickname");
		UserSex = getIntent().getStringExtra("UserSex");
		UserAge = getIntent().getStringExtra("UserAge");
		UserArea = getIntent().getStringExtra("UserArea");
		UserZaina = getIntent().getStringExtra("UserZaina");
		
		if(CommonUtils.isNullOrEmpty(UserSex)&&CommonUtils.isNullOrEmpty(UserAge)
				&&CommonUtils.isNullOrEmpty(UserArea)&&CommonUtils.isNullOrEmpty(UserZaina)
				||CommonUtils.isNullOrEmpty(UserZhiye)||CommonUtils.isNullOrEmpty(UserQianming)){
			
			
			RequestParams params = new RequestParams();
			params.add("f_user", DemoApplication.getInstance().getUser());
			params.add("user", userId);
			DeviceUuidFactory uuid = new DeviceUuidFactory(this); 
			String uid = uuid.getDeviceUuid().toString(); 
			params.add("uid", uid);//新增设备请求
			HttpRestClient.get(Constant.USER_URL_I, params, new BaseJsonHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						String rawJsonResponse, Object response) {
					// TODO Auto-generated method stub
					if(!CommonUtils.isNullOrEmpty(rawJsonResponse)){
						Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
						if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
							if(!CommonUtils.isNullOrEmpty(lm.get("result").toString())){ 
								Map<String, Object> result = JsonToMapList.getMap(lm.get("result").toString());
								UserNickName = result.get("nickname").toString();
								UserPic = result.get("headurl").toString(); 
								UserSex = result.get("sex").toString();
								UserAge = result.get("age").toString();
								UserArea = result.get("city").toString();
								UserZaina = result.get("zaina").toString();
								UserZhiye = result.get("zhiye").toString();
								UserQianming = result.get("qianming").toString();
								
								ImageLoader.getInstance().displayImage(UserPic, iv_userinfo_photo, ImageOptions.getOptions());
								tv_userinfo_nicheng.setText(UserNickName);
								tv_userinfo_xingbie.setText(UserSex.equals("1")? "男":"女");
								tv_userinfo_nianling.setText(UserAge);
								tv_userinfo_chengshi.setText(UserArea);
								tv_userinfo_zainadongtai.setText(UserZaina);
								tv_userinfo_zhiye.setText(UserZhiye); 
								tv_userinfo_qianming.setText(UserQianming);
								//Log.d(TAG,"nickname_tmp and headurl_tmp "+nickname_tmp+" "+headurl_tmp);
								/*User user_temp =  new User();
						    	user_temp.setUsername(user.getUsername());
						    	user_temp.setNick(nickname_tmp);
						    	user_temp.setHeaderurl(headurl_tmp); 
						    	dao.saveContact(user_temp);*/
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
				
			
		}else{
			tv_userinfo_xingbie.setText(UserSex.equals("1")? "男":"女");
			tv_userinfo_nianling.setText(UserAge);
			tv_userinfo_chengshi.setText(UserArea);
			tv_userinfo_zainadongtai.setText(UserZaina);
		}
		 
		ImageLoader.getInstance().displayImage(UserPic, iv_userinfo_photo, ImageOptions.getOptions());
		tv_userinfo_nicheng.setText(UserNickName);
		btn_huihua.setOnClickListener(this);
		btn_add_f.setOnClickListener(this); 
		btn_lahei.setOnClickListener(this);
		btn_jubao.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.btn_huihua:
			Intent intent = new Intent(this, ChatActivity.class);
			intent.putExtra("userId", userId);
			/****缺少判断是否已经插入数据库****/
			User local_user = new User();
			local_user.setUsername(userId);
			local_user.setNick(UserNickName);
			local_user.setHeaderurl(UserPic);
			UserDao userdao = new UserDao(this);
			userdao.saveContact_m(local_user);
			startActivity(intent);
			break;
		case R.id.btn_add_f:
			Intent intent_add = new Intent(this, AlertDialog.class);
			intent_add.putExtra("msg", "添加好友");
			intent_add.putExtra("editTextShow", true);
			intent_add.putExtra("cancel", true);
			//intent.putExtra("position", position);
			startActivityForResult(intent_add,Userinfo.REQUEST_CODE_ADD_TO_CONTACT);
			break;
		case R.id.btn_lahei:
			Intent intent_del = new Intent(this, AlertDialog.class);
			intent_del.putExtra("msg", "移入到黑名单？");
			intent_del.putExtra("cancel", true);
			//intent_del.putExtra("position", position);
			startActivityForResult(intent_del,Userinfo.REQUEST_CODE_ADD_TO_BLACKLIST);
			
			break; 
		case R.id.btn_jubao:
			String username = DemoApplication.getInstance().getUser();
			EMConversation conversation = EMChatManager.getInstance().getConversation(userId);//获取和对方通话消息的20条
			String startMsgId = conversation.getLastMessage().getMsgId();
			//System.out.println("startMsgId"+startMsgId);
			int msgcount = conversation.getMsgCount();
			List<EMMessage> messages ;
			if(msgcount<20){
				  messages = conversation.loadMoreMsgFromDB(startMsgId, 20);
				  messages.add(conversation.getLastMessage()) ;
			}else{
				  messages = conversation.loadMoreMsgFromDB(startMsgId, msgcount);
				  messages.add(conversation.getLastMessage()) ;
			}
			 
			//System.out.println(JSONHelper.toJSON(messages));
			//conversation.getLastMessage();
			conversation.loadMoreMsgFromDB(startMsgId, 1);
			/*****/
			try {
				EMContactManager.getInstance().addUserToBlackList(userId, true);
				//Toast.makeText(getApplicationContext(), "移入黑名单成功", 0).show();
			} catch (EaseMobException e) {
				e.printStackTrace();
				//Toast.makeText(getApplicationContext(), "移入黑名单失败", 0).show();
			}
			
			RequestParams params = new RequestParams();
			params.add("f_user", username);
			params.add("user", userId);
			DeviceUuidFactory uuid = new DeviceUuidFactory(this); 
			String uid = uuid.getDeviceUuid().toString(); 
			params.add("uid", uid);//新增设备请求
			params.add("black_content",JSONHelper.toJSON(messages));//新增黑名单内容
			HttpRestClient.get(Constant.USER_BLACK, params, new BaseJsonHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] headers,
						String rawJsonResponse, Object response) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "举报成功", 0).show();
				}

				@Override
				public void onFailure(int statusCode, Header[] headers,
						Throwable throwable, String rawJsonData,
						Object errorResponse) {
					// TODO Auto-generated method stub
					
				}

				@Override
				protected Object parseResponse(String rawJsonData, boolean isFailure)
						throws Throwable {
					// TODO Auto-generated method stub
					return null;
				}
				
			});/**/
			
			break; 
		default:
			break;
		}
	}
	 
	/**
	 * onActivityResult
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) { // 清空消息
			if (requestCode == REQUEST_CODE_ADD_TO_CONTACT) { // 加好友
				//EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1)); 
				String sendstr = data.getStringExtra("edittext");
				if(CommonUtils.isNullOrEmpty(sendstr)){
					sendstr = "打招呼";
				}
				//addUserToBlacklist(deleteMsg.getFrom());
				addContact(userId,sendstr);
			}else if(requestCode == REQUEST_CODE_ADD_TO_BLACKLIST){
				//EMMessage deleteMsg = (EMMessage) adapter.getItem(data.getIntExtra("position", -1));
				addUserToBlacklist(userId);
			}
		}
	}
	
	/**
	 *  添加contact
	 * @param view
	 */
	public void addContact(final String user ,final String msg){
		//if(DemoApplication.getInstance().getUserName().equals(nameText.getText().toString())){
		if(DemoApplication.getInstance().getUser().equals(user)){
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg", "不能添加自己"));
			return;
		}
		
		if(DemoApplication.getInstance().getContactList().containsKey(user)){
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
					EMContactManager.getInstance().addContact(user, msg);
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
	
	/**
	 * 加入到黑名单
	 * 
	 * @param username
	 */
	private void addUserToBlacklist(String username) {
		try {
			EMContactManager.getInstance().addUserToBlackList(username, true);
			Toast.makeText(getApplicationContext(), "移入黑名单成功", 0).show();
		} catch (EaseMobException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "移入黑名单失败", 0).show();
		}
		/*RequestParams params = new RequestParams();
		params.add("f_user", DemoApplication.getInstance().getUser());
		params.add("user", userId);
		DeviceUuidFactory uuid = new DeviceUuidFactory(this); 
		String uid = uuid.getDeviceUuid().toString(); 
		params.add("uid", uid);//新增设备请求
		HttpRestClient.get(Constant.USER_BLACK, params, new BaseJsonHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String rawJsonResponse, Object response) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, String rawJsonData,
					Object errorResponse) {
				// TODO Auto-generated method stub
				
			}

			@Override
			protected Object parseResponse(String rawJsonData, boolean isFailure)
					throws Throwable {
				// TODO Auto-generated method stub
				return null;
			}
			
		});*/
	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}
	
	/****
	 * 
	 * @param view
	 */
	public void onSendTxtMsg(String content) {
	    try {
	        //创建一个消息
	        EMMessage msg = EMMessage.createSendMessage(EMMessage.Type.TXT);
	        //设置消息的接收方
	        msg.setReceipt("bot");
	        //设置消息内容。本消息类型为文本消息。 
	        
	        msg.addBody(new TextMessageBody("我正在浏览你的主页"));
	    
	        //发送消息
	        EMChatManager.getInstance().sendMessage(msg);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
}
