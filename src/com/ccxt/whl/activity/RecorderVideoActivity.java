package com.ccxt.whl.activity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnErrorListener;
import android.media.MediaRecorder.OnInfoListener;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.ccxt.whl.R;
import com.ccxt.whl.video.util.Utils;
import com.easemob.util.EMLog;
import com.easemob.util.PathUtil;

public class RecorderVideoActivity extends BaseActivity implements
		OnClickListener, Callback, OnErrorListener, OnInfoListener {

	private final static String CLASS_LABEL="RecordActivity";
	private PowerManager.WakeLock mWakeLock;
	private ImageView btnStart;// 开始录制按钮
	private ImageView btnStop;// 停止录制按钮
	private MediaRecorder mediarecorder;// 录制视频的类
	private SurfaceView surfaceview;// 显示视频的控件

	private SurfaceHolder surfaceHolder;
	String localPath = "";// 录制的视频路径
	private Camera mCamera;
	//预览的宽高
	private int previewWidth=480;
	private int previewHeight=480;
	
	Parameters cameraParameters=null;
	

	//分别为 默认摄像头（后置）、默认调用摄像头的分辨率、被选择的摄像头（前置或者后置）
	int defaultCameraId = -1, defaultScreenResolution = -1 , cameraSelection = 0;
	int defaultVideoFrameRate=-1;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
		// 选择支持半透明模式，在有surfaceview的activity中使用
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.recorder_activity);
		PowerManager pm=(PowerManager) getSystemService(Context.POWER_SERVICE);
		mWakeLock=pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
		mWakeLock.acquire();
		
		
		
		btnStart = (ImageView) findViewById(R.id.recorder_start);
		btnStop = (ImageView) findViewById(R.id.recorder_stop);
		btnStart.setOnClickListener(this);
		btnStop.setOnClickListener(this);
		surfaceview = (SurfaceView) this.findViewById(R.id.surfaceview);
		SurfaceHolder holder = surfaceview.getHolder();// 取得holder
		holder.addCallback(this); // holder加入回调接口
		// setType必须设置，要不出错.
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	public void back(View view) {

		if (mediarecorder != null) {
			// 停止录制
			mediarecorder.stop();
			// 释放资源
			mediarecorder.release();
			mediarecorder = null;
		}
		try {
			mCamera.reconnect();
		} catch (IOException e) {
			Toast.makeText(this, "reconect fail", 0).show();
		}
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mWakeLock == null) {
			// 获取唤醒锁,保持屏幕常亮
			PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
			mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
					CLASS_LABEL);
			mWakeLock.acquire();
		}
	}
	
	
	private void handleSurfaceChanged(){
		if(mCamera==null)
		{
			finish();
			return;
		}
		
		boolean hasSupportRate=false;
		List<Integer> supportedPreviewFrameRates = mCamera.getParameters().getSupportedPreviewFrameRates();
		if(supportedPreviewFrameRates!=null&&supportedPreviewFrameRates.size()>0)
		{
			Collections.sort(supportedPreviewFrameRates);
			for(int i=0;i<supportedPreviewFrameRates.size();i++)
			{
				int supportRate=supportedPreviewFrameRates.get(i);
				
				if(supportRate==10)
				{
					hasSupportRate=true;
				}
				
			}
			if(hasSupportRate)
			{
				defaultVideoFrameRate=10;
			}else{
				defaultVideoFrameRate=supportedPreviewFrameRates.get(0);
			}
			
			
			
		}
		
		Log.d("log supportedPreviewFrameRates"+supportedPreviewFrameRates);
		
		
		//获取摄像头的所有支持的分辨率
		List<Camera.Size> resolutionList=Utils.getResolutionList(mCamera);
		if(resolutionList!=null&&resolutionList.size()>0)
		{
			Collections.sort(resolutionList,new Utils.ResolutionComparator());
			Camera.Size previewSize=null;
			if(defaultScreenResolution==-1)
			{
				boolean hasSize=false;
				//如果摄像头支持640*480，那么强制设为640*480
				for(int i=0;i<resolutionList.size();i++)
				{
					Size size=resolutionList.get(i);
					if(size!=null&&size.width==640&&size.height==480)
					{
						previewSize=size;
						previewWidth=previewSize.width;
						previewHeight=previewSize.height;
						hasSize=true;
						break;
					}
				}
				//如果不支持设为中间的那个
				if(!hasSize)
				{
					int mediumResolution=resolutionList.size()/2;
					if(mediumResolution>=resolutionList.size())
						mediumResolution=resolutionList.size()-1;
					previewSize=resolutionList.get(mediumResolution);
					previewWidth=previewSize.width;
					previewHeight=previewSize.height;
						
				}
				 
			} 
			 
		}
		
		
		
		
		
	}
	
	 
	@Override
	protected void onPause() {
		super.onPause();
		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}
	}
	 
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.recorder_start:
			mCamera.unlock();
			mediarecorder = new MediaRecorder();// 创建mediarecorder对象
			mediarecorder.reset();
			mediarecorder.setCamera(mCamera);
			mediarecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
			// 设置录制视频源为Camera（相机）
			mediarecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
			// 设置录制完成后视频的封装格式THREE_GPP为3gp.MPEG_4为mp4
			mediarecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
			mediarecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
			// 设置录制的视频编码h263 h264
			mediarecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			// 设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错
			mediarecorder.setVideoSize(previewWidth, previewHeight);
