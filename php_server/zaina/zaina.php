<?
include('conf.php'); 
$action = $_GET['action'];//action = u 为更新资料
$umd5 = $_GET['user'];//用户唯一id md5加密

$load = $_GET['load'];//获取当前加载方式-第一次加载（f）、刷新（r）、加载更多（l）

//$Latitude = $_GET['Latitude'];//纬度
//$Longitude = $_GET['Longitude'];//经度
$jingweidu = $_GET['jingweidu'];//经纬度
$jiedao = $_GET['jiedao'];

//判断是否被黑名单
is_black($umd5);
/***************性别筛选****************/
$sex = $_GET['sex'];//请求的性别
if($sex == "1"){
$sqlsex = " AND `user`.sex = '1'  ";
}else if($sex == "2"){
$sqlsex = " AND `user`.sex = '2'  "; 
}else{
$sqlsex = ''; 
}
/*******************************/
/***************距离筛选****************/
$juli = $_GET['juli'];//请求的性别
if($juli == "1"){
 
}
/*******************************/

$page = $_GET['page'];

//用户版本号，加故事版本后才有（1.5）
$version = $_GET['v'];
//$version = "1.1";


if(empty($umd5)){
json('no','参数丢失',"");
}

if(!empty($action)){
	if($action=='n'){//最新上线的
		if($load=='f'){//第一次加载
			if(!empty($jingweidu)){
				if(empty($jiedao)){
					$jwdarr = explode(',',$jingweidu);
					$baidulink = 'http://api.map.baidu.com/geocoder/v2/?ak=*****************************&location='.$jwdarr[1].','.$jwdarr[0].'&output=json&pois=0';
					$jwdjson = get($baidulink);
					$jwdstr = (array)json_decode($jwdjson);
					$jiedao = $jwdstr['result']->formatted_address;
				}else if($jiedao == ',,,,,'){
					$jwdarr = explode(',',$jingweidu); 
					$googlelink = 'http://maps.google.cn/maps/api/geocode/json?latlng='.$jwdarr[1].','.$jwdarr[0].'&sensor=true&language=zh-CN';
					$jwdjson_google = get($googlelink);
					$jwdstr_google = (array)json_decode($jwdjson_google);
					$jiedao = $jwdstr_google['result']->formatted_address;
					
				
				}
				//截取接到位置
				$jiedao = preg_replace('/^([^\d]+).*/', '$1', $jiedao);
				//从缓存服务器获取该用户上次的地点
				//并且判断是否是一个地点提交的数据
				$mem = new Memcache; 
				$mem->connect("127.0.0.1", 11211); 
				$userjiedao  = $mem->get($umd5.'jiedao');
				if($userjiedao != $jiedao){//如果不是则插入数据库
					/*******************获取该人的geohash********************/
					$jwdarrtmp = explode(',',$jingweidu);
					//include('./lib/geohash.class.php');
					require_once('./lib/geohash.class.php');
					 $geohash = new Geohash;
					 //得到这点的hash值
					 $hash = $geohash->encode($jwdarrtmp[1], $jwdarrtmp[0]);
					// echo $hash;
					//取前缀，前缀约长范围越小
					//$prefix = substr($hash, 0, 5);
					//取出相邻八个区域
					//$neighbors = $geohash->neighbors($prefix);
					/***************************************/
					$insert = array(
						'geo' => $jingweidu ,
						'time' => time(),
						'ip' => $_SERVER["REMOTE_ADDR"],
						'jiedao' => safeEncoding($jiedao),
						'geohash' =>  $hash,
						'umd5' => $umd5 
					);
					
					$mem->set($umd5.'jiedao', $jiedao, 0, 600);
					
					$db->row_insert("zaina",$insert); 
					$update =array(
						'zaina' =>  $jiedao,
						'version' => $version
					);
					$db->row_update('user',$update," umd5 = '$umd5' ");
				}
			}
			//	SELECT zaina.time ,zaina.jiedao,zaina.umd5,`user`.age ,`user`.sex ,`user`.headurl ,`user`.`name` FROM zaina LEFT JOIN `user` ON (zaina.umd5=`user`.umd5)WHERE zaina.time > 1408204280
			//这里请求每隔一段时间缓存一次（缓存在文件中）分页 时间
				//print_r($res);
			$res = select();
			json('yes','',$res);
		}else if($load=='r'){
			$mem = new Memcache; 
			$mem->connect("127.0.0.1", 11211); 
			if(!empty($jingweidu)){
					if(empty($jiedao)){
						$jwdarr = explode(',',$jingweidu);
						$baidulink = 'http://api.map.baidu.com/geocoder/v2/?ak=*****************************&location='.$jwdarr[1].','.$jwdarr[0].'&output=json&pois=0';
						$jwdjson = get($baidulink);
						$jwdstr = (array)json_decode($jwdjson);
						$jiedao = $jwdstr['result']->formatted_address;
					}
				//截取接到位置
				$jiedao = preg_replace('/^([^\d]+).*/', '$1', $jiedao);
				//从缓存服务器获取该用户上次的地点
				//并且判断是否是一个地点提交的数据 
				$userjiedao  = $mem->get($umd5.'jiedao');
				if($userjiedao != $jiedao){//如果不是则插入数据库
				
					$insert = array(
						'geo' => $jingweidu ,
						'time' => time(),
						'ip' => $_SERVER["REMOTE_ADDR"],
						'jiedao' => safeEncoding($jiedao),
						'umd5' => $umd5 
					);
					$db->row_insert("zaina",$insert); 
					
				
					$mem->set($umd5.'jiedao', $jiedao, 0, 600);
					
					$update =array(
						'zaina' =>  $jiedao
					);
					$db->row_update('user',$update," umd5 = '$umd5' ");
				}
			}
			
			;
			if($juli=='1'){
				$zainares  = $mem->get($umd5.$sex.'zaina');
			}else{
				$zainares  = $mem->get($sex.'zaina');
			}
			if(empty($zainares)){
				$res = select();
				if($juli=='1'){
					$mem->set($umd5.$sex.'zaina', $res, 0, 60);
				}else{
					$mem->set($sex.'zaina', $res, 0, 60);
				}
			}else{
				$res = $zainares;
			}
			json('yes','',$res);
		}else if($load=='l'){
				
				//$last = ($page+1)*$pagenum;
				$res = select();
				if(empty($res)){
				json('no',"擦肩而过不是缘",$res);
				}else{
				json('yes',"",$res);
				}
				
		}

		
	}else{
		json('no','缺少参数','');
	}
}else{
	json('no','缺少参数','');
}

