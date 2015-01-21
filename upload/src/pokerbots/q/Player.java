package pokerbots.q;

import java.io.*;
import java.util.*;

import pokerbots.*;

/**
 * QBOT GG
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
	
	final boolean debug=true;
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
		int potsize=0;
		ArrayList<String> activePlayers = new ArrayList<String>();
		
		int numActivePlayers = 0;
		int numRaises=0; //number of raises during this STREET
		int numFolds=0; //number of folds during this hand
		boolean button=false; //am i the button
		boolean stole=false; //if i tried a steal this round
		int failedSteals = 0; //number of steals that failed (opp didn't fold)
		boolean countedSteal = false; //have i counted this steal in failedsteals yet
		
		HashMap<String, Integer> failedStealMap = new HashMap<String, Integer>();
		HashMap<String, Integer> attemptedStealMap = new HashMap<String, Integer>();
		HashMap<String, PlayerProfile> profileMap = new HashMap<String, PlayerProfile>();
		HashMap<String, Integer> playerRaises = new HashMap<String, Integer>();
		
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
					MY_NAME = tokens[1];
					
					
					//tokens[1-3] are player names
					for (int i=1; i<=3; i++) {
						if (!failedStealMap.containsKey(tokens[i])) {
							failedStealMap.put(tokens[i],0);
						}
						
						if (!attemptedStealMap.containsKey(tokens[i])) {
							attemptedStealMap.put(tokens[i],2);
						}
						
						if (!profileMap.containsKey(tokens[i])) {
							profileMap.put(tokens[i],new PlayerProfile(tokens[i]));
						}
						
						playerRaises.put(tokens[i],0);
					}
					
					stackSize = Integer.parseInt(tokens[4]);
					bb = Integer.parseInt(tokens[5]);
					numHands = Integer.parseInt(tokens[6]);
					timeBank = Double.parseDouble(tokens[7]);
					
					button = false;
					stole = false;
					failedSteals = 0;
					countedSteal = false;
					
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
					
					stole = false;
					countedSteal = false;
					
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
					
					ArrayList<Double> estimatedStrengths = new ArrayList<Double>();
					for (String playerName : activePlayers) {
						if (!playerName.equals(MY_NAME)) {
							double estimatedstr = profileMap.get(playerName).estimateStrength(actionList.toArray(new String[actionList.size()]),board.size());
							estimatedStrengths.add(estimatedstr);
						}
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
						System.out.println("Actions: "+actionList);
						System.out.println("Active players: "+activePlayers);
						System.out.println("Players: "+numActivePlayers);
						System.out.println("Estimated strengths: "+estimatedStrengths);
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
					} else {
						System.out.println("My hand is: "+hand);
						System.out.println("Board: "+board.toString());
					}
					
					String output;
					
					if (combined.size() == 2) {
					//evaluate starting hand
						double strength = StartingHands.getStrength(hand);
						double variance = rand.nextGaussian()*(1-strength)/30;
						double estr = 0.0;
						for (double d : estimatedStrengths) {
							estr = Math.max(estr,d);
						}
						if (debug) {
							System.out.println("My starting hand strength is "+strength);
							System.out.println("This round's variance is "+variance);
						}
						
						if (canCheck) {
							if (strength > (estr+0.45)/2) {
								//premium hand
								if (maxBet > 0) {
									output = "BET:"+maxBet;
								} else if (maxRaise > 0) {
									output = "RAISE:"+maxRaise;
								} else {
									output = "CHECK";
								}
							} else if (strength > (estr+0.4)/2) {
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
							if (strength > 0.63 && maxRaise > 0) {
								//raise that ho
								output = "RAISE:"+maxRaise;
							}
							else if (strength > (estr+0.48)/2 && maxRaise > 0 && numRaises < 2) {
								//raise the roof
								output = "RAISE:"+maxRaise;
							} else if (strength > (estr+0.4)/2) {
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
						double estr = 0.0;
						for (double d : estimatedStrengths) {
							estr = Math.max(estr,d);
						}
						
						System.out.println("Relative strength: "+relativeStrength);
						System.out.println("This round's variance is: "+ variance);
						
						if (stole && !countedSteal) {
							for (String teamName : activePlayers) {
								failedStealMap.put(teamName,failedStealMap.get(teamName)+1);
							}
							System.out.println("Failed a steal during this hand");
							countedSteal = true;
						}
						
						if (canCheck) {
							if (maxBet == 0) {
								output = "CHECK";
							} else {
								if (estr > 0.5 && relativeStrength < estr) {
								//check if you think you're beat.
									System.out.println("Opponents probably beat me; checking");
									output = "CHECK";
								} else if (relativeStrength > (estr+0.9)/2 && relativeStrength > 0.85) {
									if (debug) System.out.println("Uber hand; betting max");
									int base = maxBet*9/10;
									int spread = Math.max(maxBet-base,1);
									int betAmount = Math.min(Math.max(base+rand.nextInt(spread),minBet),maxBet);
									output = "BET:"+betAmount;
								} else if (relativeStrength > (estr+0.75)/2 && relativeStrength > 0.72) {
									if (debug) System.out.println("Strong hand; betting high");
									int betAmount = Math.min(Math.max((int)Math.floor(maxBet*(relativeStrength+variance)),minBet),maxBet);
									output = "BET:"+betAmount;
								} else if (button && !stole && relativeStrength > (estr+0.5)/2 && estr<=0.6) {
									//make sure you haven't failed 3 steals on anyone
									boolean shouldSteal = true;
									for (String teamName : activePlayers) {
										double failrate = failedStealMap.get(teamName)/(double)attemptedStealMap.get(teamName);
										if (!teamName.equals(MY_NAME) && rand.nextDouble() < ((Math.atan(failrate*10-5))/(2*Math.atan(5))+0.5)) {
											shouldSteal = false;
											System.out.println("Failed too many steals to try again.");
										}
										if (playerRaises.getOrDefault(teamName,0) > 1) {
											shouldSteal = false;
											System.out.println("Too many raises to steal.");
										}
									}
									
									if (shouldSteal) {
									//try a steal
										if (debug) System.out.println("Decent hand with position; attempting steal");
										int betAmount = Math.min(Math.max((int)Math.floor(maxBet*(0.7+variance)),minBet),maxBet);
										stole = true;
										for (String teamName : activePlayers) {
											attemptedStealMap.put(teamName,attemptedStealMap.get(teamName)+1);
										}
										output = "BET:"+betAmount;
									} else {
										output = "CHECK";
									}
								} else if (button && !stole && estr<=0.6) {
									boolean shouldSteal = true;
									for (String teamName : activePlayers) {
										double failrate = failedStealMap.get(teamName)/(double)attemptedStealMap.get(teamName);
										if (!teamName.equals(MY_NAME) && rand.nextDouble() < ((Math.atan(failrate*10-5))/(2*Math.atan(5))+0.5)) {
											shouldSteal = false;
											System.out.println("Failed too many steals to try again.");
										}
										if (playerRaises.getOrDefault(teamName,0) > 0) {
											shouldSteal = false;
											System.out.println("Too many raises to steal.");
										}
									}
									
									if (shouldSteal) {
										if (debug) System.out.println("Weak hand with position; attempting steal");
										int betAmount = Math.min(Math.max((int)Math.floor(maxBet*(0.8+variance)),minBet),maxBet);
										stole = true;
										for (String teamName : activePlayers) {
											attemptedStealMap.put(teamName,attemptedStealMap.get(teamName)+1);
										}
										output = "BET:"+betAmount;
									} else {
										System.out.println("Decided not to steal");
										output = "CHECK";
									}
								} else {
									//do i want to blufferino?
									boolean bluff = false;
									if (estr < 0.6) {
										double foldchance = 1;
										
										for (String teamName : activePlayers) {
											if (!teamName.equals(MY_NAME)) {
												foldchance*=profileMap.get(teamName).getFoldRate(board.size());
												if (playerRaises.getOrDefault(teamName,0) > 0) {
													foldchance = 0;
													System.out.println("Not bluffing when opponent raised early");
												}
											}
										}
										
										if (foldchance > 0.6) {
											bluff = true;
											
											System.out.println("Attempting steal from early position");
										}
									}
									
									if (bluff) {
										int betAmount = Math.min(Math.max((int)Math.floor(maxBet*(0.8+variance)),minBet),maxBet);
										output = "BET:"+betAmount;
									} else {
										output = "CHECK";
									}
								}
							}
						} else if (numRaises == 0) {
							//can't check.
							if (relativeStrength < estr) {
								//fold if you think you're beat.
								System.out.println("Opponents probably beat me; folding");
								output = "FOLD";
							} else if (relativeStrength > (estr+0.95)/2 && relativeStrength > 0.92) {
								if (debug) System.out.println("Uber hand; raising max");
								if (maxRaise > 0) {
									int base = maxRaise*9/10;
									int spread = Math.max(maxRaise-base,1);
									int raiseAmount = Math.min(Math.max(base+rand.nextInt(spread),minRaise),maxRaise);
									output = "RAISE:"+raiseAmount;
								} else {
									output = "CALL:"+toCall;
								}
							} else if (relativeStrength > (estr+0.8)/2 && relativeStrength > 0.77) {
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
							} else if (relativeStrength > (estr+0.65)/2 && relativeStrength > 0.62) {
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
						} else {
							//someone has already raised
							if (relativeStrength > (estr+0.96)/2 && relativeStrength > 0.93) {
								if (debug) System.out.println("Uber hand; reraising max");
								if (maxRaise > 0) {
									int base = maxRaise*9/10;
									int spread = Math.max(maxRaise-base,1);
									int raiseAmount = Math.min(Math.max(base+rand.nextInt(spread),minRaise),maxRaise);
									output = "RAISE:"+raiseAmount;
								} else {
									output = "CALL:"+toCall;
								}
							} else if (relativeStrength > (estr+0.85)/2 && relativeStrength > 0.82) {
								output = "CALL:"+toCall;
							} else {
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
				
				} else if ("HANDOVER".compareToIgnoreCase(packetType) == 0) {
					//hand is over.
					//tokens[1-3] are stack sizes
					int numBoardCards = Integer.parseInt(tokens[4]);
					int base1 = 5+numBoardCards;
					//base1 is the index of numLastActions
					int numLastActions = Integer.parseInt(tokens[base1]);
					for (int i=0; i<numLastActions; i++) {
						actionList.add(tokens[base1+i+1]);
					}
					System.out.println("All hand actions: "+actionList);
					
					String[] actionArray = actionList.toArray(new String[actionList.size()]);
					
					for (String playername : activePlayers) {
						profileMap.get(playername).updateProfileAfterHand(actionArray);
					}
					
					if (tokens[1].equals("600") || tokens[2].equals("600") || tokens[3].equals("600")) {
						for (String playername : playerNames) {
							profileMap.get(playername).printInfo();
						}
						
						Set<String> profilekeys = profileMap.keySet();
						for (String key : profilekeys) {
							System.out.println(profileMap.get(key).toKeyValue());
							String keyInstruction = "PUT "+profileMap.get(key).name+" "+BytePacker.pack(profileMap.get(key).toKeyValue());
							System.out.println(keyInstruction);
						}
						
						Set<String> keys = failedStealMap.keySet();
						for (String key : keys) {
							String keyInstruction = "PUT F"+key+" "+failedStealMap.get(key);
							System.out.println(keyInstruction);
						}
						keys = attemptedStealMap.keySet();
						for (String key : keys) {
							String keyInstruction = "PUT A"+key+" "+attemptedStealMap.get(key);
							System.out.println(keyInstruction);
						}
					}
					
				} else if ("REQUESTKEYVALUES".compareToIgnoreCase(packetType) == 0) {
					// At the end, engine will allow bot to send key/value pairs to store.
					// FINISH indicates no more to store.
					try {
						for (String playername : playerNames) {
							profileMap.get(playername).printRatios();
						}
						
						Set<String> profilekeys = profileMap.keySet();
						for (String key : profilekeys) {
							System.out.println(profileMap.get(key).toKeyValue());
							String keyInstruction = "PUT "+profileMap.get(key).name+" "+BytePacker.pack(profileMap.get(key).toKeyValue());
							outStream.println(keyInstruction);
							System.out.println(keyInstruction);
						}
						
						Set<String> keys = failedStealMap.keySet();
						for (String key : keys) {
							String keyInstruction = "PUT F"+key+" "+failedStealMap.get(key);
							outStream.println(keyInstruction);
							System.out.println(keyInstruction);
						}
						keys = attemptedStealMap.keySet();
						for (String key : keys) {
							String keyInstruction = "PUT A"+key+" "+attemptedStealMap.get(key);
							outStream.println(keyInstruction);
							System.out.println(keyInstruction);
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println(e.getMessage());
					}
					outStream.println("RESET");
					outStream.println("FINISH");
				} else if ("KEYVALUE".compareToIgnoreCase(packetType) == 0) {
					try {
						if ("F".equals(tokens[1].substring(0,1))) {
							//deal with failedstealmap
							failedStealMap.put(tokens[1].substring(1,tokens[1].length()),Integer.parseInt(tokens[2]));
						} else if ("A".equals(tokens[1].substring(0,1))) {
							attemptedStealMap.put(tokens[1].substring(1,tokens[1].length()),Integer.parseInt(tokens[2]));
						} else if (profileMap.containsKey(tokens[1])) {
							String[] codedtokens = new String[tokens.length-2];
							for (int i=2; i<tokens.length; i++) {
								codedtokens[i-2] = tokens[i];
							}
							String keyValue = BytePacker.unpack(codedtokens);
							System.out.println(" unpacked: "+keyValue);
							profileMap.put(tokens[1],PlayerProfile.parseKeyValue(tokens[1],keyValue));
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