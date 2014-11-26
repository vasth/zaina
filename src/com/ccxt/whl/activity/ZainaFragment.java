package com.ccxt.whl.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import org.apache.http.Header;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ccxt.whl.Constant;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.adapter.ZainaAdapter;
import com.ccxt.whl.domain.User;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.HttpRestClient;
import com.ccxt.whl.utils.JsonToMapList;
import com.ccxt.whl.utils.MyLogger;
import com.ccxt.whl.utils.PreferenceUtils;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

@SuppressLint("NewApi")
public class ZainaFragment extends Fragment implements IXListViewListener {
    
	private XListView mListView;
	private ArrayAdapter<User> mAdapter;
	private ArrayList<String> items = new ArrayList<String>();
	private Handler mHandler;
	private int start = 0;
	private static int refreshCnt = 0;
	private static List<User> zainaList;//在哪数据集合
	private static ProgressDialog pd = null ;
	private static boolean progressShow = false;//进度条是否显示
	
	public static final int FRIST_GET_DATE = 111;
	public static final int REFRESH_GET_DATE = 112;
	public static final int LOADMORE_GET_DATE = 113;
	
	//public static final int LOADMORE_GET_SEX = 114;
	
	private static int page = 1;
	
	OnMySelectedListener mListener;  //点击自己的item的时候跳转到个人页
	
	private LinearLayout pengpeng;
	private LinearLayout shaixuan;
	
	private static MyLogger Log = MyLogger.yLog();
	
	public interface OnMySelectedListener {  
        public void onMySelected(int i);

        public void onrefresh(String string);   
    } 
	 
