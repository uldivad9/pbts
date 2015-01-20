package pokerbots;

import java.util.*;
import pokerbots.*;

public class PlayerProfile {
	public String name;
	/*
	int preFlopObs;
	int flopObs;
	int turnObs;
	int riverObs;
	*/
	int preFlopCheckObs;
	int preFlopCallObs;
	int flopCheckObs;
	int flopCallObs;
	int turnCheckObs;
	int turnCallObs;
	int riverCheckObs;
	int riverCallObs;
	
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
	

	public static PlayerProfile parseKeyValue(String name, String kv) {
		String[] tokens = kv.split(" ");
		PlayerProfile out = new PlayerProfile(name);
		out.preFlopCheckObs = Integer.parseInt(tokens[0]);
		out.preFlopCallObs = Integer.parseInt(tokens[1]);
		out.foldPreFlop = Integer.parseInt(tokens[2]);
		out.betPreFlop = Integer.parseInt(tokens[3]);
		out.raisePreFlop = Integer.parseInt(tokens[4]);
		out.winPreFlop = Integer.parseInt(tokens[5]);
		out.flopCheckObs = Integer.parseInt(tokens[6]);
		out.flopCallObs = Integer.parseInt(tokens[7]);
		out.foldFlop = Integer.parseInt(tokens[8]);
		out.betFlop = Integer.parseInt(tokens[9]);
		out.raiseFlop = Integer.parseInt(tokens[10]);
		out.winFlop = Integer.parseInt(tokens[11]);
		out.turnCheckObs = Integer.parseInt(tokens[12]);
		out.turnCallObs = Integer.parseInt(tokens[13]);
		out.foldTurn = Integer.parseInt(tokens[14]);
		out.betTurn = Integer.parseInt(tokens[15]);
		out.raiseTurn = Integer.parseInt(tokens[16]);
		out.winTurn = Integer.parseInt(tokens[17]);
		out.riverCheckObs = Integer.parseInt(tokens[18]);
		out.riverCallObs = Integer.parseInt(tokens[19]);
		out.foldRiver = Integer.parseInt(tokens[20]);
		out.betRiver = Integer.parseInt(tokens[21]);
		out.raiseRiver = Integer.parseInt(tokens[22]);
		out.winRiver= Integer.parseInt(tokens[23]);
		out.showdown = Integer.parseInt(tokens[24]);
		out.showdownWin = Integer.parseInt(tokens[25]);
		return out;
	}
	
