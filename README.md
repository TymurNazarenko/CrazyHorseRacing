## CrazyHorseRacing

An online multiplayer game inspired by Twitter "Horse Race Tests". The goal of the game is to get to the carrots first.

Each horse starts with a random velocity, but the players can accelerate their horse in cardinal directions using arrow keys.



# TODOs:
- Stop using JSESSIONID completely and switch to something else, otherwise the entire login/cookie persistence thing breaks
- Fix addWin and addGame not saving to the DB
- Polish the levels and horses, and create more of them
- Make it so that the power of the initial velocity of horses is not a constant, but is a variable controlled by the level
- Fix the fact that the database allows null entries for players

# Optional TODOs:
- Make the strength of player moves depend variable and based on the level