package com.ccxt.whl.gushi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import com.ccxt.whl.Constant;
import com.ccxt.whl.DemoApplication;
import com.ccxt.whl.R;
import com.ccxt.whl.utils.CommonUtils;
import com.ccxt.whl.utils.DeviceUuidFactory;
import com.ccxt.whl.utils.HttpRestClient;
import com.ccxt.whl.utils.ImageOptions;
import com.ccxt.whl.utils.JsonToMapList;
import com.ccxt.whl.utils.MyLogger;
import com.ccxt.whl.utils.PreferenceUtils;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;



public class PublishActivity extends Activity implements OnItemClickListener {

	private GridView gridview;
	private GridAdapter adapter;

	private LinearLayout activity_selectimg_send;
//	private ImageView back; 
	private LinearLayout back;
	private EditText comment_content;
	
	/*************输入限制************/
	private static final int MAX_COUNT = 140;
	private TextView mTextView = null;
	/*************输入限制end************/
	
	private String temp;
	private Button selectimg_bt_content_type, selectimg_bt_search;
	//private LinearLayout selectimg_relativeLayout_below;
	private LinearLayout pic_add;
	//private ScrollView activity_selectimg_scrollView;
	private HorizontalScrollView selectimg_horizontalScrollView;
	 
	private List<String> categoryList;

	private float dp;
	  
	public List<Bitmap> bmp = new ArrayList<Bitmap>();
	public List<String> drr = new ArrayList<String>();
	
	List<String> urList = new ArrayList<String>();
	 
	private ProgressDialog pd;//添加loading条
	//新增设备唯一id
	private static String uid = null;

