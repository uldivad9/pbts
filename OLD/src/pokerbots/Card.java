package pokerbots;

import pokerbots.Suit;

public class Card {
	public final Suit suit;
	public final int value;
	
	public Card(Suit s, int v) {
		suit = s;
		value = v;
	}
	
	public Card(String cardrep) {
		switch(cardrep.charAt(0)) {
			case '2':
				value = 2;
				break;
			case '3':
				value = 3;
				break;
			case '4':
				value = 4;
				break;
			case '5':
				value = 5;
				break;
			case '6':
				value = 6;
				break;
			case '7':
				value = 7;
				break;
			case '8':
				value = 8;
				break;
			case '9':
				value = 9;
				break;
			case 'T':
				value = 10;
				break;
			case 'J':
				value = 11;
				break;
			case 'Q':
				value = 12;
				break;
			case 'K':
				value = 13;
				break;
			case 'A':
				value = 14;
				break;
			case '1': //only for 'blank' cards
				value = 1;
				break;
			case '0': //only for 'blank' cards
				value = 0;
				break;
			default:
				value = 0;
				System.out.println("WARNING: INITIALIZING CARD WITH NO VALUE");
				break;
		}
		
		switch (cardrep.charAt(1)) {
			case 'c':
				suit = Suit.C;
				break;
			case 'd':
				suit = Suit.D;
				break;
			case 'h':
				suit = Suit.H;
				break;
			case 's':
				suit = Suit.S;
				break;
			case 'n': //only for 'blank' cards
				suit = Suit.NONE;
				break;
			default:
				suit = Suit.NONE;
				System.out.println("WARNING: INITIALIZING CARD WITH NO SUIT");
				break;
		}
	}
	
	public int compareTo(Card c) {
		return value-c.value;
	}
	
	public String toString() {
		String output = "";
		switch(value) {
			case 2:
				output+='2';
				break;
			case 3:
				output+='3';
				break;
			case 4:
				output+='4';
				break;
			case 5:
				output+='5';
				break;
			case 6:
				output+='6';
				break;
			case 7:
				output+='7';
				break;
			case 8:
				output+='8';
				break;
			case 9:
				output+='9';
				break;
			case 10:
				output+='T';
				break;
			case 11:
				output+='J';
				break;
			case 12:
				output+='Q';
				break;
			case 13:
				output+='K';
				break;
			case 14:
				output+='A';
				break;
			default:
				break;
		}
		switch(suit) {
			case C:
				output+='c';
				break;
			case D:
				output+='d';
				break;
			case H:
				output+='h';
				break;
			case S:
				output+='s';
				break;
			default:
				break;
		}
		return output;
	}
	
	public boolean equals(Card c) {
		return (c.value == value && c.suit == suit);
	}
}