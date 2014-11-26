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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ccxt.whl.R;
import com.ccxt.whl.activity.zai_showbigimage;
import com.ccxt.whl.domain.User;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.ImageOptions;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 在哪adpater
 * 
 */
public class ZainaAdapter extends ArrayAdapter<User> {

	private LayoutInflater inflater;
	private Context context;
	//private String headurl ;
	public ZainaAdapter(Context context, int textViewResourceId, List<User> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		inflater = LayoutInflater.from(context);
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.zaina_list_item, parent, false);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.zaina_name);
			//holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
			holder.message = (TextView) convertView.findViewById(R.id.zaina_message);
			holder.time = (TextView) convertView.findViewById(R.id.zai_time);
			holder.avatar = (ImageView) convertView.findViewById(R.id.zaina_avatar);
			holder.msgState = (TextView)convertView.findViewById(R.id.zaina_sex_state);
			holder.city = (TextView)convertView.findViewById(R.id.zaina_city);
			holder.list_item_layout=(RelativeLayout) convertView.findViewById(R.id.list_item_layout);
			convertView.setTag(holder);
		}
		
		/*if(position%2==0)
		{
			holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem);
		}else{
			holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem_grey);
		}*/
		
//		Log.d("log","position========"+position);
		final User user = getItem(position);
//		Log.d("log",user.toString());
//		Log.d("log","position=user======="+user.getNick());
		/*if(user instanceof EMGroup){
			//群聊消息，显示群聊头像
			holder.avatar.setImageResource(R.drawable.group_icon);
		}else{
			holder.avatar.setImageResource(R.drawable.default_avatar);
		}*/
		
		String username = user.getUsername();
		// 获取与此用户/群组的会话
		EMConversation conversation = EMChatManager.getInstance().getConversation(username);
		holder.name.setText(user.getNick() != null ? user.getNick() : username);
		holder.message.setText(!CommonUtils.isNullOrEmpty(user.getJiedao()) ? user.getJiedao():"我的位置暂时保密,嘻嘻~");//显示街道信息
		//holder.time.setText(user.getLasttime());//显示最后时间
		//holder.time.setText("刚刚");//显示最后时间
		//if()
		holder.time.setText(!CommonUtils.isNullOrEmpty(user.getLasttime()) ? jisuan(user.getLasttime()):""); 
		holder.msgState.setText(!CommonUtils.isNullOrEmpty(user.getAge()) ? user.getAge():"21");//这里是性别
		holder.city.setText(user.getCity());
		//holder.msgState.setBackgroundColor(R.color.gril_text_back);
		if(user.getSex().equals("1")){
			holder.msgState.setBackgroundResource(R.drawable.textview_boy_style);
		}else{
			holder.msgState.setBackgroundResource(R.drawable.textview_girl_style);
		}
		
		ImageLoader.getInstance().displayImage(user.getHeaderurl(), holder.avatar, ImageOptions.getOptions());
		
		//headurl = user.getHeaderurl();
		holder.avatar.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub 
				if(!CommonUtils.isNullOrEmpty(user.getHeaderurl())){
					Intent intent = new Intent((Activity)context, zai_showbigimage.class);
					intent.putExtra("headurl", user.getHeaderurl());
					Activity activity =(Activity)context;
					activity.startActivity(intent);
				}
				
			}
		});
		
		/*if (conversation.getUnreadMsgCount() > 0) {
			// 显示与此用户的消息未读数
			holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
			holder.unreadLabel.setVisibility(View.VISIBLE);
		} else {
			holder.unreadLabel.setVisibility(View.INVISIBLE);
		}

		if (conversation.getMsgCount() != 0) {
			// 把最后一条消息的内容作为item的message内容
			EMMessage lastMessage = conversation.getLastMessage();
			holder.message.setText(SmileUtils.getSmiledText(getContext(), getMessageDigest(lastMessage, (this.getContext()))),
					BufferType.SPANNABLE);

			holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
			if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
				holder.msgState.setVisibility(View.VISIBLE);
			} else {
				holder.msgState.setVisibility(View.GONE);
			}
		}*/

		return convertView;
	}

	/**
	 * 根据消息内容和消息类型获取消息内容提示
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION: // 位置消息
			if (message.direct == EMMessage.Direct.RECEIVE) {
				//从sdk中提到了ui中，使用更简单不犯错的获取string方法
//				digest = EasyUtils.getAppResourceString(context, "location_recv");
				digest = getStrng(context, R.string.location_recv);
				digest = String.format(digest, message.getFrom());
				return digest;
			} else {
//				digest = EasyUtils.getAppResourceString(context, "location_prefix");
				digest = getStrng(context, R.string.location_prefix);
			}
			break;
		case IMAGE: // 图片消息
			ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
			digest = getStrng(context, R.string.picture) + imageBody.getFileName();
			break;
		case VOICE:// 语音消息
			digest = getStrng(context, R.string.voice);
			break;
		case VIDEO: // 视频消息
			digest = getStrng(context, R.string.video);
			break;
		case TXT: // 文本消息
			TextMessageBody txtBody = (TextMessageBody) message.getBody();
			digest = txtBody.getMessage();
			break;
		case FILE: //普通文件消息
			digest = getStrng(context, R.string.file);
			break;
		default:
			System.err.println("error, unknow type");
			return "";
		}

		return digest;
	}
	
	@SuppressLint("SimpleDateFormat")
	private String jisuan(String shijianchuo){
		  long unixLong = 0;
		  StringBuffer sb = null;
		  try {
              unixLong = Long.parseLong(shijianchuo) * 1000;
           } catch(Exception ex) {
              System.out.println("String转换Long错误，请确认数据可以转换！");
         }
		  
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	       java.util.Date now ;
	    try {
	        now = df.parse(df.format(new Date())); 
	      // java.util.Date date=df.parse("2004-01-02 11:30:24");
	       java.util.Date date =  df.parse(df.format(unixLong));//获取参数时间
	       long l=now.getTime()- date.getTime();
	       long day=l/(24*60*60*1000);
	       long hour=(l/(60*60*1000)-day*24);
	       long min=((l/(60*1000))-day*24*60-hour*60);
	       long s=(l/1000-day*24*60*60-hour*60*60-min*60);
	        
	        sb = new StringBuffer();
	       //sb.append("发表于：");
	       if(day > 0){
	           sb.append(day+"天前");
	       }else if(hour > 0 ){
	           sb.append(hour+"小时前");
	       }else if(min > 0 ){
	           sb.append(min+"分钟前");
	       }else{
	    	   sb.append("刚刚");
	       }
	      // sb.append(s+"秒 前");
	      // sb.append("前");
	       //System.out.println(sb.toString());
	    } catch (ParseException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    
		return sb.toString();
	       
	}

	private static class ViewHolder {
		/** 和谁的聊天记录 */
		TextView name;
		/** 消息未读数 */
		TextView unreadLabel;
		/** 最后一此登录的地址 */
		TextView message;
		/** 最后一次登录的时间 */
		TextView time;
		/** 用户头像 */
		ImageView avatar;
		/** 性别 */
		TextView msgState;
		/** 城市 */
		TextView city;
		/**整个list中每一行总布局*/
		RelativeLayout list_item_layout;
		
		
	}
	
	String getStrng(Context context, int resId){
		return context.getResources().getString(resId);
	}
}
