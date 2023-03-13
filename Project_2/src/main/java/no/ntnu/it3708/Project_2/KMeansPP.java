package no.ntnu.it3708.Project_2;

import java.util.*;

/**
 * The type k-means++. Will assign clusters to the patients
 */
public class KMeansPP {
    private int n_clusters;
    private int n_iterations;
    private List<DataHandler.Patient> patients;
    private HashMap<Integer, ArrayList<DataHandler.Patient>> cluster;

    /**
     * Instantiates a new K means pp.
     *
     * @param n_clusters   the n clusters
     * @param n_iterations the n iterations
     * @param patients     the patients
     */
    public KMeansPP(int n_clusters, int n_iterations, HashMap<Integer, DataHandler.Patient> patients) {
        this.n_clusters = n_clusters;
        this.n_iterations = n_iterations;
        this.patients = new ArrayList<>(patients.values());
        this.cluster = null;
    }

    /**
     * Run the k-means++
     */
    public HashMap<Integer, ArrayList<DataHandler.Patient>> run() {
        List<Point> centroids = getInitialCentroids();
        this.cluster = null;

        for (int i=0; i<n_iterations; i++) {
            System.out.println(i);
            // Assign clusters
            this.cluster = assignPatientToCluster(centroids);

            // Calculate new centroids
            List<Point> newCentroids = getNewCentroids(cluster);

            if (centroids.equals(newCentroids)) {
                break;
            }
            centroids = newCentroids;
        }

        return this.cluster;
    }

    public int getN_clusters() {
        return n_clusters;
    }

    /**
     * Will get the initial cluster centroids
     * @return list of cluster centers (centroids)
     */
    private List<Point> getInitialCentroids() {
        Random random = new Random();
        List<Point> centroids = new ArrayList<>();

        // Add first random point
        DataHandler.Patient initial_patient = patients.get(random.nextInt(patients.size()));
        centroids.add(new Point(initial_patient.getX_coord(), initial_patient.getY_coord()));

        // Add the rest of the points
        while (centroids.size() < n_clusters) {
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
            double distance = Math.sqrt(Math.pow(patient.getX_coord() - centroid.x, 2) + Math.pow(patient.getY_coord() - centroid.y, 2));
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
    private HashMap<Integer, ArrayList<DataHandler.Patient>> assignPatientToCluster(List<Point> centroids) {
        // Init cluster
        HashMap<Integer, ArrayList<DataHandler.Patient>> cluster = new HashMap<>();
        for (int i = 0; i < centroids.size(); i++) {
            cluster.put(i, new ArrayList<>());
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
            cluster.get(closestCentroidIdx).add(patient);
        }
        return cluster;
    }

    /**
     * Will calculate the new centroids given the clusters
     * @param cluster Clusters with patients
     * @return new cluster centers (centroids)
     */
    private List<Point> getNewCentroids(HashMap<Integer, ArrayList<DataHandler.Patient>> cluster) {
        List<Point> centroids = new ArrayList<>();

        for (int i = 0; i < cluster.size(); i++) {
            ArrayList<DataHandler.Patient> cluster_patients = cluster.get(i);
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
