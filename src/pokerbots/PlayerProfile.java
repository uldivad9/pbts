package pokerbots;

import java.util.*;
import pokerbots.*;

public class PlayerProfile {
	public String name;
	int preFlopObs;
	int flopObs;
	int turnObs;
	int riverObs;
	//percentage of x during y means the probability that the player will do x if in y.
	int foldPreFlop; //number of folds preflop
	int foldFlop; //number of folds during flop
	int foldTurn; //number of folds during turn
	int foldRiver; //number of folds during river
	int raisePreFlop; //number of raises preflop
	int raiseFlop; //number of raises during flop
	int raiseTurn; //number of raises during turn
	int raiseRiver; //number of raises during river
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
		out.preFlopObs = Integer.parseInt(tokens[0]);
		out.foldPreFlop = Integer.parseInt(tokens[1]);
		out.raisePreFlop = Integer.parseInt(tokens[2]);
		out.winPreFlop = Integer.parseInt(tokens[3]);
		out.flopObs = Integer.parseInt(tokens[4]);
		out.foldFlop = Integer.parseInt(tokens[5]);
		out.betFlop = Integer.parseInt(tokens[6]);
		out.raiseFlop = Integer.parseInt(tokens[7]);
		out.winFlop = Integer.parseInt(tokens[8]);
		out.turnObs = Integer.parseInt(tokens[9]);
		out.foldTurn = Integer.parseInt(tokens[10]);
		out.betTurn = Integer.parseInt(tokens[11]);
		out.raiseTurn = Integer.parseInt(tokens[12]);
		out.winTurn = Integer.parseInt(tokens[13]);
		out.riverObs = Integer.parseInt(tokens[14]);
		out.foldRiver = Integer.parseInt(tokens[15]);
		out.betRiver = Integer.parseInt(tokens[16]);
		out.raiseRiver = Integer.parseInt(tokens[17]);
		out.winRiver = Integer.parseInt(tokens[18]);
		out.showdown = Integer.parseInt(tokens[19]);
		out.showdownWin = Integer.parseInt(tokens[20]);
		return out;
	}
	
	public PlayerProfile(String newName) {
		name = newName;
		preFlopObs = 20;
		flopObs = 10;
		turnObs = 5;
		riverObs = 3;
		foldPreFlop = 10;
		raisePreFlop = 4;
		foldFlop = 3;
		betFlop = 2;
		raiseFlop = 2;
		foldTurn = 2;
		betTurn = 1;
		raiseTurn = 1;
		foldRiver = 1;
		betRiver = 1;
		raiseRiver = 1;
		showdown = 2;
		showdownWin = 1;
		initializeStartingHandStrengths();
	}
	
	//actions: a list of strings that are the actions of an entire round	
	public void updateProfileAfterHand(String[] actions) {
		
		for (int i=actions.length-1; i>=Math.max(0,actions.length-4); i--) {
			String[] tokens = actions[i].split(":");
			if (tokens[0].equals("SHOW")) {
				String player = tokens[3];
				Card c1 = new Card(tokens[1]);
				Card c2 = new Card(tokens[2]);
				//do stuff with playername and cards... more analysis
			}
		}
		
		//check actions until flop
		int action = 0;
		
		boolean foldedPreFlop = false;
		boolean raisedPreFlop = false;
		boolean wonPreFlop = false;
		preflop:
		while (action < actions.length) {
			String[] tokens = actions[action++].split(":");
			
			if ("DEAL".equals(tokens[0])) {
				break preflop;
			} else if ("FOLD".equals(tokens[0]) && name.equals(tokens[1])) {
				foldedPreFlop = true;
			} else if ("RAISE".equals(tokens[0]) && name.equals(tokens[2])) {
				raisedPreFlop = true;
			} else if ("WIN".equals(tokens[0]) && name.equals(tokens[2])) {
				wonPreFlop = true;
			}
		}
		
		
		//check actions until turn
		boolean foldedFlop = false;
		boolean bettedFlop = false;
		boolean raisedFlop = false;
		boolean wonFlop = false;
		flop:
		while(action < actions.length) {
			String[] tokens = actions[action++].split(":");
			if ("DEAL".equals(tokens[0])) {
				break flop;
			} else if ("FOLD".equals(tokens[0]) && name.equals(tokens[1])) {
				foldedFlop = true;
			} else if ("BET".equals(tokens[0]) && name.equals(tokens[2])) {
				bettedFlop = true;
			} else if ("RAISE".equals(tokens[0]) && name.equals(tokens[2])) {
				raisedFlop = true;
			} else if ("WIN".equals(tokens[0]) && name.equals(tokens[2])) {
				wonFlop = true;
			}
		}
		
		//check actions until river
		boolean foldedTurn = false;
		boolean bettedTurn = false;
		boolean raisedTurn = false;
		boolean wonTurn = false;
		turn:
		while(action < actions.length) {
			String[] tokens = actions[action++].split(":");
			if ("DEAL".equals(tokens[0])) {
				break turn;
			} else if ("FOLD".equals(tokens[0]) && name.equals(tokens[1])) {
				foldedTurn = true;
			} else if ("BET".equals(tokens[0]) && name.equals(tokens[2])) {
				bettedTurn = true;
			} else if ("RAISE".equals(tokens[0]) && name.equals(tokens[2])) {
				raisedTurn = true;
			} else if ("WIN".equals(tokens[0]) && name.equals(tokens[2])) {
				wonTurn = true;
			}
		}
		
		//check actions until showdown
		boolean foldedRiver = false;
		boolean bettedRiver = false;
		boolean raisedRiver = false;
		boolean wonRiver = false;
		boolean madeShowdown = false;
		boolean wonShowdown = false;
		river:
		while(action < actions.length) {
			String[] tokens = actions[action++].split(":");
			if ("FOLD".equals(tokens[0]) && name.equals(tokens[1])) {
				foldedRiver = true;
			} else if ("BET".equals(tokens[0]) && name.equals(tokens[2])) {
				bettedRiver = true;
			} else if ("RAISE".equals(tokens[0]) && name.equals(tokens[2])) {
				raisedRiver = true;
			} else if ("SHOW".equals(tokens[0]) && name.equals(tokens[3])) {
				madeShowdown = true;
			} else if (madeShowdown && "WIN".equals(tokens[0]) && name.equals(tokens[2])) {
				wonShowdown = true;
			} else if (!madeShowdown && "WIN".equals(tokens[0]) && name.equals(tokens[2])) {
				wonRiver = true;
			}
		}
		
		//compute.
		compute: {
			//preflop
			preFlopObs++;
			if (raisedPreFlop) {
				raisePreFlop++;
			}
			if (foldedPreFlop) {
				foldPreFlop++;
				break compute;
			}
			if (wonPreFlop) {
				winPreFlop++;
				break compute;
			}
			
			//flop
			flopObs++;
			if (bettedFlop) {
				betFlop++;
			}
			if (raisedFlop) {
				raiseFlop++;
			}
			if (foldedFlop) {
				foldFlop++;
				break compute;
			}
			if (wonFlop) {
				winFlop++;
				break compute;
			}
			
			//turn
			turnObs++;
			if (bettedTurn) {
				betTurn++;
			}
			if (raisedTurn) {
				raiseTurn++;
			}
			if (foldedTurn) {
				foldTurn++;
				break compute;
			}
			if (wonTurn) {
				winTurn++;
				break compute;
			}
			
			//river
			riverObs++;
			if (bettedRiver) {
				betRiver++;
			}
			if (raisedRiver) {
				raiseRiver++;
			}
			if (foldedRiver) {
				foldRiver++;
				break compute;
			}
			if (wonRiver) {
				winRiver++;
				break compute;
			}
			if (madeShowdown) {
				showdown++;
			}
			if (wonShowdown) {
				showdownWin++;
			}
		}
		
		/*
		System.out.println("raised preflop: "+raisedPreFlop);
		System.out.println("folded preflop: "+foldedPreFlop);
		System.out.println("raised flop: "+raisedFlop);
		System.out.println("bet flop: "+bettedFlop);
		System.out.println("folded flop: "+foldedFlop);
		System.out.println("raised turn: "+raisedTurn);
		System.out.println("bet turn: "+bettedTurn);
		System.out.println("folded turn: "+foldedTurn);
		System.out.println("raised river: "+raisedRiver);
		System.out.println("bet river: "+bettedRiver);
		System.out.println("folded river: "+foldedRiver);
		System.out.println("showed: "+madeShowdown);
		System.out.println("won showdown: "+wonShowdown);
		*/
	}
	
	//'street' is the number of cards on the board (should be 0, 3, 4, or 5)
	public double estimateStrength(String[] actions, int street) {
		boolean raised = false;
		boolean bet = false;
		boolean called = false;
		int actionCounter = actions.length-1;
		String[] tokens = actions[actionCounter--].split(":");
		while (!"DEAL".equals(tokens[0])) {
			if ("RAISE".equals(tokens[0]) && name.equals(tokens[2])) {
				raised = true;
			} else if ("BET".equals(tokens[0]) && name.equals(tokens[2])) {
				bet = true;
			} else if ("CALL".equals(tokens[0]) && name.equals(tokens[2])) {
				called = true;
			}
		}
		
		switch(street) {
			case 0:
				if (raised) {
					double probRaise = raisePreFlop/(double)preFlopObs;
					double estimated = startingHandStrengths.get((int)(probRaise*169/2));
					return estimated;
				} else if (called) {
					double probCall = 1-(foldPreFlop/(double)preFlopObs);
					double estimated = startingHandStrengths.get((int)(probRaise*169/2));
					return estimated;
				} else {
					return 0.35;
				}
			case 3:
				if (raised) {
					double probRaise = raiseFlop/(double)flopObs;
					double estimated = 1-2*probRaise;
				} else if (bet) {
				
				} else if (called) {
				
				} else {
				
				}
				break;
			case 4:
				if (raised) {
					
				} else if (bet) {
				
				} else if (called) {
				
				} else {
				
				}
				break;
			case 5:
				if (raised) {
					
				} else if (bet) {
				
				} else if (called) {
				
				} else {
				
				}
				break;
			default:
				System.out.println("INVALID STREET NUMBER SPECIFIED!");
				break;
		}
		return 0.7;
	}
	
	public String toKeyValue() {
		String output = Integer.toString(preFlopObs);
		output+=" "+foldPreFlop;
		output+=" "+raisePreFlop;
		output+=" "+winPreFlop;
		output+=" "+flopObs;
		output+=" "+foldFlop;
		output+=" "+betFlop;
		output+=" "+raiseFlop;
		output+=" "+winFlop;
		output+=" "+turnObs;
		output+=" "+foldTurn;
		output+=" "+betTurn;
		output+=" "+raiseTurn;
		output+=" "+winTurn;
		output+=" "+riverObs;
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
		System.out.println("Out of "+preFlopObs+" preflop observations:");
		System.out.println(" raise: "+raisePreFlop);
		System.out.println(" fold: "+foldPreFlop);
		System.out.println(" won: "+winPreFlop);
		System.out.println("Out of "+flopObs+" flop observations:");
		System.out.println(" raise: "+raiseFlop);
		System.out.println(" bet: "+betFlop);
		System.out.println(" fold: "+foldFlop);
		System.out.println(" won: "+winFlop);
		System.out.println("Out of "+turnObs+" turn observations:");
		System.out.println(" raise: "+raiseTurn);
		System.out.println(" bet: "+betTurn);
		System.out.println(" fold: "+foldTurn);
		System.out.println(" won: "+winTurn);
		System.out.println("Out of "+riverObs+" river observations:");
		System.out.println(" raise: "+raiseRiver);
		System.out.println(" bet: "+betRiver);
		System.out.println(" fold: "+foldRiver);
		System.out.println(" won: "+winRiver);
		System.out.println("Entered showdown "+showdown+" times and won "+showdownWin);
	}
	
	public void printRatios() {
		System.out.println("PLAYER PROFILE FOR "+name);
		System.out.println("Out of "+preFlopObs+" preflop observations:");
		System.out.println(" raise: "+raisePreFlop/(double)preFlopObs);
		System.out.println(" fold: "+foldPreFlop/(double)preFlopObs);
		System.out.println(" won: "+winPreFlop/(double)preFlopObs);
		System.out.println("Out of "+flopObs+" flop observations:");
		System.out.println(" raise: "+raiseFlop/(double)flopObs);
		System.out.println(" bet: "+betFlop/(double)flopObs);
		System.out.println(" fold: "+foldFlop/(double)flopObs);
		System.out.println(" won: "+winFlop/(double)flopObs);
		System.out.println("Out of "+turnObs+" turn observations:");
		System.out.println(" raise: "+raiseTurn/(double)turnObs);
		System.out.println(" bet: "+betTurn/(double)turnObs);
		System.out.println(" fold: "+foldTurn/(double)turnObs);
		System.out.println(" won: "+winTurn/(double)turnObs);
		System.out.println("Out of "+riverObs+" river observations:");
		System.out.println(" raise: "+raiseRiver/(double)riverObs);
		System.out.println(" bet: "+betRiver/(double)riverObs);
		System.out.println(" fold: "+foldRiver/(double)riverObs);
		System.out.println(" won: "+winRiver/(double)riverObs);
		System.out.println("Showdown "+showdown/(double)preFlopObs);
		System.out.println(" wins: "+showdownWin/(double)showdown);
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
		startingHandStrengths.reverse();
	}
}