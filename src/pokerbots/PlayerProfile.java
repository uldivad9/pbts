package pokerbots;

import java.util.*;
import pokerbots.*;

public class PlayerProfile {
	public String name;
	public int preFlopCheckObs;
	public int preFlopCallObs;
	public int flopCheckObs;
	public int flopCallObs;
	public int turnCheckObs;
	public int turnCallObs;
	public int riverCheckObs;
	public int riverCallObs;
	
	//percentage of x during y means the probability that the player will do x if in y.
	int foldPreFlop; //number of folds preflop
	int foldFlop; //number of folds during flop
	int foldTurn; //number of folds during turn
	int foldRiver; //number of folds during river
	int raisePreFlop; //number of raises preflop
	int raiseFlop; //number of raises during flop
	int raiseTurn; //number of raises during turn
	int raiseRiver; //number of raises during river
	int betPreFlop; //number of raises instead of checking preflop
	int betFlop; //number of bets during flop
	int betTurn; //number of bets during turn
	int betRiver; //number of bets during river
	int winPreFlop; //number of hands won by folds preflop
	int winFlop; //number of hands won by folds during flop
	int winTurn; //number of hands won by folds during turn
	int winRiver; //number of hands won by folds during river
	
	int showdown; //number of hands gone to showdown
	int showdownWin; //number of showdowns won
	
	ArrayList<Double> startingHandStrengths;
	public SortedList preFlopBets;
	public SortedList flopBets;
	public SortedList turnBets;
	public SortedList riverBets;
	public SortedList preFlopRaises;
	public SortedList flopRaises;
	public SortedList turnRaises;
	public SortedList riverRaises;

	public static PlayerProfile parseKeyValue(String name, String kv) {
		String[] tokens = kv.split(" ");
		PlayerProfile out = new PlayerProfile(name);
		out.preFlopCheckObs = Integer.parseInt(tokens[0]);
		out.preFlopCallObs = Integer.parseInt(tokens[1]);
		out.foldPreFlop = Integer.parseInt(tokens[2]);
		out.betPreFlop = Integer.parseInt(tokens[3]);
		out.raisePreFlop = Integer.parseInt(tokens[4]);
		//out.winPreFlop = Integer.parseInt(tokens[5]);
		out.flopCheckObs = Integer.parseInt(tokens[5]);
		out.flopCallObs = Integer.parseInt(tokens[6]);
		out.foldFlop = Integer.parseInt(tokens[7]);
		out.betFlop = Integer.parseInt(tokens[8]);
		out.raiseFlop = Integer.parseInt(tokens[9]);
		//out.winFlop = Integer.parseInt(tokens[11]);
		out.turnCheckObs = Integer.parseInt(tokens[10]);
		out.turnCallObs = Integer.parseInt(tokens[11]);
		out.foldTurn = Integer.parseInt(tokens[12]);
		out.betTurn = Integer.parseInt(tokens[13]);
		out.raiseTurn = Integer.parseInt(tokens[14]);
		//out.winTurn = Integer.parseInt(tokens[17]);
		out.riverCheckObs = Integer.parseInt(tokens[15]);
		out.riverCallObs = Integer.parseInt(tokens[16]);
		out.foldRiver = Integer.parseInt(tokens[17]);
		out.betRiver = Integer.parseInt(tokens[18]);
		out.raiseRiver = Integer.parseInt(tokens[19]);
		//out.winRiver= Integer.parseInt(tokens[23]);
		//out.showdown = Integer.parseInt(tokens[24]);
		//out.showdownWin = Integer.parseInt(tokens[25]);
		return out;
	}
	
