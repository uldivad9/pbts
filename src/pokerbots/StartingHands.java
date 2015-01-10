package pokerbots;

import java.util.*;
import java.io.*;

import pokerbots.*;

public class StartingHands {
	
	public static Double getStrength(Hand h) {
		if (h.size() != 2) {
			System.out.println("TRYING TO USE getStrength FOR WRONG HAND SIZE!");
			return 0.0;
		}
		
		Card first = h.getCards().get(0);
		Card second = h.getCards().get(1);
		if (first.suit == second.suit) {
			//prepare for wreck
			switch(first.value) {
				case 2:
					switch(second.value) {
						case 2:	return 0.31181;
						case 3: return 0.25781;
						case 4: return 0.26332;
						case 5: return 0.27475;
						case 6: return 0.26704;
						case 7: return 0.26344;
						case 8: return 0.27815;
						case 9: return 0.289;
						case 10: return 0.30536;
						case 11: return 0.32143;
						case 12: return 0.34423;
						case 13: return 0.37154;
						case 14: return 0.40847;
					}
				case 3:
					switch(second.value) {
						case 2:	return 0.25781;
						case 3: return 0.34078;
						case 4: return 0.28325;
						case 5: return 0.29064;
						case 6: return 0.28383;
						case 7: return 0.28474;
						case 8: return 0.2821;
						case 9: return 0.30095;
						case 10: return 0.31563;
						case 11: return 0.3303;
						case 12: return 0.35417;
						case 13: return 0.37944;
						case 14: return 0.41724;
					}
				case 4:
					switch(second.value) {
						case 2:	return 0.26332;
						case 3: return 0.28325;
						case 4: return 0.37339;
						case 5: return 0.30744;
						case 6: return 0.30438;
						case 7: return 0.30266;
						case 8: return 0.30094;
						case 9: return 0.30445;
						case 10: return 0.31966;
						case 11: return 0.34012;
						case 12: return 0.36045;
						case 13: return 0.38445;
						case 14: return 0.42682;
					}
				case 5:
					switch(second.value) {
						case 2:	return 0.27475;
						case 3: return 0.29064;
						case 4: return 0.30744;
						case 5: return 0.40592;
						case 6: return 0.32193;
						case 7: return 0.32191;
						case 8: return 0.32024;
						case 9: return 0.32057;
						case 10: return 0.32862;
						case 11: return 0.34894;
						case 12: return 0.37143;
						case 13: return 0.39577;
						case 14: return 0.43887;
					}
				case 6:
					switch(second.value) {
						case 2:	return 0.26704;
						case 3: return 0.28383;
						case 4: return 0.30438;
						case 5: return 0.32193;
						case 6: return 0.4381;
						case 7: return 0.33946;
						case 8: return 0.34007;
						case 9: return 0.34126;
						case 10: return 0.34539;
						case 11: return 0.35327;
						case 12: return 0.37581;
						case 13: return 0.4028;
						case 14: return 0.4325;
					}
				case 7:
					switch(second.value) {
						case 2:	return 0.26344;
						case 3: return 0.28474;
						case 4: return 0.30266;
						case 5: return 0.32191;
						case 6: return 0.33946;
						case 7: return 0.46835;
						case 8: return 0.35652;
						case 9: return 0.36074;
						case 10: return 0.3671;
						case 11: return 0.37469;
						case 12: return 0.38153;
						case 13: return 0.41194;
						case 14: return 0.44629;
					}
				case 8:
					switch(second.value) {
						case 2:	return 0.27815;
						case 3: return 0.2821;
						case 4: return 0.30094;
						case 5: return 0.32024;
						case 6: return 0.34007;
						case 7: return 0.35652;
						case 8: return 0.50239;
						case 9: return 0.37766;
						case 10: return 0.38466;
						case 11: return 0.39146;
						case 12: return 0.39969;
						case 13: return 0.41929;
						case 14: return 0.45238;
					}
				case 9:
					switch(second.value) {
						case 2:	return 0.289;
						case 3: return 0.30095;
						case 4: return 0.30445;
						case 5: return 0.32057;
						case 6: return 0.34126;
						case 7: return 0.36074;
						case 8: return 0.37766;
						case 9: return 0.54185;
						case 10: return 0.40423;
						case 11: return 0.41159;
						case 12: return 0.42233;
						case 13: return 0.43955;
						case 14: return 0.46296;
					}
				case 10:
					switch(second.value) {
						case 2:	return 0.30536;
						case 3: return 0.31563;
						case 4: return 0.31966;
						case 5: return 0.32862;
						case 6: return 0.34539;
						case 7: return 0.3671;
						case 8: return 0.38466;
						case 9: return 0.40423;
						case 10: return 0.57894;
						case 11: return 0.43456;
						case 12: return 0.4472;
						case 13: return 0.4629;
						case 14: return 0.48627;
					}
				case 11:
					switch(second.value) {
						case 2:	return 0.32143;
						case 3: return 0.3303;
						case 4: return 0.34012;
						case 5: return 0.34894;
						case 6: return 0.35327;
						case 7: return 0.37469;
						case 8: return 0.39146;
						case 9: return 0.41159;
						case 10: return 0.43456;
						case 11: return 0.61453;
						case 12: return 0.45721;
						case 13: return 0.47423;
						case 14: return 0.49411;
					}
				case 12:
					switch(second.value) {
						case 2:	return 0.34423;
						case 3: return 0.35417;
						case 4: return 0.36045;
						case 5: return 0.37143;
						case 6: return 0.37581;
						case 7: return 0.38153;
						case 8: return 0.39969;
						case 9: return 0.42233;
						case 10: return 0.4472;
						case 11: return 0.45721;
						case 12: return 0.65078;
						case 13: return 0.48236;
						case 14: return 0.50602;
					}
				case 13:
					switch(second.value) {
						case 2:	return 0.37154;
						case 3: return 0.37944;
						case 4: return 0.38445;
						case 5: return 0.39577;
						case 6: return 0.4028;
						case 7: return 0.41194;
						case 8: return 0.41929;
						case 9: return 0.43955;
						case 10: return 0.4629;
						case 11: return 0.47423;
						case 12: return 0.48236;
						case 13: return 0.69361;
						case 14: return 0.52027;
					}
				case 14:
					switch(second.value) {
						case 2:	return 0.40847;
						case 3: return 0.41724;
						case 4: return 0.42682;
						case 5: return 0.43887;
						case 6: return 0.4325;
						case 7: return 0.44629;
						case 8: return 0.45238;
						case 9: return 0.46296;
						case 10: return 0.48627;
						case 11: return 0.49411;
						case 12: return 0.50602;
						case 13: return 0.52027;
						case 14: return 0.73786;
					}
				default:
					System.out.println("SOMETHING BAD HAPPENED IN STARTING HAND STRENGTH CALCULATION");
					return 0.0;
			}
			
		} else {
			//wreck #2
			switch(first.value) {
				case 2:
					switch(second.value) {
						case 2:	return 0.31181;
						case 3: return 0.21691;
						case 4: return 0.22671;
						case 5: return 0.23275;
						case 6: return 0.22795;
						case 7: return 0.2249;
						case 8: return 0.23989;
						case 9: return 0.25259;
						case 10: return 0.2694;
						case 11: return 0.28486;
						case 12: return 0.30701;
						case 13: return 0.33757;
						case 14: return 0.3742;
					}
				case 3:
					switch(second.value) {
						case 2:	return 0.21691;
						case 3: return 0.34078;
						case 4: return 0.24438;
						case 5: return 0.25611;
						case 6: return 0.24675;
						case 7: return 0.24471;
						case 8: return 0.2418;
						case 9: return 0.2596;
						case 10: return 0.27556;
						case 11: return 0.29137;
						case 12: return 0.3155;
						case 13: return 0.34139;
						case 14: return 0.38378;
					}
				case 4:
					switch(second.value) {
						case 2:	return 0.22671;
						case 3: return 0.24438;
						case 4: return 0.37339;
						case 5: return 0.27292;
						case 6: return 0.26909;
						case 7: return 0.26598;
						case 8: return 0.26601;
						case 9: return 0.26746;
						case 10: return 0.28373;
						case 11: return 0.30221;
						case 12: return 0.32623;
						case 13: return 0.35243;
						case 14: return 0.39593;
					}
				case 5:
					switch(second.value) {
						case 2:	return 0.23275;
						case 3: return 0.25611;
						case 4: return 0.27292;
						case 5: return 0.40592;
						case 6: return 0.28491;
						case 7: return 0.2859;
						case 8: return 0.28499;
						case 9: return 0.28614;
						case 10: return 0.29088;
						case 11: return 0.30993;
						case 12: return 0.33475;
						case 13: return 0.36302;
						case 14: return 0.40445;
					}
				case 6:
					switch(second.value) {
						case 2:	return 0.22795;
						case 3: return 0.24675;
						case 4: return 0.26909;
						case 5: return 0.28491;
						case 6: return 0.4381;
						case 7: return 0.30368;
						case 8: return 0.30266;
						case 9: return 0.30402;
						case 10: return 0.3118;
						case 11: return 0.31872;
						case 12: return 0.34455;
						case 13: return 0.36842;
						case 14: return 0.40092;
					}
				case 7:
					switch(second.value) {
						case 2:	return 0.2249;
						case 3: return 0.24471;
						case 4: return 0.26598;
						case 5: return 0.2859;
						case 6: return 0.30368;
						case 7: return 0.46835;
						case 8: return 0.32328;
						case 9: return 0.32303;
						case 10: return 0.33573;
						case 11: return 0.33408;
						case 12: return 0.35124;
						case 13: return 0.38062;
						case 14: return 0.41358;
					}
				case 8:
					switch(second.value) {
						case 2:	return 0.23989;
						case 3: return 0.2418;
						case 4: return 0.26601;
						case 5: return 0.28499;
						case 6: return 0.30266;
						case 7: return 0.32328;
						case 8: return 0.50239;
						case 9: return 0.34638;
						case 10: return 0.35371;
						case 11: return 0.36198;
						case 12: return 0.37379;
						case 13: return 0.38843;
						case 14: return 0.42166;
					}
				case 9:
					switch(second.value) {
						case 2:	return 0.25259;
						case 3: return 0.2596;
						case 4: return 0.26746;
						case 5: return 0.28614;
						case 6: return 0.30402;
						case 7: return 0.32303;
						case 8: return 0.34638;
						case 9: return 0.54185;
						case 10: return 0.37557;
						case 11: return 0.3798;
						case 12: return 0.39099;
						case 13: return 0.40861;
						case 14: return 0.43227;
					}
				case 10:
					switch(second.value) {
						case 2:	return 0.2694;
						case 3: return 0.27556;
						case 4: return 0.28373;
						case 5: return 0.29088;
						case 6: return 0.3118;
						case 7: return 0.33573;
						case 8: return 0.35371;
						case 9: return 0.37557;
						case 10: return 0.57894;
						case 11: return 0.40665;
						case 12: return 0.41772;
						case 13: return 0.43471;
						case 14: return 0.45536;
					}
				case 11:
					switch(second.value) {
						case 2:	return 0.28486;
						case 3: return 0.29137;
						case 4: return 0.30221;
						case 5: return 0.30993;
						case 6: return 0.31872;
						case 7: return 0.33408;
						case 8: return 0.36198;
						case 9: return 0.3798;
						case 10: return 0.40665;
						case 11: return 0.61453;
						case 12: return 0.42708;
						case 13: return 0.4459;
						case 14: return 0.46742;
					}
				case 12:
					switch(second.value) {
						case 2:	return 0.30701;
						case 3: return 0.3155;
						case 4: return 0.32623;
						case 5: return 0.33475;
						case 6: return 0.34455;
						case 7: return 0.35124;
						case 8: return 0.37379;
						case 9: return 0.39099;
						case 10: return 0.41772;
						case 11: return 0.42708;
						case 12: return 0.65078;
						case 13: return 0.45573;
						case 14: return 0.4808;
					}
				case 13:
					switch(second.value) {
						case 2:	return 0.33757;
						case 3: return 0.34139;
						case 4: return 0.35243;
						case 5: return 0.36302;
						case 6: return 0.36842;
						case 7: return 0.38062;
						case 8: return 0.38843;
						case 9: return 0.40861;
						case 10: return 0.43471;
						case 11: return 0.4459;
						case 12: return 0.45573;
						case 13: return 0.69361;
						case 14: return 0.49371;
					}
				case 14:
					switch(second.value) {
						case 2:	return 0.3742;
						case 3: return 0.38378;
						case 4: return 0.39593;
						case 5: return 0.40445;
						case 6: return 0.40092;
						case 7: return 0.41358;
						case 8: return 0.42166;
						case 9: return 0.43227;
						case 10: return 0.45536;
						case 11: return 0.46742;
						case 12: return 0.4808;
						case 13: return 0.49371;
						case 14: return 0.73786;
					}
				default:
					System.out.println("SOMETHING BAD HAPPENED IN STARTING HAND STRENGTH CALCULATION");
					return 0.0;
			}
		}
	}

}