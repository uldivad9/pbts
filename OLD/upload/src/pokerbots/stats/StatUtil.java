package pokerbots.stats;

import pokerbots.*;
import java.util.*;
import java.io.*;

public class StatUtil {
	public static void main (String args[]) throws IOException {
		BufferedReader fin = new BufferedReader(new FileReader(new File("startingHands_100000.txt")));
		ArrayList<StartingHandStat> stats = new ArrayList<StartingHandStat>();
		String next;
		while ((next=fin.readLine()) != null) {
			Hand nextHand = new Hand();
			nextHand.addCard(next.substring(1,3));
			nextHand.addCard(next.substring(4,6));
			stats.add(new StartingHandStat(nextHand, Double.parseDouble(next.substring(9,next.length()))));
		}
		
		Collections.sort(stats);
		BufferedWriter fout = new BufferedWriter(new FileWriter(new File("startingHands.txt")));
		for (int i=stats.size()-1; i>=0; i--) {
			Card c1 = stats.get(i).hand.getCards().get(0);
			Card c2 = stats.get(i).hand.getCards().get(1);
			if (c1.suit == c2.suit) {
				fout.write(c1.value + " " + c2.value + " s " + stats.get(i).winrate);
			} else {
				fout.write(c1.value + " " + c2.value + " o " + stats.get(i).winrate);
			}
			fout.newLine();
		}
		fout.close();
	}
}

class StartingHandStat implements Comparable {
	Hand hand;
	double winrate;
	
	public StartingHandStat(Hand h, double d) {
		hand = h;
		winrate = d;
	}
	
	public int compareTo(Object o) {
		StartingHandStat s = (StartingHandStat)o;
		if (winrate > s.winrate) {
			return 1;
		} else if (winrate < s.winrate) {
			return -1;
		} else {
			return 0;
		}
	}
}