	public PlayerProfile(String newName) {
		name = newName;
		preFlopCheckObs = 10;
		preFlopCallObs = 20;
		foldPreFlop = 12;
		betPreFlop = 1;
		raisePreFlop = 2;
		winPreFlop = 10;
		flopCheckObs = 10;
		flopCallObs = 8;
		foldFlop = 4;
		betFlop = 5;
		raiseFlop = 1;
		winFlop = 6;
		turnCheckObs = 6;
		turnCallObs = 5;
		foldTurn = 3;
		betTurn = 2;
		raiseTurn = 1;
		winTurn = 3;
		riverCheckObs = 6;
		riverCallObs = 5;
		foldRiver = 3;
		betRiver = 2;
		raiseRiver = 1;
		winRiver= 3;
		showdown = 5;
		showdownWin = 2;
		preFlopBets = new SortedList();
		flopBets = new SortedList();
		turnBets = new SortedList();
		riverBets = new SortedList();
		preFlopRaises = new SortedList();
		flopRaises = new SortedList();
		turnRaises = new SortedList();
		riverRaises = new SortedList();
		initializeStartingHandStrengths();
	}
	
	//actions: a list of strings that are the actions of an entire round	
	public void updateProfileAfterHand(String[] actions) {
		/*
		for (int i=actions.length-1; i>=Math.max(0,actions.length-4); i--) {
			String[] tokens = actions[i].split(":");
			if (tokens[0].equals("SHOW")) {
				String player = tokens[3];
				Card c1 = new Card(tokens[1]);
				Card c2 = new Card(tokens[2]);
				//do stuff with playername and cards... more analysis??????????
			}
		}*/
		
		
		//for bet size analysis
		int lastIncrease = 2;
		int potsize = 3;
		int toCall = 2;
		HashMap<String,Integer> pips = new HashMap<String,Integer>();
		
		boolean postedBB = false;
		boolean raised = false;
		
		//check actions until flop
		int action = 0;
		preflop:
		while (action < actions.length) {
			String[] tokens = actions[action++].split(":");
			
			String playerName = parsePlayer(tokens[tokens.length-1]);
			if (!pips.containsKey(playerName)) {
				pips.put(playerName,0);
			}
			
			if ("POST".equals(tokens[0])) {
				int amount = Integer.parseInt(tokens[1]);
				pips.put(playerName,amount);
				//POSTs are already accounted for in bet size variables
				
				if (amount == 2 && name.equals(playerName)) {
					postedBB = true;
				}
			} else if ("WIN".equals(tokens[0]) && name.equals(playerName)) {
				winPreFlop++;
			} else if ("DEAL".equals(tokens[0])) {
				break preflop;
			} else if (name.equals(playerName)) {
				if (postedBB && !raised) {
					preFlopCheckObs++;
				} else {
					preFlopCallObs++;
				}
			}
			
			if ("RAISE".equals(tokens[0])) {
				if (name.equals(playerName)) {
					//it's me raising
					if (postedBB && !raised) {
						betPreFlop++;
					} else {
						raisePreFlop++;
					}
				}
				
				int min = toCall+lastIncrease;
				int max = potsize+toCall*2;
				
				int amount = Integer.parseInt(tokens[1]);
				potsize+=(amount-pips.get(playerName));
				pips.put(playerName,amount);
				lastIncrease = amount-toCall;
				toCall = amount;
				
				double ratio = calculateBetSize(amount, min, max);
				if (!raised) {
					preFlopBets.insert(ratio);
				} else {
					preFlopRaises.insert(ratio);
				}
				raised = true;
			} else if ("CALL".equals(tokens[0])) {
				int amount = Integer.parseInt(tokens[1]);
				potsize+=(amount-pips.get(playerName));
				pips.put(playerName,amount);
			} else if ("FOLD".equals(tokens[0])) {
				if (playerName.equals(name)) {
					foldPreFlop++;
				}
			}
			
			
			
		}
		
		for (String key : pips.keySet()) {
			pips.put(key,0);
		}
		lastIncrease = 2;
		toCall = 0;
		//check actions until turn
		boolean canCheck = true;
		flop:
		while(action < actions.length) {
			String[] tokens = actions[action++].split(":");
			
			String playerName = parsePlayer(tokens[tokens.length-1]);
			
			if ("WIN".equals(tokens[0]) && name.equals(playerName)) {
				winFlop++;
			} else if ("DEAL".equals(tokens[0])) {
				break flop;
			} else if (name.equals(playerName)) {
				if (canCheck) {
					flopCheckObs++;
				} else {
					flopCallObs++;
				}
			}
			
			if ("RAISE".equals(tokens[0])) {
				if (name.equals(playerName)) {
					//it's me raising
					raiseFlop++;
				}
				canCheck = false;
				
				int min = toCall+lastIncrease;
				int max = potsize+toCall*2;
				
				int amount = Integer.parseInt(tokens[1]);
				potsize+=(amount-pips.get(playerName));
				pips.put(playerName,amount);
				lastIncrease = toCall-amount;
				toCall = amount;
				
				double ratio = calculateBetSize(amount, min, max);
				flopRaises.insert(ratio);
				
			} else if ("BET".equals(tokens[0])) {
				if (name.equals(playerName)) {
					betFlop++;
				}
				canCheck = false;
				
				int min = 2;
				int max = potsize;
				
				int amount = Integer.parseInt(tokens[1]);
				potsize+=amount;
				pips.put(playerName,amount);
				lastIncrease = amount;
				toCall = amount;
				
				double ratio = calculateBetSize(amount, min, max);
				flopBets.insert(ratio);
				
			} else if ("CALL".equals(tokens[0])) {
				int amount = Integer.parseInt(tokens[1]);
				potsize+=(amount-pips.get(playerName));
				pips.put(playerName,amount);
			} else if ("FOLD".equals(tokens[0])) {
				if (playerName.equals(name)) {
					foldFlop++;
				}
			}
			
		}
		
		for (String key : pips.keySet()) {
			pips.put(key,0);
		}
		lastIncrease = 2;
		toCall = 0;
		//check actions until river
		canCheck = true;
		turn:
		while(action < actions.length) {
			String[] tokens = actions[action++].split(":");
			
			String playerName = parsePlayer(tokens[tokens.length-1]);
			
			if ("WIN".equals(tokens[0]) && name.equals(playerName)) {
				winTurn++;
			} else if ("DEAL".equals(tokens[0])) {
				break turn;
			} else if (name.equals(playerName)) {
				if (canCheck) {
					turnCheckObs++;
				} else {
					turnCallObs++;
				}
			}
			
			if ("RAISE".equals(tokens[0])) {
				if (name.equals(playerName)) {
					//it's me raising
					raiseTurn++;
				}
				canCheck = false;
				
				int min = toCall+lastIncrease;
				int max = potsize+toCall*2;
				
				int amount = Integer.parseInt(tokens[1]);
				potsize+=(amount-pips.get(playerName));
				pips.put(playerName,amount);
				lastIncrease = toCall-amount;
				toCall = amount;
				
				double ratio = calculateBetSize(amount, min, max);
				turnRaises.insert(ratio);
			} else if ("BET".equals(tokens[0])) {
				if (name.equals(playerName)) {
					betTurn++;
				}
				canCheck = false;
				
				int min = 2;
				int max = potsize;
				
				int amount = Integer.parseInt(tokens[1]);
				potsize+=amount;
				pips.put(playerName,amount);
				lastIncrease = amount;
				toCall = amount;
				
				double ratio = calculateBetSize(amount, min, max);
				turnBets.insert(ratio);
				
			} else if ("CALL".equals(tokens[0])) {
				int amount = Integer.parseInt(tokens[1]);
				potsize+=(amount-pips.get(playerName));
				pips.put(playerName,amount);
			} else if ("FOLD".equals(tokens[0])) {
				if (playerName.equals(name)) {
					foldTurn++;
				}
			}
		}
		
		for (String key : pips.keySet()) {
			pips.put(key,0);
		}
		lastIncrease = 2;
		toCall = 0;
		//check actions until showdown
		canCheck = true;
		boolean madeShowdown = false;
		river:
		while(action < actions.length) {
			String[] tokens = actions[action++].split(":");
			
			String playerName = parsePlayer(tokens[tokens.length-1]);
			
			if ("SHOW".equals(tokens[0]) && name.equals(playerName)) {
				madeShowdown = true;
				showdown++;
			} else if ("WIN".equals(tokens[0]) && name.equals(playerName)) {
				if (madeShowdown) {
					showdownWin++;
				} else {
					winRiver++;
				}
			} else if ("DEAL".equals(tokens[0])) {
				break river;
			} else if (name.equals(playerName)) {
				if (canCheck) {
					riverCheckObs++;
				} else {
					riverCallObs++;
				}
			}
			
			if ("RAISE".equals(tokens[0])) {
				if (name.equals(playerName)) {
					//it's me raising
					raiseRiver++;
				}
				canCheck = false;
				
				int min = toCall+lastIncrease;
				int max = potsize+toCall*2;
				
				int amount = Integer.parseInt(tokens[1]);
				potsize+=(amount-pips.get(playerName));
				pips.put(playerName,amount);
				lastIncrease = toCall-amount;
				toCall = amount;
				
				double ratio = calculateBetSize(amount, min, max);
				riverRaises.insert(ratio);
			} else if ("BET".equals(tokens[0])) {
				if (name.equals(playerName)) {
					betRiver++;
				}
				canCheck = false;
				
				int min = 2;
				int max = potsize;
				
				int amount = Integer.parseInt(tokens[1]);
				potsize+=amount;
				pips.put(playerName,amount);
				lastIncrease = amount;
				toCall = amount;
				
				double ratio = calculateBetSize(amount, min, max);
				riverBets.insert(ratio);
				
			} else if ("CALL".equals(tokens[0])) {
				int amount = Integer.parseInt(tokens[1]);
				potsize+=(amount-pips.get(playerName));
				pips.put(playerName,amount);
			} else if ("FOLD".equals(tokens[0])) {
				if (playerName.equals(name)) {
					foldRiver++;
				}
			}
			
		}
		
		
		/*
		public int preFlopCheckObs;
		public int preFlopCallObs;
		public int flopCheckObs;
		public int flopCallObs;
		public int turnCheckObs;
		public int turnCallObs;
		public int riverCheckObs;
		public int riverCallObs;
		
		//percentage of x during y means the probability that the player will do x if in y.
		int foldPreFlop; //number of folds preflop
		int foldFlop; //number of folds during flop
		int foldTurn; //number of folds during turn
		int foldRiver; //number of folds during river
		int raisePreFlop; //number of raises preflop
		int raiseFlop; //number of raises during flop
		int raiseTurn; //number of raises during turn
		int raiseRiver; //number of raises during river
		int betPreFlop; //number of raises instead of checking preflop
		int betFlop; //number of bets during flop
		int betTurn; //number of bets during turn
		int betRiver; //number of bets during river
		int winPreFlop; //number of hands won by folds preflop
		int winFlop; //number of hands won by folds during flop
		int winTurn; //number of hands won by folds during turn
		int winRiver; //number of hands won by folds during river*/
	}
	