	private static MyLogger Log = MyLogger.yLog();
	
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gushi_publish_activity);
		/****************配置必要参数****************/
		DeviceUuidFactory uuid = new DeviceUuidFactory(PublishActivity.this); 
		uid = uuid.getDeviceUuid().toString(); //获取设备唯一id
		pd = new ProgressDialog(PublishActivity.this);
		pd.setMessage("正在提交请求...");
		/*****************配置必要参数***************/
		
		Init();
		//实例化字数统计控件
		mTextView = (TextView) findViewById(R.id.count);
		setLeftCount();
	}



	public void Init() {
		dp = getResources().getDimension(R.dimen.dp);
		comment_content = (EditText) findViewById(R.id.comment_content);
		comment_content.setFocusable(true);
		comment_content.setFocusableInTouchMode(true); 
		//添加字数统计监听
		comment_content.addTextChangedListener(mTextWatcher);
		
		back = (LinearLayout) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(bmp.size()>0||comment_content.getText().length()>0){
					new AlertDialog.Builder(PublishActivity.this)  
					.setTitle("要放弃发布故事吗？")   
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
			            public void onClick(DialogInterface dialog, int which) {  
							PublishActivity.this.finish(); 
			                dialog.dismiss();  
			                //设置你的操作事项  
			            }  
			        })  
					.setNegativeButton("取消", null)  
					.show();
				}else{ 
					PublishActivity.this.finish(); 
				}
				
			}
		});

		selectimg_horizontalScrollView = (HorizontalScrollView) findViewById(R.id.selectimg_horizontalScrollView);
		gridview = (GridView) findViewById(R.id.noScrollgridview);
		gridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gridviewInit();
		comment_content.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				temp = s.toString();
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		activity_selectimg_send = (LinearLayout) findViewById(R.id.activity_selectimg_send);
		activity_selectimg_send.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				/*if (bmp.size() < 1) {
					Toast.makeText(getApplicationContext(), "至少需要一张图片",
							Toast.LENGTH_SHORT).show();
					return;
				}*/
				if (bmp.size() > 3) {
					Toast.makeText(getApplicationContext(), "每次发布最多三张图片",
							Toast.LENGTH_SHORT).show();
					return;
				}

				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(PublishActivity.this
								.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);

				String content = comment_content.getText().toString().trim();
				
				String content_str = null;//要发布的文字
				
				try {
					content_str = URLEncoder.encode(content,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (content.equals("")) {
					Toast.makeText(getApplicationContext(), "发布的内容不能为空",
							Toast.LENGTH_SHORT).show();
					return;
				}
				for (int i = 0; i < drr.size(); i++) {
					urList.add(drr.get(i));
				}
				
				//System.out.println(urList.toString());
				
				publish_gushi(urList, content_str);//发布故事事件
				  
				// 图片地址 urList；
			}

		
			private void publish_gushi(final List<String> urList, String content) {
				activity_selectimg_send.setEnabled(false); 
             	pd.show();
			 
             	//System.out.println("=================="+urList.size());
				RequestParams params = new RequestParams(); 
				for(int i=0;i<urList.size();i++){ 
					File file = new File(urList.get(i));
					try {
						params.put("pic"+i, file,"image/jpeg");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				params.add("content", content);
				params.put("user", DemoApplication.getInstance().getUser());
				params.put("picnum", urList.size());//发布图片数量
            	params.add("uid", uid);
            	params.add("jwd", PreferenceUtils.getInstance(PublishActivity.this).getSettingUserloc());
            	
            	HttpRestClient.post(Constant.GUSHI_PUBLISH, params, new BaseJsonHttpResponseHandler() {
            		 
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							String rawJsonResponse, Object response) {
						urList.clear();
						Log.d("publish_qes"+rawJsonResponse );
						pd.dismiss();
						activity_selectimg_send.setEnabled(true); 
						if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
							Toast.makeText(PublishActivity.this, "您的网络不稳定,请检查网络！", 0).show();
							return;
						}
						
						Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
		        		if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
		        			Toast.makeText(PublishActivity.this, "更新成功", 0).show();
		        			Log.d("message=="+lm.get("message").toString()); 
		        			
		        			comment_content.setText("");
		        			bmp.clear();
		        			adapter.notifyDataSetChanged();
		        			 
		        		}else{ 
		        			Toast.makeText(PublishActivity.this, lm.get("message").toString(), 0).show();
		        			Log.d("message=="+lm.get("message").toString()); 
		        		}
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, String rawJsonData,
							Object errorResponse) {
						pd.dismiss();
						Toast.makeText(PublishActivity.this, "您的网络不稳定，请稍后再试", 0).show();
						activity_selectimg_send.setEnabled(true); 
						urList.clear();
						// TODO Auto-generated method stub
						
					}
					 
					@Override
					protected Object parseResponse(String rawJsonData,
							boolean isFailure) throws Throwable {
						// TODO Auto-generated method stub
						
						return null;
					}
				});
            	
//				params.put("pic1", file,"image/jpeg");
//				params.put("pic2", file,"image/jpeg");
//				params.put("pic3", file,"image/jpeg");
				//params.put("user", DemoApplication.getInstance().getUser());
            	//params.put("param", "headurl");
            	//params.add("uid", uid);
//            	HttpRestClient.post(Constant.UPDATE_USER_URL, params, responseHandler);
//            	pd.show();
            	/*
				RequestParams params = new RequestParams();
	            final String contentType = RequestParams.APPLICATION_OCTET_STREAM;
	            params.put("fileOne", , contentType);
	            params.put("fileTwo", , contentType);
	            params.put("fileThree", , contentType);
	            params.put("fileFour", , contentType);
	            params.put("fileFive",, contentType);
	            //  client.post(this, URL, params, responseHandler);
	            
	             *
	             *
	             *
	             *
	             *
	             *@Override
					public void onSuccess(int statusCode, Header[] headers,
							String rawJsonResponse, Object response) {
						// TODO Auto-generated method stub
						Log.d("publish_qes",rawJsonResponse );
						pd.dismiss();
						activity_selectimg_send.setEnabled(true); 
						if(CommonUtils.isNullOrEmpty(rawJsonResponse)){
							Toast.makeText(PublishActivity.this, "您的网络不稳定,请检查网络！", 0).show();
							return;
						}
						
						Map<String, Object> lm = JsonToMapList.getMap(rawJsonResponse);
		        		if(lm.get("status").toString() != null && lm.get("status").toString().equals("yes")){
		        			Toast.makeText(PublishActivity.this, "更新成功", 0).show();
		        			Log.d("log","message=="+lm.get("message").toString()); 
		        		}else{ 
		        			Toast.makeText(PublishActivity.this, lm.get("message").toString(), 0).show();
		        			Log.d("log","message=="+lm.get("message").toString()); 
		        		}
						
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable throwable, String rawJsonData,
							Object errorResponse) {
						// TODO Auto-generated method stub
						pd.dismiss();
						Toast.makeText(PublishActivity.this, "您的网络不稳定，请稍后再试", 0).show();
						activity_selectimg_send.setEnabled(true); 
					}

					@Override
					protected Object parseResponse(String rawJsonData,
							boolean isFailure) throws Throwable {
						// TODO Auto-generated method stub
						System.out.println("================="+rawJsonData);
						pd.dismiss();
						Toast.makeText(PublishActivity.this, "您的网络不稳定，请稍后再试", 0).show();
						activity_selectimg_send.setEnabled(true); 
						return null;
					}
	             *
	             */
	             
			}
		});
		