/*************查询方法**************/
function select(){
	global $db;
	global $load;
	global $juli;
	global $page;
	global $sqlsex;
	global $juli;
	global $umd5;
	global $jiedao;
	
	if($juli == '1'){
		//$res_user = $db->row_select_one("user","umd5 = '$umd5' "," zaina");
		//$diqu = strCut($res_user['zaina'],4);
		//echo $jiedao;
		if(empty($jiedao)){
			//echo $umd5;
			$res_user = $db->row_select_one("user","umd5 = '$umd5' "," zaina");
			$diqu = strCut($res_user['zaina'],4);
			
			//print_r($res_user);
			//echo $res_user['zaina'];
		}else{
			//echo $jiedao;
			$diqu = strCut($jiedao,4);
		}
		
		//echo $diqu;
		if($load == 'f'||$load == 'r'){
		 /*{
			time: "1412991387",
			jiedao: "北京市海淀区复兴路",
			umd5: "ff9e537dbea1b731de24c85116a2fff8",
			age: "25",
			sex: "1",
			headurl: "http://img1.touxiang.cn/uploads/20121129/29-032851_236.jpg",
			province: "",
			city: "北京市",
			name: "杨旸"
		}*/
		$res_temp = $db->row_select_order("user","zaina LIKE '$diqu%' $sqlsex ",10,"name,city,province,headurl,sex,age,umd5,zaina");
		foreach($res_temp as $item){
		
			$tmparr['time'] = '';
			$tmparr['jiedao'] = $item['zaina'];
			$tmparr['umd5'] = $item['umd5'];
			$tmparr['age'] = $item['age'];
			$tmparr['sex'] = $item['sex'];
			$tmparr['headurl'] = $item['headurl'];
			$tmparr['province'] = $item['province'];
			$tmparr['city'] = $item['city'];
			$tmparr['name'] = $item['name'];
			 
			$res[] = $tmparr;
		}
					
		}else if($load=='l'){
			$pagenum = 10;//每一页显示的数目
			if($page<=0){
				$page = 1;
			}if($page>20){
				json('no','时代已久远','');
			}
			$start = $page*$pagenum;
			$res_temp = $db->row_select_order("user","zaina LIKE '$diqu%' $sqlsex ","$start,$pagenum","name,city,province,headurl,sex,age,umd5,zaina");
			foreach($res_temp as $item){
			
				$tmparr['time'] = '';
				$tmparr['jiedao'] = $item['zaina'];
				$tmparr['umd5'] = $item['umd5'];
				$tmparr['age'] = $item['age'];
				$tmparr['sex'] = $item['sex'];
				$tmparr['headurl'] = $item['headurl'];
				$tmparr['province'] = $item['province'];
				$tmparr['city'] = $item['city'];
				$tmparr['name'] = $item['name'];
				 
				$res[] = $tmparr;
			}
			
		}	
	}else{
		if($load == 'f'){
		$res = $db->row_query("SELECT zaina.time ,zaina.jiedao,zaina.umd5,`user`.age ,`user`.sex ,`user`.headurl ,`user`.province,`user`.city,`user`.`name`
												FROM zaina  LEFT JOIN `user`  ON (zaina.umd5=`user`.umd5) WHERE zaina.time > 1410878567   $sqlsex   ORDER BY zaina.id DESC  LIMIT  0,10 ");
					
		}else if($load=='r'){
		$res = $db->row_query("SELECT zaina.time ,zaina.jiedao,zaina.umd5,`user`.age ,`user`.sex ,`user`.headurl ,`user`.province,`user`.city,`user`.`name`
												FROM zaina  LEFT JOIN `user`  ON (zaina.umd5=`user`.umd5) WHERE 1=1    $sqlsex   ORDER BY zaina.id DESC  LIMIT  0,10 ");
		}else if($load=='l'){
			$pagenum = 10;//每一页显示的数目
			if($page<=0){
				$page = 1;
			}if($page>20){
				json('no','时代已久远','');
			}
			$start = $page*$pagenum;
			
		$res = $db->row_query("SELECT zaina.time ,zaina.jiedao,zaina.umd5,`user`.age ,`user`.sex ,`user`.headurl ,`user`.province,`user`.city,`user`.`name`
												FROM zaina  LEFT JOIN `user`  ON (zaina.umd5=`user`.umd5) WHERE zaina.time > 1408204280  $sqlsex   ORDER BY zaina.id DESC  LIMIT $start,$pagenum ");
		}	
	}
	return $res;
}

function strCut($str,$length)//$str为要进行截取的字符串，$length为截取长度（汉字算一个字，字母算半个字）
{
	$str = trim($str);
	$string = "";
	if(strlen($str) > $length)
	{
		for($i = 0 ; $i<$length ; $i++)
		{
			if(ord($str) > 127)
			{
				$string .= $str[$i] . $str[$i+1] . $str[$i+2];
				$i = $i + 2;
			}
			else
			{
				$string .= $str[$i];
			}
		}
		//$string .= "...";
		return $string;
	}
	return $str;
}