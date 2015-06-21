<?php

/*
 *	mysql数据库 DB类
 *	@package	db
 *	@author		yytcpt(无影)
 *	@version	2008-03-27
 *	@copyrigth	http://www.d5s.cn/ 
 */
class db {
	var $debug = true;
	var $connection_id = "";
	var $pconnect = 0;
	var $shutdown_queries = array();
	var $queries = array();
	var $query_id = "";
	var $query_count = 0;
	var $record_row = array();
	var $failed = 0;
	var $halt = "";
	var $query_log = array();
	function connect($db_config){
		 //echo '11111111111111111111111111';
		if ($this->pconnect){
			$this->connection_id = mysql_pconnect($db_config["hostname"], $db_config["username"], $db_config["password"]);
		}else{
			$this->connection_id = mysql_connect($db_config["hostname"], $db_config["username"], $db_config["password"]);
		}
		if ( ! $this->connection_id ){ 
			$this->halt("Can not connect MySQL Server");
		}
		if ( ! @mysql_select_db($db_config["database"], $this->connection_id) ){
			$this->halt("Can not connect MySQL Database");
		}
		if ($db_config["charset"]) {
			@mysql_unbuffered_query("SET NAMES '".$db_config["charset"]."'");
		}
		return true;
	}
	//发送SQL 查询，并返回结果集
    function query($query_id, $query_type='mysql_query'){
        $this->query_id = $query_type($query_id, $this->connection_id);
		$this->queries[] = $query_id;
        if (! $this->query_id ) {
            $this->halt("查询失败:\n$query_id");
		}
		$this->query_count++;
		$this->query_log[] = $str;
        return $this->query_id;
    }
	//发送SQL 查询，并不获取和缓存结果的行
	function query_unbuffered($sql=""){
		return $this->query($sql, 'mysql_unbuffered_query');
	}
	//从结果集中取得一行作为关联数组
    function fetch_array($sql = ""){
    	if ($sql == "") $sql = $this->query_id;
        $this->record_row = @mysql_fetch_array($sql, MYSQL_ASSOC);
        return $this->record_row;
    }
	function shutdown_query($query_id = ""){
		$this->shutdown_queries[] = $query_id;
    }
	//取得结果集中行的数目，仅对 INSERT，UPDATE 或者 DELETE
	function affected_rows() {
        return @mysql_affected_rows($this->connection_id);
    }
	//取得结果集中行的数目，仅对 SELECT 语句有效
    function num_rows($query_id="") {
		if ($query_id == "") $query_id = $this->query_id;
        return @mysql_num_rows($query_id);
    }
	//返回上一个 MySQL 操作中的错误信息的数字编码
	function get_errno(){
		$this->errno = @mysql_errno($this->connection_id);
		return $this->errno;
	}
	//取得上一步 INSERT 操作产生的 ID
    function insert_id(){
        return @mysql_insert_id($this->connection_id);
    }
	//得到查询次数
    function query_count() {
        return $this->query_count;
    }
	//释放结果内存
    function free_result($query_id=""){
   		if ($query_id == "") $query_id = $this->query_id;
    	@mysql_free_result($query_id);
    }
	//关闭 MySQL 连接
    function close_db(){
    	if ( $this->connection_id ) return @mysql_close( $this->connection_id );
    }
	//列出 MySQL 数据库中的表
    function get_table_names(){
    	global $db_config;
		$result = mysql_list_tables($db_config["database"]);
		$num_tables = @mysql_numrows($result);
		for ($i = 0; $i < $num_tables; $i++) {
			$tables[] = mysql_tablename($result, $i);
		}
		mysql_free_result($result);
		return $tables;
   	}
	//从结果集中取得列信息并作为对象返回，取得所有字段
    function get_result_fields($query_id=""){
   		if ($query_id == "") $query_id = $this->query_id;
		while ($field = mysql_fetch_field($query_id)) {
            $fields[] = $field;
		}
		return $fields;
   	}
	//错误提示
    function halt($the_error=""){
		$message = $the_error."<br/>\r\n";
		//echo $the_error."error";
		$message.= $this->get_errno() . "<br/>\r\n";
		$sql = "INSERT INTO `db_error`(pagename, errstr, timer) VALUES('".$_SERVER["PHP_SELF"]."', '".base64_encode($message)."', ".time().")";
		@mysql_unbuffered_query($sql);
		if ($this->debug==true){
			echo "<html><head><title>MySQL 数据库错误</title>";
			echo "<style type=\"text/css\"><!--.error { font: 11px tahoma, verdana, arial, sans-serif, simsun; }--></style></head>\r\n";
			echo "<body>\r\n";
			echo "<blockquote>\r\n";
			echo "<textarea class=\"error\" rows=\"15\" cols=\"100\" wrap=\"on\" >" . htmlspecialchars($message) . "</textarea>\r\n";
			echo "</blockquote>\r\n</body></html>";
			exit;
		}
    }
	function __destruct(){
		$this->shutdown_queries = array();
		$this->close_db();
	}
	
