const byte ROWS = 8;                
const byte COLUMNS = 3;                 
const byte row[ROWS] = {4, 5, 6, 7, 8, 9, 10, 11}; 

const byte lineColumns[COLUMNS] = {40, 42, 44}; 

const byte column[ROWS][COLUMNS] = 
{
  {23, 25, 27},
  {29, 31, 33},
  {35, 37, 39},
  {41, 43, 45},
  {47, 49, 51},
  {22, 24, 26},
  {28, 30, 32},
  {34, 36, 38}
}; 

const byte bitColumn = 2;

String str;

String listIn; // Lista de posiciones en donde hay piezas.
String listOut; // Lista de posiciones en donde no hay piezas.

String auxListIn; // Lista de posiciones en donde habia piezas.
String auxListOut; // Lista de posiciones en donde no habia piezas.

String listPut; // Lista de posiciones donde se ha encontrado una nueva pieza.
String listLeft; // Lista de posiciones en donde no se ha encontrado una pieza.


void setup()
{
  Serial.begin(9600);
  
  for (byte r=0; r<ROWS; r++) 
    pinMode(row[r], OUTPUT);

  for (byte c=0; c<COLUMNS; c++) 
    pinMode(lineColumns[c], OUTPUT); 

  for(byte r=0; r<ROWS; r++)
    for(byte c=0; c<COLUMNS; c++)
      pinMode(column[r][c], OUTPUT);

  pinMode(bitColumn, INPUT_PULLUP);
}

