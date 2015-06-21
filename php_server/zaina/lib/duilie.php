<?php
/**
* Memcache 消息队列类
*/
class QMC {
const PREFIX = 'ASDFASDFFWQKE';
/**
* 初始化mc
* @staticvar string $mc
* @return Memcache
*/
static private function mc_init() {
static $mc = null;
if (is_null($mc)) {
$mc = new Memcache;
$mc->connect("127.0.0.1", 11211);
}
return $mc;
}
/**
* mc 计数器,增加计数并返回新的计数
* @param string $key   计数器
* @param int $offset   计数增量,可为负数.0为不改变计数
* @param int $time     时间
* @return int/false    失败是返回false,成功时返回更新计数器后的计数
*/
static public function set_counter( $key, $offset, $time=0 ){
$mc = self::mc_init();
$val = $mc->get($key);
if( !is_numeric($val) || $val < 0 ){
$ret = $mc->set( $key, 0, $time );
if( !$ret ) return false;
$val = 0;
}
$offset = intval( $offset );
if( $offset > 0 ){
return $mc->increment( $key, $offset );
}elseif( $offset < 0 ){
return $mc->decrement( $key, -$offset );
}
return $val;
}
/**
* 写入队列
* @param string $key
* @param mixed $value
* @return bool
*/
static public function input( $key, $value ){
$mc = self::mc_init();
$w_key = self::PREFIX.$key.'W';
$v_key = self::PREFIX.$key.self::set_counter($w_key, 1);
return $mc->set( $v_key, $value );
}
/**
* 读取队列里的数据
* @param string $key
* @param int $max  最多读取条数
* @return array
*/
static public function output( $key, $max=100 ){
$out = array();
$mc = self::mc_init();
$r_key = self::PREFIX.$key.'R';
$w_key = self::PREFIX.$key.'W';
$r_p   = self::set_counter( $r_key, 0 );//读指针
$w_p   = self::set_counter( $w_key, 0 );//写指针
if( $r_p == 0 ) $r_p = 1;
while( $w_p >= $r_p ){
if( --$max < 0 ) break;
$v_key = self::PREFIX.$key.$r_p;
$r_p = self::set_counter( $r_key, 1 );
$out[] = $mc->get( $v_key );
$mc->delete($v_key);
}
return $out;
}
}
/**
使用方法:
QMC::input($key, $value );//写入队列
$list = QMC::output($key);//读取队列
*/
?>