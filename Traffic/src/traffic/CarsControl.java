
package traffic;


import java.util.*;

public class CarsControl 
{
    private int numCars = 20; //no more than 100
    private int numCarsGoal = 20;
    private boolean newCarFlag = false;
    private Grid grid = new Grid();
    private ArrayList carArray = new ArrayList();
    private Car carBuffer;
    private int[][] occupancyMatrix = new int[37][37];
    Random rand = new Random();
    
    public CarsControl() //calls the functions to create the occupancy matrix, and spawn the first set of cars
    {
        initializeOccMatrix();
        spawnStartingCars();
    }
    
    public void removeCarFromList(int index) //removes specified car from the arraylist
    {
        carArray.remove(index);
    }
    
    public void reduceNumCarsByOne() //reduces the numcars variable by one
    {
        numCars--;
    }
    
    /**
    public void printOM() //only used for debugging
    {
        int i, j;
        System.out.println("Current OM");
        for(i=0; i<37; i++)
        {
            for(j=0; j<37; j++)
            {
                System.out.print(occupancyMatrix[i][j]);
                
            }
            System.out.println();
        }
    }
    */
    
    private void initializeOccMatrix() //set all elements to 0 to begin with
    {
        int i, j;
        
        for(i = 0; i < 37; i++)
            for(j = 0; j < 37; j++)
            {
                occupancyMatrix[i][j] = 0;
            }
    }
    
    private void spawnStartingCars() //spawns the first set of cars
    {
        
        int i, j, sR, sC, dR, dC, dir;
        
        for(i = 0; i < numCars; i++) //create initial cars
        {
            sR = sC = dR = dC = dir = 0;
            while(sR == dR && sC == dC) //while start and destination are the same node, retry
            {
                sR = rand.nextInt(10);
                sC = rand.nextInt(10);
                dR = rand.nextInt(10);
                dC = rand.nextInt(10);
            }
            
            for(j = 0; j <= i; j++) //check to make sure the car being generated is on an unused intersection
            {
                if(j == i) //we check all J elements up to I, if J hasn't failed before reaching I then there are no duplicates of the starting values we are setting
                {
                    carBuffer = new Car(); //create the car object
                    carBuffer.startTimer();
                    carBuffer.setPath(grid.path((10*sR + sC), (10*dR + dC))); //set the path since grid is created in this class and not for each car created
                    //System.out.println("Car: "+i+" new path: "+carBuffer.getPath());
                    carBuffer.getNextIntersection();
                    carBuffer.removeIntersectionFromPath();
                    carBuffer.setOMLocation(4*sR, 4*sC); //save the OM location directly to the car for easy association
                    occupancyMatrix[4*sR][4*sC] = 1; //set location in OM to 1, for being occupied
                    if( sR > carBuffer.getCurrentDest()/10)
                        dir = 0;//north
                    else if(sR < carBuffer.getCurrentDest()/10)
                        dir = 2;//south
                    else if(sC < carBuffer.getCurrentDest()%10)
                        dir = 1;//east
                    else if(sC > carBuffer.getCurrentDest()%10)
                        dir = 3;//west
                    
                    carBuffer.setDirection(dir);
                    
                    carArray.add(carBuffer);
                }
                else if(occupancyMatrix[4*sR][4*sC] == 1) //this will check to see if the new car's starting values already exist in our car array
                {
                    i--; //keep trying to fill this element until successful
                    break;
                }
            }
        }
    }
    
