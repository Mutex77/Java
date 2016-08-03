
package traffic;

import java.text.DecimalFormat;
import java.util.*;

public class Car 
{
    private int OMI, OMJ, currentDestination, nextDest, direction, pixelX, pixelY, turningDirection;
    private ArrayList path = new ArrayList();
    private boolean drive = true;
    private boolean stoppedAtLight = false;
    private boolean nextStreetFull = false;
    private boolean despawn = false;
    private boolean turning = false;
    private boolean isUserCar = false;
    private long startTime;
    private long endTime;
    private long startWaitTimer;
    private long stopWaitTimer;
    private long startNSFWaitTimer;
    private long stopNSFWaitTimer;
    private long startDualWaitTimer;
    private long stopDualWaitTimer;
    private double waitTime = 0.0;
    private double dualWaitTime = 0.0;
    
    //most of these variables are pretty self explanatory
    
    public Car()
    {
    }
    
    public void startTimer()
    {
        startTime = System.nanoTime();
    }
    
    public void stopTimer()
    {
        endTime = System.nanoTime();
    }
    
    public double getTravelTime()
    {
        double elapsed = (double)(endTime - startTime);
        //System.out.print(formatDouble(elapsed/1000000000) + " seconds, and waited at lights for: " + formatDouble(timeAtLight/1000000000) + " seconds,");
        return formatDouble(elapsed/1000000000);
    }
    
    public double getWaitTime()
    {
        return formatDouble((waitTime - dualWaitTime)/1000000000);
    }
    
    private static double formatDouble(double d) 
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
	return Double.valueOf(twoDForm.format(d));
    }
    
    public void setPath(ArrayList p)
    {
        path = new ArrayList(p);
        //System.out.println((10*startI + startJ) + " to " + (10*destI + destJ) + ": " +path);
        
    }
    
    public void setOMLocation(int i, int j)
    {
        OMI = i;
        OMJ = j;
    }
    
    public boolean getStoppedAtLight()
    {
        return stoppedAtLight;
    }
    
    public void setStoppedAtLight(boolean s)
    {
        if(s == true && stoppedAtLight == false)
            startWaitTimer = System.nanoTime();
        else if(s == false && stoppedAtLight == true)
        {
            stopWaitTimer = System.nanoTime();
            waitTime += (double)(stopWaitTimer - startWaitTimer);
        }
        
        if(s == true && stoppedAtLight == false && nextStreetFull == true)
            startDualWaitTimer = System.nanoTime();
        else if(s == false && stoppedAtLight == true && nextStreetFull == true)
        {
            stopDualWaitTimer = System.nanoTime();
            dualWaitTime += stopDualWaitTimer - startDualWaitTimer;
        }
            
        stoppedAtLight = s;
    }
    
    public void setAsUserCar()
    {
        isUserCar = true;
    }
    
    public boolean getIsUserCar()
    {
        return isUserCar;
    }
    
    public int getOMI()
    {
        return OMI;
    }
    
    public int getOMJ()
    {
        return OMJ;
    }
    
    public int getNextIntersection() //needed this new variable "nextDest" b/c of the way we are calling for the next path destination in our animation functions, if we use "currentDestination" we get data loss
    {
        nextDest = (int)path.get(0);
        return nextDest;
    }
    
    public void removeIntersectionFromPath()
    {
        currentDestination = (int)path.get(0); //necessary in case a car sees that a street is full and trips that flag, preserving data
        path.remove(0);
    }
    
    public boolean isPathEmpty()
    {
        return path.isEmpty();
    }
    
    public int getCurrentDest()
    {
        return currentDestination;
    }
    
    public void turn(int dir) //sets the value of the direction that the car needs to turn to when entering the next intersection
    {
        turning = true;
        turningDirection = dir;
        
    }
    
    public void finishedTurn() 
    {
        turning = false;
    }
    
    public boolean isTurning()
    {
        return turning;
    }
    
    public int getTurningDirection()
    {
        return turningDirection;
    }
    
    public boolean getNextStreetFull()
    {
        return nextStreetFull;
    }
    
    public void setNextStreetFull(boolean n) //this is the variable that represents if the next street after an intersection is currently full or not. This keeps cars from getting stuck in intersections.
    {
        if(n == true && nextStreetFull == false)
            startNSFWaitTimer = System.nanoTime();
        else if(n == false && nextStreetFull == true)
        {
            stopNSFWaitTimer = System.nanoTime();
            waitTime += stopNSFWaitTimer - startNSFWaitTimer;
        }
        
        nextStreetFull = n;
    }
    
    public void setCarToDespawn()
    {
        despawn = true;
    }
    
    public boolean getDespawnFlag()
    {
        return despawn;
    }
    
    public void stop()
    {
        if(drive == true)
            startWaitTimer = System.nanoTime();
        
        drive = false;
    }
    
    public void go()
    {
        if(drive == false)
        {
            stopWaitTimer = System.nanoTime();
            waitTime += stopWaitTimer - startWaitTimer;
        }
        drive = true;
    }
    
    public boolean getDrive()
    {
        return drive;
    }
    
    public void setDirection(int i)
    {
        //0 = north, 1 = east, 2 = south, 3 = west
        if(i >= 0 && i <= 3)
            direction = i;
        else
            System.err.println("Bad direction setting.");
    }
    
    public int getDirection()
    {
        return direction;
    }
    
    public void setPixelLoc(int x, int y)
    {
        pixelX = x;
        pixelY = y;
    }
    
    public int getPixelX()
    {
        return pixelX;
    }
    
    public int getPixelY()
    {
        return pixelY;
    }
}
