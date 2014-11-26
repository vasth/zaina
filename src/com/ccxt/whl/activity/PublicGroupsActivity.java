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

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ccxt.whl.R;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;

public class PublicGroupsActivity extends BaseActivity {
	private ProgressBar pb;
	private ListView listView;
	private EditText query;
	private ImageButton clearSearch;
	private GroupsAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_public_groups);

		pb = (ProgressBar) findViewById(R.id.progressBar);
		listView = (ListView) findViewById(R.id.list);

		new Thread(new Runnable() {
			public void run() {
				try {
					// 从服务器获取所用公开的群聊
					final List<EMGroupInfo> groupsList = EMGroupManager.getInstance().getAllPublicGroupsFromServer();
					runOnUiThread(new Runnable() {

						public void run() {
							pb.setVisibility(View.INVISIBLE);
							adapter = new GroupsAdapter(PublicGroupsActivity.this, 1, groupsList);
							listView.setAdapter(adapter);
							
							//设置item点击事件
							listView.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
									startActivity(new Intent(PublicGroupsActivity.this, GroupSimpleDetailActivity.class).
											putExtra("groupinfo", adapter.getItem(position)));
								}
							});
							
							// 搜索框
							query = (EditText) findViewById(R.id.query);
							// 搜索框中清除button
							clearSearch = (ImageButton) findViewById(R.id.search_clear);
							query.addTextChangedListener(new TextWatcher() {
								public void onTextChanged(CharSequence s, int start, int before, int count) {
									
									adapter.getFilter().filter(s);
									if (s.length() > 0) {
										clearSearch.setVisibility(View.VISIBLE);
									} else {
										clearSearch.setVisibility(View.INVISIBLE);
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
									query.getText().clear();

								}
							});
						}
					});
				} catch (EaseMobException e) {
					e.printStackTrace();
					runOnUiThread(new Runnable() {
						public void run() {
							pb.setVisibility(View.INVISIBLE);

						}
					});
				}
			}
		}).start();

	}

	private class GroupsAdapter extends ArrayAdapter<EMGroupInfo> {

		private LayoutInflater inflater;

		public GroupsAdapter(Context context, int res, List<EMGroupInfo> groups) {
			super(context, res, groups);
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.row_group, null);
			}

			((TextView) convertView.findViewById(R.id.name)).setText(getItem(position).getGroupName());

			return convertView;
		}
	}
	
	public void back(View view){
		finish();
	}
}