    public void checkNumCarsGoal() //checks to see if the goal of numcars is greater than the current value, and creates new cars if needed
    {
        if(numCarsGoal > numCars)
        {
            
            int i, sR, sC, dR, dC, dir;
            boolean spawn = false;
            Car checkBufferAgainst;
            
            while(spawn == false) //create initial cars
            {
                spawn = true;
                sR = sC = dR = dC = dir = 0;
                while(sR == dR && sC == dC) //while start and destination are the same node, retry
                {
                    sR = rand.nextInt(10);
                    sC = rand.nextInt(10);
                    dR = rand.nextInt(10);
                    dC = rand.nextInt(10);
                }
            
                for(i = 0; i < numCars; i++)
                {
                    checkBufferAgainst = (Car)carArray.get(i);
                    if(checkBufferAgainst.getOMI() == 4*sR && checkBufferAgainst.getOMJ() == 4*sC)
                        spawn = false;
                }
            
                if(spawn)
                {
                    carBuffer = new Car(); //create the car object
                    carBuffer.startTimer();
                    carBuffer.setPath(grid.path((10*sR + sC), (10*dR + dC))); //set the path since grid is created in this class and not for each car created
                    
                    carBuffer.getNextIntersection();
                    carBuffer.removeIntersectionFromPath();
                    carBuffer.setOMLocation(4*sR, 4*sC); //save the OM location directly to the car for easy association
                    
                    if( sR > carBuffer.getCurrentDest()/10)
                        dir = 0;//north
                    else if(sR < carBuffer.getCurrentDest()/10)
                        dir = 2;//south
                    else if(sC < carBuffer.getCurrentDest()%10)
                        dir = 1;//east
                    else if(sC > carBuffer.getCurrentDest()%10)
                        dir = 3;//west
                    
                    //This is an attempt to minimize the chance of deadlock, only spawns a car at intersections where no cars are around
                    if( ((4*sR)+1 < 37 && occupancyMatrix[(4*sR)+1][4*sC] == 1) || ((4*sC)+1 < 37 && occupancyMatrix[(4*sR)][4*sC+1] == 1) 
                            || ((4*sR)-1 >= 0 && occupancyMatrix[(4*sR)-1][4*sC] == 1) || ((4*sC)-1 >= 0 && occupancyMatrix[(4*sR)][4*sC-1] == 1) )
                    {
                        spawn = false;
                    }
                    
                    if(spawn == true)
                    {
                        occupancyMatrix[4*sR][4*sC] = 1; //set location in OM to 1, for being occupied
                        carBuffer.setDirection(dir);
                        carArray.add(carBuffer);
                        numCars++;
                        newCarFlag = true;
                        //System.out.println("NEW Car: "+numCars+" new path: "+carBuffer.getPath());
                    }
                }
            }
            
        }
    }
    
    public boolean getNewCarFlag() //returns newcarflag
    {
        return newCarFlag;
    }
    
    public void resetNewCarFlag() //sets newcarflag to false
    {
        newCarFlag = false;
    }
    
    public void setOMForDespawnedCar(int index) //when a car is despawned, need to set the corresponding value in the occupancy matrix to 0. Thereby freeing that space.
    {
        carBuffer = (Car)carArray.get(index);
        occupancyMatrix[carBuffer.getOMI()][carBuffer.getOMJ()] = 0; //make old OM location vacant
    }
    
    public void spawnUserCar(int start, int end) //creates the user's car when they click the button to do so
    {
        int sR, sC, dR, dC, dir;
        
        dir = 0;
        sR = start/10;
        sC = start%10;
        dR = end/10;
        dC = end%10;

        carBuffer = new Car(); //create the car object
        carBuffer.startTimer();
        carBuffer.setPath(grid.path((10*sR + sC), (10*dR + dC))); //set the path since grid is created in this class and not for each car created

        carBuffer.getNextIntersection();
        carBuffer.removeIntersectionFromPath();
        carBuffer.setOMLocation(4*sR, 4*sC); //save the OM location directly to the car for easy association

        if( sR > carBuffer.getCurrentDest()/10)
            dir = 0;//north
        else if(sR < carBuffer.getCurrentDest()/10)
            dir = 2;//south
        else if(sC < carBuffer.getCurrentDest()%10)
            dir = 1;//east
        else if(sC > carBuffer.getCurrentDest()%10)
            dir = 3;//west


            carBuffer.setDirection(dir);
            occupancyMatrix[4*sR][4*sC] = 1; //set location in OM to 1, for being occupied
            carArray.add(carBuffer);
            numCars++;
            //System.out.println("User car created at: "+ start +" new path: "+carBuffer.getPath());


        
    }
    
