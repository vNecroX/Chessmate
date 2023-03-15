<?PHP

function connectToDB()
{
	$server = "localhost";
	$username = "u283897785_root";
	$password = "darkpass<hostinger>";
	$database = "u283897785_ChessmateDB";

	$mysqli = new mysqli($server, $username, $password, $database);

	if($mysqli->connect_errno) 
	{
		die("Error with connection to the database");
	}

	if (!$mysqli->set_charset("utf8")) 
	{
	    die ("Error loading character set utf8: " . $mysqli->error);
	}

	return $mysqli;
}

?>