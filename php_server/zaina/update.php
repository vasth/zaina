<?
$action = $_GET['action'];// c 为check检查版本更新
if(!empty($action)){
	if($action=='c'){//现在最高版本是6
		//$jsonarr = array("versioncode" => '11' ,"downurl" => "http://shouji.360tpcdn.com/141011/50757d49ca446fda25691a8fb43820d0/com.ccxt.whl_11.apk", "content" =>"新增按区域筛选用户");
		$jsonarr = array("versioncode" => '13' ,"downurl" => "http://t.cn/R7mtgZV", "content" =>"升级啦服务器接口");
		//http://t.cn/R7mtgZV
		json('yes','',$jsonarr);
	}
}

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
?>