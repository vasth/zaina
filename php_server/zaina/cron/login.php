<?
 $filedir =  dirname(__FILE__); //当前文件的所在目录的路径
include $filedir."/../conf.php";
//QMC::input("login_uid", "sn497281193@qq.com#4dfbbacf-fa8f-36b2-a744-dfa58a807573");//写入队列
 login_insert_uid();
    /****插入用户uid***/
zan_tj();//执行统计赞数据
//zan_send();//发送赞消息
getmessage();//获取聊天记录

if($_GET['key'] == 'test'){
	setnick();
}

function login_insert_uid(){

	global $db;
// echo 'empty--->11list';
	 $list = QMC::output("login_uid",1);//读取队列
	 //echo 'empty--->list'; 
	 print_r($list);
	 if(!empty($list)){
		$value = $list[0];
		//$value = 'ff9e537dbea1b731de24c85116a2fff8#["173"]';
		$valuearr = explode("#",$value);
		if(!empty($valuearr[0])||!empty($valuearr[1])){
			
			$otherid = trim($valuearr[0]);
			 echo $otherid ;
			$res = $db->row_select_one("user","email = '$otherid' OR media_uid = '$otherid' "," umd5,name"); // 
			print_r($res);
			$insert = array(
				"umd5" => $res['umd5'],
				"deviceid" => $valuearr[1],
				"otherid" =>$otherid,
				"time" => time()
			);
			$db->row_insert("login_log",$insert);
			if(!empty($res['umd5'])){
				$name = $res['name'];
				echo "user---> ".$res['umd5']."<br>";
			   echo "name---> ".$name."<br>";
			  
			   if(empty($res['name'])){
					$name = ' ';
			   }
			   set_usernick($res['umd5'],$name);
		    }
			if($res['umd5'] == 'a59e4cc197423bdb671a66e066853ca6'){
				send_txt('ff9e537dbea1b731de24c85116a2fff8','apple--->check');
			}
		}
	 }else{
		echo 'empty--->list';
	 }
}

function zan_tj(){
	global $db;

 	 $list = QMC::output("upzan",1);//读取队列
	 echo '<br>zan_tj--->list<br>';

	 print_r($list);
	 if(!empty($list)){
	 
		$value = $list[0];
		$valuearr = explode("#",$value);
		if(!empty($valuearr[0])||!empty($valuearr[1])){
			
			$gushi_id_json = trim($valuearr[1]);
			$itemzanarr = json_decode($gushi_id_json);
			 
			foreach($itemzanarr as $item){
				$res = $db->row_select_one("gushi"," id = '$item'  "," umd5 , content , zan"); // 
				$to = $res['umd5'];
				$zan = $res['zan'];
				$zan = $zan+1;
				$zanarr[$item] = $zan;
				//$zanarr['id'] = ;
				echo $to."--->send<br>";
				
			}
 
			$ids = implode(',', array_keys($zanarr)); 
			$sql = "UPDATE gushi SET zan = CASE id "; 
			foreach ($zanarr as $id => $ordinal) { 
				$sql .= sprintf("WHEN %d THEN %d ", $id, $ordinal); 
			} 
			$sql .= "END WHERE id IN ($ids)"; 
			$db->query_unbuffered($sql);
			 
		}
	 }else{
		echo 'zan_send--->empty';
	 }
}


function zan_send(){
	global $db;
// echo 'empty--->11list';
 	 $list = QMC::output("upzan",1);//读取队列
	 echo '<br>zan_send--->list<br>';
	 // $list = array('ff9e537dbea1b731de24c85116a2fff8#["128"]');
	 print_r($list);
	 if(!empty($list)){
	 
		$value = $list[0];
		$valuearr = explode("#",$value);
		if(!empty($valuearr[0])||!empty($valuearr[1])){
			
			$from = trim($valuearr[0]);
			
			$res_from = $db->row_select_one("user","umd5 = '$from' "," name , headurl , sex, age");
				$from_name = $res_from['name'];
				$from_sex = $res_from['sex']==1?'男':'女';
				$from_age =  $res_from['age'];
				$headurl  = $res_from['headurl'];
				
				
			$gushi_id_json = trim($valuearr[1]);
			$itemzanarr = json_decode($gushi_id_json);
			  
			foreach($itemzanarr as $item){
				$res = $db->row_select_one("gushi"," id = '$item'  "," umd5 , content"); // 
				$to = $res['umd5'];
				echo $to."--->send<br>";
				$content_gushi = $res['content'];
				$slstr = strCut($content_gushi,31);
				$content['headurl'] = $headurl;
				$content['username']=$from_name;
				$ext = array("attr"=>"card",
					"content" => $from_sex."  ".$from_age."岁  \n\n ta  赞了您的相册 \n".$slstr,
					"user" =>$from);
				//echo $from_name;
				$sendlog = send_card($to,$content,$ext);
					//print_r($sendlog);
			}
			 
		}
	 }else{
		echo 'zan_send--->empty';
	 }
}