    public void spawnNewCar(int index) //creates a new car in the place of the old car, once the old car has reached it's destination and needs to be despawned.
    {
        int i, sR, sC, dR, dC, dir;
        boolean spawn = false;
        Car checkBufferAgainst;
        carBuffer = (Car)carArray.get(index);
        occupancyMatrix[carBuffer.getOMI()][carBuffer.getOMJ()] = 0; //make old OM location vacant
        
        while(spawn == false) //create initial cars
        {
            spawn = true;
            sR = sC = dR = dC = dir = 0;
            while(sR == dR && sC == dC) //while start and destination are the same node, retry
            {
                sR = rand.nextInt(10);
                sC = rand.nextInt(10);
                dR = rand.nextInt(10);
                dC = rand.nextInt(10);
            }
            
            for(i = 0; i < numCars; i++)
            {
                checkBufferAgainst = (Car)carArray.get(i);
                if(checkBufferAgainst.getOMI() == 4*sR && checkBufferAgainst.getOMJ() == 4*sC)
                    spawn = false;
            }
            
            if(spawn)
            {
                carBuffer = new Car(); //create the car object
                carBuffer.startTimer();
                carBuffer.setPath(grid.path((10*sR + sC), (10*dR + dC))); //set the path since grid is created in this class and not for each car created
                
                carBuffer.getNextIntersection();
                carBuffer.removeIntersectionFromPath();
                carBuffer.setOMLocation(4*sR, 4*sC); //save the OM location directly to the car for easy association
                
                if( sR > carBuffer.getCurrentDest()/10)
                    dir = 0;//north
                else if(sR < carBuffer.getCurrentDest()/10)
                    dir = 2;//south
                else if(sC < carBuffer.getCurrentDest()%10)
                    dir = 1;//east
                else if(sC > carBuffer.getCurrentDest()%10)
                    dir = 3;//west
                
                //This is an attempt to minimize the chance of deadlock, only spawns a car at intersections where no cars are around
                if( ((4*sR)+1 < 37 && occupancyMatrix[(4*sR)+1][4*sC] == 1) || ((4*sC)+1 < 37 && occupancyMatrix[(4*sR)][4*sC+1] == 1) 
                        || ((4*sR)-1 >= 0 && occupancyMatrix[(4*sR)-1][4*sC] == 1) || ((4*sC)-1 >= 0 && occupancyMatrix[(4*sR)][4*sC-1] == 1) )
                {
                    spawn = false;
                }
                
                
                if(spawn == true)
                {
                    carBuffer.setDirection(dir);
                    occupancyMatrix[4*sR][4*sC] = 1; //set location in OM to 1, for being occupied
                    carArray.set(index, carBuffer);
                    //System.out.println("Car: "+index+" new path: "+carBuffer.getPath());
                }
            }
        }
    }
    
    public void setNumCarsGoal(int num) //set's the numcarsgoal, this is effected by the numcars slider
    {
        numCarsGoal = num;
    }
    
    public int getNumCarsGoal() //returns numcarsgoal
    {
        return numCarsGoal;
    }
    
    public Car getCarFromArray(int i) //returns the car object from the arraylist index
    {
        carBuffer = (Car)carArray.get(i);
        return carBuffer;
    }
    
    public int getNumcars() //returns numcars
    {
        return numCars;
    }
    
    public int getOMValue(int i, int j) //returns the value held by the specified element in the occupancy matrix
    {
        return occupancyMatrix[i][j];
    }
    
    public void setOMValue(int i, int j, int val) //sets the value of the element in the specified index of the occupancy matrix, either 0 or 1, 0 for vacant, 1 for occupied
    {
        occupancyMatrix[i][j] = val;
    }
    
