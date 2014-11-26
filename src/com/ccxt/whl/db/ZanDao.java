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

import com.ccxt.whl.Constant;
import com.ccxt.whl.domain.User;
import com.easemob.util.HanziToPinyin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class ZanDao {
	public static final String TABLE_NAME = "zan";
	public static final String COLUMN_GUSHI_ID = "id";
	public static final String COLUMN_UP_GUSHI_ID = "up_id";
	 
	private DbOpenHelper dbHelper;

	public ZanDao(Context context) {
		dbHelper = DbOpenHelper.getInstance(context);
	}
 
	
	/**
	 * 删除一个联系人
	 * @param username
	 */
	public void deleteContact(String gushi_id){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if(db.isOpen()){
			//System.out.println("==============================delete"+gushi_id);
			db.delete(TABLE_NAME, COLUMN_GUSHI_ID + " = ?", new String[]{gushi_id});
		}
	}
	
	
	
	/**
	 * 保存一个联系人
	 * @param user
	 */
	public void saveContact(String gushi_id){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		 
			values.put(COLUMN_GUSHI_ID, gushi_id); 
			values.put(COLUMN_UP_GUSHI_ID, "0"); 
			  
			if(db.isOpen()){
				db.insert(TABLE_NAME, null, values);
			} 
		   
	}
	
	/**
	 * 更新gushi up
	 * @param msgId
	 * @param values
	 */
	public void updategushi(String gushi_id){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();  
		values.put(COLUMN_UP_GUSHI_ID, "1"); 
		if(db.isOpen()){
			db.update(TABLE_NAME, values, COLUMN_GUSHI_ID + " = ?", new String[]{gushi_id});
		}
	}
	
	/**
	 * 查询故事的信息
	 * 
	 * @return
	 */
	public  boolean getZanGushi(String gushi_id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		if (db.isOpen()) { 
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME +" WHERE " + COLUMN_GUSHI_ID +" = '"+gushi_id+"'"/* + " desc" */, null);
			//System.out.println(cursor.moveToFirst());
			return cursor.moveToFirst();
		}
		return false;
	}
	

	/**
	 * 查询为上传故事的信息
	 * 
	 * @return
	 */
	public  String[] getUPZanGushi() {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		//int size = Long.valueOf(getCount());
		String strings[] = new String[new Long(getCount()).intValue()];
		if (db.isOpen()) { 
			Cursor cursor = db.rawQuery("select * from " + TABLE_NAME +" WHERE " + COLUMN_UP_GUSHI_ID +" = '0' "/* + " desc" */, null);
			int i = 0;
			while (cursor.moveToNext()) {
				String gushi = cursor.getString(cursor.getColumnIndex(COLUMN_GUSHI_ID));
				strings[i] =  gushi; 
				i++;
			}
			cursor.close(); 
			//return strings[];
		}
		return strings;
	}
	
	/** 
     * 获取数据总数 
     * @return 
     */  
    public long getCount(){  
        SQLiteDatabase db=dbHelper.getReadableDatabase();  
        Cursor cursor =db.rawQuery("select count(*) from "+TABLE_NAME +" WHERE " + COLUMN_UP_GUSHI_ID +" = '0' ", null);  
        cursor.moveToFirst();  
        long reslut=cursor.getLong(0);  
        return reslut;  
    }  
	

}
