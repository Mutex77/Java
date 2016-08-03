/*
 * one light at every node, lighting direction determined by node's (i, j)
 * use i and j with % 2
 * LIGHT FACING DIRECTIONS
 * 0, 0 - up / right
 * 1, 0 - up / left
 * 0, 1 - down / right
 * 1, 1 = down / left
 */
package traffic;

public class Lights
{
    private int greenState;
    
    Lights()
    {
        createLight();
    }
    
    private void createLight()
    {
        greenState = 0;
    }
    
    public void changeLightState()
    {
        greenState = (greenState + 1)%2; //either 0 or 1, lights only bi-directional
        /*
         * This is now paired with a drawing function that will update the light
         * image depending on the value of greenState
         */
        
    }
    
    public int getLightState()
    {
        return greenState;
    }
    
    public void setLightState(int i)
    {
        if(i == 0 || i == 1)
            greenState = i;
        else
            System.err.println("Illegal value for setLightState.");
    }
}
