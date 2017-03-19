# Art-of-War
An interactive Java game designed to simulate war. Similar mechanics to "Conway's Game of Life".

## Phenomenon
War is, without doubt, evil. It brings catastrophic death and destruction to those involved in its horrific path. However, it can also be very interesting to analyze the way in which armies move during times of war. Strategists are essential to winning a war since a wrongly placed army can cost a major battle. For example, chess is a game that is entirely based around the premise of strategy in war. In chess, there are over 288 billion different possible positions after only four moves! Similarly, in war there are an infinite amount of possibilities that a strategist must try to predict and plan for. 

For my project, I decided to simulate war through the use of an interactive grid. My project aims to provide an engaging experience for the user while being a realistic simulation of war. My game is interactive so the user and simulate putting down armies into strategic places. However, it is best to be wary of putting down armies since the computer will replicate the user’s move in another spot. This simulates war in the sense that the enemy will react to one’s move and fight back. I have also incorporated “base” cells which are essentially specialized cells that one must protect, since if all base cells are lost on either side of the battle, then the respective player loses. Base cells also allow friendly cells to spawn nearby without much help, if there are no enemy cells nearby. This simulates real army bases in which recruits are trained and sent out to war except when the base is under attack. 

## Possible states of any cell within the game
: Empty/dead cell
: User-controlled soldier
: Computer-controlled enemy
: User base cell (specialized function)
: Computer base cell (specialized function)


## Rules for the evolution of a cell
Note: Rules are listed in the priority order that they are executed in 
If the cell is a normal user/computer controlled cell, the number of friendly neighbours and enemy neighbours is calculated, then:
If there are only 0 to 2 friendly neighbours (inclusive) then the cell dies due to loneliness (soldiers need comrades to keep their morale high in times of war).
If there are more than 5 friendly neighbours then the cell dies since it is overcrowded (soldiers must have adequate living space in order to function well, especially in trenches).
If there are more than 2 enemy cells surrounding the cell, then it will die since it will be attacked and overwhelmed (it is almost impossible to win a 1 versus 3 fight or worse).
Otherwise, the cell will stay as is it is (stays alive).
If the cell is a user/computer controlled base cell, the number of enemy neighbours (normal cells) will be calculated, then:
If the base cell has 3 or more enemy neighbours then it will die (similar to a soldier, a base cannot survive a large attack).
Otherwise, the base cell will stay alive
If the cell is dead, then the number of user-controlled neighbours and computer-controlled neighbours is calculated, then:
If it has exactly 3 of either neighbour type, then it becomes that neighbour type (soldiers can train more soldiers on the field). Note that too many friendly neighbours (more than 3) cannot train a new recruit since they will argue amongst themselves on strategies and techniques that they teach to the new recruit, similar to the idiom: “too many cooks in the kitchen”.
If it has exactly 1 base cell in its immediate vicinity (the bounding 3x3 square with the cell as the center) and it does not have any enemies nearby then it becomes a normal cell (type is dependent on base type) e.g. a player base can spawn a player-controlled cell. This simulates how army bases can train soldiers and send them into battle. It also allows for the bases to be heavily guarded, which they are in times of real war. 
Otherwise, the cell will stay dead
If all base cells of any one player are dead then the other player will win e.g. if the computer’s base cells all die then the player/user will win. At this point, there will be no further generations calculated or displayed.



## Instructions
Note: Please enable sound on your machine to ensure that the background music works
Run the game (ArtOfWar) using NetBeans or another Java IDE (Integrated Development Environment).
Select a cell to surround with 4 player-controlled bases as instructed by the dialog. Another set of 4 cells will be randomly chosen by the computer as its bases. These cells will not be very close to the player’s base cells to ensure the game does not end instantly. 
Place 3x3 blocks of cells on the grid whenever desired. Beware that the computer will also place down a similar block in a random place when the player places a block. 
Use the “FPS” (Frame per second) slider to adjust the speed of the game. The generation number shows how far the game has progressed. 
Continue playing until the player or computer’s base cells all die. The person with base cells still alive wins the game, as shown in the ending dialog. 

