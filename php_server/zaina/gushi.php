<?
include 'conf.php';
include './lib/emoji.php';

//json('yes','上传成功','');

$action = $_GET['action'];//action
$user = $_POST['user'];//用户的唯一id
$uid = $_POST['uid'];//设备唯一id
$picnum = $_POST['picnum'];//发布的图片数量
$content =  urldecode($_POST['content']);//发布故事的内容 
$jwd = $_POST['jwd'];//用户的经纬度


$page = $_GET['page'];

$f_user = $_GET['f_user'];//请求者
$q_user = $_GET['q_user'];//被请求者
$uid_get = $_GET['uid'];//设备唯一id
$gu_id = $_GET['gu_id'];//要删除的故事id

//判断是否被黑名单
is_black($user,$uid);
is_black($f_user,$uid_get);

$upzan = $_GET['upzan'];
if(!empty($upzan)){
	$mem = new Memcache; 
	$mem->connect("127.0.0.1", 11211); 
	$zanlog = $mem->get($f_user.$upzan);
	if($zanlog==1){
	
	}else{
		$userjiedao  = $mem->set($f_user.$upzan, 1, 0, 10000);
		QMC::input("upzan", $f_user.'#'.$upzan);//写入队列
		$insert  = array(
			"user" =>$f_user,
			"zan" => $upzan
		);
		$db->row_insert('zan',$insert);
	}
}
//$content = str_replace('\n','',$content);
$content = str_replace(PHP_EOL, ' ', $content);//待做需要吧连续的换行符替换为一个
//echo "1231321";
//--$mgcfile = trie_filter_load('./temp/mgc.dic');
//var_dump($mgcfile); 
//--$mgcints = trie_filter_search_all($mgcfile, $content);  // 一次把所有的敏感词都检测出来
//$res2 = trie_filter_search($mgcfile, $content);// 每次只检测一个敏感词  
//print_r($res1);
//foreach($mgcints as $mgcint){
//	$mgc = substr($content,$mgcint[0], $mgcint[1]); 
//	$mgcarr[] = $mgc;
//	$restr .= '**';
//}
 
//$content = str_replace($mgcarr, $restr, $content);
 