    public void checkCarsAgainstLights(LightsControl lc) //checks all cars against the lights to see if the light is red or green, and sets the car to stop or go
    {
        int numCars = getNumcars();
        int i, dir, x, y, lightI, lightJ;
        for(i = 0; i < numCars; i++)
        {
            
            dir = getCarFromArray(i).getDirection();
            x = getCarFromArray(i).getPixelX();
            y = getCarFromArray(i).getPixelY();
            
            
            if(x < 0 || x > 504 || y < 0 || y > 504)
            {
                //do nothing
            }
            else if(dir == 0)//north
            {
                
                
                if(y%56 == 14) //if at an intersection
                {
                    lightI = y/56; //street width + block width = 56
                    lightJ = x/56;
                    
                    if(lc.getLightFromMatrix(lightI, lightJ).getLightState() == 0)
                    {
                        getCarFromArray(i).setStoppedAtLight(false);
                    }
                    else if(lc.getLightFromMatrix(lightI, lightJ).getLightState() == 1)
                    {
                        getCarFromArray(i).setStoppedAtLight(true);
                    }
                }
            }
            else if(dir == 1)//east
            {
                if(x%56 == 42) //if at an intersection
                {
                    lightI = y/56; //street width + block width = 56
                    lightJ = (x/56)+1;
                    
                    if(lc.getLightFromMatrix(lightI, lightJ).getLightState() == 1)
                    {
                        getCarFromArray(i).setStoppedAtLight(false);
                    }
                    else if(lc.getLightFromMatrix(lightI, lightJ).getLightState() == 0)
                    {
                        getCarFromArray(i).setStoppedAtLight(true);
                    }
                }
            }
            else if(dir == 2) //south
            {
                if(y%56 == 42) //if at an intersection
                {
                    lightI = (y/56)+1; //street width + block width = 56
                    lightJ = x/56;
                    
                    if(lc.getLightFromMatrix(lightI, lightJ).getLightState() == 0)
                    {
                        getCarFromArray(i).setStoppedAtLight(false);
                    }
                    else if(lc.getLightFromMatrix(lightI, lightJ).getLightState() == 1)
                    {
                        getCarFromArray(i).setStoppedAtLight(true);
                    }
                }
            }
            else if(dir == 3)//west
            {
                if(x%56 == 14) //if at an intersection
                {
                    lightI = y/56; //street width + block width = 56
                    lightJ = x/56;
                    
                    if(lc.getLightFromMatrix(lightI, lightJ).getLightState() == 1)
                    {
                        getCarFromArray(i).setStoppedAtLight(false);
                    }
                    else if(lc.getLightFromMatrix(lightI, lightJ).getLightState() == 0)
                    {
                        getCarFromArray(i).setStoppedAtLight(true);
                    }
                }
            }
        }
    }
    
    public void checkCarsAgainstOM() //one of the major functions, keeps the occupancy matrix updated, and allows the cars to "see" other cars via this matrix. This is a map, if you will, of all of the cars' locations.
    {
        
        //If a car is at the exact pixel location of a slot, then check if the next slot is open, if it is, then release the current slot and claim the new slot and drive
        int i, numCars, dir, x, y, omI, omJ;
        numCars = getNumcars();
        
        for(i = 0; i < numCars; i++)
        {
            dir = getCarFromArray(i).getDirection();
            x = getCarFromArray(i).getPixelX();
            y = getCarFromArray(i).getPixelY();
            omI = getCarFromArray(i).getOMI();
            omJ = getCarFromArray(i).getOMJ();
            
            if(x%14 == 0 && y%14 == 0 && !getCarFromArray(i).getStoppedAtLight() && !getCarFromArray(i).getNextStreetFull()) //if in the slot, look ahead to next slot and claim it if vacant
            {
                if(dir == 0 && y > 0) //north
                {
                    //System.out.println("Dir: "+dir+"\t"+omI+"\t"+omJ+"\t"+x+"\t"+y);
                    //System.out.println("Car: "+i+" OMI: "+omI+" OMJ: "+omJ);
                    if(getOMValue(omI-1, omJ) == 0) //look ahead to see if the next OM slot is available, if it is, claim it and set car's drive flag to true (via go())
                    {
                        setOMValue(omI, omJ, 0);
                        getCarFromArray(i).setOMLocation(omI-1, omJ);
                        setOMValue(omI-1, omJ, 1);
                        getCarFromArray(i).go();
                    }
                    else if(getOMValue(omI-1, omJ) == 1)
                        getCarFromArray(i).stop();
                }
                else if(dir == 1 && x < 504) //east
                {
                    //System.out.println("Dir: "+dir+"\t"+omI+"\t"+omJ+"\t"+x+"\t"+y);
                    //System.out.println("Car: "+i+" OMI: "+omI+" OMJ: "+omJ);
                    if(getOMValue(omI, omJ+1) == 0)
                    {
                        setOMValue(omI, omJ, 0);
                        getCarFromArray(i).setOMLocation(omI, omJ+1);
                        setOMValue(omI, omJ+1, 1);
                        getCarFromArray(i).go();
                    }
                    else if(getOMValue(omI, omJ+1) == 1)
                        getCarFromArray(i).stop();
                }
                else if(dir == 2 && y < 504) //south
                {
                    //System.out.println("Dir: "+dir+"\t"+omI+"\t"+omJ+"\t"+x+"\t"+y);
                    //System.out.println("Car: "+i+" OMI: "+omI+" OMJ: "+omJ);
                    if(getOMValue(omI+1, omJ) == 0)
                    {
                        setOMValue(omI, omJ, 0);
                        getCarFromArray(i).setOMLocation(omI+1, omJ);
                        setOMValue(omI+1, omJ, 1);
                        getCarFromArray(i).go();
                    }
                    else if(getOMValue(omI+1, omJ) == 1)
                        getCarFromArray(i).stop();
                }
                else if(dir == 3 && x > 0) //west
                {
                    //System.out.println("Dir: "+dir+"\t"+omI+"\t"+omJ+"\t"+x+"\t"+y);
                    //System.out.println("Car: "+i+" OMI: "+omI+" OMJ: "+omJ);
                    if(getOMValue(omI, omJ-1) == 0)
                    {
                        setOMValue(omI, omJ, 0);
                        getCarFromArray(i).setOMLocation(omI, omJ-1);
                        setOMValue(omI, omJ-1, 1);
                        getCarFromArray(i).go();
                    }
                    else if(getOMValue(omI, omJ-1) == 1)
                        getCarFromArray(i).stop();
                }
            }
        }
    }
    
