import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.io.*; 
import java.util.Random;

public class KMeans 
{
	public static void main (String [] args){
		//Proper command line:
		//First: compile with javac KMeans.java
		//Second (ex): execute with java KMeans database.txt 5 4 0.23 output.txt
		//Reads in the arguments from the command line
        String database_text = args[0];

        String num_clusters_string = args[1];
        int num_clusters = Integer.parseInt(num_clusters_string);

        String num_iterations_string = args[2];
        int num_iterations = Integer.parseInt(num_iterations_string);

        String min_distance_string = args[3];
        Float min_distance = Float.parseFloat(min_distance_string);

        String output_text = args[4];

        ArrayList<float[]> datapoints = new ArrayList<float[]>(); //Data structure for holding all of the data point
        datapoints = read_database(database_text);

        genKmeans(datapoints, num_clusters, num_iterations, min_distance, output_text);


	}

	//This method stores the data pints into a matrix data structure
    //@param database_text is the .txt file containing the data points
    //@returns datapoints which is an arraylist of arrays
    public static ArrayList<float[]> read_database(String database_text){
    	ArrayList<float[]> datapoints = new ArrayList<float[]>();
    	//The following try and catches opens the folder and read from following lines
    	File file = new File(database_text);
    	try
    	{
    		BufferedReader br = new BufferedReader(new FileReader(file));
	    	try
	    	{
		    	//Begin reading in the following data points into corresponding arrays
		    	String st;
		    	//Iterator i serves as placeholder for the data_array, or the array holding datapoints
		    	int i =0;
		    	//Continue reading through file until all lines, or datapoints, are complete
		    	while ((st = br.readLine()) != null)
		    	{		
		    			//Current_data_string serves as the current datapoint represented as string
		    			String [] current_data_string = st.split("\\s+");
		    			float [] current_data_int = new float[current_data_string.length];
		    			//Current_trans_int serves as the current data point represented as int
		    			//Must iterate through current_data_string to transfer each feature to integer form
		    			for (int k = 0; k<current_data_string.length; k++){
		    				current_data_int[k] = Float.parseFloat(current_data_string[k]);
		    			}
		    			//Once the datapoint has been converted, add it to the arraylist of all datapoints
		    			datapoints.add(current_data_int);
		    			i++;
		    	}
	    	}
	    	catch (IOException e)
	    	{
	    		System.out.println("No datapoints");
	    	}
    	}
    	catch (FileNotFoundException ex)
    	{
    		System.out.println("Cannot open file");
    	}


    	return datapoints;
    }

    public static void genKmeans(ArrayList<float[]> database, int num_clusters, int iterations, float distance, String output)
    {
    	ArrayList<Integer> random_nums = new ArrayList<Integer>();
    	ArrayList<float[]> centroids = new ArrayList<float[]>();
    	int[]clusters = new int[database.size()];
    	List<List<float []>> assigned_clusters = new ArrayList<List<float[]>>();
    	random_nums = random(num_clusters, database.size()); //call random function to aid in initalizing centroids
    	centroids = create_random_centroid(random_nums, database); //randomly initialize the centroids

    	int wrte = 100; //parameter used for "with respect to e" -- meaning distance between old and new centroid. Initalizing to 100 to make sure it goes through first iteration
    	int i = 0;
    	while ((wrte <= distance) || i <=iterations){ //repeat until the new and old cluster centroids are e difference or many iterations
    		clusters = assign_points(centroids, database); //this method assigns points to clusters
    		assigned_clusters = create_clusters(clusters, num_clusters, database); //this method creates the new clusters
            centroids_sum = update_centroids_sum(assigned_clusters);
            centorids = update_centroids_avg(centroids_sum)
    		i++;
    	}

    }

    public static ArrayList<Integer> random(int clusters, int datapoints)
    {
    	ArrayList<Integer> random_num = new ArrayList<Integer>();
    	Random rand = new Random(); //use java's built in random function

    	for (int i=0; i < clusters; i++){ //get random numbers based on the number of clusters needed
    		int n = rand.nextInt(datapoints); //get random number
    		while(random_num.contains(n)){// if random_num contains this number, keep trying random until get one that is not contained
    			n = rand.nextInt(datapoints);
    		}
    		random_num.add(n); //add random number to structure
    	}

    	return random_num;

    }

