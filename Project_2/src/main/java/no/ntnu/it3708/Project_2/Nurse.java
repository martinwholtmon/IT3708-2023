package no.ntnu.it3708.Project_2;

/**
 * The type Nurse.
 */
public class Nurse {
    private int id;
    private int capacity;
    private int position;
    private double occupied_until;

    /**
     * Instantiates a new Nurse.
     *
     * @param id       the id
     * @param capacity the capacity
     */
    public Nurse(int id, int capacity) {
        this.id = id;
        this.capacity = capacity;
        this.position = 0;
        this.occupied_until = 0;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets capacity.
     *
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Reduce capacity.
     *
     * @param capacity the reduction in capacity
     */
    public void reduceCapacity(int capacity) {
        this.capacity -= capacity;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets position.
     *
     * @param position the position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Gets occupied until.
     *
     * @return the occupied until
     */
    public double getOccupied_until() {
        return occupied_until;
    }

    /**
     * Sets occupied until.
     *
     * @param occupied_until the occupied until
     */
    public void setOccupied_until(double occupied_until) {
        this.occupied_until = occupied_until;
    }
}
