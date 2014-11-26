package com.ccxt.whl.gushi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ccxt.whl.Constant;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.activity.BaseActivity;
import com.ccxt.whl.domain.MyBean;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.DeviceUuidFactory;
import com.ccxt.whl.utils.HttpRestClient;
import com.ccxt.whl.utils.JsonToMapList;
import com.ccxt.whl.utils.MyLogger;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 个人故事页
 * @author Administrator
 *
 */
@SuppressLint("NewApi")
public class User_gushi_Activity extends BaseActivity implements IXListViewListener {
    
	private XListView mListView;
	//private ArrayAdapter<User> mAdapter;
	private UserMyListAdapter mAdapter;
   
	private static List<MyBean> zainaList;//在哪故事数据集合

	
	public static final int FRIST_GET_DATE = 111;
	public static final int REFRESH_GET_DATE = 112;
	public static final int LOADMORE_GET_DATE = 113;
	
	//public static final int LOADMORE_GET_SEX = 114;
	
	private static int page = 0;
	
	private String userId = null;//要请求的用户唯一id
	//发布
	private LinearLayout publish;
	
	private static MyLogger Log = MyLogger.yLog();
	
    @Override
   	public void onCreate(Bundle savedInstanceState) {
   	 
   		super.onCreate(savedInstanceState);
   		setContentView(R.layout.activity_gushi);
   		
   		userId = getIntent().getStringExtra("userId");
   		
   		zainaList =  new ArrayList<MyBean>();//实例化在哪数据
   		publish = (LinearLayout)findViewById(R.id.fabu_w_gushi);
   		 
		mListView = (XListView)findViewById(R.id.user_gushi_xListView);
		mListView.setPullLoadEnable(true);
		  
		loaddata();
	 
		mAdapter =  new UserMyListAdapter(this, zainaList);
		mListView.setAdapter(mAdapter);
//		mListView.setPullLoadEnable(false);
//		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
	/*	mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//Intent intent = new Intent(getActivity(), Userinfo.class);
				//intent.putExtra("userId", mAdapter.getItem(arg2).getName());
				startActivity(new Intent(getActivity(), Userinfo.class).putExtra("userId", mAdapter.getItem(arg2-1).getUser()));
			}
			
		});*/
		publish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//TODO publish 
				Intent intent = new Intent(User_gushi_Activity.this, PublishActivity.class); 
				//intent.putExtra("ID", arg2);
				startActivity(intent);
			}
		});
    }
    /**
     * 第一次加载数据
     */
    private void loaddata() {
    	 mListView.setPullLoadEnable(false);
 		 mListView.setPullRefreshEnable(false);
 		 
		 geneItems(REFRESH_GET_DATE);
		
	}
 

	@Override
	public void onRefresh() { 
		mListView.setPullLoadEnable(false);
		 mListView.setPullRefreshEnable(false);
	    geneItems(REFRESH_GET_DATE); 
	}
    
	@SuppressLint("SimpleDateFormat")
	private void onLoad() {
		// TODO Auto-generated method stub
		mListView.stopRefresh();
		mListView.stopLoadMore();
		SimpleDateFormat  formatter = new SimpleDateFormat("yyyy年MM月dd日   HH:mm:ss");     
		Date  curDate  =  new Date(System.currentTimeMillis());//获取当前时间     
		String   str   =   formatter.format(curDate);     
		mListView.setRefreshTime(str);
		
		mListView.setPullLoadEnable(true);
		mListView.setPullRefreshEnable(true);
	}
	
	@Override
	public void onLoadMore() { 
		mListView.setPullLoadEnable(false);
		mListView.setPullRefreshEnable(false);
		geneItems(LOADMORE_GET_DATE); 
		
	}
	
	 
	private void geneItems(final int ACTION) {
		RequestParams params = new RequestParams(); 
		params.add("f_user", DemoApplication.getInstance().getUser());
		params.add("q_user", userId);
		DeviceUuidFactory uuid = new DeviceUuidFactory(this); 
		String uid = uuid.getDeviceUuid().toString(); 
		params.add("uid", uid);//新增设备请求
		 
		if(ACTION==REFRESH_GET_DATE){//刷新数据 
		 
			params.add("page", "0"); 
		 
		}else if(ACTION==LOADMORE_GET_DATE){//加载更多
		  
			params.add("page", ""+page);
		 
		}
	  
		HttpRestClient.get(Constant.GUSHI_USER, params, new BaseJsonHttpResponseHandler(){

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String rawJsonResponse, Object response) {
				//pd.dismiss();
				// TODO Auto-generated method stub
				Log.d(rawJsonResponse); 
				if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
					Toast.makeText(User_gushi_Activity.this, "您的网络不稳定,请检查网络！", 0).show();
					return;
				} 
				Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
				
        		if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
        			//Toast.makeText(getActivity(), "更新成功", 0).show();
        			//Log.d("log","message=="+lm.get("message").toString());
        			if(!CommonUtils.isNullOrEmpty(lm.get("result").toString())){
        				Log.d("reslut不为空");
        				List<Map<String, Object>> lmresarr = JsonToMapList.getList(lm.get("result").toString());  
        		       /*********************/
        				if(ACTION==REFRESH_GET_DATE||ACTION==FRIST_GET_DATE){
        		    	   zainaList.clear();
        		        }
        			   /*********************/
        				for(int i=0;i<lmresarr.size();i++){ 
        					
        					MyBean user = new MyBean();
        		            Log.d(lmresarr.get(i).get("id").toString());  
        		            Log.d(lmresarr.get(i).get("headurl").toString()); 
        		            Log.d(lmresarr.get(i).get("name").toString());  
        		            Log.d(lmresarr.get(i).get("content").toString());  
        		            Log.d(lmresarr.get(i).get("pic").toString());  
        		            Log.d(lmresarr.get(i).get("umd5").toString()); 
        		            Log.d(lmresarr.get(i).get("zan").toString()); 
        		            
        		            String strings[] = JsonToMapList.getArr(lmresarr.get(i).get("pic").toString());
        		            
        		            user.setId(lmresarr.get(i).get("id").toString());
        		            user.setAvator(lmresarr.get(i).get("headurl").toString());
        		            user.setName(lmresarr.get(i).get("name").toString());
        		            user.setContent(lmresarr.get(i).get("content").toString());
        		            user.setUrls(strings);
        		            user.setUser(lmresarr.get(i).get("umd5").toString());
        		            user.setTime(lmresarr.get(i).get("time").toString());
        		            user.setZan(lmresarr.get(i).get("zan").toString());
        		                		            
        		            zainaList.add(user);
        		            
        		        }  
        			    	
        				if(ACTION==FRIST_GET_DATE){//第一次加载
        					
        				}else if(ACTION==REFRESH_GET_DATE){//刷新数据
        					mAdapter = new UserMyListAdapter(User_gushi_Activity.this, zainaList);
            				mListView.setAdapter(mAdapter);
            				onLoad();
            				page = 1;
        				}else if(ACTION==LOADMORE_GET_DATE){//加载更多
        					mAdapter.notifyDataSetChanged();
        					onLoad();
        					page++; 
        				}
        				
        			}else{ 
        				Log.d("reslut为空");
        				if(ACTION==FRIST_GET_DATE){//第一次加载
        					
        				}else if(ACTION==REFRESH_GET_DATE){//刷新数据
        					Toast.makeText(User_gushi_Activity.this, lm.get("message").toString(), 0).show(); 
        					onLoad();
        				}else if(ACTION==LOADMORE_GET_DATE){//加载更多
        					Toast.makeText(User_gushi_Activity.this, lm.get("message").toString(), 0).show(); 
        					onLoad();
        				}
        					 
        			}
        		}else{
        			Log.d("reslut为空");
    				if(ACTION==FRIST_GET_DATE){//第一次加载
        				Toast.makeText(User_gushi_Activity.this, lm.get("message").toString(), 0).show();
        				onLoad();
    				}else if(ACTION==REFRESH_GET_DATE){//刷新数据
    					Toast.makeText(User_gushi_Activity.this, lm.get("message").toString(), 0).show(); 
    					onLoad();
    				}else if(ACTION==LOADMORE_GET_DATE){//加载更多
    					Toast.makeText(User_gushi_Activity.this, lm.get("message").toString(), 0).show(); 
    					onLoad();
    				}
        		}
				
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, String rawJsonData,
					Object errorResponse) {
				// TODO Auto-generated method stub
				Toast.makeText(User_gushi_Activity.this, "请求失败,请检查网络！", 0).show();
				
				onLoad();
				return;
			}

			@Override
			protected Object parseResponse(String rawJsonData, boolean isFailure)
					throws Throwable {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
	 
	}
	
	public void del_gushi(final int position){
		//System.out.println("=========================="+position);
		new AlertDialog.Builder(this)  
		.setTitle("要删除该故事吗？")   
		.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
            	 dialog.dismiss();  
                 //设置你的操作事项  
            	RequestParams params = new RequestParams(); 
        		params.add("f_user", DemoApplication.getInstance().getUser()); 
        		DeviceUuidFactory uuid = new DeviceUuidFactory(getParent()); 
        		String uid = uuid.getDeviceUuid().toString(); 
        		params.add("uid", uid);//新增设备请求
        		params.add("gu_id", zainaList.get(position).getId());//新增设备请求
        		 
        		HttpRestClient.get(Constant.GUSHI_DEL, params, new BaseJsonHttpResponseHandler(){

        			@Override
        			public void onSuccess(int statusCode, Header[] headers,
        					String rawJsonResponse, Object response) {
        				// TODO Auto-generated method stub
        				Log.d(rawJsonResponse); 
        				if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
        					Toast.makeText(User_gushi_Activity.this, "您的网络不稳定,请检查网络！", 0).show();
        					return;
        				} 
        				Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
        				
                		if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){ 
        					zainaList.remove(position);  
        					mAdapter.notifyDataSetChanged();
                		} 
                		
                		Toast.makeText(User_gushi_Activity.this, lm.get("message").toString(), 0).show();
                		 
        			}

        			@Override
        			public void onFailure(int statusCode, Header[] headers,
        					Throwable throwable, String rawJsonData,
        					Object errorResponse) {
        				// TODO Auto-generated method stub
        				Toast.makeText(User_gushi_Activity.this, "您的网络不稳定,请检查网络！", 0).show();
        				return;
        			}

        			@Override
        			protected Object parseResponse(String rawJsonData, boolean isFailure)
        					throws Throwable {
        				// TODO Auto-generated method stub
        				return null;
        			}
        			
        		});
               
            }  
        })  
		.setNegativeButton("取消", null)  
		.show();
		
		
	}
 
}
 