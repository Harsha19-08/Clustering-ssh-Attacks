import java.io.*;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

/**
 * SSH Attack Clustering - Main Entry Point
 * Enhanced with visualization capabilities
 */
public class Start{

	public static void main(String[] args){
		System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
		System.out.println("║   SSH ATTACK CLUSTERING WITH VISUALIZATION                    ║");
		System.out.println("╚════════════════════════════════════════════════════════════════╝");
		
		String path = "Data_with_filename.csv";
		
		Process P = new Process();
		try{
			System.out.println("\n➤ Loading data from: " + path);
			P.loadData(path);
			System.out.println("✓ Data loaded successfully\n");
		}
		catch(IOException e){
			System.out.println("✗ Problem in loading the file: " + e.getMessage());
			return;
		}

		// Test different K values for elbow method
		System.out.println("➤ Analyzing optimal cluster count (K) using Elbow Method...");
		System.out.println("  Testing K values: 2, 3, 4, 5, 6\n");
		
		Map<Integer, Double> kAnalysis = new LinkedHashMap<>();
		int[] kValues = {2, 3, 4, 5, 6};
		
		for (int k : kValues) {
			System.out.println("  Processing K=" + k + "...");
			try {
				Process P_temp = new Process();
				Process.loadData(path);
				Entry<Integer[], Double> tempResult = P_temp.cluster(k);
				kAnalysis.put(k, tempResult.getValue());
				System.out.println("    ✓ K=" + k + " completed (SSE: " + String.format("%.2f", tempResult.getValue()) + ")");
			} catch (IOException e) {
				System.out.println("    ✗ Error processing K=" + k);
			}
		}
		
		// Display elbow analysis
		Visualization.displayElbowAnalysis(kAnalysis);
		
		// Run final clustering with K=4 (default)
		System.out.println("➤ Running final clustering with K=4...");
		Entry<Integer[], Double> result = P.cluster(4);
		
		// Display comprehensive visualization
		Visualization.displayClusteringReport(result.getKey(), result.getValue(), 4);
		
		// Display detailed cluster information
		Visualization.displayClusterDetails(result.getKey());
		
		System.out.println("✓ Analysis complete!");
	}

}
