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
package com.ccxt.whl.utils;

import com.ccxt.whl.DemoApplication;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtils {

	/**
	 * 保存Preference的name
	 */
	public static final String PREFERENCE_NAME = "saveInfo";
	private static SharedPreferences mSharedPreferences;
	private static PreferenceUtils mPreferenceUtils;
	private static SharedPreferences.Editor editor;

	private String SHARED_KEY_SETTING_NOTIFICATION = "shared_key_setting_notification";
	private String SHARED_KEY_SETTING_SOUND = "shared_key_setting_sound";
	private String SHARED_KEY_SETTING_VIBRATE = "shared_key_setting_vibrate";
	private String SHARED_KEY_SETTING_SPEAKER = "shared_key_setting_speaker";
	
	/************设置用户信息*************/
	private String SHARED_KEY_SETTING_USER_NICKNAME = "shared_key_setting_user_nickname";
	private String SHARED_KEY_SETTING_USER_PIC = "shared_key_setting_user_pic";
	private String SHARED_KEY_SETTING_USER_SEX = "shared_key_setting_user_sex";
	private String SHARED_KEY_SETTING_USER_AGE = "shared_key_setting_user_age";
	private String SHARED_KEY_SETTING_USER_AREA = "shared_key_setting_user_area";
	//-----start
	private String SHARED_KEY_SETTING_USER_ZHIYE = "shared_key_setting_user_zhiye";
	private String SHARED_KEY_SETTING_USER_QIANMING = "shared_key_setting_user_qianming";
	//-----end
	private String SHARED_KEY_SETTING_USER_ZAINA = "shared_key_setting_user_zaina";
	
	
	private String SHARED_KEY_SETTING_USER_LOC = "shared_key_setting_user_loc";
	/****×*******设置加载用户性别****/
	private String SHARED_KEY_LOAD_SEX = "shared_key_load_sex";
	/****×*******设置按地区或者时间筛选****/
	private String SHARED_KEY_LOAD_TIME_LOC = "shared_key_load_time_loc";
	
	private PreferenceUtils(Context cxt) {
	//	mSharedPreferences = cxt.getSharedPreferences(DemoApplication.getInstance().getUser()+PREFERENCE_NAME, 
	//			Context.MODE_PRIVATE);
		mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME,Context.MODE_PRIVATE);
	}

	/**
	 * 单例模式，获取instance实例
	 * 
	 * @param cxt
	 * @return
	 */
	public static PreferenceUtils getInstance(Context cxt) {
		if (mPreferenceUtils == null) {
			mPreferenceUtils = new PreferenceUtils(cxt);
		}
		editor = mSharedPreferences.edit();
		return mPreferenceUtils;
	}

	public void setSettingMsgNotification(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_NOTIFICATION, paramBoolean);
		editor.commit();
	}

	public boolean getSettingMsgNotification() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_NOTIFICATION, true);
	}

	public void setSettingMsgSound(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_SOUND, paramBoolean);
		editor.commit();
	}

	public boolean getSettingMsgSound() {

		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_SOUND, true);
	}

	public void setSettingMsgVibrate(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_VIBRATE, paramBoolean);
		editor.commit();
	}

	public boolean getSettingMsgVibrate() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_VIBRATE, true);
	}

	public void setSettingMsgSpeaker(boolean paramBoolean) {
		editor.putBoolean(SHARED_KEY_SETTING_SPEAKER, paramBoolean);
		editor.commit();
	}

	public boolean getSettingMsgSpeaker() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SETTING_SPEAKER, true);
	}
	/**
	 * 设置用户昵称
	 * @param UserNickName
	 */
	public void setSettingUserNickName(String UserNickName) {
		editor.putString(SHARED_KEY_SETTING_USER_NICKNAME, UserNickName);
		editor.commit();
	}
	/**
	 * 获取用户昵称 
	 */
	public String getSettingUserNickName() {
		return mSharedPreferences.getString(SHARED_KEY_SETTING_USER_NICKNAME,"");
	}
 
	/**
	 * 设置用户头像
	 * @param UserPic
	 */
	public void setSettingUserPic(String UserPic) {
		editor.putString(SHARED_KEY_SETTING_USER_PIC, UserPic);
		editor.commit();
	}
	/**
	 * 获取用户头像 
	 */
	public String getSettingUserPic() {
		return mSharedPreferences.getString(SHARED_KEY_SETTING_USER_PIC,"");
	}
	
	/**
	 * 设置用户性别
	 * @param UserSex
	 */
	public void setSettingUserSex(String UserSex) {
		editor.putString(SHARED_KEY_SETTING_USER_SEX, UserSex);
		editor.commit();
	}
	/**
	 * 获取用户性别 
	 */
	public String getSettingUserSex() {
		return mSharedPreferences.getString(SHARED_KEY_SETTING_USER_SEX,"女");
	}
	
	/**
	 * 设置用户年龄
	 * @param UserAge
	 */
	public void setSettingUserAge(String UserAge) {
		editor.putString(SHARED_KEY_SETTING_USER_AGE, UserAge);
		editor.commit();
	}
	/**
	 * 获取用户年龄 
	 */
	public String getSettingUserAge() {
		return mSharedPreferences.getString(SHARED_KEY_SETTING_USER_AGE,"21");
	}
	
	/**
	 * 设置用户区域
	 * @param UserArea
	 */
	public void setSettingUserArea(String UserArea) {
		editor.putString(SHARED_KEY_SETTING_USER_AREA, UserArea);
		editor.commit();
	}
	/**
	 * 获取用户区域 
	 */
	public String getSettingUserArea() {
		return mSharedPreferences.getString(SHARED_KEY_SETTING_USER_AREA,"");
	}
	
	/**
	 * 设置用户在哪动态
	 * @param UserZaina
	 */
	public void setSettingUserZaina(String UserZaina) {
		editor.putString(SHARED_KEY_SETTING_USER_ZAINA, UserZaina);
		editor.commit();
	}
	/**
	 * 获取用户在哪动态 
	 */
	public String getSettingUserZaina() {
		return mSharedPreferences.getString(SHARED_KEY_SETTING_USER_ZAINA,"");
	}
	
	/**
	 * 设置用户经纬度
	 * @param UserZaina
	 */
	public void setSettingUserloc(String UserLoc) {
		editor.putString(SHARED_KEY_SETTING_USER_LOC, UserLoc);
		editor.commit();
	}
	/**
	 * 获取用户经纬度
	 */
	public String getSettingUserloc() {
		return mSharedPreferences.getString(SHARED_KEY_SETTING_USER_LOC,"");
	}
	
	/**
	 * 设置筛选用户性别
	 * @param UserZaina
	 */
	public void setloadsex(String loadsex) {
		editor.putString(SHARED_KEY_LOAD_SEX, loadsex);
		editor.commit();
	}
	
	/**
	 * 获取筛选用户性别
	 */
	public String getloadsex() {
		return mSharedPreferences.getString(SHARED_KEY_LOAD_SEX,"");
	}
	
	/**
	 * 设置按距离或者是时间筛选用户
	 * @param UserZaina
	 */
	public void setloadtimeloc(String loadtimeloc) {
		editor.putString(SHARED_KEY_LOAD_TIME_LOC, loadtimeloc);
		editor.commit();
	}
	/**
	 * 获取按距离或者是时间筛选用户
	 */
	public String getloadtimeloc() {
		return mSharedPreferences.getString(SHARED_KEY_LOAD_TIME_LOC,"");
	}
	
	
	/**
	 * 设置用户职业
	 * @param UserZaina
	 */
	public void setSettingUserZhiye(String Zhiye) {
		editor.putString(SHARED_KEY_SETTING_USER_ZHIYE, Zhiye);
		editor.commit();
	}
	/**
	 * 获取用户职业
	 */
	public String getSettingUserZhiye() {
		return mSharedPreferences.getString(SHARED_KEY_SETTING_USER_ZHIYE,"");
	}
	
	
	/**
	 * 设置筛选用户签名
	 * @param UserZaina
	 */
	public void setSettingUserQianming(String Qianming) {
		editor.putString(SHARED_KEY_SETTING_USER_QIANMING, Qianming);
		editor.commit();
	}
	/**
	 * 获取筛选用户签名
	 */
	public String getSettingUserQianming() {
		return mSharedPreferences.getString(SHARED_KEY_SETTING_USER_QIANMING,"");
	}
	/*private String  = "shared_key_setting_user_pic";
	private String  = "shared_key_setting_user_sex";
	private String  = "shared_key_setting_user_age";
	private String  = "shared_key_setting_user_area";
	private String  = "shared_key_setting_user_zaina";*/
}
