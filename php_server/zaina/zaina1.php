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

$page = $_GET['page'];

//用户版本号，加故事版本后才有（1.5）
$version = $_GET['v'];
//$version = "1.1";
$pagenum = 10;//每一页显示的数目

if(empty($umd5)){
json('no','参数丢失',"");
}

if(!empty($action)){
	if($action=='n'){//最新上线的
		if($load=='f'){//第一次加载
			if(!empty($jingweidu)){
				if(empty($jiedao)){
					$jwdarr = explode(',',$jingweidu);
					$baidulink = 'http://api.map.baidu.com/geocoder/v2/?ak=O2IwdiijdOaQ3KQA9pdmMw8c&location='.$jwdarr[1].','.$jwdarr[0].'&output=json&pois=0';
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
				$mem->connect("172.18.1.23", 12000); 
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
			$res = $db->row_query("SELECT zaina.time ,zaina.jiedao,zaina.umd5,`user`.age ,`user`.sex ,`user`.headurl ,`user`.province,`user`.city,`user`.`name`
											FROM zaina  LEFT JOIN `user`  ON (zaina.umd5=`user`.umd5) WHERE zaina.time > 1410878567   $sqlsex   ORDER BY zaina.id DESC  LIMIT  0,10 ");
				json('yes','',$res);
		}else if($load=='r'){
			$mem = new Memcache; 
			$mem->connect("172.18.1.23", 12000); 
			if(!empty($jingweidu)){
					if(empty($jiedao)){
						$jwdarr = explode(',',$jingweidu);
						$baidulink = 'http://api.map.baidu.com/geocoder/v2/?ak=O2IwdiijdOaQ3KQA9pdmMw8c&location='.$jwdarr[1].','.$jwdarr[0].'&output=json&pois=0';
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
			
			$zainares  = $mem->get($sex.'zaina');
			if(empty($zainares)){
			$res = $db->row_query("SELECT zaina.time ,zaina.jiedao,zaina.umd5,`user`.age ,`user`.sex ,`user`.headurl ,`user`.province,`user`.city,`user`.`name`
											FROM zaina  LEFT JOIN `user`  ON (zaina.umd5=`user`.umd5) WHERE 1=1    $sqlsex   ORDER BY zaina.id DESC  LIMIT  0,10 ");
				$mem->set($sex.'zaina', $res, 0, 60);
			}else{
				$res = $zainares;
			}
			json('yes','',$res);
		}else if($load=='l'){
				if($page<=0){
					$page = 1;
				}if($page>20){
					json('no','时代已久远','');
				}
				$start = $page*$pagenum;
				//$last = ($page+1)*$pagenum;
				$res = $db->row_query("SELECT zaina.time ,zaina.jiedao,zaina.umd5,`user`.age ,`user`.sex ,`user`.headurl ,`user`.province,`user`.city,`user`.`name`
											FROM zaina  LEFT JOIN `user`  ON (zaina.umd5=`user`.umd5) WHERE zaina.time > 1408204280  $sqlsex   ORDER BY zaina.id DESC  LIMIT $start,$pagenum ");
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