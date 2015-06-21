<?		
		/**********************调试用***********************/
		//require_once('debug/FirePHP.class.php');
		//ini_set('display_errors', 1);
		//error_reporting('E_ERROR');//错误等级提示
		error_reporting(E_ALL);
		//echo date('Y-m-d h:i:s');
		 
		set_error_handler(my_error, E_ERROR);

		function my_error($errno , $errstr)
		{
		   echo "<font color = 'red' >错误编号 $errno</font> 错误信息 $errstr";
		   exit();
		}
		/**********************调试用***********************/
		 $basedir =  dirname(__FILE__); //当前文件的所在目录的路径
	
		include('db.class.php');
	    include_once($basedir.'/lib/duilie.php'); //暂时是绝对路径，如果不是在其他文件中调用会出错
		//include '/lib/duilie.php';
			 
			 
	    $db_config["hostname"]        = "localhost";        //服务器地址 (请搜索localhost和127.0.0.1的区别)
        $db_config["username"]        = "*****";                //数据库用户名
        $db_config["password"]        = "*****";                //数据库密码
        $db_config["database"]        = "***";                //数据库名称
        $db_config["charset"]         = "utf-8";

        
        $db  = new db();
        $db->connect($db_config);
		
		
	//统一json生成
	function  json($status,$token,$array){
		$jsonf = array(  
			   "status" => $status,
			   "message" => $token,
			   "result" => $array
				  );
		echo json_encode($jsonf); 
		exit;
	 }
	 
	 //判断是否为邮箱
	 function is_email($email){
			$email = SBC_DBC($email, 1);
			return preg_match("/^[\w\-\.]+@[\w\-\.]+(\.\w+)+$/", $email);
			/* $pattern = "/^([0-9A-Za-z\\-_\\.]+)@([0-9a-z]+\\.[a-z]{2,3}(\\.[a-z]{2})?)$/i";
				if ( preg_match( $pattern, $email ) )
				{
					return true;
				}
				else
				{
					return false;
				}
				/*
			 if (ereg("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(\.[a-zA-Z0-9_-])+",$email)){
					return true;
			 } else{
					return false;
			 } */
	 }
	 //判断是否为url
	 function is_url($str){
		return preg_match("/^http://[A-Za-z0-9]+.[A-Za-z0-9]+[/=?%-&_~`@[]':+!]*([^<>''])*$/", $str);
	 }
	 //判断是否为手机号
	 function is_mobile($str){
		return preg_match("/^(((d{3}))|(d{3}-))?13d{9}$/", $str);
	 }
	 
	 function sc_uuid(){
		return time().rand(0,9).rand(0,9);
	 }
	 
	 function get($url){
		$ch = curl_init();
		curl_setopt($ch, CURLOPT_URL, $url);
		curl_setopt($ch, CURLOPT_TIMEOUT, 60);
		curl_setopt($ch, CURLOPT_USERAGENT, _USERAGENT_);
		curl_setopt($ch, CURLOPT_REFERER,_REFERER_);
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);
		$r = curl_exec($ch);
		curl_close($ch);
		return $r;
	 }
	 
	 function post($url,$data){
		//echo $url;
		//print_r($data);
		$ch = curl_init ();
		// print_r($ch);
		curl_setopt($ch, CURLOPT_URL, $url);
		curl_setopt($ch, CURLOPT_TIMEOUT, 60);
		curl_setopt($ch, CURLOPT_POST, 1 );
		curl_setopt($ch, CURLOPT_HEADER, 0 );
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1 );
		curl_setopt($ch, CURLOPT_POSTFIELDS, $data );
		$r= curl_exec($ch);
		curl_close($ch);
		return $r;
	 }
	 
	  /*****custom 请求*****/
	  function custom($url,$custom){
		//echo $url;
		//print_r($data);
		$ch = curl_init ();
		// print_r($ch);
		curl_setopt($ch, CURLOPT_URL, $url);
		curl_setopt($ch, CURLOPT_TIMEOUT, 60); 
		curl_setopt($ch, CURLOPT_AUTOREFERER, 1 );
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1 );
		curl_setopt($ch, CURLOPT_FOLLOWLOCATION, 1 ); 
		curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $custom );
		//curl_setopt($ch, CURLOPT_POSTFIELDS, $data );
		$r= curl_exec($ch);
		curl_close($ch);
		return $r;
	 }
	 
	 /**获取文件编号*/
	 function get_fid(){
		$assign = get("http://file.fujinde.com:9333/dir/assign");
		$assignobj = json_decode($assign);
		$fid = $assignobj->fid;
		return $fid;
	 }
	 /****上传文件***/
	  function update_pic($postdata,$pic_url){
		
		//$picurl = "http://180.153.40.10:8081/".$fid;
		return post($pic_url,$postdata);
	 }
	 /****删除文件***/
	  function delete_pic($pic_url){
		
		//$picurl = "http://180.153.40.10:8081/".$fid;
		return custom($pic_url,'DELETE');
	 }
	 
	 /****增加用户**
	  * @param $users['username'] 用户名        	
	 * @param $options['password'] 密码
	 *        	批量注册传二维数组
	 */
	  function add_user($users){
	  include_once('/home/wwwroot/default/zaina/lib/huanxin/Easemob.class.php');
			//include_once($basedir.'/lib/huanxin/Easemob.class.php');
			$option = array(
				 'client_id' => 'YX*****************************yiw',	
				 'client_secret' => '*****************************',
				 'org_name' =>  '**',
				 'app_name' => '*****',
			);
			$easemob = new Easemob($option);
			//$res = $easemob->yy_hxSend('kefu',array("$umd5"), $content);
			//$res = $easemob->accreditRegister($users);
			$res = $easemob->openRegister($users);
			return $res ;
	  }
	  
	  /****获取用户详情***/
	  function get_userDetails($umd5){
			include_once('./lib/huanxin/Easemob.class.php');
			$option = array(
				 'client_id' => '*****************************',	
				 'client_secret' => '*****************************',
				 'org_name' =>  '*****************************',
				 'app_name' => '*****************************',
			);
			$easemob = new Easemob($option);
			//$res = $easemob->yy_hxSend('kefu',array("$umd5"), $content);
			$res = $easemob->userDetails($umd5);
			return $res ;
	  }
	  
	  /****想用户发送消息***/
	  function send_txt($umd5,$content){
			include_once('./lib/huanxin/Easemob.class.php');
			$option = array(
				 'client_id' => '*****************************',	
				 'client_secret' => '*****************************',
				 'org_name' =>  '*****************************',
				 'app_name' => '*****************************',
			);
			$easemob = new Easemob($option);
			$res = $easemob->yy_hxSend('kefu',array("$umd5"), $content);
			//$res = $easemob->Send_file('kefu',$us,'hi');
			return $res ;
	  }
	  
	  	  /****向用户发送名片***/
	  function send_card($umd5,$content,$ext){
			include_once('/home/wwwroot/default/zaina/lib/huanxin/Easemob.class.php');
			$option = array(
				 'client_id' => '*****************************',	
				 'client_secret' => '*****************************',
				 'org_name' =>  '*****************************',
				 'app_name' => '*****************************',
			);
			$easemob = new Easemob($option);
			//$res = $easemob->yy_hxSend('kefu',array("$umd5"), $content);
			//array("attr"=>"card",
			//"content" => "女 21岁",
			//"user" =>"c7b3c6d3c6e7b6b14e55fbe615536849")
			$res = $easemob->Send_card('kefu',array("$umd5"), $content,"users",$ext);
			//$res = $easemob->Send_file('kefu',$us,'hi');
			return $res ;
	  }
	  
	    /****用户发送升级消息暂时这样***/
	  function send_update($umd5){
			include_once('./lib/huanxin/Easemob.class.php');
			$option = array(
				 'client_id' => '*****************************',	
				 'client_secret' => '*****************************',
				 'org_name' =>  '*****************************',
				 'app_name' => '*****************************',
			);
			$easemob = new Easemob($option);
			$res = $easemob->yy_hxSend('kefu',array("$umd5"),
			"新版本发布啦,\n小客服诚邀您来内部测试尝鲜,\n新增了故事页面,\n每个人都有自己的故事,或悲伤、或欢乐。\n大声说出你的故事");
			$easemob->Send_file('kefu',array("$umd5"),'hi');
	  }
	  
	      /****判断是否是黑名单***/
	  function is_black($umd5="0",$deviceid="0"){
			global $db;
			if(empty($umd5)){
				$umd5="0";
			}
			if(empty($deviceid)){
				$deviceid="0";
			}
			if(!empty($umd5)||!empty($deviceid)){
				$res = $db->row_count("black"," (umd5 = '$umd5' OR deviceid = '$deviceid' ) AND isdel=1"," umd5");
				if($res>0){
					exit;
				}
			}
	  }
	  
	   /****设置用户昵称***/
	  function set_usernick($user,$nickname){
			include_once('/home/wwwroot/default/zaina/lib/huanxin/Easemob.class.php');
			$option = array(
				 'client_id' => '*****************************',	
				 'client_secret' => '*****************************',
				 'org_name' =>  '*****************************',
				 'app_name' => '*****************************',
			);
			$easemob = new Easemob($option);
			$res = $easemob->set_usernick($user,$nickname);
			//$res = $easemob->Send_file('kefu',$us,'hi');
			return $res ;
	  }
	  
	  
	 
	 /**
	*全角半角互转（全角转半角 $args2 为 1）
	*（半角转全角 $args2 为 0）
	*/
	function SBC_DBC($str, $args2) {
		$DBC = Array(//全角
			'０' , '１' , '２' , '３' , '４' ,
			'５' , '６' , '７' , '８' , '９' ,
			'Ａ' , 'Ｂ' , 'Ｃ' , 'Ｄ' , 'Ｅ' ,
			'Ｆ' , 'Ｇ' , 'Ｈ' , 'Ｉ' , 'Ｊ' ,
			'Ｋ' , 'Ｌ' , 'Ｍ' , 'Ｎ' , 'Ｏ' ,
			'Ｐ' , 'Ｑ' , 'Ｒ' , 'Ｓ' , 'Ｔ' ,
			'Ｕ' , 'Ｖ' , 'Ｗ' , 'Ｘ' , 'Ｙ' ,
			'Ｚ' , 'ａ' , 'ｂ' , 'ｃ' , 'ｄ' ,
			'ｅ' , 'ｆ' , 'ｇ' , 'ｈ' , 'ｉ' ,
			'ｊ' , 'ｋ' , 'ｌ' , 'ｍ' , 'ｎ' ,
			'ｏ' , 'ｐ' , 'ｑ' , 'ｒ' , 'ｓ' ,
			'ｔ' , 'ｕ' , 'ｖ' , 'ｗ' , 'ｘ' ,
			'ｙ' , 'ｚ' , '－' , '　' , '：' ,
			'．' , '，' , '／' , '％' , '＃' ,
			'！' , '＠' , '＆' , '（' , '）' ,
			'＜' , '＞' , '＂' , '＇' , '？' ,
			'［' , '］' , '｛' , '｝' , '＼' ,
			'｜' , '＋' , '＝' , '＿' , '＾' ,
			'￥' , '￣' , '｀'
		);
		$SBC = Array( // 半角
			'0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9',
			'A', 'B', 'C', 'D', 'E',
			'F', 'G', 'H', 'I', 'J',
			'K', 'L', 'M', 'N', 'O',
			'P', 'Q', 'R', 'S', 'T',
			'U', 'V', 'W', 'X', 'Y',
			'Z', 'a', 'b', 'c', 'd',
			'e', 'f', 'g', 'h', 'i',
			'j', 'k', 'l', 'm', 'n',
			'o', 'p', 'q', 'r', 's',
			't', 'u', 'v', 'w', 'x',
			'y', 'z', '-', ' ', ':',
			'.', ',', '/', '%', '#',
			'!', '@', '&', '(', ')',
			'<', '>', '"', '\'','?',
			'[', ']', '{', '}', '\\',
			'|', '+', '=', '_', '^',
			'$', '~', '`'
		);
		if ($args2 == 0) {
			return str_replace($SBC, $DBC, $str);  // 半角到全角
		} else if ($args2 == 1) {
			return str_replace($DBC, $SBC, $str);  // 全角到半角
		} else {
			return false;
		}
	}

	function safeEncoding($string,$outEncoding ='UTF-8'){ 
		$encoding = "UTF-8";    
		for($i=0;$i<strlen($string);$i++)    
		{    
			if(ord($string{$i})<128)    
				continue;    
			
			if((ord($string{$i})&224)==224)    
			{    
				//第一个字节判断通过    
				$char = $string{++$i};    
				if((ord($char)&128)==128)    
				{    
					//第二个字节判断通过    
					$char = $string{++$i};    
					if((ord($char)&128)==128)    
					{    
						$encoding = "UTF-8";    
						break;    
					}    
				}    
			}    
		
			if((ord($string{$i})&192)==192)    
			{    
				//第一个字节判断通过    
				$char = $string{++$i};    
				if((ord($char)&128)==128)    
				{    
					// 第二个字节判断通过    
					$encoding = "GB2312";    
					break;    
				}    
			}    
		}    
				 
		if(strtoupper($encoding) == strtoupper($outEncoding))    
			return $string;    
		else   
			return iconv($encoding,$outEncoding,$string);    
	}
?>