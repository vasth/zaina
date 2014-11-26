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
package com.ccxt.whl.domain;

import com.easemob.chat.EMContact;
						  
public class User extends EMContact {
	private int unreadMsgCount;//未读消息数量
	private String header;//首字母
	
	private String headerurl;//头像 
	private String age;//年龄
	private String sex;//性别
	private String lasttime;//最后登录时间
	private String jiedao;//在哪街道
	private String city;//城市
	private String is_stranger;//是否是陌生人
	/*××××××××××××××*新增****************************/

	public String getIs_stranger() {
		return is_stranger;
	}

	public void setIs_stranger(String is_stranger) {
		this.is_stranger = is_stranger;
	}
	//public void setIs_stranger(String is_stranger2) {
		// TODO Auto-generated method stub 
	//}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getHeaderurl() {
		return headerurl;
	}

	public void setHeaderurl(String headerurl) {
		this.headerurl = headerurl;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getLasttime() {
		return lasttime;
	}

	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}

	public String getJiedao() {
		return jiedao;
	}

	public void setJiedao(String jiedao) {
		this.jiedao = jiedao;
	}
	/*××××××××××××××××××××××××××××××××××××××××××*/

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public int getUnreadMsgCount() {
		return unreadMsgCount;
	}

	public void setUnreadMsgCount(int unreadMsgCount) {
		this.unreadMsgCount = unreadMsgCount;
	}

	@Override
	public int hashCode() {
		return 17 * getUsername().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof User)) {
			return false;
		}
		return getUsername().equals(((User) o).getUsername());
	}

	@Override
	public String toString() {
		return nick == null ? username : nick;
	}
	
	public void clean() {
		this.unreadMsgCount = 0;//未读消息数量
		this.header= "";//首字母
		
		this.headerurl= "";//头像 
		this.age= "";//年龄
		this.sex= "";//性别
		this.lasttime= "";//最后登录时间
		this.jiedao = "";//在哪街道
		this.city = "";//城市
	}

	
}
