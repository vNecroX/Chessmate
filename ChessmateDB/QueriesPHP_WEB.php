<?PHP

class Queries
{
	function __construct(){}

	public static function verifyUsername($c, $uN) // In Used
	{
		$query = "SELECT ID_Usuario FROM Usuario WHERE NombreUsuario = ?";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('s', $uN);
				$stmt->execute();
				$stmt->bind_result($x);

				$data = array();

				while($stmt->fetch())
				{
					$temp = [
	                      'res' => $x
		            ];

		            array_push($data, $temp);
				}

				return json_encode(array('data' => $data));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function createAccount($c, $n, $uN, $m, $p) // In Used
	{
		//$query = "CALL createAccount(?, ?, ?, ?, 1, 1, 1, 1)";
		$query = "INSERT INTO Usuario VALUES(0, (SELECT ID_Nacionalidad FROM Nacionalidad WHERE Nombre = ?), ?, ?, ?, 1, 1, 1, 1)";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('ssss', $n, $uN, $m, $p);
				$stmt->execute();

				return json_encode(array('data' => "Done"));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function verifyLogin($c, $uN, $p) // In Used
	{
		//$query = "CALL verifyLogin(?, ?);";
		$query = "SELECT NombreUsuario, ID_Usuario, Correo FROM Usuario WHERE NombreUsuario = ? AND Contrasena = ?";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('ss', $uN, $p);
				$stmt->execute();
				$stmt->bind_result($u, $idU, $m);

				$data = array();

				while($stmt->fetch())
				{
					$temp = [
	                      'userName' => $u,
	                      'idUser' => $idU,
	                      'mail' => $m
		            ];

		            array_push($data, $temp);
				}

				return json_encode(array('data' => $data));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function getIdOponent($c, $oN) // In Used
	{
		//$query = "CALL getIdOfUser(?)";
		$query = "SELECT ID_Usuario FROM Usuario WHERE NombreUsuario = ?";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('s', $oN);
				$stmt->execute();
				$stmt->bind_result($idU);

				$data = array();

				while($stmt->fetch())
				{
					$temp = [
	                      'idUser' => $idU
		            ];

		            array_push($data, $temp);
				}

				return json_encode(array('data' => $data));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function createGame($c, $idM, $idD, $idU, $oN, $idGS, $name, $sL, $color, $colorO, $r) // In Used
	{
		//$query = "CALL createGame(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		$query = "INSERT INTO Partida VALUES(0, ?, ?, ?, (SELECT ID_Usuario FROM Usuario WHERE NombreUsuario = ?), ?, ?, ?, ?, ?, ?, '', '')";

		try
		{
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('iiisisissi', $idM, $idD, $idU, $oN, $idGS, $name, $sL, $color, $colorO, $r);
				$stmt->execute();

				$data = array();

				$temp = [
	                      'idGame' => 0
		            ];

		        array_push($data, $temp);

				return json_encode(array('data' => $data));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function getIDLastGame($c, $idU) // In Used
	{
		//$query = "CALL getIdOfUser(?)";
		$query = "SELECT ID_Partida FROM Partida WHERE ID_Partida = (SELECT MAX(ID_Partida) FROM Partida WHERE ID_Usuario = ?)";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('i', $idU);
				$stmt->execute();
				$stmt->bind_result($idG);

				$data = array();

				while($stmt->fetch())
				{
					$temp = [
	                      'idGame' => $idG
		            ];

		            array_push($data, $temp);
				}

				return json_encode(array('data' => $data));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function addNewMove($c, $sM, $sL, $iG) // In Used
	{
		$query = "UPDATE Partida SET Movimientos = ?, SegundosTranscurridos = ? WHERE ID_Partida = ?";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('sii', $sM, $sL, $iG);
				$stmt->execute();

				return json_encode(array('res' => "Done"));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function setGame($c, $idGS, $sM, $sL, $r, $idG) 
	{
		//$query = "CALL setGame(?, ?, ?, ?)";
		$query = "UPDATE Partida SET ID_EstadoPartida = ?, Movimientos = ?, SegundosTranscurridos = ?, Retrocesos = ? WHERE ID_Partida = ?";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('isiii', $idGS, $sM, $sL, $r, $idG);
				$stmt->execute();

				return json_encode(array('res' => "Done"));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		} //
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function getPlayersList($c, $idU) // In Used
	{
		//$query = "CALL getPlayersList(?)";
		$query = "SELECT Usuario.ID_Usuario, Usuario.NombreUsuario, Nacionalidad.Nombre, Usuario.Notificacion FROM Usuario 
		INNER JOIN Nacionalidad ON Nacionalidad.ID_Nacionalidad = Usuario.ID_Nacionalidad 
		WHERE Usuario.ID_Usuario != ? AND Usuario.NombreUsuario != 'ia'
		AND Usuario.Notificacion = 1";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('i', $idU);
				$stmt->execute();
				$stmt->bind_result($idO, $u, $n, $notif);

				$data = array();

				while($stmt->fetch())
				{
					$temp = [
						  'idOponent' => $idO,
	                      'userName' => $u,
	                      'nation' => $n,
	                      'notif' => $notif
		            ];

		            array_push($data, $temp);
				}

				return json_encode(array('data' => $data), JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function getSavedGames($c, $idU) // In Used
	{
		//$query = "CALL getSavedGames(?)";
		$query = "SELECT Partida.ID_Partida, Partida.Nombre, U1.NombreUsuario, U2.NombreUsuario, Partida.ColorPiezas, Partida.ColorPiezasOponente, 
		Modo.Nombre, Dificultad.Nombre, EstadoPartida.Nombre, Partida.Movimientos FROM Partida 
		INNER JOIN Dificultad ON Dificultad.ID_Dificultad = Partida.ID_Dificultad 
		INNER JOIN Modo ON Modo.ID_Modo = Partida.ID_Modo 
		INNER JOIN Usuario U1 ON U1.ID_Usuario = Partida.ID_Usuario 
		INNER JOIN Usuario U2 ON U2.ID_Usuario = Partida.ID_Oponente 
		INNER JOIN EstadoPartida ON EstadoPartida.ID_EstadoPartida = Partida.ID_EstadoPartida 
		WHERE U1.ID_Usuario = ? ORDER BY Partida.ID_Partida ASC";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('i', $idU);
				$stmt->execute();
				$stmt->bind_result($idG, $nG, $p, $o, $cPP, $cPO, $m, $d, $lS, $moves);

				$data = array();

				while($stmt->fetch())
				{
					$temp = [
	                      'idGame' => $idG, 
	                      'nameGame' => $nG, 
	                      'playerName' => $p, 
	                      'oponentName' => $o, 
	                      'colorPlayer' => $cPP, 
	                      'colorOponent' => $cPO, 
	                      'mode' => $m, 
	                      'difficulty' => $d, 
	                      'lastState' => $lS, 
	                      'moves' => $moves 
		            ];

		            array_push($data, $temp);
				}

				return json_encode(array('data' => $data));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function getSavedGamesInfo($c, $idG) // In Used
	{
		//$query = "CALL getSavedGamesInfo(?)";
		$query = "SELECT ID_Usuario, ID_Oponente FROM Partida WHERE ID_Partida = ?";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('i', $idG);
				$stmt->execute();
				$stmt->bind_result($idU, $idO);

				$data = array();

				while($stmt->fetch())
				{
					$temp = [
	                      'idUser' => $idU,
	                      'idOponent' => $idO
		            ];

		            array_push($data, $temp);
				}

				return json_encode(array('data' => $data));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function setNameOfGame($c, $nG, $idG)
	{
		//$query = "CALL setNameOfGame(?, ?)";
		$query = "UPDATE Partida SET Nombre = ? WHERE ID_Partida = ?";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('si', $nG, $idG);
				$stmt->execute();

				return json_encode(array('data' => "Done"));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function deleteGame($c, $idG)
	{
		//$query = "CALL deleteGame(?)";
		$query = "DELETE FROM Partida WHERE ID_Partida = ?";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('i', $idG);
				$stmt->execute();

				return json_encode(array('data' => "Done"));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function getSavedMoves($c, $iG) // In Used
	{
		//$query = "CALL getSavedMoves(?)";
		$query = "SELECT Movimientos, SegundosTranscurridos, Retrocesos, FI FROM Partida WHERE ID_Partida = ?";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('i', $iG);
				$stmt->execute();
				$stmt->bind_result($sM, $sL, $r, $fi);

				$data = array();

				while($stmt->fetch())
				{
					$temp = [
	                      'stringMove' => $sM,
	                      'secondsLeft' => $sL,
	                      'rewinds' => $r,
	                      'fi' => $fi
		            ];

		            array_push($data, $temp);
				}

				return json_encode(array('data' => $data));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function deleteMoves($c, $sM, $r, $iG)
	{
		//$query = "CALL deleteMoves(?, ?, ?)";
		$query = "UPDATE Partida SET Movimientos = ?, Retrocesos = ? WHERE ID_Partida = ?";

		try
		{
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('sii', $sM, $r, $iG);
				$stmt->execute();

				return json_encode(array('data' => "Done"));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function setPersonalData($c, $n, $uN, $m, $p, $idU) // In Used
	{
		//$query = "CALL setPersonalData(?, ?, ?, ?, ?)";
		$query = "UPDATE Usuario SET ID_Nacionalidad = (SELECT ID_Nacionalidad FROM Nacionalidad WHERE Nombre = ?), 
		NombreUsuario = ?, Correo = ?, Contrasena = ? WHERE ID_Usuario = ?";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('ssssi', $n, $uN, $m, $p, $idU);
				$stmt->execute();

				return json_encode(array('data' => "Done"));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function getPersonalData($c, $idU) // In Used
	{
		//$query = "CALL getPersonalData(?)";
		$query = "SELECT Usuario.NombreUsuario, Usuario.Correo, Usuario.Contrasena, Nacionalidad.Nombre FROM Usuario 
		INNER JOIN Nacionalidad ON Nacionalidad.ID_Nacionalidad = Usuario.ID_Nacionalidad WHERE Usuario.ID_Usuario = ?";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('i', $idU);
				$stmt->execute();
				$stmt->bind_result($uN, $m, $psw, $nation);

				$data = array();

				while($stmt->fetch())
				{
					$temp = [
	                      'userName' => $uN,
	                      'mail' => $m,
	                      'password' => $psw,
	                      'nation' => $nation
		            ];

		            array_push($data, $temp);
				}

				return json_encode(array('data' => $data), JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}
	
	public static function getNations($c) // In Used
	{
		//$query = "CALL getNations()";
		$query = "SELECT Nombre FROM Nacionalidad";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				//$stmt->bind_param('i', $idU);
				$stmt->execute();
				$stmt->bind_result($n);

				$data = array();

				while($stmt->fetch())
				{
					$temp = [
	                      'nation' => $n
		            ];

		            array_push($data, $temp);
				}

				return json_encode(array('data' => $data), JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function checkWifi($c)
	{
		$query = "SELECT * FROM Usuario";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->execute();

				$data = array();

				$temp = [
	                      'conn' => true
		        ];

				array_push($data, $temp);

				return json_encode(array('data' => $data));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	// NEW ONES

	public static function CreateTableChat($c, $tableName)
	{
      	try
      	{
        	$s = "CREATE TABLE $tableName (
        	message_id int PRIMARY KEY auto_increment, 
        	message VARCHAR(50) NOT NULL, 
        	message_type int, message_time VARCHAR(10) NOT NULL)";

        	if (mysqli_query($c, $s))
        	{
             	return 200;
        	}
        	else
        	{
              	return -1;
        	}
      	}
      	catch(PDOException $e)
      	{
        	return -1;
      	}
    }

    public static function SendMessages($c, $tableName, $message, $message_type, $message_time)
    {
      	try
      	{
        	$s = mysqli_prepare($c, "INSERT INTO $tableName (message, message_type, message_time) VALUES (?,?,?)");
        	mysqli_stmt_bind_param($s, "sis", $message, $message_type, $message_time);
        	mysqli_stmt_execute($s);

        	if($s)
        	{
          		if($message_type == 1)
          		{
             		$data = array();
              		$data["message_time"] = $message_time;
              		echo json_encode(array('data' => $data));
          		}
        	}
      	}
      	catch(PDOException $e)
      	{
          	return -1;
      	}
    }

    public static function SeeAllMessages($c, $tableName)
    {
      	try
      	{
        	$s = "SELECT message, message_type, message_time FROM $tableName";
       	 	$stmt = $c->prepare($s);
        	$stmt->execute();
        	$stmt->bind_result($message, $message_type, $message_time);

        	$data = array();

          	while($stmt->fetch())
          	{
            	$temp = [
                      'message' => $message,
                      'message_type' => $message_type,
                      'message_time' => $message_time
            	];

            	array_push($data, $temp);
          	}

          	return json_encode(array('data' => $data));
      	}
      	catch(PDOException $e)
      	{
        	return -1;
      	}
    }

    public static function UnseenMessages($c, $tableName)
    {
      	try
      	{
        	$s = "SELECT message_id, message_type FROM $tableName order by message_id DESC LIMIT 1";
        	$stmt = $c->prepare($s);
        	$stmt->execute();
        	$stmt->bind_result($message_id, $message_type);

        	$data = array();

          	while($stmt->fetch())
          	{
            	$temp = [
                      	'message_id' => $message_id,
                      	'message_type' => $message_type
            	];

            	array_push($data, $temp);
          	}

          	return json_encode(array('data' => $data));
      	}
      	catch(PDOException $e)
      	{
        	return -1;
      	}
    }

    public static function DeleteMessages($c, $tableName)
    {
      	try
      	{
        	$s = "DELETE FROM $tableName";
        	$stmt = $c->prepare($s);

        	if($stmt->execute())
        	{
          		return "Messages Deleted Succesfully!.";
        	}
        	else
        	{
          		return "Hnmm There's A Problem deleting messages. . .";
        	}
      	}
      	catch(PDOException $e)
      	{
        	return -1;
      	}
    }

    public static function DropTable($c, $tableName)
    {
      	try
      	{
        	$s = "DROP TABLE $tableName";
        	$stmt = $c->prepare($s);

        	if($stmt->execute())
        	{
          		return "Table Deleted Succesfully!.";
        	}
        	else
        	{
          		return "Hnmm There's A Problem dropping table. . .";
        	}
      	}
      	catch(PDOException $e)
      	{
        	return -1;
      	}
    }

    //////////////////////////////////////////////////////////////
    /// NEW ONES

    public static function setAllConfiguration($c, $autos, $board, $chat, $notif, $idU)
	{
		//$query = "CALL setConfiguration(?, ?, ?, ?, ?)";
		$query = "UPDATE Usuario SET GuardadoAuto = ?, Tablero = ?, Chat = ?, Notificacion = ? WHERE ID_Usuario = ?";

		try
		{	
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('iiiii', $autos, $board, $chat, $notif, $idU);
				$stmt->execute();

				return json_encode(array('data' => "Done"));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

    public static function setBoardConfiguration($c, $board, $idU)
	{
		$query = "UPDATE Usuario SET Tablero = ? WHERE ID_Usuario = ?";

		try
		{
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('ii', $board, $idU);
				$stmt->execute();

				return json_encode(array('data' => "Done"));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

	public static function getConfiguration($c, $idU)
	{
		$query = "SELECT GuardadoAuto, Tablero, Chat, Notificacion FROM Usuario WHERE ID_Usuario = ?";

		try
		{
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('i', $idU);
				$stmt->execute();
				$stmt->bind_result($autos, $board, $chat, $notif);

				$data = array();

				while($stmt->fetch())
				{
					$temp = [
	                      'autos' => $autos,
	                      'board' => $board,
	                      'chat' => $chat,
	                      'notif' => $notif
		            ];

		            array_push($data, $temp);
				}

				echo json_encode(array('data' => $data));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}


	///////////////////////////////////////

	public static function getNumOfGamesSaved($c, $idU)
	{
		$query = "SELECT Count(*) FROM Partida WHERE ID_Usuario = ?";

		try
		{
			if($stmt = $c->prepare($query))
			{
				$stmt->bind_param('i', $idU);
				$stmt->execute();
				$stmt->bind_result($numG);

				$data = array();

				while($stmt->fetch())
				{
					$temp = [
	                      'numGames' => $numG
		            ];

		            array_push($data, $temp);
				}

				echo json_encode(array('data' => $data));
			}
			else
			{
				echo "Error in the query";
			}

			$c->close();
		}
		catch(Exception $e)
		{
			return "Exception: " . $e->getMessage();
		}
	}

}

?>