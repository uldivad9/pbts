package pokerbots.stats;

import java.io.*;
import java.util.*;

import pokerbots.*;

public class PlayerSim {
	
	
	static ArrayList<Card> deck;
	static ArrayList<Integer> pip = new ArrayList<Integer>();
	static Random rand = new Random();
	static ArrayList<Boolean> folded = new ArrayList<Boolean>();
	
	static int pot;
	static ArrayList<Player> allPlayers = new ArrayList<Player>();
	static ArrayList<Integer> avgWin = new ArrayList<Integer>();
	static ArrayList<Player> players = new ArrayList<Player>();
	static ArrayList<Integer> stacks = new ArrayList<Integer>();
	static ArrayList<Hand> hands = new ArrayList<Hand>();
	static ArrayList<Double> strengths = new ArrayList<Double>();
	static ArrayList<Integer> randomNumbers = new ArrayList<Integer>();
	static Hand board;
	
	static int randomCounter = 0;
	static int num_generations = 40;
	static int num_hands = 600;
	
	public static void main (String args[]) throws IOException {
		
		for (int i=0; i<num_hands; i++) {
			for (int j=0; j<11; j++) {
				randomNumbers.add(rand.nextInt(52-j));
			}
		}
		
		players.add(new Player());
		players.add(new Player());
		players.add(new Player());
		
		PrintWriter fout = new PrintWriter(new BufferedWriter(new FileWriter(new File("field.txt"))));
		/*
		pfraise = 0.44;
		pfbet = 0.42;
		pfcall = 0.38;
		afraise = 0.90;
		afbet = 0.80;
		afcall = 0.70;
		*/
		/*
		for (int i=0; i<10; i++) {
			allPlayers.add(new Player(0.52,0.4,0.42,0.0+0.1*i,0.82,0.78,0.9,0.0+0.1*i));
			avgWin.add(0);
		}*/
		
		//0.5149472117519682,0.3803297553820115,0.372498612584523,0.42349689109118405,0.8171863741192711,0.8048175674843728,0.9155636310746307,0.4139439597719085
		//0.5718216940219379,0.36855955660539585,0.3391981358372262,0.4189518322295907,0.818316738675473,0.8399573854933662,0.8663319173898942,0.37470363098334936
		//allPlayers.add(new Player(0.52,0.44,0.41,0.0,0.82,0.78,0.9,0.0));
		//allPlayers.add(new Player(0.52,0.44,0.41,0.0,0.82,0.78,0.9,0.0));
		allPlayers.add(new Player(0.535479954402045,0.38977670351339894,0.29144978113870645,0.4032774213016445,0.7739414584047954,0.8160732595961975,0.8628702519276038,0.37090542846281516));
		allPlayers.add(new Player(0.535479954402045,0.38977670351339894,0.29144978113870645,0.4032774213016445,0.7739414584047954,0.8160732595961975,0.8628702519276038,0.37090542846281516));
		for (int i=0; i<10; i++) {
			allPlayers.add(new Player(0.535479954402045,0.38977670351339894,0.29144978113870645,0.4032774213016445,0.7739414584047954,0.8160732595961975,0.8628702519276038,0.37090542846281516));
		}
		
		int generation = 0;
		while (generation++ < num_generations) {
			System.out.println("Generation "+generation);
			
			avgWin = new ArrayList<Integer>();
			for (int i=0; i<allPlayers.size(); i++) {
				avgWin.add(0);
			}
			
			for (int i=2; i<allPlayers.size(); i++) {
				playTriplicate(0,1,i);
			}
			
			for (int i=2; i<allPlayers.size()-1; i++) {
				for (int j=i+1; j<allPlayers.size(); j++) {
					if (avgWin.get(i) < avgWin.get(j)) {
						int temp = avgWin.get(i);
						avgWin.set(i,avgWin.get(j));
						avgWin.set(j,temp);
						
						Player ptemp = allPlayers.get(i);
						allPlayers.set(i,allPlayers.get(j));
						allPlayers.set(j,ptemp);
					}
				}
			}
			
			System.out.println("Survivors:");
			double[] avg = new double[8];
			for (int i=7; i<allPlayers.size(); i++) {
				System.out.println(allPlayers.get(i-5));
				allPlayers.set(i,allPlayers.get(i-5).mutation());
				avg[0]+=allPlayers.get(i-5).pfraise;
				avg[1]+=allPlayers.get(i-5).pfbet;
				avg[2]+=allPlayers.get(i-5).pfcall;
				avg[3]+=allPlayers.get(i-5).pfbluff;
				avg[4]+=allPlayers.get(i-5).afraise;
				avg[5]+=allPlayers.get(i-5).afbet;
				avg[6]+=allPlayers.get(i-5).afcall;
				avg[7]+=allPlayers.get(i-5).afbluff;
			}
			fout.println(generation+","+avg[0]/5+","+avg[1]/5+","+avg[2]/5+","+avg[3]/5+","+avg[4]/5+","+avg[5]/5+","+avg[6]/5+","+avg[7]/5);
			
		}
		
		
		/* for specific 2
		
		*/
		
		/* for round-robin
		for (int h=0; h<allPlayers.size()-2; h++) {
			System.out.println("beginning outer iteration "+h);
		for (int j=h+1; j<allPlayers.size()-1; j++) {
			System.out.println("beginning middle iteration "+j);
		for (int k=j+1; k<allPlayers.size(); k++) {
			
			playTriplicate(h,j,k);
			//nextgame
		}
		}
		}*/
		
		for (int i=0; i<allPlayers.size(); i++) {
			fout.println(avgWin.get(i) + " "+allPlayers.get(i));
		}
		
		fout.close();
		
	}
	
