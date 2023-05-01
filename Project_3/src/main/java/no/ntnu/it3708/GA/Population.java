/**
 * This class represent a population in the GA
 */
package no.ntnu.it3708.GA;

import no.ntnu.it3708.Parameters;
import no.ntnu.it3708.Utils.ImageHandler;
import no.ntnu.it3708.Utils.utils;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The type Population.
 */
public class Population {
    private final Integer generationNr;
    private ArrayList<Individual> individuals;
    private List<List<Individual>> paretoFronts;

    /**
     * Instantiates a new Population.
     */
    public Population() {
        this.individuals = initialPopulation();
        this.paretoFronts = new ArrayList<>();
        this.generationNr = 0;
    }

    /**
     * Gets generation nr.
     *
     * @return the generation nr
     */
    Integer getGenerationNr() {
        return generationNr;
    }

    private ArrayList<Individual> initialPopulation() {
        ArrayList<Individual> population = new ArrayList<>();

        // Generate individuals
        for (int i = 0; i < Parameters.POP_SIZE; i++) {
            Individual individual = new Individual();
            population.add(individual);
        }

        // Non-dominated sorting + crowding distance
        this.paretoFronts = nonDominatedSorting(population);
        calculateCrowdingDistance(this.paretoFronts);
        return population;
    }

    /**
     * NSGA-II loop: Creates the next generation
     *
     * @return new generation
     */
    Population nextGeneration() {
        ArrayList<Individual> offsprings = new ArrayList<>();
        for (int i = 0; i < Parameters.POP_SIZE; i++) {
            // Parent selection: tournament selection
            ArrayList<Individual> parents = parentSelection(2);

            // Crossover
            List<Segment> newGenes = crossover(parents.get(0), parents.get(1));

            // Mutation
            if (utils.randomDouble() < Parameters.MUTATION_RATE) {

            }

            // Add new offsprings
        }

        // ? Merge population ?

        // Non-dominated sorting + crowding distance calculation

        // Child selection: by crowding distance
        return null;
    }

