import java.io.*;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

/**
 * Fast Demo of SSH Attack Clustering Visualizations
 * Shows visualization without running full elbow analysis
 */
public class StartDemo{

	public static void main(String[] args){
		System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
		System.out.println("║   SSH ATTACK CLUSTERING - VISUALIZATION DEMO                   ║");
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

		// Run clustering with K=4 only (faster)
		System.out.println("➤ Running K-Means clustering with K=4...\n");
		Entry<Integer[], Double> result = P.cluster(4);
		
		// Display comprehensive visualization
		Visualization.displayClusteringReport(result.getKey(), result.getValue(), 4);
		
		// Display detailed cluster information
		Visualization.displayClusterDetails(result.getKey());
		
		// Show simulated elbow analysis (sample data for demonstration)
		System.out.println("➤ Showing simulated Elbow Method analysis:");
		Map<Integer, Double> sampleElbowData = createSampleElbowData(result.getValue());
		Visualization.displayElbowAnalysis(sampleElbowData);
		
		System.out.println("✓ Visualization Demo Complete!");
		System.out.println("\nTo run the full analysis with all K values (2-6), run: java Start");
		System.out.println("(Note: Full analysis takes longer but provides complete elbow curve)");
	}
	
	/**
	 * Create sample elbow data for demonstration
	 */
	private static Map<Integer, Double> createSampleElbowData(double baseSSE) {
		Map<Integer, Double> data = new LinkedHashMap<>();
		// Simulate realistic elbow curve - decreasing improvements as K increases
		data.put(2, baseSSE * 2.8);
		data.put(3, baseSSE * 1.9);
		data.put(4, baseSSE * 1.0);   // Our actual result
		data.put(5, baseSSE * 0.85);  // Small improvement
		data.put(6, baseSSE * 0.78);  // Diminishing returns (elbow point around K=4)
		return data;
	}
	
}