	public PlayerProfile(String newName) {
		name = newName;
		preFlopCheckObs = 10;
		preFlopCallObs = 20;
		flopCheckObs = 10;
		flopCallObs = 8;
		turnCheckObs = 6;
		turnCallObs = 5;
		riverCheckObs = 6;
		riverCallObs = 5;
		foldPreFlop = 12;
		betPreFlop = 1;
		raisePreFlop = 2;
		foldFlop = 4;
		betFlop = 5;
		raiseFlop = 1;
		foldTurn = 3;
		betTurn = 2;
		raiseTurn = 1;
		foldRiver = 3;
		betRiver = 2;
		raiseRiver = 1;
		showdown = 5;
		showdownWin = 2;
		
		/*
		preFlopObs = 40;
		flopObs = 10;
		turnObs = 5;
		riverObs = 4;
		foldPreFlop = 20;
		raisePreFlop = 5;
		foldFlop = 3;
		betFlop = 3;
		raiseFlop = 1;
		foldTurn = 2;
		betTurn = 1;
		raiseTurn = 1;
		foldRiver = 1;
		betRiver = 1;
		raiseRiver = 1;
		showdown = 2;
		showdownWin = 1;*/
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
				//do stuff with playername and cards... more analysis
			}
		}*/
		
		//check actions until flop
		int action = 0;
		
		boolean postedBB = false;
		boolean raised = false;
		preflop:
		while (action < actions.length) {
			String[] tokens = actions[action++].split(":");
			
			
			if ("POST".equals(tokens[0]) && name.equals(tokens[2])) {
				if ("2".equals(tokens[1])) {
					postedBB = true;
				}
			} else if ("WIN".equals(tokens[0]) && name.equals(tokens[2])) {
				winPreFlop++;
			} else if (name.equals(tokens[tokens.length-1])) {
				//our player is making an action
				if (postedBB && !raised) {
					preFlopCheckObs++;
				} else {
					preFlopCallObs++;
				}
				if ("FOLD".equals(tokens[0])) {
					foldPreFlop++;
				} else if ("RAISE".equals(tokens[0])) {
					if (postedBB && !raised) {
						betPreFlop++;
					} else {
						raisePreFlop++;
					}
				}
			} else if ("RAISE".equals(tokens[0])) {
				raised = true;
			} else if ("DEAL".equals(tokens[0])) {
				break preflop;
			}
		}
		
		
		//check actions until turn
		boolean canCheck = true;
		flop:
		while(action < actions.length) {
			String[] tokens = actions[action++].split(":");
			if ("WIN".equals(tokens[0]) && name.equals(tokens[2])) {
				winFlop++;
			} else if (name.equals(tokens[tokens.length-1])) {
				//our player is making an action
				if (canCheck) {
					flopCheckObs++;
				} else {
					flopCallObs++;
				}
				if ("FOLD".equals(tokens[0])) {
					foldFlop++;
				} else if ("BET".equals(tokens[0])) {
					betFlop++;
				} else if ("RAISE".equals(tokens[0])) {
					raiseFlop++;
				}
			} else if ("BET".equals(tokens[0]) || "RAISE".equals(tokens[0])) {
				canCheck = false;
			} else if ("DEAL".equals(tokens[0])) {
				break flop;
			}
		}
		
		//check actions until river
		canCheck = true;
		turn:
		while(action < actions.length) {
			String[] tokens = actions[action++].split(":");
			if ("WIN".equals(tokens[0]) && name.equals(tokens[2])) {
				winTurn++;
			} else if (name.equals(tokens[tokens.length-1])) {
				//our player is making an action
				if (canCheck) {
					turnCheckObs++;
				} else {
					turnCallObs++;
				}
				if ("FOLD".equals(tokens[0])) {
					foldTurn++;
				} else if ("BET".equals(tokens[0])) {
					betTurn++;
				} else if ("RAISE".equals(tokens[0])) {
					raiseTurn++;
				}
			} else if ("BET".equals(tokens[0]) || "RAISE".equals(tokens[0])) {
				canCheck = false;
			} else if ("DEAL".equals(tokens[0])) {
				break turn;
			}
		}
		
		//check actions until showdown
		canCheck = true;
		boolean madeShowdown = false;
		river:
		while(action < actions.length) {
			String[] tokens = actions[action++].split(":");
			if ("SHOW".equals(tokens[0]) && name.equals(tokens[3])) {
				madeShowdown = true;
				showdown++;
			} else if ("WIN".equals(tokens[0]) && name.equals(tokens[2])) {
				if (madeShowdown) {
					showdownWin++;
				} else {
					winRiver++;
				}
			} else if (name.equals(tokens[tokens.length-1])) {
				//our player is making an action
				if (canCheck) {
					riverCheckObs++;
				} else {
					riverCallObs++;
				}
				if ("FOLD".equals(tokens[0])) {
					foldRiver++;
				} else if ("BET".equals(tokens[0])) {
					betRiver++;
				} else if ("RAISE".equals(tokens[0])) {
					raiseRiver++;
				}
			} else if ("BET".equals(tokens[0]) || "RAISE".equals(tokens[0])) {
				canCheck = false;
			}
		}
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
		String[] tokens = actions[actionCounter--].split(":");
		while (!("DEAL".equals(tokens[0])) && actionCounter > 0) {
			if ("RAISE".equals(tokens[0]) && name.equals(tokens[2])) {
				raised = true;
				numRaises++;
				foundAction = true;
				if (firstRaise == 0) {
					firstRaise = actionCounter;
				}
			} else if ("BET".equals(tokens[0]) && name.equals(tokens[2])) {
				bet = true;
				foundAction = true;
			} else if ("CALL".equals(tokens[0]) && name.equals(tokens[2])) {
				called = true;
				foundAction = true;
			}
			tokens = actions[actionCounter--].split(":");
		}
		if (!foundAction && street > 3) {
			street--;
			tokens = actions[actionCounter--].split(":");
			while (!("DEAL".equals(tokens[0])) && actionCounter >= 0) {
				if ("RAISE".equals(tokens[0]) && name.equals(tokens[2])) {
					raised = true;
					numRaises++;
				} else if ("BET".equals(tokens[0]) && name.equals(tokens[2])) {
					bet = true;
				} else if ("CALL".equals(tokens[0]) && name.equals(tokens[2])) {
					called = true;
				}
				tokens = actions[actionCounter--].split(":");
			}
		}
		if (street == 0) {
			for (int i=0; i<firstRaise; i++) {
				tokens = actions[i].split(":");
				if ("POST".equals(tokens[0]) && "2".equals(tokens[1]) && name.equals(tokens[2])) {
					isBB = true;
				} else if ("RAISE".equals(tokens[0])) {
					canCheck = false;
				}
			}
		}
		
		
		
		switch(street) {
			case 0:
				if (raised && canCheck && isBB) {
					double probBetRaise = betPreFlop/(double)preFlopCheckObs;
					double estimated = startingHandStrengths.get((int)(probBetRaise*169/2));
					return estimated;
				} else if (raised) {
					double probRaise = raisePreFlop/(double)preFlopCallObs;
					double estimated = startingHandStrengths.get((int)(probRaise*169/2));
					return estimated;
				} else if (called) {
					double probCall = 1-(foldPreFlop/(double)preFlopCallObs);
					double estimated = startingHandStrengths.get((int)(probCall*169/2));
					return estimated;
				} else {
					return 0.35;
				}
			case 3:
				if (raised) {
					double probRaise = raiseFlop/(double)flopCallObs;
					double estimated = 1-probRaise/2;
					return estimated;
				} else if (bet) {
					double probBet = betFlop/(double)flopCheckObs;
					double estimated = 1-probBet/2;
					return estimated;
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
					double estimated = 1-probRaise/2;
					return estimated;
				} else if (bet) {
					double probBet = betTurn/(double)turnCheckObs;
					double estimated = 1-probBet/2;
					return estimated;
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
					double estimated = 1-probRaise/2;
					return estimated;
				} else if (bet) {
					double probBet = betRiver/(double)riverCheckObs;
					double estimated = 1-probBet/2;
					return estimated;
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
		String output = Integer.toString(preFlopCheckObs);
		output+=" "+preFlopCallObs;
		output+=" "+foldPreFlop;
		output+=" "+betPreFlop;
		output+=" "+raisePreFlop;
		output+=" "+winPreFlop;
		output+=" "+flopCheckObs;
		output+=" "+flopCallObs;
		output+=" "+foldFlop;
		output+=" "+betFlop;
		output+=" "+raiseFlop;
		output+=" "+winFlop;
		output+=" "+turnCheckObs;
		output+=" "+turnCallObs;
		output+=" "+foldTurn;
		output+=" "+betTurn;
		output+=" "+raiseTurn;
		output+=" "+winTurn;
		output+=" "+riverCheckObs;
		output+=" "+riverCallObs;
		output+=" "+foldRiver;
		output+=" "+betRiver;
		output+=" "+raiseRiver;
		output+=" "+winRiver;
		output+=" "+showdown;
		output+=" "+showdownWin;
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
		Collections.sort(startingHandStrengths, Collections.reverseOrder());
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
}