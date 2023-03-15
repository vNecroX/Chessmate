const int stepPinX = A0; //PURPLE
const int dirPinX = A1;  //GRAY
const int enPinX = A2;  //WHITE

class MotorX
{  
  private:

    int originX = 2800; 
      
    int A = 0;
    int B = 800; 
    int C = 1600;  
    int D = 2400;
    int E = 3200;
    int F = 4000;
    int G = 4800;
    int H = 5600;

    int lastCoordValueX = 0;
    int auxLastCoordValX = 0;
    int howMuchSquaresX = 0;
    int auxHowMuchSqX = 0;
    
    boolean directionCoordX = true;

    boolean isLastPositionX = false;

    boolean isRetToCenterX = false;

  public :

    MotorX()
    {
    
    }

    void initializeMotorX()
    {
      pinMode(stepPinX, OUTPUT);
      pinMode(dirPinX, OUTPUT);
      pinMode(enPinX, OUTPUT);

      digitalWrite(enPinX, LOW);

      originToCenterPositionX();
    }

    void originToCenterPositionX()
    {
      isLastPositionX = true;

      digitalWrite(dirPinX, HIGH);
      
      Serial.println("Carrying MotorX To Center. . .");
      moveMotorX(originX);
    }

    void finalizeMotorX()
    {
      isLastPositionX = true;

      digitalWrite(dirPinX, LOW);
      
      centerToOriginPositionX();
    }

    void centerToOriginPositionX()
    { 
      Serial.println("Carrying MotorX To Origin. . .");

      delay(1000);
      moveMotorX(originX);
    }

    void firstPosition(String coordX)
    {
      howMuchSquaresX = 0;

      isLastPositionX = false;
      isRetToCenterX = false;
      
      delay(1000);

      if(coordX == "A" || coordX == "B" || 
         coordX == "C" || coordX == "D")
      {
        directionCoordX = false;

        if(coordX == "A")
        {
          howMuchSquaresX = originX - A;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = A;
        }
        
        if(coordX == "B")
        {
          howMuchSquaresX = originX - B;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = B;
        }

        if(coordX == "C")
        {
          howMuchSquaresX = originX - C;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = C;
        }

        if(coordX == "D")
        {
          howMuchSquaresX = originX - D;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = D;
        }
      }

      if(coordX == "E" || coordX == "F" ||
         coordX == "G" || coordX == "H")
      {
        directionCoordX = true;

        if(coordX == "E")
        {
          howMuchSquaresX = originX - E;
          howMuchSquaresX *= -1;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = E;
        }
      
        if(coordX == "F")
        {
          howMuchSquaresX = originX - F;
          howMuchSquaresX *= -1;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = F;
        }

        if(coordX == "G")
        {
          howMuchSquaresX = originX - G;
          howMuchSquaresX *= -1;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = G;
        }

        if(coordX == "H")
        {
          howMuchSquaresX = originX - H;
          howMuchSquaresX *= -1;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = H;
        }
      } 
    }

    void lastPosition(String coordX)
    {
      howMuchSquaresX = 0;

      auxLastCoordValX = 0;

      isLastPositionX = true;
      isRetToCenterX = false;

      delay(1000);

      switchLastMoveX(coordX);
    }

    void currentToCenterPositionX(String coordX)
    {
      isLastPositionX = true;
      isRetToCenterX = true;
      
      Serial.println("Carrying MotorX To Center, Waitin' For The Next Move. . .");

      delay(1000);
      
      switchLastMoveX(coordX);
    }

    void eatenPieceX(String coordX)
    {
      howMuchSquaresX = 0;

      isLastPositionX = false;
      isRetToCenterX = false;

      Serial.println("EatinX'. . .");
      
      delay(1000);

      if(coordX == "A" || coordX == "B" || 
         coordX == "C" || coordX == "D")
      {
        directionCoordX = false;

        if(coordX == "A")
        {
          howMuchSquaresX = originX - A;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = A;
        }
        
        if(coordX == "B")
        {
          howMuchSquaresX = originX - B;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = B;
        }

        if(coordX == "C")
        {
          howMuchSquaresX = originX - C;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = C;
        }

        if(coordX == "D")
        {
          howMuchSquaresX = originX - D;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = D;
        }
      }

      if(coordX == "E" || coordX == "F" ||
         coordX == "G" || coordX == "H")
      {
        directionCoordX = true;

        if(coordX == "E")
        {
          howMuchSquaresX = originX - E;
          howMuchSquaresX *= -1;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = E;
        }
      
        if(coordX == "F")
        {
          howMuchSquaresX = originX - F;
          howMuchSquaresX *= -1;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = F;
        }

        if(coordX == "G")
        {
          howMuchSquaresX = originX - G;
          howMuchSquaresX *= -1;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = G;
        }

        if(coordX == "H")
        {
          howMuchSquaresX = originX - H;
          howMuchSquaresX *= -1;
          moveMotorX(howMuchSquaresX);
          lastCoordValueX = H;
        }
      } 
    }

