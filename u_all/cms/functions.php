<?

function add2log($msg) {
	$fp = fopen("log.txt", 'a');
	fwrite($fp, $msg."\n");
	fclose($fp);
}

?>
