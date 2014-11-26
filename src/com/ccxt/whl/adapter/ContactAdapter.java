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
package com.ccxt.whl.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.ccxt.whl.Constant;
import com.ccxt.whl.R;
import com.ccxt.whl.db.UserDao;
import com.ccxt.whl.domain.User;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.ImageOptions;
import com.ccxt.whl.widget.Sidebar;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 简单的好友Adapter实现
 *
 */
public class ContactAdapter extends ArrayAdapter<User>  implements SectionIndexer{

	private LayoutInflater layoutInflater;
	private EditText query;
	private ImageButton clearSearch;
	private SparseIntArray positionOfSection;
	private SparseIntArray sectionOfPosition;
	private Sidebar sidebar;
	private int res;
	private Context context;

	public ContactAdapter(Context context, int resource, List<User> objects,Sidebar sidebar) {
		super(context, resource, objects);
		this.res = resource;
		this.sidebar=sidebar;
		this.context = context;
		layoutInflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return position == 0 ? 0 : 1;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0) {//搜索框
			if(convertView == null){
				convertView = layoutInflater.inflate(R.layout.search_bar_with_padding, null);
				query = (EditText) convertView.findViewById(R.id.query);
				clearSearch = (ImageButton) convertView.findViewById(R.id.search_clear);
				query.addTextChangedListener(new TextWatcher() {
					public void onTextChanged(CharSequence s, int start, int before, int count) {
						getFilter().filter(s);
						if (s.length() > 0) {
							clearSearch.setVisibility(View.VISIBLE);
							if (sidebar != null)
								sidebar.setVisibility(View.GONE);
						} else {
							clearSearch.setVisibility(View.INVISIBLE);
							if (sidebar != null)
								sidebar.setVisibility(View.VISIBLE);
						}
					}
	
					public void beforeTextChanged(CharSequence s, int start, int count, int after) {
					}
	
					public void afterTextChanged(Editable s) {
					}
				});
				clearSearch.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						InputMethodManager manager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
						if (((Activity) getContext()).getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
							if (((Activity) getContext()).getCurrentFocus() != null)
							manager.hideSoftInputFromWindow(((Activity) getContext()).getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
						//清除搜索框文字
						query.getText().clear();
					}
				});
			}
		}else{
			if(convertView == null){
				convertView = layoutInflater.inflate(res, null);
			}
			
			ImageView avatar = (ImageView) convertView.findViewById(R.id.avatar);
			TextView unreadMsgView = (TextView) convertView.findViewById(R.id.unread_msg_number);
			TextView nameTextview = (TextView) convertView.findViewById(R.id.name);
			TextView tvHeader = (TextView) convertView.findViewById(R.id.header);
			User user = getItem(position);
			String username = "";
			String header = "";
			if(CommonUtils.isNullOrEmpty(user)){
				//return;
			}else{
				//设置nick，demo里不涉及到完整user，用username代替nick显示 
				username = user.getUsername(); 
				header = user.getHeader();
				
			}
			
			/***获取本地数据库用户信息***/
			UserDao userdao =  new UserDao(context);
			User user_info = userdao.getUser(username);
			/***获取本地数据库用户信息end***/
			
			if (position == 0 || header != null && !header.equals(getItem(position - 1).getHeader())) {
				if ("".equals(header)) {
					tvHeader.setVisibility(View.GONE);
				} else {
					tvHeader.setVisibility(View.VISIBLE);
					tvHeader.setText(header);
				}
			} else {
				tvHeader.setVisibility(View.GONE);
			}
			//显示申请与通知item
			if(username.equals(Constant.NEW_FRIENDS_USERNAME)){
				 nameTextview.setText(user.getNick()); 
				 avatar.setImageResource(R.drawable.new_friends_icon); 
				if(user.getUnreadMsgCount() > 0){
					unreadMsgView.setVisibility(View.VISIBLE);
					unreadMsgView.setText(user.getUnreadMsgCount()+"");
				}else{
					unreadMsgView.setVisibility(View.INVISIBLE);
				}
			}else if(username.equals(Constant.GROUP_USERNAME)){
				//群聊item
				nameTextview.setText(user.getNick());
				avatar.setImageResource(R.drawable.groups_icon);
			}else if(username.equals(Constant.KEFU)){
				//客服
				nameTextview.setText(user.getNick());
				avatar.setImageResource(R.drawable.logo_uidemo);
			}else{
				//nameTextview.setText(username);
				nameTextview.setText(user_info.getNick()); 
				if(unreadMsgView != null)
					unreadMsgView.setVisibility(View.INVISIBLE);
				avatar.setImageResource(R.drawable.default_avatar);
				ImageLoader.getInstance().displayImage(user_info.getHeaderurl(), avatar, ImageOptions.getOptions());

			}
		}
		
		return convertView;
	}
	
	@Override
	public User getItem(int position) {
		return position == 0 ? new User() : super.getItem(position - 1);
	}
	
	@Override
	public int getCount() {
		//有搜索框，cout+1
		return super.getCount() + 1;
	}

	public int getPositionForSection(int section) {
		return positionOfSection.get(section);
	}

	public int getSectionForPosition(int position) {
		return sectionOfPosition.get(position);
	}

	@Override
	public Object[] getSections() {
		positionOfSection = new SparseIntArray();
		sectionOfPosition = new SparseIntArray();
		int count = getCount();
		List<String> list = new ArrayList<String>();
		list.add(getContext().getString(R.string.search_header));
		positionOfSection.put(0, 0);
		sectionOfPosition.put(0, 0);
		for (int i = 1; i < count; i++) {

			String letter = getItem(i).getHeader();
			System.err.println("contactadapter getsection getHeader:" + letter + " name:" + getItem(i).getUsername());
			int section = list.size() - 1;
			if (list.get(section) != null && !list.get(section).equals(letter)) {
				list.add(letter);
				section++;
				positionOfSection.put(section, i);
			}
			sectionOfPosition.put(i, section);
		}
		return list.toArray(new String[list.size()]);
	}

}
