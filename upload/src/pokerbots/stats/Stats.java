package pokerbots.stats;

import pokerbots.*;
import java.util.*;
import java.io.*;

public class Stats {
	static Random rand = new Random();
	static ArrayList<Card> deck;
	static Hand startingHand;
	static Hand[] otherHands;
	static Hand board;
	
	static ArrayList<Hand> startingHandList;
	
	static int totalWon = 0;
	static int numRounds = 100000;
	
	public static void main (String args[]) throws IOException {
		BufferedWriter fout = new BufferedWriter(new FileWriter(new File("startingHands.txt")));
		
		
		initializeStartingHands();
		
		for (int i=0; i<startingHandList.size(); i++) {
			startingHand = startingHandList.get(i);
			totalWon=0;
			for (int round=0; round<numRounds; round++) {
				shuffle();
				takeCard(startingHand.getCards().get(0));
				takeCard(startingHand.getCards().get(1));
				startRound();
			}
			fout.write(startingHand+" "+totalWon/(double)numRounds);
			fout.newLine();
		}
		
		fout.close();
		//System.out.println(totalWon + " rounds won");
		//System.out.println("win percentage: " + totalWon/(double)numRounds);
	}
	
	public static void startRound() {
		board = new Hand();
		//startingHand = new Hand();
		otherHands = new Hand[2];
		otherHands[0] = new Hand();
		otherHands[1] = new Hand();
		
		//startingHand.addCard(dealCard());
		//startingHand.addCard(dealCard());
		
		otherHands[0].addCard(dealCard());
		otherHands[0].addCard(dealCard());
		otherHands[1].addCard(dealCard());
		otherHands[1].addCard(dealCard());
		
		board.addCard(dealCard());
		board.addCard(dealCard());
		board.addCard(dealCard());
		board.addCard(dealCard());
		board.addCard(dealCard());
		
		if (showdown()) {
			totalWon++;
		}
	}
	
	public static Card dealCard() {
		return deck.remove(rand.nextInt(deck.size()));
	}
	
	public static Card takeCard(Card c) {
		for (int i=0; i<deck.size(); i++) {
			if (deck.get(i).equals(c)) {
				deck.remove(i);
				return c;
			}
		}
		System.out.println("TRIED TO TAKE A CARD THAT DOESN'T EXIST!");
		return c;
	}
	
	public static void initializeStartingHands() {
		startingHandList = new ArrayList<Hand>();
		for (int i=14; i>=2; i--) {
			for (int j=i; j>=2; j--) {
				Hand handToAdd = new Hand();
				handToAdd.addCard(new Card(Suit.C,i));
				handToAdd.addCard(new Card(Suit.D,j));
				if (i!=j) {
					Hand secondHTA = new Hand();
					secondHTA.addCard(new Card(Suit.H,i));
					secondHTA.addCard(new Card(Suit.H,j));
					startingHandList.add(secondHTA);
				}
				startingHandList.add(handToAdd);
			}
		}
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
	
	public static boolean showdown() {
		int maxCompetingValue = 0;
		for (int i=0; i<otherHands.length; i++) {
			Hand combined = new Hand();
			for (Card c : board.getCards()) {
				combined.addCard(c);
			}
			for (Card c : otherHands[i].getCards()) {
				combined.addCard(c);
			}
			maxCompetingValue = Math.max(maxCompetingValue,combined.evaluateHand());
		}
		
		Hand myCombined = new Hand();
		for (Card c : board.getCards()) {
			myCombined.addCard(c);
		}
		for (Card c : startingHand.getCards()) {
			myCombined.addCard(c);
		}
		return myCombined.evaluateHand() >= maxCompetingValue;
	}
}
