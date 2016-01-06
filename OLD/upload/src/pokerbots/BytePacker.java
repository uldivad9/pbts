package pokerbots;

import java.util.*;

public class BytePacker {
	
	/*
	takes a string consisting of numbers and spaces, and converts this into a base 11 integer (where spaces are 10).
	this integer is then encoded into a string of ascii 33-126 characters using base 94.
	the ascii string is returned.
	*/
	public static String pack(String message) {
		StringBuilder packed = new StringBuilder();
		int numChunks = (int)Math.ceil(message.length()/17.0);
		
		for (int chunkno=0; chunkno<numChunks; chunkno++) {
			String chunk = message.substring(chunkno*17,Math.min((chunkno+1)*17,message.length()));
			//encode to base 11
			long base12 = encode12(chunk);
			//encode to base 94 ascii
			String out = encode94(base12);
			
			packed.append(out);
			if (chunkno != numChunks-1) {
				packed.append(" ");
			}
		}
		return packed.toString();
	}
	
	/*
	undoes the functionality of pack. accepts an array of strings, which should be equivalent to an encoded string split by spaces.
	*/
	public static String unpack(String[] tokens) {
		StringBuilder out = new StringBuilder();
		for (int i=0; i<tokens.length; i++) {
			//undo base94 encoding
			long base12 = decode94(tokens[i]);
			String chunk = decode12(base12);
			out.append(chunk);
		}
		return out.toString();
	}
	
	public static String unpack(String coded) {
		return unpack(coded.split(" "));
	}
	
	//turns a long into an ascii base-94 length-4 string.
	public static String encode94(long m) {
		if (m == 0) {
			return "!";
		}
		StringBuilder sb = new StringBuilder();
		long remainder = m;
		while (remainder > 0) {
			long lastdigit = remainder%94;
			sb.append((char)(lastdigit+33));
			remainder-=lastdigit;
			remainder/=94;
		}
		sb.reverse();
		//System.out.println("encode94 "+sb.toString()+"]");
		return sb.toString();
	}
	
	public static long decode94(String m) {
		long out = 0;
		for (int i=0; i<m.length(); i++) {
			out*=94;
			out+=((int)m.charAt(i)-33);
		}
		//System.out.println("decode94 " + out);
		return out;
	}
	
	//turns a string of digits and spaces into a long.
	public static long encode12(String chunk) {
		if (chunk.length() > 17) {
			System.out.println("encode12 ARGUMENT TOO LONG");
		}
		long base12 = 0;
		for (int i=0; i<chunk.length(); i++) {
			base12*=12;
			switch(chunk.charAt(i)) {
				case '0':
					base12+=11;
					break;
				case '1':
					base12+=1;
					break;
				case '2':
					base12+=2;
					break;
				case '3':
					base12+=3;
					break;
				case '4':
					base12+=4;
					break;
				case '5':
					base12+=5;
					break;
				case '6':
					base12+=6;
					break;
				case '7':
					base12+=7;
					break;
				case '8':
					base12+=8;
					break;
				case '9':
					base12+=9;
					break;
				case ' ':
					base12+=10;
					break;
				default:
					System.out.println("WARNING: PACKING MESSAGE CONTAINING CHARACTERS!");
					break;
			}
		}
		//System.out.println("encode12 " + base12);
		return base12;
	}
	
	public static String decode12(long m) {
		if (m == 0) {
			return "0";
		}
		StringBuilder out = new StringBuilder();
		long remainder = m;
		while (remainder > 0) {
			long lastdigit = remainder%12;
			if (lastdigit == 10) {
				out.append(' ');
			} else if (lastdigit == 11) {
				out.append('0');
			} else {
				out.append(lastdigit);
			}
			remainder-=lastdigit;
			remainder/=12;
		}
		out.reverse();
		//System.out.println("decode12 "+out.toString()+"]");
		return out.toString();
	}
}