Lucas Jackson : backend, message passing, train, locking,
Tony Nguyen : GUI, config file, building track system,
We both agreed on class hierarchies track / message.

Configuration file:
The track system and trains are built based on symbols that represent specific tracks.
'O' : station (big O not zero) , '-' : train track, 'T' : station with train on top, 
'*' : lights , digits are used to create switches. Also, a pair of switches must have a
unique type of digit to relate to it counterpart switch.
O--1-O
O-1--O

It's important to note that configuration file must strictly follows the above form, where
another level track system must be on another line. There may not be an empty line in between
levels.

Only 1 level of track is functional, switches are displayed on the GUI but may
not reach a destination path that includes a switch, also the train will only
travel from one station and then stop.

Version 1 : NOT FUNCTIONAL BECAUSE OF NOT USING CLASS LOADER FOR TEXT FILE.
How to play: starts the program when jar file is run, Version 1 creates a single level track with
	     stations on the ends of interconnecting tracks. The idea is the following, 
	     1) Build tracks, trains, and stations that are contained within threads.
	     2) Create a train which will request a possible route by finding a target station.
	     3) Once a route is found, train will request a lock onto the path it has found.
	     4) Once the route is locked the train will begin moving until the target station is 
		reached.
note : the state of the game will be displayed through the GUI, there is no need for user interaction
       besides starting the program.
Entry Point: TrainController
Known bugs:
       version 1 is not able to display the lights as well as function with them, if a route is not
       found then the program will become idle.
Version 2
How to play: first click a station that is the desired origin station, secondly
        click a destination station for the train to reach. Once two stations are
        clicked a train will spawn and begin requesting a route to the destination
        station.