    public void checkCarsAgainstPath()
    {
        /*
         * If a car is about to claim an intersection:
         * 1. check the path to see if we are turning
         * 2. check the OM to see if the slot after the intersection is open (so cars aren't sitting in intersections)
         * 3. if the slot after the intersection is NOT open, then DO NOT claim the intersection, wait until next move to check again
         * 4. if the slot after the intersection IS open, then claim the intersection, and move
         * 5. if turning, then change the car image icon to the correct direction once the car is fully in the intersection.
         * 
         * 
         * The nextStreetFull variable is the main focus for this function, as well as setting the turning variable and direction
         * 
         * If a car is turning, this will set the car's internal "isTurning" variable to true, and sets the direction.
         * I decided to always set this to true and if the car is going in the same direction, just keep using that direction
         */
        
        int numCars = getNumcars();
        int i, dir, x, y, omi, omj, next;
        for(i = 0; i < numCars; i++)
        {
            
            dir = getCarFromArray(i).getDirection();
            x = getCarFromArray(i).getPixelX();
            y = getCarFromArray(i).getPixelY();
            omi = getCarFromArray(i).getOMI();
            omj = getCarFromArray(i).getOMJ();
            
            if(x < 0 || x > 504 || y < 0 || y > 504 || getCarFromArray(i).getStoppedAtLight() || !getCarFromArray(i).getDrive())
            {
                //System.out.println("car is stopped or not drivable");//do nothing
            }
            else if(dir == 0)//north
            {
                if(y%56 == 14) //if at an intersection
                {
                    
                    //System.out.println("Travelling NORTH and at an intersection. isEmpty: " + car.getCarFromArray(i).isPathEmpty() + ", Current Dest: " + car.getCarFromArray(i).getCurrentDest()
                            //+ ", Current omi: " + omi + ", Current omj: " + omj);
                    //if we have arrived to current destination, then get next
                    if(!getCarFromArray(i).isPathEmpty() && 
                        omi-1 == 4 * (getCarFromArray(i).getCurrentDest() / 10)) //if this next intersection is our current destination
                    {
                        next = getCarFromArray(i).getNextIntersection();
                        
                        //System.out.println("Car: " + i + ", Dir: " + dir + ", OMI: " + omi + ", OMJ: " + omj + " Next: " + next);
                        if(4*(next%10) < omj) //west
                        {
                            if(getOMValue(omi-1, omj-1) == 0) //up and left in OM
                            {
                                getCarFromArray(i).setNextStreetFull(false);//slot is open, clear to go
                                getCarFromArray(i).turn(3); //setting turn flag to true, and direction to west
                                getCarFromArray(i).removeIntersectionFromPath();
                            }
                            else
                                getCarFromArray(i).setNextStreetFull(true);
                        }
                        else if(4*(next%10) == omj) //north
                        {
                            if(getOMValue(omi-2, omj) == 0) //up 2 in OM
                            {
                                getCarFromArray(i).setNextStreetFull(false);//slot is open, clear to go
                                getCarFromArray(i).turn(0);//not turning
                                getCarFromArray(i).removeIntersectionFromPath();
                            }
                            else
                                getCarFromArray(i).setNextStreetFull(true);
                        }
                        else if(4*(next%10) > omj) //east
                        {
                            if(getOMValue(omi-1, omj+1) == 0) //up and right in OM
                            {
                                getCarFromArray(i).setNextStreetFull(false);//slot is open, clear to go
                                getCarFromArray(i).turn(1); //setting turn flag to true, and direction to east
                                getCarFromArray(i).removeIntersectionFromPath();
                            }
                            else
                                getCarFromArray(i).setNextStreetFull(true);
                        }
                        else
                            System.err.println("Error in NORTH statement of checkCarsAgainstPath");
                    }
                    else if(getCarFromArray(i).isPathEmpty() &&
                        omi-1 == 4 * (getCarFromArray(i).getCurrentDest() / 10)) //if this next intersection is our current destination AND our path is empty, despawn
                    {
                        
                        getCarFromArray(i).setCarToDespawn();
                    }
                }
            }
            else if(dir == 1)//east
            {
                if(x%56 == 42) //if at an intersection
                {
                    
                    //System.out.println("Travelling EAST and at an intersection. isEmpty: " + car.getCarFromArray(i).isPathEmpty() + ", Current Dest: " + car.getCarFromArray(i).getCurrentDest()
                            //+ ", Current omi: " + omi + ", Current omj: " + omj);
                    //if we have arrived to current destination, then get next
                    if(!getCarFromArray(i).isPathEmpty() &&
                        omj+1 == 4 * (getCarFromArray(i).getCurrentDest() % 10)) //if this next intersection is our current destination
                    {
                        next = getCarFromArray(i).getNextIntersection();
                        
                        //System.out.println("Car: " + i + ", Dir: " + dir + ", OMI: " + omi + ", OMJ: " + omj + " Next: " + next);
                        if(4*(next/10) < omi) //north
                        {
                            if(getOMValue(omi-1, omj+1) == 0)
                            {
                                getCarFromArray(i).setNextStreetFull(false);//slot is open, clear to go
                                getCarFromArray(i).turn(0); //setting turn flag to true
                                getCarFromArray(i).removeIntersectionFromPath();
                            }
                            else
                                getCarFromArray(i).setNextStreetFull(true);
                        }
                        else if(4*(next/10) == omi) //east
                        {
                            if(getOMValue(omi, omj+2) == 0)
                            {
                                getCarFromArray(i).setNextStreetFull(false);//slot is open, clear to go
                                getCarFromArray(i).turn(1);//not turning
                                getCarFromArray(i).removeIntersectionFromPath();
                            }
                            else
                                getCarFromArray(i).setNextStreetFull(true);
                        }
                        else if(4*(next/10) > omi) //south
                        {
                            if(getOMValue(omi+1, omj+1) == 0) 
                            {
                                getCarFromArray(i).setNextStreetFull(false);//slot is open, clear to go
                                getCarFromArray(i).turn(2); //setting turn flag to true, and direction to south
                                getCarFromArray(i).removeIntersectionFromPath();
                            }
                            else
                                getCarFromArray(i).setNextStreetFull(true);
                        }
                        else
                            System.err.println("Error in EAST statement of checkCarsAgainstPath");
                    }
                    else if(getCarFromArray(i).isPathEmpty() &&
                        omj+1 == 4 * (getCarFromArray(i).getCurrentDest() % 10)) //if this next intersection is our current destination AND our path is empty, despawn
                    {
                        getCarFromArray(i).setCarToDespawn();
                    }
                }
            }
            else if(dir == 2) //south
            {
                if(y%56 == 42) //if at an intersection
                {
                    
                    //System.out.println("Travelling SOUTH and at an intersection. isEmpty: " + car.getCarFromArray(i).isPathEmpty() + ", Current Dest: " + car.getCarFromArray(i).getCurrentDest()
                            //+ ", Current omi: " + omi + ", Current omj: " + omj);
                    //if we have arrived to current destination, then get next
                    if(!getCarFromArray(i).isPathEmpty() &&
                        omi+1 == 4 * (getCarFromArray(i).getCurrentDest() / 10)) //if this next intersection is our current destination
                    {
                        next = getCarFromArray(i).getNextIntersection();
                        
                        //System.out.println("Car: " + i + ", Dir: " + dir + ", OMI: " + omi + ", OMJ: " + omj + " Next: " + next);
                        if(4*(next%10) > omj) //east
                        {
                            if(getOMValue(omi+1, omj+1) == 0)
                            {
                                getCarFromArray(i).setNextStreetFull(false);//slot is open, clear to go
                                getCarFromArray(i).turn(1); //setting turn flag to true
                                getCarFromArray(i).removeIntersectionFromPath();
                            }
                            else
                                getCarFromArray(i).setNextStreetFull(true);
                        }
                        else if(4*(next%10) == omj) //south
                        {
                            if(getOMValue(omi+2, omj) == 0)
                            {
                                getCarFromArray(i).setNextStreetFull(false);//slot is open, clear to go
                                getCarFromArray(i).turn(2);//not turning
                                getCarFromArray(i).removeIntersectionFromPath();
                            }
                            else
                                getCarFromArray(i).setNextStreetFull(true);
                        }
                        else if(4*(next%10) < omj) //west
                        {
                            if(getOMValue(omi+1, omj-1) == 0) 
                            {
                                getCarFromArray(i).setNextStreetFull(false);//slot is open, clear to go
                                getCarFromArray(i).turn(3); //setting turn flag to true, and direction to west
                                getCarFromArray(i).removeIntersectionFromPath();
                            }
                            else
                                getCarFromArray(i).setNextStreetFull(true);
                        }
                        else
                            System.err.println("Error in SOUTH statement of checkCarsAgainstPath");
                    }
                    else if(getCarFromArray(i).isPathEmpty()) //if this next intersection is our current destination AND our path is empty, despawn
                    {
                        getCarFromArray(i).setCarToDespawn();
                    }
                }
            }
            else if(dir == 3)//west
            {
                if(x%56 == 14) //if at an intersection
                {
                    
                    //System.out.println("Travelling WEST and at an intersection. isEmpty: " + car.getCarFromArray(i).isPathEmpty() + ", Current Dest: " + car.getCarFromArray(i).getCurrentDest()
                            //+ ", Current omi: " + omi + ", Current omj: " + omj);
                   //if we have arrived to current destination, then get next
                    if(!getCarFromArray(i).isPathEmpty() &&
                        omj-1 == 4 * (getCarFromArray(i).getCurrentDest() % 10)) //if this next intersection is our current destination
                    {
                        next = getCarFromArray(i).getNextIntersection();
                        
                        //System.out.println("Car: " + i + ", Dir: " + dir + ", OMI: " + omi + ", OMJ: " + omj + " Next: " + next);
                        if(4*(next/10) < omi) //north
                        {
                            if(getOMValue(omi-1, omj-1) == 0)
                            {
                                getCarFromArray(i).setNextStreetFull(false);//slot is open, clear to go
                                getCarFromArray(i).turn(0); //setting turn flag to true
                                getCarFromArray(i).removeIntersectionFromPath();
                            }
                            else
                                getCarFromArray(i).setNextStreetFull(true);
                        }
                        else if(4*(next/10) == omi) //west
                        {
                            if(getOMValue(omi, omj-2) == 0)
                            {
                                getCarFromArray(i).setNextStreetFull(false);//slot is open, clear to go
                                getCarFromArray(i).turn(3);//not turning
                                getCarFromArray(i).removeIntersectionFromPath();
                            }
                            else
                                getCarFromArray(i).setNextStreetFull(true);
                        }
                        else if(4*(next/10) > omi) //south
                        {
                            if(getOMValue(omi+1, omj-1) == 0) 
                            {
                                getCarFromArray(i).setNextStreetFull(false);//slot is open, clear to go
                                getCarFromArray(i).turn(2); //setting turn flag to true, and direction to south
                                getCarFromArray(i).removeIntersectionFromPath();
                            }
                            else
                                getCarFromArray(i).setNextStreetFull(true);
                        }
                        else
                            System.err.println("Error in WEST statement of checkCarsAgainstPath");
                    }
                    else if(getCarFromArray(i).isPathEmpty() &&
                        omj-1 == 4 * (getCarFromArray(i).getCurrentDest() % 10)) //if this next intersection is our current destination AND our path is empty, despawn
                    {
                        getCarFromArray(i).setCarToDespawn();
                    }
                }
            }
        }
        
    }
    
}
