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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.apache.http.Header;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
 

import com.ccxt.whl.Constant;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.HttpRestClient;
import com.ccxt.whl.utils.ImageOptions;
import com.ccxt.whl.utils.JsonToMapList;
import com.ccxt.whl.utils.PreferenceUtils;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.util.PathUtil;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 设置界面
 * 
 * @author Administrator
 * 
 */
public class SettingsFragmentC_0815 extends Fragment implements OnClickListener {
/*	private String  = "shared_key_setting_user_pic";
	private String  = "shared_key_setting_user_sex";
	private String  = "shared_key_setting_user_age";
	private String  = "shared_key_setting_user_area";
	private String  = "shared_key_setting_user_zaina";*/
	/**********************************新增用户信息*****************************************/
	/**
	 * 设置用户头像
	 */
	private RelativeLayout rl_user_pic;
	/**
	 * 设置用户昵称
	 */
	private RelativeLayout rl_user_nicheng;
	/**
	 * 设置用户性别
	 */
	private RelativeLayout rl_user_xingbie;
	/**
	 * 设置用户年龄
	 */
	private RelativeLayout rl_user_nianling;
	/**
	 * 设置用户城市
	 */
	private RelativeLayout rl_user_chengshi;
	/**
	 * 设置用户在哪的动态
	 */
	private RelativeLayout rl_user_zainadongtai;
	
	/**
	 * 用户头像imageView
	 */
	private ImageView iv_user_photo;
	
	/**
	 * 用户昵称
	 */
	private TextView tv_user_nicheng;
	/**
	 * 用户性别
	 */
	private TextView tv_user_xingbie;
	/**
	 * 用户年龄
	 */
	private TextView tv_user_nianling;
	/**
	 * 用户地区
	 */
	private TextView tv_user_chengshi;
	/**
	 * 用户在哪动态
	 */
	private TextView tv_user_zainadongtai;
	
	private String UserPic = null;
	private String UserNickName = null;
	private String UserSex = null;
	private String UserAge = null;
	private String UserArea = null;
	private String UserZaina = null;
	
	/******************************************************************************/
	/**
	 * 设置新消息通知布局
	 */
	private RelativeLayout rl_switch_notification;
	/**
	 * 设置声音布局
	 */
	private RelativeLayout rl_switch_sound;
	/**
	 * 设置震动布局
	 */
	private RelativeLayout rl_switch_vibrate;
	/**
	 * 设置扬声器布局
	 */
	private RelativeLayout rl_switch_speaker;

	/**
	 * 打开新消息通知imageView
	 */
	private ImageView iv_switch_open_notification;
	/**
	 * 关闭新消息通知imageview
	 */
	private ImageView iv_switch_close_notification;
	/**
	 * 打开声音提示imageview
	 */
	private ImageView iv_switch_open_sound;
	/**
	 * 关闭声音提示imageview
	 */
	private ImageView iv_switch_close_sound;
	/**
	 * 打开消息震动提示
	 */
	private ImageView iv_switch_open_vibrate;
	/**
	 * 关闭消息震动提示
	 */
	private ImageView iv_switch_close_vibrate;
	/**
	 * 打开扬声器播放语音
	 */
	private ImageView iv_switch_open_speaker;
	/**
	 * 关闭扬声器播放语音
	 */
	private ImageView iv_switch_close_speaker;

	/**
	 * 声音和震动中间的那条线
	 */
	private TextView textview1, textview2;

	private LinearLayout blacklistContainer;
	
	/**
	 * 退出按钮
	 */
	private Button logoutBtn;

	private EMChatOptions chatOptions;
	
