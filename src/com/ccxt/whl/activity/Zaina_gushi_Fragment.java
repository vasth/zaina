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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.ccxt.whl.Constant;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.db.ZanDao;
import com.ccxt.whl.domain.MyBean;
import com.ccxt.whl.gushi.MyListAdapter;
import com.ccxt.whl.gushi.PublishActivity;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.DeviceUuidFactory;
import com.ccxt.whl.utils.HttpRestClient;
import com.ccxt.whl.utils.JSONHelper;
import com.ccxt.whl.utils.JsonToMapList;
import com.ccxt.whl.utils.MyLogger;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;


@SuppressLint("NewApi")
public class Zaina_gushi_Fragment extends Fragment implements IXListViewListener {
    
	private XListView mListView;
	//private ArrayAdapter<User> mAdapter;
	private MyListAdapter mAdapter;
   
	private static List<MyBean> zainaList;//在哪故事数据集合

	
	public static final int FRIST_GET_DATE = 111;
	public static final int REFRESH_GET_DATE = 112;
	public static final int LOADMORE_GET_DATE = 113;
	
	//public static final int LOADMORE_GET_SEX = 114;
	
	private static int page = 0;
	
	//发布
	private LinearLayout publish;
	
	private static MyLogger Log = MyLogger.yLog();
	/*	OnMySelectedListener mListener;  //点击自己的item的时候跳转到个人页
 
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
    }  */
   
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	return inflater.inflate(R.layout.fragment_conversation_gushi, container, false);
         
    }
    
    
    
    @Override
   	public void onActivityCreated(Bundle savedInstanceState) {
   	 
   		super.onActivityCreated(savedInstanceState);
   		zainaList =  new ArrayList<MyBean>();//实例化在哪数据
   		 
		mListView = (XListView)getView().findViewById(R.id.gushi_xListView);
		mListView.setPullLoadEnable(true);
		 
		publish = (LinearLayout)getView().findViewById(R.id.fabu_gushi);
		
		loaddata();
	 
		//mAdapter = new ZainaAdapter(getActivity(), R.layout.zaina_list_item, zainaList);
//		mAdapter =  new MyListAdapter(getActivity(), zainaList);
//		mListView.setAdapter(mAdapter);
//		mListView.setPullLoadEnable(false);
//		mListView.setPullRefreshEnable(false);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				//Intent intent = new Intent(getActivity(), Userinfo.class);
				//intent.putExtra("userId", mAdapter.getItem(arg2).getName());
				startActivity(new Intent(getActivity(), Userinfo.class).putExtra("userId", mAdapter.getItem(arg2-1).getUser()));
			}
			
		});
		/*mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				Log.d("log","position"+(position-1));
				 
				Intent intent = new Intent(getActivity(), Userinfo.class);
				intent.putExtra("userId", mAdapter.getItem(position-1).getUsername());
				intent.putExtra("nickname", mAdapter.getItem(position-1).getNick());
				intent.putExtra("headurl", mAdapter.getItem(position-1).getHeaderurl());
				intent.putExtra("UserSex", mAdapter.getItem(position-1).getSex());
				intent.putExtra("UserAge", mAdapter.getItem(position-1).getAge());
				intent.putExtra("UserArea", mAdapter.getItem(position-1).getCity());
				intent.putExtra("UserZaina", mAdapter.getItem(position-1).getJiedao());
				
				if(mAdapter.getItem(position-1).getUsername().equals(DemoApplication.getInstance().getUser())){
					//mListener.onMySelected(3);  
					//Toast.makeText(getActivity(), "不能和自己聊天！", 0).show();
					return;
				}
				 
				startActivity(intent);

			}
			
		});*/
		
	 
		
		publish.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				//TODO publish
				Intent iontent = new Intent();
				Intent intent = new Intent(getActivity(), PublishActivity.class); 
				//intent.putExtra("ID", arg2);
				startActivity(intent);
			}
		});
		
    }
    /**
     * 第一次加载数据
     */
    private void loaddata() {
    	String json = "{\"status\":\"yes\",\"message\":\"ok\",\"result\":["
				+ "{\"id\":110,\"avator\":\"http://img0.bdstatic.com/img/image/shouye/leimu/mingxing.jpg\",\"name\":\"张三\",\"content\":\"大家好\",\"urls\":[]},"
				+ "{\"id\":111,\"avator\":\"http://img0.bdstatic.com/img/image/shouye/leimu/mingxing2.jpg\",\"name\":\"李四\",\"content\":\"大家好\",\"urls\":[\"http://d.hiphotos.baidu.com/album/w%3D2048/sign=14b0934b78310a55c424d9f4837d42a9/a8014c086e061d95e9fd3e807af40ad163d9cacb.jpg\"]},"
				+ "{\"id\":112,\"avator\":\"http://img0.bdstatic.com/img/image/shouye/leimu/mingxing1.jpg\",\"name\":\"王五\",\"content\":\"大家好\",\"urls\":[\"http://g.hiphotos.bdimg.com/album/s%3D680%3Bq%3D90/sign=ccd33b46d53f8794d7ff4b26e2207fc9/0d338744ebf81a4c0f993437d62a6059242da6a1.jpg\",\"http://c.hiphotos.bdimg.com/album/s%3D900%3Bq%3D90/sign=b8658f17f3d3572c62e290dcba28121a/5fdf8db1cb134954bb97309a574e9258d0094a47.jpg\"]},"
				+ "{\"id\":113,\"avator\":\"http://img0.bdstatic.com/img/image/shouye/leimu/mingxing6.jpg\",\"name\":\"赵六\",\"content\":\"大家好\",\"urls\":[\"http://f.hiphotos.bdimg.com/album/s%3D680%3Bq%3D90/sign=6b62f61bac6eddc422e7b7f309e0c7c0/6159252dd42a2834510deef55ab5c9ea14cebfa1.jpg\",\"http://g.hiphotos.bdimg.com/album/s%3D680%3Bq%3D90/sign=ccd33b46d53f8794d7ff4b26e2207fc9/0d338744ebf81a4c0f993437d62a6059242da6a1.jpg\",\"http://c.hiphotos.bdimg.com/album/s%3D680%3Bq%3D90/sign=cdab1512d000baa1be2c44b3772bc82f/91529822720e0cf3855c96050b46f21fbf09aaa1.jpg\"]}]}";

    	//String json =  "{"status":"yes","message":"","result":[{"time":"1408204281","content":"\u6bd4\u6211","pic":"[\"4,a9af242396\"]","umd5":"ff9e537dbea1b731de24c85116a2fff8","age":"23","sex":"1","headurl":"http:\/\/img1.touxiang.cn\/uploads\/20121129\/29-032851_236.jpg","name":"\u6768\u65f8"}]}';
    	//jiexi(json, FRIST_GET_DATE);
		 geneItems(REFRESH_GET_DATE);
		 
	}

     
    
	/*private void jiexi(String rawJsonResponse,final int ACTION) {
		// TODO Auto-generated method stub
		Log.d("log",rawJsonResponse); 
		if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
			Toast.makeText(getActivity(), "您的网络不稳定,请检查网络！", 0).show();
			return;
		} 
		Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
		
		if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
			 
			if(!CommonUtils.isNullOrEmpty(lm.get("result").toString())){
				Log.d("log","reslut不为空");
				List<Map<String, Object>> lmresarr = JsonToMapList.getList(lm.get("result").toString());  
		       *//*********************//*
				if(ACTION==REFRESH_GET_DATE||ACTION==FRIST_GET_DATE){
		    	   zainaList.clear();
		        }
			   *//*********************//*
				for(int i=0;i<lmresarr.size();i++){ 
					MyBean user = new MyBean();
		            Log.d("",lmresarr.get(i).get("id").toString());  
		            Log.d("",lmresarr.get(i).get("avator").toString()); 
		            Log.d("",lmresarr.get(i).get("name").toString());  
		            Log.d("",lmresarr.get(i).get("content").toString());  
		            Log.d("",lmresarr.get(i).get("urls").toString());  
		            
		          
		           // List<Map<String, Object>> lists = JsonToMapList.getList(lmresarr.get(i).get("urls").toString());  
		            
		            String strings[] = JsonToMapList.getArr(lmresarr.get(i).get("urls").toString());
		            for(int ii=0;ii<lists.size();ii++){  
		            	strings[ii]=lists.get(ii).toString(); 
			        } 
		            user.setId(lmresarr.get(i).get("id").toString());
		            user.setAvator(lmresarr.get(i).get("avator").toString());
		            user.setName(lmresarr.get(i).get("name").toString());
		            user.setContent(lmresarr.get(i).get("content").toString());
		            user.setUrls(strings);
		            //user.setZan(lmresarr.get(i).get("zan").toString());
		           
		            zainaList.add(user);
		        }  
			    	
				if(ACTION==FRIST_GET_DATE){//第一次加载
					mAdapter = new MyListAdapter(getActivity(), zainaList);
    				mListView.setAdapter(mAdapter);
    				
    				//如果loading条没有开启
					//pd.dismiss();
					//pd.dismiss();
				}else if(ACTION==REFRESH_GET_DATE){//刷新数据
					 
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
				Log.d("log","reslut为空");
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
	}*/



	@Override
	public void onRefresh() {
		String json = "{\"status\":\"yes\",\"message\":\"ok\",\"result\":["
				+ "{\"id\":110,\"avator\":\"http://img0.bdstatic.com/img/image/shouye/leimu/mingxing.jpg\",\"name\":\"张三\",\"content\":\"大家好\",\"urls\":[]},"
				+ "{\"id\":111,\"avator\":\"http://img0.bdstatic.com/img/image/shouye/leimu/mingxing2.jpg\",\"name\":\"李四\",\"content\":\"大家好\",\"urls\":[\"http://d.hiphotos.baidu.com/album/w%3D2048/sign=14b0934b78310a55c424d9f4837d42a9/a8014c086e061d95e9fd3e807af40ad163d9cacb.jpg\"]},"
				+ "{\"id\":112,\"avator\":\"http://img0.bdstatic.com/img/image/shouye/leimu/mingxing1.jpg\",\"name\":\"王五\",\"content\":\"大家好\",\"urls\":[\"http://g.hiphotos.bdimg.com/album/s%3D680%3Bq%3D90/sign=ccd33b46d53f8794d7ff4b26e2207fc9/0d338744ebf81a4c0f993437d62a6059242da6a1.jpg\",\"http://c.hiphotos.bdimg.com/album/s%3D900%3Bq%3D90/sign=b8658f17f3d3572c62e290dcba28121a/5fdf8db1cb134954bb97309a574e9258d0094a47.jpg\"]},"
				+ "{\"id\":113,\"avator\":\"http://img0.bdstatic.com/img/image/shouye/leimu/mingxing6.jpg\",\"name\":\"赵六\",\"content\":\"大家好\",\"urls\":[\"http://f.hiphotos.bdimg.com/album/s%3D680%3Bq%3D90/sign=6b62f61bac6eddc422e7b7f309e0c7c0/6159252dd42a2834510deef55ab5c9ea14cebfa1.jpg\",\"http://g.hiphotos.bdimg.com/album/s%3D680%3Bq%3D90/sign=e58fb67bc8ea15ce45eee301863b4bce/a5c27d1ed21b0ef4fd6140a0dcc451da80cb3e47.jpg\",\"http://c.hiphotos.bdimg.com/album/s%3D680%3Bq%3D90/sign=cdab1512d000baa1be2c44b3772bc82f/91529822720e0cf3855c96050b46f21fbf09aaa1.jpg\"]}]}";
		
		//jiexi(json, REFRESH_GET_DATE);
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
	}
	
	@Override
	public void onLoadMore() { 
		geneItems(LOADMORE_GET_DATE); 
	}
	
	/**
	 * 普通加载数据
	 * /
	private void geneItems(final int ACTION) {
		RequestParams params = new RequestParams(); 
		params.add("user", DemoApplication.getInstance().getUser());
		params.add("sex", PreferenceUtils.getInstance(getActivity()).getloadsex());
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
					  //mListener.onrefresh("我在"+adr);
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
				 Log.d("log","jingweidunow:===>"+jingweidunow);
				 String[] strarray = jingweidu.split(","); 
				 double dis = CommonUtils.Distance( Double.parseDouble(Double.toString(Longitude)), Double.parseDouble(Double.toString(Latitude)), 
						 Double.parseDouble(strarray[0]),Double.parseDouble(strarray[1]));
				 Log.d("log","Distance:===>"+dis);//当距离大于一些上传经纬度
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
			 
		}
	  
		HttpRestClient.get(Constant.ZAINA_URL, params, new BaseJsonHttpResponseHandler(){

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					String rawJsonResponse, Object response) {
				//pd.dismiss();
				// TODO Auto-generated method stub
				Log.d("log",rawJsonResponse); 
				if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
					Toast.makeText(getActivity(), "您的网络不稳定,请检查网络！", 0).show();
					return;
				} 
				Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
				
        		if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
        			//Toast.makeText(getActivity(), "更新成功", 0).show();
        			//Log.d("log","message=="+lm.get("message").toString());
        			if(!CommonUtils.isNullOrEmpty(lm.get("result"))){
        				Log.d("log","reslut不为空");
        				List<Map<String, Object>> lmresarr = JsonToMapList.getList(lm.get("result").toString());  
        		       /********************* /
        				if(ACTION==REFRESH_GET_DATE||ACTION==FRIST_GET_DATE){
        		    	   zainaList.clear();
        		        }
        			   /********************* /
        				for(int i=0;i<lmresarr.size();i++){ 
        					User user = new User();
        		            Log.d("", lmresarr.get(i).get("time").toString());  
        		            Log.d("",lmresarr.get(i).get("jiedao").toString()); 
        		            Log.d("",lmresarr.get(i).get("umd5").toString());  
        		            Log.d("",lmresarr.get(i).get("age").toString());  
        		            Log.d("",lmresarr.get(i).get("sex").toString()); 
        		            Log.d("",lmresarr.get(i).get("headurl").toString()); 
        		            Log.d("",lmresarr.get(i).get("name").toString());
        		            Log.d("",lmresarr.get(i).get("city").toString());
        		            
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
        					}* /
        					/*mAdapter = new ZainaAdapter(getActivity(), R.layout.zaina_list_item, zainaList);
        					mListView.setAdapter(mAdapter);* /
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
        				Log.d("log","reslut为空");
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
        			Log.d("log","reslut为空");
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
	 
	}*/
	private void geneItems(final int ACTION) {
		RequestParams params = new RequestParams(); 
		params.add("f_user", DemoApplication.getInstance().getUser());
		DeviceUuidFactory uuid = new DeviceUuidFactory(getActivity()); 
		String uid = uuid.getDeviceUuid().toString(); 
		params.add("uid", uid);//新增设备请求
		 
		if(ACTION==REFRESH_GET_DATE){//刷新数据 
			ZanDao zandao = new ZanDao(getActivity());
			String[] upzan = zandao.getUPZanGushi();
			if(!CommonUtils.isNullOrEmpty(upzan)){ 
				params.add("upzan", JSONHelper.toJSON(upzan)); 
			}
			params.add("page", "0"); 
			
		 
		}else if(ACTION==LOADMORE_GET_DATE){//加载更多
		  
			params.add("page", ""+page);
		 
		}
	  
		HttpRestClient.get(Constant.GUSHI_NEW, params, new BaseJsonHttpResponseHandler(){

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
        			
        			//for(int i=0 ; i<upzan.){
        			//zandao.getUPZanGushi();
        			//}
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
        					mAdapter = new MyListAdapter(getActivity(), zainaList);
            				mListView.setAdapter(mAdapter);
            				onLoad();
            				page = 1;
            				ZanDao zandao = new ZanDao(getActivity());
            				String[] upzan = zandao.getUPZanGushi();
            				if(!CommonUtils.isNullOrEmpty(upzan)){
            					for(int i = 0;i<upzan.length;i++){
                					zandao.updategushi(upzan[i]);
                				}
            				}
            				
        				}else if(ACTION==LOADMORE_GET_DATE){//加载更多
        					mAdapter.notifyDataSetChanged();
        					onLoad();
        					page++; 
        				}
        				
        			}else{ 
        				Log.d("reslut为空");
        				if(ACTION==FRIST_GET_DATE){//第一次加载
        					
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
        				Toast.makeText(getActivity(), lm.get("message").toString(), 0).show();
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
 
}
 