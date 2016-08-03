
package traffic;

import java.awt.event.*;
import java.io.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

public class SwingGUI extends javax.swing.JFrame
{
    final static String USERCARN = "UserCarsN.gif";
    final static String USERCARE = "UserCarsE.gif";
    final static String USERCARS = "UserCarsS.gif";
    final static String USERCARW = "UserCarsW.gif";
    final static String BACKGROUND = "Bg-Design.jpg";
    final static String STREET = "STREET-BG-DARK.jpg";
    final static String BUILDINGS = "BUILDING-LAYER.png";
    final static String NEN = "NEN.png";
    final static String NEE = "NEE.png";
    final static String NWN = "NWN.png";
    final static String NWW = "NWW.png";
    final static String SEE = "SEE.png";
    final static String SES = "SES.png";
    final static String SWS = "SWS.png";
    final static String SWW = "SWW.png";
    final static String CARN = "carN.gif";
    final static String CARE = "carE.gif";
    final static String CARS = "carS.gif";
    final static String CARW = "carW.gif";
    final static String NUMBEROFCARS_IMAGE = "numberofcars.png";
    final static String LIGHTINTERVAL_IMAGE = "lightinterval.png";
    final static String CARSPEED_IMAGE = "carspeed.png";
    final static String TITLE_IMAGE = "trafficsimulator.png";
    
    
    ImageIcon images;
    JLabel bg, streets, buildings, LSSliderLabel, carSpeedSliderLabel, carNumberLabel;
    JLayeredPane bgLayer, lightsLayer, carLayer, buttonLayer; //all buttons go onto the bgLayer at layer level "1" (bg image is at layer level 0)
    JLabel[][] lightImageMatrix = new JLabel[10][10]; //array of light images, used to display our lights
    ArrayList carImageArray = new ArrayList(); //allowing for up to 100 cars, we CAN increase this, but only after up to 100 are spawned initially, ask me if you want to know more
    JLabel carImageBuffer, userCarLabel1, userCarLabel2, lightSchemeLabel, carSpeedImage, carNumberImage, lightIntervalImage, titleImage,
            totalRunTimeLabel, averageTravelTimeLabel, longestTravelTimeLabel, shortestTravelTimeLabel, averageWaitTimeLabel, longestWaitTimeLabel,
            shortestWaitTimeLabel, totalCarsRecordedLabel, averageCarSpeedLabel, fastestCarSpeedLabel, slowestCarSpeedLabel, averageNumberOfCarsLabel,
            mostNumberOfCarsLabel, leastNumberOfCarsLabel, averageLightIntervalLabel, shortestLightIntervalLabel, longestLightIntervalLabel, 
            timeOnLS1Label, timeOnLS2Label;
    JSlider lightSpeed; //this slider will control the amount of time between switching light states
    JSlider carSpeed, carNumber;
    JTextField userCarStart, userCarDest;
    JTextArea userCarFeedback;
    JButton userCarButton, lightScheme1, lightScheme2, statsButton;
    JFrame statsFrame;
    JMenuBar menuBar;
    CarsControl car;
    LightsControl lc;
    JFileChooser fc;
    int completedCarCount, carSpeedValue = 33, carNumberValue = 20, numberOfFunctionCalls;
    double allCarsTotalTravelTime, averageTravelTime, longestTravelTime, shortestTravelTime,
            allCarsTotalWaitTime, averageWaitTime, longestWaitTime, shortestWaitTime = 99,
            programStartTime = System.nanoTime(), currentRunTime,
            totalCarSpeeds, averageCarSpeed, fastestCarSpeed, slowestCarSpeed,
            totalCarNumberSlider, averageCarNumberSlider, mostCarNumberSlider, leastCarNumberSlider,
            totalLightIntervalSlider, averageLightIntervalSlider, longestLightIntervalSlider, shortestLightIntervalSlider, lightSpeedValue = 3.0,
            timeOnLS1, timeOnLS2;
    

    public SwingGUI(CarsControl carIn, LightsControl lcIn) //calls to initialize components, and creates threads for the lights and cars
    {
        super("Traffic");
        car = carIn;
        lc = lcIn;
        initializeComponents(); //gets everything set up in the beginning
    }
    
    public class lightSpeedCL implements ChangeListener //this is a listener for the light speed slider, and changes the value of the light speed and JLabel accordingly
    {
        public void stateChanged(ChangeEvent ce)
        {
            lightSpeedValue = lightSpeed.getValue() * .001;
            
            String str = Double.toString(lightSpeedValue);
            LSSliderLabel.setText(str+"s");
        }
    }
    
    public class carSpeedCL implements ChangeListener //this is a listener for the car speed slider, and changes the value of the car speed and JLabel accordingly
    {
        public void stateChanged(ChangeEvent ce)
        {
            carSpeedValue = carSpeed.getValue();
            carSpeedValue = (int)(1/((double)carSpeedValue / 1000));
            String str = Integer.toString(carSpeedValue);
            carSpeedSliderLabel.setText(str+"p/s");
        }
    }
    
    public class carNumberCL implements ChangeListener //this is a listener for the car number slider, and changes the value of the car number and JLabel accordingly
    {
        public void stateChanged(ChangeEvent ce)
        {
            carNumberValue = carNumber.getValue();
            
            String str = Integer.toString(carNumberValue);
            carNumberLabel.setText(str);
        }
    }
    