## Sample generations
![Sample Generations](/Screenshots/generations.PNG?raw=true "Sample Generations")


* Cell 1: Since this cell has only 2 friendly neighbours it will die (too lonely)
* Cell 2: This cell has 3 friendly neighbours which means it will live (it only has 1 enemy neighbour which is not enough to kill this cell).
* Cell 3: This cell has 2 friendly neighbours meaning it will die (too lonely). It also has 1 enemy neighbour but that will not affect its next state.
* Cell 4: Since this cell is currently dead, it can be recruited into the player’s army (becomes a soldier) due to the fact that it has exactly 1 base in its immediate vicinity and no enemies surrounding it. It also has exactly 3 player-controlled cells around it which results in the same outcome. 
* Cell 5: This cell has 3 friendly neighbours which means it can live on to the next generation. It does have 2 enemy neighbours but only 3 or more enemies can overwhelm the cell. 
* Cell 6: This cell has 3 enemy neighbours (player-controlled) so it will die in the next generation. It has 1 friendly (computer-controlled) neighbour but that will not affect its next state.
* Cell 7: This cell is a user-controlled base cell. The only way it can die is if it has 3 or more enemy neighbours, which it does not have (currently has no enemy neighbours). This means it will continue living in the next generation.
* Cell 8: Since this cell is currently dead, it can only be recruited into an army if it has exactly 3 neighbours of any one army beside it. It only has 1 player-controlled neighbour and 2 computer-controlled neighbours, meaning that it will stay dead in the next generation. It has a player-controlled base directly next to it, however it is surrounded by 2 enemies so that means it cannot become a player-controlled cell (bases cannot train recruits while the recruit is being attacked).
* Cell 9: This cell has 1 friendly and 1 enemy neighbour. It does not have enough comrades to survive meaning that it will die in the next generation. 

## Screenshot of a live game

## Challenges Faced
Creating an interactive grid of JButtons was very hard to accomplish. Specifically, managing to detect when a JButton was pressed and acting upon it was very difficult. In the end, this was accomplished through a 2D array of JButtons with each JButton being assigned a MouseListener, whose actions were defined in the MouseHandler class, specifically in the mouseClicked method. This method allowed the program to see which button was clicked according to its x and y coordinates or row and column values.
Adding music to the game in the background was also quite tricky due to the way in which Java handles music. Initially, I tried adding an MP3 file to be the background music, however there was many problems related with the AudioInputStream. Surprisingly, when I switched to using a WAV file, the music worked perfectly. 
Adding images to each button as the background was tough in the sense that these images needed to be very low resolution (around 10x10 pixels). This meant that I had to find recognizable images of player sprites that were super small. In the end, I used the PyMaze, a game similar to PAC-Man, player sprites used to create the SJAM Computer Science Club project in 2015, in which I worked on the artificial intelligence of the enemies.
Adding base type cells was very difficult to add into the game without severely breaking the game mechanics. Initially, when I added base cells, the rules of the game essentially made them invulnerable causing the game to never end. After tinkering with the rules, I managed to allow for the base cells to be very tough to destroy but not impossible. 

## Credits
Player/User soldier sprites:
"Ninja [Animated]" by DezrasDragons on OpenGameArt.org - Public domain
http://opengameart.org/content/ninja-animated 

Enemy blob sprites:
"RPG Enemies updated" by Stephen Challener (Redshrike) on OpenGameArt.org - CC-BY 3.0/OGA-BY 3.0
http://opengameart.org/content/16x16-16x24-32x32-rpg-enemies-updated 

Background music:
“Epic Battle Music - Epic Legend (Auracle Music)” by Darko on YouTube.com
https://www.youtube.com/watch?v=Mw4O9pN-a1g 
