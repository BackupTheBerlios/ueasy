<?
include 'functions.php';

$error_code = "777";
$error_msg = "Boeing!";
$name = "Mon rapport";
$lang = "fr_FR";

// Here, mode is based on the file extension. "foo.html" becomes "org.makcorp.ueasy.modes.Html". It can be done otherwise
$point = strrpos($argv[2],'.');
$extensionFirstLetter = strtoupper(substr($argv[2],$point+1,1));
$extensionLastLetters =  strtolower(substr($argv[2],$point+2,strlen($argv[2])));
$modename = "org.makcorp.ueasy.modes.$extensionFirstLetter$extensionLastLetters";

add2log ("Opening ".$argv[2]." as $modename ...");

$css = join("",file("../cms/test.css"));

// Early Experimentation for Spip
// If it is a spip document, get it in the mysql database
if ("$extensionFirstLetter$extensionLastLetters" == "Spip") {
	$id_article = substr($argv[2],0,$point);
	add2log("Opening Spip article: $id_article\n");
	$link = mysql_connect ("localhost", "root", ""); // Ooh, bad !
	mysql_select_db ("spip");
	$res0 = mysql_query("select * from spip_articles where id_article=$id_article;", $link);
	$article = mysql_fetch_array($res0);
}
// If not, it must be a filesystem document
else {
	$data = join("",file("data-".$argv[2]));
}

// Reply to the server
echo "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>
<methodResponse>
	<params>
		<param><value><int>$error_code</int></value></param>
		<param><value><string>$error_msg</string></value></param>
		<param><value><string><![CDATA[$name]]></string></value></param>
		<param><value><string>$modename</string></value></param>
		<param><value><string>$lang</string></value></param>
		<param><value><string><![CDATA[TO BE OVERWRITTEN BY SERVER]]></string></value></param>
		<param><value><string><![CDATA[$css]]></string></value></param>
		<param><value><string><![CDATA[$data]]></string></value></param>
	</params>
</methodResponse>";

add2log("... open ".$argv[2]." as $modename");

?>