	public static void playTriplicate(int h, int j, int k) {
		players.set(0,allPlayers.get(h));
		players.set(1,allPlayers.get(j));
		players.set(2,allPlayers.get(k));
		randomCounter = 0;
		stacks = new ArrayList<Integer>(3);
		stacks.add(1000000);
		stacks.add(1000000);
		stacks.add(1000000);
		play(h,j,k);
		
		
		players.set(0,allPlayers.get(j));
		players.set(0,allPlayers.get(h));
		players.set(0,allPlayers.get(k));
		randomCounter = 0;
		stacks = new ArrayList<Integer>(3);
		stacks.add(1000000);
		stacks.add(1000000);
		stacks.add(1000000);
		play(j,h,k);
		
		
		players.set(0,allPlayers.get(h));
		players.set(1,allPlayers.get(k));
		players.set(2,allPlayers.get(j));
		randomCounter = 0;
		stacks = new ArrayList<Integer>(3);
		stacks.add(1000000);
		stacks.add(1000000);
		stacks.add(1000000);
		play(h,k,j);
		
		
		players.set(0,allPlayers.get(j));
		players.set(1,allPlayers.get(k));
		players.set(2,allPlayers.get(h));
		randomCounter = 0;
		stacks = new ArrayList<Integer>(3);
		stacks.add(1000000);
		stacks.add(1000000);
		stacks.add(1000000);
		play(j,k,h);
		
		
		players.set(0,allPlayers.get(k));
		players.set(1,allPlayers.get(j));
		players.set(2,allPlayers.get(h));
		randomCounter = 0;
		stacks = new ArrayList<Integer>(3);
		stacks.add(1000000);
		stacks.add(1000000);
		stacks.add(1000000);
		play(k,j,h);
		
		
		players.set(0,allPlayers.get(k));
		players.set(1,allPlayers.get(h));
		players.set(2,allPlayers.get(j));
		randomCounter = 0;
		stacks = new ArrayList<Integer>(3);
		stacks.add(1000000);
		stacks.add(1000000);
		stacks.add(1000000);
		play(k,h,j);
	}
	