/*******************************************暂时未知以下代码作用***************************************************/
//		selectimg_relativeLayout_below = (LinearLayout) findViewById(R.id.selectimg_relativeLayout_below);
		//activity_selectimg_scrollView = (ScrollView) findViewById(R.id.activity_selectimg_scrollView);
		//activity_selectimg_scrollView.setVerticalScrollBarEnabled(false);
		pic_add = (LinearLayout) findViewById(R.id.pic_add);
		final View decorView = getWindow().getDecorView();
		final WindowManager wm = this.getWindowManager();

		decorView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						@SuppressWarnings("deprecation")
						int displayheight = wm.getDefaultDisplay().getHeight();
						Rect rect = new Rect();
						decorView.getWindowVisibleDisplayFrame(rect);
						int dynamicHight = rect.bottom - rect.top;
						float ratio = (float) dynamicHight
								/ (float) displayheight;

						if (ratio > 0.2f && ratio < 0.6f) {
//							selectimg_relativeLayout_below
//									.setVisibility(View.VISIBLE);
//							activity_selectimg_scrollView.scrollBy(0,
//									activity_selectimg_scrollView.getHeight());
							pic_add.setVisibility(View.GONE);
						} else {
//							selectimg_relativeLayout_below
//									.setVisibility(View.GONE);
							pic_add.setVisibility(View.VISIBLE);
						}
					}
				});
