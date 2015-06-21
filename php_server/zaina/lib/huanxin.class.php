<?
error_reporting('E_ERROR');//错误等级提示
class huanxin {
	 
	function adduser($name,$pwd){
		$access_token_header = "Authorization: Bearer "."YWMt21JuoCdvEeSWDKHOVtMULgAAAUgRJoVC3r_QiuqmeEwpotv7Kga_MANLHws";
		$reques_url = "https://a1.easemob.com/ccxt/sandbox/users";
		$user_arr = array(
			"username" => "test",
			"password" => "pwd"
		);
		$res=$this->post($reques_url,json_encode($user_arr),$access_token_header);
		print_r($res);
	}
	
	function gettoken(){
		$postarr = array(
			"grant_type"=>"client_credentials",
			"client_id" =>"YXA6vpskYB7lEeSFFt0E5_hyiw",
			"client_secret"=>"YXA6ZJ8yU3uRdiXP9yGgbnlOhxXjEnM"
		);
		
		$postdata = json_encode($postarr);
		
		$res=$this->post("https://a1.easemob.com/ccxt/sandbox/token",$postdata,"token");
		print_r($res);
	}
	
	 
	function post($curl_url,$post_data,$access_token){
		//要请求的内容
		//$post_data['user']        =    "root";
		//$post_data['password']    =     "1988725"; 
		//$post_data['file']        =    '@C:\Documents and Settings\chenzhi\My Documents\My Pictures\1286606098_38.jpg'; 
		///$post_data['file']    =     '@'.$_FILES['image']['tmp_name']; 
		$ch        =    curl_init();
		//$curl_url    =    "http://172.16.27.51/server.php";
		curl_setopt($ch,CURLOPT_URL,$curl_url);
		curl_setopt($ch, CURLOPT_POST, 1);
		curl_setopt($ch, CURLOPT_HTTPHEADER, $access_token);//设置HTTP头
		curl_setopt($ch, CURLOPT_POSTFIELDS, $post_data); 
		curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);//不直接输出，返回到变量
		//curl_setopt($ch, CURLOPT_QUOTE, 1);
		//curl_setopt($ch, CURLOPT_HTTP200ALIASES, 1);
		///curl_setopt($ch, CURLOPT_POSTQUOTE, 1);
		 
		$curl_result = curl_exec($ch);
		return $curl_result;  
	}
}

$huanxin = new huanxin();
$huanxin->gettoken();
//$huanxin->adduser("test","123");
?>