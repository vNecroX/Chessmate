#include "Reader.h"

class SysSensors
{
  private:

    String stringKeysIn;
    String stringKeysOut;

    String stringKeysPut;
    String stringKeysLeft;

    int limitKeysIn;

    Reader reader;

  public:

    SysSensors()
    {
    }

    void start()
    {
      reader.create();
      resetStringKeys();
      Serial.println("SysSensors ready");
    }

    void resetStringKeys()
    {
      stringKeysIn = "";
      stringKeysOut = "";
      stringKeysLeft = "";
      stringKeysPut = "";
    }

    void setLimitKeysIn(int limit)
    {
      limitKeysIn = limit;
    }

    // Full scan of all sensors

    boolean fullScanSensors()
    {
        reader.scanBoard();

        if(reader.anyKeyPut() || reader.anyKeyLeft())
        {
          stringKeysIn = reader.getKeysIn();
          stringKeysOut = reader.getKeysOut();

          stringKeysPut = reader.getKeysPut();
          stringKeysLeft = reader.getKeysLeft();

          return true; // there was a change, someone move something
        }

        stringKeysIn = reader.getKeysIn();
        stringKeysOut = reader.getKeysOut();

        stringKeysPut = reader.getKeysPut();
        stringKeysLeft = reader.getKeysLeft();

        return false; // pieces stated the same, no move, nothing
    }
    
    String stateTable()
    {
        if(reader.moreThanxKeysIn(limitKeysIn))
        {
          return "exceeded";
        }
        else if(reader.moreThanxKeysOut(64))
        {
          return "missing";
        }
        else if(reader.moreThanOneKeysLeft())
        {
          return "minus";
        }
        else if(reader.moreThanOneKeysPut())
        {
          return "more";
        }
        else if((reader.anyKeyLeft() && !reader.anyKeyPut()) || !reader.anyKeyLeft() && reader.anyKeyPut())
        {
          return "moving";
        }
        else if(reader.anyKeyLeft() && reader.anyKeyPut())
        {
          Serial.println("*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*");
          Serial.println("*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*");
          Serial.println(" BOTH LEFT AND PUT EXECUTED AT THE SAME SCAN ");
          Serial.println("*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*");
          Serial.println("*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*/*");
          return "??";
        }
        else
        {
          return "?";
        }
    }

    // Getters of keys in, out, put, left

    String getStringKeysIn()
    {
      return stringKeysIn;
    }

    String getStringKeysOut()
    {
      return stringKeysOut;
    }

    String getStringKeysLeft()
    {
      return stringKeysLeft;
    }

    String getStringKeysPut()
    {
      return stringKeysPut;
    }
  
};
