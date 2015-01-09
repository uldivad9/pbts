package pokerbots.q;

import java.util.*;

public class Hand {
	int[] valueCount;
	int[] suitCount;
	ArrayList<Card> cards;
	
	public Hand() {
		valueCount = new int[14]; //0 and 1 are unused. 2-14 are 2-ace
		suitCount = new int[4];
		cards = new ArrayList<Card>();
	}
	
	public void addCard(Card c) {
		cards.add(c);
		valueCount[c.value]++;
		switch(c.suit) {
			case C:
				suitCount[0]++;
				break;
			case D:
				suitCount[1]++;
				break;
			case H:
				suitCount[2]++;
				break;
			case S:
				suitCount[3]++;
				break;
			default:
				break;
		}
	}
	
	/*
	returns a 8-digit integer representing the hand strength.
	x0000000:
	1 - single
	2 - pair
	3 - two pair
	4 - three of a kind
	5 - straight
	6 - flush
	7 - full house
	8 - four of a kind
	9 - straight flush
	0xx00000:
	strength of the hand type.
	000xxxxx:
	kicker strengths
	*/
	public int evaluateHand() {
		maxRepeat = 0;
		repeatValue = 0;
		for (int i=14; i>=2; i--) {
			if (valueCount[i] > maxRepeat) {
				maxRepeat = valueCount[i];
				repeatValue = i;
			}
		}
		
		if (maxRepeat == 4) {
			return 
		}
	}
	
	//returns the top card of the best straight.
	//returns 0 if there is no straight.
	public int straightValue() {
		int straightcounter = 0;
		for (int i=14; i>=2; i--) {
			if (valueCount[i] > 0) {
				straightcounter++;
			} else {
				straightcounter = 0;
			}
			if (straightcounter == 5) {
				return i;
			}
		}
		if (valueCount[14] > 0) {
			straightcounter++;
		}
		if (straightcounter == 5) {
			return 5;
		}
		return 0;
	}
	
	//returns the top card of the best flush.
	//returns 0 if there is no flush.
	public int flushValue() {
		Suit suit;
		if (suitCount[0] >= 5) {
			suit=Suit.C;
		} else if (suitCount[1] >= 5) {
			suit=Suit.D;
		} else if (suitCount[2] >= 5) {
			suit=Suit.H;
		} else if (suitCount[3] >= 5) {
			suit=Suit.S;
		} else {
			return 0;
		}
		int maxval = 6;
		for (Card card : cards) {
			if (card.suit == suit && card.val > maxval) {
				maxval = card.val;
			}
		}
		return maxval;
	}
}