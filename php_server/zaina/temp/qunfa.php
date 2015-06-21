<?
include '../conf.php';
$page = 15;
$start = $page*10;
$pagenum = 100;
$res = $db->row_select('user',"", " $start,$pagenum ", "name, umd5");
foreach($res as $item){
	$us[] = $item['umd5'];
	$insert = array(
		'name' =>  $item['name'],
		'umd5' => $item['umd5'],
		'issend' => 1
	);
	//$db->row_insert('db_qunfa',$insert);
}
$user = array(
'e6b3716de67693439d64f6d6458a88a3',
'ff9e537dbea1b731de24c85116a2fff8',
'c7b3c6d3c6e7b6b14e55fbe615536849'
);
$json['users'] = $us;
$json['content'] = '新版本发布啦,\n小客服诚邀您来内部测试尝鲜,\n新增了故事页面,\n每个人都有自己的故事,或悲伤、或欢乐。\n大声说出你的故事';
$json['file'] = '内测版-1.5.apk111';
echo json_encode($json);
?>