	function sql_select($tbname, $where="", $limit=0, $fields="*", $orderby="id", $sort="DESC"){
		//暂时用不到先注释
		//$sql = "SELECT ".$fields." FROM `".$tbname."` ".($where?" WHERE ".$where:"")." ORDER BY ".$orderby." ".$sort.($limit ? " limit ".$limit:"");
		$sql = "SELECT ".$fields." FROM `".$tbname."` ".($where?" WHERE ".$where:"").($limit ? " limit ".$limit:"");
		return $sql;
	}
	
	function sql_select_order($tbname, $where="", $limit=0, $fields="*", $orderby="id", $sort="DESC"){
		//暂时用不到先注释
		$sql = "SELECT ".$fields." FROM `".$tbname."` ".($where?" WHERE ".$where:"")." ORDER BY ".$orderby." ".$sort.($limit ? " limit ".$limit:"");
		//$sql = "SELECT ".$fields." FROM `".$tbname."` ".($where?" WHERE ".$where:"").($limit ? " limit ".$limit:"");
		return $sql;
	}
	
	function sql_insert($tbname, $row){
		foreach ($row as $key=>$value) {
			$sqlfield .= $key.",";
			$sqlvalue .= "'".$value."',";
		}
		return "INSERT INTO `".$tbname."`(".substr($sqlfield, 0, -1).") VALUES (".substr($sqlvalue, 0, -1).")";
	}
	function sql_update($tbname, $row, $where){
		foreach ($row as $key=>$value) {
			$sqlud .= $key."= '".$value."',";
		}
		return "UPDATE `".$tbname."` SET ".substr($sqlud, 0, -1)." WHERE ".$where;
	}
	function sql_delete($tbname, $where){
		return "DELETE FROM `".$tbname."` WHERE ".$where;
	}
	//新增加一条记录
	function row_insert($tbname, $row){
		$sql = $this->sql_insert($tbname, $row);
		return $this->query_unbuffered($sql);
	}
	//更新指定记录
	function row_update($tbname, $row, $where){
		$sql = $this->sql_update($tbname, $row, $where);
		return $this->query_unbuffered($sql);
	}
	//删除满足条件的记录
	function row_delete($tbname, $where){
		$sql = $this->sql_delete($tbname, $where);
		return $this->query_unbuffered($sql);
	}
	/*	根据条件查询，返回所有记录
	 *	$tbname 表名, $where 查询条件, $limit 返回记录, $fields 返回字段
	 */
	function row_select($tbname, $where="", $limit=0, $fields="*", $orderby="id", $sort="DESC"){
		$sql = $this->sql_select($tbname, $where, $limit, $fields, $orderby, $sort);
		return $this->row_query($sql);
	}
	
	/*	根据条件排序查询，返回所有记录
	 *	$tbname 表名, $where 查询条件, $limit 返回记录, $fields 返回字段
	 */
	function row_select_order($tbname, $where="", $limit=0, $fields="*", $orderby="id", $sort="DESC"){
		$sql = $this->sql_select_order($tbname, $where, $limit, $fields, $orderby, $sort);
		return $this->row_query($sql);
	}
	
	//详细显示一条记录
	function row_select_one($tbname, $where, $fields="*", $orderby="id"){
	
		$sql = $this->sql_select_order($tbname, $where, 1, $fields, $orderby);
		//echo  $sql;
		return $this->row_query_one($sql);
	}
	function row_query($sql){
		$rs	 = $this->query($sql);
		$rs_num = $this->num_rows($rs);
		$rows = array();
		for($i=0; $i<$rs_num; $i++){
			$rows[] = $this->fetch_array($rs);
		}
		$this->free_result($rs);
		return $rows;
	}
	function row_query_one($sql){
		$rs	 = $this->query($sql);
		$row = $this->fetch_array($rs);
		$this->free_result($rs);
		return $row;
	}
	//计数统计
	function row_count($tbname, $where=""){
		$sql = "SELECT count(id) as row_sum FROM `".$tbname."` ".($where?" WHERE ".$where:"");
		$row = $this->row_query_one($sql);
		//return $sql."----------".$row["row_sum"];//调试则打开
		return  $row["row_sum"];
	}
}
?>