    /**
     * This will perform parent selection by tournament.
     * The size of the tournament is controlled by the parameter TOURNAMENET_SIZE.
     *
     * @param size number of parents
     * @return parents
     */
    private ArrayList<Individual> parentSelection(int size) {
        ArrayList<Individual> parents = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            // Select individuals for tournament
            ArrayList<Individual> tournamentIndividuals = new ArrayList<>();
            Set<Integer> selectedInts = new HashSet<>();
            for (int j = 0; j < Parameters.TOURNAMENET_SIZE; j++) {
                int randIndex;
                do {
                    randIndex = utils.randomInt(Parameters.POP_SIZE);
                } while (selectedInts.contains(randIndex));
                selectedInts.add(randIndex);
                tournamentIndividuals.add(this.individuals.get(randIndex));
            }

            // Pick the best individual:
            // - Minimize rank
            // - Break tie by maxing crowding distance
            Individual best = Collections.min(tournamentIndividuals,
                    Comparator.comparing(Individual::getRank).thenComparing(Individual::getCrowdingDistance));
            parents.add(best);
        }
        return parents;
    }

    /**
     * Given two parents, we will create one child (uniformly)
     * - Combine segments from parents, shuffle/randomize segment order
     * - Add clean segments to offspring (does not contain any already added pixels)
     * -
     *
     * @param parent1 parent one
     * @param parent2 parent two
     * @return a child, or rather the childs genes.
     */
    private List<Segment> crossover(Individual parent1, Individual parent2) {
        List<Segment> newSegments = new ArrayList<>();

        // Combine segments from parents, shuffle/randomize segment order
        Map<Integer, Segment> pixelSegmentMap = new HashMap<>();
        List<Segment> combinedSegments = new ArrayList<>();
        combinedSegments.addAll(parent1.getSegments());
        combinedSegments.addAll(parent2.getSegments());
        Collections.shuffle(combinedSegments);

        // Get all "clean" segments and add them to the newSegments
        boolean[] usedPixels = new boolean[GA.pixels.size()];
        int nrAddedPixels = 0;

        for (Iterator<Segment> it = combinedSegments.iterator(); it.hasNext(); ) {
            Segment segment = it.next();
            boolean isClean = true;

            // Check if pixels in segment has already been added
            for (int pixel : segment.getPixels().keySet()) {
                if (usedPixels[pixel]) {
                    isClean = false;
                    break;
                }
            }

            // if clean, add
            if (isClean) {
                Segment newSegment = new Segment();
                for (Pixel pixel : segment.getPixels().values()) {
                    pixelSegmentMap.put(pixel.getId(), newSegment);
                    newSegment.addPixels(pixel.getId(), pixel);
                    usedPixels[pixel.getId()] = true;
                    nrAddedPixels++;
                }
                newSegments.add(newSegment);
                it.remove(); // remove added segment
            }
        }

        // Deal with remaining pixels
        for (Segment segment : combinedSegments) {
            //Check if we have added all pixels
            if (nrAddedPixels == usedPixels.length) {
                break;
            }

            // Add non-used pixels
            Segment newSegment = new Segment();
            for (Pixel pixel : segment.getPixels().values()) {
                if (!usedPixels[pixel.getId()]) {
                    pixelSegmentMap.put(pixel.getId(), newSegment);
                    newSegment.addPixels(pixel.getId(), pixel);
                    usedPixels[pixel.getId()] = true;
                    nrAddedPixels++;
                }
            }
            if (newSegment.getPixels().size() > 0) {
                newSegments.add(newSegment);
            }
        }

        // If too many segments, merge segments.
        int maxNrSegments = Math.max(parent1.getSegments().size(), parent2.getSegments().size());
        int minNrSegments = Math.min(parent1.getSegments().size(), parent2.getSegments().size());
        int nrSegments = utils.randomInt(minNrSegments, maxNrSegments);
        if (nrSegments < newSegments.size()) {
            mergeSegments(newSegments, nrSegments, pixelSegmentMap);
        }
        return newSegments;
    }

    /**
     * Merge neighboring segments with the lowest color distance
     *
     * @param segments   segments to operate on
     * @param nrSegments reduce nr. segments to this number
     */
    private void mergeSegments(List<Segment> segments, int nrSegments, Map<Integer, Segment> pixelSegmentMap) {
        while (segments.size() > nrSegments) {
            // Calculate centroids for every segment
            for (Segment segment : segments) {
                segment.findCentroid();
            }

            // find the two segments to combine
            Segment s1 = null, s2 = null;
            double minDistance = Double.MAX_VALUE;

            for (Segment segment : segments) {
                List<Segment> neighboringSegments = findNeighboringSegments(segments, segment, pixelSegmentMap);
                for (Segment neighboringSegment : neighboringSegments) {
                    double distance = ImageHandler.colorDistance(segment.getCentroid(), neighboringSegment.getCentroid());
                    if (distance < minDistance) {
                        minDistance = distance;
                        s1 = segment;
                        s2 = neighboringSegment;
                    }
                }
            }

            // Combine: Add s2 to s1
            for (Pixel pixel : s2.getPixels().values()) {
                pixelSegmentMap.put(pixel.getId(), s1); // update map
                s1.addPixels(pixel.getId(), pixel);
            }
            segments.remove(s2);
        }
    }

    /**
     * Find all the segments neighbors
     *
     * @param segments all segments
     * @param segment  segment to find neighbors to
     * @return list of neighboring segments
     */
    private List<Segment> findNeighboringSegments(List<Segment> segments, Segment segment,
                                                  Map<Integer, Segment> pixelSegmentMap) {
        Set<Segment> neighboringSegments = new HashSet<>();

        // Iterate over the pixels to find neighboring segments
        for (Pixel pixel : segment.getPixels().values()) {
            // Check all edges, same segment?
            for (Node node : pixel.getNeighbors()) {
                Segment nodeSegment = pixelSegmentMap.get(node.getNeighboringPixel().getId());
                if (nodeSegment != segment) {
                    neighboringSegments.add(nodeSegment);
                }
            }
        }
        return new ArrayList<>(neighboringSegments);
    }

    /**
     * Performs non-dominated sorting and updates the individuals rank.
     * It will rank the individuals based on how much it dominates the others.
     *
     * @param individuals
     */
    private List<List<Individual>> nonDominatedSorting(ArrayList<Individual> individuals) {
        List<List<Individual>> paretoFronts = new ArrayList<>();

        // Iterate over the individuals and rank them
        AtomicInteger rank = new AtomicInteger(1);
        ArrayList<Individual> remainingIndividuals = new ArrayList<>(individuals);
        while (!remainingIndividuals.isEmpty()) {
            List<Individual> currDominatingIndividuals = findDominatingIndividuals(remainingIndividuals);
            currDominatingIndividuals.forEach(i -> i.setRank(rank.get()));
            remainingIndividuals.removeAll(currDominatingIndividuals);
            rank.incrementAndGet();
            paretoFronts.add(currDominatingIndividuals);
        }
        return paretoFronts;
    }

    /**
     * This method will find all the individuals that are dominating.
     *
     * @param individuals individuals to compare
     * @return list of dominating individuals
     */
    private List<Individual> findDominatingIndividuals(ArrayList<Individual> individuals) {
        List<Individual> dominatingIndividuals = new ArrayList<>();

        // Iterate over the individuals
        for (Individual individual : individuals) {
            boolean isDominated = false;

            // use iterators so we can modify the list while iterating
            for (Iterator<Individual> it = dominatingIndividuals.iterator(); it.hasNext(); ) {
                Individual dominatingInd = it.next(); // get a dominating Individual to compare

                // if same, go next
                if (individual == dominatingInd) {
                    continue;
                }

                // if current individual got dominated, break
                if (dominatingInd.dominates(individual)) {
                    isDominated = true;
                    break;
                }

                // Current individual dominates
                // old individual no longer dominates, remove
                if (individual.dominates(dominatingInd)) {
                    it.remove();
                }
            }

            // if individual was not dominated, add to list
            if (!isDominated) {
                dominatingIndividuals.add(individual);
            }
        }
        return dominatingIndividuals;
    }

    private void calculateCrowdingDistance(List<List<Individual>> paretoFronts) {
        for (List<Individual> front : paretoFronts) {
            // reset distances
            for (Individual individual : front) {
                individual.setCrowdingDistance(0);
            }

            // assign crowding distance for the objectives
            for (Objectives segmentationCriteria : Objectives.values()) {
                assignCrowdingDistanceObjective(front, segmentationCriteria);
            }

        }
    }

    private void assignCrowdingDistanceObjective(List<Individual> paretoFront, Objectives segmentationCriteria) {
        // sort: lowest -> highest
        paretoFront.sort(Objectives.getComparator(segmentationCriteria));

        // Get min and max
        Individual minInd = paretoFront.get(0);
        Individual maxInd = paretoFront.get(paretoFront.size() - 1);

        // Set max and min
        minInd.setCrowdingDistance(Integer.MAX_VALUE);
        maxInd.setCrowdingDistance(Integer.MAX_VALUE);

        // find diff between min and max
        double diff = Objectives.getObjective(segmentationCriteria, maxInd)
                - Objectives.getObjective(segmentationCriteria, minInd);

        // Iterate over the individuals
        double segCriteriaDiff;
        for (int i = 1; i < paretoFront.size() - 1; i++) {
            Double distance = Objectives.getObjective(segmentationCriteria, paretoFront.get(i))
                    - Objectives.getObjective(segmentationCriteria, paretoFront.get(i - 1)) / diff;
            paretoFront.get(i).setCrowdingDistance(paretoFront.get(i).getCrowdingDistance() + distance);
        }
    }

    @Override
    public String toString() {
        return "Population{" +
                "generationNr=" + generationNr +
                // ", paretoIndividuals=" + paretoFronts.get(0).stream()
                // .map(Individual::toString)
                // .collect(Collectors.toList())
                // +
                '}';
    }
}
