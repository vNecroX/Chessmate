#include "Motorx.h"
#include "Motory.h"
//#include "ServoMotor.h"

#include "Coord.h"

class SysMotors
{
  private:

    MotorX motorX;
    MotorY motorY;

    Coord coord;

  public:

    SysMotors()
    {

    }

    void start()
    {
      calibrate();
      Serial.println("SysMotors ready.");
    }

    void calibrate()
    {
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CALIBRATE FUNCTION ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      
      motorX.initializeMotorX();
      motorY.initializeMotorY();
    }

    void recal()
    {
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RECAL FUNCTION ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      
      motorX.originToCenterPositionX();
      motorY.originToCenterPositionY();
    }

    void recalibrate()
    {
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ RECALIBRATE FUNCTION ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      
      motorX.finalizeMotorX();
      motorY.centerToOriginPositionY();
    }

    void initialMoveXY(String x, String y)
    {
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ INITIAL MOVE XY FUNCTION ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      
      coord.setCoords(x, y);

      motorX.firstPosition(coord.getX());
      motorY.firstPosition(coord.getY());
    }

    void finalMoveXY(String x, String y)
    {
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@FINAL MOVE XY FUNCTION ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      
      coord.setCoords(x, y);

      motorX.lastPosition(coord.getX());
      motorY.lastPosition(coord.getY());
    }

    void eatenPieceXY(String x, String y)
    {
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ EATEN PIECE XY FUNCTION ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      
      coord.setCoords(x, y);
      
      motorX.eatenPieceX(coord.getX());
      motorY.eatenPieceY(coord.getY());  
      motorX.setToEdgeX();
    }

    void toEatenPieceXY1(String x, String y)
    {
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ toEatenPieceXY1 FUNCTION ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      
      coord.setCoords(x, y);
      
      motorX.eatenPieceX(coord.getX());
      motorY.eatenPieceY(coord.getY());  
    }

    void toEatenPieceXY2()
    {
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ toEatenPieceXY2 FUNCTION ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      
      motorX.setToEdgeX();
    }

    void eaterPieceXY(String x, String y)
    {
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ EATER PIECE XY FUNCTION ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      
      coord.setCoords(x, y);

      motorX.eaterPieceX(coord.getX());
      motorY.eaterPieceY(coord.getY());
    }

    void eaterToEatenXY(String x, String y)
    {
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ EATER TO EATEN XY FUNCTION ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      
      coord.setCoords(x, y);
      
      motorX.eaterToEatenX(coord.getX());
      motorY.eaterToEatenY(coord.getY());
    }

    void currentToCenterPositionXY(String x, String y)
    {
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ CURRENT TO CENTER POSITION XY FUNCTION ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      Serial.println(" ");
      
      coord.setCoords(x, y);
      
      motorX.currentToCenterPositionX(coord.getX());
      motorY.currentToCenterPositionY(coord.getY());
    }
};
