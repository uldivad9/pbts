package pokerbots;

import pokerbots.*;

import java.util.*;
import java.io.*;
/*
For reference:
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

public class Tester {
	public static void main (String args[]) {
		/*
		PlayerProfile pp = new PlayerProfile("QBOT");
		//String actionstring = "POST:1:Q_TIGHT, POST:2:Q_TIGHT2, RAISE:7:QBOT, FOLD:Q_TIGHT, RAISE:22:Q_TIGHT2, CALL:22:QBOT, DEAL:FLOP, BET:37:Q_TIGHT2, CALL:37:QBOT, DEAL:TURN, BET:97:Q_TIGHT2, CALL:97:QBOT, DEAL:RIVER, CHECK:Q_TIGHT2, BET:126:QBOT, CALL:126:Q_TIGHT2, SHOW:Th:Tc:QBOT, SHOW:Qd:Td:Q_TIGHT2, WIN:565:Q_TIGHT2";
		//String[] actions = actionstring.split(", ");
		
		Scanner scan = new Scanner(System.in);
		
		String line;
		while ((line = scan.nextLine()) != null) {
			String[] actions = line.split(", ");
			pp.updateProfileAfterHand(actions);
			pp.printInfo();
		}*/
		
		PlayerProfile POTRIPPER = PlayerProfile.parseKeyValue("POTRIPPER","4020 1964 320 1336 720 180 237 13 208 330 36 31 5 0 294 21 55 7 33 240 108");
	}
}