import java.util.*;

/**
 * Visualization class for SSH Attack Clustering results
 * Provides cluster statistics, distribution charts, and performance metrics
 */
public class Visualization {

    /**
     * Display comprehensive clustering report
     */
    public static void displayClusteringReport(Integer[] clusterAssignments, double evaluation, int k) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                    CLUSTERING ANALYSIS REPORT");
        System.out.println("=".repeat(70));
        
        // Cluster statistics
        Map<Integer, Integer> clusterDistribution = getClusterDistribution(clusterAssignments);
        displayClusterStatistics(clusterDistribution, evaluation, k);
        
        // Cluster size distribution chart
        System.out.println("\n" + "-".repeat(70));
        displayClusterChart(clusterDistribution, k);
        
        System.out.println("-".repeat(70));
        System.out.println("=" + "=".repeat(68) + "\n");
    }

    /**
     * Analyze multiple K values and display elbow method results
     */
    public static void displayElbowAnalysis(Map<Integer, Double> kValuesAndErrors) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                    ELBOW METHOD ANALYSIS (K vs SSE)");
        System.out.println("=".repeat(70));
        
        // Sort by K value
        List<Integer> sortedK = new ArrayList<>(kValuesAndErrors.keySet());
        Collections.sort(sortedK);
        
        // Find min and max SSE for scaling
        double minSSE = Double.MAX_VALUE;
        double maxSSE = Double.MIN_VALUE;
        for (double sse : kValuesAndErrors.values()) {
            minSSE = Math.min(minSSE, sse);
            maxSSE = Math.max(maxSSE, sse);
        }
        
        double range = maxSSE - minSSE;
        if (range == 0) range = 1;
        
        // Display graph
        int chartWidth = 50;
        int chartHeight = 15;
        
        System.out.println("\nSSE vs K Value Chart:");
        System.out.println();
        
        for (int row = chartHeight; row >= 0; row--) {
            System.out.print(String.format("%8.0f |", maxSSE - (row * range / chartHeight)));
            
            for (int k : sortedK) {
                double sse = kValuesAndErrors.get(k);
                int normalizedRow = (int) ((sse - minSSE) * chartHeight / range);
                
                if (normalizedRow == row) {
                    System.out.print("██");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.println();
        }
        
        System.out.print("        +");
        for (int i = 0; i < sortedK.size() * 2; i++) {
            System.out.print("-");
        }
        System.out.println();
        System.out.print("         ");
        for (int k : sortedK) {
            System.out.print(String.format("%2d", k));
        }
        System.out.println("\n              K Value\n");
        
        // Detailed table
        System.out.println("Detailed Results:");
        System.out.println(String.format("%-5s | %-15s", "K", "Sum Squared Error"));
        System.out.println("-".repeat(25));
        for (int k : sortedK) {
            System.out.println(String.format("%-5d | %-15.2f", k, kValuesAndErrors.get(k)));
        }
        
        // Find and suggest optimal K
        int optimalK = findOptimalK(kValuesAndErrors);
        System.out.println("\n✓ Suggested optimal K: " + optimalK + 
                         " (elbow point where improvement diminishes)");
        System.out.println("=" + "=".repeat(68) + "\n");
    }

    /**
     * Display individual cluster details
     */
    public static void displayClusterDetails(Integer[] clusterAssignments) {
        System.out.println("\n" + "=".repeat(70));
        System.out.println("                    DETAILED CLUSTER INFORMATION");
        System.out.println("=".repeat(70));
        
        Map<Integer, List<Integer>> details = new HashMap<>();
        for (int i = 0; i < clusterAssignments.length; i++) {
            int cluster = clusterAssignments[i];
            details.computeIfAbsent(cluster, k -> new ArrayList<>()).add(i);
        }
        
        for (int cluster = 0; cluster < details.size(); cluster++) {
            List<Integer> members = details.get(cluster);
            System.out.println(String.format("\nCluster %d: %d members (%.1f%%)", 
                cluster, 
                members.size(),
                (members.size() * 100.0 / clusterAssignments.length)));
            System.out.println("  Members: " + members.toString());
        }
        System.out.println("\n" + "=".repeat(70) + "\n");
    }

    /**
     * Get distribution of points across clusters
     */
    private static Map<Integer, Integer> getClusterDistribution(Integer[] clusterAssignments) {
        Map<Integer, Integer> distribution = new LinkedHashMap<>();
        for (int clusterId : clusterAssignments) {
            distribution.put(clusterId, distribution.getOrDefault(clusterId, 0) + 1);
        }
        return distribution;
    }

    /**
     * Display cluster statistics
     */
    private static void displayClusterStatistics(Map<Integer, Integer> distribution, double evaluation, int k) {
        System.out.println("\n📊 CLUSTERING SUMMARY:");
        System.out.println(String.format("  • Number of Clusters: %d", k));
        System.out.println(String.format("  • Total Instances: %d", distribution.values().stream().mapToInt(Integer::intValue).sum()));
        System.out.println(String.format("  • Sum of Squared Errors (SSE): %.2f", evaluation));
        System.out.println(String.format("  • Average Cluster Size: %.1f", 
            distribution.values().stream().mapToInt(Integer::intValue).average().orElse(0)));
        
        int maxClusterSize = distribution.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        int minClusterSize = distribution.values().stream().mapToInt(Integer::intValue).min().orElse(0);
        System.out.println(String.format("  • Largest Cluster: %d instances", maxClusterSize));
        System.out.println(String.format("  • Smallest Cluster: %d instances", minClusterSize));
        System.out.println(String.format("  • Cluster Balance: %.1f%% (std dev)", calculateBalance(distribution)));
    }

    /**
     * Display ASCII chart of cluster distribution
     */
    private static void displayClusterChart(Map<Integer, Integer> distribution, int k) {
        System.out.println("\n📈 CLUSTER DISTRIBUTION:");
        
        int maxSize = distribution.values().stream().mapToInt(Integer::intValue).max().orElse(1);
        int barWidth = 40;
        
        for (int cluster = 0; cluster < k; cluster++) {
            int size = distribution.getOrDefault(cluster, 0);
            int barLength = (int) ((size * barWidth) / maxSize);
            double percentage = (size * 100.0 / distribution.values().stream().mapToInt(Integer::intValue).sum());
            
            System.out.print(String.format("  Cluster %d: [", cluster));
            for (int i = 0; i < barLength; i++) {
                System.out.print("█");
            }
            for (int i = barLength; i < barWidth; i++) {
                System.out.print(" ");
            }
            System.out.println(String.format("] %4d instances (%.1f%%)", size, percentage));
        }
    }

    /**
     * Calculate cluster balance (how evenly distributed are the clusters)
     */
    private static double calculateBalance(Map<Integer, Integer> distribution) {
        double mean = distribution.values().stream().mapToInt(Integer::intValue).average().orElse(0);
        double variance = distribution.values().stream()
            .mapToDouble(v -> Math.pow(v - mean, 2))
            .average().orElse(0);
        return Math.sqrt(variance);
    }

    /**
     * Find optimal K using elbow method
     */
    private static int findOptimalK(Map<Integer, Double> kValuesAndErrors) {
        List<Integer> sortedK = new ArrayList<>(kValuesAndErrors.keySet());
        Collections.sort(sortedK);
        
        if (sortedK.size() < 3) {
            return sortedK.get(0);
        }
        
        // Calculate second derivative to find elbow point
        double maxSecondDerivative = 0;
        int optimalK = sortedK.get(0);
        
        for (int i = 1; i < sortedK.size() - 1; i++) {
            int k1 = sortedK.get(i - 1);
            int k2 = sortedK.get(i);
            int k3 = sortedK.get(i + 1);
            
            double sse1 = kValuesAndErrors.get(k1);
            double sse2 = kValuesAndErrors.get(k2);
            double sse3 = kValuesAndErrors.get(k3);
            
            double firstDeriv1 = (sse2 - sse1) / (k2 - k1);
            double firstDeriv2 = (sse3 - sse2) / (k3 - k2);
            double secondDeriv = firstDeriv2 - firstDeriv1;
            
            if (secondDeriv > maxSecondDerivative) {
                maxSecondDerivative = secondDeriv;
                optimalK = k2;
            }
        }
        
        return optimalK;
    }
}
