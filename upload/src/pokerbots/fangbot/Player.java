package pokerbots.fangbot;

import java.io.*;
import java.util.*;

import pokerbots.*;

/**
 * YUN FANG
 * 
 */
public class Player {
	
	private String MY_NAME;
	
	private final PrintWriter outStream;
	private final BufferedReader inStream;
	private final Random rand = new Random();
	
	PlayerProfile myProfile;
	PlayerProfile oppProfile1;
	PlayerProfile oppProfile2;
	
	final boolean debug=false;
	//whether to print out debug output
	
	public Player(PrintWriter output, BufferedReader input) {
		this.outStream = output;
		this.inStream = input;
	}
	
	public void run() {
		String input;
		String[] playerNames = new String[3];
		int seatNo = 0;
		int stackSize=200;
		int bb=2;
		int numHands;
		double timeBank=20.0;
		int[] stacks = new int[3]; //0 is own stack, 1 is the next player over
		int myStack = 200;
		int potsize=0;
		ArrayList<String> activePlayers = new ArrayList<String>();
		
		int numActivePlayers = 0;
		int numRaises=0; //number of raises during this STREET
		int numFolds=0; //number of folds during this hand
		boolean button=false; //am i the button
		
		HashMap<String,Integer> playerRaises = new HashMap<String,Integer>();
		ArrayList<String> actionList = new ArrayList<String>();
		
		Hand hand = null;
		Hand board = null;
		Hand combined = null;
		
		int wins = 0;
		boolean reset = false; //reset all poor performing bots to given specs
		
		double pfraise = 0.456545447759851;
		double pfbet = 0.32555048286957056;
		double pfcall = 0.4785323492092885;
		double afraise = 0.9386514688178289;
		double afbet = 0.6888379785584023;
		double afcall = 0.7163182823913401;
		
		try {
			// Block until engine sends us a packet; read it into input.
			while ((input = inStream.readLine()) != null) {

				// Here is where you should implement code to parse the packets
				// from the engine and act on it.
				if (debug) System.out.println("");
				if (debug) System.out.println(input);
				
				String[] tokens = input.split(" ");
				String packetType = tokens[0];
				if ("NEWGAME".compareToIgnoreCase(packetType) == 0) {
					MY_NAME = tokens[1];
					
					
					//tokens[1-3] are player names
					for (int i=1; i<=3; i++) {
						playerRaises.put(tokens[i],0);
					}
					
					stackSize = Integer.parseInt(tokens[4]);
					bb = Integer.parseInt(tokens[5]);
					numHands = Integer.parseInt(tokens[6]);
					timeBank = Double.parseDouble(tokens[7]);
					
				} else if ("NEWHAND".compareToIgnoreCase(packetType) == 0) {
					actionList = new ArrayList<String>();
					
					//tokens[1] is hand id
					seatNo = Integer.parseInt(tokens[2]);
					hand = new Hand();
					board = new Hand();
					combined = new Hand();
					hand.addCard(tokens[3]);
					hand.addCard(tokens[4]);
					
					numRaises = 0;
					numFolds = 0;
					
					combined.addCard(tokens[3]);
					combined.addCard(tokens[4]);
					
					stacks[0] = Integer.parseInt(tokens[5]);
					stacks[1] = Integer.parseInt(tokens[6]);
					stacks[2] = Integer.parseInt(tokens[7]);
					//tokens[8,9,10] are player names
					for (int i=0; i<3; i++) {
						playerNames[i] = tokens[i+8];
					}
					
					if (tokens[8].equals(MY_NAME)) {
						button = true;
					} else {
						button = false;
					}
					//tokens[11] is the number of active players
					//tokens[12,13,14] are the booleans of whether players in seats 1,2,3 are active
					numActivePlayers = Integer.parseInt(tokens[11]);
					activePlayers = new ArrayList<String>();
					for (int i=0; i<numActivePlayers; i++) {
						if(tokens[12+i].equals("true")) {
							activePlayers.add(playerNames[i]);
						}
					}
					
					timeBank = Double.parseDouble(tokens[15]);
					
					playerRaises = new HashMap<String,Integer>();
					
					if (debug) {
						System.out.println("");
						System.out.println("-------------------------------------------------");
						System.out.println("");
						System.out.println("Hand number: "+tokens[1]);
					}
					
				} else if ("GETACTION".compareToIgnoreCase(packetType) == 0) {
					potsize = Integer.parseInt(tokens[1]);
					int numBoardCards = Integer.parseInt(tokens[2]);
					for (int i=board.size(); i<numBoardCards; i++) {
						Card newCard = new Card(tokens[board.size()+3]);
						board.addCard(newCard);
						combined.addCard(newCard);
					}
					
					int base1 = 3+numBoardCards;
					//points to the first token after the board cards, which should be the stack sizes.
					stacks[0] = Integer.parseInt(tokens[base1]);
					stacks[1] = Integer.parseInt(tokens[base1+1]);
					stacks[2] = Integer.parseInt(tokens[base1+2]);
					
					//tokens[base1+3] is the number of active players
					/*
					int numActivePlayers = Integer.parseInt(tokens[base1+3]);
					activePlayers = new ArrayList<String>();
					for (int i=0; i<numActivePlayers; i++) {
						if(tokens[base1+4+i].equals("true")) {
							activePlayers.add(playerNames[i]);
						}
					}*/
					//tokens[base1+4-6] is the booleans of whether players are active
					
					int numLastActions = Integer.parseInt(tokens[base1+7]);
					int base2 = base1+8;
					//points to the first token after numLastActions, which should be the first last action.
					//do stuff with the actions
					for (int i=0; i<numLastActions; i++) {
						String[] actionTokens = tokens[base2+i].split(":");
						if ("FOLD".equals(actionTokens[0])) {
							numFolds++;
							numActivePlayers--;
							activePlayers.remove(actionTokens[1]);
						} else if ("RAISE".equals(actionTokens[0])) {
							numRaises++;
							String playerName = actionTokens[2];
							if (playerRaises.containsKey(playerName)) {
								playerRaises.put(playerName,playerRaises.get(playerName)+1);
							} else {
								playerRaises.put(playerName,1);
							}
						} else if ("DEAL".equals(actionTokens[0])) {
							numRaises = 0;
						}
						actionList.add(tokens[base2+i]);
					}
					
					int numLegalActions = Integer.parseInt(tokens[base2+numLastActions]);
					int base3 = base2+numLastActions+1;
					//points to the first token after numLegalActions, which should be the first legal action.
					boolean canCheck = false; //whether there is a current bet
					//to be used if checking is allowed
					int minBet=0;
					int maxBet=0;
					//to be used if checking is disallowed
					int toCall=0;
					int minRaise=0;
					int maxRaise=0;
					
					//estimations
					
					
					for (int i=base3; i<base3+numLegalActions; i++) {
						String legalAction = tokens[i];
						String[] actionTokens = legalAction.split(":");
						if ("BET".equalsIgnoreCase(actionTokens[0])) {
							minBet = Integer.parseInt(actionTokens[1]);
							maxBet = Integer.parseInt(actionTokens[2]);
						} else if ("CALL".equalsIgnoreCase(actionTokens[0])) {
							toCall = Integer.parseInt(actionTokens[1]);
						} else if ("RAISE".equalsIgnoreCase(actionTokens[0])) {
							minRaise = Integer.parseInt(actionTokens[1]);
							maxRaise = Integer.parseInt(actionTokens[2]);
						} else if ("CHECK".equalsIgnoreCase(actionTokens[0])) {
							canCheck = true;
						}
					}
					
					if (debug) {
						System.out.println("Players: "+numActivePlayers);
						System.out.println("Raises: "+numRaises);
						System.out.println("Time left: "+timeBank);
						System.out.println("My hand: "+hand.toString());
						System.out.println("Board: "+board.toString());
						System.out.println("Combined:"+combined.toString());
						System.out.println("Stacks: "+Arrays.toString(stacks)+" (I have "+stacks[seatNo-1]+")");
						System.out.println("Pot size: "+potsize);
						if (canCheck) {
							System.out.println("I can check.");
						} else {
							System.out.println("I cannot check.");
						}
						System.out.println("I can bet "+minBet+" to "+maxBet);
						System.out.println("I can call with "+toCall);
						System.out.println("I can raise from "+minRaise+" to "+maxRaise);
					}
					
					String output;
					
					if (combined.size() == 2) {
						double strength = StartingHands.getStrength(hand);
						if (canCheck) {
							if (strength > pfbet && maxRaise > 0) {
								output = "RAISE:"+maxRaise;
							} else {
								output = "CHECK";
							}
						} else {
							if (strength > pfraise && maxRaise > 0 && numRaises < 2) {
								output = "RAISE:"+maxRaise;
							} else if (strength > pfcall) {
								output = "CALL:"+toCall;
							} else {
								output = "FOLD";
							}
						}
					} else {
						double relativeStrength = AfterFlop.getRelativeStrength(hand,board);
						if (canCheck) {
							if (relativeStrength > afbet && maxBet > 0) {
								output = "BET:"+maxBet;
							} else {
								output = "CHECK";
							}
						} else {
							if (relativeStrength > afraise && maxRaise > 0 && numRaises < 2) {
								output = "RAISE:"+maxRaise;
							} else if (relativeStrength > afcall) {
								output = "CALL:"+toCall;
							} else {
								output = "FOLD";
							}
						}
					}
					
					outStream.println(output);
				
				} else if ("HANDOVER".compareToIgnoreCase(packetType) == 0) {
					//tokens[1-3] are stack sizes
					stacks[0] = Integer.parseInt(tokens[1]);
					stacks[1] = Integer.parseInt(tokens[2]);
					stacks[2] = Integer.parseInt(tokens[3]);
					myStack = stacks[seatNo-1];
				} else if ("REQUESTKEYVALUES".compareToIgnoreCase(packetType) == 0) {
					// At the end, engine will allow bot to send key/value pairs to store.
					// FINISH indicates no more to store.
					try {
						System.out.println("PUT RESULTS "+myStack+" "+pfraise+" "+pfbet+" "+pfcall+" "+afraise+" "+afbet+" "+afcall);
						outStream.println("PUT RESULTS "+myStack+" "+pfraise+" "+pfbet+" "+pfcall+" "+afraise+" "+afbet+" "+afcall);
						System.out.println("PUT WINS "+myStack+" "+wins);
						outStream.println("PUT WINS "+myStack+" "+wins);
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println(e.getMessage());
					}
					//outStream.println("RESET");
					outStream.println("FINISH");
				} else if ("KEYVALUE".compareToIgnoreCase(packetType) == 0) {
					try {
						if (tokens[1].equals("RESULTS")) {
							if (Integer.parseInt(tokens[2]) > 9000000) {
								//stay
								pfraise = Double.parseDouble(tokens[3]);
								pfbet = Double.parseDouble(tokens[4]);
								pfcall  = Double.parseDouble(tokens[5]);
								afraise = Double.parseDouble(tokens[6]);
								afbet = Double.parseDouble(tokens[7]);
								afcall = Double.parseDouble(tokens[8]);
							} else if (reset) {
								//do nothing!
							} else {
								System.out.println("mutating");
								pfraise = Double.parseDouble(tokens[3])+rand.nextGaussian()/30;
								pfbet = Double.parseDouble(tokens[4])+rand.nextGaussian()/30;
								pfcall  = Double.parseDouble(tokens[5])+rand.nextGaussian()/30;
								afraise = Double.parseDouble(tokens[6])+rand.nextGaussian()/30;
								afbet = Double.parseDouble(tokens[7])+rand.nextGaussian()/30;
								afcall = Double.parseDouble(tokens[8])+rand.nextGaussian()/30;
							}
							
							System.out.println("pfraise: "+pfraise);
							System.out.println("pfbet:   "+pfbet);
							System.out.println("pfcall:  "+pfcall);
							System.out.println("afraise: "+afraise);
							System.out.println("afbet:   "+afbet);
							System.out.println("afcall:  "+afcall);
						} else if (tokens[1].equals("WINS")) {
							if (Integer.parseInt(tokens[2]) > 9000000) {
								wins = Integer.parseInt(tokens[3])+1;
							} else {
								wins = 0;
							}
							System.out.println("CONSECUTIVE WINS: "+wins);
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println(e.getMessage());
					}
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