    public class userCarButtonAL implements ActionListener //button to activate the user car with given beginning and end, with error checking
    {
        public void actionPerformed(ActionEvent evt) 
        {
            //create new car using input from the two text fields
            userCarButton.setEnabled(false);
            int start, end, dir, numCars;
            String startBuffer, endBuffer;
            char character;
            boolean isGood = true;
            
            startBuffer = userCarStart.getText();
            endBuffer = userCarDest.getText();
            
            for(int i = 0; i < startBuffer.length(); i++)
            {
                character = startBuffer.charAt(i);
                if(character > 57 || character < 48) //if all characters are numeric
                {
                    isGood = false;
                    userCarButton.setEnabled(true);
                    userCarFeedback.setText("One of the characters in the text fields is not a numeral.");
                }
            }
            
            for(int i = 0; i < endBuffer.length(); i++)
            {
                character = endBuffer.charAt(i);
                if(character > 57 || character < 48) //if all characters are numberic
                {
                    isGood = false;
                    userCarButton.setEnabled(true);
                    userCarFeedback.setText("One of the characters in the text fields is not a numeral.");
                }
            }
            
            if(startBuffer.length() == 0 || endBuffer.length() == 0)
            {
                isGood = false;
                userCarButton.setEnabled(true);
                userCarFeedback.setText("The text field is empty.");
            }
            
            if(isGood)
            {
                start = Integer.parseInt(startBuffer);
                end = Integer.parseInt(endBuffer);
                
                if(start > 99 || end > 99)
                {
                    userCarButton.setEnabled(true);
                    userCarFeedback.setText("The numbers are too large!");
                }
                else if(car.getOMValue(start/10, start%10) == 1)
                {
                    userCarButton.setEnabled(true);
                    userCarFeedback.setText("That starting intersection is currently occupied.");
                }
                else if(start == end)
                {
                    userCarButton.setEnabled(true);
                    userCarFeedback.setText("The start and end locations are the same.");
                }
                else
                {
                    userCarFeedback.setText("Car creation successful!");
                    carImageBuffer = new JLabel();
                    
                    carImageBuffer.setBounds(-20, -20, 14, 14); //-20, -20 gets it just out of frame
                    carLayer.add(carImageBuffer, new Integer(0)); //adding cars to the car layer at level 0, but the car layer itself will be level 2 in the bgLayer
                    carImageArray.add(carImageBuffer);
                    car.spawnUserCar(start, end);
                    numCars = car.getNumcars();
                    carImageBuffer = (JLabel)carImageArray.get(numCars-1);
                    carImageBuffer.setLocation(14*car.getCarFromArray(numCars-1).getOMJ(), 14*car.getCarFromArray(numCars-1).getOMI());
                    car.getCarFromArray(numCars-1).setPixelLoc(14*car.getCarFromArray(numCars-1).getOMJ(), 14*car.getCarFromArray(numCars-1).getOMI());
                    car.getCarFromArray(numCars-1).setAsUserCar();
                    dir = car.getCarFromArray(numCars-1).getDirection();
                    if(dir == 0)
                            carImageBuffer.setIcon(new ImageIcon(USERCARN));
                        else if(dir == 1)
                            carImageBuffer.setIcon(new ImageIcon(USERCARE));
                        else if(dir == 2)
                            carImageBuffer.setIcon(new ImageIcon(USERCARS));
                        else if(dir == 3)
                            carImageBuffer.setIcon(new ImageIcon(USERCARW));
                }
                
            }
        }
    }
    
    public class lightScheme1AL implements ActionListener //listener for the first light scheme button
    {
        public void actionPerformed(ActionEvent evt)
        {
            lightScheme1.setEnabled(false);
            lightScheme2.setEnabled(true);
            lc.changeLightScheme1();
            refreshLights();
        }
    }
    
    public class lightScheme2AL implements ActionListener //listener for the second light scheme button
    {
        public void actionPerformed(ActionEvent evt)
        {
            lightScheme2.setEnabled(false);
            lightScheme1.setEnabled(true);
            lc.changeLightScheme2();
            refreshLights();
        }
    }
    
