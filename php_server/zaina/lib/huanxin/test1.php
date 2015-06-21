<?error_reporting('E_ERROR');//错误等级提示

$filedir =  dirname(__FILE__); //当前文件的所在目录的路径
include $filedir."/../../conf.php";
return;
/******************************发送升级消息******************************/
    $page = 0;
	$start = $page*1000;
	//$start = 770;//770
	$pagenum = 5000;
   $res = $db->row_select('user'," version < '2.8' ", " $start,$pagenum ", "id,name, umd5,headurl");
	//$res = $db->row_select('user',"", " 1,11 ", "name, umd5");
	foreach($res as $item){
		$us[] = $item['umd5'];	  
	}
	print_r($us);
$res = $easemob->yy_hxSend('kefu',$us,"你好,由于服务器接口升级，如果您发现无法上传相册、无法获取在哪页面数据和无法查看用户信息请更新到最新的版本。最新安装包的下载地址是http://t.cn/R7mtgZV。如果可以正常访问请忽略该消息！相逢即是缘，您的使用是我们前进的不竭动力！");
return;

/******************************替换头像地址******************************/
    $page = 4;
	$start = $page*1000;
	//$start = 770;//770
	$pagenum = 1000;
$res = $db->row_select('user',"", " $start,$pagenum ", "id,name, umd5,headurl");
	//$res = $db->row_select('user',"", " 1,11 ", "name, umd5");
	foreach($res as $item){
		$us[] = $item['umd5'];
		$id =  $item['id'];
		 $item['headurl'];
		 $pos = strpos($item['headurl'], "180.153.40.10");
		 if($pos){
			 $update = array(
				"headurl" =>  str_replace("180.153.40.10","file.fujinde.com",$item['headurl'])
			 );
			 $db->row_update('user',$update,"id = $id "); 
			 
			 echo $id."<br>";
		 }
		 
		//$res = $easemob->getToken (); 
		//$db->row_insert('db_qunfa',$insert); 
	}
	
return;
/******************************测试添加用户******************************/
/* include('Easemob.class.php');
$option = array(
	 'client_id' => 'YXA6vpskYB7lEeSFFt0E5_hyiw',	
	 'client_secret' => 'YXA6ZJ8yU3uRdiXP9yGgbnlOhxXjEnM',
	 'org_name' =>  'ccxt',
	 'app_name' => 'sandbox',
);
$easemob = new Easemob($option); */
//$content['headurl'] = 'http://180.153.40.10:8081/7,0190453a213e';
//$content['username']='佳佳';
$user['username'] = 'test111';
$user['password'] = '123123';
$res = add_user($user);
print_r($res);
return;
/******************************测试向用户发送举报成功消息******************************/
$us = array('1c0412f4431d0b2bef90722abaec613f');
$res = $easemob->yy_hxSend('kefu',$us,
"您好，您举报的帐号已被禁用，感谢您对我们软件的支持，让我们共同创建文明交友环境");
//$res = $easemob->Send_card('kefu',$us,$content,"users",array("attr"=>"card","content" => "女 21岁 \n\n ta刚刚访问了您的主页。发布您的故事让更多人了解你。","user" =>"c7b3c6d3c6e7b6b14e55fbe615536849"));
print_r($res);

 return;
/******************************向用户发送祝福消息******************************/
$res = $db->row_select('user',"", "", "name, umd5");
foreach($res as $item){
	$us[] = $item['umd5']; 
}
//$us = array('ff9e537dbea1b731de24c85116a2fff8');
$res = $easemob->yy_hxSend('kefu',$us,
"好朋友我喊你尽情吃月饼了，第一口，祝祖国繁荣昌盛！第二口，祝我们幸福安康！第三口，不要掉渣砸到小强！第四口，吃完了吧，请君结账！");
print_r($res);
return;
/******************************测试发送访问消息******************************/
$us = array('c7b3c6d3c6e7b6b14e55fbe615536849');

$res = $easemob->Send_card('kefu',$us,'hi',"users",array("attr"=>"card","content" => "男 21岁","user" =>"c7b3c6d3c6e7b6b14e55fbe615536849"));
print_r($res);
 
return;

/******************************测试群发升级消息******************************/
$send_action = 1;//0是群发，1是单独发送
if($send_action == 0){
	$page = 74;
	$start = $page*10;
	$start = 770;//770
	$pagenum = 2;
	$res = $db->row_select('user',"", " $start,$pagenum ", "name, umd5");
	//$res = $db->row_select('user',"", " 1,11 ", "name, umd5");
	foreach($res as $item){
		$us[] = $item['umd5'];
		$insert = array(
			'name' =>  $item['name'],
			'umd5' => $item['umd5'],
			'issend' => 1
		);
		//$res = $easemob->getToken (); 
		$db->row_insert('db_qunfa',$insert); 
	}
}else{
	$us = array('ecc934314b6a69423d09f5915f12fa58');
}
$res = $easemob->yy_hxSend('kefu',$us,
"新版本发布啦,\n小客服诚邀您来内部测试尝鲜,\n新增了故事页面,\n每个人都有自己的故事,或悲伤、或欢乐。\n大声说出你的故事");
$res = $easemob->Send_file('kefu',$us,'hi');

echo json_encode($res );
//print_r();
?>