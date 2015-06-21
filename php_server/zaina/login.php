<? 
include('conf.php'); 

$action = $_GET['action'];
//普通登录参数
//$tel = $_GET['tel'];
$pwd = $_GET['pwd'];
//$email = $_GET['email'];
$tel_email = $_GET['tel_email'];
//社会化登录参数
$name  =  $_GET['name'];
$sex  =  $_GET['sex'];
$birthday  =  $_GET['birthday'];
$headurl  =  $_GET['headurl'];
//$age  =  $_GET['age'];
$province  =  $_GET['province'];
$city  =  $_GET['city'];
$meida_type  =  $_GET['meida_type'];
$meida_uid  =  $_GET['meida_uid'];
$access_token  =  $_GET['access_token'];

$uid = $_GET['uid'];//获取设备唯一id
//用户版本号，加故事版本后才有（1.5）
$v = $_GET['v'];
$os = $_GET['os'];
$mem = new Memcache; 
$mem->connect("172.18.1.23", 12000); 
$mem->set($tel_email.$meida_uid.'uid', $uid, 0, 60000);
QMC::input("login_uid", $tel_email.$meida_uid.'#'.$uid);//写入队列

if(!empty($action)){
	if($action=='p'){//普通登录 
		//echo $tel_email."|".$pwd;
		//判断是否为email
		if(is_email($tel_email)){ 
			$user_login_info = $db->row_select_one("user","email = '$tel_email' AND pwd = '$pwd' "); //邮箱登录 
		}else{
			$user_login_info = $db->row_select_one("user","tel = '$tel_email' AND pwd = '$pwd' "); //手机号登录
		}
		
		if(!empty($user_login_info)){
				$jsonres  = array(
					'headurl' => "".$user_login_info['headurl'],
					'name' => "".$user_login_info['name'],
					'sex' => $user_login_info['sex']==1?'男':'女',  
					'age' => "".$user_login_info['age'],
					'province' => "".$user_login_info['province'],//用户所在省
					'city' => "".$user_login_info['city'],//用户城市 
					'umd5' => md5($user_login_info['uid']),
					'pwd' => "".$user_login_info['pwd'],
					'zaina' => "".$user_login_info['zaina'],
					'zhiye' => "".$user_login_info['zhiye'],
					'qianming' => "".$user_login_info['qianming']
				);
				
			json("yes","登录成功",$jsonres);
		}else{
			json("no","登录失败，用户名或密码错误","");
		}
	}else{//社会化登录
		$baiduuserinfoJson = get('https://openapi.baidu.com/social/api/2.0/user/info?access_token='.$access_token); 
		$baiduuserinfo  = (array)json_decode($baiduuserinfoJson);
		 //print_r($baiduuserinfo);
		//exit;
		$error_code = $baiduuserinfo['error_code'];
		if(empty($error_code)){
			$name  =  $baiduuserinfo['username'];
			$sex  =  $baiduuserinfo['sex'];
			$birthday  =  $baiduuserinfo['birthday'];
			$headurl  =  $baiduuserinfo['headurl']; 
			$province  =  $baiduuserinfo['province'];
			$city  =  $baiduuserinfo['city'];
			
			if(empty($city)){
				$ipJson = get('http://ip.taobao.com/service/getIpInfo.php?ip='.$_SERVER["REMOTE_ADDR"]); 
				$ipinfo  = (array)json_decode($ipJson);
				$city  = $ipinfo['data']->city;
			}
			
			$media_type  =  $baiduuserinfo['media_type'];
			$media_uid  =  $baiduuserinfo['media_uid'];
			//$access_token  =  $baiduuserinfo['access_token'];
			
			
			$row_user = $db->row_select_one("user","media_type = '$media_type' AND media_uid = '$media_uid' "); //查看该用户是否存在
			//print_r($row_user); 
			if(!empty($row_user)){//该社会化帐号已存在
				 $userDetails = get_userDetails($row_user['umd5']);
				 //判断用户在环信服务器是否存在
				 if(empty($userDetails['error'])){
					 $jsonres  = array(
							'headurl' => "".$row_user['headurl'],
							'name' => "".$row_user['name'],
							'sex' => $row_user['sex']==1?'男':'女',  
							'age' => "".$row_user['age'],
							'province' => "".$row_user['province'],//用户所在省
							'city' => "".$row_user['city'],//用户城市 
							'umd5' => md5($row_user['uid']),
							'pwd' => "".$row_user['pwd'],  
							'is_res' => 'yes',
							'zhiye' => "".$row_user['zhiye'],
							'qianming' => "".$row_user['qianming']
						);
						json("yes","登录成功",$jsonres);
						/*****
						更新access_token（待做）
						*/
				 }else{
						 $jsonres  = array(
								'headurl' => "".$row_user['headurl'],
								'name' => "".$row_user['name'],
								'sex' => $row_user['sex']==1?'男':'女',  
								'age' => "".$row_user['age'],
								'province' => "".$row_user['province'],//用户所在省
								'city' => "".$row_user['city'],//用户城市 
								'umd5' => md5($row_user['uid']),
								'pwd' => $row_user['pwd'],
								'is_res' => 'no',
								'zhiye' => "".$row_user['zhiye'],
								'qianming' => "".$row_user['qianming']
							);
							json("yes","登录成功",$jsonres);
				 }
				/* if(empty($row_user['zaina'])){//判断是否接收登录回执
					
				}else{
					
				} */
				
			}else{//该社会化帐号不存在
				$age = birthday($birthday);
				$uid = sc_uuid();
				
				//ios社会化登录
				if($os =='ios'){
					
					$hx_user['username'] = md5($uid);
					$hx_user['password'] = md5($media_type.'inipwd'.$media_uid);
					$hx_res = add_user($hx_user);
					if(empty($hx_res['error']) && $hx_res['entities'][0]['username']==md5($uid)){
						$insert = array(
							"media_type" => $media_type,//用户社会化类型
							"media_uid" => $media_uid,//用户社会化id
							"access_token" => $access_token,//用户社会化token
							"province" => $province,//用户所在省
							"city" => $city,//用户城市
							"birthday" => $birthday,//用户生日
							"sex" => $sex,	//用户性别
							"age" => $age,	//用户年龄
							"uid" => $uid,	//用户唯一id
							"umd5" => md5($uid), //用户唯一hash
							'headurl' => $headurl,
							"name" => $name, //用户名称
							'zhiye' => "",
							'qianming' => "",
							"pwd" => md5($media_type.'inipwd'.$media_uid)
						);
						$jsonres  = array(
							'headurl' => "".$headurl,
							'name' => "".$name,
							'sex' => $sex==1?'男':'女',  
							'age' => "".$age,
							'province' => "".$province,//用户所在省
							'city' => "".$city,//用户城市
							'umd5' => md5($uid),
							"pwd" => md5($media_type.'inipwd'.$media_uid),
							'zhiye' => "",
							'qianming' => "",
							'is_res' => 'yes'
						); 
						
						$db->row_insert('user',$insert);
						//send_update(md5($uid));//发送内测消息
						$umd5 = md5($uid);
						$send_content = "欢迎来到这里,我是小客服,填写真实资料和头像会获得ta的好感。\n聊天时请保持社交礼仪,如有用户恶意骚扰,请立即将其拉黑举报给我们";
						send_txt($umd5,$send_content); 
						json("yes","登录成功",$jsonres);
					
					}else{
						json("no","注册服务器失败",$jsonres);
					}
					
				
				}
				//json("no","登录失败，用户名或密码错误","");
				/********************通过服务器注册*************************/
				$hx_user['username'] = md5($uid);
					$hx_user['password'] = md5($media_type.'inipwd'.$media_uid);
					$hx_res = add_user($hx_user);
					if(empty($hx_res['error']) && $hx_res['entities'][0]['username']==md5($uid)){
						$insert = array(
							"media_type" => $media_type,//用户社会化类型
							"media_uid" => $media_uid,//用户社会化id
							"access_token" => $access_token,//用户社会化token
							"province" => $province,//用户所在省
							"city" => $city,//用户城市
							"birthday" => $birthday,//用户生日
							"sex" => $sex,	//用户性别
							"age" => $age,	//用户年龄
							"uid" => $uid,	//用户唯一id
							"umd5" => md5($uid), //用户唯一hash
							'headurl' => $headurl,
							"name" => $name, //用户名称
							'zhiye' => "",
							'qianming' => "",
							"pwd" => md5($media_type.'inipwd'.$media_uid)
						);
						$jsonres  = array(
							'headurl' => "".$headurl,
							'name' => "".$name,
							'sex' => $sex==1?'男':'女',  
							'age' => "".$age,
							'province' => "".$province,//用户所在省
							'city' => "".$city,//用户城市
							'umd5' => md5($uid),
							"pwd" => md5($media_type.'inipwd'.$media_uid),
							'zhiye' => "",
							'qianming' => "",
							'is_res' => 'yes'
						); 
						
						$db->row_insert('user',$insert);
						//send_update(md5($uid));//发送内测消息
						$umd5 = md5($uid);
						$send_content = "欢迎来到这里,我是小客服,填写真实资料和头像会获得ta的好感。\n聊天时请保持社交礼仪,如有用户恶意骚扰,请立即将其拉黑举报给我们";
						send_txt($umd5,$send_content); 
						json("yes","登录成功",$jsonres);
					
					}else{
						json("no","注册服务器失败",$jsonres);
					}
				/********************************************* /
				
				$insert = array(
					"media_type" => $media_type,//用户社会化类型
					"media_uid" => $media_uid,//用户社会化id
					"access_token" => $access_token,//用户社会化token
					"province" => $province,//用户所在省
					"city" => $city,//用户城市
					"birthday" => $birthday,//用户生日
					"sex" => $sex,	//用户性别
					"age" => $age,	//用户年龄
					"uid" => $uid,	//用户唯一id
					"umd5" => md5($uid), //用户唯一hash
					'headurl' => $headurl,
					"name" => $name, //用户名称
					'zhiye' => "",
					'qianming' => "",
					"pwd" => md5($media_type.'inipwd'.$media_uid)
				);
				$jsonres  = array(
					'headurl' => "".$headurl,
					'name' => "".$name,
					'sex' => $sex==1?'男':'女',  
					'age' => "".$age,
					'province' => "".$province,//用户所在省
					'city' => "".$city,//用户城市
					'umd5' => md5($uid),
					"pwd" => md5($media_type.'inipwd'.$media_uid),
					'zhiye' => "",
					'qianming' => "",
					'is_res' => 'no'
				); 
				
				$db->row_insert('user',$insert);
				//send_update(md5($uid));//发送内测消息
				$umd5 = md5($uid);
				$send_content = "欢迎来到这里,我是小客服,填写真实资料和头像会获得ta的好感。\n聊天时请保持社交礼仪,如有用户恶意骚扰,请立即将其拉黑举报给我们";
				send_txt($umd5,$send_content); 
				//set_usernick($umd5,"".$name);
				json("yes","登录成功",$jsonres);
				/*********************************************/
			}
			
			
		//exit;
		}else{
			json("no","第三方token失效","");
		}
	}
}else{
	json("no","缺少必要参数","");
}

// birthday('1987-07-11');
 function birthday($mydate){
	if(empty($mydate)){
		$age = "21";
		return $age;
	}
    $birth=$mydate;
    list($by,$bm,$bd)=explode('-',$birth);
    $cm=date('n');
    $cd=date('j');
    $age=date('Y')-$by-1;
    if ($cm>$bm || $cm==$bm && $cd>$bd) $age++;
    return $age;
//echo "生日:$birth\n年龄:$age\n";
} 
 
?>