	/*************
	 * 新加逻辑变量
	 */
	private BaseJsonHttpResponseHandler responseHandler;
	private File cameraFile;//照相机拍照的图片
//	private File cutFile;//剪切后的图片
	//头像uri
	private static String IMAGE_FILE_LOCATION = null;
	private Uri imageUri = null;
	private static final String IMAGE_FILE_LOCATION_TEAST = "file:///sdcard/lehu/temp.jpg";//temp file
	Uri imageUritest = Uri.parse(IMAGE_FILE_LOCATION_TEAST);//The Uri to store the big bitmap
	//private Uri imageUri = null;//Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap
	public static final int USERPIC_REQUEST_CODE_LOCAL = 101;
	public static final int USERPIC_REQUEST_CODE_LOCAL_19 = 101;
	public static final int USERPIC_REQUEST_CODE_CAMERA = 102;
	public static final int USERPIC_REQUEST_CODE_CUT = 103;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_conversation_settings, container, false);
	}

	/*
	rl_user_pic   
	rl_user_nicheng 
	rl_user_xingbie  
	rl_user_nianling 
	rl_user_chengshi 
	rl_user_zainadongtai
	iv_user_photo
	tv_user_nicheng
	tv_user_xingbie
	tv_user_nianling
	tv_user_chengshi
	tv_user_zainadongtai
	UserPic
	UserNickName
	UserSex
	UserAge
	UserArea
	UserZaina
	*/
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		/***************用户信息设置**************/
		rl_user_pic  = (RelativeLayout) getView().findViewById(R.id.rl_user_pic);
		rl_user_nicheng = (RelativeLayout) getView().findViewById(R.id.rl_user_nicheng);
		rl_user_xingbie = (RelativeLayout) getView().findViewById(R.id.rl_user_xingbie);
		rl_user_nianling = (RelativeLayout) getView().findViewById(R.id.rl_user_nianling);
		rl_user_chengshi = (RelativeLayout) getView().findViewById(R.id.rl_user_chengshi);
		rl_user_zainadongtai = (RelativeLayout) getView().findViewById(R.id.rl_user_zainadongtai);
		//实例化用户信息组件
		iv_user_photo = (ImageView) getView().findViewById(R.id.iv_user_photo);
		tv_user_nicheng = (TextView) getView().findViewById(R.id.tv_user_nicheng);
		tv_user_xingbie = (TextView) getView().findViewById(R.id.tv_user_xingbie);
		tv_user_nianling = (TextView) getView().findViewById(R.id.tv_user_nianling);
		tv_user_chengshi = (TextView) getView().findViewById(R.id.tv_user_chengshi);
		tv_user_zainadongtai = (TextView) getView().findViewById(R.id.tv_user_zainadongtai);
		//获取用户信息
		UserPic = PreferenceUtils.getInstance(getActivity()).getSettingUserPic();
		UserNickName = PreferenceUtils.getInstance(getActivity()).getSettingUserNickName();
		UserSex = PreferenceUtils.getInstance(getActivity()).getSettingUserSex();
		UserAge = PreferenceUtils.getInstance(getActivity()).getSettingUserAge();
		UserArea = PreferenceUtils.getInstance(getActivity()).getSettingUserArea();
		UserZaina = PreferenceUtils.getInstance(getActivity()).getSettingUserZaina();
		//设置用户信息
		//iv_user_photo
		ImageLoader.getInstance().displayImage(UserPic, iv_user_photo, ImageOptions.getOptions());
		tv_user_nicheng.setText(UserNickName);
		tv_user_xingbie.setText(UserSex);
		tv_user_nianling.setText(UserAge);
		tv_user_chengshi.setText(UserArea);
		tv_user_zainadongtai.setText(UserZaina);
		/***************用户信息设置**************/
		
		rl_switch_notification = (RelativeLayout) getView().findViewById(R.id.rl_switch_notification);
		rl_switch_sound = (RelativeLayout) getView().findViewById(R.id.rl_switch_sound);
		rl_switch_vibrate = (RelativeLayout) getView().findViewById(R.id.rl_switch_vibrate);
		rl_switch_speaker = (RelativeLayout) getView().findViewById(R.id.rl_switch_speaker);

		iv_switch_open_notification = (ImageView) getView().findViewById(R.id.iv_switch_open_notification);
		iv_switch_close_notification = (ImageView) getView().findViewById(R.id.iv_switch_close_notification);
		iv_switch_open_sound = (ImageView) getView().findViewById(R.id.iv_switch_open_sound);
		iv_switch_close_sound = (ImageView) getView().findViewById(R.id.iv_switch_close_sound);
		iv_switch_open_vibrate = (ImageView) getView().findViewById(R.id.iv_switch_open_vibrate);
		iv_switch_close_vibrate = (ImageView) getView().findViewById(R.id.iv_switch_close_vibrate);
		iv_switch_open_speaker = (ImageView) getView().findViewById(R.id.iv_switch_open_speaker);
		iv_switch_close_speaker = (ImageView) getView().findViewById(R.id.iv_switch_close_speaker);
		logoutBtn = (Button) getView().findViewById(R.id.btn_logout);
		if(!TextUtils.isEmpty(EMChatManager.getInstance().getCurrentUser())){
			logoutBtn.setText(getString(R.string.button_logout) + "(" + EMChatManager.getInstance().getCurrentUser() + ")");
		}

		textview1 = (TextView) getView().findViewById(R.id.textview1);
		textview2 = (TextView) getView().findViewById(R.id.textview2);
		
		blacklistContainer = (LinearLayout) getView().findViewById(R.id.ll_black_list);
		
		/**********设置***********/
		rl_user_pic.setOnClickListener(this);   
		rl_user_nicheng.setOnClickListener(this);
		rl_user_xingbie.setOnClickListener(this);  
		rl_user_nianling.setOnClickListener(this); 
		rl_user_chengshi.setOnClickListener(this); 
		rl_user_zainadongtai.setOnClickListener(this);
		/***********设置**********/
		
		blacklistContainer.setOnClickListener(this);
		rl_switch_notification.setOnClickListener(this);
		rl_switch_sound.setOnClickListener(this);
		rl_switch_vibrate.setOnClickListener(this);
		rl_switch_speaker.setOnClickListener(this);
		logoutBtn.setOnClickListener(this);

		chatOptions = EMChatManager.getInstance().getChatOptions();
		if (chatOptions.getNotificationEnable()) {
			iv_switch_open_notification.setVisibility(View.VISIBLE);
			iv_switch_close_notification.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_notification.setVisibility(View.INVISIBLE);
			iv_switch_close_notification.setVisibility(View.VISIBLE);
		}
		if (chatOptions.getNoticedBySound()) {
			iv_switch_open_sound.setVisibility(View.VISIBLE);
			iv_switch_close_sound.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_sound.setVisibility(View.INVISIBLE);
			iv_switch_close_sound.setVisibility(View.VISIBLE);
		}
		if (chatOptions.getNoticedByVibrate()) {
			iv_switch_open_vibrate.setVisibility(View.VISIBLE);
			iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
			iv_switch_close_vibrate.setVisibility(View.VISIBLE);
		}

		if (chatOptions.getUseSpeaker()) {
			iv_switch_open_speaker.setVisibility(View.VISIBLE);
			iv_switch_close_speaker.setVisibility(View.INVISIBLE);
		} else {
			iv_switch_open_speaker.setVisibility(View.INVISIBLE);
			iv_switch_close_speaker.setVisibility(View.VISIBLE);
		}
		
	
		
		/*************************http请求处理***********************/
		responseHandler = new BaseJsonHttpResponseHandler() {
			   
			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String rawJsonResponse, Object response) {
				// TODO Auto-generated method stub
				Log.d("log",rawJsonResponse); 
				if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
					Toast.makeText(getActivity(), "您的网络不稳定,请检查网络！", 0).show();
					return;
				}
				
				Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
        		if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
        			Toast.makeText(getActivity(), "更新成功", 0).show();
        			Log.d("log","message=="+lm.get("message").toString());
        			if(!CommonUtils.isNullOrEmpty(lm.get("result").toString())){
        				Map<String, Object> lmres = JsonToMapList.getMap(lm.get("result").toString());
        				String nickname = lmres.get("nickname").toString();
        				String age = lmres.get("age").toString();
        				String sex = lmres.get("sex").toString();
        				String headurl = lmres.get("headurl").toString();
        				if(!CommonUtils.isNullOrEmpty(nickname)){
        					tv_user_nicheng.setText(nickname);
        				}else if(!CommonUtils.isNullOrEmpty(age)){
        					tv_user_nianling.setText(age);
        				}else if(!CommonUtils.isNullOrEmpty(sex)){
        					tv_user_xingbie.setText(sex);
        				}else if(!CommonUtils.isNullOrEmpty(headurl)){
        					//tv_user_xingbie.setText(sex);
        					//更新头像
        				}
        				
        			}
        		}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, String rawJsonData,
					Object errorResponse) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "网络请求失败,请检查网络！", 0).show();
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_user_pic:
			//startActivity(new Intent(getActivity(), BlacklistActivity.class));
			//更改头像图片
			change_headurl();
			break;
		case R.id.rl_user_nicheng:
			//startActivity(new Intent(getActivity(), BlacklistActivity.class));
			//修改昵称
			change_nickname(""+tv_user_nicheng.getText().toString().trim());
			break;
		case R.id.rl_user_xingbie:
			//修改性别
			change_sex();
			break;
		case R.id.rl_user_nianling:
			//修改年龄 
			change_age(""+tv_user_nianling.getText().toString().trim());
		case R.id.rl_user_chengshi:
			//修改城市
			break;
		case R.id.rl_user_zainadongtai:
			//修改在哪动态
			break;
		case R.id.rl_switch_notification:
			if (iv_switch_open_notification.getVisibility() == View.VISIBLE) {
				iv_switch_open_notification.setVisibility(View.INVISIBLE);
				iv_switch_close_notification.setVisibility(View.VISIBLE);
				rl_switch_sound.setVisibility(View.GONE);
				rl_switch_vibrate.setVisibility(View.GONE);
				textview1.setVisibility(View.GONE);
				textview2.setVisibility(View.GONE);
				chatOptions.setNotificationEnable(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);

				PreferenceUtils.getInstance(getActivity()).setSettingMsgNotification(false);
			} else {
				iv_switch_open_notification.setVisibility(View.VISIBLE);
				iv_switch_close_notification.setVisibility(View.INVISIBLE);
				rl_switch_sound.setVisibility(View.VISIBLE);
				rl_switch_vibrate.setVisibility(View.VISIBLE);
				textview1.setVisibility(View.VISIBLE);
				textview2.setVisibility(View.VISIBLE);
				chatOptions.setNotificationEnable(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgNotification(true);
			}
			break;
		case R.id.rl_switch_sound:
			if (iv_switch_open_sound.getVisibility() == View.VISIBLE) {
				iv_switch_open_sound.setVisibility(View.INVISIBLE);
				iv_switch_close_sound.setVisibility(View.VISIBLE);
				chatOptions.setNoticeBySound(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgSound(false);
			} else {
				iv_switch_open_sound.setVisibility(View.VISIBLE);
				iv_switch_close_sound.setVisibility(View.INVISIBLE);
				chatOptions.setNoticeBySound(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgSound(true);
			}
			break;
		case R.id.rl_switch_vibrate:
			if (iv_switch_open_vibrate.getVisibility() == View.VISIBLE) {
				iv_switch_open_vibrate.setVisibility(View.INVISIBLE);
				iv_switch_close_vibrate.setVisibility(View.VISIBLE);
				chatOptions.setNoticedByVibrate(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgVibrate(false);
			} else {
				iv_switch_open_vibrate.setVisibility(View.VISIBLE);
				iv_switch_close_vibrate.setVisibility(View.INVISIBLE);
				chatOptions.setNoticedByVibrate(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgVibrate(true);
			}
			break;
		case R.id.rl_switch_speaker:
			if (iv_switch_open_speaker.getVisibility() == View.VISIBLE) {
				iv_switch_open_speaker.setVisibility(View.INVISIBLE);
				iv_switch_close_speaker.setVisibility(View.VISIBLE);
				chatOptions.setUseSpeaker(false);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgSpeaker(false);
			} else {
				iv_switch_open_speaker.setVisibility(View.VISIBLE);
				iv_switch_close_speaker.setVisibility(View.INVISIBLE);
				chatOptions.setUseSpeaker(true);
				EMChatManager.getInstance().setChatOptions(chatOptions);
				PreferenceUtils.getInstance(getActivity()).setSettingMsgVibrate(true);
			}
			break;
		case R.id.btn_logout:
			DemoApplication.getInstance().logout();
			// 重新显示登陆页面
			((MainActivity) getActivity()).finish();
			startActivity(new Intent(getActivity(), LoginActivity.class));
			break;
		case R.id.ll_black_list:
			startActivity(new Intent(getActivity(), BlacklistActivity.class));
			
			break;
		default:
			break;
		}

	}
	
	/**
	 * 更改年龄
	 */
	public void change_age(String age){
 
		final EditText texta = new EditText(getActivity());
		texta.setText(age);
		//设置EditText输入类型
		texta.setKeyListener(new NumberKeyListener() {  
		    public int getInputType() {  
		        return InputType.TYPE_CLASS_PHONE;  
		    } 
			@Override
			protected char[] getAcceptedChars() {
				// TODO Auto-generated method stub
				char[] numbers = new char[] {'0', '1', '2', '3', '4', '5','6','7','8','9'};  
		        return numbers;  
			} 
		});
		new AlertDialog.Builder(getActivity())  
		.setTitle("请输入您的年龄")  
		.setIcon(android.R.drawable.ic_dialog_info)  
		.setView(texta)  
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) { 
            	String age = texta.getEditableText().toString();
            	RequestParams params = new RequestParams();
            
				params.add("user", DemoApplication.getInstance().getUser());
				params.add("age", age);
				params.add("param", "age");
                HttpRestClient.get(Constant.UPDATE_USER_URL, params, responseHandler);
                dialog.dismiss();  
                //设置你的操作事项  
            }  
        })  
		.setNegativeButton("取消", null)  
		.show();
		//return true; 
	}
	
	/**
	 * 更改昵称
	 */
	public void change_nickname(String nickname){
 
		final EditText texta = new EditText(getActivity());
		texta.setText(nickname);
		new AlertDialog.Builder(getActivity())  
		.setTitle("请输入您的昵称")  
		.setIcon(android.R.drawable.ic_dialog_info)  
		.setView(texta)  
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) { 
            	String nickname = texta.getEditableText().toString();
            	RequestParams params = new RequestParams();
                
				params.add("user", DemoApplication.getInstance().getUser());
				params.add("nickname", nickname);
				params.add("param", "nickname");
                HttpRestClient.get(Constant.UPDATE_USER_URL, params, responseHandler);
                dialog.dismiss();  
                //设置你的操作事项  
            }  
        })  
		.setNegativeButton("取消", null)  
		.show();
		//return true; 
	}
	
	/**
	 * 更改性别
	 */
	public void change_sex(){
 
		AlertDialog.Builder builder = new Builder(getActivity());
		 String[] strarr = {"男","女"};
        builder.setItems(strarr, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
            	String sex = "2";
                // TODO 自动生成的方法存根 
                if (arg1 == 0) {//男
                	sex = "1";
                }else {//女
                	sex = "2";
                } 
                RequestParams params = new RequestParams(); 
				params.add("user", DemoApplication.getInstance().getUser());
				params.add("sex", sex);
				params.add("param", "sex");
               HttpRestClient.get(Constant.UPDATE_USER_URL, params, responseHandler);
            }
        });
        builder.show();
	}
	
	/**
	 * 更改头像
	 */
	public void change_headurl(){
		//如果挂在SDcard
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			//在SDcard文件TestSyncListView下创建文件
			IMAGE_FILE_LOCATION = Environment.getExternalStorageDirectory()+Constant.CACHE_DIR_IMAGE+"/temp.jpg";
			imageUri = Uri.parse(IMAGE_FILE_LOCATION);
			//File cutFile = new File(Constant.CACHE_DIR_IMAGE,"temp.jpg");
			//cutFile.getParentFile().mkdirs();
			File dir = new File(IMAGE_FILE_LOCATION);  
	        if (!dir.exists()) {  
	              try {  
	                  //在指定的文件夹中创建文件  
	                  dir.createNewFile();  
	            } catch (Exception e) {  
	            	//println(e);
	            }  
	        }  
		}else{
			Toast.makeText(getActivity(), "SD卡不存在，不能更改头像", 0).show();
			return;
		}
		AlertDialog.Builder builder = new Builder(getActivity());
		 String[] strarr = {"选择拍照","选择本地相册"};
        builder.setItems(strarr, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            { 
                // TODO 自动生成的方法存根 
                if (arg1 == 0) {//选择拍照
                	selectPicFromCamera();
                }else {//选择本地相册
                	selectPicFromLocal();
                }  
            }
        });
        builder.show();
	}
	
	/**
	 * 从图库获取图片
	 */
	public void selectPicFromLocal() {
		Intent intent;
		if (Build.VERSION.SDK_INT < 19) {
			intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("image/*");
			
			//Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null); 
			intent.setType("image/*"); 
			intent.putExtra("crop", "true"); 
			intent.putExtra("aspectX", 1); 
			intent.putExtra("aspectY", 1); 
			intent.putExtra("outputX", 600); 
			intent.putExtra("outputY", 600); 
			intent.putExtra("scale", true); 
			intent.putExtra("return-data", false);  
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUritest); 
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); 
			intent.putExtra("noFaceDetection", true); // no face detection 
			//startActivityForResult(intent, CHOOSE_BIG_PICTURE);
			startActivityForResult(intent, USERPIC_REQUEST_CODE_LOCAL);
		} else {
			intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
			startActivityForResult(intent, USERPIC_REQUEST_CODE_LOCAL_19);
		}
		/*//创建生成头像的图片地址
		cutFile = new File(PathUtil.getInstance().getImagePath(), DemoApplication.getInstance().getUser()
				+ System.currentTimeMillis() + "_m.jpg");
		cutFile.getParentFile().mkdirs();*/
		
	}
	
	/**
	 * 照相获取图片
	 */
	public void selectPicFromCamera() {
		if (!CommonUtils.isExitsSdcard()) {
			Toast.makeText(getActivity(), "SD卡不存在，不能拍照", 0).show();
			return;
		}

//		cameraFile = new File(PathUtil.getInstance().getImagePath(), DemoApplication.getInstance().getUserName()
		cameraFile = new File(PathUtil.getInstance().getImagePath(), DemoApplication.getInstance().getUser()
				+ System.currentTimeMillis() + ".jpg");
		cameraFile.getParentFile().mkdirs();
		
		startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
				USERPIC_REQUEST_CODE_CAMERA);
	}
	
	/**
	 * onActivityResult
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		  
			 if (requestCode == USERPIC_REQUEST_CODE_CAMERA) { // 发送照片
				if (cameraFile != null && cameraFile.exists()){
					//sendPicture(cameraFile.getAbsolutePath());
					Log.d("log","cameraFile"+cameraFile.getAbsolutePath());
					//改成返回到指定的uri imageUri = Uri.fromFile(cameraFile);
					imageUri = Uri.fromFile(cameraFile);
					cropImageUri(imageUri, 200, 200, USERPIC_REQUEST_CODE_CUT);
					
				} 
			} else if (requestCode == USERPIC_REQUEST_CODE_LOCAL) { //获取剪切后的本地图片
				if(imageUritest != null){ 
					         // Bitmap bitmap = decodeUriAsBitmap(imageUritest);//decode bitmap 
					         //iv_user_photo.setImageBitmap(bitmap);  
							Log.d("log","imageUri==>"+imageUritest);
			            	 iv_user_photo.setImageURI(imageUri); 
					         RequestParams params = new RequestParams(); 
				            	// final String contentType = RequestParams.APPLICATION_OCTET_STREAM;
				            	 File file = Uritofile(imageUritest);
				            	 //File file = new File(imageUri.getPath());
			     			if (file.exists()) {
			     				try {
									params.put("headurl", file,"image/jpg");
									params.put("user", DemoApplication.getInstance().getUser());
					            	params.put("param", "headurl");
					            	HttpRestClient.post(Constant.UPDATE_USER_URL, params, responseHandler);
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
			     			}else{
			     				Toast toast = Toast.makeText(getActivity(), "无法获取图片，请检查SD卡是否存在", Toast.LENGTH_SHORT);
				     		}
				 }else{
	     				Toast toast = Toast.makeText(getActivity(), "无法获取图片，请检查SD卡是否存在", Toast.LENGTH_SHORT);
	     		 } 
			} else if (requestCode == USERPIC_REQUEST_CODE_LOCAL_19) { // 获取本地图片19 
				if (data != null) { 
					Uri selectedImage = data.getData();
					if (selectedImage != null) {
						//修改成返回到指定的uri imageUri = selectedImage;
						//sendPicByUri(selectedImage);
						 imageUri = selectedImage;
						 sendPicByUri(selectedImage);
						//Log.d("log","selectedImage"+selectedImage);
					}
				}
			} else if(requestCode==USERPIC_REQUEST_CODE_CUT){//裁剪图片19
				// 从剪切图片返回的数据  
	            if (data != null) {  

	            	 Log.d("log","imageUribundle==>"+imageUri);
	            	 iv_user_photo.setImageURI(imageUri); 
	            	 RequestParams params = new RequestParams(); 
	            	// final String contentType = RequestParams.APPLICATION_OCTET_STREAM;
	            	 File file = Uritofile(imageUri);
	            	 //File file = new File(imageUri.getPath());
	     			if (file.exists()) {
	     				try {
							params.put("headurl", file,"image/jpeg");
							params.put("user", DemoApplication.getInstance().getUser());
			            	params.put("param", "headurl");
			            	HttpRestClient.post(Constant.UPDATE_USER_URL, params, responseHandler);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	     			}else{
	     				Toast toast = Toast.makeText(getActivity(), "无法获取图片，请检查SD卡是否存在", Toast.LENGTH_SHORT);
	     			}
	            	  
	            	 //params.put(key, file, )
	            	
	                 //**获取返回图片的数据用于返回data是使用
	                //Bitmap bitmap = data.getParcelableExtra("data");
	               // Bitmap bitmap = data.getExtras().getParcelable("data");
	                
	                /*压缩图片
	                // ByteArrayOutputStream out = new ByteArrayOutputStream(); 
	                // bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);  
	                */
	                
	                //this.iv_image.setImageBitmap(bitmap);  
	            }else{
     				Toast toast = Toast.makeText(getActivity(), "无法获取图片，请检查SD卡是否存在", Toast.LENGTH_SHORT);
	     			
     			}  
	         
	            /*  try {  
	                // 将临时文件删除  
	               // tempFile.delete();  
	            } catch (Exception e) {  
	                e.printStackTrace();  
	            }  */
	    
				 RequestParams params = new RequestParams(); 
				 final String contentType = RequestParams.APPLICATION_OCTET_STREAM;
				// params.put(key, file, contentType)
				//HttpRestClient.post(url, params, responseHandler)
			}
	}
	
	//uri 转bitmap
	private Bitmap decodeUriAsBitmap(Uri uri){ 
		    Bitmap bitmap = null; 
		    try { 
		        bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri)); 
		    } catch (FileNotFoundException e) { 
		        e.printStackTrace(); 
		        return null; 
		    } 
		    return bitmap; 
		}
	/**
	 *  根据uri获取图片地址
	 * 
	 * @param selectedImage
	 */
	private File Uritofile(Uri selectedImage) { 
		File file = null;
		Cursor cursor = getActivity().getContentResolver().query(selectedImage, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(getActivity(), "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return null;
			}
			  file = new File(picturePath);
			//sendPicture(picturePath);
		} else {
			file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(getActivity(), "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return null ; 
			}
			 
		}
		
		return file;

	}
	
	/**
	 * 根据图库图片uri获取图片
	 * 
	 * @param selectedImage
	 */
	private void sendPicByUri(Uri selectedImage) {
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().getContentResolver().query(selectedImage, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(getActivity(), "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;
			}
			 //copyFile(picturePath,imageUri.getPath());
			 cropImageUri(selectedImage, 200, 200, USERPIC_REQUEST_CODE_CUT);
			//sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(getActivity(), "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return;

			}
			 //copyFile(selectedImage.getPath(),imageUri.getPath());
			 cropImageUri(selectedImage, 200, 200, USERPIC_REQUEST_CODE_CUT);
			//sendPicture(file.getAbsolutePath());
		}
		/*if(getpathfromUri(uri)!=null&&getpathfromUri(imageUri)!=null){
			 copyFile(getpathfromUri(uri),getpathfromUri(imageUri));
			}else{
				return;
			}*/
		//cropImageUri(selectedImage, 150, 150, USERPIC_REQUEST_CODE_CUT);

	}
	
	/**
	 * 根据图库图片uri获取图片
	 * 
	 * @param selectedImage
	 */
	private String getpathfromUri(Uri selectedImage) {
		// String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = getActivity().getContentResolver().query(selectedImage, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex("_data");
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			cursor = null;

			if (picturePath == null || picturePath.equals("null")) {
				Toast toast = Toast.makeText(getActivity(), "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return null;
			}
			return picturePath;
			//sendPicture(picturePath);
		} else {
			File file = new File(selectedImage.getPath());
			if (!file.exists()) {
				Toast toast = Toast.makeText(getActivity(), "找不到图片", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				return null;

			}
			return file.getAbsolutePath(); 
		}

	}
	/**
	 * 裁剪图片
	 * @param uri
	 * @param outputX
	 * @param outputY
	 * @param requestCode
	 */
	private void cropImageUri(Uri uri, int outputX, int outputY, int requestCode){
	  
		 Intent intent = new Intent("com.android.camera.action.CROP");
		 intent.setDataAndType(uri, "image/*");
		 intent.putExtra("crop", "true");
		 //aspectX aspectY宽高比例
		 intent.putExtra("aspectX", 1);
		 intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		 intent.putExtra("outputX", outputX);
		 intent.putExtra("outputY", outputY);
		 intent.putExtra("scale", true);
		 intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		 intent.putExtra("return-data", false);
		 intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		 intent.putExtra("noFaceDetection", true); // no face detection
		 startActivityForResult(intent, requestCode);
		}
	
	/**
	 * 保存Bitmap为文件
	 * @param baseBitmap
	 */
	public void save(Bitmap baseBitmap) {
		  try { 
			  /*			  BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(cutFile));
			   baseBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
	           bos.flush();
	           bos.close();*/
		   OutputStream stream = new FileOutputStream(Uritofile(imageUri));
		   baseBitmap.compress(CompressFormat.JPEG, 100, stream);
		   stream.close();
		  /*
		   // 模拟一个广播，通知系统sdcard被挂载
		   Intent intent = new Intent();
		   intent.setAction(Intent.ACTION_MEDIA_MOUNTED);
		   intent.setData(Uri.fromFile(Environment
		     .getExternalStorageDirectory()));
		   sendBroadcast(intent);*/
		   Toast.makeText(getActivity(), "保存图片成功", 0).show();
		  } catch (Exception e) {
		   Toast.makeText(getActivity(), "保存图片失败", 0).show();
		   e.printStackTrace();
		  }
	}
	
	
	/**  
     * 复制单个文件  
     * @param oldPath String 原文件路径 如：c:/fqf.txt  
     * @param newPath String 复制后路径 如：f:/fqf.txt  
     * @return boolean  
     */   
   public void copyFile(String oldPath, String newPath) {   
       try {   
           int bytesum = 0;   
           int byteread = 0;   
           File oldfile = new File(oldPath);   
           if (oldfile.exists()) { //文件存在时   
               InputStream inStream = new FileInputStream(oldPath); //读入原文件   
               FileOutputStream fs = new FileOutputStream(newPath);   
               byte[] buffer = new byte[1444];   
               int length;   
               while ( (byteread = inStream.read(buffer)) != -1) {   
                   bytesum += byteread; //字节数 文件大小   
                   //Log.d("log",bytesum);   
                   fs.write(buffer, 0, byteread);   
               }   
               inStream.close();   
           }   
       }   
       catch (Exception e) {   
           Log.d("log","复制单个文件操作出错");   
           e.printStackTrace();   
  
       }   
  
   }   

	
	 
}