	@Override  
    public void onAttach(Activity activity) {  
        super.onAttach(activity);  
        try {  
            mListener = (OnMySelectedListener) activity;  
         } catch (ClassCastException e) {  
            throw new ClassCastException(activity.toString() + " must implement OnMySelectedListener");  
        }  
    }  
   
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.fragment_conversation_zaina, container, false);
         
    }
    
    
    
    @Override
   	public void onActivityCreated(Bundle savedInstanceState) {
   		// TODO Auto-generated method stub
   		super.onActivityCreated(savedInstanceState);
   		zainaList =  new ArrayList<User>();//实例化在哪数据
   		//onRefresh();
		mListView = (XListView)getView(). findViewById(R.id.xListView);
		mListView.setPullLoadEnable(true);
		
		pengpeng = (LinearLayout)getView().findViewById(R.id.pengpeng);
		shaixuan = (LinearLayout)getView().findViewById(R.id.shaixuan);
		
		loaddata();
		//mListView.dorefresh();
		
		//Log.d("log","toarray"+zainaList.toArray());
		//mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, items);
		mAdapter = new ZainaAdapter(getActivity(), R.layout.zaina_list_item, zainaList);
		mListView.setAdapter(mAdapter);
//		mListView.setPullLoadEnable(false);
//		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mHandler = new Handler();
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Log.d("position"+(position-1));
				// TODO Auto-generated method stub
				// demo中直接进入聊天页面，实际一般是进入用户详情页
				//Intent intent = new Intent(getActivity(), ChatActivity.class);
				Intent intent = new Intent(getActivity(), Userinfo.class);
				intent.putExtra("userId", mAdapter.getItem(position-1).getUsername());
				intent.putExtra("nickname", mAdapter.getItem(position-1).getNick());
				intent.putExtra("headurl", mAdapter.getItem(position-1).getHeaderurl());
				intent.putExtra("UserSex", mAdapter.getItem(position-1).getSex());
				intent.putExtra("UserAge", mAdapter.getItem(position-1).getAge());
				intent.putExtra("UserArea", mAdapter.getItem(position-1).getCity());
				intent.putExtra("UserZaina", mAdapter.getItem(position-1).getJiedao());
				
				if(mAdapter.getItem(position-1).getUsername().equals(DemoApplication.getInstance().getUser())){
					mListener.onMySelected(4);  //跳转设置页面
					//Toast.makeText(getActivity(), "不能和自己聊天！", 0).show();
					return;
				}
				/****缺少判断是否已经插入数据库***
				User local_user = new User();
				local_user.setUsername(mAdapter.getItem(position-1).getUsername());
				local_user.setNick(mAdapter.getItem(position-1).getNick());
				local_user.setHeaderurl(mAdapter.getItem(position-1).getHeaderurl());
				UserDao userdao = new UserDao(getActivity());
				userdao.saveContact_m(local_user);*/
				startActivity(intent);

			}
			
		});
		
		pengpeng.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mListView.startrefresh();
				// TODO Auto-generated method stub
				Log.d("pengpeng=======================================================");
			}
		});
		
		shaixuan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new Builder(getActivity());
				String oldsex =  PreferenceUtils.getInstance(getActivity()).getloadsex();
				String loadtimeloc =  PreferenceUtils.getInstance(getActivity()).getloadtimeloc();//获取筛选类型
				String quanbu = "  " , nan= "  " , nv = "  ",loadtime = " ",loadloc = " ";
				if(oldsex.equals("1")){
					nan = nan + "√";
				}else if(oldsex.equals("2")){
					nv = nv + "√";
				}else{
					quanbu = quanbu + "√";
				}
				 String[] strarr = {"全部"+quanbu,"男"+nan,"女"+nv};
		        builder.setItems(strarr, new DialogInterface.OnClickListener()
		        {
		            public void onClick(DialogInterface arg0, int arg1)
		            {
		            	String sex = "0";
		                // TODO 自动生成的方法存根 
		                if (arg1 == 0) {//全部
		                	sex = "0";
		                }else if(arg1 == 1){//男
		                	sex = "1"; 
		                }else{
		                	sex = "2";
		                }
		                
				          PreferenceUtils.getInstance(getActivity()).setloadsex(sex);
				          progressShow = false;
				          onRefresh();
		            }
		        });
		        if(loadtimeloc.equals("1")){//1是距离筛选，0或者为空则是时间筛选
		        	loadloc = loadloc + "√";
		        }else{
		        	loadtime = loadtime + "√";
		        }
		        builder.setPositiveButton("时间筛选"+loadtime, new DialogInterface.OnClickListener() {   
   
                    @Override   
                    public void onClick(DialogInterface dialog, int which) {   
                    	PreferenceUtils.getInstance(getActivity()).setloadtimeloc("0");
				          progressShow = false;
				          onRefresh();
                    }   
                });
		        builder.setNegativeButton("距离筛选"+loadloc, new DialogInterface.OnClickListener() {   
   
                    @Override   
                    public void onClick(DialogInterface dialog, int which) {   
                        // TODO Auto-generated method stub   
                    	PreferenceUtils.getInstance(getActivity()).setloadtimeloc("1");
				          progressShow = false;
				          onRefresh();
                    }   
                });
		        
		        builder.show();
				// TODO Auto-generated method stub
				Log.d("shaixuan=======================================================");
			}
		});
		
    }
    /**
     * 第一次加载数据
     */
    private void loaddata() {
		// TODO Auto-generated method stub
    	//Log.d("log","loaddata=======================================================");
    	//if(zainaList.size()<1){
    	//	progressShow = false;
    	//}
    	//if(progressShow){
    	//	return;
    	//}
    	//progressShow = true;//开启loading条
        //pd = new ProgressDialog(getActivity()); 
		//pd.setMessage("正在加载在哪栏目数据...");
		//Log.d("log","loaddata正在加载在哪栏目数据...");
		//pd.show();
		geneItems(FRIST_GET_DATE);
		/******请求数据******//*
		RequestParams params = new RequestParams(); 
		params.add("user", DemoApplication.getInstance().getUser());
		*//******获取位置信息并上传******//*
		HttpRestClient.get(Constant.ZAINA_URL, params, new BaseJsonHttpResponseHandler(){

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
        			Toast.makeText(getActivity(), "加载成功", 0).show();
        			Log.d("log","message=="+lm.get("message").toString());
        			if(!CommonUtils.isNullOrEmpty(lm.get("result").toString())){ 
        				List<Map<String, Object>> lmresarr = JsonToMapList.getList(lm.get("result").toString());   
        				for(int i=0;i<lmresarr.size();i++){ 
        					User user = new User();
        		            Log.d("log",lmresarr.get(i).get("time"));  
        		            Log.d("log",lmresarr.get(i).get("jiedao")); 
        		            Log.d("log",lmresarr.get(i).get("umd5"));  
        		            Log.d("log",lmresarr.get(i).get("age"));  
        		            Log.d("log",lmresarr.get(i).get("sex")); 
        		            Log.d("log",lmresarr.get(i).get("headurl")); 
        		            Log.d("log",lmresarr.get(i).get("name"));
        		            
        		            user.setNick(lmresarr.get(i).get("name").toString());
        		            user.setUsername(lmresarr.get(i).get("umd5").toString());
        		            user.setAge(lmresarr.get(i).get("age").toString());
        		            user.setHeaderurl(lmresarr.get(i).get("headurl").toString());
        		            user.setJiedao(lmresarr.get(i).get("jiedao").toString());
        		            user.setLasttime(lmresarr.get(i).get("time").toString());
        		            user.setSex(lmresarr.get(i).get("sex").toString());
        		            Log.d("log",user.toString());
        		            zainaList.add(user);
        		        }  
        				
        				mAdapter = new ZainaAdapter(getActivity(), R.layout.zaina_list_item, zainaList);
        				mListView.setAdapter(mAdapter);
        				
        				//如果loading条没有开启
        				if (!progressShow) {
        					return;
    					}else{
    						pd.dismiss();
    						progressShow = false;
    					}				
        			}else{
        				//如果loading条没有开启
        				if (!progressShow) {
        					Toast.makeText(getActivity(), "暂无在哪的数据", 0).show(); 
    					}else{
    						pd.dismiss();
    						progressShow = false;
    					}
        				
        			}
        		}
				
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, String rawJsonData,
					Object errorResponse) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "请求失败,请检查网络！", 0).show();
				return;
			}

			@Override
			protected Object parseResponse(String rawJsonData, boolean isFailure)
					throws Throwable {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
	   */
	}

    /**
     * 第一次加载数据
     
    private void loadshaixuan() {
		// TODO Auto-generated method stub
    	Log.d("log","loaddata=======================================================");
    	if(progressShow){
    		return;
    	}
    	progressShow = true;//开启loading条
        pd = new ProgressDialog(getActivity()); 
		pd.setMessage("正在加载在哪栏目数据...");
		pd.show();
		geneItems(FRIST_GET_DATE);
		/******请求数据******//*
		RequestParams params = new RequestParams(); 
		params.add("user", DemoApplication.getInstance().getUser());
		*//******获取位置信息并上传****** / 
	}*/
    
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub 
				//start = ++refreshCnt;
				
				geneItems(REFRESH_GET_DATE);
				// mAdapter.notifyDataSetChanged();
				//mAdapter = new ArrayAdapter<User>(getActivity(), R.layout.list_item, zainaList);
				/*	for(int i =0 ; i<zainaList.size();i++){
					Log.d("log","=================>"+zainaList.get(i).getNick());
				}
				mAdapter = new ZainaAdapter(getActivity(), R.layout.zaina_list_item, zainaList);
				mListView.setAdapter(mAdapter);
				onLoad();*/
	 
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
	}
	
	@Override
	public void onLoadMore() {
		
		geneItems(LOADMORE_GET_DATE);
		
		/*// TODO Auto-generated method stub
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				
				mAdapter.notifyDataSetChanged();
				onLoad();
			}
		}, 2000);*/
	}
	
	/**
	 * 普通加载数据
	 */
	private void geneItems(final int ACTION) {
		RequestParams params = new RequestParams(); 
		params.add("user", DemoApplication.getInstance().getUser());
		params.add("sex", PreferenceUtils.getInstance(getActivity()).getloadsex());
		//新增按时间地区筛选
		params.add("juli", PreferenceUtils.getInstance(getActivity()).getloadtimeloc());
		if(ACTION==FRIST_GET_DATE){//第一次加载 (暂时无法判断是否是第一次加载，因为加了一个性别)
			 params.add("load", "f");
			if (DemoApplication.getInstance().getlastloc() != null) {
				  double Latitude = DemoApplication.getInstance().getlastloc().getLatitude(); 
				  double Longitude = DemoApplication.getInstance().getlastloc().getLongitude();
				  String adr = DemoApplication.getInstance().getlastloc().getAddrStr();
				  //String Street = DemoApplication.getInstance().getlastloc().getStreet();  
				  params.add("jingweidu", Double.toString(Longitude)+
						  ","+Double.toString(Latitude)); 
				  params.add("jiedao", adr);
				  //存储经纬度数据
				  PreferenceUtils.getInstance(getActivity()).setSettingUserloc(Double.toString(Longitude)+
						  ","+Double.toString(Latitude));
				  if(!CommonUtils.isNullOrEmpty(adr)){
					  PreferenceUtils.getInstance(getActivity()).setSettingUserZaina("我在"+adr);
					  mListener.onrefresh("我在"+adr);
				  }
				  
			}
			
		}else if(ACTION==REFRESH_GET_DATE){//刷新数据
			 DemoApplication.getInstance().startLocate();//每刷新一次，定一次位置
			 params.add("load", "r");
			 String jingweidu = PreferenceUtils.getInstance(getActivity()).getSettingUserloc();
			 //如果上个经纬度没有保存而现在可以获取到经纬度则使用现在经纬度
			 if(CommonUtils.isNullOrEmpty(jingweidu)&&DemoApplication.getInstance().getlastloc() != null){
				  double Latitude = DemoApplication.getInstance().getlastloc().getLatitude(); 
				  double Longitude = DemoApplication.getInstance().getlastloc().getLongitude();
				  String adr = DemoApplication.getInstance().getlastloc().getAddrStr(); 
				  params.add("jingweidu", Double.toString(Longitude)+
						  ","+Double.toString(Latitude)); 
				  params.add("jiedao", adr);
				  //存储经纬度数据
				  if(!CommonUtils.isNullOrEmpty(adr)){
					  PreferenceUtils.getInstance(getActivity()).setSettingUserloc(Double.toString(Longitude)+
							  ","+Double.toString(Latitude));
					  PreferenceUtils.getInstance(getActivity()).setSettingUserZaina("我在"+adr);
				  }
				  
			  //如果上次保存的有经纬度而且这次也获得到经纬度
			 }else if (DemoApplication.getInstance().getlastloc() != null) {
				  double Latitude = DemoApplication.getInstance().getlastloc().getLatitude()  ; 
				  double Longitude = DemoApplication.getInstance().getlastloc().getLongitude() ;
				  String adr = DemoApplication.getInstance().getlastloc().getAddrStr();
				  //String Street = DemoApplication.getInstance().getlastloc().getStreet();
				 String jingweidunow = Double.toString(Longitude)+ ","+Double.toString(Latitude);
				 Log.d("jingweidunow:===>"+jingweidunow);
				 String[] strarray = jingweidu.split(","); 
				 double dis = CommonUtils.Distance( Double.parseDouble(Double.toString(Longitude)), Double.parseDouble(Double.toString(Latitude)), 
						 Double.parseDouble(strarray[0]),Double.parseDouble(strarray[1]));
				 Log.d("Distance:===>"+dis);//当距离大于一些上传经纬度
				 if(Double.valueOf(dis) > 1000){
					 params.add("jingweidu", Double.toString(Longitude)+
							  ","+Double.toString(Latitude)); 
					 params.add("jiedao", adr);
					//存储经纬度数据 
					  PreferenceUtils.getInstance(getActivity()).setSettingUserloc(Double.toString(Longitude)+
							  ","+Double.toString(Latitude)); 
					 
				 }
				 if(!CommonUtils.isNullOrEmpty(adr)){
					 PreferenceUtils.getInstance(getActivity()).setSettingUserZaina("我在"+adr);
				 }
				 
			 }
			// jingweidu
		}else if(ACTION==LOADMORE_GET_DATE){//加载更多
			 
			 params.add("load", "l");
			 params.add("page", ""+page);
			 //System.out.println("page=========================="+page);
			 //System.out.println("size=========================="+zainaList.size());
		}
	  
		HttpRestClient.get(Constant.ZAINA_URL, params, new BaseJsonHttpResponseHandler(){

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String rawJsonResponse, Object response) {
				//pd.dismiss();
				// TODO Auto-generated method stub
				Log.d(rawJsonResponse); 
				if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
					Toast.makeText(getActivity(), "您的网络不稳定,请检查网络！", 0).show();
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
        					User user = new User();
        		            Log.d(lmresarr.get(i).get("time").toString());  
        		            Log.d(lmresarr.get(i).get("jiedao").toString()); 
        		            Log.d(lmresarr.get(i).get("umd5").toString());  
        		            Log.d(lmresarr.get(i).get("age").toString());  
        		            Log.d(lmresarr.get(i).get("sex").toString()); 
        		            Log.d(lmresarr.get(i).get("headurl").toString()); 
        		            Log.d(lmresarr.get(i).get("name").toString());
        		            Log.d(lmresarr.get(i).get("city").toString());
        		            
        		            user.setNick(lmresarr.get(i).get("name").toString());
        		            user.setUsername(lmresarr.get(i).get("umd5").toString());
        		            user.setAge(lmresarr.get(i).get("age").toString());
        		            user.setHeaderurl(lmresarr.get(i).get("headurl").toString());
        		            user.setJiedao(lmresarr.get(i).get("jiedao").toString());
        		            user.setLasttime(lmresarr.get(i).get("time").toString());
        		            user.setSex(lmresarr.get(i).get("sex").toString());
        		            user.setCity(lmresarr.get(i).get("province").toString()+" "+lmresarr.get(i).get("city").toString());
        		             
        		            zainaList.add(user);
        		        }  
        			    	
        				if(ACTION==FRIST_GET_DATE){//第一次加载
        					mAdapter = new ZainaAdapter(getActivity(), R.layout.zaina_list_item, zainaList);
            				mListView.setAdapter(mAdapter);
            				
            				//如果loading条没有开启
        					//pd.dismiss();
        					//pd.dismiss();
        				}else if(ACTION==REFRESH_GET_DATE){//刷新数据
        					/*for(int i =0 ; i<zainaList.size();i++){
        						Log.d("log","=================>"+zainaList.get(i).getNick());
        					}*/
        					/*mAdapter = new ZainaAdapter(getActivity(), R.layout.zaina_list_item, zainaList);
        					mListView.setAdapter(mAdapter);*/
        					mAdapter.notifyDataSetChanged();
        					onLoad();
        					page = 1;
        				}else if(ACTION==LOADMORE_GET_DATE){//加载更多
        					//mAdapter.addAll(zainaList);
        					mAdapter.notifyDataSetChanged();
        					onLoad();
        					page++;
        				}
        				
        			}else{ 
        				Log.d("reslut为空");
        				if(ACTION==FRIST_GET_DATE){//第一次加载
        					//pd.dismiss();
        					//progressShow = false;
        					//如果loading条没有开启
            				//if (!progressShow) {
            					Toast.makeText(getActivity(), lm.get("message").toString(), 0).show();
        					//}else{ 
        						//progressShow = false;
        					//} 
            				onLoad();
        				}else if(ACTION==REFRESH_GET_DATE){//刷新数据
        					Toast.makeText(getActivity(), lm.get("message").toString(), 0).show(); 
        					onLoad();
        				}else if(ACTION==LOADMORE_GET_DATE){//加载更多
        					Toast.makeText(getActivity(), lm.get("message").toString(), 0).show(); 
        					onLoad();
        				}
        					 
        			}
        		}else{
        			Log.d("reslut为空");
    				if(ACTION==FRIST_GET_DATE){//第一次加载
    					//pd.dismiss();
    					//progressShow = false;
    					//如果loading条没有开启
        				//if (!progressShow) {
        					Toast.makeText(getActivity(), lm.get("message").toString(), 0).show();
    					//}else{ 
    						//progressShow = false;
    					//} 
        				onLoad();
    				}else if(ACTION==REFRESH_GET_DATE){//刷新数据
    					Toast.makeText(getActivity(), lm.get("message").toString(), 0).show(); 
    					onLoad();
    				}else if(ACTION==LOADMORE_GET_DATE){//加载更多
    					Toast.makeText(getActivity(), lm.get("message").toString(), 0).show(); 
    					onLoad();
    				}
        		}
				
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					Throwable throwable, String rawJsonData,
					Object errorResponse) {
				// TODO Auto-generated method stub
				Toast.makeText(getActivity(), "请求失败,请检查网络！", 0).show();
				//if (progressShow) {
					//pd.dismiss();
				//}
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
		/*for (int i = 0; i != 20; ++i) {
			items.add("refresh cnt " + (++start));
		}*/
	}
	
	/*@Override
	public void onResume() {
		// TODO Auto-generated method stub
		 Log.d("log","onResume");
		if (DemoApplication.getInstance().mLocClient != null) {
			 DemoApplication.getInstance().startLocate();
		}
		
		super.onResume();
	}
	
	@Override
	public void onDestroy() {
		 Log.d("log","onDestroy");
		if (DemoApplication.getInstance().mLocClient != null) {
			 DemoApplication.getInstance().stopLocate();
		}
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onPause() {
		Log.d("log","onPause");
		if (DemoApplication.getInstance().mLocClient != null) {
			 DemoApplication.getInstance().stopLocate();
		}
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		Log.d("log","onStart");
		super.onStart();
	}*/
	

	
}

	
