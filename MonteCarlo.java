import java.util.Scanner;
import java.io.*; 
import java.util.ArrayList; 

//implemented the three lattice moves from 
//"Monte Carlo Study of the Interacting Self-Avoiding Walk Model in Three Dimensions" 
//by Tesi et.al 1996
public class MonteCarlo
{
	PrintWriter pw; 
	Dh d; 
	Coordinate [] coor; 
	ArrayList<Integer> hats; 
	ArrayList<Integer> corners; 
	String fileName; 
	
	public  MonteCarlo(String str)
	{
		fileName = str; 
	}
	
	public MonteCarlo()
	{
		fileName = "smolunknot.dat"; 
	}
	public void metropolis()
	{
		
		createEnergyFile("confenergy.txt"); 
		//Scanner sc = new Scanner(System.in);
		int counter = 0; 
		findHatsAndCorners();
		double prevEnergy = getEnergy(); //large number to begin with 
		double currEnergy; 
		
		
		while (counter<50)
		{
			findHatsAndCorners();
			coor = d.getCoordinates(); 
			crankNum(100); 
			currEnergy = d.potential(coor); 
			if (currEnergy<prevEnergy) //accept conformation
			{
				prevEnergy = currEnergy;
				pw.write(prevEnergy+"\n"); 
				counter++; 
				System.out.println("accepted confirmation"); 
				updateFile(); 
			}
			else
			{
				double p = Math.pow(Math.E, -1*(currEnergy - prevEnergy)*Math.pow(10, 8));
				double q = Math.random(); 
			
				if (q<p) //accept confirmation
				{
					prevEnergy = currEnergy; 
					pw.write(prevEnergy+"\n");
					counter++; 
					System.out.println("accepted confirmation"); 
					updateFile(); 
					
				} //else reject confirmation
				else 
				{
					System.out.println("rejected confirmation"); 
				}
			}
			System.out.println("COUNTER: "+ counter);
			
			
			//System.out.print("next step? 1 for yes, 0 for no"); 
			//yesno = sc.nextInt();
			
		}
		 
		pw.close(); 
	}
	
	public double getEnergy()
	{
		//d.loadCoor();
		return d.potential(); 
	}
	
	public static void main (String [] args)
	{
		MonteCarlo mc = new MonteCarlo("5_2.dat"); 
		
		
		//mc.crankNum(50); 
		mc.metropolis();
	//	d = new Dh("bigtrefoil.dat"); 
	//mc.metropolis(); 
		//mc.energyVConf();
	}
	
	public void debug()
	{
		System.out.println("DEBUG"); 
		for (int i =0; i<coor.length-1; i++)
		{
			int counter = 0; 
			for (int j=0; j<3; j++)
			{
				int diff = Math.abs(coor[i].get(j)-coor[i+1].get(j)); 
				if (diff > 1)
				{
					System.out.println("\ni= " + i);
					System.out.println("OH NO"); 
					System.exit(1);
				}
				if (diff==1)
					counter++; 
			}
			if (counter != 1)
			{
				System.out.println("i= " + i);
				System.out.println("\n\nOH NO"); 
				
				System.exit(0);
			}
		}
	}
	public void createEnergyFile(String filename) //creates file to save energies of conformations to 
	{
		try
		{
			pw = new PrintWriter(new File(filename)); 
		}
		catch (Exception e)
		{
			System.out.println("darnit :("); 
		}
	}
	public void crankNum(int n)
	{
		for (int i=0; i<n; i++)
			crankshaft(); 
		//updateFile() ;
	}
	
	public void crankshaft()
	{
		//Dh test = new Dh (fileName); //test
		//test.loadCoor(); 
		//coor = test.getCoordinates(); 
		//printCoorArray(coor); 
		boolean success = false; 
		while (!success)
		{
			int move = (int)(Math.random()*3+1); 
			//System.out.println("MOVE " + move);
			//randomly selects crankshaft move
			//1 for 180 flip, 2 for 90 flip, 3 for one bead flip
			
			if ((move == 1 || move==2) && hats.size()!=0)
			{
				int c = hats.get((int)(Math.random()*hats.size())); 
				Coordinate [] four = {new Coordinate(coor[c]), new Coordinate (coor[c].getNext()), new Coordinate (coor[c].getNext().getNext()), new Coordinate (coor[c].getNext().getNext().getNext())};
				
				if (move==1)
					four = oneEighty(four); 
				else
					four = ninety(four); 
				if (noRepeats(four))
				{
					//System.out.println("move " + move); 
					if (c+1 >= coor.length)
						coor[c+1-coor.length].setValue(four[1]);
					else 
						coor[c+1].setValue(four[1]);  
					if (c+2 >= coor.length)
						coor[c+2-coor.length].setValue(four[2]);
					else 
						coor[c+2].setValue(four[2]); 
					//updateFile();
					updateHat(boundary(c, -2));
					updateHat(boundary(c, 2)); 
					updateCorner(boundary(c, -1)); 
					updateCorner(boundary(c, 2)); 
					
					//updateFile(); 
					success = true; 
				}
			}
			else if (move == 3 && corners.size()!=0)
			{
				int c = corners.get((int)(Math.random()*corners.size())); 
				Coordinate [] three = {new Coordinate(coor[c]), new Coordinate (coor[c].getNext()), new Coordinate (coor[c].getNext().getNext())};
				three = flipCorner(three); 
				if (noRepeatsThree(three))
				{
					if (c+1 >= coor.length)
						coor[c+1-coor.length].setValue(three[1]); 
					else 
						coor[c+1].setValue(three[1]); 
					//System.out.println("move " + move); 
					//updateFile(); 
					updateCorner(boundary(c, 1));
					updateCorner(boundary(c, -1)); 
					updateHat(boundary(c, -2)); 
					updateHat(boundary(c, 1)); 
					
					//updateFile(); 
					success = true; 
				}
			} 
			
			//printCoorArray(coor); 
			//System.out.print(hats); 
			//System.out.print(corners); 
			//debug(); 
		}
		
	}
	
