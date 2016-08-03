
package traffic;

import java.text.DecimalFormat;



public class LightsControl 
{
    private Lights[][] lightMatrix = new Lights[10][10];
    private long startTimeLS1, stopTimeLS1, totalLS1;
    private long startTimeLS2, stopTimeLS2, totalLS2;
    boolean onLS1 = true;
    boolean onLS2 = false;
    
    public LightsControl()
    {
        createLightsMatrix();
    }
    
    private void createLightsMatrix()
    {
        int i, j;
        
        for(i = 0; i<10; i++)
        {
            for(j = 0; j<10; j++)
            {
                lightMatrix[i][j] = new Lights();
            }
        }
        startTimeLS1 = System.nanoTime();
    }
    
    public Lights getLightFromMatrix(int i, int j)
    {
        return lightMatrix[i][j]; //neccessary for SwingGUI to have access to all of the functions
    }
    
    
    
    public void changeLights() //changes the greenstate variable in the light, which determines the direction that the green light is facing
    {
        int i, j;
        
        for(i = 0; i<10; i++)
        {
            for(j = 0; j<10; j++)
            {
                lightMatrix[i][j].changeLightState();
            }
        }
        
    }
    
    public void resetTimers()
    {
        totalLS1 = 0;
        totalLS2 = 0;
        startTimeLS1 = System.nanoTime();
        startTimeLS2 = System.nanoTime();
    }
    
    public double getTimeLS1()
    {
        if(onLS1 == true)
        {
            totalLS1 += System.nanoTime() - startTimeLS1;
            startTimeLS1 = System.nanoTime();
            return formatDouble((double)totalLS1 / 1000000000);
        }
        else if(onLS1 == false)
        {
            return formatDouble((double)totalLS1 / 1000000000);
        }
        else
            return -1.0;
    }
    
    public double getTimeLS2()
    {
        if(onLS2 == true)
        {
            totalLS2 += System.nanoTime() - startTimeLS2;
            startTimeLS2 = System.nanoTime();
            return formatDouble((double)totalLS2 / 1000000000);
        }
        else if(onLS2 == false)
        {
            return formatDouble((double)totalLS2 / 1000000000);
        }
        else
            return -1.0;
    }
    
    public void changeLightScheme1() //lights are either all red or all green on rows / columns
    {
        int i, j;
        
        stopTimeLS2 = System.nanoTime();
        totalLS2 += startTimeLS2 - stopTimeLS2;
        onLS1 = true;
        onLS2 = false;
        startTimeLS1 = System.nanoTime();
        
        for(i = 0; i < 10; i++)
        {
            for(j = 0; j < 10; j++)
            {
                lightMatrix[i][j].setLightState(0);
            }
        }
    }
    
    private static double formatDouble(double d) 
    {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
	return Double.valueOf(twoDForm.format(d));
    }
    
    public void changeLightScheme2() //lights alternate red / green on any given row or column
    {
        int i, j;
        
        stopTimeLS1 = System.nanoTime();
        totalLS1 += startTimeLS1 - stopTimeLS1;
        onLS1 = false;
        onLS2 = true;
        startTimeLS2 = System.nanoTime();
        
        for(i = 0; i < 10; i++)
        {
            for(j = 0; j < 10; j++)
            {
                if(i%2 == 0)
                {
                    if(j%2 == 0)
                        lightMatrix[i][j].setLightState(0);
                    else
                        lightMatrix[i][j].setLightState(1);
                }
                else
                {
                    if(j%2 == 0)
                        lightMatrix[i][j].setLightState(1);
                    else
                        lightMatrix[i][j].setLightState(0);
                }
            }
        }
    }
}
