package pokerbots;

public class PlayerProfile {
	String name;
	int preFlopObs;
	int flopObs;
	int turnObs;
	int riverObs;
	//percentage of x during y means the probability that the player will do x if in y.
	double foldPreFlop; //percentage of folds preflop
	double foldFlop; //percentage of folds during flop
	double foldTurn; //percentage of folds during turn
	double foldRiver; //percentage of folds during river
	double raisePreFlop; //percentage of raises preflop
	double raiseFlop; //percentage of raises during flop
	double raiseTurn; //percentage of raises during turn
	double raiseRiver; //percentage of raises during river
	double betFlop; //percentage of bets during flop
	double betTurn; //percentage of bets during turn
	double betRiver; //percentage of bets during river
	
	double showdown; //percentage of hands gone to showdown
	double showdownWin; //percentage of showdowns won
	
	public PlayerProfile(String newName) {
		name = newName;
		preFlopObs = 20;
		flopObs = 10;
		turnObs = 5;
		riverObs = 3;
		foldPreFlop = 0.6;
		raisePreFlop = 0.2;
		foldFlop = 0.3;
		betFlop = 0.2;
		raiseFlop = 0.2;
		foldTurn = 0.3;
		betTurn = 0.2;
		raiseTurn = 0.2;
		foldRiver = 0.3;
		betRiver = 0.2;
		raiseRiver = 0.2;
		showdown = 0.1;
		showdownWin = 0.5;
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
		preflop:
		while (action < actions.length) {
			String[] tokens = actions[action++].split(":");
			
			if ("DEAL".equals(tokens[0])) {
				break preflop;
			} else if ("FOLD".equals(tokens[0]) && name.equals(tokens[1])) {
				foldedPreFlop = true;
			} else if ("RAISE".equals(tokens[0]) && name.equals(tokens[2])) {
				raisedPreFlop = true;
			}
		}
		
		
		//check actions until turn
		boolean foldedFlop = false;
		boolean bettedFlop = false;
		boolean raisedFlop = false;
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
			}
		}
		
		//check actions until river
		boolean foldedTurn = false;
		boolean bettedTurn = false;
		boolean raisedTurn = false;
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
			}
		}
		
		//check actions until showdown
		boolean foldedRiver = false;
		boolean bettedRiver = false;
		boolean raisedRiver = false;
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
			} else if ("WIN".equals(tokens[0]) && name.equals(tokens[2])) {
				wonShowdown = true;
			}
		}
		
		//compute.
		compute: {
			//preflop
			preFlopObs++;
			if (raisedPreFlop) {
				raisePreFlop = (raisePreFlop*(preFlopObs-1)+1)/preFlopObs;
			}
			if (foldedPreFlop) {
				foldPreFlop = (foldPreFlop*(preFlopObs-1)+1)/preFlopObs;
				break compute;
			}
			
			//flop
			flopObs++;
			if (bettedFlop) {
				betFlop = (betFlop*(flopObs-1)+1)/flopObs;
			}
			if (raisedFlop) {
				raiseFlop = (raiseFlop*(flopObs-1)+1)/flopObs;
			}
			if (foldedFlop) {
				foldFlop = (foldFlop*(flopObs-1)+1)/flopObs;
				break compute;
			}
			
			//turn
			turnObs++;
			if (bettedTurn) {
				betTurn = (betTurn*(turnObs-1)+1)/turnObs;
			}
			if (raisedTurn) {
				raiseTurn = (raiseTurn*(turnObs-1)+1)/turnObs;
			}
			if (foldedTurn) {
				foldTurn = (foldTurn*(turnObs-1)+1)/turnObs;
				break compute;
			}
			
			//river
			riverObs++;
			if (bettedRiver) {
				betRiver = (betRiver*(riverObs-1)+1)/riverObs;
			}
			if (raisedRiver) {
				raiseRiver = (raiseRiver*(riverObs-1)+1)/riverObs;
			}
			if (foldedRiver) {
				foldRiver = (foldRiver*(riverObs-1)+1)/riverObs;
				break compute;
			}
			
			if (madeShowdown) {
				showdown = (showdown*(preFlopObs-1)+1)/preFlopObs;
			}
			if (wonShowdown) {
				showdownWin = (showdownWin*(showdown*preFlopObs)+1)/(showdown*preFlopObs);
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
	
	public void printInfo() {
		System.out.println("PLAYER PROFILE FOR "+name);
		System.out.println("Out of "+preFlopObs+" preflop observations:");
		System.out.println(" raise: "+raisePreFlop);
		System.out.println(" fold: "+foldPreFlop);
		System.out.println("Out of "+flopObs+" flop observations:");
		System.out.println(" raise: "+raiseFlop);
		System.out.println(" bet: "+betFlop);
		System.out.println(" fold: "+foldFlop);
		System.out.println("Out of "+turnObs+" turn observations:");
		System.out.println(" raise: "+raiseTurn);
		System.out.println(" bet: "+betTurn);
		System.out.println(" fold: "+foldTurn);
		System.out.println("Out of "+riverObs+" river observations:");
		System.out.println(" raise: "+raiseRiver);
		System.out.println(" bet: "+betRiver);
		System.out.println(" fold: "+foldRiver);
		System.out.println("Enters showdown at "+showdown+" and wins at "+showdownWin);
	}
}