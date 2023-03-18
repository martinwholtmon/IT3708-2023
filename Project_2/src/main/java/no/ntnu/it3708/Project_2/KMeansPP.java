package no.ntnu.it3708.Project_2;

import java.util.*;

/**
 * The type k-means++. Will assign clusters to the patients
 */
public class KMeansPP {
    private int n_clusters;
    private int n_iterations;
    private List<DataHandler.Patient> patients;

    /**
     * Instantiates a new K means pp.
     *
     * @param n_clusters   the n clusters (max)
     * @param n_iterations the n iterations
     * @param patients     the patients
     */
    public KMeansPP(int n_clusters, int n_iterations, HashMap<Integer, DataHandler.Patient> patients) {
        this.n_clusters = n_clusters;
        this.n_iterations = n_iterations;
        this.patients = new ArrayList<>(patients.values());
    }

    /**
     * Run the K-means++ algorithm. Using the elbow method to select the best k value given the tolerance
     * @param tolerance  change in the sum of squared distances between k-values
     * @return The cluster assignment
     */
    public HashMap<Integer, Cluster> run(double tolerance) {
        int k = 1;
        double prevTotalDistance = Double.MAX_VALUE;
        HashMap<Integer, Cluster> cluster = null;


        // Normalize
        double[] means = new double[2];
        double[] stdDevs = new double[2];

        // get Mean
        for (DataHandler.Patient patient : patients) {
            means[0] += patient.getX_coord();
            means[1] += patient.getY_coord();
        }
        means[0] /= patients.size();
        means[1] /= patients.size();

        // get std
        for (DataHandler.Patient patient : patients) {
            stdDevs[0] += Math.pow(patient.getX_coord() - means[0], 2);
            stdDevs[1] += Math.pow(patient.getY_coord() - means[1], 2);
        }
        stdDevs[0] = Math.sqrt(stdDevs[0] / patients.size());
        stdDevs[1] = Math.sqrt(stdDevs[1] / patients.size());

        // Do the normalization on patients
        for (DataHandler.Patient patient : patients) {
            patient.setX_coord((patient.getX_coord() - means[0])/ stdDevs[0]);
            patient.setY_coord((patient.getY_coord() - means[1])/ stdDevs[1]);
        }


        // Elbow method
        while (k <= n_clusters) {
            HashMap<Integer, Cluster> tempCluster = kMeansPP(k);
            double totalDistance = getTotalDistanceToCentroids(tempCluster);

            // We have the best solution, quit
            if (Math.abs(prevTotalDistance - totalDistance) < tolerance) {
                break;
            }

            // Continue searching
            cluster = tempCluster;
            prevTotalDistance = totalDistance;
            k++;
        }
        System.out.println(k);

        // Undo normalization
        for (int i = 0; i<cluster.size(); i++) {
            Cluster c = cluster.get(i);

            // centroid
            c.centroid.x *= stdDevs[0] + means[0];
            c.centroid.x *= stdDevs[1] + means[1];

            // patients
            for (DataHandler.Patient patient : c.getMembers()) {
                patient.setX_coord(patient.getX_coord() * stdDevs[0] + means[0]);
                patient.setY_coord(patient.getY_coord() * stdDevs[1] + means[1]);
            }
        }
        return cluster;
    }

    private HashMap<Integer, Cluster> kMeansPP(int k) {
        List<Point> centroids = getInitialCentroids(k);
        HashMap<Integer, Cluster> cluster = null;

        for (int i=0; i<n_iterations; i++) {
            // Assign Patients to clusters
            cluster = assignPatientToCluster(centroids);

            // Calculate new centroids
            List<Point> newCentroids = getNewCentroids(cluster);

            if (centroids.equals(newCentroids)) {
                break;
            }
            centroids = newCentroids;
        }
        return cluster;
    }

    private double getTotalDistanceToCentroids(HashMap<Integer, Cluster> clusters) {
        double totalDistance = 0;

        // Iterate over clusters
        for (Map.Entry<Integer, Cluster> entry : clusters.entrySet()) {
            int cluster_idx = entry.getKey();
            Cluster cluster = entry.getValue();
            Point centroid = cluster.getCentroid();

            // Iterate over patients
            for (DataHandler.Patient patient : cluster.getMembers()) {
                totalDistance += getDistanceBetweenPoints(patient.getX_coord(), patient.getY_coord(), centroid.x, centroid.y);
            }
        }
        return totalDistance;
    }

