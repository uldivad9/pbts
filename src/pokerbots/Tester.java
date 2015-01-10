package pokerbots;

import pokerbots.*;

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
		Hand hand = null;
		/*
		hand = new Hand("Ks 7s Kc Kd Kh Qs Ad");
		System.out.println(hand.evaluateHand());
		hand = new Hand("8d 9d Td Qd Tc Ts Th");
		System.out.println(hand.evaluateHand());
		hand = new Hand("Qs Qc 2d 2c Qc Ac 7d");
		System.out.println(hand.evaluateHand());
		hand = new Hand("2c 2s As Td 2h 5c Ad");
		System.out.println(hand.evaluateHand());
		hand = new Hand("Qs 7s 4s Qc 2s 3c As");
		System.out.println(hand.evaluateHand());
		hand = new Hand("Ac 5c Tc Jc 2c 7c 9c");
		System.out.println(hand.evaluateHand());
		hand = new Hand("4s 6c 8d 7d 5c Kd 2h");
		System.out.println(hand.evaluateHand());
		hand = new Hand("Ac 2c 7d Tc 4d 5h 3s");
		System.out.println(hand.evaluateHand());
		hand = new Hand("Ac Kd Kc Ks Ts Qd 8c");
		System.out.println(hand.evaluateHand());
		hand = new Hand("Ts 6c 7c Th 9d Kd Td");
		System.out.println(hand.evaluateHand());
		hand = new Hand("Jc 2d Ac Js Ah 3d Td");
		System.out.println(hand.evaluateHand());
		hand = new Hand("7c Td Jd Js 5s Kh Kd");
		System.out.println(hand.evaluateHand());
		hand = new Hand("5c 8d Tc Qh Td Ah 3c");
		System.out.println(hand.evaluateHand());
		hand = new Hand("7s 6c 6h As Kc Qc 2d");
		System.out.println(hand.evaluateHand());
		hand = new Hand("4c 5s 8d Kc Th Qc 2d");
		System.out.println(hand.evaluateHand());
		hand = new Hand("2h 3s 6d 9d Tc Js Kd");
		System.out.println(hand.evaluateHand());
		*/
		
		hand = new Hand("4s 3c 2d 1n 0n");
		System.out.println(hand.evaluateHand());
	}
}