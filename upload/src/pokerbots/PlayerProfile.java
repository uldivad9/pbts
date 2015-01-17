package pokerbots;

public class PlayerProfile {
	String name;
	int observations;
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
		observations = 20;
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
		int actionNum = 0;
		
		boolean foldedPreFlop = false;
		boolean raisedPreFlop = false;
		preflop:
		while (actionNum < actions.length) {
			String[] tokens = actions[actionNum++].split(":");
			
			if ("DEAL".equals(tokens[0])) {
				break preflop;
			} else if ("FOLD".equals(tokens[0]) && name.equals(tokens[1])) {
				foldedPreFlop = false;
			} else if ("RAISE".equals(tokens[0]) && name.equals(tokens[2])) {
				raisedPreFlop = true;
			}
		}
		
		
		//check actions until turn
		boolean foldedFlop = false;
		boolean bettedFlop = false;
		boolean raisedFlop = false;
		flop:
		while(actionNum < actions.length) {
			String[] tokens = actions[actionNum++].split(":");
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
		turn:
		while(actionNum < actions.length) {
			String[] tokens = actions[actionNum++].split(":");
			if ("DEAL".equals(tokens[0])) {
				break turn;
			} else if ("FOLD".equals(tokens[0])) {
				String playerName = tokens[1];
			}
		}
		
		//check actions until showdown
		river:
		while(actionNum < actions.length) {
			String[] tokens = actions[actionNum++].split(":");
			if ("SHOW".equals(tokens[0])) {
				break river;
			} else if ("FOLD".equals(tokens[0])) {
				String playerName = tokens[1];
			}
		}
	}
}