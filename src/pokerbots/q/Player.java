package pokerbots.q;

import java.io.*;
import java.util.*;

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
	
	final boolean debug=false;
	//whether to print out debug output
	
	public Player(PrintWriter output, BufferedReader input) {
		this.outStream = output;
		this.inStream = input;
	}
	
	public void run() {
		String input;
		int seatNo = 0;
		int stackSize=200;
		int bb=2;
		int numHands;
		double timeBank=20.0;
		int[] stacks = new int[3]; //0 is own stack, 1 is the next player over
		int potsize=0;
		
		Hand hand = null;
		
		try {
			// Block until engine sends us a packet; read it into input.
			while ((input = inStream.readLine()) != null) {

				// Here is where you should implement code to parse the packets
				// from the engine and act on it.
				if(debug) System.out.println(input);
				
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
					seatNo = Integer.parseInt(tokens[2]);
					hand = new Hand();
					hand.addCard(tokens[3]);
					hand.addCard(tokens[4]);
					
					stacks[0] = Integer.parseInt(tokens[5]);
					stacks[1] = Integer.parseInt(tokens[6]);
					stacks[2] = Integer.parseInt(tokens[7]);
					//tokens[8,9,10] are player names
					//token[11] is the number of active players
					//token[12,13,14] are the booleans of whether players in seats 1,2,3 are active
					timeBank = Double.parseDouble(tokens[15]);
				
				} else if ("GETACTION".compareToIgnoreCase(packetType) == 0) {
					potsize = Integer.parseInt(tokens[1]);
					int numBoardCards = Integer.parseInt(tokens[2]);
					for (int i=hand.size(); i<numBoardCards+2; i++) {
						hand.addCard(tokens[hand.size()+1]);
					}
					
					int base1 = 3+numBoardCards;
					//points to the first token after the board cards, which should be the stack sizes.
					stacks[0] = Integer.parseInt(tokens[base1]);
					stacks[1] = Integer.parseInt(tokens[base1+1]);
					stacks[2] = Integer.parseInt(tokens[base1+2]);
					
					//tokens[base1+3] is the number of active players
					//tokens[base1+4-6] is the booleans of whether players are active
					
					int numLastActions = Integer.parseInt(tokens[base1+7]);
					int base2 = base1+8;
					//points to the first token after numLastActions, which should be the first last action.
					//do stuff with the actions
					
					int numLegalActions = Integer.parseInt(tokens[base2+numLastActions]);
					int base3 = numLegalActions+1;
					//points to the first token after numLegalActions, which should be the first legal action.
					boolean canCheck = true; //whether there is a current bet
					//to be used if checking is allowed
					int minBet=0;
					int maxBet=0;
					//to be used if checking is disallowed
					int toCall=0;
					int minRaise=0;
					int maxRaise=0;
					
					for (int i=base3; i<base3+numLegalActions; i++) {
						String legalAction = tokens[i];
						String[] actionTokens = legalAction.split(":");
						if ("BET".equalsIgnoreCase(actionTokens[0])) {
							minBet = Integer.parseInt(actionTokens[1]);
							maxBet = Integer.parseInt(actionTokens[2]);
						} else if ("CALL".equalsIgnoreCase(actionTokens[0])) {
							canCheck = false;
							
						} else if ("RAISE".equalsIgnoreCase(actionTokens[0])) {
							canCheck = false;
							minRaise = Integer.parseInt(actionTokens[1]);
							maxRaise = Integer.parseInt(actionTokens[2]);
						}
					}
					
					String output;
					
					//status report
					
					
					if (hand.size() >= 5) {
					
						if (canCheck) {
							if (hand.evaluateHand() > 21200000) {
								output = "BET:"+maxBet;
							} else {
								output = "FOLD";
							}
						} else {
							//can't check :(
							if (hand.evaluateHand() > 30000000) {
								//raise
								output = "RAISE:"+maxRaise;
							} else if (hand.evaluateHand() > 21200000) {
								output = "CALL:"+toCall;
							} else {
								output = "FOLD";
							}
						}
					} else {
						if (canCheck) {
							output = "CHECK";
						} else {
							output = "CALL:"+toCall;
						}
					}
					
					if (!debug) {
						System.out.println("My hand: "+hand.toString());
						System.out.println("Stacks: "+Arrays.toString(stacks)+" (I have "+stacks[seatNo-1]+")");
						System.out.println("My action is: "+output);
					}
					
					outStream.println(output);
					
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


