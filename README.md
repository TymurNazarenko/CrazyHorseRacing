## CrazyHorseRacing

An online multiplayer game inspired by Twitter "Horse Race Tests". The goal of the game is to get to the carrots first.

Each horse starts with a random velocity, but the players can accelerate their horse in cardinal directions using arrow keys.

To start the website, it's necessary to set the environment variable DB_PASSWORD to the password of the database. Also, it needs to be within the internal network of the THB, otherwise the connection to the DB will fail and the website will crash.



# TODOs:
- "Waiting for players..." text
- Instructions for the players that you can move with the arrow keys
- Change the button on the main menu to say "Join your existing game" or something if the player already has a game
- Ability to leave your game if the game is still in the "Waiting for players" phase
- Fix the fact that the database allows null entries for players

# Optional TODOs:
- Make the strength of player moves variable and based on the level