	//'street' is the number of cards on the board (should be 0, 3, 4, or 5)
	public double estimateStrength(String[] actions, int street) {
		boolean raised = false;
		int numRaises = 0;
		boolean bet = false;
		boolean called = false;
		boolean canCheck = true;
		boolean isBB = false;
		boolean foundAction = false;
		int firstRaise = 0;
		int actionCounter = actions.length-1;
		int streetOfAction = street;
		
		String[] tokens = actions[actionCounter--].split(":");
		while (!("DEAL".equals(tokens[0])) && actionCounter > 0) {
			if ("RAISE".equals(tokens[0]) && name.equals(parsePlayer(tokens[2]))) {
				raised = true;
				numRaises++;
				foundAction = true;
				if (firstRaise == 0) {
					firstRaise = actionCounter;
				}
			} else if ("BET".equals(tokens[0]) && name.equals(parsePlayer(tokens[2]))) {
				bet = true;
				foundAction = true;
			} else if ("CALL".equals(tokens[0]) && name.equals(parsePlayer(tokens[2]))) {
				called = true;
				foundAction = true;
			}
			tokens = actions[actionCounter--].split(":");
		}
		if (!foundAction && street > 3) {
			street--;
			streetOfAction--;
			tokens = actions[actionCounter--].split(":");
			while (!("DEAL".equals(tokens[0])) && actionCounter >= 0) {
				if ("RAISE".equals(tokens[0]) && name.equals(parsePlayer(tokens[2]))) {
					raised = true;
					numRaises++;
				} else if ("BET".equals(tokens[0]) && name.equals(parsePlayer(tokens[2]))) {
					bet = true;
				} else if ("CALL".equals(tokens[0]) && name.equals(parsePlayer(tokens[2]))) {
					called = true;
				}
				tokens = actions[actionCounter--].split(":");
			}
		}
		if (street == 0) {
			for (int i=0; i<firstRaise; i++) {
				tokens = actions[i].split(":");
				if ("POST".equals(tokens[0]) && "2".equals(tokens[1]) && name.equals(parsePlayer(tokens[2]))) {
					isBB = true;
				} else if ("RAISE".equals(tokens[0])) {
					canCheck = false;
				}
			}
		}
		
		
		//analyze bet amount
		String stop;
		
		switch (streetOfAction) {
			case 0:
				stop = "FLOP";
				break;
			case 3:
				stop = "TURN";
				break;
			case 4:
				stop = "RIVER";
				break;
			case 5:
				stop = "never";
				break;
			default:
				System.out.println("INVALID STREET OF ACTION!");
				stop = "never";
				break;
		}
		
		//find bet amount
		int lastIncrease = 2;
		int potsize = 3;
		int toCall = 2;
		actionCounter = 0;
		boolean onStreetOfAction = (streetOfAction == 0);
		HashMap<String,Integer> pips = new HashMap<String,Integer>();
		
		boolean postedBB = false;
		boolean hasOption = true;
		
		double maxBetRatio=-1;
		double maxRaiseRatio=-1;
		
		
		while (actionCounter < actions.length) {
			tokens = actions[actionCounter++].split(":");
			
			String playerName = parsePlayer(tokens[tokens.length-1]);
			
			if (actionCounter < 4) {
				if (!pips.containsKey(playerName)) {
					pips.put(playerName,0);
				}
			}
			
			if ("POST".equals(tokens[0])) {
				int amount = Integer.parseInt(tokens[1]);
				pips.put(playerName,Integer.parseInt(tokens[1]));
				if (amount == 2 && name.equals(playerName)) {
					postedBB = true;
				}
			} else if ("RAISE".equals(tokens[0])) {
				int min = toCall+lastIncrease;
				int max = potsize+toCall*2;
				int amount = Integer.parseInt(tokens[1]);
				potsize+=(amount-pips.get(playerName));
				pips.put(playerName,amount);
				lastIncrease = toCall-amount;
				toCall = amount;
				
				if (onStreetOfAction && name.equals(playerName)) {
					double ratio = calculateBetSize(amount,min,max);
					if (postedBB && hasOption) {
						maxBetRatio = Math.max(maxBetRatio,ratio);
					} else {
						maxRaiseRatio=Math.max(maxRaiseRatio,ratio);
					}
				}
				
				hasOption = false;
			} else if ("BET".equals(tokens[0])) {
				int min = 2;
				int max = potsize;
				int amount = Integer.parseInt(tokens[1]);
				potsize+=amount;
				pips.put(playerName,amount);
				lastIncrease = amount;
				toCall = amount;
				
				if (onStreetOfAction && name.equals(playerName)) {
					double ratio = calculateBetSize(amount,min,max);
					maxBetRatio = Math.max(maxBetRatio,ratio);
				}
				
				hasOption = false;
			} else if ("CALL".equals(tokens[0])) {
				int amount = Integer.parseInt(tokens[1]);
				potsize+=(amount-pips.get(playerName));
				pips.put(playerName,amount);
			} else if ("DEAL".equals(tokens[0])) {
				if (tokens[1].equals(stop)) {
					//STOP
					actionCounter = actions.length;
				} else if ((streetOfAction == 3 && tokens[1].equals("FLOP")) || (streetOfAction == 4 && tokens[1].equals("TURN")) || (streetOfAction == 5 && tokens[1].equals("RIVER"))) {
					onStreetOfAction = true;
				}
				lastIncrease = 0;
				toCall = 0;
				for (String key : pips.keySet()) {
					pips.put(key,0);
				}
			}
			
			
		}
		
		
		
		
		switch(street) {
			case 0:
				if (raised && canCheck && isBB) {
					double probBetRaise = betPreFlop/(double)preFlopCheckObs;
					if (preFlopBets.size() > 5) {
						double max = 1.0;
						double min = 1.0-probBetRaise;
						//int index=0;
						int index = (int)((min+(max-min)*preFlopBets.relativeIndex(maxBetRatio))*168);
						double estimated = startingHandStrengths.get(index);
						return estimated;
					} else {
						double estimated = startingHandStrengths.get((int)(84*(2-probBetRaise)));
						return estimated;
					}
				} else if (raised) {
					double probRaise = raisePreFlop/(double)preFlopCallObs;
					if (preFlopRaises.size() > 5) {
						double max = 1.0;
						double min = 1.0-probRaise;
						//int index=0;
						int index = (int)((min+(max-min)*preFlopRaises.relativeIndex(maxRaiseRatio))*168);
						double estimated = startingHandStrengths.get(index);
						return estimated;
					} else {
						double estimated = startingHandStrengths.get((int)(84*(2-probRaise)));
						return estimated;
					}
				} else if (called) {
					double probCall = 1-(foldPreFlop/(double)preFlopCallObs); //should this include raise chance?
					double estimated = startingHandStrengths.get((int)(84*(2-probCall)));
					return estimated;
				} else {
					return 0.35;
				}
			case 3:
				if (raised) {
					double probRaise = raiseFlop/(double)flopCallObs;
					if (flopRaises.size() > 3) {
						double max = 1.0;
						double min = 1.0-probRaise;
						//double estimated = 0.0;
						double estimated = (min+(max-min)*flopRaises.relativeIndex(maxRaiseRatio)); //should i check if i have enough observations?
						return estimated;
					} else {
						double estimated = 1-probRaise/2;
						return estimated;
					}
				} else if (bet) {
					
					double probBet = betFlop/(double)flopCheckObs;
					if (flopBets.size() > 3) {
						double max = 1.0;
						double min = 1.0-probBet;
						//double estimated = 0.0;
						double estimated = (min+(max-min)*flopBets.relativeIndex(maxBetRatio));
						return estimated;
					} else {
						double estimated = 1-probBet/2;
						return estimated;
					}
				} else if (called) {
					double probCall = 1-(foldFlop/(double)flopCallObs);
					double estimated = 1-probCall/2;
					return estimated;
				} else {
					double estimated = 0.5;
					return estimated;
				}
			case 4:
				if (raised) {
					double probRaise = raiseTurn/(double)turnCallObs;
					if (turnRaises.size() > 3) {
						double max = 1.0;
						double min = 1.0-probRaise;
						//double estimated = 0.0;
						double estimated = (min+(max-min)*turnRaises.relativeIndex(maxRaiseRatio));
						return estimated;
					} else {
						double estimated = 1-probRaise/2;
						return estimated;
					}
				} else if (bet) {
					double probBet = betTurn/(double)turnCheckObs;
					if (turnBets.size() > 3) {
						double max = 1.0;
						double min = 1.0-probBet;
						//double estimated = 0.0;
						double estimated = (min+(max-min)*turnBets.relativeIndex(maxBetRatio));
						return estimated;
					} else {
						double estimated = 1-probBet/2;
						return estimated;
					}
				} else if (called) {
					double probCall = 1-(foldTurn/(double)turnCallObs);
					double estimated = 1-probCall/2;
					return estimated;
				} else {
					double estimated = 0.5;
					return estimated;
				}
			case 5:
				if (raised) {
					double probRaise = raiseRiver/(double)riverCallObs;
					if (riverRaises.size() > 3) {
						double max = 1.0;
						double min = 1.0-probRaise;
						//double estimated = 0.0;
						double estimated = (min+(max-min)*riverRaises.relativeIndex(maxRaiseRatio));
						return estimated;
					} else {
						double estimated = 1-probRaise/2;
						return estimated;
					}
				} else if (bet) {
					double probBet = betRiver/(double)riverCheckObs;
					if (riverBets.size() > 3) {
						double max = 1.0;
						double min = 1.0-probBet;
						//double estimated = 0.0;
						double estimated = (min+(max-min)*riverBets.relativeIndex(maxBetRatio));
						return estimated;
					} else {
						double estimated = 1-probBet/2;
						return estimated;
					}
				} else if (called) {
					double probCall = 1-(foldRiver/(double)riverCallObs);
					double estimated = 1-probCall/2;
					return estimated;
				} else {
					double estimated = 0.5;
					return estimated;
				}
			default:
				System.out.println("INVALID STREET NUMBER SPECIFIED!");
				break;
		}
		return 0.7;
	}
	