/*****************************************暂时未知以上代码作用***************************************************/
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	if(bmp.size()>0||comment_content.getText().length()>0){
				new AlertDialog.Builder(PublishActivity.this)  
				.setTitle("要放弃发布故事吗？")   
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
		            public void onClick(DialogInterface dialog, int which) {  
						PublishActivity.this.finish(); 
		                dialog.dismiss();  
		                //设置你的操作事项  
		            }  
		        })  
				.setNegativeButton("取消", null)  
				.show();
			}else{ 
				PublishActivity.this.finish(); 
			}
        	return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

	public void gridviewInit() {
		adapter = new GridAdapter(this);
		adapter.setSelectedPosition(0);
		int size = 0;
		if (bmp.size() < 6) {
			size = bmp.size() + 1;
		} else {
			size = bmp.size();
		}
		LayoutParams params = gridview.getLayoutParams();
		final int width = size * (int) (dp * 9.4f);
		params.width = width;
		gridview.setLayoutParams(params);
		gridview.setColumnWidth((int) (dp * 9.4f));
		gridview.setStretchMode(GridView.NO_STRETCH);
		gridview.setNumColumns(size);
		gridview.setAdapter(adapter);
		gridview.setOnItemClickListener(this);

		selectimg_horizontalScrollView.getViewTreeObserver()
				.addOnPreDrawListener(// 绘制完毕
						new OnPreDrawListener() {
							public boolean onPreDraw() {
								selectimg_horizontalScrollView.scrollTo(width,
										0);
								selectimg_horizontalScrollView
										.getViewTreeObserver()
										.removeOnPreDrawListener(this);
								return false;
							}
						});
	}

	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	public class GridAdapter extends BaseAdapter {
		private LayoutInflater listContainer;
		private int selectedPosition = -1;
		private boolean shape;

		public boolean isShape() {
			return shape;
		}

		public void setShape(boolean shape) {
			this.shape = shape;
		}

		public class ViewHolder {
			public ImageView image;
			public Button bt;
		}

		public GridAdapter(Context context) {
			listContainer = LayoutInflater.from(context);
		}

		public int getCount() {
			if (bmp.size() < 6) {
				return bmp.size() + 1;
			} else {
				return bmp.size();
			}
		}

		public Object getItem(int arg0) {

			return null;
		}

		public long getItemId(int arg0) {

			return 0;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		public int getSelectedPosition() {
			return selectedPosition;
		}

		/**
		 * ListView Item设置
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			final int sign = position;
			// 自定义视图
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				// 获取list_item布局文件的视图

				convertView = listContainer.inflate(
						R.layout.gushi_item_published_grida, null);

				// 获取控件对象
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				holder.bt = (Button) convertView
						.findViewById(R.id.item_grida_bt);
				// 设置控件集到convertView
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (position == bmp.size()) {
				holder.image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.icon_addpic_unfocused));
				holder.bt.setVisibility(View.GONE);
				if (position == 6) {
					holder.image.setVisibility(View.GONE);
				}
			} else {
				holder.image.setImageBitmap(bmp.get(position));
				holder.bt.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						PhotoActivity.bitmap.remove(sign);
						bmp.get(sign).recycle();
						bmp.remove(sign);
						drr.remove(sign);

						gridviewInit();
					}
				});
			}

			return convertView;
		}
	}

	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View
					.inflate(mContext, R.layout.gushi_select_popupwindows, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			// ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
			// R.anim.push_bottom_in_2));

			setWidth(LayoutParams.FILL_PARENT);
			setHeight(LayoutParams.FILL_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(
							// 相册
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(i, RESULT_LOAD_IMAGE);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	private static final int TAKE_PICTURE = 0;
	private static final int RESULT_LOAD_IMAGE = 1;
	private static final int CUT_PHOTO_REQUEST_CODE = 2;
	private static final int SELECTIMG_SEARCH = 3;
	private String path = "";
	private Uri photoUri;

	public void photo() {
		try {
			Intent openCameraIntent = new Intent(
					MediaStore.ACTION_IMAGE_CAPTURE);

			String sdcardState = Environment.getExternalStorageState();
			String sdcardPathDir = android.os.Environment
					.getExternalStorageDirectory().getPath() + Constant.CACHE_DIR+"/photo/";
			//String sdcardPathDir = FileUtils.SDPATH1;
			File file = null;
			if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
				// 有sd卡，是否有myImage文件夹
				File fileDir = new File(sdcardPathDir);
				if (!fileDir.exists()) {
					fileDir.mkdirs();
				}
				// 是否有headImg文件
				file = new File(sdcardPathDir + System.currentTimeMillis()
						+ ".jpg");
			}
			if (file != null) {
				path = file.getPath();
				photoUri = Uri.fromFile(file);
				openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

				startActivityForResult(openCameraIntent, TAKE_PICTURE);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (drr.size() < 6 && resultCode == -1) {// 拍照
				startPhotoZoom(photoUri);
			}
			break;
		case RESULT_LOAD_IMAGE:
			if (drr.size() < 6 && resultCode == RESULT_OK && null != data) {// 相册返回
				Uri uri = data.getData();
				if (uri != null) {
					startPhotoZoom(uri);
				}
			}
			break;
		case CUT_PHOTO_REQUEST_CODE:
			if (resultCode == RESULT_OK && null != data) {// 裁剪返回
				Bitmap bitmap = Bimp.getLoacalBitmap(drr.get(drr.size() - 1));
				PhotoActivity.bitmap.add(bitmap);
				bitmap = Bimp.createFramedPhoto(480, 480, bitmap,
						(int) (dp * 1.6f));
				bmp.add(bitmap);
				gridviewInit();
			}
			break;
		case SELECTIMG_SEARCH:
			if (resultCode == RESULT_OK && null != data) {
				String text = "#" + data.getStringExtra("topic") + "#";
				text = comment_content.getText().toString() + text;
				comment_content.setText(text);

				comment_content.getViewTreeObserver().addOnPreDrawListener(
						new OnPreDrawListener() {
							public boolean onPreDraw() {
								comment_content.setEnabled(true);
								// 设置光标为末尾
								CharSequence cs = comment_content.getText();
								if (cs instanceof Spannable) {
									Spannable spanText = (Spannable) cs;
									Selection.setSelection(spanText,
											cs.length());
								}
								comment_content.getViewTreeObserver()
										.removeOnPreDrawListener(this);
								return false;
							}
						});

			}

			break;
		}
	}

	private void startPhotoZoom(Uri uri) {
		try {
			/****判断目录是否已存在****/
			String sdcardPathDir = android.os.Environment
					.getExternalStorageDirectory().getPath() + Constant.CACHE_DIR+"/photo/";
			//String sdcardPathDir = FileUtils.SDPATH1;
			File file = null; 
			// 有sd卡，是否有myImage文件夹
			File fileDir = new File(sdcardPathDir);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			/****判断目录是否已存在end****/
			
			// 获取系统时间 然后将裁剪后的图片保存至指定的文件夹
			SimpleDateFormat sDateFormat = new SimpleDateFormat(
					"yyyyMMddhhmmss");
			String address = sDateFormat.format(new java.util.Date());
			if (!FileUtils.isFileExist("")) {
				FileUtils.createSDDir("");

			}
			drr.add(FileUtils.SDPATH + address + ".jpg");
			
			//Uri imageUri = Uri.parse("file:///sdcard/formats/" + address
			//		+ ".jpg");
			//这里需要和FileUtils.SDPATH 一致，而且在之前要创建文件夹
			Uri imageUri = Uri.parse("file:///sdcard/"+Constant.CACHE_DIR+"/photo/thumb" + address
					+ ".jpg");
			System.out.println("uri===="+ FileUtils.SDPATH + address + ".jpg");
			final Intent intent = new Intent("com.android.camera.action.CROP");

			// 照片URL地址
			intent.setDataAndType(uri, "image/*");

			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", 480);
			intent.putExtra("outputY", 480);
			// 输出路径
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			// 输出格式
			intent.putExtra("outputFormat",
					Bitmap.CompressFormat.JPEG.toString());
			// 不启用人脸识别
			intent.putExtra("noFaceDetection", false);
			intent.putExtra("return-data", false);
			startActivityForResult(intent, CUT_PHOTO_REQUEST_CODE);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void onDestroy() {

		FileUtils.deleteDir(FileUtils.SDPATH);
		FileUtils.deleteDir(FileUtils.SDPATH1);
		// 清理图片缓存
		for (int i = 0; i < bmp.size(); i++) {
			bmp.get(i).recycle();
		}
		for (int i = 0; i < PhotoActivity.bitmap.size(); i++) {
			PhotoActivity.bitmap.get(i).recycle();
		}
		PhotoActivity.bitmap.clear();
		bmp.clear();
		drr.clear();
		super.onDestroy();
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(PublishActivity.this
						.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
		if (arg2 == bmp.size()) {
			String sdcardState = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
				new PopupWindows(PublishActivity.this, gridview);
			} else {
				Toast.makeText(getApplicationContext(), "sdcard已拔出，不能选择照片",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			Intent intent = new Intent(PublishActivity.this,
					PhotoActivity.class);

			intent.putExtra("ID", arg2);
			startActivity(intent);
		}
	}
/**********************************************editext输入限制方法******************************************/
	private TextWatcher mTextWatcher = new TextWatcher() {

		private int editStart;

		private int editEnd;

		public void afterTextChanged(Editable s) {
			editStart = comment_content.getSelectionStart();
			editEnd = comment_content.getSelectionEnd();

			// 先去掉监听器，否则会出现栈溢出
			comment_content.removeTextChangedListener(mTextWatcher);

			// 注意这里只能每次都对整个EditText的内容求长度，不能对删除的单个字符求长度
			// 因为是中英文混合，单个字符而言，calculateLength函数都会返回1
			while (calculateLength(s.toString()) > MAX_COUNT) { // 当输入字符个数超过限制的大小时，进行截断操作
				s.delete(editStart - 1, editEnd);
				editStart--;
				editEnd--;
			}
			comment_content.setText(s);
			comment_content.setSelection(editStart);

			// 恢复监听器
			comment_content.addTextChangedListener(mTextWatcher);

			setLeftCount();
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	};
	
	/**
	 * 计算分享内容的字数，一个汉字=两个英文字母，一个中文标点=两个英文标点 注意：该函数的不适用于对单个字符进行计算，因为单个字符四舍五入后都是1
	 * 
	 * @param c
	 * @return
	 */
	private long calculateLength(CharSequence c) {
		double len = 0;
		for (int i = 0; i < c.length(); i++) {
			int tmp = (int) c.charAt(i);
			if (tmp > 0 && tmp < 127) {
				len += 0.5;
			} else {
				len++;
			}
		}
		return Math.round(len);
	}

	/**
	 * 刷新剩余输入字数,最大值新浪微博是140个字，人人网是200个字
	 */
	private void setLeftCount() {
		mTextView.setText(String.valueOf((MAX_COUNT - getInputCount())));
	}

	/**
	 * 获取用户输入的分享内容字数
	 * 
	 * @return
	 */
	private long getInputCount() {
		return calculateLength(comment_content.getText().toString());
	}

}