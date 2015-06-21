<?
include 'conf.php';

$action = $_GET['action'];//c 请求昵称和头像链接（用于chatactivity） e为根据email查询用户头像和昵称   i 为请求用户详细资料 d为拉黑举报
$umd5 = $_GET['user'];//被请求的用户

$from = $_GET['f_user'];//from 为要请求用户的用户 
//$from = $_GET['from'];
$email = $_GET['email'];
$uid = $_GET['uid'];

//判断是否被黑名单
is_black($from,$uid);
 

if(!empty($action)){
	if($action=='c'){
		$res = $db->row_select_one("user","umd5 = '$umd5' "," name ,headurl ");
		if(!empty($res)){
			$nickname = $res['name'];
			$headurl = $res['headurl'];
			$res_json = array(
				'nickname' => $nickname,
				'headurl' => $headurl
			);
			json('yes','',$res_json);
		}else{
			json('no','暂无该用户',"");
		}
	}else if($action=='e'){
		$res = $db->row_select_one("user","email = '$email' "," name ,headurl,umd5 ");
		if(!empty($res)){
			$nickname = $res['name'];
			$headurl = $res['headurl'];
			$umd5 = $res['umd5'];
			$res_json = array(
				'nickname' => $nickname,
				'user' => $umd5,
				'headurl' => $headurl
			);
			json('yes','',$res_json);
		}else{
			json('no','暂无该用户',"");
		}
	}else if($action=='i'){
		if(empty($from)){
			json('no','缺少参数',"");
		}
		$res = $db->row_select_one("user","umd5 = '$umd5' "," name , headurl, umd5 , sex, age, city, zaina ,zhiye , qianming ,version");
		if(!empty($res)){
			$nickname = $res['name'];
			$headurl = $res['headurl'];
			$umd5 = $res['umd5'];
			$sex = $res['sex'];
			$age = $res['age'];
			$city = $res['city'];
			$zaina = "".$res['zaina'];
			$zhiye = "".$res['zhiye'];
			$qianming = "".$res['qianming'];
			if(empty($zaina)){
				$zaina = " ";
			}
			$res_json = array(
				'nickname' => $nickname,
				'user' => $umd5,
				'headurl' => $headurl,
				'sex' => $sex,
				'age' => $age,
				'city' => $city,
				'zaina' => $zaina,
				'zhiye' => $zhiye,
				'qianming' => $qianming
			);
			$mem = new Memcache; 
			$mem->connect("172.18.1.23", 12000); 
			$issend  = $mem->get($from.$umd5.'is_look_send');
			if(empty($issend )){
				$res_from = $db->row_select_one("user","umd5 = '$from' "," name , headurl , sex, age");
				$from_name = $res_from['name'];
				$from_sex = $res_from['sex']==1?'男':'女';
				$from_age =  $res_from['age'];
				$send_content = "昵称:".$from_name."\n性别:".$from_sex."\n年龄:".$from_age."岁"."\n刚刚访问了您的主页.\n发布您的故事让更多人了解你。";
				if($res['version']<2){
					send_txt($umd5,$send_content); 
				}else{
					$content['headurl'] = $res_from['headurl'];
					$content['username']= $from_name;
					$ext = array("attr"=>"card",
						"content" => $from_sex."  ".$from_age."岁  \n\n ta 刚刚访问了您的主页。发布您的故事让更多人了解你。",
						"user" =>$from);
					//$umd5 = 'ff9e537dbea1b731de24c85116a2fff8';
					send_card($umd5,$content,$ext);
				}
				$mem->set($from.$umd5.'is_look_send', "1", 0, 60000);
			}
			json('yes','',$res_json);
		}else{
			json('no','暂无该用户',"");
		}
	}else if($action=='d'){//拉黑举报
		$black_content = $_GET['black_content'];
		if(is_array($black_content)){
			$black_content = json_encode($black_content);
		}
			//$umd5 
			$insert = array(
				'umd5' => $umd5,
				'f_user' => $from  ,
				'f_deviceid' => $uid ,
				'isdel' => '0',
				'content' => $black_content
			);
			$send_content = '感谢您的举报,我们马上处理,举报结果处理后发送您,我们共建文明交友环境';
			$res = send_txt($from,$send_content); 
			print_r($res);
			$db->row_insert('black',$insert);
			$res = send_txt('ff9e537dbea1b731de24c85116a2fff8','有举报');
	}
}
?>