//			// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错
			if (defaultVideoFrameRate != -1) {
				mediarecorder.setVideoFrameRate(defaultVideoFrameRate);
			}
			mediarecorder.setPreviewDisplay(surfaceHolder.getSurface());
			// 设置视频文件输出的路径
			localPath = PathUtil.getInstance().getVideoPath() + "/"
					+ System.currentTimeMillis() + ".mp4";
			mediarecorder.setOutputFile(localPath);
			mediarecorder.setOnErrorListener(this);
			mediarecorder.setOnInfoListener(this);
			try {
				// 准备录制
				mediarecorder.prepare();
				// 开始录制
				mediarecorder.start();
				Toast.makeText(this, "录像开始", Toast.LENGTH_SHORT).show();
				btnStart.setVisibility(View.INVISIBLE);
				btnStop.setVisibility(View.VISIBLE);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			break;
		case R.id.recorder_stop:

			if (mediarecorder != null) {
				// 停止录制
				mediarecorder.stop();
				// 释放资源
				mediarecorder.release();
				mediarecorder = null;
			}
			try {
				mCamera.reconnect();
			} catch (IOException e) {
				Toast.makeText(this, "reconect fail", 0).show();
			}
			btnStart.setVisibility(View.VISIBLE);
			btnStop.setVisibility(View.INVISIBLE);

			new AlertDialog.Builder(this)
					.setMessage("是否发送？")
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface arg0,
										int arg1) {
									arg0.dismiss();
									sendVideo(null);

								}
							}).setNegativeButton(R.string.cancel, null).show();

			break;

		default:
			break;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
		surfaceHolder = holder;

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// 将holder，这个holder为开始在oncreat里面取得的holder，将它赋给surfaceHolder
		surfaceHolder = holder;
		initpreview();
		handleSurfaceChanged();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// surfaceDestroyed的时候同时对象设置为null
		surfaceview = null;
		surfaceHolder = null;
		mediarecorder = null;
		releaseCamera();
	}

	protected void releaseCamera() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	}

	@SuppressLint("NewApi")
	protected void initpreview() {
		try {
			
			if(Build.VERSION.SDK_INT>Build.VERSION_CODES.FROYO)
			{
				int numberOfCameras=Camera.getNumberOfCameras();
				CameraInfo cameraInfo=new CameraInfo();
				for (int i = 0; i < numberOfCameras; i++) {
					Camera.getCameraInfo(i, cameraInfo);
					if(cameraInfo.facing==cameraSelection)
					{
						defaultCameraId=i;
					}
				}
				
				
			}
			if(mCamera!=null)
			{
				mCamera.stopPreview();
			}

			mCamera = Camera.open(CameraInfo.CAMERA_FACING_BACK);
			mCamera.setPreviewDisplay(surfaceHolder);
			setCameraDisplayOrientation(this, CameraInfo.CAMERA_FACING_BACK,
					mCamera);
			mCamera.startPreview();
		} catch (Exception e) {
			EMLog.e("###", e.getMessage());
			showFailDialog();
			return;
		}

	}

	@SuppressLint("NewApi")
	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	MediaScannerConnection msc = null;

	public void sendVideo(View view) {
		if (TextUtils.isEmpty(localPath)) {
			EMLog.e("Recorder", "recorder fail please try again!");
			return;
		}

		msc = new MediaScannerConnection(this,
				new MediaScannerConnectionClient() {

					@Override
					public void onScanCompleted(String path, Uri uri) {
						Log.d("log scanner completed");
						msc.disconnect();
						setResult(RESULT_OK, getIntent().putExtra("uri", uri));
						finish();
					}

					@Override
					public void onMediaScannerConnected() {
						msc.scanFile(localPath, "video/*");
					}
				});
		msc.connect();

	}

	@Override
	public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(MediaRecorder arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		releaseCamera();
		
		if(mWakeLock!=null)
		{
			mWakeLock.release();
			mWakeLock=null;
		}
		
	}

	@Override
	public void onBackPressed() {
		back(null);
	}
	
	
	
	
	
	private void showFailDialog(){
		new AlertDialog.Builder(this).setTitle("提示").setMessage("打开设备失败！").setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
				
			}
		}).setCancelable(false).show();
		
		
	}
	
	 
	

}
