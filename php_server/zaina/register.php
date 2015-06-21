<?
include('conf.php'); 
$action = $_GET['action'];//action = u 为更新资料 r 为新注册
$umd5 = $_GET['user'];//用户唯一id md5加密   注册回执需要
$age = $_GET['age']; //年龄
$name = $_GET['nickname'];//昵称		更新和注册参数
$sex = $_GET['sex'];//性别			更新和注册参数
$tel_email = $_GET['tel_email'];	//注册参数
$pwd = $_GET['pwd'];				//注册参数
$jiedao = $_GET['jiedao'];
//$u_pic_headurl = $_POST['headurl'];
$u_pic_user = $_POST['user'];
$u_pic_param = $_POST['param'];
$u_param = $_GET['param'];

$zhiye = $_GET['zhiye'];
$qianming = $_GET['qianming'];

$uid = $_GET['uid'];//设备的唯一id
$os = $_GET['os'];//操作系统
$version = $_GET['v'];//程序的版本
//判断是否被黑名单
if(!empty($umd5)){
//is_black($umd5,$uid);//待思考--》更新资料可以，但是注册不可以
}
is_black($umd5,$uid);//待测试


if(!empty($action)){

	if($action=='u'){//更新资料
		if(!empty($umd5)||!empty($u_pic_user)){
				if($u_param=='nickname'){//昵称
					$update = array(
						'name' => $name
					);
					$res = $db->row_update('user',$update,"umd5 = '$umd5' ");
					if($res){
						$resJson = array(
							'headurl' => "",
							'nickname' => "".$name,
							'age' => "".$age,
							'sex' => "".$sex,
							'zhiye' => "",
							'qianming' => ""
						);
						json('yes',"$res",$resJson);
					}else{
						json('no',"更新失败，请重试","");
					} 
				}else if($u_param=='age'){//年龄
					$update = array(
						'age' => $age
					);
					$res = $db->row_update('user',$update,"umd5 = '$umd5' ");
					if($res){
						$resJson = array(
							'headurl' => "",
							'nickname' => "".$name,
							'age' => "".$age,
							'sex' => "".$sex,
							'zhiye' => "",
							'qianming' => ""
						);
						json('yes',"$res",$resJson);
					}else{
						json('no',"更新失败，请重试","");
					} 
				}else if($u_param=='sex'){//性别
					$update = array(
						'sex' => $sex
					);
					$res = $db->row_update('user',$update,"umd5 = '$umd5' ");
					if($res){
						$sexs = 
						$resJson = array(
							'headurl' => "",
							'nickname' => "".$name,
							'age' => "".$age,
							'sex' => ($sex=='1')?'男':'女',
							'zhiye' => "",
							'qianming' => ""
						);
						json('yes',"$res",$resJson);
					}else{
						json('no',"更新失败，请重试","");
					} 
				}else if($u_pic_param=='headurl'){
				
				    //$picname  = $_FILES['headurl']['name'];
					//$pictype  =$_FILES['headurl']['type'];
					if(is_uploaded_file($_FILES['headurl']['tmp_name'])){
					
						 $file=$_FILES['headurl'];
						 $name=$file['name'];
						 $type=$file['type']; 
						 $size=$file['size'];  
						 $tmpfile=$file['tmp_name'];  //临时存放文件
						 $error=$file['error'];  
						if($erro){
							json('no','上传出现错误'.$error,'');
						}
						if($size>100000) {//大于100kb
							json('no','图片太大'.$size,'');
						}

						if($type != 'image/jpeg' ){
							json('no','类型不符合标准'.$type,'');
						}
						 
						 $extension = '.jpg';
						 $filename="wt209_".date("Ymdhis").$extension;
						 $myfile="./temp/".$filename;
						 /*****上传至weed服务器*****/
						 $post_data['file'] ='@'.$tmpfile;  
						 /*方案一，服务器最省事，但是客户端有缓存，所以暂不用
						 /****判断是否是已存在的weed服务器图片**** /
						 $is_weed = $db->row_select_one("user","umd5 = '$u_pic_user'","headurl");
						 $pos = strpos($is_weed['headurl'], "180.153.40.10:8081");  
						 if($pos){
							
							 $uppic = update_pic($post_data,$is_weed['headurl']);
							 $uppicobj = json_decode($uppic);
							 if($uppicobj->size>0 ){
								$new_pic = $is_weed['headurl'];
								 $resJson = array(
									'headurl' => $new_pic.'?'.rand(0,9),
									'nickname' => "",
									'age' => "",
									'sex' => ''
									);
								 json('yes','上传成功',$resJson);
								 //$update = array(
								 //	"headurl" => $new_pic 
								 //);
								 //$db->row_update('user',$update,"umd5 = '$u_pic_user' ");
							 }else{
								/*if(move_uploaded_file($tmpfile,$myfile)){ 
									$resJson = array(
									'headurl' => "http://180.153.40.16:600/zaina/".$myfile,
									'nickname' => "",
									'age' => "",
									'sex' => ''
									);
									json('yes','上传成功',$resJson);
								}else{
									json('no','update faile','');
								}* /
								json('no','上传失败'.$uppic.$is_weed['headurl'],'');
							 }
						 }*/
						 
						 $fid = get_fid();
						 if(empty($fid)){
							//抽空吧这里改成存储到本服务器
							json('no','上传失败-图片服务器不可用',''); 
						 }
						 //×××××××××××××××××待做查看本机通过公网访问图片服务器的收到地址
						 $uppic = update_pic($post_data,'http://172.18.1.23:8081/'.$fid);
						 $uppicobj = json_decode($uppic);
						 if($uppicobj->size>0 ){
							$new_pic = 'http://file.fujinde.com:8081/'.$fid;
							 $resJson = array(
								'headurl' => $new_pic,
								'nickname' => "",
								'age' => "",
								'sex' => '',
								'zhiye' => "",
								'qianming' => ""
								);
							
							 /****方案二，图片上传后吧原来的删掉***/
							 $is_weed = $db->row_select_one("user","umd5 = '$u_pic_user'","headurl");
							 $pos = strpos($is_weed['headurl'], "file.fujinde.com:8081");  
							 if($pos){
								//print_r(delete_pic($is_weed['headurl']));
								$old_temp = $is_weed['headurl'];
							 }
							 $update = array(
								"headurl" => $new_pic 
							 );
							 $db->row_update('user',$update,"umd5 = '$u_pic_user' ");
							 //需要改成内部服务器地址
							 delete_pic(str_replace('file.fujinde.com','172.18.1.23',$old_temp));
							 //delete_pic('http://172.18.1.23:8081/4,79688abbdd');
							  json('yes','上传成功',$resJson);
						 }else{
							json('no','上传失败','');
						}
						// $uppicobj->name
						// $uppicobj->size
						// {"name":"xinput1_3.dll","size":81768}
						 /*****上传至weed服务器end*****/
						 
						 /***存储到本服务器start*** /
						if(move_uploaded_file($tmpfile,$myfile)){ 
							$resJson = array(
							'headurl' => "http://180.153.40.16:600/zaina/".$myfile,
							'nickname' => "",
							'age' => "",
							'sex' => ''
							);
							json('yes','上传成功',$resJson);
						}else{
							json('no','update faile','');
						}
						/***存储到本服务器end***/
					}else{
						json('no','not is_uploaded_file','');
				    }
				
				}else if($u_param=='zhiye'){//职业
					$update = array(
						'zhiye' => $zhiye
					);
					$res = $db->row_update('user',$update,"umd5 = '$umd5' ");
					if($res){
						$resJson = array(
							'headurl' => "",
							'nickname' => "".$name,
							'age' => "".$age,
							'sex' => "".$sex,
							'zhiye' => $zhiye,
							'qianming' => ""
						);
						json('yes',"$res",$resJson);
					}else{
						json('no',"更新失败，请重试","");
					} 
							 
				}else if($u_param=='qianming'){//签名
					$update = array(
						'qianming' => $qianming
					);
					$res = $db->row_update('user',$update,"umd5 = '$umd5' ");
					if($res){
						$resJson = array(
							'headurl' => "",
							'nickname' => "".$name,
							'age' => "".$age,
							'sex' => "".$sex,
							'zhiye' => "".$zhiye,
							'qianming' => "".$qianming
						);
						json('yes',"$res",$resJson);
					}else{
						json('no',"更新失败，请重试","");
					} 
				}else{
					json('no','缺少参数','');
				}
		}else{
			json('no','缺少参数user','');
		}		
	}else if($action == 'r'){//注册用户
		if(is_email($tel_email)){
			$count = $db->row_count("user","email = '$tel_email' "); //邮箱登录
		}else{
			json('no','邮箱不正确','');
			$count = $db->row_count("user","tel = '$tel_email' "); //手机号登录
		}
		//ios的注册
		if($os=='ios'){
			$uid = sc_uuid();
			
			$ipJson = get("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=".$_SERVER["REMOTE_ADDR"]);
			$ipinfo  = (array)json_decode($ipJson);
			$city  = $ipinfo['city'];
			$province = $ipinfo['province'];
			$country = $ipinfo['country'];
			if(empty($city)){
				if(empty($province)){
					$city = $country;
				}else{
					$city = $province;
				} 
			}
			
			$hx_user['username'] = md5($uid);
			$hx_user['password'] = $pwd;
			$hx_res = add_user($hx_user);
			if(empty($hx_res['error']) && $hx_res['entities'][0]['username']==md5($uid)){
				$insert = array(
					'email' => $tel_email,
					'sex' => $sex,
					'name' => $name,
					'pwd' => $pwd,
					'city'=> $city,
					'uid' => $uid,
					'umd5'=> md5($uid)
				);
				
				$db->row_insert('user',$insert);
				$resjson = array(
					"user" =>md5($uid),
					'pwd' => $pwd
				);
				json('yes','注册成功',$resjson);
			}else{
				json('no','注册失败','');
			}
					
		}
		
		if($version>2.2){
			if($count>0){
				json('no','重复注册','');
			}
			$uid = sc_uuid();
			
			$ipJson = get("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=".$_SERVER["REMOTE_ADDR"]);
			$ipinfo  = (array)json_decode($ipJson);
			$city  = $ipinfo['city'];
			$province = $ipinfo['province'];
			$country = $ipinfo['country'];
			if(empty($city)){
				if(empty($province)){
					$city = $country;
				}else{
					$city = $province;
				} 
			}
			
			$hx_user['username'] = md5($uid);
			$hx_user['password'] = $pwd;
			$hx_res = add_user($hx_user);
			if(empty($hx_res['error']) && $hx_res['entities'][0]['username']==md5($uid)){
				$insert = array(
					'email' => $tel_email,
					'sex' => $sex,
					'name' => $name,
					'pwd' => $pwd,
					'city'=> $city,
					'uid' => $uid,
					'umd5'=> md5($uid)
				);
				
				$db->row_insert('user',$insert);
				$resjson = array(
					"user" =>md5($uid),
					'pwd' => $pwd
				);
				json('yes','注册成功',$resjson);
			}else{
				json('no','注册失败','');
			}
		
		
		}
		//android 2.2版本及以下的注册方式
		if($count>0){
			$resuser_one = $db->row_select_one("user","email = '$tel_email' ");
			$re_zaina = $resuser_one['zaina'];
			$uid = $resuser_one['uid'];
			$pwd = $resuser_one['pwd'];
			if(!empty($re_zaina)){
				json('no','重复注册','');
			}else{
				$resjson = array(
					"user" => $umd5,
					'pwd' => $pwd
				); 
				json('yes','注册成功',$resjson);
			}			
		}else{
			$uid = sc_uuid();
			/*
			$ipJson = get('http://ip.taobao.com/service/getIpInfo.php?ip='.$_SERVER["REMOTE_ADDR"]);  
			$ipinfo  = (array)json_decode($ipJson);
			$city  = $ipinfo['data']->city;
			*/
			$ipJson = get("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=".$_SERVER["REMOTE_ADDR"]);
			$ipinfo  = (array)json_decode($ipJson);
			$city  = $ipinfo['city'];
			$province = $ipinfo['province'];
			$country = $ipinfo['country'];
			if(empty($city)){
				if(empty($province)){
					$city = $country;
				}else{
					$city = $province;
				} 
			}
			
			
			$insert = array(
				'email' => $tel_email,
				'sex' => $sex,
				'name' => $name,
				'pwd' => $pwd,
				'city'=> $city,
				'uid' => $uid,
				'umd5'=> md5($uid)
			);
			
			$db->row_insert('user',$insert);
			$resjson = array(
				"user" =>md5($uid),
				'pwd' => $pwd
			);
			set_usernick(md5($uid),"".$name);
			json('yes','注册成功',$resjson);
		}
		 
	}else if($action == 'h'){//注册客户端回执
		if(!empty($umd5)){
			if(empty($jiedao)){
				$ipJson = get('http://ip.taobao.com/service/getIpInfo.php?ip='.$_SERVER["REMOTE_ADDR"]); 
				$ipinfo  = (array)json_decode($ipJson);
				$city  = $ipinfo['data']->city;
				$jiedao = "我在".$city;
			}
			
			$update = array(
				'zaina' => $jiedao
			);
			
			$res = $db->row_update('user',$update,"umd5 = '$umd5' ");
			if($res){
				//send_update($umd5);//发送内测消息
				$send_content = "欢迎来到这里,我是小客服,填写真实资料和头像会获得ta的好感。\n聊天时请保持社交礼仪,如有用户恶意骚扰,请立即将其拉黑举报给我们";
				send_txt($umd5,$send_content); 
				json('yes','注册成功','');
			}else{
				json('no','请重试','');
			}
		}
	}	
	
}else{
	json('no','缺少参数action','');
}

 /*
function post($curl_url,$post_data){
		//要请求的内容
		//$post_data['user']        =    "root";
		//$post_data['password']    =     "1988725"; 
		//$post_data['file']        =    '@C:\Documents and Settings\chenzhi\My Documents\My Pictures\1286606098_38.jpg'; 
		///$post_data['file']    =     '@'.$_FILES['image']['tmp_name']; 
		$ch        =    curl_init();
		//$curl_url    =    "http://172.16.27.51/server.php";
		curl_setopt($ch,CURLOPT_URL,$curl_url);
		curl_setopt($ch, CURLOPT_POST, 1);
		//curl_setopt($ch, CURLOPT_HTTPHEADER, $access_token);//设置HTTP头
		curl_setopt($ch, CURLOPT_POSTFIELDS, $post_data); 
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);//不直接输出，返回到变量
		//curl_setopt($ch, CURLOPT_QUOTE, 1);
		//curl_setopt($ch, CURLOPT_HTTP200ALIASES, 1);
		///curl_setopt($ch, CURLOPT_POSTQUOTE, 1);
		 
		$curl_result = curl_exec($ch);
		return $curl_result;  
}
*/
?>