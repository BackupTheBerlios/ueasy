<?
$error_code = 777;
$error_msg = "Boeing";

if (!copy($argv[5],"../cms/data-".$argv[2])) {
	$error_code = 100;
	$error_msg = "error while writing document";
}

echo "<?xml version=\"1.0\"?>
<methodResponse>
	<params>
		<param><value><int>$error_code</int></value></param>
		<param><value><string>$error_msg</string></value></param>
	</params>
</methodResponse>"
?>
