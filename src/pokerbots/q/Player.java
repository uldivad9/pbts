package pokerbots.q;

import java.io.*;

import pokerbots.q.*;

/**
 * Simple example pokerbot, written in Java.
 * 
 * This is an example of a bare bones, pokerbot. It only sets up the socket
 * necessary to connect with the engine and then always returns the same action.
 * It is meant as an example of how a pokerbot should communicate with the
 * engine.
 * 
 */
public class Player {
	
	private final PrintWriter outStream;
	private final BufferedReader inStream;

	public Player(PrintWriter output, BufferedReader input) {
		this.outStream = output;
		this.inStream = input;
	}
	
	public void run() {
		String input;
		int stackSize=200;
		int bb=2;
		int numHands;
		double timeBank=20.0;
		int[] stacks = new int[3]; //0 is own stack, 1 is the next player over
		int potsize=0;
		
		Hand hand;
		
		try {
			// Block until engine sends us a packet; read it into input.
			while ((input = inStream.readLine()) != null) {

				// Here is where you should implement code to parse the packets
				// from the engine and act on it.
				System.out.println(input);
				
				String[] tokens = input.split(" ");
				String packetType = tokens[0];
				if ("NEWGAME".compareToIgnoreCase(packetType) == 0) {
					//tokens[1-3] are player names
					stackSize = Integer.parseInt(tokens[4]);
					bb = Integer.parseInt(tokens[5]);
					numHands = Integer.parseInt(tokens[6]);
					timeBank = Double.parseDouble(tokens[7]);
				} else if ("NEWHAND".compareToIgnoreCase(packetType) == 0) {
					//tokens[1] is hand id
					int seatNo = Integer.parseInt(tokens[2]);
					hand = new Hand();
					hand.addCard(tokens[3]);
					hand.addCard(tokens[4]);
					System.out.println("STARTING HAND: " + hand);
					if (seatNo == 1) {
						stacks[0] = Integer.parseInt(tokens[5]);
						stacks[1] = Integer.parseInt(tokens[6]);
						stacks[2] = Integer.parseInt(tokens[7]);
					} else if (seatNo == 2) {
						stacks[0] = Integer.parseInt(tokens[6]);
						stacks[1] = Integer.parseInt(tokens[7]);
						stacks[2] = Integer.parseInt(tokens[5]);
					} else {
						stacks[0] = Integer.parseInt(tokens[7]);
						stacks[1] = Integer.parseInt(tokens[5]);
						stacks[2] = Integer.parseInt(tokens[6]);
					}
					//tokens[8,9,10] are player names
					//token[11] is the number of active players
					//token[12,13,14] are the booleans of whether players in seats 1,2,3 are active
					timeBank = Double.parseDouble(tokens[15]);
				
				} else if ("GETACTION".compareToIgnoreCase(packetType) == 0) {
					potsize = Integer.parseInt(tokens[1]);
					outStream.println("CHECK");
				} else if ("REQUESTKEYVALUES".compareToIgnoreCase(packetType) == 0) {
					// At the end, engine will allow bot to send key/value pairs to store.
					// FINISH indicates no more to store.
					outStream.println("FINISH");
				}
			}
		} catch (IOException e) {
			System.out.println("IOException: " + e.getMessage());
		}

		System.out.println("Gameover, engine disconnected");
		
		// Once the server disconnects from us, close our streams and sockets.
		try {
			outStream.close();
			inStream.close();
		} catch (IOException e) {
			System.out.println("Encountered problem shutting down connections");
			e.printStackTrace();
		}
	}
	
}


