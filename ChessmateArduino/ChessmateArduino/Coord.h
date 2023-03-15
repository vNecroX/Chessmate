
class Coord
{
  private:

    String x, y;

  public:

    Coord(String x, String y)
    {
      this->x = x;
      this->y = y;
    }

    Coord()
    {
      x = "";
      y = "";
    }

    void setCoords(String x, String y)
    {
      this->x = x;
      this->y = y;
    }

    String getX()
    {
      return x;
    }

    String getY()
    {
      return y;
    }
};