	public int boundary (int c, int n)
	{
		int check = c+n; 
		if (check > coor.length)
			return check-coor.length; 
		else if (check < 0)
			return check + coor.length; 
		return c+n; 
	}
	public void findHatsAndCorners() //find locations of hats (possible 180/90 degree flip locations 
	{ 								//and find locations of corners (possible one bead flip location)
		d = new Dh(fileName); 
		d.loadCoor();
		coor = d.getCoordinates(); 
		//printCoorArray(coor); 
		hats = new ArrayList<Integer>(); 
		for (int i =0; i<coor.length; i++)
		{
			Coordinate [] four = {new Coordinate(coor[i]), new Coordinate (coor[i].getNext()), new Coordinate (coor[i].getNext().getNext()), new Coordinate (coor[i].getNext().getNext().getNext())};

			if (checkSquare(four))
				hats.add(i); 
		}
		corners = new ArrayList<Integer>(); 
		for (int i =0; i<coor.length; i++)
		{
			Coordinate [] three = {new Coordinate(coor[i]), new Coordinate (coor[i].getNext()), new Coordinate (coor[i].getNext().getNext())};

			if (checkCorner(three))
				corners.add(i); 
		}
	//	System.out.println(hats); 
	//	System.out.println(corners); 
	}
	
	public void updateHat(int check)
	{
		//check to see if hat starting from "check" was a valid hat
		if (check<0)
			check = coor.length+check;
		else if (check>=coor.length)
			check = check-coor.length; 
		
		int checkLoc = bisearch(hats, check); 
		Coordinate [] four = {new Coordinate(coor[check]), new Coordinate (coor[check].getNext()), new Coordinate (coor[check].getNext().getNext()), new Coordinate (coor[check].getNext().getNext().getNext())};
		boolean squareYesNo = checkSquare(four); 
		//System.out.println(checkLoc + ", square yesno: " + squareYesNo); 
		if (checkLoc == -1 && squareYesNo == true)
		{
			int i = 0; 
			while (i<hats.size())
			{
				//System.out.println("update hat"); 
				if (hats.get(i)>check)
				{ 
					hats.add(i, check); 
					return; 
				}
				i++; 
			}
			hats.add(check); 
		}
		
		else if (checkLoc != -1 && squareYesNo == false)
		{
			hats.remove(new Integer (check)); 
		}
			
	}
	
	public void updateCorner(int check)
	{
		if (check<0)
			check = coor.length+check;
		else if (check>=coor.length)
			check = check-coor.length; 
		
		int checkLoc = bisearch(corners, check); 
		Coordinate [] three = {new Coordinate(coor[check]), new Coordinate (coor[check].getNext()), new Coordinate (coor[check].getNext().getNext())};
		boolean cornerYesNo = checkCorner (three); 
	//	System.out.println(checkLoc + ", corneryesno " + cornerYesNo); 
		if (checkLoc == -1 && cornerYesNo == true)
		{
			int i = 0; 
			while (i<corners.size())
			{
				//System.out.println("update corner"); 
				if (corners.get(i)>check)
				{ 
					corners.add(i, check); 
					return; 
				}	
				i++; 
			}
			corners.add(check); 
		}
		
		else if (checkLoc != -1 && cornerYesNo == false)
		{
			corners.remove(new Integer (check)); 
		}
	}
	
	public int bisearch (ArrayList<Integer> arr, int target) //binary search
	{
		int min = 0; 
		int max = arr.size()-1; 
		while (min <= max)
		{
			int mid = (min+max)/2; 
			if (arr.get(mid) < target)
				min = mid +1; 
			else if (arr.get(mid) > target)
				max = mid -1; 
			else 
				return mid; 
		}
		return -1; 
	}
	
