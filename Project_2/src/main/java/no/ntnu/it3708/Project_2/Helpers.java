package no.ntnu.it3708.Project_2;

import com.google.gson.Gson;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Helpers {

    public static Integer getRandomPatientIndex(ArrayList<Integer> patients, Random random) {
        int patientIdx = 0;
        if (patients.size() != 0) {
            patientIdx = random.nextInt(patients.size());
        }
        return patientIdx;
    }

    public static void printSolution(DataHandler data, Individual bestIndividual) {
        // print
        HashMap<Integer, ArrayList<Integer>> bitstring = bestIndividual.getBitstring();
        for (int nurseIdx = 0; nurseIdx < data.getNbr_nurses(); nurseIdx++) {
            System.out.println("Nurse " + nurseIdx);

            int currentPos = 0; // Depot
            double startTime = 0;
            int totalDemand = 0;

            System.out.print(currentPos + " (" + startTime + ") -> ");
            for (Integer patientIdx : bitstring.get(nurseIdx)) {
                // Get patient details
                DataHandler.Patient patient = data.getPatients().get(patientIdx);

                // Set arrival_time given the patients start_time
                double arrival_time = startTime + data.getTravel_times().get(currentPos).get(patientIdx);
                ;
                if (arrival_time < patient.getStart_time()) {
                    // Must wait for time window, update arrival time
                    arrival_time = (double) patient.getStart_time();
                }
                double care_time_finish = arrival_time + patient.getCare_time();

                // Print
                System.out.print(patientIdx + " (" + arrival_time + "-" + care_time_finish + ") ["
                        + patient.getStart_time() + "-" + patient.getEnd_time() + "] -> ");

                // Update variables
                currentPos = patientIdx;
                startTime = care_time_finish;
                totalDemand += patient.getDemand();
            }

            // travel time back to depot
            double routeEndTime = startTime + data.getTravel_times().get(currentPos).get(0);
            System.out.println("0 (" + routeEndTime + ")");
            System.out.println("Duration: " + routeEndTime);
            System.out.println("Demand: " + totalDemand);
            System.out.println();
        }
        System.out.println("Objective value: " + bestIndividual.getFitness());

        // Draw map
        Gson gson = new Gson();
        System.out.println("\n\nCopy here:");

        // Print routes:
        System.out.print("{\"Routes\":" + gson.toJson(bestIndividual.getBitstring()));

        // Print patients
        System.out.print(",\"Patients\":{");
        boolean firstElement = true;
        for (DataHandler.Patient patient : data.getPatients().values()) {
            if (!firstElement) {
                System.out.print(",");
            }
            firstElement = false;
            System.out.print("\"" + patient.getId() + "\":{\"x\":" + patient.getX_coord() + ",\"y\":"
                    + patient.getY_coord() + "}");
        }
        System.out.print("}");

        // Print depot
        System.out.print(",\"Depot\":{\"x\":" + data.getDepot().getX_coord() + ", \"y\":"
                + data.getDepot().getY_coord());
        System.out.print("}}");
    }

    /**
     * Generate a random bitstring:
     * [
     * [1,5,7], // nurse 1 visits patient 1, 5 and 7
     * [2,8,4] // nurse 2 visits patient 2, 8 and 4
     * ]
     *
     * @return the bitstring
     */
    public static HashMap<Integer, ArrayList<Integer>> generate_bitstring_random(DataHandler data, Random random, long timeout) throws TimeoutException {
        // timeout
        final long startTime = System.nanoTime();
        final long timeoutNanos = TimeUnit.MILLISECONDS.toNanos(timeout);

        // create bitstring
        HashMap<Integer, ArrayList<Integer>> bitstring = create_bitstring(data);

        // Prepare list of patients
        ArrayList<DataHandler.Patient> patients = new ArrayList<>(data.getPatients().values());
        patients.sort(Comparator.comparing(DataHandler.Patient::getStart_time));

        // Prepare nurses
        ArrayList<Nurse> nurses = new ArrayList<>();
        for (int i = 0; i < data.getNbr_nurses(); i++) {
            Nurse nurse = new Nurse(i, data.getCapacity_nurse());
            nurses.add(nurse);
        }

        // Assign patients to nurse
        for (DataHandler.Patient patient : patients) {
            // Logic: Select random nurse. Ok -> assign. Notok -> new nurse
            boolean foundNurse = false;
            while (!foundNurse) {
                // Reached timeout
                final long elapsedNanos = System.nanoTime() - startTime;
                final long timeLeftNanos = timeoutNanos - elapsedNanos;
                if (timeLeftNanos <= 0) {
                    throw new TimeoutException();
                }

                Nurse nurse = nurses.get(random.nextInt(nurses.size()));

                // Update arrival_time
                Double arrival_time = nurse.getOccupied_until()
                        + data.getTravel_times().get(nurse.getPosition()).get(patient.getId());
                if (arrival_time < patient.getStart_time()) {
                    arrival_time = (double) patient.getStart_time();
                }

                // finish within the time window
                double end_time = arrival_time + patient.getCare_time();
                if (end_time > patient.getEnd_time()) {
                    continue;
                }

                // Still has capacity
                if (nurse.getCapacity() < patient.getDemand()) {
                    continue;
                }

                // Finish before depot limit
                Double depot_arrival_time = end_time + data.getTravel_times().get(nurse.getPosition()).get(0);
                if (depot_arrival_time > data.getDepot().getReturn_time()) {
                    continue;
                }

                // Use this nurse
                foundNurse = true;

                // Assign nurse
                nurse.setPosition(patient.getId());
                nurse.reduceCapacity(patient.getDemand());
                nurse.setOccupied_until(end_time);

                // Update bitstring
                bitstring.get(nurse.getId()).add(patient.getId());
            }
        }
        return bitstring;
    }

    /**
     * Generate a bitstring using the heuristic in data: clusters of patients
     *
     * @return the bitstring
     */
    public static HashMap<Integer, ArrayList<Integer>> generate_bitstring_heuristic(ArrayList<DataHandler.Cluster> clusters, DataHandler data, Random random, long timeout) throws TimeoutException {
        // timeout
        final long startTime = System.nanoTime();
        final long timeoutNanos = TimeUnit.MILLISECONDS.toNanos(timeout);

        // create bitstring
        HashMap<Integer, ArrayList<Integer>> bitstring = create_bitstring(data);

        // Prepare nurses
        ArrayList<Nurse> nurses = new ArrayList<>();
        for (int i = 0; i < data.getNbr_nurses(); i++) {
            Nurse nurse = new Nurse(i, data.getCapacity_nurse());
            nurses.add(nurse);
        }

        // Iterate over the clusters and assign nurses
        // Set randomness variables
        // Sort clusters
        List<String> clusterSortOptions = Arrays.asList("shuffle", "demand", "start_time");
        String selectedClusterSortOption = clusterSortOptions.get(random.nextInt(clusterSortOptions.size()));
        switch (selectedClusterSortOption) {
            case "shuffle":
                Collections.shuffle(clusters, random);
                break;
            case "demand":
                clusters.sort(Comparator.comparing(DataHandler.Cluster::getDemand));
                break;
            case "start_time":
                clusters.sort(Comparator.comparing(DataHandler.Cluster::getStart_time));
                break;
            default:
                throw new IllegalArgumentException("Invalid sort option: " + selectedClusterSortOption);
        }

        // Sort patients
        List<String> patientSortOptions = Arrays.asList("shuffle", "demand", "range", "start_time");
        String selectedPatientSortOption = patientSortOptions.get(random.nextInt(patientSortOptions.size()));

        // Sort nurses
        Boolean sortNurses = random.nextBoolean();

        for (DataHandler.Cluster cluster : clusters) {
            // Get cluster
            ArrayList<DataHandler.Patient> cluster_patients = cluster.getPatients();

            // Sort patients
            switch (selectedPatientSortOption) {
                case "shuffle":
                    Collections.shuffle(cluster_patients);
                    break;
                case "demand":
                    cluster_patients.sort(Comparator.comparing(DataHandler.Patient::getDemand));
                    break;
                case "range":
                    cluster_patients.sort(Comparator.comparing(DataHandler.Patient::getRange));
                    break;
                case "start_time":
                    cluster_patients.sort(Comparator.comparing(DataHandler.Patient::getStart_time));
                    break;
                default:
                    throw new IllegalArgumentException("Invalid sort option: " + selectedPatientSortOption);
            }

            // Assign nurses
            // Logic: Have a list of nurses. Iterate over patients
            // - Select patient
            // - Check if nurse(s) at location is available
            // - else: pop off new nurse from stack
            // - loop
            // No more patient, move the nurses to top (last) of stack
            ArrayList<Nurse> cluster_nurses = new ArrayList<>();
            for (DataHandler.Patient patient : cluster_patients) {
                Integer nurse_idx = -1;
                Nurse nurse = null;
                Double end_time = 0d;

                Boolean foundNurse = false;
                while (!foundNurse) {
                    // Reached timeout
                    final long elapsedNanos = System.nanoTime() - startTime;
                    final long timeLeftNanos = timeoutNanos - elapsedNanos;
                    if (timeLeftNanos <= 0) {
                        throw new TimeoutException();
                    }

                    // select nurse
                    nurse_idx++;
                    try {
                        // Try to get the nurse
                        nurse = cluster_nurses.get(nurse_idx);
                    } catch (IndexOutOfBoundsException exception) {
                        // Does not exist, try to add a new nurse
                        try {
                            nurse = nurses.get(nurse_idx);
                            cluster_nurses.add(nurse);
                        } catch (EmptyStackException exception1) {
                            System.out.println("No more nurses");
                        }
                    }

                    // Update arrival_time
                    Double arrival_time = nurse.getOccupied_until()
                            + data.getTravel_times().get(nurse.getPosition()).get(patient.getId());
                    if (arrival_time < patient.getStart_time()) {
                        arrival_time = (double) patient.getStart_time();
                    }

                    // finish within the time window
                    end_time = arrival_time + patient.getCare_time();
                    if (end_time > patient.getEnd_time()) {
                        continue;
                    }

                    // Still has capacity
                    if (nurse.getCapacity() < patient.getDemand()) {
                        continue;
                    }

                    // Finish before depot limit
                    Double depot_arrival_time = end_time + data.getTravel_times().get(nurse.getPosition()).get(0);
                    if (depot_arrival_time > data.getDepot().getReturn_time()) {
                        continue;
                    }

                    // Use this nurse
                    foundNurse = true;
                }

                // Assign nurse
                nurse.setPosition(patient.getId());
                nurse.reduceCapacity(patient.getDemand());
                nurse.setOccupied_until(end_time);

                // Update bitstring
                bitstring.get(nurse.getId()).add(patient.getId());
            }

            // Move nurses
            for (int i = 0; i < cluster_nurses.size(); i++) {
                nurses.remove(i); // Remove used nurses
                nurses.add(cluster_nurses.get(i)); // add back at the end
            }

            // Sort nurses
            if (sortNurses == true) {
                nurses.sort(Comparator.comparing(Nurse::getOccupied_until));
            }
        }
        return bitstring;
    }

    /**
     * Create a bitstring representation where each key represent a nurse and the
     * arraylist represent visited patients
     *
     * @return the bitstring
     */
    public static HashMap<Integer, ArrayList<Integer>> create_bitstring(DataHandler data) {
        // Prepare bitstring
        HashMap<Integer, ArrayList<Integer>> bitstring = new HashMap<>();
        for (int i = 0; i < data.getNbr_nurses(); i++) {
            bitstring.put(i, new ArrayList<>());
        }
        return bitstring;
    }
}
