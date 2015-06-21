<?
include('duilie.php');
$key = "duilie";
$value = "duilie_value";
for($i=0;$i<100;$i++){
  //QMC::input($key, $i);//写入队列
}

for($i=0;$i<100;$i++){
	$list = QMC::output($key,1);//读取队列
	print_r($list);
}

?>