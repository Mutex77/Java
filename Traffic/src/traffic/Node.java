
package traffic;

public class Node 
{
    private int row, col;
    
    public Node(int R, int C)
    {
        if(R>=0 && R<=9 && C>=0 && C<=9)
        {
            row = R;
            col = C;
        }
        else
            System.err.println("Bad input for node location.");
    }
    
    public Node()
    {
        //empty node values
    }
    /*
    public Node(int nodeNum)
    {
        if(nodeNum >= 0 || nodeNum <= 99)
        {
            row = nodeNum/10;
            col = nodeNum%10;
        }
        else
            System.err.println("Bad Node Number in Node constructor.");
    }
    */
    public int getRow()
    {
        return row;
    }
    
    public int getCol()
    {
        return col;
    }
    
    public void setRow(int i)
    {
        row = i;
    }
    
    public void setCol(int j)
    {
        col = j;
    }
    
    public boolean equalNode(Node dest)
    {
        if(this.row == dest.row && this.col == dest.col)
            return true;
        else
            return false;
    }
    
    public int RowColtoNodeNum()
    {
        int num = row*10 + col;
        return num;
    }
}