	public static void play(int h, int j, int k) {
		int round = 0;
		while (round++ < num_hands) {
			shuffle();
			//System.out.println("---------------------------------------------------");
			//System.out.println(stacks);
			
			pot = 0;
			hands = new ArrayList<Hand>(3);
			folded = new ArrayList<Boolean>(3);
			folded.add(false);
			folded.add(false);
			folded.add(false);
			pip = new ArrayList<Integer>(3);
			pip.add(0);
			pip.add(0);
			pip.add(0);
			
			board = new Hand();
			hands.add(new Hand());
			hands.add(new Hand());
			hands.add(new Hand());
			hands.get(0).addCard(takeCard(randomNumbers.get(randomCounter++)));
			hands.get(0).addCard(takeCard(randomNumbers.get(randomCounter++)));
			hands.get(1).addCard(takeCard(randomNumbers.get(randomCounter++)));
			hands.get(1).addCard(takeCard(randomNumbers.get(randomCounter++)));
			hands.get(2).addCard(takeCard(randomNumbers.get(randomCounter++)));
			hands.get(2).addCard(takeCard(randomNumbers.get(randomCounter++)));
			
			strengths = new ArrayList<Double>(3);
			strengths.add(StartingHands.getStrength(hands.get(0)));
			strengths.add(StartingHands.getStrength(hands.get(1)));
			strengths.add(StartingHands.getStrength(hands.get(2)));
			//dealt starting hands.
			
			//System.out.println(hands);
			
			pip.set((round+1)%3,1);
			pip.set((round+2)%3,2);
			
			//preflop
			int toPlay = round%3;
			int bet = 2;
			int numRaises = 0;
			while (pip.get(toPlay) < bet || (bet == 2 && toPlay == (round+2)%3)) {
				if (folded.get(toPlay) == false) {
					String action = players.get(toPlay).bet(strengths.get(toPlay), 0, bet-pip.get(toPlay), numRaises);
					//System.out.println("received action "+action+" from position "+toPlay);
					if ("RAISE".equals(action)) {
						numRaises++;
						bet*=2;
						pip.set(toPlay,bet);
					} else if ("CALL".equals(action)) {
						pip.set(toPlay,bet);
					} else if ("CHECK".equals(action)) {
						//do nothing
					} else if ("FOLD".equals(action)) {
						folded.set(toPlay,true);
						if (countPlayersActive() == 1) {
							bet = -100;
						}
					}
				}
				toPlay=(toPlay+1)%3;
			}
			
			pot+=pip.get(0);
			stacks.set(0,stacks.get(0)-pip.get(0));
			pot+=pip.get(1);
			stacks.set(1,stacks.get(1)-pip.get(1));
			pot+=pip.get(2);
			stacks.set(2,stacks.get(2)-pip.get(2));
			pip.set(0,0);
			pip.set(1,0);
			pip.set(2,0);
			//System.out.println("pot size "+pot);
			//System.out.println("after preflop "+stacks);
			
			if (countPlayersActive() == 1) {
				if (folded.get(0) == false) {
					//System.out.println("Position 0 wins!");
					stacks.set(0,stacks.get(0)+pot);
				} else if (folded.get(1) == false) {
					//System.out.println("Position 1 wins!");
					stacks.set(1,stacks.get(1)+pot);
				} else {
					//System.out.println("Position 2 wins!");
					stacks.set(2,stacks.get(2)+pot);
				}
				while (board.size() < 5) {
					board.addCard(takeCard(randomNumbers.get(randomCounter++)));
				}
				continue;
			}
			
			
			
			//flop
			board.addCard(takeCard(randomNumbers.get(randomCounter++)));
			board.addCard(takeCard(randomNumbers.get(randomCounter++)));
			board.addCard(takeCard(randomNumbers.get(randomCounter++)));
			
			//System.out.println(board);
			
			strengths.set(0,AfterFlop.getRelativeStrength(hands.get(0),board));
			strengths.set(1,AfterFlop.getRelativeStrength(hands.get(1),board));
			strengths.set(2,AfterFlop.getRelativeStrength(hands.get(2),board));
			
			toPlay = (round+1)%3;
			bet = 0;
			numRaises = 0;
			int checks = 0;
			while (pip.get(toPlay) < bet || checks < 3) {
				if (folded.get(toPlay) == false) {
					String action = players.get(toPlay).bet(strengths.get(toPlay), 1, bet-pip.get(toPlay), numRaises);
					//System.out.println("received action "+action+" from position "+toPlay);
					if ("RAISE".equals(action)) {
						numRaises++;
						bet*=2;
						pip.set(toPlay,bet);
					} else if ("CALL".equals(action)) {
						pip.set(toPlay,bet);
					} else if ("BET".equals(action)) {
						bet = pot;
						pip.set(toPlay,bet);
					} else if ("CHECK".equals(action)) {
						//do nothing
					} else if ("FOLD".equals(action)) {
						folded.set(toPlay,true);
						if (countPlayersActive() == 1) {
							bet = -100;
						}
					}
				}
				toPlay=(toPlay+1)%3;
				checks++;
			}
			
			pot+=pip.get(0);
			stacks.set(0,stacks.get(0)-pip.get(0));
			pot+=pip.get(1);
			stacks.set(1,stacks.get(1)-pip.get(1));
			pot+=pip.get(2);
			stacks.set(2,stacks.get(2)-pip.get(2));
			pip.set(0,0);
			pip.set(1,0);
			pip.set(2,0);
			//System.out.println("pot size "+pot);
			//System.out.println("after flop "+stacks);
			
			if (countPlayersActive() == 1) {
				if (folded.get(0) == false) {
					//System.out.println("Position 0 wins!");
					stacks.set(0,stacks.get(0)+pot);
				} else if (folded.get(1) == false) {
					//System.out.println("Position 1 wins!");
					stacks.set(1,stacks.get(1)+pot);
				} else {
					//System.out.println("Position 2 wins!");
					stacks.set(2,stacks.get(2)+pot);
				}
				while (board.size() < 5) {
					board.addCard(takeCard(randomNumbers.get(randomCounter++)));
				}
				continue;
			}
			
			//turn
			board.addCard(takeCard(randomNumbers.get(randomCounter++)));
			
			//System.out.println(board);
			
			strengths.set(0,AfterFlop.getRelativeStrength(hands.get(0),board));
			strengths.set(1,AfterFlop.getRelativeStrength(hands.get(1),board));
			strengths.set(2,AfterFlop.getRelativeStrength(hands.get(2),board));
			
			toPlay = (round+1)%3;
			bet = 0;
			numRaises = 0;
			checks = 0;
			while (pip.get(toPlay) < bet || checks < 3) {
				if (folded.get(toPlay) == false) {
					String action = players.get(toPlay).bet(strengths.get(toPlay), 2, bet-pip.get(toPlay), numRaises);
					//System.out.println("received action "+action+" from position "+toPlay);
					if ("RAISE".equals(action)) {
						numRaises++;
						bet*=2;
						pip.set(toPlay,bet);
					} else if ("CALL".equals(action)) {
						pip.set(toPlay,bet);
					} else if ("BET".equals(action)) {
						bet = pot;
						pip.set(toPlay,bet);
					} else if ("CHECK".equals(action)) {
						//do nothing
					} else if ("FOLD".equals(action)) {
						folded.set(toPlay,true);
						if (countPlayersActive() == 1) {
							bet = -100;
						}
					}
				}
				toPlay=(toPlay+1)%3;
				checks++;
			}
			
			pot+=pip.get(0);
			stacks.set(0,stacks.get(0)-pip.get(0));
			pot+=pip.get(1);
			stacks.set(1,stacks.get(1)-pip.get(1));
			pot+=pip.get(2);
			stacks.set(2,stacks.get(2)-pip.get(2));
			pip.set(0,0);
			pip.set(1,0);
			pip.set(2,0);
			//System.out.println("pot size "+pot);
			//System.out.println("after turn "+stacks);
			
			if (countPlayersActive() == 1) {
				if (folded.get(0) == false) {
					//System.out.println("Position 0 wins!");
					stacks.set(0,stacks.get(0)+pot);
				} else if (folded.get(1) == false) {
					//System.out.println("Position 1 wins!");
					stacks.set(1,stacks.get(1)+pot);
				} else {
					//System.out.println("Position 2 wins!");
					stacks.set(2,stacks.get(2)+pot);
				}
				while (board.size() < 5) {
					board.addCard(takeCard(randomNumbers.get(randomCounter++)));
				}
				continue;
			}
			
			
			//river
			board.addCard(takeCard(randomNumbers.get(randomCounter++)));
			
			//System.out.println(board);
			
			strengths.set(0,AfterFlop.getRelativeStrength(hands.get(0),board));
			strengths.set(1,AfterFlop.getRelativeStrength(hands.get(1),board));
			strengths.set(2,AfterFlop.getRelativeStrength(hands.get(2),board));
			
			toPlay = (round+1)%3;
			bet = 0;
			numRaises = 0;
			checks = 0;
			while (pip.get(toPlay) < bet || checks < 3) {
				if (folded.get(toPlay) == false) {
					String action = players.get(toPlay).bet(strengths.get(toPlay), 3, bet-pip.get(toPlay), numRaises);
					//System.out.println("received action "+action+" from position "+toPlay);
					if ("RAISE".equals(action)) {
						numRaises++;
						bet*=2;
						pip.set(toPlay,bet);
					} else if ("CALL".equals(action)) {
						pip.set(toPlay,bet);
					} else if ("BET".equals(action)) {
						bet = pot;
						pip.set(toPlay,bet);
					} else if ("CHECK".equals(action)) {
						//do nothing
					} else if ("FOLD".equals(action)) {
						folded.set(toPlay,true);
						if (countPlayersActive() == 1) {
							bet = -100;
						}
					}
				}
				toPlay=(toPlay+1)%3;
				checks++;
			}
			
			pot+=pip.get(0);
			stacks.set(0,stacks.get(0)-pip.get(0));
			pot+=pip.get(1);
			stacks.set(1,stacks.get(1)-pip.get(1));
			pot+=pip.get(2);
			stacks.set(2,stacks.get(2)-pip.get(2));
			pip.set(0,0);
			pip.set(1,0);
			pip.set(2,0);
			//System.out.println("pot size "+pot);
			//System.out.println("after river "+stacks);
			
			if (countPlayersActive() == 1) {
				if (folded.get(0) == false) {
					//System.out.println("Position 0 wins!");
					stacks.set(0,stacks.get(0)+pot);
				} else if (folded.get(1) == false) {
					//System.out.println("Position 1 wins!");
					stacks.set(1,stacks.get(1)+pot);
				} else {
					//System.out.println("Position 2 wins!");
					stacks.set(2,stacks.get(2)+pot);
				}
				continue;
			} else {
				//SHOWDOWN!
				for (int i=0; i<3; i++) {
					if (folded.get(i) == false) {
						Hand combined = new Hand();
						for (Card c : hands.get(i).getCards()) {
							combined.addCard(c);
						}
						for (Card c : board.getCards()) {
							combined.addCard(c);
						}
						strengths.set(i,(double)combined.evaluateHand());
					} else {
						strengths.set(i,0.0);
					}
				}
				
				double maxStrength = Math.max(Math.max(strengths.get(0),strengths.get(1)),strengths.get(2));
				
				int numWinners = 0;
				for (int i=0; i<3; i++) {
					if (strengths.get(i) == maxStrength) {
						numWinners++;
					}
				}
				
				int splitPot = pot/numWinners;
				for (int i=0; i<3; i++) {
					if (strengths.get(i) == maxStrength) {
						stacks.set(i,stacks.get(i)+splitPot);
						//System.out.println("Position "+i+" wins "+splitPot);
					}
				}
			}
			
			
		}
		// game is over.
		
		avgWin.set(h,avgWin.get(h)+stacks.get(0)-1000000);
		avgWin.set(j,avgWin.get(j)+stacks.get(1)-1000000);
		avgWin.set(k,avgWin.get(k)+stacks.get(2)-1000000);
	}
	
