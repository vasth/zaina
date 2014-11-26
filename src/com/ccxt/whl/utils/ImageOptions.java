package com.ccxt.whl.utils;


import com.ccxt.whl.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class  ImageOptions{
	public static DisplayImageOptions getOptions() {
		DisplayImageOptions options;
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.mini_avatar_shadow)// 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.mini_avatar_shadow)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.mini_avatar_shadow)// 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)// 是否緩存都內存中
				.cacheOnDisc(true)// 是否緩存到sd卡上
				.displayer(new RoundedBitmapDisplayer(20)).build();
		return options;
	}
	
	public static DisplayImageOptions get_gushi_Options() {
		DisplayImageOptions options;
		options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)// 是否緩存都內存中
				.cacheOnDisc(true)// 是否緩存到sd卡上
				.build();
		return options;
	}
	
	public static DisplayImageOptions get_shenqing_Options() {
		DisplayImageOptions options;
		options = new DisplayImageOptions.Builder()
				.cacheInMemory(true)// 是否緩存都內存中
				.cacheOnDisc(true)// 是否緩存到sd卡上
				.build();
		return options;
	}
}

