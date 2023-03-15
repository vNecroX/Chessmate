/*

	SCRIPT BASE DE DATOS >>> CHESSMATE

	Devs:
	
		Esteban Quintero 	
		Jorge Davalos

	Utima fecha de Actualizacion:

		21 de Octubre del 2019 
*/

/*
	Estado de juego:

		n = nulo					<- 1ra/2da Accion o Consecuencia
		K,Q,B,I,T,P = comer 		<- 1ra Accion

		e = enroque         		<- 2da Accion
		p = promocion   			<- 2da Accion

		j = jaque 					<- Consecuencia
		k = jaque mate 				<- Consecuencia
		l = drawmat					<- Consecuencia
		m = drawmov					<- Consecuencia

	Pieza:

		K = rey
		Q = reina
		B = alfil
		N = caballo
		R = torre
		P = peon
		  = nulo					<- Cuando no hay pieza en tal posicion

	Formato:

		Movimientos
		@2TD5_D8nn@1PA3_A5nn...

		FI:
		@01@03@33@42....

		MovimientoIlegal	(0)
		PiezasMovidas		(1)
		ConexionFallida		(2)
		Otras				(3)
*/

# <<<<<<<<<<<<< CREACION DE BASE DE DATOS >>>>>>>>>>>>>>>>

#DROP DATABASE ChessmateDB;
#CREATE DATABASE ChessmateDB;
#USE ChessmateDB;

# <<<<<<<<<<<<< CREACION DE TABLAS >>>>>>>>>>>>>>>>

CREATE TABLE Dificultad(
  ID_Dificultad smallint PRIMARY KEY AUTO_INCREMENT NOT NULL,
  Nombre varchar(15)
);

CREATE TABLE Modo(
  ID_Modo smallint PRIMARY KEY AUTO_INCREMENT NOT NULL,
  Nombre varchar(20)
);

CREATE TABLE Nacionalidad(
  ID_Nacionalidad int PRIMARY KEY AUTO_INCREMENT NOT NULL,
  Nombre varchar(30),
  ISO varchar(4)
);

CREATE TABLE EstadoPartida(
  ID_EstadoPartida smallint PRIMARY KEY AUTO_INCREMENT NOT NULL,
  Nombre varchar(20)
);

CREATE TABLE Usuario(
  ID_Usuario int PRIMARY KEY AUTO_INCREMENT NOT NULL,
  ID_Nacionalidad int,
  NombreUsuario varchar(14),
  Correo varchar(40),
  Contrasena varchar(14),
  GuardadoAuto boolean,
  Tablero boolean,
  Chat boolean,
  Notificacion boolean,
  FOREIGN KEY(ID_Nacionalidad) REFERENCES Nacionalidad(ID_Nacionalidad) ON DELETE CASCADE
);

CREATE TABLE Partida(
  ID_Partida int PRIMARY KEY AUTO_INCREMENT NOT NULL,
  ID_Modo smallint,
  ID_Dificultad smallint,
  ID_Usuario int,
  ID_Oponente int,
  ID_EstadoPartida smallint,
  Nombre varchar(30),
  SegundosTranscurridos bigint,
  ColorPiezas char(1),
  ColorPiezasOponente char(1),
  Retrocesos smallint,
  Movimientos varchar(2000),
  FI varchar(800),
  FOREIGN KEY(ID_Dificultad) REFERENCES Dificultad(ID_Dificultad) ON DELETE CASCADE,
  FOREIGN KEY(ID_Modo) REFERENCES Modo(ID_Modo) ON DELETE CASCADE,
  FOREIGN KEY(ID_Usuario) REFERENCES Usuario(ID_Usuario) ON DELETE CASCADE,
  FOREIGN KEY(ID_Oponente) REFERENCES Usuario(ID_Usuario) ON DELETE CASCADE,
  FOREIGN KEY(ID_EstadoPartida) REFERENCES EstadoPartida(ID_EstadoPartida) ON DELETE CASCADE
);


# <<<<<<<<<<<<< CREACION DE REGISTROS >>>>>>>>>>>>>>>>

