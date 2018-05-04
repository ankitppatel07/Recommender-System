import java.io.*;

class InitialRatingMatrix 
{
	//Given number of users and items
	int no_of_users	= 944;
	int no_of_items = 1683;
	int inputratingMatrix[][] = new int[no_of_users][no_of_items];
	int outputratingMatrix[][] = new int[no_of_users][no_of_items];
	
	String outputfilename = "output.txt";
	
	//function to read the input file and storing it in a matrix 
	void initial_matrix() throws IOException
	{
			String inputfilename;					
			InputStreamReader isr2 = new InputStreamReader(System.in);
			BufferedReader br2 = new BufferedReader(isr2);
		
			System.out.println("Enter Input File Name:");
			inputfilename = br2.readLine();
		
			FileReader fr1 = new FileReader(inputfilename);		
			BufferedReader br3 = new BufferedReader(fr1);
		
			String line;
			String data1[];
	
			while((line = br3.readLine()) != null)
			{
				data1 = line.split("\\s");	//split based on spaces
				int i = Integer.parseInt(data1[0]);		//rows(userID)
				int j = Integer.parseInt(data1[1]);		//columns(itemID)
				inputratingMatrix[i][j] = Integer.parseInt(data1[2]);	//storing the rating value
				//bw1.write(i +" "+ j + " " + ratingMatrix[i][j] + "\n");
				//bw1.newLine();
			}
			br2.close();
			br3.close();						
	}
}


class Prediction extends InitialRatingMatrix
{
	double Pearson_Coeff = 0;	
	//defining 2d matrices for storing the similar usersID's for a particular userID
	int correlated_Users_Matrix[][] = new int[no_of_users][no_of_users];
	//defining 2d matrices for storing the similar users pearson coefficiient 
	double correlated_Users_Coeff_Matrix[][] = new double[no_of_users][no_of_users];
	
	/*
	 function to store the correlated users for a particular user and 
	 calculated Pearson Correlation Coefficient between users based on item-Ratings
	*/
	void prediction()
	{		
		System.out.println("Calculating the Pearson Correlation Coefficient");
		System.out.println("between Users based on Item-Ratings...........");
		for(int i = 1; i < no_of_users; i++)
		{
			Prediction p1 = new Prediction();
			
			for(int j = 1; j < no_of_users; j++)
			{
				if(i == j)
				{
					continue;
				}
				
				Pearson_Coeff = p1.Pearson_Correlation(inputratingMatrix[i], inputratingMatrix[j]);
				
				//coefficient value more than 0.3 denotes strong correlation
				if(Pearson_Coeff > 0.3)
				{
					//store the correlated users in a 2d matrix
					correlated_Users_Matrix[i][j] = j;
					//store the pearson coefficient between the users in a 2d matrix 
					correlated_Users_Coeff_Matrix[i][j] = Pearson_Coeff;
				}
			}
		}
	}
	
	//function to calculate the Pearson Correlation Coefficient between users based on item-Ratings
	double Pearson_Correlation(int ratingMatrix1[], int ratingMatrix2[])
	{
		int diff_user1 = 0, diff_user2 = 0, summation = 0;
		int square_diff_user1 = 0, square_diff_user2 = 0;  
		double numerator = 0, denominator = 0;
		double pearson_coeff = 0.0;
		int k_sum = 0, mean;
		
		//rating value (k) ranges from 1 to 5, finding mean value
		for(int z = 1; z <= 5; z++)
		{
			k_sum += z;
		}
		mean = k_sum/5;
		
		for(int k = 1; k < ratingMatrix1.length; k++)
		{
			diff_user1 = ratingMatrix1[k] - mean;
	    	diff_user2 = ratingMatrix2[k] - mean;
	    	summation += diff_user1 * diff_user2;
			square_diff_user1 += Math.pow(diff_user1, 2);
			square_diff_user2 += Math.pow(diff_user2, 2);
			diff_user1 = 0;
			diff_user2 = 0;
		}
		numerator = summation;
		denominator =  (Math.sqrt(square_diff_user1) * Math.sqrt(square_diff_user2));
		//formula to calculate Pearson Correlation Coefficient 
		pearson_coeff = numerator/denominator; 
		
		return pearson_coeff;
	}
}

class OutputMatrix extends Prediction
{
	//predicting the missing rate values based on the Pearson Correlation Coefficient calculated above
	void outputmatrix()
	{
		System.out.println("Predicting the Missing Rate Values....");
		int prediction;

		for(int i = 1; i < no_of_users; i++)
		{
			for(int j = 1; j < no_of_items ; j++)
			{
				double summation1= 0, summation2 = 0;
				//check for rating value(k) = 0 and replace it with predicted value
				if(inputratingMatrix[i][j] == 0)
				{											
					for(int k = 1; k < correlated_Users_Matrix[i].length; k++)
					{
						int temp = correlated_Users_Matrix[i][k];
						if(inputratingMatrix[temp][j]!=0)
						{
							summation1 += inputratingMatrix[temp][j] * correlated_Users_Coeff_Matrix[i][temp];
							
							if(correlated_Users_Coeff_Matrix[i][temp] < 0)
							{
								summation2 += -1 * correlated_Users_Coeff_Matrix[i][temp];
							}
							else
							{
								summation2 += correlated_Users_Coeff_Matrix[i][temp];
							}
						}
					}
					prediction = (int) Math.round(summation1/summation2);
					
					//keeping the rating value withing range 1 to 5
					if(prediction <= 1)
					{
						prediction = 1;
						outputratingMatrix[i][j] = prediction;
					}
					else if(prediction >= 5)
					{
						prediction = 5;
						outputratingMatrix[i][j] = prediction;
					}
					else
					{
						outputratingMatrix[i][j] = prediction;
					}
				}
				else
				{
					//if the rating value(k) is present, just store it in output matrix
					outputratingMatrix[i][j] = inputratingMatrix[i][j];
				}
			}
		}
	}
	
	//function to write the output matrix to output.txt
	void writeOutputMatrix() throws IOException
	{
		FileWriter fw1 = new FileWriter(outputfilename);
		BufferedWriter bw1 = new BufferedWriter(fw1);
		
		System.out.println("Writing the Predicted Result to output.txt.....");
		
		for(int i = 1; i < no_of_users; i++)
		{
			for(int j = 1; j < no_of_items; j++)
			{
				String line = i + " " + j + " " + outputratingMatrix[i][j];
				//write to output.txt
				bw1.write(line);
				bw1.newLine();
			}
		}
		System.out.println("Done Writing Result to output.txt");
		bw1.close();
	}	 
}

public class dm_project extends OutputMatrix
{
	public static void main(String args[]) throws IOException
	{		
		InputStreamReader isr1 = new InputStreamReader(System.in);
		BufferedReader br1 = new BufferedReader(isr1);
		
		System.out.println("1. Predict the Missing Rate Values in the Input Data:");
		System.out.println("2. Exit the Program:");
		System.out.println("Enter the option:");
		String option = br1.readLine();
		
		dm_project r1 = new dm_project();
		
		switch(option) 
		{
		   case "1" :
		      r1.initial_matrix();
		      r1.prediction();
		      r1.outputmatrix();
		      r1.writeOutputMatrix();
		      break; // optional
		   
		   case "2" :
			   System.exit(0);
		   
		   default :
		      System.out.println("Invalid Option!!!");
		}
		br1.close();
	}
}


