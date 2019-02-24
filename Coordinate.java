public class Coordinate
{
	int [] xyz =  new int [3]; 
	Coordinate prev; //coordinate prior to this one in the polygon
	Coordinate next; //next coordinate point in polygon
	public Coordinate (int a, int b, int c)
	{
		xyz[0] = a; 
		xyz[1] = b; 
		xyz[2] = c;  
	}
	
	public Coordinate (Coordinate copy) //copy constructor
	{
		for (int i = 0; i<3; i++)
		{
			xyz[i] = copy.get(i); 
		}
	}
	public Coordinate getNext()
	{
		return next; 
	}
	
	public Coordinate setPrev()
	{
		return prev; 
	}
	public void setNext(Coordinate c)
	{
		next = c; 
	}
	
	public void setPrev(Coordinate c)
	{
		prev = c; 
	}
	
	public int get(int i)
	{
		return xyz[i]; 
	}
	
	public void set (int i, int n)
	{
		xyz[i] = n; 
	}
	
	public void setValue(Coordinate other)
	{
		for (int i=0; i<3; i++)
		{
			xyz[i] = other.get(i); 
		}
	}
	
	public boolean equals (Coordinate other)
	{
		for (int i = 0; i<3; i++)
		{
			if (this.get(i)!=other.get(i))
				return false; 
		}
		return true; 
	}
	
	public String toString ()
	{
		return xyz[0] + " " + xyz[1] + " " + xyz[2]; 
	}
	
}