    public class statsButtonAL implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            statsFrame.setVisible(true);
        }
    }
    
    public class saveAL implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            int response = 0;
            File f;
            int returnVal = fc.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                f = fc.getSelectedFile();
                if(f.exists())
                    response = JOptionPane.showConfirmDialog (null, "Overwrite existing file?","Confirm Overwrite", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(response == JOptionPane.OK_OPTION)
                    saveFile(fc.getSelectedFile().toString());
            }
        }
    }
    
    public class clearAL implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            completedCarCount = 0;
            numberOfFunctionCalls = 0;
            allCarsTotalTravelTime = 0;
            averageTravelTime = 0;
            longestTravelTime = 0;
            shortestTravelTime = 0;
            allCarsTotalWaitTime = 0;
            averageWaitTime = 0;
            longestWaitTime = 0;
            shortestWaitTime = 99;
            programStartTime = System.nanoTime();
            totalCarSpeeds = 0;
            averageCarSpeed = 0;
            fastestCarSpeed = 0;
            slowestCarSpeed = 0;
            totalCarNumberSlider = 0;
            averageCarNumberSlider = 0;
            mostCarNumberSlider = 0;
            leastCarNumberSlider = 0;
            totalLightIntervalSlider = 0;
            averageLightIntervalSlider = 0;
            longestLightIntervalSlider = 0;
            shortestLightIntervalSlider = 0;
            lc.resetTimers();
        }
    }
    
    private static double formatDouble(double d) 
    {
        DecimalFormat twoDForm = new DecimalFormat("#.#");
	return Double.valueOf(twoDForm.format(d));
    }
    
    private void initializeComponents() //initializes the entire layout, including the background, lights, cars, and buttons, as well as program boundries, etc.
    {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE); //what to do when closing the window
        
        initStatsFrame();
        initBGLayer();
        initLightsLayer();
        initCarsLayer();
        initButtonsLayer();
        
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(bgLayer, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE));
        layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(bgLayer, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE));
        
        
        this.setResizable(false); //no resizing
        this.setBounds(100, 100, 800, 600); //starting x and y position, the other 2 numbers cannot override the previous setting
        this.setVisible(true); //definitely need to be able to see the window
        
        pack(); //not sure what this does exactly, the program works without it, but it was used in all of the examples I've seen, should probably look it up later
    }
    
    private void initStatsFrame()
    {
        statsFrame = new JFrame("Statistics");
        statsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        statsFrame.setBounds(200, 200, 400, 440);
        statsFrame.setJMenuBar(statsMenuBar());
        statsFrame.setLayout(null);
        statsFrame.setResizable(false);
        
        totalRunTimeLabel = new JLabel("Total run time: ");
        totalRunTimeLabel.setBounds(5, 0, 300, 20);
        statsFrame.add(totalRunTimeLabel);
        
        averageTravelTimeLabel = new JLabel("Average travel time: ");
        averageTravelTimeLabel.setBounds(5, 20, 300, 20);
        statsFrame.add(averageTravelTimeLabel);
        
        longestTravelTimeLabel = new JLabel("Longest travel time: ");
        longestTravelTimeLabel.setBounds(5, 40, 300, 20);
        statsFrame.add(longestTravelTimeLabel);
        
        shortestTravelTimeLabel = new JLabel("Shortest travel time: ");
        shortestTravelTimeLabel.setBounds(5, 60, 300, 20);
        statsFrame.add(shortestTravelTimeLabel);
        
        averageWaitTimeLabel = new JLabel("Average wait time: ");
        averageWaitTimeLabel.setBounds(5, 80, 300, 20);
        statsFrame.add(averageWaitTimeLabel);
        
        longestWaitTimeLabel = new JLabel("Longest wait time: ");
        longestWaitTimeLabel.setBounds(5, 100, 300, 20);
        statsFrame.add(longestWaitTimeLabel);
        
        shortestWaitTimeLabel = new JLabel("Shortest wait time: ");
        shortestWaitTimeLabel.setBounds(5, 120, 300, 20);
        statsFrame.add(shortestWaitTimeLabel);
        
        totalCarsRecordedLabel = new JLabel("Number of cars recorded: ");
        totalCarsRecordedLabel.setBounds(5, 140, 300, 20);
        statsFrame.add(totalCarsRecordedLabel);
        
        averageCarSpeedLabel = new JLabel("Average car speed: ");
        averageCarSpeedLabel.setBounds(5, 160, 300, 20);
        statsFrame.add(averageCarSpeedLabel);
        
        fastestCarSpeedLabel = new JLabel("Fastest car speed: ");
        fastestCarSpeedLabel.setBounds(5, 180, 300, 20);
        statsFrame.add(fastestCarSpeedLabel);
        
        slowestCarSpeedLabel = new JLabel("Slowest car speed: ");
        slowestCarSpeedLabel.setBounds(5, 200, 300, 20);
        statsFrame.add(slowestCarSpeedLabel);
        
        averageNumberOfCarsLabel = new JLabel("Average number of cars at a time: ");
        averageNumberOfCarsLabel.setBounds(5, 220, 300, 20);
        statsFrame.add(averageNumberOfCarsLabel);
        
        mostNumberOfCarsLabel = new JLabel("Most number of cars at a time: ");
        mostNumberOfCarsLabel.setBounds(5, 240, 300, 20);
        statsFrame.add(mostNumberOfCarsLabel);
        
        leastNumberOfCarsLabel = new JLabel("Least number of cars at a time: ");
        leastNumberOfCarsLabel.setBounds(5, 260, 300, 20);
        statsFrame.add(leastNumberOfCarsLabel);
        
        averageLightIntervalLabel = new JLabel("Average light interval: ");
        averageLightIntervalLabel.setBounds(5, 280, 300, 20);
        statsFrame.add(averageLightIntervalLabel);
        
        shortestLightIntervalLabel = new JLabel("Shortest light interval: ");
        shortestLightIntervalLabel.setBounds(5, 300, 300, 20);
        statsFrame.add(shortestLightIntervalLabel);
        
        longestLightIntervalLabel = new JLabel("Longest light interval: ");
        longestLightIntervalLabel.setBounds(5, 320, 300, 20);
        statsFrame.add(longestLightIntervalLabel);
        
        timeOnLS1Label = new JLabel("Time on light scheme 1: ");
        timeOnLS1Label.setBounds(5, 340, 300, 20);
        statsFrame.add(timeOnLS1Label);
        
        timeOnLS2Label = new JLabel("Time on light cheme 2: ");
        timeOnLS2Label.setBounds(5, 360, 300, 20);
        statsFrame.add(timeOnLS2Label);
    }
    
    public void refreshStatsWindow()
    {
        while(true)
        {
            try 
                {
                    currentRunTime = (System.nanoTime() - programStartTime)/1000000000;
                    numberOfFunctionCalls++;
                    calcCarSpeeds();
                    calcNumberOfCarsSlider();
                    calcLightIntervalSlider();
                    calcLightTimes();
                    
                    totalRunTimeLabel.setText("Total run time: " + formatDouble(currentRunTime) + " seconds");
                    averageTravelTimeLabel.setText("Average travel time: " + formatDouble(averageTravelTime));
                    longestTravelTimeLabel.setText("Longest travel time: " + longestTravelTime);
                    shortestTravelTimeLabel.setText("Shortest travel time: " + shortestTravelTime);
                    averageWaitTimeLabel.setText("Average wait time: " + formatDouble(averageWaitTime));
                    longestWaitTimeLabel.setText("Longest wait time: " + longestWaitTime);
                    shortestWaitTimeLabel.setText("Shortest wait time: " + shortestWaitTime);
                    totalCarsRecordedLabel.setText("Number of cars recorded: " + completedCarCount);
                    averageCarSpeedLabel.setText("Average car speed: " + formatDouble(averageCarSpeed) + " pixels per second");
                    fastestCarSpeedLabel.setText("Fastest car speed: " + fastestCarSpeed + " pixels per second");
                    slowestCarSpeedLabel.setText("Slowest car speed: " + slowestCarSpeed + " pixels per second");
                    averageNumberOfCarsLabel.setText("Average number of cars at a time: " + formatDouble(averageCarNumberSlider));
                    mostNumberOfCarsLabel.setText("Most number of cars at a time: " + mostCarNumberSlider);
                    leastNumberOfCarsLabel.setText("Least number of cars at a time: " + leastCarNumberSlider);
                    averageLightIntervalLabel.setText("Average light interval: " + formatDouble(averageLightIntervalSlider));
                    shortestLightIntervalLabel.setText("Shortest light interval: " + shortestLightIntervalSlider);
                    longestLightIntervalLabel.setText("Longest light interval: " + longestLightIntervalSlider);
                    timeOnLS1Label.setText("Time on light scheme 1: " + timeOnLS1 + " seconds");
                    timeOnLS2Label.setText("Time on light scheme 2: " + timeOnLS2 + " seconds");
                    Thread.sleep(50);
                }
                catch (InterruptedException ie) 
                {
                    
                }
            
        }
    }
    
    private void calcLightTimes()
    {
        timeOnLS1 = lc.getTimeLS1();
        timeOnLS2 = lc.getTimeLS2();
    }
    
    private void calcTravelTimes(double newTime)
    {
        if(longestTravelTime < newTime)
            longestTravelTime = newTime;
        
        if(shortestTravelTime > newTime || shortestTravelTime == 0.0)
            shortestTravelTime = newTime;
        
        allCarsTotalTravelTime += newTime;
        averageTravelTime = allCarsTotalTravelTime / completedCarCount;
    }
    
    private void calcWaitTimes(double waitTime)
    {
        if(longestWaitTime < waitTime)
            longestWaitTime = waitTime;
        
        if(shortestWaitTime > waitTime)
            shortestWaitTime = waitTime;
        
        allCarsTotalWaitTime += waitTime;
        averageWaitTime = allCarsTotalWaitTime / completedCarCount;
    }
    
    private void calcCarSpeeds()
    {
        if(fastestCarSpeed < carSpeedValue)
            fastestCarSpeed = carSpeedValue;
        
        if(slowestCarSpeed > carSpeedValue || slowestCarSpeed == 0.0)
            slowestCarSpeed = carSpeedValue;
        
        totalCarSpeeds += carSpeedValue;
        averageCarSpeed = totalCarSpeeds / numberOfFunctionCalls;
    }
    
    private void calcNumberOfCarsSlider()
    {
        if(mostCarNumberSlider < carNumberValue)
            mostCarNumberSlider = carNumberValue;
        
        if(leastCarNumberSlider > carNumberValue || leastCarNumberSlider == 0.0)
            leastCarNumberSlider = carNumberValue;
        
        totalCarNumberSlider += carNumberValue;
        averageCarNumberSlider = totalCarNumberSlider / numberOfFunctionCalls;
    }
    
    private void calcLightIntervalSlider()
    {
        if(longestLightIntervalSlider < lightSpeedValue)
            longestLightIntervalSlider = lightSpeedValue;
        
        if(shortestLightIntervalSlider > lightSpeedValue || shortestLightIntervalSlider == 0.0)
            shortestLightIntervalSlider = lightSpeedValue;
        
        totalLightIntervalSlider += lightSpeedValue;
        averageLightIntervalSlider = totalLightIntervalSlider / numberOfFunctionCalls;
    }

    protected JMenuBar statsMenuBar()
    {
        menuBar = new JMenuBar();
        fc = new JFileChooser();
        
        JMenu menu = new JMenu("File");
        menu.setMnemonic('F');
        menuBar.add(menu);
        
        JMenuItem save = new JMenuItem("Save");
        save.setMnemonic('S');
        save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
        save.setActionCommand("Save");
        save.addActionListener(new saveAL());
        menu.add(save);
        
        JMenuItem clear = new JMenuItem("Clear");
        clear.setMnemonic('C');
        clear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
        clear.setActionCommand("Clear");
        clear.addActionListener(new clearAL());
        menu.add(clear);
        
        return menuBar;
    }
    
    private void saveFile(String fileName)
    {
        
        File file = new File(fileName);
        
        try
        {
            file.createNewFile();
            PrintWriter out = new PrintWriter(fileName);
            
            out.println(totalRunTimeLabel.getText());
            out.println(averageTravelTimeLabel.getText());
            out.println(longestTravelTimeLabel.getText());
            out.println(shortestTravelTimeLabel.getText());
            out.println(averageWaitTimeLabel.getText());
            out.println(longestWaitTimeLabel.getText());
            out.println(shortestWaitTimeLabel.getText());
            out.println(totalCarsRecordedLabel.getText());
            out.println(averageCarSpeedLabel.getText());
            out.println(fastestCarSpeedLabel.getText());
            out.println(slowestCarSpeedLabel.getText());
            out.println(averageNumberOfCarsLabel.getText());
            out.println(mostNumberOfCarsLabel.getText());
            out.println(leastNumberOfCarsLabel.getText());
            out.println(averageLightIntervalLabel.getText());
            out.println(shortestLightIntervalLabel.getText());
            out.println(longestLightIntervalLabel.getText());
            out.println(timeOnLS1Label.getText());
            out.println(timeOnLS2Label.getText());
            
            out.close();
        }
        catch(Exception e)
        {
            
        }
    }

    private void initBGLayer() //initialize the background JlayeredPane, and ImageIcons to display the background and streets
    {
        bgLayer = new JLayeredPane(); //bg layer level 0, lights level 1, car level 2
        bg = new JLabel();
        streets = new JLabel();
        buildings = new JLabel();
        titleImage = new JLabel();
        carSpeedImage = new JLabel();
        carNumberImage = new JLabel();
        lightIntervalImage = new JLabel();
        
        titleImage.setIcon(new ImageIcon(TITLE_IMAGE));
        titleImage.setBounds(50, 20, 133, 44);
        bgLayer.add(titleImage, new Integer(0));
        
        carSpeedImage.setIcon(new ImageIcon(CARSPEED_IMAGE));
        carSpeedImage.setBounds(4, 135, 211, 47);
        bgLayer.add(carSpeedImage, new Integer(0));
        
        carNumberImage.setIcon(new ImageIcon(NUMBEROFCARS_IMAGE));
        carNumberImage.setBounds(0, 195, 189, 41);
        bgLayer.add(carNumberImage, new Integer(0));
        
        lightIntervalImage.setIcon(new ImageIcon(LIGHTINTERVAL_IMAGE));
        lightIntervalImage.setBounds(10, 75, 156, 49);
        bgLayer.add(lightIntervalImage, new Integer(0));
        
        bg.setIcon(new ImageIcon(BACKGROUND));
        bg.setBounds(0, 0, 800, 600);
        bgLayer.add(bg, new Integer(0)); //the higher the number, the higher the layer
        
        streets.setIcon(new ImageIcon(STREET));
        streets.setBounds(241, 41, 518, 518);
        bgLayer.add(streets, new Integer(1));
        
        buildings.setIcon(new ImageIcon(BUILDINGS));
        buildings.setBounds(241, 41, 518, 518);
        bgLayer.add(buildings, new Integer(4));
    }
    
    private void initLightsLayer() //initialize the lights JlayeredPane, and ImageIcons to display the lights, as well as the slider for the lights
    {
        lightsLayer = new JLayeredPane();//each JLayeredPane has a level in the JFrame, and each component of a JLayered pane has levels within the layer (for sliders and JLabels etc.)
        
        lightSpeed = new JSlider(500, 5000, 3000); //(min value, max value, start value) time in ms for light thread sleep
        int i, j;
        
        for(i = 0; i < 10; i++) //initializing the light images, determining light directions based on i/j position
        {
            for(j = 0; j < 10; j++) //first 2 letters are the directions of the lights, the 3rd letter is the direction of the green light
            {
                lightImageMatrix[i][j] = new JLabel(); //create a jlabel for each light
                
                if(i%2 == 0 && j%2 == 0)
                    lightImageMatrix[i][j].setIcon(new ImageIcon(NEN)); //create if's to determine which light image and pixel location
                else if(i%2 == 1 && j%2 == 0)
                    lightImageMatrix[i][j].setIcon(new ImageIcon(NWN));
                else if(i%2 == 0 && j%2 == 1)
                    lightImageMatrix[i][j].setIcon(new ImageIcon(SES));
                else if(i%2 == 1 && j%2 == 1)
                    lightImageMatrix[i][j].setIcon(new ImageIcon(SWS));
                else
                    System.err.println("Error initializing light images.");
                
                lightImageMatrix[i][j].setBounds((56*j)-14, (56*i)-14, 42, 42); //each street is 14 pixels wide, blocks are 42 pixels wide
                
                lightsLayer.add(lightImageMatrix[i][j], new Integer(0)); //add the lights to the lights layer for display
            }
        }
        
        lightsLayer.setBounds(241, 41, 518, 518);
        bgLayer.add(lightsLayer, new Integer(2));
    }
    
    private void initCarsLayer() //initialize the cars JlayeredPane, and ImageIcons to display the cars
    {
        carLayer = new JLayeredPane();
        int i, numCars;
        numCars = car.getNumcars();
        
        for(i = 0; i < numCars; i++) //adding 100 car images off screen, when a car is "spawned" just move one on screen etc.
        {
            carImageBuffer = new JLabel();
            carImageBuffer.setIcon(new ImageIcon(CARS)); //these images are being spawned "out of frame" and simply moved to the correct place when the cars are spawned
            carImageBuffer.setBounds(-20, -20, 14, 14); //-20, -20 gets it just out of frame
            carLayer.add(carImageBuffer, new Integer(0)); //adding cars to the car layer at level 0, but the car layer itself will be level 2 in the bgLayer
            carImageArray.add(carImageBuffer);
        }
        
        carLayer.setBounds(241, 41, 518, 518); //sets bounds of car and light layer to only be within that square
        bgLayer.add(carLayer, new Integer(3));
    }
    
    private void initButtonsLayer() //initialize the buttons JlayeredPane, and JButtons / JLabels to show the buttons, as well as the slider for the cars
    {
        buttonLayer = new JLayeredPane();
        carSpeedSliderLabel = new JLabel("33p/s");
        carNumberLabel = new JLabel("20");
        carSpeed = new JSlider(10, 100, 30);
        carNumber = new JSlider(1, 80, 20);
        userCarStart = new JTextField("0", 2);
        userCarDest = new JTextField("0", 2);
        userCarLabel1 = new JLabel("Create your own car!");
        userCarLabel2 = new JLabel("Enter any numbers 0 - 99:");
        userCarFeedback = new JTextArea(20, 2);
        userCarButton = new JButton("Go!");
        LSSliderLabel = new JLabel("3.0s");
        lightScheme1 = new JButton("1");
        lightScheme2 = new JButton("2");
        lightSchemeLabel = new JLabel("Lighting Schemes:");
        statsButton = new JButton("Open Stats Window");
        
        statsButton.setBounds(10, 500, 200, 40);
        statsButton.addActionListener(new statsButtonAL());
        buttonLayer.add(statsButton, new Integer(0));
        
        lightSchemeLabel.setBounds(10, 440, 200, 20);
        buttonLayer.add(lightSchemeLabel, new Integer(0));
        
        lightScheme1.setBounds(10, 460, 60, 30);
        lightScheme1.addActionListener(new lightScheme1AL());
        lightScheme1.setEnabled(false);
        buttonLayer.add(lightScheme1, new Integer(0));
        
        lightScheme2.setBounds(80, 460, 60, 30);
        lightScheme2.addActionListener(new lightScheme2AL());
        buttonLayer.add(lightScheme2, new Integer(0));
        
        userCarStart.setBounds(10, 310, 70, 40);
        userCarStart.setBorder(BorderFactory.createTitledBorder("Start:"));
        userCarStart.setOpaque(false);
        buttonLayer.add(userCarStart, new Integer(0));
        
        userCarDest.setBounds(90, 310, 70, 40);
        userCarDest.setBorder(BorderFactory.createTitledBorder("End:"));
        userCarDest.setOpaque(false);
        buttonLayer.add(userCarDest, new Integer(0));
        
        userCarLabel1.setBounds(10, 280, 200, 13);
        buttonLayer.add(userCarLabel1, new Integer(0));
        userCarLabel2.setBounds(10, 295, 200, 13);
        buttonLayer.add(userCarLabel2, new Integer(0));
        
        userCarButton.setBounds(170, 318, 60, 30);
        userCarButton.addActionListener(new userCarButtonAL());
        buttonLayer.add(userCarButton, new Integer(0));
        
        userCarFeedback.setBounds(10, 360, 220, 80);
        userCarFeedback.setBorder(BorderFactory.createTitledBorder("Feedback:"));
        userCarFeedback.setOpaque(false);
        userCarFeedback.setEditable(false);
        userCarFeedback.setLineWrap(true);
        userCarFeedback.setWrapStyleWord(true);
        buttonLayer.add(userCarFeedback, new Integer(0));
        
        //lightSpeed.setBorder(BorderFactory.createTitledBorder("Light Interval"));
        lightSpeed.setBounds(50, 90, 150, 60); //setBounds is formatted (x, y, width, height)
        lightSpeed.addChangeListener(new lightSpeedCL());
        lightSpeed.setMajorTickSpacing(500);
        lightSpeed.setPaintTicks(true);
        lightSpeed.setSnapToTicks(true);
        lightSpeed.setOpaque(false);
        buttonLayer.add(lightSpeed, new Integer(0));
        LSSliderLabel.setBounds(200, 110, 40, 20);
        buttonLayer.add(LSSliderLabel, new Integer(0));
        
        //carSpeed.setBorder(BorderFactory.createTitledBorder("Car Speed (time per pixel)"));
        carSpeed.setBounds(50, 150, 150, 60);
        carSpeed.addChangeListener(new carSpeedCL());
        carSpeed.setMajorTickSpacing(10);
        carSpeed.setPaintTicks(true);
        carSpeed.setSnapToTicks(true);
        carSpeed.setOpaque(false);
        buttonLayer.add(carSpeed, new Integer(0));
        carSpeedSliderLabel.setBounds(200, 170, 40, 20);
        buttonLayer.add(carSpeedSliderLabel, new Integer(0));
        
        //carNumber.setBorder(BorderFactory.createTitledBorder("Number of cars"));
        carNumber.setBounds(50, 210, 150, 60);
        carNumber.addChangeListener(new carNumberCL());
        carNumber.setOpaque(false);
        buttonLayer.add(carNumber, new Integer(0));
        carNumberLabel.setBounds(200, 230, 40, 20);
        buttonLayer.add(carNumberLabel, new Integer(0));
        
        //buttonLayer.setBorder(BorderFactory.createTitledBorder("Settings:"));
        //buttonLayer.setBorder(BorderFactory.createLineBorder(Color.RED));
        //buttonLayer.setBackground(Color.BLUE);
        //buttonLayer.setOpaque(true);
        buttonLayer.setBounds(0, 0, 241, 600);
        bgLayer.add(buttonLayer, new Integer(1));
    }
    
    private void refreshLights() //updates the image of each light depending on its current light state (greenstate variable)
    {
        int i, j;
        for(i = 0; i<10; i++)
        {
            for(j = 0; j<10; j++)
            {
                if(i%2 == 0 && j%2 == 0 && lc.getLightFromMatrix(i, j).getLightState() == 0)
                    lightImageMatrix[i][j].setIcon(new ImageIcon(NEN));
                else if(i%2 == 0 && j%2 == 0 && lc.getLightFromMatrix(i, j).getLightState() == 1)
                    lightImageMatrix[i][j].setIcon(new ImageIcon(NEE));
                else if(i%2 == 1 && j%2 == 0 && lc.getLightFromMatrix(i, j).getLightState() == 0)
                    lightImageMatrix[i][j].setIcon(new ImageIcon(NWN));
                else if(i%2 == 1 && j%2 == 0 && lc.getLightFromMatrix(i, j).getLightState() == 1)
                    lightImageMatrix[i][j].setIcon(new ImageIcon(NWW));
                else if(i%2 == 0 && j%2 == 1 && lc.getLightFromMatrix(i, j).getLightState() == 0)
                    lightImageMatrix[i][j].setIcon(new ImageIcon(SES));
                else if(i%2 == 0 && j%2 == 1 && lc.getLightFromMatrix(i, j).getLightState() == 1)
                    lightImageMatrix[i][j].setIcon(new ImageIcon(SEE));
                else if(i%2 == 1 && j%2 == 1 && lc.getLightFromMatrix(i, j).getLightState() == 0)
                    lightImageMatrix[i][j].setIcon(new ImageIcon(SWS));
                else if(i%2 == 1 && j%2 == 1 && lc.getLightFromMatrix(i, j).getLightState() == 1)
                    lightImageMatrix[i][j].setIcon(new ImageIcon(SWW));
                else
                    System.err.println("Error initializing light images.");
            }
        }
    }
    
    public void lightsAnimator() //main function of the lights thread, this handles the light change interval, and the changing of the lights
    {
        try
        {
            while(true)
            {
                
                Thread.sleep(lightSpeed.getValue()); //getting value from slider to determine the sleep time
                lc.changeLights(); //make every light change
                refreshLights();
            }
        }
        catch(InterruptedException ie)
        {
            //If this thread was intrrupted by nother thread
        }
    }
    
    private void initializeCarImages() //move the car images to their corresponding locations, and have them face the correct direction, this is called after the car values have been determined
    {
        int numCars = car.getNumcars();
        int i, dir;
        
        for(i=0; i<numCars; i++)
        {
            carImageBuffer = (JLabel)carImageArray.get(i);
            carImageBuffer.setLocation(14*car.getCarFromArray(i).getOMJ(), 14*car.getCarFromArray(i).getOMI()); //each OMI and OMJ value represents 14 pixels in length (42/3)
            car.getCarFromArray(i).setPixelLoc(14*car.getCarFromArray(i).getOMJ(), 14*car.getCarFromArray(i).getOMI()); //OMI = occupancy matrix I
            dir = car.getCarFromArray(i).getDirection();
            
            if(dir == 0)
                carImageBuffer.setIcon(new ImageIcon(CARN));
            else if(dir == 1)
                carImageBuffer.setIcon(new ImageIcon(CARE));
            else if(dir == 2)
                carImageBuffer.setIcon(new ImageIcon(CARS));
            else if(dir == 3)
                carImageBuffer.setIcon(new ImageIcon(CARW));
        }
        
    }
    
    private void moveCars() //move each car one pixel in the proper direction, if it is movable, and update the car's internal variables as well to reflect the new position
    {
        int numCars = car.getNumcars();
        int i, carX, carY, dir;
        
        
        for(i = 0; i < numCars; i++)
        {
            if(car.getCarFromArray(i).getDrive() && !car.getCarFromArray(i).getStoppedAtLight() && !car.getCarFromArray(i).getNextStreetFull())
            {
                carImageBuffer = (JLabel)carImageArray.get(i);
                carX = car.getCarFromArray(i).getPixelX(); //sort of self explanatory
                carY = car.getCarFromArray(i).getPixelY();
                dir = car.getCarFromArray(i).getDirection(); //common theme throughout 0 = north, 1 = east, 2 = south, 3 = west
                if(dir == 0)
                {
                    carImageBuffer.setLocation(carX, carY-1);
                    car.getCarFromArray(i).setPixelLoc(carX, carY-1);
                }
                else if(dir == 1)
                {
                    carImageBuffer.setLocation(carX+1, carY);
                    car.getCarFromArray(i).setPixelLoc(carX+1, carY);
                }
                else if(dir == 2)
                {
                    carImageBuffer.setLocation(carX, carY+1);
                    car.getCarFromArray(i).setPixelLoc(carX, carY+1);
                }
                else if(dir == 3)
                {
                    carImageBuffer.setLocation(carX-1, carY);
                    car.getCarFromArray(i).setPixelLoc(carX-1, carY);
                }
            }
        }
    }
    
    private void turnCar() //checks to see which direction the cars need to go when entering an intersection, and sets the cars' direction to their queued direction, and changes their images to reflect the change in direction 
    {
        int i, numcars, turningDir, x, y;
        numcars = car.getNumcars();
        
        
        for(i = 0; i < numcars; i++)
        {
            x = car.getCarFromArray(i).getPixelX();
            y = car.getCarFromArray(i).getPixelY();
            carImageBuffer = (JLabel)carImageArray.get(i);
            
            if(x%56 == 0 && y%56 == 0 && car.getCarFromArray(i).isTurning()) //if car is in an intersection AND turning
            {
                turningDir = car.getCarFromArray(i).getTurningDirection();
                //System.out.println("Turning direction is now: " + turningDir + ", while at intersection: " + y/56 + "" + x/56);
                //System.out.println(y/56 + "" + x/56 + " ");
                car.getCarFromArray(i).setDirection(turningDir);
                
                if(car.getCarFromArray(i).getIsUserCar())
                {
                    if(turningDir == 0)
                        carImageBuffer.setIcon(new ImageIcon(USERCARN));
                    else if(turningDir == 1)
                        carImageBuffer.setIcon(new ImageIcon(USERCARE));
                    else if(turningDir == 2)
                        carImageBuffer.setIcon(new ImageIcon(USERCARS));
                    else if(turningDir == 3)
                        carImageBuffer.setIcon(new ImageIcon(USERCARW));
                }
                else
                {
                    if(turningDir == 0)
                        carImageBuffer.setIcon(new ImageIcon(CARN));
                    else if(turningDir == 1)
                        carImageBuffer.setIcon(new ImageIcon(CARE));
                    else if(turningDir == 2)
                        carImageBuffer.setIcon(new ImageIcon(CARS));
                    else if(turningDir == 3)
                        carImageBuffer.setIcon(new ImageIcon(CARW));
                }
                
                //System.out.println("Car: "+i+" just turned to direction: "+turningDir+" with OMI: "+car.getCarFromArray(i).getOMI()+" and OMJ: "+car.getCarFromArray(i).getOMJ());
                
                car.getCarFromArray(i).finishedTurn();
            }
            
        }
    }
    
    private void checkCarsForDespawn() 
    {
        /*
         * this will immediately overwrite the car's current values with new ones 
         * that are randomly generated, essentially "despawning" the old car and 
         * "spawning" the new one at once. It also references the goal for the number 
         * of cars, so if goal < current number of cars, a car will fully despawn instead
         * of spawning a new one.
         */
        int i, numcars, numcarsGoal, dir;
        numcars = car.getNumcars();
        
        for(i = 0; i < numcars; i++)
        {
            carImageBuffer = (JLabel)carImageArray.get(i);
            
            numcarsGoal = car.getNumCarsGoal();
            
            if(car.getCarFromArray(i).getDespawnFlag())
            {
                //First stop timer and report travel time!!!
                car.getCarFromArray(i).stopTimer();
                completedCarCount++;
                calcTravelTimes(car.getCarFromArray(i).getTravelTime());
                calcWaitTimes(car.getCarFromArray(i).getWaitTime());
                
                if(car.getCarFromArray(i).getIsUserCar()) //if this car is a user generated car, set the button to be clickable again
                    {
                        userCarButton.setEnabled(true);
                        userCarFeedback.setText("The generated car had a travel time of " + car.getCarFromArray(i).getTravelTime() 
                                + " seconds, and wait time of " + car.getCarFromArray(i).getWaitTime() + " seconds.");
                    }
                
                if(numcarsGoal < numcars)
                {
                    carImageBuffer = (JLabel)carImageArray.get(i);
                    carImageBuffer.setIcon(null);
                    car.setOMForDespawnedCar(i);
                    car.removeCarFromList(i);
                    carImageArray.remove(i);
                    car.reduceNumCarsByOne();
                    
                    //System.out.println("Car: " + i + " has been despawned.");
                    //car.printOM();
                    //System.out.println();
                }
                else if(numcarsGoal >= numcars)
                {
                    car.spawnNewCar(i); //generates new car at an empty intersection
                    
                    carImageBuffer.setLocation(14*car.getCarFromArray(i).getOMJ(), 14*car.getCarFromArray(i).getOMI());
                    car.getCarFromArray(i).setPixelLoc(14*car.getCarFromArray(i).getOMJ(), 14*car.getCarFromArray(i).getOMI());
                    dir = car.getCarFromArray(i).getDirection();
                    
                    if(dir == 0)
                        carImageBuffer.setIcon(new ImageIcon(CARN));
                    else if(dir == 1)
                        carImageBuffer.setIcon(new ImageIcon(CARE));
                    else if(dir == 2)
                        carImageBuffer.setIcon(new ImageIcon(CARS));
                    else if(dir == 3)
                        carImageBuffer.setIcon(new ImageIcon(CARW));
                }
            }
            numcars = car.getNumcars();
        }
    }
    
    private void checkForNewCar() //creates a new car when needed, i.e. when our number of cars goal > number of cars, which is represented by the new car flag
    {
        int numCars, dir;
        
        if(car.getNewCarFlag())
        {
            car.resetNewCarFlag();
            
            carImageBuffer = new JLabel();
            //carImageBuffer.setIcon(new ImageIcon("carS.gif")); //these images are being spawned "out of frame" and simply moved to the correct place when the cars are spawned
            carImageBuffer.setBounds(-20, -20, 14, 14); //-20, -20 gets it just out of frame
            carLayer.add(carImageBuffer, new Integer(0)); //adding cars to the car layer at level 0, but the car layer itself will be level 2 in the bgLayer
            carImageArray.add(carImageBuffer);
            
            numCars = car.getNumcars();
            carImageBuffer = (JLabel)carImageArray.get(numCars-1);
            carImageBuffer.setLocation(14*car.getCarFromArray(numCars-1).getOMJ(), 14*car.getCarFromArray(numCars-1).getOMI());
            car.getCarFromArray(numCars-1).setPixelLoc(14*car.getCarFromArray(numCars-1).getOMJ(), 14*car.getCarFromArray(numCars-1).getOMI());
            dir = car.getCarFromArray(numCars-1).getDirection();
            
            if(dir == 0)
                    carImageBuffer.setIcon(new ImageIcon(CARN));
                else if(dir == 1)
                    carImageBuffer.setIcon(new ImageIcon(CARE));
                else if(dir == 2)
                    carImageBuffer.setIcon(new ImageIcon(CARS));
                else if(dir == 3)
                    carImageBuffer.setIcon(new ImageIcon(CARW));
        }
    }
    
    public void carAnimator() //main function of the car thread, handles everything to do with the cars
    {
        initializeCarImages();
        
        try
        {
            while(true)
            { //the ordering of these functions is very important, they must stay in this order
                
                Thread.sleep(carSpeed.getValue()); //affects car speed
                
                car.setNumCarsGoal(carNumber.getValue());
                
                checkCarsForDespawn(); //all the time
                
                car.checkNumCarsGoal();
                
                checkForNewCar();
                
                car.checkCarsAgainstLights(lc); //if car is at an intersection
                
                car.checkCarsAgainstPath(); //if car is at an intersection
                
                turnCar(); //must be called after path check, while IN an intersection
                
                car.checkCarsAgainstOM(); //called at every slot must be done after turnCar b/c it needs the updated direction to check the OM correctly
                
                moveCars();
                
            }
        }
        catch(InterruptedException e)
        {
        
        }
    
    
    }
    
    
    
    
}
