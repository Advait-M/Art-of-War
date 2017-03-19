/*
 * This program simulates war through the use of mechanisms similar to those used
 * in Conway's Game of Life. The user can play an interactive game against the computer
 * in order to see who wins.
 */
package artofwar;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.*;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import java.util.concurrent.ThreadLocalRandom;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

/**
 *
 * @author mayba4372
 */
public class ArtOfWar extends JFrame
{
    static private int width = 900; //Width of the window in pixels
    static private int height = 900; //Height of the window in pixels
    
    static private final int numGenerations = 50000; //Number of total generations
    static private int generationNum; //Keeps track of current generation
    
    static private final int numCellsY = 50; //Height of grid
    static private final int numCellsX = 50; //Width of grid
    
    static private Color deadColor = Color.YELLOW;  //Color of dead cells
    
    private String fileName = "Initial cells.txt"; //File name to load cells from (if wanted)
    
    //states[][] keeps track of the current generation's cell states. Explanation of each state:
    //1:Player 1 (human) normal cells, 2:Player 2 (computer) normal cells
    //3:Player 1 base cells 4:Player 2 base cells
    private int states[][] = new int[numCellsY][numCellsX];
    //Used to update the states for the next generation (then it is used to overwrite the states array)
    private int statesNext[][] = new int[numCellsY][numCellsX];
    
    static private int startOfGrid = 0; //Defines the start of the grid (starting coordinate)
    
    //A 2D array of JButtons (used for displaying the grid to the user)
    private JButton buttonArray[][] = new JButton[numCellsY][numCellsX];
    private JPanel buttons = new JPanel(); //Container for the button field 
    
    private JPanel hud = new JPanel(); //Container for the statistics at the top of the screen
    private JLabel fpsText = new JLabel("FPS: "); //Text used at the top of the screen    
    static private String genString = "Generation: " + generationNum; //String added to the genNum label
    static private JLabel genNum = new JLabel(); //Used to store the current generation number
    static private boolean gridLines = true; //Boolean that controls whether the grid lines are enabled
    
    //ImageIcons used to store the images used for player and enemy sprites
    private ImageIcon playerImage = new ImageIcon("player.png");
    private ImageIcon deadImage = new ImageIcon("dead.png");
    private ImageIcon AIImage = new ImageIcon("enemy.png");
    private ImageIcon base1Image = new ImageIcon("base1.png");
    private ImageIcon base2Image = new ImageIcon("base2.png");
    
    static int FPSMin = 1; //Minimum frames per second
    static int FPSMax = 50; //Maximum frames per second
    static int FPSInit = 10; //Initial frames per second
    //JSlider used to control the frames per second
    static JSlider framesPerSecond = new JSlider(JSlider.HORIZONTAL, FPSMin, FPSMax, FPSInit);
    
    static boolean gameEnded = false; //Boolean keeps track of whether the game has ended (if someone has won)
    
    /*
     * Constructor used to create the ArtOfWar object
     * Initializes the button grid and associated variables
     */
    public ArtOfWar() 
    {  
        //Ensures the states array is filled with 0s (sets all cells to dead)
        makeEveryoneDead();
        
        //Set aesthetic attributes of the buttons JPanel
        buttons.setSize(width, height);
        buttons.setBackground(Color.black);
        buttons.setLayout(new GridLayout(numCellsX, numCellsY));
        buildButtonField(buttons); //Builds the 2D array of JButtons
        
        //Add the desired statistics to the hud JPanel
        hud.add(genNum, BorderLayout.NORTH);
        hud.add(fpsText, BorderLayout.EAST);
        
        //Set up the JSlider used for the FPS
        framesPerSecond.setMinorTickSpacing(1);
        framesPerSecond.setMajorTickSpacing(5);
        framesPerSecond.setPaintTicks(true);
        framesPerSecond.setPaintLabels(true);      
        //Add the FPS slider to the hud
        hud.add(framesPerSecond, BorderLayout.SOUTH);
        
        //Add the button field and the hud to the JFrame (ArtOfWar)
        add(buttons, BorderLayout.CENTER);
        add(hud, BorderLayout.NORTH);
        
        //Calls paint() and shows the JFrame to the user
        setVisible(true);
    }
    
