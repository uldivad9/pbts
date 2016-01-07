package pokerbots;

import java.util.*;

import pokerbots.*;

public class Outs {
	//returns a double array.
	//[0.7, 0.75, 0.8, 0.85, 0.9, 0.95]
	
	static ArrayList<Card> deck;
	
	public static double[] getOuts (Hand hand, Hand board) {
		int[] outs = new int[6];
		double[] output = new double[6];
		
		if (hand.size() != 2 || board.size() < 3) {
			System.out.println("TRYING TO USE getRelativeStrength FOR WRONG HAND/BOARD SIZE!");
			return output;
		}
		
		shuffle();
		for (Card c : hand.getCards()) {
			takeCard(c);
		}
		for (Card c : board.getCards()) {
			takeCard(c);
		}
		
		int totalWon = 0;
		
		for (int i=0; i<deck.size(); i++) {
			Hand newBoard = new Hand();
			for (Card c : board.getCards()) {
				newBoard.addCard(c);
			}
			
			newBoard.addCard(deck.get(i));
			
			double relativeStrength = AfterFlop.getRelativeStrength(hand,newBoard);
			if (relativeStrength < 0.7) {
				continue;
			} else if (relativeStrength >= 0.95) {
				outs[0]++;
				outs[1]++;
				outs[2]++;
				outs[3]++;
				outs[4]++;
				outs[5]++;
			} else if (relativeStrength >= 0.9) {
				outs[0]++;
				outs[1]++;
				outs[2]++;
				outs[3]++;
				outs[4]++;
			} else if (relativeStrength >= 0.85) {
				outs[0]++;
				outs[1]++;
				outs[2]++;
				outs[3]++;
			} else if (relativeStrength >= 0.8) {
				outs[0]++;
				outs[1]++;
				outs[2]++;
			} else if (relativeStrength >= 0.75) {
				outs[0]++;
				outs[1]++;
			} else if (relativeStrength >= 0.7) {
				outs[0]++;
			}
		}
		
		for (int i=0; i<outs.length; i++) {
			output[i] = outs[i]/(double)deck.size();
		}
		return output;
	}
	
	public static double getOuts (Hand hand, Hand board, double threshold) {
		int outs = 0;
		
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
		
		for (int i=0; i<deck.size(); i++) {
			Hand newBoard = new Hand();
			for (Card c : board.getCards()) {
				newBoard.addCard(c);
			}
			
			newBoard.addCard(deck.get(i));
			
			double relativeStrength = AfterFlop.getRelativeStrength(hand,newBoard);
			if (relativeStrength >= threshold) {
				outs++;
			}
		}
		
		return outs/(double)deck.size();
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