	public String toKeyValue() {
		String output = Integer.toString(preFlopCheckObs/5);
		output+=" "+preFlopCallObs/5;
		output+=" "+foldPreFlop/5;
		output+=" "+betPreFlop/5;
		output+=" "+raisePreFlop/5;
		//output+=" "+winPreFlop/10;
		output+=" "+flopCheckObs/3;
		output+=" "+flopCallObs/3;
		output+=" "+foldFlop/3;
		output+=" "+betFlop/3;
		output+=" "+raiseFlop/3;
		//output+=" "+winFlop/10;
		output+=" "+turnCheckObs/2;
		output+=" "+turnCallObs/2;
		output+=" "+foldTurn/2;
		output+=" "+betTurn/2;
		output+=" "+raiseTurn/2;
		//output+=" "+winTurn/10;
		output+=" "+riverCheckObs/2;
		output+=" "+riverCallObs/2;
		output+=" "+foldRiver/2;
		output+=" "+betRiver/2;
		output+=" "+raiseRiver/2;
		//output+=" "+winRiver/10;
		//output+=" "+showdown/10;
		//output+=" "+showdownWin/10;
		return output;
	}
	
	public void printInfo() {
		System.out.println("PLAYER PROFILE FOR "+name);
		System.out.println("Out of "+preFlopCheckObs+" checkable preflops:");
		System.out.println(" bet-raise: "+betPreFlop);
		System.out.println("Out of "+preFlopCallObs+" forced preflops:");
		System.out.println(" fold: "+foldPreFlop);
		System.out.println(" raise: "+raisePreFlop);
		System.out.println("Preflops won: "+winPreFlop);
		System.out.println("Out of "+flopCheckObs+" checkable flops:");
		System.out.println(" bet: "+betFlop);
		System.out.println("Out of "+flopCallObs+" forced flops:");
		System.out.println(" fold: "+foldFlop);
		System.out.println(" raise: "+raiseFlop);
		System.out.println("Flops won: "+winFlop);
		System.out.println("Out of "+turnCheckObs+" checkable turns:");
		System.out.println(" bet: "+betTurn);
		System.out.println("Out of "+turnCallObs+" forced turns:");
		System.out.println(" fold: "+foldTurn);
		System.out.println(" raise: "+raiseTurn);
		System.out.println("Turns won: "+winTurn);
		System.out.println("Out of "+riverCheckObs+" checkable rivers:");
		System.out.println(" bet: "+betRiver);
		System.out.println("Out of "+riverCallObs+" forced rivers:");
		System.out.println(" fold: "+foldRiver);
		System.out.println(" raise: "+raiseRiver);
		System.out.println("Rivers won: "+winRiver);
		System.out.println("Entered showdown "+showdown+" times and won "+showdownWin);
	}
	
