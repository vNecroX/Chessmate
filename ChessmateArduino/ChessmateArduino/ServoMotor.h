//#include <Servo.h>
#include "ServoTimer2.h"

class ServoMotor
{
  private:

    //Servo servo;
    ServoTimer2 servo;
    int outServo = 7;

  public:

    ServoMotor()
    {
      pinMode(outServo, OUTPUT);

      servo.attach(outServo);
      servo.write(mapServo(0));
    }

    // ------------------------------------------------------------

    void moveTo(int d)
    {
      servo.write(mapServo(d));
    }

    int mapServo(int d)
    {
      return map(d, 0, 180, 750, 2250);
    }

};
