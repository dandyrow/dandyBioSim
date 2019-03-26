import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * A simple model of a lion.
 * Lions age, move, eat rabbits (if they're female) and foxes (if they're male), fight (male vs male) and die.
 *
 * @author David J. Barnes, Michael Kölling and Daniel Lowry
 * @version 1.0
 */
public class Lion extends Animal {
    // Characteristics shared by all lions (class variables).

    // The age at which a lion can start to breed.
    private static int BREEDING_AGE;
    // The age to which a lion can live.
    private static final int MAX_AGE = 70;
    // The likelihood of a lion breeding.
    private static double BREEDING_PROBABILITY;
    // The maximum number of births.
    private static int MAX_LITTER_SIZE;
    // The food value of a single fox and rabbit. In effect, this is the
    // number of steps a lion can go before it has to eat again.
    private static int FOX_FOOD_VALUE;
    private static int RABBIT_FOOD_VALUE;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

    // Individual characteristics (instance fields).
    // The lion's age.
    private int age;
    // The lion's food level, which is increased by eating rabbits.
    private int foodLevel;
    // The lion's gender.
    private int gender;

    /**
     * Create a new lion at location in field. A lion can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     *
     * @param randomAge If true, the lion will have random age and hunger level.
     * @param field    The field currently occupied.
     * @param location The location within the field.
     * @param ba Breeding age passed from settings tab.
     * @param bp Breeding probability passed from settings tab.
     * @param mls Max litter size passed from settings tab.
     * @param rfv Rabbit food value passed from settings tab.
     * @param ffv Fox food value passed from settings tab.
     */
    public Lion(boolean randomAge, Field field, Location location, int ba, double bp, int mls, int rfv, int ffv) {
        super(field, location);
        BREEDING_AGE = ba;
        BREEDING_PROBABILITY = bp;
        MAX_LITTER_SIZE = mls;
        RABBIT_FOOD_VALUE = rfv;
        FOX_FOOD_VALUE = ffv;
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(FOX_FOOD_VALUE);
            gender = rand.nextInt(2);

        }
        else {
            age = 0;
            foodLevel = RABBIT_FOOD_VALUE;
            gender = rand.nextInt(2);
        }
    }

    /**
     * This is what the lion does most of the time: it hunts for
     * foxes and rabbits. In the process, it might breed, fight other male lions,
     * die of hunger, or die of old age.
     * @param newLions A list to return newly born lions.
     */
    public void act(List<Animal> newLions) {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newLions);
            //Move towards a source of food if found.
            Location newLocation = findFood();
            Location newLocation2 = fight();
            if(newLocation == null && newLocation2 == null)
                //No food found or males to fight - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());

            //see if it was possible to move.
            if(newLocation != null)
                setLocation(newLocation);
            else if(newLocation2 != null)
                setLocation(newLocation2);
            else
                //overcrowding
                setDead();
        }
    }

    /**
     * Increase the age. This could result in the lion's death.
     */
    private void incrementAge() {
        age++;
        if(age > MAX_AGE)
            setDead();
    }

    //Make this lion more hungry. This could result in the lion's death.
    private void incrementHunger() {
        foodLevel--;
        if(foodLevel <=0) {
            setDead();
        }
    }

    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live fox or rabbit is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Fox && gender == 1) {
                Fox fox = (Fox) animal;
                if(fox.isAlive()) {
                    fox.setDead();
                    foodLevel = FOX_FOOD_VALUE;
                    //Remove dead fox from field.
                    return where;
                }
            }
            else if(animal instanceof Rabbit && gender == 0) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) {
                    rabbit.setDead();
                    foodLevel = RABBIT_FOOD_VALUE;
                    //Remove dead rabbit from field.
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Check whether or not this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newLions A list to return newly born lions.
     */
    private void giveBirth(List<Animal> newLions) {
        //New lions are born into adjacent locations.
        //Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for (int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Lion young = new Lion(false, field, loc, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE, RABBIT_FOOD_VALUE, FOX_FOOD_VALUE);
            newLions.add(young);
        }
    }

    /**#
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed() {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

    /**
     * A lion can breed if it has reached the breeding age and is female.
     * @return true if the lion can breed, false otherwise.
     */
    private boolean canBreed() { return age >= BREEDING_AGE && gender == 0; }

    /**
     * Check whether there is an adjacent male lion. If so kill it.
     * @return Where other lion was or null if there wasn't one.
     */
    private Location fight() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object animal = field.getObjectAt(where);
            if (animal instanceof Lion && gender == 1) {
                Lion lion = (Lion) animal;
                if (lion.isAlive() && lion.gender == 1) {
                    lion.setDead();
                    return where;
                }
            }
        }
        return null;
    }
}
