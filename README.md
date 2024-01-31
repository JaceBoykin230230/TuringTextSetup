This is a Java project based on Alan Turing's 1950 paper Computer Machinery and Intelligence.

The goal is to recreate the 'Imitation Game' as described by Turing in the introduction of this paper.

The project includes multiple components: 

- Twilio API is used for text messaging functionality, as texting will be the method in which the game is played. 
- The OpenAI API using 'gpt-4' will serve as the machine in the Imitation Game.
- Prompt engineering techniques used to tune the gpt model for the purpose of the game.
- The program will live on a HTTP server, and user texts are handled by the program when they text the phone number provided.
- The majority of the code is written to manage game sessions and queuing, and then the logic of each game session itself (communication between players, and determining the winning condition of the game).

  
