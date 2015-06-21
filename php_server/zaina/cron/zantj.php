<?
include "../conf.php";
 login_insert_uid();

function login_insert_uid(){
	global $db;
$zanarr = $db->row_select('zan',"iscount = '0'");
	 if(!empty($zanarr)){
		$allarr = array();
		foreach($zanarr as $item){
			$itemzanjson = $item['zan'];
			$itemzanarr = json_decode($itemzanjson);
			$allarr = array_merge_recursive($allarr, $itemzanarr);  
		}
		
		print_r($display_order = array_count_values($allarr));
		
		/*$display_order = array( 
			1 => 4, 
			2 => 1, 
			3 => 2, 
			4 => 3, 
			5 => 9, 
			6 => 5, 
			7 => 8, 
			8 => 9 
		); */
		$ids = implode(',', array_keys($display_order)); 
		$sql = "UPDATE gushi SET zan = CASE id "; 
		foreach ($display_order as $id => $ordinal) { 
			$sql .= sprintf("WHEN %d THEN %d ", $id, $ordinal); 
		} 
		$sql .= "END WHERE id IN ($ids)"; 
		echo $sql;
		 
	 }else{
		echo 'empty--->list';
	 }
}

?>