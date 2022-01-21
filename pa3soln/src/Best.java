final class Best
{
    int row;
    int column;
    int val;
    
    // value-only constructor: no position information
    public Best( int v )
      { this( v, -6, -7 ); }  // provide illegal position to detect accidental use
      
    public Best( int v, int r, int c )
      { val = v; row = r; column = c; }
}
