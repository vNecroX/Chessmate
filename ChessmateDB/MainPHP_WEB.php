<?PHP

include("QueriesPHP_WEB.php");
include("ConnectionPHP_WEB.php");

$c = connectToDB();

if(!isset($_POST["case"]))
{
	echo "Error, variable not obtained";
}

switch ($_POST["case"]) 
{
	case "verifyUsername":
		echo Queries::verifyUsername($c, $_POST["userName"]); // Ready
		break;
	case "createAccount":
		echo Queries::createAccount($c, $_POST["nation"], $_POST["userName"], $_POST["mail"], $_POST["password"]); // Ready
		break;
	case "verifyLogin":
		echo Queries::verifyLogin($c, $_POST["userName"], $_POST["password"]); // Ready
		break;
	case "getIdOponent":
		echo Queries::getIdOponent($c, $_POST["userName"]); // Ready
		break;
	case "createGame":
		echo Queries::createGame($c, $_POST["idMode"], $_POST["idDifficulty"], $_POST["idUser"], 
			$_POST["oponentName"], $_POST["idGameState"], $_POST["name"], $_POST["secondsLeft"], 
			$_POST["color"], $_POST["colorOponent"], $_POST["rewinds"]); // Ready
		break;
	case "getIDLastGame":
		echo Queries::getIDLastGame($c, $_POST["idUser"]); // Ready
		break;
	case "addNewMove":
		echo Queries::addNewMove($c, $_POST["stringMoves"], $_POST["secondsLeft"], $_POST["idGame"]); // Ready
		break;
	case "setGame":
		echo Queries::setGame($c, $_POST["idGameState"], $_POST["stringMoves"], $_POST["secondsLeft"], $_POST["rewinds"], $_POST["idGame"]); // Ready
		break;
	case "getPlayersList":
		echo Queries::getPlayersList($c, $_POST["idUser"]); // Ready
		break;
	case "getSavedGames":
		echo Queries::getSavedGames($c, $_POST["idUser"]); // Ready
		break;
	case "getSavedGamesInfo":
		echo Queries::getSavedGamesInfo($c, $_POST["idGame"]); // Ready
		break;
	case "setNameOfGame":
		echo Queries::setNameOfGame($c, $_POST["nameOfGame"], $_POST["idGame"]); // Ready
		break;
	case "deleteGame":
		echo Queries::deleteGame($c, $_POST["idGame"]); // Ready
		break;
	case "getSavedMoves":
		echo Queries::getSavedMoves($c, $_POST["idGame"]); // Ready
		break;
	case "deleteMoves":
		echo Queries::deleteMoves($c, $_POST["stringMoves"], $_POST["rewinds"], $_POST["idGame"]); // Ready
		break;
	case "setPersonalData":
		echo Queries::setPersonalData($c, $_POST["nation"], $_POST["userName"], $_POST["mail"], $_POST["password"], $_POST["idUser"]); // Ready
		break;
	case "getPersonalData":
		echo Queries::getPersonalData($c, $_POST["idUser"]); // Ready
		break;
	case "getNations":
		echo Queries::getNations($c); // Ready
		break;
	case "checkWifi":
		echo Queries::checkWifi($c); // Ready
		break;



	case "CreateTablesChat": // Ready
	    	$emitter = $_POST["emitter"];
			$receptor = $_POST["receptor"];

			$tableNameEmitter = "messages_" . $emitter;
			$tableNameReceptor = "messages_" . $receptor;

			$rCreateTableEmitter = Queries::CreateTableChat($c, $tableNameEmitter);

			if($rCreateTableEmitter == -1)
			{
				Queries::DeleteMessages($c, $tableNameEmitter);
			}

			$rCreateTableReceptor = Queries::CreateTableChat($c, $tableNameReceptor);

			if($rCreateTableReceptor == -1)
			{
				Queries::DeleteMessages($c, $tableNameReceptor);
			}

			echo "Success";
	    	break;

	case "SendMessages": // Ready
		setlocale(LC_TIME, 'es_PE.UTF-8');
		date_default_timezone_set('America/Monterrey');

		$emitter = $_POST["emitter"];
		$receptor = $_POST["receptor"];
		$message = $_POST["message"];

		$tableNameEmitter = "messages_" . $emitter;
		$tableNameReceptor = "messages_" . $receptor;

		$mssg_hour = strftime("%H:%M");

		$rMssgSentEmitter = Queries::SendMessages($c, $tableNameEmitter, $message, 1, $mssg_hour);
		$rMssgSentReceptor = Queries::SendMessages($c, $tableNameReceptor, $message, 2, $mssg_hour);

		echo $rMssgSentEmitter . $rMssgSentReceptor;
		break;

	case "SeeAllMessages": // Ready
		$emitter = $_POST["emitter"];
  		$tableNameEmitter = "messages_" . $emitter;
     	$rMssgSeeAll = Queries::SeeAllMessages($c, $tableNameEmitter);
      	echo $rMssgSeeAll;
      	break;

    case "UnseenMessages": // Ready
    	$emitter = $_POST["emitter"];
  		$tableNameEmitter = "messages_" . $emitter;
      	$rMssgUnseen = Queries::UnseenMessages($c, $tableNameEmitter);
      	echo $rMssgUnseen;
      	break;

	case "DropTable": // Ready
  		$emitter = $_POST["emitter"];
  		$tableNameEmitter = "messages_" . $emitter;
  		$rDropMssgEmitter = Queries::DropTable($c, $tableNameEmitter);
  		echo $rDropMssgEmitter;
  		break;




  	case "setAllConfiguration": 
			echo Queries::setAllConfiguration($c, $_POST["autosave"], $_POST["board"], $_POST["chat"], $_POST["notif"], $_POST["idUser"]);
			break;

	case "setBoardConfiguration":
			echo Queries::setBoardConfiguration($c, $_POST["board"], $_POST["idUser"]);
			break;

	case "getConfiguration":
		 	echo Queries::getConfiguration($c, $_POST["idUser"]); // Ready
			break;




	case "getNumOfGamesSaved":
			echo Queries::getNumOfGamesSaved($c, $_POST["idUser"]); // Ready
			break;



	default:
		echo "Option do not exist";
		break;

}

?>