INSERT INTO Nacionalidad (Nombre, ISO) VALUES
("Afganistán", "AFG"),
("Albania", "ALB"),
("Alemania", "DEU"),
("Andorra", "AND"),
("Angola", "AGO"),
("AntiguayBarbuda", "ATG"),
("ArabiaSaudita", "SAU"),
("Argelia", "DZA"),
("Argentina", "ARG"),
("Armenia", "ARM"),
("Aruba", "ABW"),
("Australia", "AUS"),
("Austria", "AUT"),
("Azerbaiyán", "AZE"),
("Bahamas", "BHS"),
("Bangladés", "BGD"),
("Barbados", "BRB"),
("Baréin", "BHR"),
("Bélgica", "BEL"),
("Belice", "BLZ"),
("Benín", "BEN"),
("Bielorrusia", "BLR"),
("Birmania", "MMR"),
("Bolivia", "BOL"),
("BosniayHerzegovina", "BIH"),
("Botsuana", "BWA"),
("Brasil", "BRA"),
("Brunéi", "BRN"),
("Bulgaria", "BGR"),
("BurkinaFaso", "BFA"),
("Burundi", "BDI"),
("Bután", "BTN"),
("CaboVerde", "CPV"),
("Camboya", "KHM"),
("Camerún", "CMR"),
("Canadá", "CAN"),
("Catar", "QAT"),
("Chad", "TCD"),
("Chile", "CHL"),
("China", "CHN"),
("Chipre", "CYP"),
("CiudaddelVaticano", "VAT"),
("Colombia", "COL"),
("Comoras", "COM"),
("CoreadelNorte", "PRK"),
("CoreadelSur", "KOR"),
("CostadeMarfil", "CIV"),
("CostaRica", "CRI"),
("Croacia", "HRV"),
("Cuba", "CUB"),
("Dinamarca", "DNK"),
("Dominica", "DMA"),
("Ecuador", "ECU"),
("Egipto", "EGY"),
("ElSalvador", "SLV"),
("EmiratosÁrabesUnidos", "ARE"),
("Eritrea", "ERI"),
("Eslovaquia", "SVK"),
("Eslovenia", "SVN"),
("España", "ESP"),
("EstadosUnidos", "USA"),
("Estonia", "EST"),
("Etiopía", "ETH"),
("Filipinas", "PHL"),
("Finlandia", "FIN"),
("Fiyi", "FJI"),
("Francia", "FRA"),
("Gabón", "GAB"),
("Gambia", "GMB"),
("Georgia", "GEO"),
("Gibraltar", "GIB"),
("Ghana", "GHA"),
("Granada", "GRD"),
("Grecia", "GRC"),
("Groenlandia", "GRL"),
("Guatemala", "GTM"),
("Guineaecuatorial", "GNQ"),
("Guinea", "GIN"),
("Guinea-Bisáu", "GNB"),
("Guyana", "GUY"),
("Haití", "HTI"),
("Honduras", "HND"),
("Hungría", "HUN"),
("India", "IND"),
("Indonesia", "IDN"),
("Irak", "IRQ"),
("Irán", "IRN"),
("Irlanda", "IRL"),
("Islandia", "ISL"),
("IslasCook", "COK"),
("IslasMarshall", "MHL"),
("IslasSalomón", "SLB"),
("Israel", "ISR"),
("Italia", "ITA"),
("Jamaica", "JAM"),
("Japón", "JPN"),
("Jordania", "JOR"),
("Kazajistán", "KAZ"),
("Kenia", "KEN"),
("Kirguistán", "KGZ"),
("Kiribati", "KIR"),
("Kuwait", "KWT"),
("Laos", "LAO"),
("Lesoto", "LSO"),
("Letonia", "LVA"),
("Líbano", "LBN"),
("Liberia", "LBR"),
("Libia", "LBY"),
("Liechtenstein", "LIE"),
("Lituania", "LTU"),
("Luxemburgo", "LUX"),
("Madagascar", "MDG"),
("Malasia", "MYS"),
("Malaui", "MWI"),
("Maldivas", "MDV"),
("Malí", "MLI"),
("Malta", "MLT"),
("Marruecos", "MAR"),
("Martinica", "MTQ"),
("Mauricio", "MUS"),
("Mauritania", "MRT"),
("México", "MEX"),
("Micronesia", "FSM"),
("Moldavia", "MDA"),
("Mónaco", "MCO"),
("Mongolia", "MNG"),
("Montenegro", "MNE"),
("Mozambique", "MOZ"),
("Namibia", "NAM"),
("Nauru", "NRU"),
("Nepal", "NPL"),
("Nicaragua", "NIC"),
("Níger", "NER"),
("Nigeria", "NGA"),
("Noruega", "NOR"),
("NuevaZelanda", "NZL"),
("Omán", "OMN"),
("PaísesBajos", "NLD"),
("Pakistán", "PAK"),
("Palaos", "PLW"),
("Palestina", "PSE"),
("Panamá","PAN"),
("PapúaNuevaGuinea", "PNG"),
("Paraguay", "PRY"),
("Perú", "PER"),
("Polonia", "POL"),
("Portugal", "PRT"),
("PuertoRico", "PRI"),
("ReinoUnido", "GBR"),
("RepúblicaCentroafricana", "CAF"),
("RepúblicaCheca", "CZE"),
("RepúblicadeMacedonia", "MKD"),
("RepúblicadelCongo", "COG"),
("RepúblicaDemocráticadelCongo", "COD"),
("RepúblicaDominicana", "DOM"),
("RepúblicaSudafricana", "ZAF"),
("Ruanda", "RWA"),
("Rumanía", "ROU"),
("Rusia", "RUS"),
("Samoa", "WSM"),
("SanCristóbalyNieves", "KNA"),
("SanMarino", "SMR"),
("SanVicenteylasGranadinas", "VCT"),
("SantaLucía", "LCA"),
("SantoToméyPríncipe", "STP"),
("Senegal", "SEN"),
("Serbia", "SRB"),
("Seychelles", "SYC"),
("SierraLeona", "SLE"),
("Singapur", "SGP"),
("Siria", "SYR"),
("Somalia", "SOM"),
("SriLanka", "LKA"),
("Suazilandia", "SWZ"),
("SudándelSur", "SSD"),
("Sudán", "SDN"),
("Suecia", "SWE"),
("Suiza", "CHE"),
("Surinam", "SUR"),
("Tailandia", "THA"),
("Tanzania", "TZA"),
("Tayikistán", "TJK"),
("TimorOriental", "TLS"),
("Togo", "TGO"),
("Tonga", "TON"),
("TrinidadyTobago", "TTO"),
("Túnez", "TUN"),
("Turkmenistán","TKM"),
("Turquía", "TUR"),
("Tuvalu", "TUV"),
("Ucrania", "UKR"),
("Uganda", "UGA"),
("Uruguay", "URY"),
("Uzbekistán", "UZB"),
("Vanuatu", "VUT"),
("Venezuela", "VEN"),
("Vietnam", "VNM"),
("Yemen", "YEM"),
("Yibuti", "DJI"),
("Zambia", "ZMB"),
("Zimbabue", "ZWE");

INSERT INTO Dificultad (Nombre) VALUES
("Facil"),
("Intermedio"),
("Dificil"),
("Ninguna");

INSERT INTO Modo (Nombre) VALUES
("pvplocal"),
("pvponline"),
("pvia");

INSERT INTO EstadoPartida (Nombre) VALUES
("Pausada"),
("Finalizada"),
("Interrumpida"),
("En Juego");

INSERT INTO Usuario (ID_Nacionalidad, NombreUsuario, Correo, Contrasena, GuardadoAuto, Tablero, Chat, Notificacion) VALUES
(122, "ia", "ia", "ia", 1, 1, 1, 1),
(122, "jorge26", "jorge@gmail.com", "jorgepsw", 1, 1, 1, 1),
(122, "juan26", "juan@gmail.com", "juanpsw", 1, 1, 1, 1),
(122, "jair26", "jair@gmail.com", "jairpsw", 1, 1, 1, 1),
(122, "gerardo26", "gerardo@gmail.com", "gerardopsw", 1, 1, 1, 1);
