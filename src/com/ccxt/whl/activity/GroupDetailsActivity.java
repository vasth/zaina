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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.widget.ExpandGridView;
import com.easemob.util.EMLog;
import com.easemob.util.NetUtils;

public class GroupDetailsActivity extends BaseActivity {
	private static final String TAG = "GroupDetailsActivity";
	private static final int REQUEST_CODE_ADD_USER = 0;
	private static final int REQUEST_CODE_EXIT = 1;
	private static final int REQUEST_CODE_EXIT_DELETE = 2;
	private static final int REQUEST_CODE_CLEAR_ALL_HISTORY=3;
	
	private ExpandGridView userGridview;
	private String groupId;
	private ProgressBar loadingPB;
	private Button exitBtn;
	private Button deleteBtn;
	private EMGroup group;
	private GridAdapter adapter;
	private int referenceWidth;
	private int referenceHeight;
	private ProgressDialog progressDialog;
	
	public static GroupDetailsActivity instance;
	
	//清空所有聊天记录
	private RelativeLayout clearAllHistory;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_details);
		instance = this;
		clearAllHistory=(RelativeLayout) findViewById(R.id.clear_all_history);
		userGridview = (ExpandGridView) findViewById(R.id.gridview);
		loadingPB = (ProgressBar) findViewById(R.id.progressBar);
		exitBtn = (Button) findViewById(R.id.btn_exit_grp);
		deleteBtn = (Button) findViewById(R.id.btn_exitdel_grp);

		Drawable referenceDrawable = getResources().getDrawable(R.drawable.smiley_add_btn);
		referenceWidth = referenceDrawable.getIntrinsicWidth();
		referenceHeight = referenceDrawable.getIntrinsicHeight();

		// 获取传过来的groupid
		groupId = getIntent().getStringExtra("groupId");
		group = EMGroupManager.getInstance().getGroup(groupId);

		// 如果自己是群主，显示解散按钮
		if(group.getOwner() == null || "".equals(group.getOwner())){
			exitBtn.setVisibility(View.GONE);
			deleteBtn.setVisibility(View.GONE);
		}
		if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
			exitBtn.setVisibility(View.GONE);
			deleteBtn.setVisibility(View.VISIBLE);
		}
		((TextView) findViewById(R.id.group_name)).setText(group.getGroupName()+"("+group.getAffiliationsCount()+"人)");
		adapter = new GridAdapter(this, R.layout.grid, group.getMembers());
		userGridview.setAdapter(adapter);

		// 保证每次进详情看到的都是最新的group
		updateGroup();

		// 设置OnTouchListener
		userGridview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (adapter.isInDeleteMode) {
						adapter.isInDeleteMode = false;
						adapter.notifyDataSetChanged();
						return true;
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
		
		clearAllHistory.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent=new Intent(GroupDetailsActivity.this, AlertDialog.class);
				intent.putExtra("cancel",true);
				intent.putExtra("titleIsCancel", true);
				intent.putExtra("msg","确定删除群的聊天记录吗？");
				startActivityForResult(intent, REQUEST_CODE_CLEAR_ALL_HISTORY);
			}
		});
		
		
	}

	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			if (progressDialog == null) {
				progressDialog = new ProgressDialog(GroupDetailsActivity.this);
				progressDialog.setMessage("正在添加...");
				progressDialog.setCanceledOnTouchOutside(false);
				progressDialog.show();
			}
			switch (requestCode) {
			case REQUEST_CODE_ADD_USER:// 添加群成员
				final String[] newmembers = data.getStringArrayExtra("newmembers");
				addMembersToGroup(newmembers);

				break;
			case REQUEST_CODE_EXIT: // 退出群
				progressDialog.setMessage("正在退出群聊...");
				exitGrop();
				break;
			case REQUEST_CODE_EXIT_DELETE: // 解散群
				progressDialog.setMessage("正在解散群聊...");
				deleteGrop();
				break;
			case REQUEST_CODE_CLEAR_ALL_HISTORY:
				//删除此群聊的聊天记录
				progressDialog.setMessage("正在删除群消息...");
				
				deleteGroupHistory();
				
				
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 点击退出群组按钮
	 * 
	 * @param view
	 */
	public void exitGroup(View view) {
		startActivityForResult(new Intent(this, ExitGroupDialog.class), REQUEST_CODE_EXIT);

	}

	/**
	 * 点击解散群组按钮
	 * 
	 * @param view
	 */
	public void exitDeleteGroup(View view) {
		startActivityForResult(new Intent(this, ExitGroupDialog.class).putExtra("deleteToast", getString(R.string.dissolution_group_hint)),
				REQUEST_CODE_EXIT_DELETE);

	}

	
	
	
	/**
	 * 删除群聊天记录
	 */
	public void deleteGroupHistory(){
		
		
		EMChatManager.getInstance().deleteConversation(group.getGroupId());
		progressDialog.dismiss();
//		adapter.refresh(EMChatManager.getInstance().getConversation(toChatUsername));
		
		
		
	}
	
	
	/**
	 * 退出群组
	 * 
	 * @param groupId
	 */
	private void exitGrop() {
		new Thread(new Runnable() {
			public void run() {
				try {
					EMGroupManager.getInstance().exitFromGroup(groupId);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(RESULT_OK);
							finish();
							ChatActivity.activityInstance.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "退出群聊失败: " + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 解散群组
	 * 
	 * @param groupId
	 */
	private void deleteGrop() {
		new Thread(new Runnable() {
			public void run() {
				try {
					EMGroupManager.getInstance().exitAndDeleteGroup(groupId);
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							setResult(RESULT_OK);
							finish();
							ChatActivity.activityInstance.finish();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "解散群聊失败: " + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 增加群成员
	 * 
	 * @param newmembers
	 */
	private void addMembersToGroup(final String[] newmembers) {
		new Thread(new Runnable() {

			public void run() {
				try {
					//创建者调用add方法
					if(EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())){
						EMGroupManager.getInstance().addUsersToGroup(groupId, newmembers);
					}else{
						//一般成员调用invite方法
						EMGroupManager.getInstance().inviteUser(groupId, newmembers, null);
					}
					runOnUiThread(new Runnable() {
						public void run() {
							adapter.notifyDataSetChanged();
							((TextView) findViewById(R.id.group_name)).setText(group.getGroupName()+"("+group.getAffiliationsCount()+"人)");
							progressDialog.dismiss();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "添加群成员失败: " + e.getMessage(), 1).show();
						}
					});
				}
			}
		}).start();
	}

	/**
	 * 群组成员gridadapter
	 * 
	 * @author admin_new
	 * 
	 */
	private class GridAdapter extends ArrayAdapter<String> {

		private int res;
		public boolean isInDeleteMode;
		private List<String> objects;

		public GridAdapter(Context context, int textViewResourceId, List<String> objects) {
			super(context, textViewResourceId, objects);
			this.objects = objects;
			res = textViewResourceId;
			isInDeleteMode = false;
		}

		@Override
		public View getView(final int position, View convertView, final ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(res, null);
			}
			final Button button = (Button) convertView.findViewById(R.id.button_avatar);
			// 最后一个item，减人按钮
			if (position == getCount() - 1) {
				button.setText("");
				// 设置成删除按钮
				button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_minus_btn, 0, 0);
				// 如果不是创建者或者没有相应权限，不提供加减人按钮
//				if (!group.getOwner().equals(DemoApplication.getInstance().getUserName())) {
				if (!group.getOwner().equals(DemoApplication.getInstance().getUser())) {
					// if current user is not group admin, hide add/remove btn
					convertView.setVisibility(View.INVISIBLE);
				} else { // 显示删除按钮
					if (isInDeleteMode) {
						// 正处于删除模式下，隐藏删除按钮
						convertView.setVisibility(View.INVISIBLE);
					} else {
						// 正常模式
						convertView.setVisibility(View.VISIBLE);
						convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
					}
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EMLog.d(TAG, "删除按钮被点击");
							isInDeleteMode = true;
							notifyDataSetChanged();
						}
					});
				}
			} else if (position == getCount() - 2) { // 添加群组成员按钮
				button.setText("");
				button.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_add_btn, 0, 0);
				//如果不是创建者或者没有相应权限
//				if (!group.isAllowInvites() && !group.getOwner().equals(DemoApplication.getInstance().getUserName())) {
				if (!group.isAllowInvites() && !group.getOwner().equals(DemoApplication.getInstance().getUser())) {
					// if current user is not group admin, hide add/remove btn
					convertView.setVisibility(View.INVISIBLE);
				} else {
					// 正处于删除模式下,隐藏添加按钮
					if (isInDeleteMode) {
						convertView.setVisibility(View.INVISIBLE);
					} else {
						convertView.setVisibility(View.VISIBLE);
						convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
					}
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EMLog.d(TAG, "添加按钮被点击");
							// 进入选人页面
							startActivityForResult(
									(new Intent(GroupDetailsActivity.this, GroupPickContactsActivity.class).putExtra("groupId", groupId)),
									REQUEST_CODE_ADD_USER);
						}
					});
				}
			} else { // 普通item，显示群组成员
				final String username = getItem(position);
				button.setText(username);
				convertView.setVisibility(View.VISIBLE);
				button.setVisibility(View.VISIBLE);
				Drawable avatar = getResources().getDrawable(R.drawable.default_avatar);
				avatar.setBounds(0, 0, referenceWidth, referenceHeight);
				button.setCompoundDrawables(null, avatar, null, null);
				// demo群组成员的头像都用默认头像，需由开发者自己去设置头像
				if (isInDeleteMode) {
					// 如果是删除模式下，显示减人图标
					convertView.findViewById(R.id.badge_delete).setVisibility(View.VISIBLE);
				} else {
					convertView.findViewById(R.id.badge_delete).setVisibility(View.INVISIBLE);
				}
				button.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (isInDeleteMode) {
							// 如果是删除自己，return
							if (EMChatManager.getInstance().getCurrentUser().equals(username)) {
								startActivity(new Intent(GroupDetailsActivity.this, AlertDialog.class).putExtra("msg", "不能删除自己"));
								return;
							}
							if (!NetUtils.hasNetwork(getApplicationContext())) {
								Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), 0).show();
								return;
							}
							EMLog.d("group", "remove user from group:" + username);
							deleteMembersFromGroup(username);
						} else {
							// 正常情况下点击user，可以进入用户详情或者聊天页面等等
							// startActivity(new
							// Intent(GroupDetailsActivity.this,
							// ChatActivity.class).putExtra("userId",
							// user.getUsername()));
						}
					}
					
					/**
					 * 删除群成员
					 * @param username
					 */
					protected void deleteMembersFromGroup(final String username) {
						final ProgressDialog deleteDialog = new ProgressDialog(GroupDetailsActivity.this);
						deleteDialog.setMessage("正在移除...");
						deleteDialog.setCanceledOnTouchOutside(false);
						deleteDialog.show();
						new Thread(new Runnable() {

							@Override
							public void run() {
								try {
									// 删除被选中的成员
									EMGroupManager.getInstance().removeUserFromGroup(groupId, username);
									isInDeleteMode = false;
									runOnUiThread(new Runnable() {

										@Override
										public void run() {
											deleteDialog.dismiss();
											notifyDataSetChanged();
											((TextView) findViewById(R.id.group_name)).setText(group.getGroupName()+"("+group.getAffiliationsCount()+"人)");
										}
									});
								} catch (final Exception e) {
									deleteDialog.dismiss();
									runOnUiThread(new Runnable() {
										public void run() {
											Toast.makeText(getApplicationContext(), "删除失败：" + e.getMessage(), 1).show();
										}
									});
								}

							}
						}).start();
					}
				});
			}
			return convertView;
		}

		@Override
		public int getCount() {
			return super.getCount() + 2;
		}
	}
	
	protected void updateGroup() {
		new Thread(new Runnable() {
			public void run() {
				try {
					EMGroup returnGroup = EMGroupManager.getInstance().getGroupFromServer(groupId);
					//更新本地数据
					EMGroupManager.getInstance().createOrUpdateLocalGroup(returnGroup);
					
					runOnUiThread(new Runnable() {
						public void run() {
							((TextView) findViewById(R.id.group_name)).setText(group.getGroupName()+"("+group.getAffiliationsCount()+"人)");
							loadingPB.setVisibility(View.INVISIBLE);
							adapter.notifyDataSetChanged();
							if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
								//显示解散按钮
								exitBtn.setVisibility(View.GONE);
								deleteBtn.setVisibility(View.VISIBLE);
							}else{
								//显示退出按钮
								exitBtn.setVisibility(View.VISIBLE);
								deleteBtn.setVisibility(View.GONE);
								
							}
						}
					});

				} catch (Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							loadingPB.setVisibility(View.INVISIBLE);
						}
					});
				}
			}
		}).start();
	}

	public void back(View view) {
		setResult(RESULT_OK);
		finish();
	}



	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		finish();
	}



	@Override
	protected void onDestroy() {
		super.onDestroy();
		instance = null;
	}
	
	
	
	
}
