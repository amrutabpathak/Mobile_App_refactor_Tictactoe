Refactoring changes :


1. Externalizing strings, colors, resources, etc. into resource files so they are easily modifiable. 
Used strings , colors and styles xml generously to shift all the strings , and colors from the activity.xml into their respective resource files. This ensures ease to modify the values instantly without touching the actual UI xmls, reusability, and clean code.

2. Move game logic out of the TicTacToe activity.
Used MVC architecture to ensure seggragation between the business logic and the UI compoenents.
The code was divided as follows :
View :
activity_main.xml 
board_fragment.xml
status_fragment.xml
These files only deal with the UI components.

Controller:
MainActivityController - that talks to the view and the model.
BoardFragment and StatusFragment (These too are controllers that talk to Main controller and model.) More about fragments in the next point.

Model:
TGameModel
This holds the entire gaming logic of the application. This will also talk to the database layer. 

Beans:
The following two beans flow between the above components to ensure the flow of data between them.
Game and Player:
Game bean holds the current state of the game. ie which cell was clicked, is the game over, players etc.
Game has Player 1 and 2 beans too. We need them to hold the current state of both the players.

Player:
Player has all the information about the player. ie player id , their score.

3.Contain the layout components into Fragments. 
Tictactoe is ddivided into two fragments  
BoardFragment - that has all the board related code.
StatusFragment - that has the reset button and the players scores, messages etc.

UI is divided into -(view)
board_fragment.xml
status_fragment.xml
It contains actual UI components 

BoardFragment and StatusFragment files that act as controllers are responsible to communicate with the model class.
They talk to each other through the MainActivityController.
MainActivityController is like a manager that manages these two fragments.

4. Provide multiple layouts for different screen orientations, sizes, resolutions, etc. 


5. Define different resources (images, sizing, layouts, etc) for different screen orientations, sizes, resolutions, etc. 


6. Internationalize the game, with an ability to play with alternative languages. 
Internationalized for two mpre languages - Spanish and Hindi.
Resources in res values_ folders.  

7. Use images instead of text on the display. 
Used Fall pumpkin images instead of X and O's since it is beautiful Fall :)
available in the drawable folder. 

8. Apply Material Design to the interface/interaction. 


9. Create custom components (for example, the buttons could be custom buttons) that have some state and/or unique behavior. 
created custom button for reset inspired by Fall colors.
available in the drawable folder - customButton

10.Tweak the game flow or aspects of the user experience to make it a game you really are excited about! 
The methods were seggreagted for reusabilty.
The app is made as loosely coupled as possible.
variable names and methods have been named to understand their usage quickly.
The code is organised in relevant packages.
Essentially , all the classes have been written such that they can be easily extended and reused.














 