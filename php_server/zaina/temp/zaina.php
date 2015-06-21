<?
<?
include('conf.php'); 
$action = $_GET['action'];//action = u 为更新资料
$umd5 = $_GET['user'];//用户唯一id md5加密

$load = $_GET['load'];//获取当前加载方式-第一次加载（f）、刷新（r）、加载更多（l）

//$Latitude = $_GET['Latitude'];//纬度
//$Longitude = $_GET['Longitude'];//经度
$jingweidu = $_GET['jingweidu'];//经纬度
$jiedao = $_GET['jiedao'];


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
			 
			//	SELECT zaina.time ,zaina.jiedao,zaina.umd5,`user`.age ,`user`.sex ,`user`.headurl ,`user`.`name` FROM zaina LEFT JOIN `user` ON (zaina.umd5=`user`.umd5)WHERE zaina.time > 1408204280
			//这里请求每隔一段时间缓存一次（缓存在文件中）分页 时间
				//print_r($res);
			$res = $db->row_query("SELECT zaina.time ,zaina.jiedao,zaina.umd5,`user`.age ,`user`.sex ,`user`.headurl ,`user`.province,`user`.city,`user`.`name`
											FROM zaina  LEFT JOIN `user`  ON (zaina.umd5=`user`.umd5) WHERE zaina.time > 1410878567   $sqlsex   ORDER BY zaina.id DESC  LIMIT  0,10 ");
				json('yes','',$res);
		}else if($load=='r'){
			 
			 
			$res = $db->row_query("SELECT zaina.time ,zaina.jiedao,zaina.umd5,`user`.age ,`user`.sex ,`user`.headurl ,`user`.province,`user`.city,`user`.`name`
											FROM zaina  LEFT JOIN `user`  ON (zaina.umd5=`user`.umd5) WHERE 1=1    $sqlsex   ORDER BY zaina.id DESC  LIMIT  0,10 ");
				 
			 
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
?>