
class Decoder
{
  private:

    

  public:

    Decoder()
    {

    }

    static Move decodeStringToMove(String s)
    {
      boolean x = true;
      boolean y = s.charAt(4) == ' '?false:true;

      boolean e = s.charAt(4) == 'e'?true:false;

      int a = letterToNumber(s.charAt(2));
      int b = s.charAt(3) - '0';
      int c = letterToNumber(s.charAt(5));
      int d = s.charAt(6) - '0';
      
      Move theMove(x, y, a, b, c, d, e);
      return theMove;
    }

    static String decodeMoveToString(int a, int b, int c, int d)
    {
       String theMove = "@";
       theMove += "00";
       theMove += numberToLetter(a);
       theMove += (String) b;
       theMove += "0";
       theMove += numberToLetter(c);
       theMove += (String) d;
       theMove += "00";
       return theMove;
    }

    static String decodeStringMoveToString(String s)
    {
       String theMove = "@";
       theMove += "00";
       theMove += s.charAt(0);
       theMove += s.charAt(1);
       theMove += "0";
       theMove += s.charAt(2);
       theMove += s.charAt(3);
       theMove += "00";
       return theMove;
    }

    static int letterToNumber(char letter)
    {
      switch(letter)
      {
        case 'A': return 0; 
        case 'B': return 1;
        case 'C': return 2;
        case 'D': return 3;
        case 'E': return 4;
        case 'F': return 5;
        case 'G': return 6;
        case 'H': return 7; 
        default: 1000;
      }
    }

    static String numberToLetter(int number)
    {
      switch(number)
      {
        case 0: return "A";
        case 1: return "B";
        case 2: return "C";
        case 3: return "D";
        case 4: return "E";
        case 5: return "F";
        case 6: return "G";
        case 7: return "H";
        return "";
      }
    }
};
