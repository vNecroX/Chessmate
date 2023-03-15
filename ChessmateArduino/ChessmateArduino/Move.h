
class Move
{
  private:

    boolean x, y;
    
    int a, b, c, d;

    boolean e;
    
  public:

    Move()
    {
      this->x = false;
      this->y = false;

      this->e = false;

      this->a = 0;
      this->b = 0;
      this->c = 0;
      this->d = 0;
    }

    Move(boolean x, boolean y, int a, int b, int c, int d, boolean e)
    {
      this->x = x;
      this->y = y;

      this->e = e;

      this->a = a;
      this->b = b;
      this->c = c;
      this->d = d;
    }

    String inString()
    {
      return (String)a+(String)b+(String)c+(String)d;
    }

    boolean getEat()
    {
       return this->y;
    }

    int getXi()
    {
        return this->a;
    }

    int getYi()
    {
        return this->b;
    }

    int getXf()
    {
        return this->c;
    }

    int getYf()
    {
        return this->d;
    }

    boolean getRook()
    {
        return this->e;
    }
    
};