	public static int countPlayersActive() {
		int active = 0;
		if (folded.get(0) == false) {
			active++;
		}
		if (folded.get(1) == false) {
			active++;
		}
		if (folded.get(2) == false) {
			active++;
		}
		return active;
	}
	
	public static void shuffle() {
		deck = new ArrayList<Card>(52);
		for (int i=0; i<13; i++) {
			deck.add(new Card(Suit.C,i+2));
			deck.add(new Card(Suit.D,i+2));
			deck.add(new Card(Suit.H,i+2));
			deck.add(new Card(Suit.S,i+2));
		}
	}
	
	public static Card dealCard() {
		return deck.remove(rand.nextInt(deck.size()));
	}
	
	public static Card takeCard(int i) {
		return deck.remove(i);
	}
	
}

class Player {
	double pfbluff;
	double pfraise;
	double pfbet;
	double pfcall;
	double afbluff;
	double afraise;
	double afbet;
	double afcall;
	
	Random rand = new Random();
	
	public Player() {
		
		pfraise = 0.44;
		pfbet = 0.42;
		pfcall = 0.38;
		pfbluff = 0.2;
		afraise = 0.90;
		afbet = 0.80;
		afcall = 0.70;
		afbluff = 0.3;
		/*
		pfraise = 0.4;
		pfbet = 0.35;
		pfcall = 0.3;
		afraise = 0.7;
		afbet = 0.6;
		afcall = 0.5;*/
	}
	
