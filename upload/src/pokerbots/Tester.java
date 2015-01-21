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
		
		PlayerProfile pp = PlayerProfile.parseKeyValue("RB1","190 1494 1013 55 142 433 381 163 118 141 8 118 203 70 35 40 4 25 163 53 29 26 4 14 139 73");
		//String actionstring = "POST:1:Q_TIGHT, POST:2:Q_TIGHT2, RAISE:7:QBOT, FOLD:Q_TIGHT, RAISE:22:Q_TIGHT2, CALL:22:QBOT, DEAL:FLOP, BET:37:Q_TIGHT2, CALL:37:QBOT, DEAL:TURN, BET:97:Q_TIGHT2, CALL:97:QBOT, DEAL:RIVER, CHECK:Q_TIGHT2, BET:126:QBOT, CALL:126:Q_TIGHT2, SHOW:Th:Tc:QBOT, SHOW:Qd:Td:Q_TIGHT2, WIN:565:Q_TIGHT2";
		//String[] actions = actionstring.split(", ");
		
		Scanner scan = new Scanner(System.in);
		
		String line;
		while ((line = scan.nextLine()) != null) {
			String[] actions = line.split(", ");
			String line2;
			int street=0;
			innerloop:
			while((line2 = scan.nextLine()) != null) {
				street = Integer.parseInt(line2);
				break innerloop;
			}
			System.out.println(pp.estimateStrength(actions,street));
		}
		//pp.printRatios();
		
		
		
	}
}