    void setToEdgeX()
    {
      Serial.println("Si entra el STUPID");
      
      Serial.print("AVEAVER: lastCoordValueX: ");
      Serial.println(lastCoordValueX);

      auxLastCoordValX = lastCoordValueX;
        
      if(lastCoordValueX <= originX)
      {
        lastCoordValueX -= A;
      }
      
      if(lastCoordValueX >= originX)
      {
        lastCoordValueX -= H;
        lastCoordValueX *= -1;
      }
      
      moveMotorX(lastCoordValueX);

      if(directionCoordX)
      {
        directionCoordX = false;
      }
      else
      {
        directionCoordX = true;
      }

      moveMotorX(lastCoordValueX);

      Serial.println("Piece Defeated!");
    }

    void eaterPieceX(String coordX)
    {
      Serial.println("E4TER!");
      howMuchSquaresX = 0;

      isLastPositionX = true;
      isRetToCenterX = false;

      delay(1000);
      switchLastMoveX(coordX);
    }

    void eaterToEatenX(String coordX)
    {

      delay(1000);

      Serial.print("jiji: howMuchSquaresX: ");
      Serial.println(String(howMuchSquaresX));
      Serial.print("jiji: lastCoordValueX: ");
      Serial.println(String(lastCoordValueX));
      Serial.print("jiji: dirPinX: ");
      Serial.println(String(dirPinX));

      if(howMuchSquaresX < lastCoordValueX) 
      {
        Serial.println("Si entra el PENDEJO");

        Serial.print("jojo: howMuchSquaresX: ");
        Serial.println(howMuchSquaresX);
        Serial.print("jojo: lastCoordValueX: ");
        Serial.println(lastCoordValueX);
        Serial.print("jojo: dirPinX: ");
        Serial.println(dirPinX);

        if(lastCoordValueX <= originX)
        {
          if(auxHowMuchSqX > 0)
          {
            Serial.println("HnmmLOW1");
            digitalWrite(dirPinX, HIGH);
          }
          else
          {
            Serial.println("HnmmHIGH1");
            digitalWrite(dirPinX, LOW);
          }
        }
        
        if(lastCoordValueX >= originX)
        {
          if(auxHowMuchSqX > 0)
          {
            Serial.println("HnmmHIGH2");
            digitalWrite(dirPinX, LOW);
          }
          else
          {
            Serial.println("HnmmLOW2");
            digitalWrite(dirPinX, HIGH);
          }
        }
        
        //howMuchSquaresX *= -1;
        Serial.println(String(howMuchSquaresX) + "!");

        for(int x = 0; x < howMuchSquaresX; x++)
        {
          digitalWrite(stepPinX, HIGH);
          delay(1);
          digitalWrite(stepPinX, LOW);
          delay(1);
        }
  
        delay(1000);
      }
      else if(howMuchSquaresX == lastCoordValueX)  //No need to move X
      {
        Serial.println("No need to move X axis.");
      }
      else if(howMuchSquaresX > lastCoordValueX)  
      {
        Serial.println("Si entra el puto");

        Serial.print("jaja: howMuchSquaresX: "); 
        Serial.println(howMuchSquaresX);
        Serial.print("jaja: lastCoordValueX: ");
        Serial.println(lastCoordValueX);
        Serial.print("jaja: dirPinX: ");
        Serial.println(dirPinX);
        
        if(lastCoordValueX <= originX) //H TO A
        {
          if(auxHowMuchSqX > 0)
          {
            Serial.println("HnmmHIGH3");
            digitalWrite(dirPinX, HIGH);
          }
          else
          {
            Serial.println("HnmmLOW3");
            digitalWrite(dirPinX, LOW);
          }
        }
        
        if(lastCoordValueX >= originX) //H TO A
        {
          if(auxHowMuchSqX > 0)
          {
            Serial.println("HnmmLOW4");
            digitalWrite(dirPinX, LOW);
          }
          else
          {
            Serial.println("HnmmHIGH4");
            digitalWrite(dirPinX, HIGH);
          }
        }
        
        for(int x = 0; x < howMuchSquaresX; x++)
        {
          digitalWrite(stepPinX, HIGH);
          delay(1);
          digitalWrite(stepPinX, LOW);
          delay(1);
        }
  
        delay(1000);
      } 
    }