    /*
     * Used to handle mouse clicks from the user
     * Finds out if the mouse click is a valid move and executes code accordingly
     */
    private class MouseHandler extends MouseAdapter {
        public int row, column;
        //Constructor initializes row and column variables
        public MouseHandler(int r, int c)
        {
            this.row = r;
            this.column = c;
        }
        //Method is called whenever a mouse is clicked (on a button)
        public void mouseClicked(MouseEvent e)
        {
            //Verifies the mouse click was the left mouse button (primary button)
            if(e.getButton() == 1)
            {
                if (!readyToStart()){ //If this is the first click of the game
                    if(plantBase(column, row, 1) == 0){ //If the coordinates are a legal place to plant a base
                        //Create random x and y coordinates for the computer's base
                        int randX = ThreadLocalRandom.current().nextInt(1, numCellsX-2);
                        int randY = ThreadLocalRandom.current().nextInt(1, numCellsY-2);
                        //Ensure the computer's base is 15 units away from the player's base
                        while (countBaseCell(randX, randY, 3, 15) != 0){
                            //If the computer's base is too close to the player's base, select new x and y coordinates
                            randX = ThreadLocalRandom.current().nextInt(1, numCellsX-2);
                            randY = ThreadLocalRandom.current().nextInt(1, numCellsY-2);
                        }
                        //Plant the computer's base
                        plantBase(randX, randY, 2);
                    }
                }
                //Otherwise, make sure the coordinates are 5 units away from any base (to be a legal move)
                else if(countBaseCell(column, row, 4, 5) == 0 && countBaseCell(column, row, 3, 5) == 0){
                    //Plant the player's 3x3 block
                    plantBlock(column-1, row-1, 3, 3, 1);
                    //Choose random coordinates for the computer's base
                    int randX = ThreadLocalRandom.current().nextInt(0, numCellsX-2);
                    int randY = ThreadLocalRandom.current().nextInt(0, numCellsY-2);
                    //Plant the computer's 3x3 block
                    plantBlock(randX, randY, 3, 3, 2);
                }
            }
        }
    }
    
    /*
     * Plants the player's or computer's base
     */
    public int plantBase(int centerX, int centerY, int state){
        //Returns -1 if the position of the base is not allowed
        if (centerX == 0 || centerY == 0 || centerX == numCellsX-1 || centerY == numCellsY-1){
            return -1;
        }
        
        //Calculate the ending column and row of the base
        int endCol = Math.min(centerX-1 + 3, numCellsX);
        int endRow = Math.min(centerY-1 + 3, numCellsY);
        
        //Iterate through the desired coordinates
        for (int i = centerY-1; i < endRow; i++) {
            for (int j = centerX-1; j < endCol; j++) {
                if (((i == centerY+1 || i == centerY-1) && j != centerX) || ((j == centerX+1 || j == centerX-1) && i != centerY)){
                    //Set the 4 corners of the center square to be the desired base (player or computer)
                    if (state == 1){
                        states[i][j] = 3;
                    }
                    else{
                        states[i][j] = 4;
                    }
                }
                else{
                    //Otherwise, set the cell to be part of the desired army (player or computer)
                    states[i][j] = state;
                }
            }
        }
        //Returns 0 to indicate successful execution of the method
        return 0;
    }
    
    //Initializes the window, specifically plants a generation if wanted
    public void initializeGame() throws IOException{
        playSound(); //Starts music
        plantFirstGeneration(); //Plant initial generation if wanted
    }
    
    public void plantFirstGeneration() throws IOException {
        //Plant first generation from file
        //plantFromFile(fileName);
        
        //Or plant first generation using defined functions to create patterns
        //plantBlock (20, 20, 5, 20, 1);
    }
    
    //Reads the first generation's alive cells from a file
    public void plantFromFile(String fileName) throws IOException {
        //Used to read from the desired file
        FileReader f = new FileReader(fileName);
        Scanner s = new Scanner(f);

        int x, y;
        //Format of file: "x-coord y-coord state"
        //Loops throught the entire file
        while (s.hasNext()) {
            x = s.nextInt();
            y = s.nextInt();
            
            //Set the desired cell to the specified state
            states[y][x] = s.nextInt();
        }
    }
    
    //Plants a block (rectangle) with the desired attributes
    public void plantBlock(int startX, int startY, int numColumns, int numRows, int state) {
        //If the player has selected the left column or top row, offset the click to make it a legal move
        if (startX < 0){
            startX = 0;
        }
        if (startY < 0){
            startY = 0;
        }
        
        //Find the ending columns and rows with the desired attributes
        int endCol = Math.min(startX + numColumns, numCellsX);
        int endRow = Math.min(startY + numRows, numCellsY);
        
        //Set the specified cells to the desired state
        for (int i = startY; i < endRow; i++) {
            for (int j = startX; j < endCol; j++) {
                states[i][j] = state;
            }
        }
    }
    
