<?
 $filedir =  dirname(__FILE__); //当前文件的所在目录的路径
include $filedir."/../conf.php";

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

foreach($entities as $entitie_item){
	$from = $entitie_item['from'];
	$to = $entitie_item['to'];
	$timestamp = $entitie_item['timestamp'];
	$type = $entitie_item['payload']['bodies'][0]['type'];
	$url =  $entitie_item['payload']['bodies'][0]['url'];
	$msg =  $entitie_item['payload']['bodies'][0]['msg'];
	$filename = $entitie_item['payload']['bodies'][0]['filename'];
	echo $type."<br>";
	echo $url."<br>";
	echo $msg."<br>";
	echo $filename."<br>";
 $insert = array(
	"`from`" => $from ,
	"`to`" => $to,
	"`timestamp`" =>$timestamp,
	"`type`" => $type,
	"`content`" => addslashes($msg.$filename.$url) 
 );
 $db->row_insert('message',$insert);
 print_r($insert);
}
$messinsert = array(
	"pagename" => 'getmessage_cursor',
	"timer" => $timestamp,
	"errstr" => $cursor
);
 //$db->row_insert('db_error',$messinsert);
$messupdate = array(
	"pagename" => 'getmessage_cursor',
	"timer" => $timestamp,
	"errstr" => $cursor
);
 $db->row_update('db_error',$messupdate,"pagename = 'getmessage_cursor' ");
 
 //print_r($res);
 

 
?>