    void switchLastMoveX(String coordX) 
    {
      if(coordX.equals("A"))
      {
        if(!isRetToCenterX)
        {
          if(auxLastCoordValX != 0)
          {
            howMuchSquaresX = auxLastCoordValX - A;
          }
          else
          {
            howMuchSquaresX = lastCoordValueX - A;
          }
        }
        else
        {
          howMuchSquaresX = A - originX;
        }
      }

      if(coordX.equals("B"))
      {
        if(!isRetToCenterX)
        {
          if(auxLastCoordValX != 0)
          {
            howMuchSquaresX = auxLastCoordValX - B;
          }
          else
          {
            howMuchSquaresX = lastCoordValueX - B;
          }
        }
        else
        {
          howMuchSquaresX = B - originX;
        }
      }

      if(coordX.equals("C"))
      {
        if(!isRetToCenterX)
        {
          if(auxLastCoordValX != 0)
          {
            howMuchSquaresX = auxLastCoordValX - C;
          }
          else
          {
            howMuchSquaresX = lastCoordValueX - C;
          }
        }
        else
        {
          howMuchSquaresX = C - originX;
        }
      }

      if(coordX.equals("D"))
      {
        if(!isRetToCenterX)
        {
          Serial.println("DDDNORET");
          if(auxLastCoordValX != 0)
          {
            howMuchSquaresX = auxLastCoordValX - D;
          }
          else
          {
            howMuchSquaresX = lastCoordValueX - D;
          }
        }
        else
        {
          Serial.println("DDDRET");
          howMuchSquaresX = D - originX;
        }
      }

      if(coordX.equals("E"))
      {
        if(!isRetToCenterX)
        {
          if(auxLastCoordValX != 0)
          {
            howMuchSquaresX = auxLastCoordValX - E;
          }
          else
          {
            howMuchSquaresX = lastCoordValueX - E;
          }
        }
        else
        {
          howMuchSquaresX = E - originX;
        }
      }

      if(coordX.equals("F"))
      {
        if(!isRetToCenterX)
        {
          if(auxLastCoordValX != 0)
          {
            howMuchSquaresX = auxLastCoordValX - F;
          }
          else
          {
            howMuchSquaresX = lastCoordValueX - F;
            Serial.println("isLastF?");
          }
        }
        else
        {
          howMuchSquaresX = F - originX;
        }
      }

      if(coordX.equals("G"))
      {
        if(!isRetToCenterX)
        {
          if(auxLastCoordValX != 0)
          {
            howMuchSquaresX = auxLastCoordValX - G;
          }
          else
          {
            howMuchSquaresX = lastCoordValueX - G;
          }
        }
        else
        {
          Serial.println(":G4");
          howMuchSquaresX = G - originX;
        }
      }

      if(coordX.equals("H"))
      {
        if(!isRetToCenterX)
        {
          if(auxLastCoordValX != 0)
          {
            howMuchSquaresX = auxLastCoordValX - H;
          }
          else
          {
            howMuchSquaresX = lastCoordValueX - H;
          }
        }
        else
        {
          howMuchSquaresX = H - originX;
        }
      }

      Serial.print("EATER: howMuchSquaresX: ");
      Serial.println(String(howMuchSquaresX));

      auxHowMuchSqX = howMuchSquaresX;

      Serial.print("F1NAL: auxLasCoordValX: ");
      Serial.println(String(auxLastCoordValX));

      if(howMuchSquaresX < 0) //At current point to right
      {
        Serial.println("finalHIGH1");
        digitalWrite(dirPinX, HIGH);
        howMuchSquaresX *= -1;
        Serial.println(String(howMuchSquaresX) + "!");
        moveMotorX(howMuchSquaresX);
      }
      else if(howMuchSquaresX == 0)  //No need to move X
      {
        Serial.println("No need to move X axis.");
      }
      else if(howMuchSquaresX > 0)  //At current point to left
      {
        Serial.println("finalLOW2");
        digitalWrite(dirPinX, LOW);
        moveMotorX(howMuchSquaresX);
      } 
    }

    void moveMotorX(int stepsX)
    {
      if(!isLastPositionX)
      {
        if(directionCoordX)
        {
          digitalWrite(dirPinX, HIGH);
        }
        else
        {
          digitalWrite(dirPinX, LOW);
        }
      }

      for(int x = 0; x < stepsX; x++)
      {
        digitalWrite(stepPinX, HIGH);
        delay(1);
        digitalWrite(stepPinX, LOW);
        delay(1);
      }

      delay(1000);
    }
};
