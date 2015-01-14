package pokerbots.q;

import java.io.*;
import java.util.*;

import pokerbots.*;

/**
 * QBOT GG
 * 
 */
public class Player {
	
	private final PrintWriter outStream;
	private final BufferedReader inStream;
	private final Random rand = new Random();
	
	final boolean debug=true;
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
		
		ArrayList<String> actionList = new ArrayList<String>();
		
		Hand hand = null;
		Hand board = null;
		Hand combined = null;
		
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
					//tokens[1-3] are player names
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
					
					combined.addCard(tokens[3]);
					combined.addCard(tokens[4]);
					
					stacks[0] = Integer.parseInt(tokens[5]);
					stacks[1] = Integer.parseInt(tokens[6]);
					stacks[2] = Integer.parseInt(tokens[7]);
					//tokens[8,9,10] are player names
					//tokens[11] is the number of active players
					//tokens[12,13,14] are the booleans of whether players in seats 1,2,3 are active
					timeBank = Double.parseDouble(tokens[15]);
					
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
					//tokens[base1+4-6] is the booleans of whether players are active
					
					int numLastActions = Integer.parseInt(tokens[base1+7]);
					int base2 = base1+8;
					//points to the first token after numLastActions, which should be the first last action.
					//do stuff with the action
					for (int i=0; i<numLastActions; i++) {
						actionList.add(tokens[base2+numLastActions]);
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
						System.out.println("Actions: "+actionList);
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
					} else {
						System.out.println("My hand is: "+hand);
						System.out.println("Board: "+board.toString());
					}
					
					String output;
					
					
					
					if (combined.size() == 2) {
					//evaluate starting hand
						double strength = StartingHands.getStrength(hand);
						double variance = rand.nextGaussian()*(1-strength)/30;
						if (debug) {
							System.out.println("My starting hand strength is "+strength);
							System.out.println("This round's variance is "+variance);
						}
						
						if (canCheck) {
							if (strength > 0.43+variance) {
								//premium hand
								if (maxBet > 0) {
									output = "BET:"+maxBet;
								} else if (maxRaise > 0) {
									output = "RAISE:"+maxRaise;
								} else {
									output = "CHECK";
								}
							} else if (strength > 0.38+variance) {
								//good hand
								if (maxBet > 0) {
									output = "BET:"+Math.max(minBet,maxBet/2);
								} else if (maxRaise > 0) {
									output = "RAISE:"+Math.max(minRaise,maxRaise/2);
								} else {
									output = "CHECK";
								}
							} else {
								output = "CHECK";
							}
						} else {
							if (strength > 0.47+variance && maxRaise > 0) {
								//raise that ho
								output = "RAISE:"+maxRaise;
							} else if (strength > 0.4+variance) {
								output = "CALL:"+toCall;
							} else {
								output = "FOLD";
							}
						}
						//end of pre-flop strategy
					} else {
						//flop is out.
						int handStrength = combined.evaluateHand();
						double relativeStrength = AfterFlop.getRelativeStrength(hand,board);
						double variance = rand.nextGaussian()*(1-relativeStrength)/20;
						System.out.println("My hand strength: "+handStrength);
						System.out.println("Relative strength: "+relativeStrength);
						System.out.println("This round's variance is: "+ variance);
						
						if (canCheck) {
							if (maxBet == 0) {
								output = "CHECK";
							} else {
								if (relativeStrength > 0.9) {
									if (debug) System.out.println("Uber hand; betting max");
									int base = maxBet*9/10;
									int spread = Math.max(maxBet-base,1);
									int betAmount = Math.min(Math.max(base+rand.nextInt(spread),minBet),maxBet);
									output = "BET:"+betAmount;
								} else if (relativeStrength > 0.7+variance) {
									if (debug) System.out.println("Strong hand; betting high");
									int betAmount = Math.min(Math.max((int)Math.floor(maxBet*(relativeStrength+variance)),minBet),maxBet);
									output = "BET:"+betAmount;
								} else {
									output = "CHECK";
								}
							}
						} else {
							//can't check.
							if (relativeStrength > 0.95) {
								if (debug) System.out.println("Uber hand; raising max");
								if (maxRaise > 0) {
									int base = maxRaise*9/10;
									int spread = Math.max(maxRaise-base,1);
									int raiseAmount = Math.min(Math.max(base+rand.nextInt(spread),minRaise),maxRaise);
									output = "RAISE:"+raiseAmount;
								} else {
									output = "CALL:"+toCall;
								}
							} else if (relativeStrength > 0.8+variance) {
								//consider raising.
								int raiseAmount = (int)(potsize*(relativeStrength-variance)/1.5);
								if (raiseAmount > minRaise && maxRaise > 0) {
									if (debug) System.out.println("Strong hand; raising high");
									output = "RAISE:"+Math.min(raiseAmount,maxRaise);
								} else {
									int callAmount = (int)(potsize*(relativeStrength-variance));
									if (toCall < callAmount) {
										if (debug) System.out.println("Strong hand; calling");
										output = "CALL:"+toCall;
									} else {
										if (debug) System.out.println("Strong hand; folding");
										output = "FOLD";
									}
								}
							} else if (relativeStrength > 0.65+variance) {
								//call for small bets.
								int callAmount = (int)(potsize*(relativeStrength-variance)/1.5);
								if (toCall < callAmount) {
									if (debug) System.out.println("Decent hand; calling");
									output = "CALL:"+toCall;
								} else {
									if (debug) System.out.println("Decent hand; folding");
									output = "FOLD";
								}
							} else {
								if (debug) System.out.println("Weak hand; folding");
								output = "FOLD";
							}
						}
						
						if ("FOLD".equals(output) && combined.size() < 7) {
							//check outs!
							//arbitrary threshold.... 0.8!
							double[] outStrength = Outs.getOuts(hand,board);
							if(debug) System.out.println("My chances for outs are: " + Arrays.toString(outStrength));
							
							if (toCall <= outStrength[2]*potsize*0.8 || toCall <= outStrength[3]*potsize*0.9 || toCall <= outStrength[4]*potsize) {
								//call dis bitch
								System.out.println("Drawing hand; calling");
								output = "CALL:"+toCall;
							}
						}
						//end of after-flop strategy
					}
					//end of strategy
					
					
					if (debug) {
						System.out.println(toCall + " to call");
						System.out.println("My action is: "+output);
					}
					
					outStream.println(output);
					
				} else if ("REQUESTKEYVALUES".compareToIgnoreCase(packetType) == 0) {
					// At the end, engine will allow bot to send key/value pairs to store.
					// FINISH indicates no more to store.
					outStream.println("CLEAR");
					outStream.println("FINISH");
				} else if ("KEYVALUE".compareToIgnoreCase(packetType) == 0) {
					System.out.println("I GOT A KEY VALUE!");
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