	public void printRatios() {
		System.out.println("PLAYER PROFILE FOR "+name);
		System.out.println("Out of "+preFlopCheckObs+" checkable preflops:");
		System.out.println(" bet-raise: "+betPreFlop/(double)preFlopCheckObs);
		System.out.println("Out of "+preFlopCallObs+" forced preflops:");
		System.out.println(" fold: "+foldPreFlop/(double)preFlopCallObs);
		System.out.println(" raise: "+raisePreFlop/(double)preFlopCallObs);
		System.out.println("Preflops won: "+winPreFlop/((double)preFlopCheckObs+preFlopCallObs));
		System.out.println("Out of "+flopCheckObs+" checkable flops:");
		System.out.println(" bet: "+betFlop/(double)flopCheckObs);
		System.out.println("Out of "+flopCallObs+" forced flops:");
		System.out.println(" fold: "+foldFlop/(double)flopCallObs);
		System.out.println(" raise: "+raiseFlop/(double)flopCallObs);
		System.out.println("Flops won: "+winFlop/((double)flopCheckObs+flopCallObs));
		System.out.println("Out of "+turnCheckObs+" checkable turns:");
		System.out.println(" bet: "+betTurn/(double)turnCheckObs);
		System.out.println("Out of "+turnCallObs+" forced turns:");
		System.out.println(" fold: "+foldTurn/(double)turnCallObs);
		System.out.println(" raise: "+raiseTurn/(double)turnCallObs);
		System.out.println("Turns won: "+winTurn/((double)turnCheckObs+turnCallObs));
		System.out.println("Out of "+riverCheckObs+" checkable rivers:");
		System.out.println(" bet: "+betRiver/(double)riverCheckObs);
		System.out.println("Out of "+riverCallObs+" forced rivers:");
		System.out.println(" fold: "+foldRiver/(double)riverCallObs);
		System.out.println(" raise: "+raiseRiver/(double)riverCallObs);
		System.out.println("Rivers won: "+winRiver/((double)riverCheckObs+riverCallObs));
		System.out.println("Enters showdown "+showdown/((double)preFlopCheckObs+preFlopCallObs)+" times and wins "+(showdownWin/(double)showdown));
	}
	
