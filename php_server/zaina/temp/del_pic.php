<? 
$key = $_GET['key'];
$fid = $_GET['fid'];
if($key=='delete_pic'){
	$res = delete_pic('http://172.18.1.23:8081/'.$fid);
	echo $res;
}

 function delete_pic($pic_url){
		
		//$picurl = "http://180.153.40.10:8081/".$fid;
		return custom($pic_url,'DELETE');
	 }

/*****custom ַכַף*****/
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
	 
?>