	public boolean checkSquare(Coordinate [] four) //checks to see if the four coordinates form a square on a plane
	{
		for (int i = 0; i<3; i++)
		{
			if (four[0].get(i)-four[1].get(i) != four[3].get(i) - four[2].get(i))
				return false; 
		}
		
		return true; 
	}
	
	public Coordinate[] oneEighty(Coordinate [] four) //generates new coordinate positions for 180 degree crankshaft
	{
		//System.out.println("ONE EIGHTY FLIP"); 
		for (int i = 0; i<3; i++)
		{
			int diff = four[0].get(i) - four[1].get(i); 
			if (diff != 0)
			{
				four[1].set(i, four[1].get(i)+2*diff); 
				four[2].set(i, four[2].get(i)+2*diff); 
				
				break;
			}
		}
		return four; 
	}
	
	public Coordinate[] ninety (Coordinate [] four) //generates new coordinate positions for 90 degree crankshaft
	{
		//System.out.println("NINETY FLIP"); 
		int rand = (int)(Math.random()*2); //generates either 0 or 1 randomly, determines direction of 90degree rotation
		for (int i=0; i<3; i++)
		{
			int diff = four[0].get(i) - four[1].get(i); 
			if (diff!=0)
			{
				four[1].set(i, four[1].get(i) + diff); 
				four[2].set(i, four[2].get(i) + diff);
				//System.out.println("ONE" + four[1]); 
				//System.out.println("TWO" + four[2]); 
				for (int j = 0; j<3; j++)
				{
					int diff2 = four[0].get(j) - four[3].get(j); 
					if (diff2 == 0 && i != j)
					{
						if (rand==0) //flips 90 degrees one way
						{
							four[1].set(j, four[1].get(j)+1); 
							four[2].set(j, four[2].get(j)+1); 
						}
						else //flips 90 degrees the other way
						{
							four[1].set(j, four[1].get(j)-1); 
							four[2].set(j, four[2].get(j)-1); 
						}
							
					}
				}
				break; 
			}
		}
		return four; 
	}
	
	public boolean checkCorner(Coordinate [] three) //checks if three coordinates form a "corner" (not collinear)
	{
		int counter = 0; 
		for (int i =0; i<3; i++)
		{
			if (three[0].get(i) == three[1].get(i) && three [1].get(i)== three[2].get(i))
				counter++; 
		}
		if (counter >1)
			return false; 
		return true; 
	}
	
	public Coordinate[] flipCorner (Coordinate [] three)
	{
		//System.out.println("ONE BEAD FLIP"); 
		for (int i=0; i<3; i++)
		{
			if (three[0].get(i) != three[1].get(i))
			{
				three[1].set(i, three[0].get(i)); 
				for (int j=0; j<3;j++)
				{
					if(i!=j && three[1].get(j) != three[2].get(j))
						three[1].set(j, three[2].get(j)); 
				}
				break; 
			} 
		}
		return three; 
	}
	
	public boolean noRepeats(Coordinate [] four) //checks for self intersection for 90/180 flip
	{
		for (int i = 0; i<coor.length; i++)
		{
			if (four[1].equals(coor[i]) || four[2].equals(coor[i]))
			{
				//System.out.println ("FALSE" + coor[i]); 
				return false;
			}
				
		}
		return true; 
	}
	
	public boolean noRepeatsThree(Coordinate [] three) //checks for self intersection for one bead flip
	{
		for (int i =0; i<coor.length; i++)
		{
			if (three[1].equals(coor[i]))
			{
				//System.out.println("FALSE" + coor[i]); 
				return false; 
			}
		}
		return true; 
	}
	
	public void updateFile()
	{
		PrintWriter updater = null; 
		try
		{
			updater = new PrintWriter(new File (fileName)); 
		}
		catch (Exception e)
		{
			
		}
		for (int i = 0; i<coor.length; i++)
		{
			updater.write(coor[i].get(0) + " " + coor[i].get(1) + " " + coor[i].get(2) + "\n");
		}
		updater.close(); 
	}
	
	public void energyVConf()
	{
		createEnergyFile("energyvconf.dat"); 
		for (int i=0; i<10; i++)
		{
			//getEnergy(); 
			pw.write(getEnergy() + "\n");
			crankNum(5); 
		}
		
		pw.close(); 
	}
	
	public void printCoorArray(Coordinate [] arr)
	{
		for (int i =0; i<arr.length; i++)
		{
			System.out.println(arr[i]); 
		}
	}
	
	public void printIntArray(int [] arr)
	{
		for (int i=0; i<arr.length; i++)
			System.out.println(arr[i]); 
	}
}