if(!empty($action)){
	if($action == 'p'){
		if(empty($uid)){//判断设备唯一id是否存在
			json('no','缺少参数','');
		}
		
		if($picnum<1){//没有发布图片
			if(!empty($content)){
				$insert = array(
					"content" => emoji_unified_to_docomo($content),
					"jwd" => $jwd,
					"pic" => '[]',
					'time' => time(),
					'is_del' => 1,
					"umd5" => $user
				);
				$res = $db->row_insert('gushi',$insert);
				if($res){
					send_txt('*****************************','http://180.153.40.16:600/zaina/cron/delgushi.php?id='.$db->insert_id()); 
					json('yes','发布成功','');
				}
			}else{
				json('no','参数不能为空','');
			}
		}else{//有发布的图片信息
			 
			for($i = 0 ; $i<$picnum ; $i++){
				//判断是否是从客户端上传的图片
				if(is_uploaded_file($_FILES['pic'.$i]['tmp_name'])){
						 $file=$_FILES['pic'.$i];
						 $name=$file['name'];
						 $type=$file['type']; 
						 $size=$file['size'];  
						 $tmpfile=$file['tmp_name'];  //临时存放文件
						 $error=$file['error'];  
						if($erro){
							json('no','上传出现错误'.$error,'');
						}
						if($size>1000000) {//大于1m
							json('no','图片太大'.$size,'');
						} 
						if($type != 'image/jpeg' ){ 
							json('no','类型不符合标准'.$type,'');
						}
						$extension = '.jpg';
						$filename=date("Ymdhis").'_'.rand(0,100).$extension;
						$thumbpic = "./temp/thumb_".$filename;
						img2thumb($tmpfile, $thumbpic);
						//$myfile="./temp/".$filename;
						/*****上传至weed服务器*****/
						$fid = get_fid();
						if(empty($fid)){
							//抽空吧这里改成存储到本服务器
							json('no','上传失败-图片服务器不可用',''); 
						}
						$post_data['file'] ='@'.$tmpfile;  //原图
						$uppic = update_pic($post_data,'http://127.0.0.1:8081/'.$fid.'_1');
						$post_thumb_data['file'] ='@'.$thumbpic;  //缩略图
						$uppic_thumb = update_pic($post_thumb_data,'http://127.0.0.1:8081/'.$fid);
						
						
						$uppicobj_thumb = json_decode($uppic_thumb);
						$uppicobj = json_decode($uppic);
						
						if($uppicobj->size>0 && $uppicobj_thumb->size>0){
							@unlink($uppic_thumb);
							$fids[$i] = 'http://file.fujinde.com:8081/'.$fid;
						}else{
							json('no','图片服务器错误','');
						} 
						 
				}else{
					json('no','非法上传','');
				}
				
				
			}
			  
			$insert = array(
				"content" => addslashes($content),
				"jwd" => $jwd,
				"pic" => json_encode($fids),
				'time' => time(),
				'is_del' => 1,
				"umd5" => $user
			);
			$res = $db->row_insert('gushi',$insert);
			if($res){
				send_txt('*****************************','http://180.153.40.16:600/zaina/cron/delgushi.php?id='.$db->insert_id()); 
				json('yes','上传成功','');
			}
		}
		 
	
	}else if($action == 't'){ //获取最新的故事
	
			if(empty($uid_get)){//判断设备唯一id是否存在
				json('no','缺少参数','');
			}
		
			$pagenum =10 ;
			  if($page<=0){
					$page = 0;
				}if($page>10){
					json('no','时代已久远','');
				}
				$start = $page*$pagenum;
				//$last = ($page+1)*$pagenum;
				
				//$res = $db->row_query("SELECT zaina.time ,zaina.jiedao,zaina.umd5,`user`.age ,`user`.sex ,`user`.headurl ,`user`.province,`user`.city,`user`.`name`
				//							FROM zaina  LEFT JOIN `user`  ON (zaina.umd5=`user`.umd5) WHERE zaina.time > 1408204280  $sqlsex   ORDER BY zaina.id DESC  LIMIT $start,$pagenum ");
				$res = $db->row_query("SELECT gushi.id ,gushi.time ,gushi.content,gushi.zan,gushi.pic,gushi.umd5,`user`.age ,`user`.sex ,`user`.headurl ,`user`.`name`
											FROM gushi  LEFT JOIN `user`  ON (gushi.umd5=`user`.umd5) WHERE gushi.is_del = 1    ORDER BY gushi.id DESC  LIMIT $start,$pagenum");
			
				//print_r($res);
				//print_r($res[0]['pic']);
				foreach($res as $item){
					$item['pic'] = json_decode($item['pic']);
					if($item['zan']=='0'){
						$item['zan'] = "";
					}else{
						$item['zan'] = "".$item['zan'];
					}
					//$item['zan'] = "";
					
					$jsonarr[] = $item;
 				}
				
				//json('yes',"擦肩而过不是缘",$jsonarr);
				
				if(empty($jsonarr)){
				json('no',"擦肩而过不是缘",$jsonarr);
				}else{
				json('yes',"",$jsonarr);
				}
				
		
	}else if($action == 'u'){ //获取指定用户的故事
			$pagenum =10 ;
			  if($page<=0){
					$page = 0;
				}if($page>10){
					json('no','时代已久远','');
				}
				$start = $page*$pagenum;
				//$last = ($page+1)*$pagenum;
				
				if(empty($q_user)&&empty($uid_get)&&empty($f_user)){
					json('no','缺少参数','');
				}
				
				//$res = $db->row_query("SELECT zaina.time ,zaina.jiedao,zaina.umd5,`user`.age ,`user`.sex ,`user`.headurl ,`user`.province,`user`.city,`user`.`name`
				//							FROM zaina  LEFT JOIN `user`  ON (zaina.umd5=`user`.umd5) WHERE zaina.time > 1408204280  $sqlsex   ORDER BY zaina.id DESC  LIMIT $start,$pagenum ");
				$res = $db->row_query("SELECT gushi.id ,gushi.time ,gushi.content,gushi.pic,gushi.umd5,`user`.age ,`user`.sex ,`user`.headurl ,`user`.`name`
											FROM gushi  LEFT JOIN `user`  ON (gushi.umd5=`user`.umd5) WHERE gushi.umd5 = '$q_user'  AND gushi.is_del < 2   ORDER BY gushi.id DESC  LIMIT $start,$pagenum");
			
				//print_r($res);
				//print_r($res[0]['pic']);
				foreach($res as $item){
					$item['pic'] = json_decode($item['pic']);
					if($item['zan']=='0'){
						$item['zan'] = "";
					}else{
						$item['zan'] = "".$item['zan'];
					}
					//$item['zan'] = "";
					$jsonarr[] = $item;
 				}
				
				//json('yes',"擦肩而过不是缘",$jsonarr);
				
				if(empty($jsonarr)){
				json('no',"擦肩而过不是缘",$jsonarr);
				}else{
				json('yes',"",$jsonarr);
				}
				
		
	}else if($action == 'd'){ //删除指定用户的故事
			  
				if(empty($gu_id)&&empty($uid_get)&&empty($f_user)){
					json('no','缺少参数','');
				}
				
				$update = array(
					'is_del' => 2
				);
				
				$res = $db->row_update('gushi',$update, "id = '$gu_id' AND umd5 = '$f_user' ");
				
				//$res = $db->row_delete('gushi',"id = '$gu_id' AND umd5 = '$f_user' ");
		   
				if($res){
				json('yes',"删除成功",'');
				}else{
				json('no',"删除失败",'');
				}
				 
	}

}

/*
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
						 /*****上传至weed服务器***** /
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
						 } * /
						 
						 $fid = get_fid();
						 if(empty($fid)){
							//抽空吧这里改成存储到本服务器
							json('no','上传失败-图片服务器不可用',''); 
						 }
						 //×××××××××××××××××待做查看本机通过公网访问图片服务器的收到地址
						 $uppic = update_pic($post_data,'http://172.18.1.23:8081/'.$fid);
						 $uppicobj = json_decode($uppic);
						 if($uppicobj->size>0 ){
							$new_pic = 'http://180.153.40.10:8081/'.$fid;
							 $resJson = array(
								'headurl' => $new_pic,
								'nickname' => "",
								'age' => "",
								'sex' => '',
								'zhiye' => "",
								'qianming' => ""
								);
							
							 /****方案二，图片上传后吧原来的删掉*** /
							 $is_weed = $db->row_select_one("user","umd5 = '$u_pic_user'","headurl");
							 $pos = strpos($is_weed['headurl'], "180.153.40.10:8081");  
							 if($pos){
								//print_r(delete_pic($is_weed['headurl']));
								$old_temp = $is_weed['headurl'];
							 }
							 $update = array(
								"headurl" => $new_pic 
							 );
							 $db->row_update('user',$update,"umd5 = '$u_pic_user' ");
							 //需要改成内部服务器地址
							 delete_pic(str_replace('180.153.40.10','172.18.1.23',$old_temp));
							 //delete_pic('http://172.18.1.23:8081/4,79688abbdd');
							  json('yes','上传成功',$resJson);
						 }else{
							json('no','上传失败','');
						}
						 
					}else{
						json('no','非法上传','');
				    }
					
					
					
		 
/**
 * 生成缩略图
 * @author yangzhiguo0903@163.com
 * @param string     源图绝对完整地址{带文件名及后缀名}
 * @param string     目标图绝对完整地址{带文件名及后缀名}
 * @param int        缩略图宽{0:此时目标高度不能为0，目标宽度为源图宽*(目标高度/源图高)}
 * @param int        缩略图高{0:此时目标宽度不能为0，目标高度为源图高*(目标宽度/源图宽)}
 * @param int        是否裁切{宽,高必须非0}
 * @param int/float  缩放{0:不缩放, 0<this<1:缩放到相应比例(此时宽高限制和裁切均失效)}
 * @return boolean
 */
function img2thumb($src_img, $dst_img, $width = 150, $height = 150, $cut = 0, $proportion = 0)
{
    if(!is_file($src_img))
    {
        return false;
    }
    $ot = fileext($dst_img);
    $otfunc = 'image' . ($ot == 'jpg' ? 'jpeg' : $ot);
    $srcinfo = getimagesize($src_img);
    $src_w = $srcinfo[0];
    $src_h = $srcinfo[1];
    $type  = strtolower(substr(image_type_to_extension($srcinfo[2]), 1));
    $createfun = 'imagecreatefrom' . ($type == 'jpg' ? 'jpeg' : $type);
 
    $dst_h = $height;
    $dst_w = $width;
    $x = $y = 0;
 
    /**
     * 缩略图不超过源图尺寸（前提是宽或高只有一个）
     */
    if(($width> $src_w && $height> $src_h) || ($height> $src_h && $width == 0) || ($width> $src_w && $height == 0))
    {
        $proportion = 1;
    }
    if($width> $src_w)
    {
        $dst_w = $width = $src_w;
    }
    if($height> $src_h)
    {
        $dst_h = $height = $src_h;
    }
 
    if(!$width && !$height && !$proportion)
    {
        return false;
    }
    if(!$proportion)
    {
        if($cut == 0)
        {
            if($dst_w && $dst_h)
            {
                if($dst_w/$src_w> $dst_h/$src_h)
                {
                    $dst_w = $src_w * ($dst_h / $src_h);
                    $x = 0 - ($dst_w - $width) / 2;
                }
                else
                {
                    $dst_h = $src_h * ($dst_w / $src_w);
                    $y = 0 - ($dst_h - $height) / 2;
                }
            }
            else if($dst_w xor $dst_h)
            {
                if($dst_w && !$dst_h)  //有宽无高
                {
                    $propor = $dst_w / $src_w;
                    $height = $dst_h  = $src_h * $propor;
                }
                else if(!$dst_w && $dst_h)  //有高无宽
                {
                    $propor = $dst_h / $src_h;
                    $width  = $dst_w = $src_w * $propor;
                }
            }
        }
        else
        {
            if(!$dst_h)  //裁剪时无高
            {
                $height = $dst_h = $dst_w;
            }
            if(!$dst_w)  //裁剪时无宽
            {
                $width = $dst_w = $dst_h;
            }
            $propor = min(max($dst_w / $src_w, $dst_h / $src_h), 1);
            $dst_w = (int)round($src_w * $propor);
            $dst_h = (int)round($src_h * $propor);
            $x = ($width - $dst_w) / 2;
            $y = ($height - $dst_h) / 2;
        }
    }
    else
    {
        $proportion = min($proportion, 1);
        $height = $dst_h = $src_h * $proportion;
        $width  = $dst_w = $src_w * $proportion;
    }
 
    $src = $createfun($src_img);
    $dst = imagecreatetruecolor($width ? $width : $dst_w, $height ? $height : $dst_h);
    $white = imagecolorallocate($dst, 255, 255, 255);
    imagefill($dst, 0, 0, $white);
 
    if(function_exists('imagecopyresampled'))
    {
        imagecopyresampled($dst, $src, $x, $y, 0, 0, $dst_w, $dst_h, $src_w, $src_h);
    }
    else
    {
        imagecopyresized($dst, $src, $x, $y, 0, 0, $dst_w, $dst_h, $src_w, $src_h);
    }
    $otfunc($dst, $dst_img);
    imagedestroy($dst);
    imagedestroy($src);
    return true;
}

function fileext($file)
{
    return pathinfo($file, PATHINFO_EXTENSION);
}
?>