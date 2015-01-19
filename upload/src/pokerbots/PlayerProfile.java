package pokerbots;

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
	

	public static PlayerProfile parseKeyValue(String kv) {
		String[] tokens = kv.split(" ");
		PlayerProfile out = new PlayerProfile(tokens[0]);
		out.preFlopObs = Integer.parseInt(tokens[1]);
		out.foldPreFlop = Integer.parseInt(tokens[2]);
		out.raisePreFlop = Integer.parseInt(tokens[3]);
		out.winPreFlop = Integer.parseInt(tokens[4]);
		out.flopObs = Integer.parseInt(tokens[5]);
		out.foldFlop = Integer.parseInt(tokens[6]);
		out.betFlop = Integer.parseInt(tokens[7]);
		out.raiseFlop = Integer.parseInt(tokens[8]);
		out.winFlop = Integer.parseInt(tokens[9]);
		out.turnObs = Integer.parseInt(tokens[10]);
		out.foldTurn = Integer.parseInt(tokens[11]);
		out.betTurn = Integer.parseInt(tokens[12]);
		out.raiseTurn = Integer.parseInt(tokens[13]);
		out.winTurn = Integer.parseInt(tokens[14]);
		out.riverObs = Integer.parseInt(tokens[15]);
		out.foldRiver = Integer.parseInt(tokens[16]);
		out.betRiver = Integer.parseInt(tokens[17]);
		out.raiseRiver = Integer.parseInt(tokens[18]);
		out.winRiver = Integer.parseInt(tokens[19]);
		out.showdown = Integer.parseInt(tokens[20]);
		out.showdownWin = Integer.parseInt(tokens[21]);
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
				wonPreFlop = true;
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
	
	
	
	public String toKeyValue() {
		String output = name;
		output+=" "+preFlopObs;
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
}