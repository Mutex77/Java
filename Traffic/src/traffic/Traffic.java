/*
 * Cody Bonewald, 
 */
package traffic;

public class Traffic
{
    
    public static void main(String[] args)
    {
        Thread lightThread, carThread, statsThread; //3 threads total, main thread (will handle buttons), light thread, and car thread
        
        CarsControl car = new CarsControl();
        LightsControl lc = new LightsControl();
        final SwingGUI sg = new SwingGUI(car, lc); //creates SwingGUI object that sets everything in motion, this will change later
        
        Runnable lightExecution = new Runnable() //defines what the light thread will do
        {
            public void run() 
            {
                try 
                {
                    sg.lightsAnimator(); //lights animator will have everything necessary for operating the lights
                    Thread.sleep(0); //this segment is never actually reached, and necessary for the try/catch, not sure if we actually need the try/catch, just part of the example I saw
                }
                catch (InterruptedException ie) 
                {
                    
                }
            } 
            
        };
        lightThread = new Thread(lightExecution); //creating thread for lights since we need concurrent execution of the cars class
        lightThread.start();
        
        Runnable carExecution = new Runnable() //defines what the light thread will do
        {
            public void run() 
            {
                try 
                {
                    sg.carAnimator();
                    Thread.sleep(0);
                }
                catch (InterruptedException ie) 
                {
                    
                }
            } 
            
        };
        carThread = new Thread(carExecution); //creating thread for lights since we need concurrent execution of the cars class
        carThread.start();
        
        Runnable statsExecution = new Runnable() //defines what the light thread will do
        {
            public void run() 
            {
                try 
                {
                    sg.refreshStatsWindow(); //lights animator will have everything necessary for operating the lights
                    Thread.sleep(0); //this segment is never actually reached, and necessary for the try/catch, not sure if we actually need the try/catch, just part of the example I saw
                }
                catch (InterruptedException ie) 
                {
                    
                }
            } 
            
        };
        statsThread = new Thread(statsExecution); //creating thread for lights since we need concurrent execution of the cars class
        statsThread.start();
    }
    
}