void loop()
{
  listIn = "";
  listOut = "";
  listPut = "";
  listLeft = "";
  
  //-------------------------------------------------------------------Fila1[0], 4

  digitalWrite(40, LOW);
  digitalWrite(42, LOW);
  digitalWrite(44, LOW);

  digitalWrite(4, LOW);

  for(int i = 0; i < 8; i++){
      switch(i){
        case 7:
          digitalWrite(23, LOW);
          digitalWrite(25, LOW);
          digitalWrite(27, LOW);
          break;

        case 6:
          digitalWrite(23, HIGH);
          digitalWrite(25, LOW);
          digitalWrite(27, LOW);
          break;

        case 5:
          digitalWrite(23, LOW);
          digitalWrite(25, HIGH);
          digitalWrite(27, LOW);
          break;

        case 4:
          digitalWrite(23, HIGH);
          digitalWrite(25, HIGH);
          digitalWrite(27, LOW);
          break;

        case 3:
          digitalWrite(23, LOW);
          digitalWrite(25, LOW);
          digitalWrite(27, HIGH);
          break;

        case 2:
          digitalWrite(23, HIGH);
          digitalWrite(25, LOW);
          digitalWrite(27, HIGH);
          break;

        case 1:
          digitalWrite(23, LOW);
          digitalWrite(25, HIGH);
          digitalWrite(27, HIGH);
          break;

        case 0:
          digitalWrite(23, HIGH);
          digitalWrite(25, HIGH);
          digitalWrite(27, HIGH);
          break;
      }

      if(digitalRead(2) == LOW)
      {
        listIn += String(i);
        listIn += "0";
      }
      else
      {
        listOut += String(i);
        listOut += "0";
      }
    }

  //-------------------------------------------------------------------Fila2[1], 5

  digitalWrite(40, HIGH);
  digitalWrite(42, LOW);
  digitalWrite(44, LOW);

  digitalWrite(5, LOW);

  for(int i = 0; i < 8; i++){
      switch(i){
        case 7:
          digitalWrite(29, LOW);
          digitalWrite(31, LOW);
          digitalWrite(33, LOW);
          break;

        case 6:
          digitalWrite(29, HIGH);
          digitalWrite(31, LOW);
          digitalWrite(33, LOW);
          break;

        case 5:
          digitalWrite(29, LOW);
          digitalWrite(31, HIGH);
          digitalWrite(33, LOW);
          break;

        case 4:
          digitalWrite(29, HIGH);
          digitalWrite(31, HIGH);
          digitalWrite(33, LOW);
          break;

        case 3:
          digitalWrite(29, LOW);
          digitalWrite(31, LOW);
          digitalWrite(33, HIGH);
          break;

        case 2:
          digitalWrite(29, HIGH);
          digitalWrite(31, LOW);
          digitalWrite(33, HIGH);
          break;

        case 1:
          digitalWrite(29, LOW);
          digitalWrite(31, HIGH);
          digitalWrite(33, HIGH);
          break;

        case 0:
          digitalWrite(29, HIGH);
          digitalWrite(31, HIGH);
          digitalWrite(33, HIGH);
          break;
      }

      if(digitalRead(2) == LOW)
      {
        listIn += String(i);
        listIn += "1";
      }
      else
      {
        listOut += String(i);
        listOut += "1";
      }
    }

  //-------------------------------------------------------------------Fila3[2], 6

  digitalWrite(40, LOW);
  digitalWrite(42, HIGH);
  digitalWrite(44, LOW);

  digitalWrite(6, LOW);

  for(int i = 0; i < 8; i++){
      switch(i){
        case 7:
          digitalWrite(35, LOW);
          digitalWrite(37, LOW);
          digitalWrite(39, LOW);
          break;

        case 6:
          digitalWrite(35, HIGH);
          digitalWrite(37, LOW);
          digitalWrite(39, LOW);
          break;

        case 5:
          digitalWrite(35, LOW);
          digitalWrite(37, HIGH);
          digitalWrite(39, LOW);
          break;

        case 4:
          digitalWrite(35, HIGH);
          digitalWrite(37, HIGH);
          digitalWrite(39, LOW);
          break;

        case 3:
          digitalWrite(35, LOW);
          digitalWrite(37, LOW);
          digitalWrite(39, HIGH);
          break;

        case 2:
          digitalWrite(35, HIGH);
          digitalWrite(37, LOW);
          digitalWrite(39, HIGH);
          break;

        case 1:
          digitalWrite(35, LOW);
          digitalWrite(37, HIGH);
          digitalWrite(39, HIGH);
          break;

        case 0:
          digitalWrite(35, HIGH);
          digitalWrite(37, HIGH);
          digitalWrite(39, HIGH);
          break;
      }

      if(digitalRead(2) == LOW)
      {
        listIn += String(i);
        listIn += "2";
      }
      else
      {
        listOut += String(i);
        listOut += "2";
      }
    }
  
  //-------------------------------------------------------------------Fila4[3], 7

  digitalWrite(40, HIGH);
  digitalWrite(42, HIGH);
  digitalWrite(44, LOW);

  digitalWrite(7, LOW);

  for(int i = 0; i < 8; i++){
      switch(i){
        case 7:
          digitalWrite(41, LOW);
          digitalWrite(43, LOW);
          digitalWrite(45, LOW);
          break;

        case 6:
          digitalWrite(41, HIGH);
          digitalWrite(43, LOW);
          digitalWrite(45, LOW);
          break;

        case 5:
          digitalWrite(41, LOW);
          digitalWrite(43, HIGH);
          digitalWrite(45, LOW);
          break;

        case 4:
          digitalWrite(41, HIGH);
          digitalWrite(43, HIGH);
          digitalWrite(45, LOW);
          break;

        case 3:
          digitalWrite(41, LOW);
          digitalWrite(43, LOW);
          digitalWrite(45, HIGH);
          break;

        case 2:
          digitalWrite(41, HIGH);
          digitalWrite(43, LOW);
          digitalWrite(45, HIGH);
          break;

        case 1:
          digitalWrite(41, LOW);
          digitalWrite(43, HIGH);
          digitalWrite(45, HIGH);
          break;

        case 0:
          digitalWrite(41, HIGH);
          digitalWrite(43, HIGH);
          digitalWrite(45, HIGH);
          break;
      }

      if(digitalRead(2) == LOW)
      {
        listIn += String(i);
        listIn += "3";
      }
      else
      {
        listOut += String(i);
        listOut += "3";
      }
    }

  //-------------------------------------------------------------------Fila5[4], 8

  digitalWrite(40, LOW);
  digitalWrite(42, LOW);
  digitalWrite(44, HIGH);

  digitalWrite(8, LOW);

  for(int i = 0; i < 8; i++){
      switch(i){
        case 7:
          digitalWrite(47, LOW);
          digitalWrite(49, LOW);
          digitalWrite(51, LOW);
          break;

        case 6:
          digitalWrite(47, HIGH);
          digitalWrite(49, LOW);
          digitalWrite(51, LOW);
          break;

        case 5:
          digitalWrite(47, LOW);
          digitalWrite(49, HIGH);
          digitalWrite(51, LOW);
          break;

        case 4:
          digitalWrite(47, HIGH);
          digitalWrite(49, HIGH);
          digitalWrite(51, LOW);
          break;

        case 3:
          digitalWrite(47, LOW);
          digitalWrite(49, LOW);
          digitalWrite(51, HIGH);
          break;

        case 2:
          digitalWrite(47, HIGH);
          digitalWrite(49, LOW);
          digitalWrite(51, HIGH);
          break;

        case 1:
          digitalWrite(47, LOW);
          digitalWrite(49, HIGH);
          digitalWrite(51, HIGH);
          break;

        case 0:
          digitalWrite(47, HIGH);
          digitalWrite(49, HIGH);
          digitalWrite(51, HIGH);
          break;
      }

      if(digitalRead(2) == LOW)
      {
        listIn += String(i);
        listIn += "4";
      }
      else
      {
        listOut += String(i);
        listOut += "4";
      }
    }

  //-------------------------------------------------------------------Fila6[5], 9
  
  digitalWrite(40, HIGH);
  digitalWrite(42, LOW);
  digitalWrite(44, HIGH);

  digitalWrite(9, LOW);

  for(int i = 0; i < 8; i++){
      switch(i){
        case 7:
          digitalWrite(22, LOW);
          digitalWrite(24, LOW);
          digitalWrite(26, LOW);
          break;

        case 6:
          digitalWrite(22, HIGH);
          digitalWrite(24, LOW);
          digitalWrite(26, LOW);
          break;

        case 5:
          digitalWrite(22, LOW);
          digitalWrite(24, HIGH);
          digitalWrite(26, LOW);
          break;

        case 4:
          digitalWrite(22, HIGH);
          digitalWrite(24, HIGH);
          digitalWrite(26, LOW);
          break;

        case 3:
          digitalWrite(22, LOW);
          digitalWrite(24, LOW);
          digitalWrite(26, HIGH);
          break;

        case 2:
          digitalWrite(22, HIGH);
          digitalWrite(24, LOW);
          digitalWrite(26, HIGH);
          break;

        case 1:
          digitalWrite(22, LOW);
          digitalWrite(24, HIGH);
          digitalWrite(26, HIGH);
          break;

        case 0:
          digitalWrite(22, HIGH);
          digitalWrite(24, HIGH);
          digitalWrite(26, HIGH);
          break;
      }

      if(digitalRead(2) == LOW)
      {
        listIn += String(i);
        listIn += "5";
      }
      else
      {
        listOut += String(i);
        listOut += "5";
      }
    }

  //-------------------------------------------------------------------Fila7[6], 10

  digitalWrite(40, LOW);
  digitalWrite(42, HIGH);
  digitalWrite(44, HIGH);

  digitalWrite(10, LOW);

  for(int i = 0; i < 8; i++){
      switch(i){
        case 7:
          digitalWrite(28, LOW);
          digitalWrite(30, LOW);
          digitalWrite(32, LOW);
          break;

        case 6:
          digitalWrite(28, HIGH);
          digitalWrite(30, LOW);
          digitalWrite(32, LOW);
          break;

        case 5:
          digitalWrite(28, LOW);
          digitalWrite(30, HIGH);
          digitalWrite(32, LOW);
          break;

        case 4:
          digitalWrite(28, HIGH);
          digitalWrite(30, HIGH);
          digitalWrite(32, LOW);
          break;

        case 3:
          digitalWrite(28, LOW);
          digitalWrite(30, LOW);
          digitalWrite(32, HIGH);
          break;

        case 2:
          digitalWrite(28, HIGH);
          digitalWrite(30, LOW);
          digitalWrite(32, HIGH);
          break;

        case 1:
          digitalWrite(28, LOW);
          digitalWrite(30, HIGH);
          digitalWrite(32, HIGH);
          break;

        case 0:
          digitalWrite(28, HIGH);
          digitalWrite(30, HIGH);
          digitalWrite(32, HIGH);
          break;
      }

      if(digitalRead(2) == LOW)
      {
        listIn += String(i);
        listIn += "6";
      }
      else
      {
        listOut += String(i);
        listOut += "6";
      }
    }

  //-------------------------------------------------------------------Fila8[7], 11

  digitalWrite(40, HIGH);
  digitalWrite(42, HIGH);
  digitalWrite(44, HIGH);

  digitalWrite(11, LOW);

  for(int i = 0; i < 8; i++){
      switch(i){
        case 7:
          digitalWrite(34, LOW);
          digitalWrite(36, LOW);
          digitalWrite(38, LOW);
          break;

        case 6:
          digitalWrite(34, HIGH);
          digitalWrite(36, LOW);
          digitalWrite(38, LOW);
          break;

        case 5:
          digitalWrite(34, LOW);
          digitalWrite(36, HIGH);
          digitalWrite(38, LOW);
          break;

        case 4:
          digitalWrite(34, HIGH);
          digitalWrite(36, HIGH);
          digitalWrite(38, LOW);
          break;

        case 3:
          digitalWrite(34, LOW);
          digitalWrite(36, LOW);
          digitalWrite(38, HIGH);
          break;

        case 2:
          digitalWrite(34, HIGH);
          digitalWrite(36, LOW);
          digitalWrite(38, HIGH);
          break;

        case 1:
          digitalWrite(34, LOW);
          digitalWrite(36, HIGH);
          digitalWrite(38, HIGH);
          break;

        case 0:
          digitalWrite(34, HIGH);
          digitalWrite(36, HIGH);
          digitalWrite(38, HIGH);
          break;
      }

      if(digitalRead(2) == LOW)
      {
        listIn += String(i);
        listIn += "7";
      }
      else
      {
        listOut += String(i);
        listOut += "7";
      }
    }

  char matrix [8][8];
  
  for(int i = 0; i < 8; i++){
    for(int j = 0; j < 8; j++){
      matrix[i][j] = ' ';
    }
  }
  
  Serial.println("Posiciones ocupadas");
  for(int j = 0; j<listIn.length(); j+=2){
    Serial.print(listIn.charAt(j));
    Serial.print(listIn.charAt(j+1));
    Serial.print("-");
    matrix[listIn.charAt(j) - '0'][listIn.charAt(j+1) - '0'] = 'x';
  }
  Serial.println("");

  Serial.println(" ");
  for(int i = 0; i < 8; i++){
    Serial.println("*---*---*---*---*---*---*---*---*");
    Serial.print("|");
    for(int j = 0; j < 8; j++){
      Serial.print(" ");
      Serial.print(matrix[i][j]);
      Serial.print(" ");
      Serial.print("|");
    }
    Serial.println(" ");
  }
  Serial.println("*---*---*---*---*---*---*---*---*");

  Serial.println("Posiciones no ocupadas");
  for(int k = 0; k<listOut.length(); k+=2){
    Serial.print(listOut.charAt(k));
    Serial.print(listOut.charAt(k+1));
    Serial.print("-");
  }
  Serial.println("");

  Serial.println("Posiciones ocupadas anteriores");
  for(int j = 0; j<auxListIn.length(); j+=2){
    Serial.print(auxListIn.charAt(j));
    Serial.print(auxListIn.charAt(j+1));
    Serial.print("-");
  }
  Serial.println("");

  Serial.println("Posiciones no ocupadas anteriores");
  for(int k = 0; k<auxListOut.length(); k+=2){
    Serial.print(auxListOut.charAt(k));
    Serial.print(auxListOut.charAt(k+1));
    Serial.print("-");
  }
  Serial.println("");

  //===============================================================================================

  String aux = "";
  String aux2 = "";

  for(int j = 0; j<listIn.length(); j+=2)
  {
    aux += listIn.charAt(j);
    aux += listIn.charAt(j+1);

    aux2 = "";

    if(auxListIn.length() == 0)
    {
        listPut += aux;
    }
    else
    {
        for(int k=0; k<auxListIn.length(); k+=2)
        {
            aux2 += auxListIn.charAt(k);
            aux2 += auxListIn.charAt(k+1);
      
            if(aux2 == aux)
            {
              break;
            }
      
            //Serial.print("-");
            //Serial.println(aux2);
      
            if((k+2) >= auxListIn.length())
            {
              listPut += aux;
            }
      
            aux2 = "";
        }
    }

    aux = "";
  }

  Serial.println("Posiciones ocupadas nuevas");
  for(int j = 0; j<listPut.length(); j+=2)
  {
    Serial.print(listPut.charAt(j));
    Serial.print(listPut.charAt(j+1));
    Serial.print("-");
  }
  Serial.println("");

  //- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
  
  aux = "";
  aux2 = "";

  for(int j = 0; j<listOut.length(); j+=2)
  {
    aux += listOut.charAt(j);
    aux += listOut.charAt(j+1);

    aux2 = "";

    for(int k=0; k<auxListOut.length(); k+=2)
    {
      aux2 += auxListOut.charAt(k);
      aux2 += auxListOut.charAt(k+1);

      if(aux2 == aux)
      {
        break;
      }

      if((k+2) >= auxListOut.length())
      {
        listLeft += aux;
      }

      aux2 = "";
    }

    aux = "";
  }

  Serial.println("Posiciones no ocupadas nuevas");
  for(int j = 0; j<listLeft.length(); j+=2)
  {
    Serial.print(listLeft.charAt(j));
    Serial.print(listLeft.charAt(j+1));
    Serial.print("-");
  }
  Serial.println("");

  auxListIn = "";
  auxListIn = listIn;

  auxListOut = "";
  auxListOut = listOut;

  delay(1000);

  Serial.println("=======================================================================");
}