    //Sets all cells to dead
    public void makeEveryoneDead() {
        for (int i = 0; i < numCellsX; i++) {
            for (int j = 0; j < numCellsY; j++) {
                states[i][j] = 0;
            }
        }
    }   
    
    //Plays the background music
    public static void playSound() {
        try {
            //Import the music into the program is an AudioInputStream
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File("music.wav").getAbsoluteFile());
            
            //Play the music by making it a "Clip" object
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(1000); //Causes the music to loop for a 1000 times
            clip.start();
        } catch(Exception ex) {
            System.out.println("Error with playing music.");
            ex.printStackTrace();
        }
    }
    
    //Returns a boolean indicating whether the core game is ready to be initialized
    public boolean readyToStart(){
        //Goes through all grid cells checking for a cell designated as a player 1 base
        for (int y = 0; y < numCellsY; y++){
            for (int x= 0; x < numCellsX; x++){
                if (states[y][x] == 3){
                    return true;
                }
            }
        }
        //Returns false if no player 1 base cell is found
        return false;
    }
    
    //Draws the generation number into the genNum JLabel
    public static void drawGenerationNumLabel(){
        genString = "Generation: " + generationNum;
        genNum.setText(genString);
    }
    
    //Builds the button field used for user interaction and display
    public void buildButtonField(JPanel buttons)
    {
        for(int r = 0; r < numCellsY; r++)
        {
            for(int c = 0; c < numCellsX; c++)
            {
                //Assigns a new button to the desired array index
                buttonArray[r][c] = new JButton();
                //Add a mouse listener to listen for user clicks and handle them correctly
                buttonArray[r][c].addMouseListener(new MouseHandler(r,c));
                
                //Disables button borders if grid lines are not wanted
                if (!gridLines){
                    buttonArray[r][c].setBorderPainted(false);
                }
                
                //Adds the JButton to the buttons JPanel, so the user can see it
                buttons.add(buttonArray[r][c]);
            }
        }
    }
    
    //Updates the screen with the latest data (similar to the paint() method for JFrame)
    public void updateScreen(){
        //Loop through all cells
        for (int y = 0; y < numCellsY; y++){
            for (int x = 0; x < numCellsX; x++){
                //Set the image/colour of the cell according to its state
                if (states[y][x] == 1){ //Player 1 soldier
                    buttonArray[y][x].setIcon(playerImage);
                }
                else if (states[y][x] == 2){ //Player 2 soldier
                    buttonArray[y][x].setIcon(AIImage);
                }
                else if (states[y][x] == 3){ //Player 1 base
                    buttonArray[y][x].setIcon(base1Image);
                }
                else if (states[y][x] == 4){ //Player 2 base
                    buttonArray[y][x].setIcon(base2Image);
                }
                else{ //Otherwise, it is a dead cell
                    buttonArray[y][x].setBackground(deadColor);
                    buttonArray[y][x].setIcon(deadImage);
                }
            }
        }
    }
    
    //Count how many base cells (or any desired state) are within the specified distance
    public int countBaseCell(int x, int y, int state, int distance){
        //Calculate the rows/columns that must be traversed 
        int startCol = Math.max(x-distance, startOfGrid);
        int endCol = Math.min(x+distance, buttonArray.length - 1);
        int startRow = Math.max(y-distance, startOfGrid);
        int endRow = Math.min(y+distance, buttonArray.length - 1);
        
        int baseCount = 0; //Keeps track of how many of the desired cells are nearby
        //Loop through the calculated rows/columns looking for specific states
        for (int i = startRow; i <= endRow; i++){
            for (int j = startCol; j <= endCol; j++){
                if (states[i][j] == state){
                    baseCount++; //Increment baseCount if a cell with the desired state is found
                }
            }
        }
        return baseCount;
    }
    
    //Checks if exactly 1 base cell is touching a specified cell
    public boolean checkBaseCell(int x, int y, int state){
        //Calculate the rows/columns that must be traversed
        int startCol = Math.max(x-1, startOfGrid);
        int endCol = Math.min(x+1, buttonArray.length - 1);
        int startRow = Math.max(y-1, startOfGrid);
        int endRow = Math.min(y+1, buttonArray.length - 1);
        
        int baseCount = 0; //Keeps track of how many of the desired cells are nearby
        //Loop through the calculated rows/columns looking for specific states
        for (int i = startRow; i <= endRow; i++){
            for (int j = startCol; j <= endCol; j++){
                if (states[i][j] == state){
                    baseCount++; //Increment baseCount if a cell with the desired state is found
                }
            }
        }
        //Return true if exactly one base cell is found, otherwise return false
        if (baseCount == 1){
            return true;
        }
        return false;
    }
    
    //Overwrites the current generation's 2-D array with the values from the next generation's 2-D array
    public void plantNextGeneration() {
        //Loop through all of the 1D arrays within statesNext (which is a 2D array)
        for (int y = 0; y < numCellsX; y++) {
            //Copy the 1D array from the "statesNext" 2D array into the "states" 2D array
            System.arraycopy(statesNext[y], 0, states[y], 0, numCellsY);
        }
    }  
    
    //Counts the amount of living neighbours beside the desired cell
    public int countLivingNeighbors(int x, int y, int state) {
        //Define the bounding area of the search (the row/columns directly surrounding the cell)
        //Ensures that there is not a ArrayIndexOutOfBoundsException due to negative indices
        //startOfGrid is set to 0 (the first index/coordinate of the grid)
        int rowStart  = Math.max(y - 1, startOfGrid);
        int rowFinish = Math.min(y + 1, buttonArray.length - 1);
        int colStart  = Math.max(x - 1, startOfGrid);
        int colFinish = Math.min(x + 1, buttonArray.length - 1);
        
        //Initialize a count variable to store the amount of living neighbors
        int count = 0;
        
        //Loop through the desired rows
        for (int curRow = rowStart; curRow <= rowFinish; curRow++) {
            //Loop through the desired columns
            for (int curCol = colStart; curCol <= colFinish; curCol++) {
                //If the current neighbor found is alive AND it is not the cell itself (that we are checking)
                if (states[curRow][curCol] == state && !(curRow == y && curCol == x)){
                    //Increment the count of living neighbors
                    count++;
                }
            }
        }
        return count;
    }
    
    //Applies the rules of The Art of War to set the integer values of the statesNext[][] array,
    //based on the current values in the states[][] array
    public void computeNextGeneration() {
        //Keeps track of how many base cells there are
        int bases1 = 0;
        int bases2 = 0;
        
        //Loop through all cells and update them in the statesNext array
        for (int y = 0; y < numCellsY; y++){
            for (int x= 0; x < numCellsX; x++){
                //Count the number of user controlled neighbours and computer controlled neighbours
                int numNeighbors1 = countLivingNeighbors(x, y, 1); //user
                int numNeighbors2 = countLivingNeighbors(x, y, 2); //computer
                //Apply rules for standard user/computer cells
                if (states[y][x] == 1 || states[y][x] == 2) {
                    int numOwnNeighbors;
                    int numEnemyNeighbors;
                    //Find out the amount of friendly neighbours and enemy neighbours
                    if (states[y][x] == 1){
                        numOwnNeighbors = numNeighbors1;
                        numEnemyNeighbors = numNeighbors2;
                    }
                    else{
                        numOwnNeighbors = numNeighbors2;
                        numEnemyNeighbors = numNeighbors1;
                    }
                    
                    //If it's too lonely (0-2 neighbours) or too crowded (6-8 neighbours) the cell will die
                    if (numOwnNeighbors <= 2 || numOwnNeighbors >= 6){
                        statesNext[y][x] = 0;
                    }
                    //If there are more than 2 enemies surrounding the cell, it will die
                    else if (numEnemyNeighbors > 2){
                        statesNext[y][x] = 0;
                    }
                    //Otherwise, it stays alive
                    else{ 
                        statesNext[y][x] = states[y][x];
                    }
                }
                //If the cell is a base cell
                else if (states[y][x] == 3 || states[y][x] == 4){
                        int numEnemyNeighbors;
                        //Find the number of enemy neighbours (depends on type of base cell)
                        if (states[y][x] == 3){
                            bases1++;
                            numEnemyNeighbors = numNeighbors2;
                        }
                        else{
                            bases2++;
                            numEnemyNeighbors = numNeighbors1;
                        }
                        
                        //If the base cell has 3 or more neighbours then it will die
                        if (numEnemyNeighbors >= 3){
                            statesNext[y][x] = 0;
                        }
                        //Otherwise, the base cell stays alive
                        else{
                            statesNext[y][x] = states[y][x];
                        }
                }                
                else{ //If the cell is currently dead
                    //If it has 3 user cell neighbours OR it has a user cell base close to it and no enemy cells beside it
                    if ((numNeighbors1 == 3) || (checkBaseCell(x, y, 3) && numNeighbors2 == 0)){
                        statesNext[y][x] = 1;
                    }
                    //If it has 3 computer cell neighbours OR it has a computer cell base close to it and no enemy cells beside it
                    else if ((numNeighbors2 == 3) || (checkBaseCell(x, y, 4) && numNeighbors1 == 0)){
                        statesNext[y][x] = 2;
                    }
                    //Otherwise, the cell stays dead
                    else{                        
                        statesNext[y][x] = 0;
                    }
                }
            }
        }
        //If there are no user bases, then end the game
        if (bases1 == 0){
            endGame(2, this); //Computer (player 2) wins
        }
        //If there are no computer bases, then end the game
        else if (bases2 == 0){
            endGame(1, this); //User (player 1) wins
        }
    }
    
    //Ends the game
    public static void endGame(int winner, ArtOfWar currGame){
        gameEnded = true; //Let the other methods know that the game has ended
        String winnerName;
        //Set the winner's name as appropriate
        if (winner == 1){
            winnerName = "the player!";
        }
        else{
            winnerName = "the computer!";
        }
        //Create a JDialog to display the ending message
        JDialog endDialog = new javax.swing.JDialog();
        //Create a JLabel to store the ending message
        JLabel endStatus = new JLabel("The winner is " + winnerName);
        
        endDialog.setPreferredSize(new java.awt.Dimension(500, 600));
        endDialog.setTitle("Winner");
        endDialog.setPreferredSize(new Dimension(700, 100));
        endDialog.setLocationRelativeTo(null);
        endDialog.add(endStatus); //Add the JLabel with the ending message to the JDialog
        endDialog.pack();        
        endStatus.setFont(new Font("Arial", Font.PLAIN, 50));        
        endDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        endDialog.setVisible(true); //Show the dialog box
        currGame.dispose(); //Dispose of the game's JFrame or main window
    }
    
    //Makes the "pause" effect between generations
    public static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } 
        catch (Exception e) {}
    }
    
    //The main method which instantiates the game and calls all of the other methods
    @SuppressWarnings("empty-statement")
    public static void main(String args[]) throws IOException{
        ArtOfWar currGame = new ArtOfWar(); //Instantiate the class
        //Set JFrame attributes
        currGame.setTitle("The Art of War");
        currGame.setSize(width, height);
        currGame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        currGame.setBackground(Color.black);
        
        //Show the starting dialog asking the user to select a cell as their base
        JDialog startDialog = new javax.swing.JDialog();
        //JLabel used to contain the message text
        JLabel startStatus = new JLabel("Please select a cell as your base (do not select the outer border).");
        //Set JDialog and JLabel attributes
        startDialog.setPreferredSize(new java.awt.Dimension(500, 600));
        startDialog.setTitle("Welcome to The Art of War!");
        startDialog.setPreferredSize(new Dimension(900, 100));        
        startStatus.setFont(new Font("Arial", Font.PLAIN, 25));
        startDialog.add(startStatus); //Add the text to the JDialog
        startDialog.pack();
        startDialog.setVisible(true); //Show the dialog box to the user
        
        currGame.initializeGame(); //Start the music and place any first generation cells, if wanted
        currGame.updateScreen(); //Update screen to reflect any first generation cells placed onto the grid
        
        //Wait for the user to click somewhere in the grid to plant their base
        while (!currGame.readyToStart());
        
        startDialog.dispose(); //Delete the entry dialog
        
        //Loop for the desired number of generations
        for (generationNum = 1; generationNum <= ArtOfWar.numGenerations; generationNum++) {
            drawGenerationNumLabel(); //Update the generation number
            
            //Sleep for animation effect
            ArtOfWar.sleep(1000/framesPerSecond.getValue());
            
            //Calculate the state values of the next generation
            currGame.computeNextGeneration();
            //Plant the next generation (replace the states array with the statesNext array)
            currGame.plantNextGeneration();
            //Update the screen to show the user
            currGame.updateScreen();
            
            //If the game has ended, stop animating more generations
            if (gameEnded){
                break;
            }
        }
    }
} //End of ArtOfWar class