package pokerbots;

import java.util.*;

import pokerbots.*;

public class AfterFlop {
	static ArrayList<Card> deck;
	
	public static double getRelativeStrength(Hand hand, Hand board) {
		if (hand.size() != 2 || board.size() < 3) {
			System.out.println("TRYING TO USE getRelativeStrength FOR WRONG HAND/BOARD SIZE!");
			return 0.0;
		}
		
		shuffle();
		for (Card c : hand.getCards()) {
			takeCard(c);
		}
		for (Card c : board.getCards()) {
			takeCard(c);
		}
		
		int totalWon = 0;
		
		//looptime
		for (int i=0; i<deck.size()-1; i++) {
			for (int j=i+1; j<deck.size(); j++) {
				Hand myCombined = new Hand();
				for (Card c : hand.getCards()) {
					myCombined.addCard(c);
				}
				for (Card c : board.getCards()) {
					myCombined.addCard(c);
				}
				
				Hand oppCombined = new Hand();
				oppCombined.addCard(deck.get(i));
				oppCombined.addCard(deck.get(j));
				for (Card c : board.getCards()) {
					oppCombined.addCard(c);
				}
				
				if (myCombined.evaluateHand() >= oppCombined.evaluateHand()) {
					totalWon++;
				}
			}
		}
		
		int totalRounds = (deck.size()*(deck.size()-1))/2;
		return totalWon/(double)totalRounds;
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
	
	public static Card takeCard(Card c) {
		//can be made more efficient.
		for (int i=0; i<deck.size(); i++) {
			if (deck.get(i).equals(c)) {
				deck.remove(i);
				return c;
			}
		}
		System.out.println("TRIED TO TAKE A CARD THAT DOESN'T EXIST!");
		return c;
	}
}