function getmessage(){
	global $db;
	global $filedir;
	
//mkdir('../temp/getmessage.lock',0777);
if(file_exists($filedir."/../temp/getmessage.lock"))
{
    echo "getmessage ---> lock ";
    return;
}
/*$dir = "www.php100.com/newdata";
if(file_exists($file))
{
    echo "当前目录中，文件".$file."存在";
    echo "
";
}*/

	
	
include_once($filedir.'/../lib/huanxin/Easemob.class.php');

$option = array(
	 'client_id' => 'YXA6vpskYB7lEeSFFt0E5_hyiw',	
	 'client_secret' => 'YXA6ZJ8yU3uRdiXP9yGgbnlOhxXjEnM',
	 'org_name' =>  'ccxt',
	 'app_name' => 'sandbox',
);
$easemob = new Easemob($option);
$lastcursor = $db->row_select_one('db_error',"pagename = 'getmessage_cursor' ");
$res=$easemob->chatRecord_dao($lastcursor['errstr']);

$entities = $res['entities'];
$timestamp = $res['timestamp'];
$cursor = $res['cursor'];
echo $cursor;
foreach($entities as $entitie_item){
	$from = $entitie_item['from'];
	$to = $entitie_item['to'];
	$timestamp = $entitie_item['timestamp'];
	$type = $entitie_item['payload']['bodies'][0]['type'];
	$url =  $entitie_item['payload']['bodies'][0]['url'];
	$msg =  $entitie_item['payload']['bodies'][0]['msg'];
	$filename = $entitie_item['payload']['bodies'][0]['filename'];
	//echo $type."<br>";
	//echo $url."<br>";
	//echo $msg."<br>";
	//echo $filename."<br>";
 $insert = array(
	"`from`" => $from ,
	"`to`" => $to,
	"`timestamp`" =>$timestamp,
	"`type`" => $type,
	"`content`" => addslashes($msg.$filename.$url) 
 );
 $db->row_insert('message',$insert);
 //print_r($insert);
}
$messinsert = array(
	"pagename" => 'getmessage_cursor',
	"timer" => $timestamp,
	"errstr" => $cursor
);
 //$db->row_insert('db_error',$messinsert);
 if( !empty($cursor)){
	$messupdate = array(
		"pagename" => 'getmessage_cursor',
		"timer" => $timestamp,
		"errstr" => $cursor
	);
	 $db->row_update('db_error',$messupdate,"pagename = 'getmessage_cursor' ");
 }else{//如果为空则说明后面没有数据
	$data = $cursor;
	file_put_contents ($filedir.'/../temp/getmessage.lock', $data);
	echo 'getmessage ---> empty';
 }
  
}

//批量设置用户名
function setnick()
{
 set_time_limit(0);
 	$page = 26;
	$start = $page*100;
	//$start = 0;//770
	$pagenum = 100;
	//$res = $db->row_select('user',"", " $start,$pagenum ", "name, umd5");
	 global $db;
	 $res = $db->row_select('user',"", " $start,$pagenum ", "name, umd5");
	 foreach($res as $item){
		echo "user---> ".$item['umd5']."<br>";
		echo "name---> ".$item['name']."<br>";
		set_usernick($item['umd5'],$item['name']);
		//sleep(1);
	 }
	 //print_r($res);
	 //set_usernick('d769a9c43095f8c04ed17954658b8671','清清静静');
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
		$string .= "...";
		return $string;
	}
	return $str;
}


// sn497281193@qq.com#4dfbbacf-fa8f-36b2-a744-dfa58a807573
?>