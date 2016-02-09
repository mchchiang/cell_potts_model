package cell_potts_model;

import java.io.*;

public class GetAverage {
	public static void main (String [] args) throws IOException{
		int avgCol = Integer.parseInt(args[args.length-1]);
		int refCol = Integer.parseInt(args[args.length-2]);
		int numOfFiles = args.length-3;
		String outputFile = args[args.length-3];
		
		BufferedReader [] files = new BufferedReader[numOfFiles];
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(outputFile)));
		for (int i = 0; i < numOfFiles; i++){
			files[i] = new BufferedReader(new FileReader(args[i]));
		}
		
		String [] str;
		while (files[0].ready()){
			double avg = 0.0;
			double ref = 0.0;
			for (int i = 0; i < args.length-3; i++){
				str = files[i].readLine().split("\\s+");
				if (!str[0].equals("")){
					ref = Double.parseDouble(str[refCol]);
					avg += Double.parseDouble(str[avgCol]);
				}
			}
			avg /= (double) numOfFiles;
			writer.printf("%.8f %.8f\n", ref, avg);
		}
		for (int i = 0; i < numOfFiles; i++){
			files[i].close();
		}
		writer.close();
	}
}