	public void initializeStartingHandStrengths() {
		startingHandStrengths = new ArrayList<Double>(169);
		for (int i=14; i>=2; i--) {
			for (int j=i; j>=2; j--) {
				Hand handToAdd = new Hand();
				handToAdd.addCard(new Card(Suit.C,i));
				handToAdd.addCard(new Card(Suit.D,j));
				startingHandStrengths.add(StartingHands.getStrength(handToAdd));
				if (i!=j) {
					Hand secondHTA = new Hand();
					secondHTA.addCard(new Card(Suit.H,i));
					secondHTA.addCard(new Card(Suit.H,j));
					startingHandStrengths.add(StartingHands.getStrength(secondHTA));
				}
			}
		}
		Collections.sort(startingHandStrengths);
	}
	
	public double getBetRate(int street) {
		switch(street) {
			case 0:
				return betPreFlop/(double)preFlopCheckObs;
			case 3:
				return betFlop/(double)flopCheckObs;
			case 4:
				return betTurn/(double)turnCheckObs;
			case 5:
				return betRiver/(double)riverCheckObs;
			default:
				System.out.println("INVALID STREET SPECIFIED TO getBetRate!");
				return 0.5;
		}
	}
	
	public double getCallRate(int street) {
		switch(street) {
			case 0:
				return 1-((raisePreFlop+foldPreFlop)/(double)preFlopCallObs);
			case 3:
				return 1-((raiseFlop+foldFlop)/(double)flopCallObs);
			case 4:
				return 1-((raiseTurn+foldTurn)/(double)turnCallObs);
			case 5:
				return 1-((raiseRiver+foldRiver)/(double)riverCallObs);
			default:
				System.out.println("INVALID STREET SPECIFIED TO getCallRate!");
				return 0.5;
		}
	}
	
	public double getRaiseRate(int street) {
		switch(street) {
			case 0:
				return raisePreFlop/(double)preFlopCheckObs;
			case 3:
				return raiseFlop/(double)flopCheckObs;
			case 4:
				return raiseTurn/(double)turnCheckObs;
			case 5:
				return raiseRiver/(double)riverCheckObs;
			default:
				System.out.println("INVALID STREET SPECIFIED TO getRaiseRate!");
				return 0.5;
		}
	}
	
	public double getFoldRate(int street) {
		switch(street) {
			case 0:
				return foldPreFlop/(double)preFlopCallObs;
			case 3:
				return foldFlop/(double)flopCallObs;
			case 4:
				return foldTurn/(double)turnCallObs;
			case 5:
				return foldRiver/(double)riverCallObs;
			default:
				System.out.println("INVALID STREET SPECIFIED TO getFoldRate!");
				return 0.5;
		}
	}
	
	public double calculateBetSize(int bet, int min, int max) {
		double ratio = (bet-min)/(double)(max-min);
		if (ratio < 0 || ratio > 1) {
			return 1.0;
		} else {
			return ratio;
		}
	}
	
	public String parsePlayer(String s) {
		return s.substring(0,s.length()-1);
	}
}
