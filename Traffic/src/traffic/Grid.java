
package traffic;

import java.util.*;

public class Grid 
{
    private int[][] grid = new int[10][10];
    private int[][] adjMatrix = new int[100][100];
    private int[][] pathMatrix = new int[100][100];
    private ArrayList pathList = new ArrayList();
    
    public Grid()
    {
        pathList.ensureCapacity(30);
        initializeGrid();
        initAdjacencyMatrix();
        calcAdjMatrix();
        initPathMatrix();
        calcPathMatrix();
    }
        
    private void initializeGrid() //grid representing the intersections only (10 by 10)
    {
        int i, j;
        for(i=0; i<10; i++)
        {
            for(j=0; j<10; j++)
            {
                grid[i][j] = 1; //grid is initialized to 1 since each node's "weight" or distance is 1
            }
        }
    }
    
    private void initAdjacencyMatrix() //initialize the adjacency matrix to either 0, or 999
    {
        int i, j;
        
        for(i=0; i<100; i++)
        {
            for(j=0; j<100; j++)
            {
                if(i == j)
                    adjMatrix[i][j] = 0; //if i = j then both variables reference the same intersection
                else 
                    adjMatrix[i][j] = 999; //need a large number for the calculations to work since we're finding minimum paths
            }
        }
    }
    
    private void calcAdjMatrix() //this will bring over the node's weight from the grid to the adjacency matrix for all nodes adjacent to the current node (i, j).
    {
        int i, j;
        
        for(i=0; i<10; i++)
        {
            for(j=0; j<10; j++)
            {
                if(i%2 == 0) //left
                    if(j-1 >= 0)
                        adjMatrix[i*10+j][i*10+(j-1)] = grid[i][j]; //one node left
                
                if(i%2 == 1) //right
                    if(j+1 <= 9)
                        adjMatrix[i*10+j][i*10+(j+1)] = grid[i][j]; //one node right
                
                if(j%2 == 0) //down
                    if(i+1 <= 9) //check to make sure staying in the matrix
                        adjMatrix[i*10+j][(i+1)*10+j] = grid[i][j]; //one node down
                
                if(j%2 == 1) //up
                    if(i-1 >= 0) //check to make sure staying in the matrix
                        adjMatrix[i*10+j][(i-1)*10+j] = grid[i][j]; //one node up
                        
            }
        }
    }
    
    private void initPathMatrix()
    {
        int i, j;
        
        for(i=0; i<100; i++)
        {
            for(j=0; j<100; j++)
            {
                pathMatrix[i][j] = 9999; //setting all values to 9999 b/c we're calculating for minimums
            }
        }
    }
    
    private void calcPathMatrix() //This is using Floyd's algorithm
    {
        /*
         * By using floyd's algorithm we are calculating and saving the path matrix, and 
         * also saving the minimum distance calculation into the adjacency matrix
         */
        int i, j, k;
        
        for(k=0; k<100; k++) //node number to be saved
        {
            for(i=0; i<100; i++) //rows
            {
                for(j=0; j<100; j++) //columns
                    {
                        if(i == j) //if already at the node
                            pathMatrix[i][j] = -1; //setting to -1 since node 0 does exist and we need it for pathing
                        
                            if(adjMatrix[i][k] + adjMatrix[k][j] < adjMatrix[i][j])
                            {
                                adjMatrix[i][j] = adjMatrix[i][k] + adjMatrix[k][j];
                                pathMatrix[i][j] = k;
                            }
                    }
            }
        }
    }
    
    /**
    private void printGrid()
    {
        int i, j;
        for(i=0; i<10; i++)
        {
            for(j=0; j<10; j++)
            {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }
    
    private void printAdjMatrix()
    {
        System.out.println("This is an adjacency matrix.\n");
        int i, j, row, col;
        row = 0;
        System.out.print("\tColumn");
        for(col = 0; col < 100; col++)
        {
            System.out.print("\t" + col);
        }
        System.out.println("\nRow\n");
        for(i=0; i<100; i++)
        {
            System.out.print(row + "\t\t");
            for(j=0; j<100; j++)
            {
                System.out.print(adjMatrix[i][j] + "\t");
            }
            row++;
            System.out.println();
        }
    }
    
    private void printPathMatrix()
    {
        System.out.println("This is a path matrix.\n");
        int i, j, row, col;
        row = 0;
        System.out.print("\tColumn");
        for(col = 0; col < 100; col++)
        {
            System.out.print("\t" + col);
        }
        System.out.println("\nRow\n");
        for(i=0; i<100; i++)
        {
            System.out.print(row + "\t\t");
            for(j=0; j<100; j++)
            {
                System.out.print(pathMatrix[i][j] + "\t");
            }
            row++;
            System.out.println();
        }
    }
    
    private void printPath()
    {
        System.out.println(pathList);
    }
    * 
    */
    
    private int calcPath(int a, int b) //recursive call to calculate the path from the path matrix, recursive is needed since the path matrix is read in pieces at a time
    {
        int i, j, mid;
        i = a;
        j = b;
        mid = pathMatrix[i][j];
        if(pathMatrix[i][j] == -1)
        {
            return 0;
        }
        else if(pathMatrix[i][j] == 9999) //9999 value in the path matrix means we are at the end of the path
        {
            pathList.add(j);
            return 0;
        }
        
        return calcPath(i, mid) + calcPath(mid, j);
    }
    
    private void clearPathList()
    {
        pathList.clear();
    }
    
    public ArrayList path(int a, int b) //returns a path from a to b
    {
        clearPathList();
        calcPath(a, b);
        //printPath();
        return pathList;
    }
}