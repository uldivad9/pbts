package pokerbots.q;

class Card {
	Suit suit;
	int value;
	
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
			default:
				value = 0;
				System.out.println("WARNING: INITIALIZING CARD WITH 0 VALUE");
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
			default:
				suit = Suit.NONE;
				System.out.println("WARNING: INITIALIZING CARD WITH NO SUIT");
				break;
		}
	}
	
	public int compareTo(Card c) {
		return value-c.value;
	}
}