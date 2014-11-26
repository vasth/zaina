package com.ccxt.whl.utils;

import com.ccxt.whl.Constant;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpRestClient {
		private static MyLogger Log = MyLogger.yLog();

		  private static AsyncHttpClient client = new AsyncHttpClient();

		  public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
			  Log.d("请求的params"+params);
			  client.get(getAbsoluteUrl(url), params, responseHandler);
		  }

		  public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
			  Log.d("请求的params"+params);
			  client.post(getAbsoluteUrl(url), params, responseHandler);
		  }

		  private static String getAbsoluteUrl(String relativeUrl) {
			  Log.d("请求的url"+Constant.BASE_URL + relativeUrl); 
	 	      return  Constant.BASE_URL + relativeUrl;//基本的url
		  }
		 
}
