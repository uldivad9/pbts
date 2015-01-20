package pokerbots.stats;

import java.io.*;
import java.util.*;

import pokerbots.*;

public class PlayerSim {
	
	
	static ArrayList<Card> deck;
	static ArrayList<Integer> pip = new ArrayList<Integer>();
	static Random rand = new Random();
	static ArrayList<Boolean> folded = new ArrayList<Boolean>();
	
	public static void main (String args[]) {
		int pot;
		ArrayList<Player> players = new ArrayList<Player>();
		ArrayList<Integer> stacks = new ArrayList<Integer>();
		ArrayList<Hand> hands = new ArrayList<Hand>();
		ArrayList<Double> strengths = new ArrayList<Double>();
		Hand board;
		
		players.add(new Player());
		players.add(new Player());
		players.add(new Player());
		stacks = new ArrayList<Integer>(3);
		stacks.add(100000);
		stacks.add(100000);
		stacks.add(100000);
		
		int round = 0;
		while (round++ < 1) {
			shuffle();
			
			System.out.println(stacks);
			
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
			hands.get(0).addCard(dealCard());
			hands.get(0).addCard(dealCard());
			hands.get(1).addCard(dealCard());
			hands.get(1).addCard(dealCard());
			hands.get(2).addCard(dealCard());
			hands.get(2).addCard(dealCard());
			
			strengths = new ArrayList<Double>(3);
			strengths.add(StartingHands.getStrength(hands.get(0)));
			strengths.add(StartingHands.getStrength(hands.get(1)));
			strengths.add(StartingHands.getStrength(hands.get(2)));
			//dealt starting hands.
			
			pip.set((round+1)%3,1);
			pip.set((round+2)%3,2);
			
			//preflop
			int toPlay = round;
			int bet = 2;
			int numRaises = 0;
			while (pip.get(toPlay) < bet || (bet == 2 && toPlay == (round+2)%3)) {
				if (folded.get(toPlay) == false) {
					String action = players.get(toPlay).bet(strengths.get(toPlay), 0, bet-pip.get(toPlay), numRaises);
					System.out.println("received action "+action+" from position "+toPlay);
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
			System.out.println("pot size "+pot);
			System.out.println("after preflop "+stacks);
			
			if (countPlayersActive() == 1) {
				if (folded.get(0) == false) {
					System.out.println("Position 0 wins!");
					stacks.set(0,stacks.get(0)+pot);
				} else if (folded.get(1) == false) {
					System.out.println("Position 1 wins!");
					stacks.set(1,stacks.get(1)+pot);
				} else {
					System.out.println("Position 2 wins!");
					stacks.set(2,stacks.get(2)+pot);
				}
				continue;
			}
			
			
			
			//flop
			board.addCard(dealCard());
			board.addCard(dealCard());
			board.addCard(dealCard());
			
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
					System.out.println("received action "+action+" from position "+toPlay);
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
			System.out.println("pot size "+pot);
			System.out.println("after flop "+stacks);
			
			if (countPlayersActive() == 1) {
				if (folded.get(0) == false) {
					System.out.println("Position 0 wins!");
					stacks.set(0,stacks.get(0)+pot);
				} else if (folded.get(1) == false) {
					System.out.println("Position 1 wins!");
					stacks.set(1,stacks.get(1)+pot);
				} else {
					System.out.println("Position 2 wins!");
					stacks.set(2,stacks.get(2)+pot);
				}
				continue;
			}
			
			//turn
			board.addCard(dealCard());
			
			strengths.set(0,AfterFlop.getRelativeStrength(hands.get(0),board));
			strengths.set(1,AfterFlop.getRelativeStrength(hands.get(1),board));
			strengths.set(2,AfterFlop.getRelativeStrength(hands.get(2),board));
			
			toPlay = (round+1)%3;
			bet = 0;
			numRaises = 0;
			int checks = 0;
			while (pip.get(toPlay) < bet || checks < 3) {
				if (folded.get(toPlay) == false) {
					String action = players.get(toPlay).bet(strengths.get(toPlay), 2, bet-pip.get(toPlay), numRaises);
					System.out.println("received action "+action+" from position "+toPlay);
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
			System.out.println("pot size "+pot);
			System.out.println("after turn "+stacks);
			
			if (countPlayersActive() == 1) {
				if (folded.get(0) == false) {
					System.out.println("Position 0 wins!");
					stacks.set(0,stacks.get(0)+pot);
				} else if (folded.get(1) == false) {
					System.out.println("Position 1 wins!");
					stacks.set(1,stacks.get(1)+pot);
				} else {
					System.out.println("Position 2 wins!");
					stacks.set(2,stacks.get(2)+pot);
				}
				continue;
			}
			
			
			//river
			board.addCard(dealCard());
			
			strengths.set(0,AfterFlop.getRelativeStrength(hands.get(0),board));
			strengths.set(1,AfterFlop.getRelativeStrength(hands.get(1),board));
			strengths.set(2,AfterFlop.getRelativeStrength(hands.get(2),board));
			
			toPlay = (round+1)%3;
			bet = 0;
			numRaises = 0;
			int checks = 0;
			while (pip.get(toPlay) < bet || checks < 3) {
				if (folded.get(toPlay) == false) {
					String action = players.get(toPlay).bet(strengths.get(toPlay), 3, bet-pip.get(toPlay), numRaises);
					System.out.println("received action "+action+" from position "+toPlay);
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
			System.out.println("pot size "+pot);
			System.out.println("after river "+stacks);
			
			if (countPlayersActive() == 1) {
				if (folded.get(0) == false) {
					System.out.println("Position 0 wins!");
					stacks.set(0,stacks.get(0)+pot);
				} else if (folded.get(1) == false) {
					System.out.println("Position 1 wins!");
					stacks.set(1,stacks.get(1)+pot);
				} else {
					System.out.println("Position 2 wins!");
					stacks.set(2,stacks.get(2)+pot);
				}
				continue;
			}
			
			
		}
		// game is over.
		
		
		
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
	
}

class Player {
	double pfraise = 0.456545447759851;
	double pfbet = 0.42555048286957056;
	double pfcall = 0.4085323492092885;
	double afraise = 0.9186514688178289;
	double afbet = 0.8088379785584023;
	double afcall = 0.7163182823913401;
	
	Random rand = new Random();
	
	public Player() {
	
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
				} else {
					return "CHECK";
				}
			}
		}
	}
	
	public void mutate() {
		pfraise+=rand.nextGaussian()/30;
		pfbet+=rand.nextGaussian()/30;
		pfcall+=rand.nextGaussian()/30;
		afraise+=rand.nextGaussian()/30;
		afraise+=rand.nextGaussian()/30;
		afraise+=rand.nextGaussian()/30;
	}
}