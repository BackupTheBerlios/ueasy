<?

// quick and dirty script for the gpl

$license  =   "/**";
$license .= "\n *  uEasy (http://ueasy.berlios.de)";
$license .= "\n *  Copyright Makina-Corpus (makina-corpus.org), Nicolas Raoul (nraoul@berlios.de)";
$license .= "\n *  This program is distributed under the terms of the GNU General Public License";
$license .= "\n *  See the whole license text in the \"LICENSE\" file.";
$license .= "\n */";
$license .= "\n";
$license .= "\n";

$path = $argv[1];
echo "Entering directory $path\n";
$dir = opendir($path);
while ($file = readdir($dir)) {
	$file = $path."/".$file;
	if (!is_dir($file)) {
		echo "Processing file $file\n";
		$old = join("", file($file));
		unlink($file);
		$new = fopen($file, "a");
		fwrite($new, $license);
		fwrite($new, $old);
		fclose($new);
	}
}

?>
