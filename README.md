# CrazyHorseRacing

This is an online multiplayer game inspired by Twitter "Horse Race Tests". The goal of the game is for your horse to get to the carrots first.
Each horse starts with a random velocity, but the players can accelerate their horse in cardinal directions using arrow keys.

## How to start

If the connection to the database fails, the server will not start.
For the connection to succeed, it's necessary to set the environment variable DB_PASSWORD equal to the password of the database.
Also, your computer needs to be able to reach the internal network of the THB, where the database is located.

If the server has successfully started, then it will be available under http://localhost:8080.

### Technical details
The project itself consists of three main parts: The website (the Java code), the database, and the client browsers.

The website simulates the entire game, serves the webpages to the clients and constantly informs them about the current state of their respective games. It's the central part of the whole experience.
The database stores players and their accounts long-term, allowing them to come back to their accounts even if the website restarts.
The client browsers display the game and send moves to the server when the player presses arrow keys.

### Motivation
There have been a few implementations of this Twitter trend before, but all of them have been simple singleplayer simulators. This is the first implementation to allow real multiplayer play.