	public Player(double pfr, double pfb, double pfc, double pfbf, double afr, double afb, double afc, double afbf) {
		pfraise = pfr;
		pfbet = pfb;
		pfcall = pfc;
		pfbluff = pfbf;
		afraise = afr;
		afbet = afb;
		afcall = afc;
		afbluff = afbf;
	}
	
	//phase from 0 to 3 (123 flop turn river)
	public String bet(double strength, int phase, int toCall, int raises) {
		if (phase == 0) {
			if (toCall > 0) {
				if (raises < 2 && strength > pfraise) {
					return "RAISE";
				} else if (strength > pfcall) {
					return "CALL";
				} else {
					return "FOLD";
				}
			} else {
				if (strength > pfbet) {
					return "RAISE";
				} else {
					return "CHECK";
				}
			}
		} else {
			if (toCall > 0) {
				if (raises < 1 && strength > afraise) {
					return "RAISE";
				} else if (strength > afcall) {
					return "CALL";
				} else {
					return "FOLD";
				}
			} else {
				if (strength > afbet) {
					return "BET";
				} else if (rand.nextDouble() < pfbluff) {
					return "BET";
				} else {
					return "CHECK";
				}
			}
		}
	}
	
	public Player mutation() {
		return new Player(pfraise+rand.nextGaussian()/120,pfbet+rand.nextGaussian()/120,pfcall+rand.nextGaussian()/120,pfbluff+rand.nextGaussian()/120,
		afraise+rand.nextGaussian()/120,afbet+rand.nextGaussian()/120,afcall+rand.nextGaussian()/120,afbluff+rand.nextGaussian()/120);
	}
	
	public String toString() {
		String output = "{";
		output+="pfr"+pfraise;
		output+=",pfb"+pfbet;
		output+=",pfc"+pfcall;
		output+=",pfbf"+pfbluff;
		output+=",afr"+afraise;
		output+=",afb"+afbet;
		output+=",afc"+afcall;
		output+=",afbf"+afbluff;
		output+="}";
		return output;
	}
}