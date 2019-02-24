import java.util.Scanner; 
import java.io.*; 
import java.util.ArrayList; 

//calculates the couloumb potential of a given dna configuration using the debye huckel equation for ion shielding
public class Dh
{
	Coordinate [] coor; 
	//private Edge [] edges; 
	private double [][] rij; 
	private int numVertices; 
	private String filename; 
	
	private final double kappa = 1; //dielectric constant
	private final double boltz = 1.38065*Math.pow(10, -23); //boltzmann constant
	private final double temp = 298; //temperature
	private final double a = 0.1*boltz*temp; //constant A
	private final double v = -0.26; //constant v
	private final double elec = 1.60218*Math.pow (10, -19); 
	
	public Dh() //defaults to simplec.txt file
	{
		filename = "simplec.txt"; 
	}
	public Dh (String f) //sets file to be read as string parameter
	{
		filename = f; 
	}
	
	public double run() //returns potential 
	{
		loadCoor(); 
		return potential(); 
	}
	
	public void loadCoor() //loads file containing coordinate points
	{
		Scanner sc = null; 
		try 
		{
			sc = new Scanner(new File(filename)); 
		}
		catch (Exception e)
		{
			System.out.println("file not found :("); 
		}

		ArrayList <Double> numbers = new ArrayList<Double>(); 

		int counter = 0; 
		while(sc.hasNext())
		{
			double i = sc.nextDouble();
			counter++; 
			numbers.add(i); 
		}
		ArrayList <Integer> nums = cleanData(numbers);  
		
		numVertices = counter/3; 

		coor = new Coordinate[numVertices]; //loads coordinate array
		
		for (int i=0; i<counter; i+=3)
		{
			coor[i/3] = new Coordinate(nums.get(i), nums.get(i+1), nums.get(i+2)); 
			 
		}
		
	//	edges = new Edge [numVertices]; //loads edge array 
		/*for (int i=0; i<numVertices-1; i++)
		{
			edges[i]= new Edge(coor[i], coor[i+1]); 
		}
		edges[numVertices-1] = new Edge(coor[numVertices-1], coor[0]); */
		
		coor[numVertices-1].setNext(coor[0]); //sets previous and next coordinate point for each coordinate
		coor[0].setPrev(coor[numVertices-1]); 
		coor[0].setNext(coor[1]);
		for (int i=1; i<numVertices-1; i++)
		{
			coor[i].setPrev(coor[i-1]); 
			coor[i].setNext(coor[i+1]);
		}
		
	}
	
	public ArrayList<Integer> cleanData(ArrayList<Double> nums) //sometimes coordinate data is in float, this method changes them to ints
	{
		ArrayList<Integer> cleaned = new ArrayList<Integer> (); 
		for (int i=0; i<nums.size(); i++)
		{
			double n = nums.get(i); 
			n+=0.6; 
			int intn =0; 
			if (n<0)
			{
				n -= 1; 
			}
			intn = (int)(n); 
			cleaned.add(intn); 
		}
		return cleaned; 
	}
	
	public double potential()
	{
		calcRij(); 
		return calcPotential();

	}
	
	public double potential (Coordinate [] c)
	{
		coor = c; 
		calcRij(); 
		return mitPotential(); 
	}
	public void calcRij() //calculates euclidean distance between every pair of points
	{
		rij = new double [numVertices][numVertices]; 
		for (int i = 0; i<numVertices; i++)
		{
			for (int j = 0; j<i; j++)
			{
				int xdiff = coor[i].get(0)-coor[j].get(0); //euclidean distance between all pairs of points
				int ydiff = coor[i].get(1)-coor[j].get(1); 
				int zdiff = coor[i].get(2)-coor[j].get(2); 
				double dist = Math.sqrt(Math.pow(xdiff, 2) + Math.sqrt(Math.pow(ydiff, 2)) + Math.sqrt(Math.pow(zdiff, 2))); 
				rij[i][j] = dist; 
			}
		}
	}

	public double calcPotential() //calculates potential using debye huckel equation 
	{
		int permitivity = 81; 
		double bjerrum = Math.pow(10, 27)*Math.pow(elec, 2)/(permitivity*boltz*temp); 
	
		double c = 0.09; 
		double lambda = 0.314/(Math.pow(c, 0.5));
		double sum = 0; 
		for (int i =0; i<numVertices; i++)
		{
			for (int j=0; j<i; j++)
			{
				sum+= Math.pow(bjerrum/rij[i][j], -rij[i][j]/lambda); 
			}
		}
		System.out.println(sum); 
		return sum; 
	}
	
	public Coordinate [] getCoordinates()
	{
		return coor; 
	}
	
	
}



