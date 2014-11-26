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
package com.ccxt.whl.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.ccxt.whl.Constant;
import com.ccxt.whl.domain.User;
import com.ccxt.whl.utils.CommonUtils;
import com.easemob.util.HanziToPinyin;

public class UserDao {
	public static final String TABLE_NAME = "uers";
	public static final String COLUMN_NAME_ID = "username";
	public static final String COLUMN_NAME_NICK = "nick";
	public static final String COLUMN_HEAD_PIC = "headpic";
	public static final String COLUMN_NAME_IS_STRANGER = "is_stranger";//0是陌生人 2是好友  (1应该是关注的人,3是关注自己的)

	private DbOpenHelper dbHelper;

	public UserDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}

	/**
	 * 保存陌生人list
	 * 
	 * @param contactList
	 */
	public void saveContactList_m(List<User> contactList_m) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (User user : contactList_m) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_ID, user.getUsername());
				values.put(COLUMN_HEAD_PIC, user.getHeaderurl());
				values.put(COLUMN_NAME_IS_STRANGER, "0");//0是陌生人 2是好友
				if(user.getNick() != null)
					values.put(COLUMN_NAME_NICK, user.getNick());
				db.insert(TABLE_NAME, null, values);
			}
		}
	}
	
	/**
	 * 获取所有联系人list
	 * 待做
	 * @return
	 */
	/*public Map<String, User> getContactList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, User> users = new HashMap<String, User>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME +"WHERE " + COLUMN_NAME_IS_STRANGER +"=2" + " desc" , null);
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
				User user = new User();
				user.setUsername(username);
				user.setNick(nick);
				String headerName = null;
				if (!TextUtils.isEmpty(user.getNick())) {
					headerName = user.getNick();
				} else {
					headerName = user.getUsername();
				}
				
				if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)) {
					user.setHeader("");
				} else if (Character.isDigit(headerName.charAt(0))) {
					user.setHeader("#");
				} else {
					user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1))
							.get(0).target.substring(0, 1).toUpperCase());
					char header = user.getHeader().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setHeader("#");
					}
				}
				users.put(username, user);
			}
			cursor.close();
		}
		return users;
	}*/
	
	/**
	 * 保存好友list
	 * 
	 * @param contactList
	 */
	public void saveContactList(List<User> contactList) {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			db.delete(TABLE_NAME, null, null);
			for (User user : contactList) {
				ContentValues values = new ContentValues();
				values.put(COLUMN_NAME_ID, user.getUsername());
				values.put(COLUMN_HEAD_PIC, user.getHeaderurl());
				values.put(COLUMN_NAME_IS_STRANGER, "2");//0是陌生人 2是好友
				if(user.getNick() != null)
					values.put(COLUMN_NAME_NICK, user.getNick());
				db.insert(TABLE_NAME, null, values);
			}
		}
	}

	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	public Map<String, User> getContactList() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Map<String, User> users = new HashMap<String, User>();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME +" WHERE " + COLUMN_NAME_IS_STRANGER +" = '2' "/* + " desc" */, null);
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
				User user = new User();
				user.setUsername(username);
				user.setNick(nick);
				String headerName = null;
				if (!TextUtils.isEmpty(user.getNick())) {
					headerName = user.getNick();
				} else {
					headerName = user.getUsername();
				}
				
				if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)) {
					user.setHeader("");
				} else if (Character.isDigit(headerName.charAt(0))) {
					user.setHeader("#");
				} else {
					user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1))
							.get(0).target.substring(0, 1).toUpperCase());
					char header = user.getHeader().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setHeader("#");
					}
				}
				users.put(username, user);
			}
			cursor.close();
		}
		return users;
	}
	
	/**
	 * 删除一个联系人
	 * @param username
	 */
	public void deleteContact(String username){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			//System.out.println("==============================delete"+username);
			db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?", new String[]{username});
		}
	}
	
	
	/**
	 * 保存一个陌生人
	 * @param user
	 */
	public void saveContact_m(User user){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues(); 
		//User user_tmp = getUser(user.getUsername());
		//判断是否有昵称和头像，有则删除原来的（这里没有判断原来是否存在）
		//if(!CommonUtils.isNullOrEmpty(user.getNick())){
		if(!CommonUtils.isNullOrEmpty(user.getNick())&&!CommonUtils.isNullOrEmpty(user.getHeaderurl())){
			db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?", new String[]{user.getUsername()});
			//System.out.println("==============================delete"+user.getUsername());
 			values.put(COLUMN_NAME_ID, user.getUsername()); 
			values.put(COLUMN_HEAD_PIC, user.getHeaderurl());
			values.put(COLUMN_NAME_IS_STRANGER, "0");//0是陌生人 2是好友
			values.put(COLUMN_NAME_NICK, user.getNick());
			if(db.isOpen()){
				db.insert(TABLE_NAME, null, values);
			}
		}else{
			values.put(COLUMN_NAME_ID, user.getUsername()); 
			values.put(COLUMN_HEAD_PIC, user.getHeaderurl());
			values.put(COLUMN_NAME_IS_STRANGER, "0");//0是陌生人 2是好友
			if(user.getNick() != null)
				values.put(COLUMN_NAME_NICK, user.getNick());
			if(db.isOpen()){
				db.insert(TABLE_NAME, null, values);
			}
		}
	}
	
	/**
	 * 保存一个联系人
	 * @param user
	 */
	public void saveContact(User user){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		//User user_tmp = getUser(user.getUsername());
		//判断是否有昵称和头像，有则删除原来的（这里没有判断原来是否存在）
		//if(!CommonUtils.isNullOrEmpty(user.getNick())){
		if(!CommonUtils.isNullOrEmpty(user.getNick())||!CommonUtils.isNullOrEmpty(user.getHeaderurl())){
	 			db.delete(TABLE_NAME, COLUMN_NAME_ID + " = ?", new String[]{user.getUsername()});
	 			values.put(COLUMN_NAME_ID, user.getUsername()); 
				values.put(COLUMN_HEAD_PIC, user.getHeaderurl());
				values.put(COLUMN_NAME_IS_STRANGER, "2");//0是陌生人 2是好友
				values.put(COLUMN_NAME_NICK, user.getNick());
				if(db.isOpen()){
					db.insert(TABLE_NAME, null, values);
				}
		}else{
			values.put(COLUMN_NAME_ID, user.getUsername()); 
			values.put(COLUMN_HEAD_PIC, user.getHeaderurl());
			values.put(COLUMN_NAME_IS_STRANGER, "2");//0是陌生人 2是好友
			if(user.getNick() != null)
				values.put(COLUMN_NAME_NICK, user.getNick());
			if(db.isOpen()){
				db.insert(TABLE_NAME, null, values);
			} 
		}
		
		/*Cursor cursor = db.rawQuery("select * from " + TABLE_NAME +" WHERE " + COLUMN_NAME_ID +" = '"+user.getUsername()+"'" + " desc" , null);
		//没有
		if (cursor.moveToFirst() == false) {
			values.put(COLUMN_NAME_ID, user.getUsername()); 
			values.put(COLUMN_HEAD_PIC, user.getHeaderurl());
			values.put(COLUMN_NAME_IS_STRANGER, "2");//0是陌生人 2是好友
			if(user.getNick() != null)
				values.put(COLUMN_NAME_NICK, user.getNick());
			if(db.isOpen()){
				db.insert(TABLE_NAME, null, values);
			} 
 		 }else{
 			
       		 
       	 }*/
		
		  
		
	}
	
	
	/**
	 * 查询指定user的信息
	 * 
	 * @return
	 */
	public  User getUser(String username) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		User user = new User();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME +" WHERE " + COLUMN_NAME_ID +" = '"+username+"'"/* + " desc" */, null);
			if (cursor.moveToFirst() == false) {
	       		 //为空的Cursor
	       		 //return;
				//没有数据
				//user = null;
	       	 }else{
	       		//String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK)); 
				String headpic = cursor.getString(cursor.getColumnIndex(COLUMN_HEAD_PIC)); 
				String is_stranger = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_IS_STRANGER)); 
				user.setUsername(username);
				user.setNick(nick);
				user.setHeaderurl(headpic);
				user.setIs_stranger(is_stranger);
	       	 }
			
			/*while (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_ID));
				String nick = cursor.getString(cursor.getColumnIndex(COLUMN_NAME_NICK));
				User user = new User();
				user.setUsername(username);
				user.setNick(nick);
				String headerName = null;
				if (!TextUtils.isEmpty(user.getNick())) {
					headerName = user.getNick();
				} else {
					headerName = user.getUsername();
				}
				
				if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)) {
					user.setHeader("");
				} else if (Character.isDigit(headerName.charAt(0))) {
					user.setHeader("#");
				} else {
					user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1))
							.get(0).target.substring(0, 1).toUpperCase());
					char header = user.getHeader().toLowerCase().charAt(0);
					if (header < 'a' || header > 'z') {
						user.setHeader("#");
					}
				}
				users.put(username, user);
			}*/
			cursor.close();
		}
		return user;
	}
	
	

}
