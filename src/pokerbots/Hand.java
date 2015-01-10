package pokerbots;

import java.util.*;

import pokerbots.*;

public class Hand {
	int[] valueCount;
	int[] suitCount;
	ArrayList<Card> cards; //sorted by value
	
	public Hand() {
		valueCount = new int[15]; //0 and 1 are unused. 2-14 are 2-ace
		suitCount = new int[4];
		cards = new ArrayList<Card>();
	}
	
	public Hand(String cardString) {
		valueCount = new int[15];
		suitCount = new int[4];
		cards = new ArrayList<Card>();
		String[] cardStrings = cardString.split(" ");
		for (int i=0; i<cardStrings.length; i++) {
			addCard(new Card(cardStrings[i]));
		}
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
		//Collections.sort(cards); //is this efficient?
	}
	
	public void addCard(String stringRep) {
		addCard(new Card(stringRep));
	}
	
	public int size() {
		return cards.size();
	}
	
	public ArrayList<Card> getCards() {
		return cards;
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
		int maxRepeat = 0;
		int repeatValue = 0;
		for (int i=14; i>=2; i--) {
			if (valueCount[i] > maxRepeat) {
				maxRepeat = valueCount[i];
				repeatValue = i;
			}
		}
		
		if (maxRepeat == 4) {
			for (int i=14; i>=2; i--) {
				if (valueCount[i] > 0 && i!=repeatValue) {
					return 80000000+repeatValue*100000+i;
				}
			}
		}
			
		if (maxRepeat == 3) {
			//check full house
			for (int i=14; i>=2; i--) {
				if (valueCount[i] >= 2 && i != repeatValue) {
					//full house
					return 70000000+repeatValue*100000+i;
				}
			}
			//no full house; save triple for later
		}
		
		int flushVal = flushValue();
		if (flushVal != 0) {
			return flushVal;
		}
		
		int straightVal = straightValue();
		if (straightVal != 0) {
			return 50000000+straightVal*100000;
		}
		
		if (maxRepeat == 3) {
			//find kickers
			int[] kickers = new int[2];
			int index=14;
			int numFound=0;
			while (numFound < 2) {
				if (valueCount[index] > 0 && index != repeatValue) {
					kickers[numFound++] = index;
				}
				index--;
			}
			return 40000000+repeatValue*100000+kickers[0]*15+kickers[1];
		}
		
		if (maxRepeat == 2) {
			//check for two pair
			int secondRepeatValue = 0;
			for (int i=14; i>=2; i--) {
				if (valueCount[i] >= 2 && i != repeatValue) {
					//two pair
					secondRepeatValue = i;
					i = 0;
				}
			}
			if (secondRepeatValue != 0) {
				//find kicker
				for (int i=14; i>=0; i--) {
					if (valueCount[i] >= 1 && i != repeatValue && i != secondRepeatValue) {
						return 30000000+repeatValue*100000+secondRepeatValue*1000+i;
					}
				}
			}
			
			//one pair
			int[] kickers = new int[3];
			int index=14;
			int numFound=0;
			while (numFound < 3) {
				if (valueCount[index] > 0 && index != repeatValue) {
					kickers[numFound++] = index;
				}
				index--;
			}
			return 20000000+repeatValue*100000+kickers[0]*225+kickers[1]*15+kickers[2];
		}
		
		//nothing
		int[] kickers = new int[4];
		int index=14;
		int numFound=0;
		while (numFound < 4) {
			if (valueCount[index] > 0 && index != repeatValue) {
				kickers[numFound++] = index;
			}
			index--;
		}
		return 10000000+repeatValue*100000+kickers[0]*3375+kickers[1]*225+kickers[2]*15+kickers[3];
	}
	
	//returns the bottom card of the best straight.
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
				return i+4;
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
	
	//returns the value of the best flush.
	//returns 0 if there is no flush.
	public int flushValue() {
		Suit suit;
		int[] allSuitValues;
		if (suitCount[0] >= 5) {
			suit=Suit.C;
			allSuitValues = new int[suitCount[0]];
		} else if (suitCount[1] >= 5) {
			suit=Suit.D;
			allSuitValues = new int[suitCount[1]];
		} else if (suitCount[2] >= 5) {
			suit=Suit.H;
			allSuitValues = new int[suitCount[2]];
		} else if (suitCount[3] >= 5) {
			suit=Suit.S;
			allSuitValues = new int[suitCount[3]];
		} else {
			return 0;
		}
		
		int placeIndex=0;
		for (Card card : cards) {
			if (card.suit == suit) {
				allSuitValues[placeIndex++] = card.value;
			}
		}
		
		Arrays.sort(allSuitValues);
		
		return 60000000+allSuitValues[allSuitValues.length-1]*100000+allSuitValues[allSuitValues.length-2]*3375+allSuitValues[allSuitValues.length-3]*225+allSuitValues[allSuitValues.length-4]*15+allSuitValues[allSuitValues.length-5];
	}
	
	public String toString() {
		String output = "{";
		for (int i=0; i<cards.size(); i++) {
			output+=cards.get(i).toString();
			output+=',';
		}
		output+='}';
		return output;
	}
}