    private double getDistanceBetweenPoints(double x, double y, double x2, double y2) {
        return Math.sqrt(Math.pow(x - x2, 2) + Math.pow(y - y2, 2));
    }

    /**
     * Will get the initial cluster centroids
     * @return list of cluster centers (centroids)
     */
    private List<Point> getInitialCentroids(int k) {
        Random random = new Random();
        List<Point> centroids = new ArrayList<>();

        // Add first random point
        DataHandler.Patient initial_patient = patients.get(random.nextInt(patients.size()));
        centroids.add(new Point(initial_patient.getX_coord(), initial_patient.getY_coord()));

        // Add the rest of the points
        while (centroids.size() < k) {
            Point point = null;
            double maxDistance = 0;

            // find point that is the furthest away from all centroids
            for (DataHandler.Patient patient : patients) {
                // calculate the minimum distance to all centroid
                double distance = getMinimumDistanceToCentroids(patient, centroids);

                // further away? Update
                if (distance > maxDistance) {
                    point = new Point(patient.getX_coord(), patient.getY_coord());
                    maxDistance = distance;
                }
            }
            centroids.add((point));
        }
        return centroids;
    }

    /**
     * Find the minimum distance from a point to all the centroids.
     * @param patient   The point
     * @param centroids List of centroid
     * @return  minimum distance to the centroids
     */
    private double getMinimumDistanceToCentroids(DataHandler.Patient patient, List<Point> centroids) {
        double minDistance = Double.MAX_VALUE;
        for (Point centroid : centroids) {
            double distance = getDistanceBetweenPoints(patient.getX_coord(), patient.getY_coord(), centroid.x, centroid.y);
            if (distance < minDistance) {
                minDistance = distance;
            }
        }
        return minDistance;
    }


    /**
     * Will assign patient to clusters
     * @param centroids List of centroids
     * @return Hashmap of clusters containing patients
     */
    private HashMap<Integer, Cluster> assignPatientToCluster(List<Point> centroids) {
        // Init cluster
        HashMap<Integer, Cluster> cluster = new HashMap<>();
        for (int i = 0; i < centroids.size(); i++) {
            cluster.put(i, new Cluster(i, centroids.get(i)));
        }

        // Assign patient to cluster
        for (DataHandler.Patient patient : patients) {
            int closestCentroidIdx = -1;
            double minDistance = Double.MAX_VALUE;
            for (int i = 0; i < centroids.size(); i++) {
                double distance = Math.sqrt(Math.pow(patient.getX_coord() - centroids.get(i).x, 2) + Math.pow(patient.getY_coord() - centroids.get(i).y, 2));
                if (distance < minDistance) {
                    closestCentroidIdx = i;
                    minDistance = distance;
                }
            }

            // Assign patient to cluster
            cluster.get(closestCentroidIdx).addMember(patient);
        }
        return cluster;
    }

    /**
     * Will calculate the new centroids given the clusters
     * @param cluster Clusters with patients
     * @return new cluster centers (centroids)
     */
    private List<Point> getNewCentroids(HashMap<Integer, Cluster> cluster) {
        List<Point> centroids = new ArrayList<>();

        for (int i = 0; i < cluster.size(); i++) {
            ArrayList<DataHandler.Patient> cluster_patients = cluster.get(i).getMembers();
            double sumX = 0;
            double sumY = 0;

            // Iterate over all the patients in the clusters
            for (DataHandler.Patient patient : cluster_patients) {
                sumX += patient.getX_coord();
                sumY += patient.getY_coord();
            }
            centroids.add(new Point(sumX / cluster_patients.size(), sumY / cluster_patients.size()));
        }
        return centroids;
    }

    class Cluster {
        private int id;
        private Point centroid;
        private ArrayList<DataHandler.Patient> members;

        public Cluster(int id, Point centroid) {
            this.id = id;
            this.centroid = centroid;
            this.members = new ArrayList<>();
        }

        public int getId() {
            return id;
        }

        public Point getCentroid() {
            return centroid;
        }

        public ArrayList<DataHandler.Patient> getMembers() {
            return members;
        }

        public void addMember(DataHandler.Patient member) {
            this.members.add(member);
        }
    }

    private class Point {
        /**
         * The X.
         */
        public double x;
        /**
         * The Y.
         */
        public double y;

        /**
         * Instantiates a new Point.
         */
        public Point() {
        }

        /**
         * Instantiates a new Point.
         *
         * @param x the x
         * @param y the y
         */
        public Point(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