     public static ArrayList<float[]> create_random_centroid(ArrayList<Integer> random_nums, ArrayList<float[]> database)
    {
    	ArrayList<float[]> random_centroid = new ArrayList<float[]>();

    	for (int i=0; i < random_nums.size(); i++){ //iterate through the random numbers
    		float[] current_centroid = database.get(random_nums.get(i)); //get the random centroid based on the random number
    		random_centroid.add(current_centroid); //add random datapoint to centroid
    	}

    	return random_centroid;

    }


    public static int[] assign_points(ArrayList<float[]> centroid, ArrayList<float[]> database)
    {
    	int[]datapoint_cluster = new int[database.size()];

    	for (int i=0; i < database.size(); i++){ //iterate through the all datapoints
    		double [] dist_to_centroid = new double[centroid.size()]; //create an array to store the distances (wrte each datapoint)
    		float[] current_datapoint = database.get(i); //get the current datapoint
    		for (int j = 0; j < centroid.size(); j++){ //compare distance of datapoint to all of the centroids
    			double curr_dist = distance(current_datapoint, centroid.get(j));
    			dist_to_centroid[j] = curr_dist;
    		}
    		//Now that we have the distance between curr data point and all the centroids, assign datapoint to a centroid
    		double minValue = dist_to_centroid[0];
    		int centroid_min = 0;
			for(int s=1;s<dist_to_centroid.length;s++){
				if(dist_to_centroid[s] < minValue){
				  minValue = dist_to_centroid[s];
				  centroid_min = s;
				}
			}

			datapoint_cluster[i] = centroid_min;

		} 
    	return datapoint_cluster;

    }
     public static double distance(float[] a, float[] b) {
        double diff_square_sum  = 0.0;
        for (int i = 0; i < a.length; i++) {
            diff_square_sum += (a[i] - b[i]) * (a[i] - b[i]);
        }
        return Math.sqrt(diff_square_sum);
    }

    public static List<List<float []>> create_clusters(int[] point_to_cluster, int cluster_size, ArrayList<float[]> database){
	    List<List<float []>> lists = new ArrayList<List<float[]>>();
		for (int i = 0; i < cluster_size; i++) { //dynamically create a list of lists
		    List<float []> list = new ArrayList<>();
		    lists.add(list);
		}	

    	for (int j=0; j<point_to_cluster.length; j++){ //iterating through all data points
    		int current_cluster = point_to_cluster[j]; //get the datapoints cluster
    		float [] current_datapoint = database.get(j); //get the array for that datapoints and to the associated list
    		lists.get(current_cluster).add(current_datapoint);
    	}
    	return lists;
    }

    public static ArrayList<float[]> update_centroids(List<List<float []>> assigned_clusters){
        ArrayList<float[]> centroid_sum = new ArrayList<float[]>();
        for (int current_cluster = 0; current_cluster < assigned_clusters.size()-1; current_cluster++){//iterate through all clusters 
            int size = 550;  //hard coding in the size of the features
            float[] data_points = new float[size]; //create array to store sum of all features for that cluster -- all floats the same size
                int cur_data_point = 0; 
                while (cur_data_point < assigned_clusters.get(current_cluster).size()){

                    for (int d_features = 0; d_features < assigned_clusters.get(current_cluster).get(cur_data_point).length; d_features++){ //iterate through features

                        float cur_d_feature = assigned_clusters.get(current_cluster).get(cur_data_point)[d_features]; //get current float

                        float respective_feature = data_points[d_features]; //get the float at the same location in data points
                        
                        float sum = cur_d_feature + respective_feature; //add the two floats together 
                        data_points[d_features] = sum; //update the sum
                    }
                    cur_data_point++;
            }
            centroid_sum.add(data_points);
        }
        return centroid_sum;
    }

    
}
















