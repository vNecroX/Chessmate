const int stepPinY = A3; //ORANGE
const int dirPinY = A4;  //RED
const int enPinY = A5; //BROWN

class MotorY
{  
  private:

    int originY = 3000; 
  
    int one = 2400;  
    int two = 1600;  
    int three = 800; 
    int four = 0;
    int five = 800;
    int six = 1600;
    int seven = 2400;
    int eight = 3200;

    String lastCoordY = "";
    int howMuchSquaresY = 0;
    int stepsPerSquareY = 0;
    boolean directionCoordY = false;

    boolean isLastPositionY = true;

    boolean isRetToCenterY = false;

    boolean isEatingY = false;

  public :

    MotorY()
    {
    
    }

    void initializeMotorY()
    {
      pinMode(stepPinY, OUTPUT);
      pinMode(dirPinY, OUTPUT);
      pinMode(enPinY, OUTPUT);

      digitalWrite(enPinY, LOW);

      originToCenterPositionY();
    }

    void originToCenterPositionY()
    {
      Serial.println("Carrying MotorY To Center. . .");
      digitalWrite(dirPinY, HIGH);
      moveMotorY(originY);
    }

    void centerToOriginPositionY()
    {
      isEatingY = false;
      
      Serial.println("Carrying MotorY To Origin. . .");
      digitalWrite(dirPinY, LOW);
      moveMotorY(originY);
    }

    void firstPosition(String coordY)
    {
      howMuchSquaresY = 0;
      
      isLastPositionY = false;
      isRetToCenterY = false;
      
      delay(1000);

      if(coordY == "1" || coordY == "2" || 
         coordY == "3" || coordY == "4")
      {
        directionCoordY = false;

        if(coordY == "1")
        {
          moveMotorY(one);
        }
    
        if(coordY == "2")
        {
          moveMotorY(two);
        }

        if(coordY == "3")
        {
          moveMotorY(three);
        }

        if(coordY == "4")
        {
          moveMotorY(four);
        }
      }

      if(coordY == "5" || coordY == "6" ||
         coordY == "7" || coordY == "8")
      {
        directionCoordY = true;

        if(coordY == "5")
        {
          moveMotorY(five);
        }
    
        if(coordY == "6")
        {
          moveMotorY(six);
        }

        if(coordY == "7")
        {
          moveMotorY(seven);
        }

        if(coordY == "8")
        {
          moveMotorY(eight);
        }
      } 

      lastCoordY = coordY; 
    }

    void lastPosition(String coordY)
    {
      howMuchSquaresY = 0;
      
      isLastPositionY = true;
      isRetToCenterY = false;

      delay(1000);
      switchLastMoveY(coordY);
    }

    void currentToCenterPositionY(String coordY)
    {
      isLastPositionY = true;
      isRetToCenterY = true;
      isEatingY = false;
      
      Serial.println("Carrying MotorY To Center, Waitin' For The Next Move. . .");

      delay(1000);
      switchLastMoveY(coordY);
    }

    void eatenPieceY(String coordY)
    {
      howMuchSquaresY = 0;
      
      isLastPositionY = false;
      isRetToCenterY = false;
      Serial.println("EatinY'. . .");
      
      delay(1000);

      if(coordY == "1" || coordY == "2" || 
         coordY == "3" || coordY == "4")
      {
        directionCoordY = false;

        if(coordY == "1")
        {
          moveMotorY(one);
        }
    
        if(coordY == "2")
        {
          moveMotorY(two);
        }

        if(coordY == "3")
        {
          moveMotorY(three);
        }

        if(coordY == "4")
        {
          moveMotorY(four);
        }
      }

      if(coordY == "5" || coordY == "6" ||
         coordY == "7" || coordY == "8")
      {
        directionCoordY = true;

        if(coordY == "5")
        {
          moveMotorY(five);
        }
    
        if(coordY == "6")
        {
          moveMotorY(six);
        }

        if(coordY == "7")
        {
          moveMotorY(seven);
        }

        if(coordY == "8")
        {
          moveMotorY(eight);
        }
      } 

      lastCoordY = coordY; 
    }

    void eaterPieceY(String coordY)
    {
      howMuchSquaresY = 0;
      
      isLastPositionY = true;
      isRetToCenterY = false;

      delay(1000);
      switchLastMoveY(coordY);
    }

    void eaterToEatenY(String coordY)
    {
      isLastPositionY = false;
      isRetToCenterY = false;
      isEatingY = false;

      delay(1000);
      switchLastMoveY(coordY);
    }

    void switchLastMoveY(String coordY)
    {
      int parsedCoordY = coordY.toInt();
      int parsedLastCoordY = lastCoordY.toInt();

      Serial.print("j4j4: howMuchSquaresY: ");
      Serial.println(String(howMuchSquaresY));

      if(!isRetToCenterY)
      {
        if(!isEatingY)
        {
          if(howMuchSquaresY == 0)
          {
            howMuchSquaresY = parsedLastCoordY - parsedCoordY;
          }
          Serial.println("!isEatingY");
          Serial.print("jiji: howMuchSquaresY: ");
          Serial.println(String(howMuchSquaresY));
        }
        else
        {
          howMuchSquaresY -= parsedCoordY;
          Serial.println("isEatingY");
          Serial.print("jojo: howMuchSquaresY: ");
          Serial.println(String(howMuchSquaresY));
        }
      }
      else
      {
        howMuchSquaresY = parsedCoordY - 4;
        Serial.println("isEatingY");
        Serial.print("juju: howMuchSquaresY: ");
        Serial.println(String(howMuchSquaresY));
      }

      Serial.print("jsjs: howMuchSquaresY: ");
      Serial.println(String(howMuchSquaresY));

      if(howMuchSquaresY < 0) //At current point to up
      {
        digitalWrite(dirPinY, LOW);
        howMuchSquaresY *= -1;
        if(!isEatingY)
        {
          stepsPerSquareY = howMuchSquaresY * 800;
        }
        //stepsPerSquareY -= 200;
        moveMotorY(stepsPerSquareY);
      }

      else if(howMuchSquaresY == 0)  //No need to move Y
      {
        Serial.println("No need to move Y axis.");
      }

      else if(howMuchSquaresY > 0)  //At current point to down
      {
        digitalWrite(dirPinY, HIGH);
        if(!isEatingY)
        {
          stepsPerSquareY = howMuchSquaresY * 800;
        }
        //HBstepsPerSquareY -= 200;
        moveMotorY(stepsPerSquareY);
      }
    }

    void moveMotorY(int stepsY)
    {
      if(!isLastPositionY)
      {
        if(directionCoordY)
        {
          digitalWrite(dirPinY, LOW);
        }
        else
        {
          digitalWrite(dirPinY, HIGH);
        }
      }
      
      for(int y = 0; y < stepsY; y++)
      {
        digitalWrite(stepPinY, HIGH);
        delay(1);
        digitalWrite(stepPinY, LOW);
        delay(1);
      }

      delay(1000);
    }
};
