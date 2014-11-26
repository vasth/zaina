package com.ccxt.whl.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonToMapList {
	  
	    /** 
	     * 将json 数组转换为Map 对象 
	     * @param jsonString 
	     * @return 
	     */  
	    public static Map<String, Object> getMap(String jsonString)  
	    {  
	      JSONObject jsonObject;  
	      try  
	      {  
	       jsonObject = new JSONObject(jsonString);   @SuppressWarnings("unchecked")  
	       Iterator<String> keyIter = jsonObject.keys();  
	       String key;  
	       Object value;  
	       Map<String, Object> valueMap = new HashMap<String, Object>();  
	       while (keyIter.hasNext())  
	       {  
	        key = (String) keyIter.next();  
	        value = jsonObject.get(key);  
	        valueMap.put(key, value);  
	       }  
	       return valueMap;  
	      }  
	      catch (JSONException e)  
	      {  
	       e.printStackTrace();  
	      }  
	      return null;  
	    }  
	  
	    /** 
	     * 把json 转换为 ArrayList 形式 
	     * @return 
	     */  
	    public static List<Map<String, Object>> getList(String jsonString)  
	    {  
	      List<Map<String, Object>> list = null;  
	      try  
	      {  
	       JSONArray jsonArray = new JSONArray(jsonString);  
	       JSONObject jsonObject;  
	        list = new ArrayList<Map<String, Object>>();  
	       for (int i = 0; i < jsonArray.length(); i++)  
	       {  
	        jsonObject = jsonArray.getJSONObject(i);   
	        list.add(getMap(jsonObject.toString()));  
	       }  
	      }  
	      catch (Exception e)  
	      {  
	       e.printStackTrace();  
	      }  
	      return list;  
	    }  
	    
	    /** 
	     * 把json 转换为 String[] 形式 
	     * @return 
	     */  
	    public static String[] getArr(String jsonString)  
	    {  
	    	String[] strings = null;  
	    	
	      try  
	      {  
	       JSONArray jsonArray = new JSONArray(jsonString); 
	       
	         strings = new String[jsonArray.length()];
	        
	       for (int i = 0; i < jsonArray.length(); i++)  
	       {  
	    	   strings[i] = jsonArray.getString(i);
	           
	       }  
	      }  
	      catch (Exception e)  
	      {  
	       e.printStackTrace();  
	      }  
	      return strings;  
	    }  
	  
	      
	      
	      
	    /** 
	     * @param args 
	     
	    public static void main(String[] args) {  
	        // TODO Auto-generated method stub  
	          
	          
	        String temp = "[{\"aa\":\"1\",\"bb\":\"2\"},{\"aa\":\"3\",\"bb\":\"4\"},{\"aa\":\"5\",\"bb\":\"6\"}]";  
	        List<Map<String, Object>> lm = Test.getList(temp);  
	        for(int i=0;i<lm.size();i++){  
	            Log.d("log",lm.get(i).get("aa"));  
	            Log.d("log",lm.get(i).get("bb"));  
	        }  
	    }  */  
	  
	   
}
