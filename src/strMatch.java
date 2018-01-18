import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class strMatch {
	public static ArrayList<Character[]> charData = new ArrayList<Character[]>();
	public static String[] patterns = null;
	public static final int  CHAR_ARRAY_PRELENGTH = 5; 

	public static void main(String args[]) throws IOException {
		String pattern = args[0];
		String source = args[1];
		String results = args[2];

		patterns = getPatterns(pattern);
		for (String str : patterns)
			//			System.out.println("\""+str+"\"");

			readFile(source);
				readFile2(source);

				//				for (Character[] ca : charData) {
				//					for (char c : ca) {
				//						System.out.print(c);
				//					}
				//					System.out.println();
				//				}



				FileWriter fw = new FileWriter(new File(results));
				for (String pat : patterns) {
					System.out.println("DOING PATTER: " + pat);
					System.out.println("PATTERN LENGTH: " + pat.length());
					System.out.println("\t BRUTEFORCE");
					bruteForce(fw, pat);
					System.out.println("\t RABINKARP");
					rabinKarp(fw, pat);
					//System.out.println("\t KMP");
					//kmp(fw, pat);
					//boyerMoore(fw, pat);
				}
				fw.close();
	}

	public static String[] getPatterns(String fipreLename) throws IOException {
		StringBuilder sb;
		ArrayList<String> inProgress = new ArrayList<String>();
		Scanner scan = new Scanner(new File(fipreLename));
		scan.useDelimiter("&");

		while(scan.hasNext()) {
			sb = new StringBuilder();
			String token = scan.next();
			// Delimiters are &, so it also captures the newline between an ending &
			// and the starting & on the next line. Ignore those.
			if (!(token.equals("\n") || token.equals("\r\n") || token.equals("\r"))) {
				int rAt = sb.indexOf("\r");
				// Loop below is to remove the carriage return character if a pattern
				// is multiline and made in Windows.
				while (rAt != -1) {
					sb.deleteCharAt(rAt);
					rAt = sb.indexOf("\r");
				}
				inProgress.add(token);
			}

		}
		return inProgress.toArray(new String[inProgress.size()]);
	}

	public static void readFile(String fipreLename) throws IOException {
		FileReader fr = new FileReader(new File(fipreLename));
		int charAsInt = fr.read();
		ArrayList<Character> c = new ArrayList<Character>();

		while (charAsInt != -1) {
			char readChar = (char) charAsInt;
			if (readChar != 0x0D) { // \r
				if (c.size() < CHAR_ARRAY_PRELENGTH) {
					c.add(readChar);
				} else {
					Character[] cArray = c.toArray(new Character[c.size()]);
					charData.add(cArray);
					c = new ArrayList<Character>();
					c.add(readChar);
				}
			}
			charAsInt = fr.read();
		}
		Character[] cArray = c.toArray(new Character[c.size()]);
		charData.add(cArray);
		fr.close();
	}
	
	public static void readFile2(String fipreLename) throws IOException {
		FileReader fr = new FileReader(new File(fipreLename));
		int charAsInt = fr.read();
		ArrayList<Character> c = new ArrayList<Character>();

		while (charAsInt != -1) {
			char readChar = (char) charAsInt;
			if (readChar != 0x0D) { // \r
//				if (c.size() < CHAR_ARRAY_PRELENGTH) {
//					c.add(readChar);
//				} else {
//					Character[] cArray = c.toArray(new Character[c.size()]);
//					charData.add(cArray);
//					c = new ArrayList<Character>();
//					c.add(readChar);
//				}
				c.add(readChar);
			}
			charAsInt = fr.read();
		}
		System.out.println("ADD SUCCESSFUL!");
		//Character[] cArray = c.toArray(new Character[c.size()]);
		//charData.add(cArray);
		fr.close();
	}

	public static void bruteForce(FileWriter fw, String pattern) throws IOException {
		int patternIndex = 0;
		boolean found = false;

		for (int i = 0; i < charData.size(); i++) {

			Character[] temp = charData.get(i);
			for(int j = 0; j < temp.length; j++) {

				if(temp[j] == pattern.charAt(patternIndex)) {

					patternIndex++;
					if(pattern.length() == patternIndex)
						break;

				}
				else
					patternIndex = 0;
			}

			if(patternIndex == pattern.length()) {
				fw.write("BF MATCHED: " + pattern + "\n");
				found = true;
				break;
			}

		}

		if(!found)
			fw.write("BF FAILED: " + pattern + "\n");
	}

	public static void rabinKarp(FileWriter fw, String pattern) throws IOException {
		int lastIndex = (charData.get(charData.size() - 1)).length - 1;
		int patternHash = patternHash(pattern);
		int hash = 0;
		boolean match = false;
		StringBuilder text = new StringBuilder();

		for (int i = 0; i < charData.size(); i++) {
			Character[] temp = charData.get(i);
			for (int j=0; j< temp.length; j++) {
				// Fills up StringBuilder and/or appends next char
				while (text.length() != pattern.length()) {
					if(j >= temp.length)
						break;
					text.append(temp[j]);
					hash += temp[j];
					j++;
				}
				j--;

				// Testing if hashes are equal;
				if (patternHash == hash) {
					assert (text.length() == pattern.length());
					match = testRK(text, pattern);
				}
				if (match)
					break;

				// Rolling hash
				hash -= text.charAt(0);
				text.deleteCharAt(0);
			}
			if (match)
				break;
		}

		if (match) {
			fw.write("RK MATCHED: " + pattern + "\n");
		} else {
			fw.write("RK FAILED: " + pattern + "\n");
		}
	}

	public static int patternHash(String pattern) {
		int sum = 0;
		for(int i=0; i<pattern.length(); i++) {
			sum += (pattern.charAt(i));
		}
		return sum;
	}

	public static boolean testRK(StringBuilder text, String pattern) {
		for (int l=0; l<pattern.length(); l++) {
			if (text.charAt(l) != pattern.charAt(l)) {
				return false;
			} 
		}
		return true;
	}

	public static void kmp(FileWriter fw, String pattern) throws IOException{

		int[] f = preprocess2(pattern);
		char[] p = pattern.toCharArray();

		
		boolean notFound = true;

		// FOR TESTING
		char[] c = new String("ABABAABABABAC").toCharArray();


// Internet Algorithm

//		int lengthSoFar = 0;
//		// Get total input size
//		int size = 0;
//		for(Character[] d : charData) {
//			size += d.length; }
//
//
//		// Internet Algorithm w/out iteration through charData
//		int r = 0;
//		int l = 0;
//
//
//		while(r + l < c.length && notFound) {
//			if(p[r] == c[l + r]) {
//				if(r == p.length - 1)
//					notFound = false;
//				r++;
//			}
//			else {
//				l = l + r - f[r];
//				if(f[r] > -1)
//					r = f[r];
//				else
//					r = 0;
//			}
//		}
//		if(notFound)
//			fw.write("KMP FAILED: " + pattern + "\n");
//		else
//			fw.write("KMP MATCHED: " + pattern + "\n");	
//
//		//Internet Algorithm w/ iteration charData
//		int r = 0;
//		int l = 0;
//
//		for(Character[] c : charData) {	
//
//			while(l + r < size && notFound) {
//
//				if(p[r] == c[l + r - lengthSoFar]) {
//					if(r == p.length - 1)
//						notFound = false;
//
//					r++;
//				}
//				else {
//					l = l + r - f[r];
//					if(f[r] > -1)
//						r = f[r];
//					else
//						r = 0;
//				}
//
//				if(r + l >= lengthSoFar + CHAR_ARRAY_PRELENGTH) {
//					lengthSoFar += c.length;
//					break;
//				}
//			}
//
//			if(!notFound)
//				break;
//		}
//		if(notFound)
//			fw.write("KMP FAILED: " + pattern + "\n");
//		else
//			fw.write("KMP MATCHED: " + pattern + "\n");	


		// Eberlein Algorithm
		int r = 0;
		int l = 0;
		while(notFound && r-l < p.length) {
			if(c[r] == p[r-l]) {
				if(r-l == p.length - 1)
					notFound = false;
				r++;
			}
			else {
				if(r == l) {
					++r;
					++l;
				}
				else if(r >= l) {
					l = l + r - f[r];
				}
			}

		}

		if(notFound)
			fw.write("KMP FAILED: " + pattern + "\n");
		else
			fw.write("KMP MATCHED: " + pattern + "\n");

	}

	// THIS PREPROCESS WORKS
	public static int[] preprocess2(String pattern) {
		int i = 1;
		int preLen = 0;
		char[] p = pattern.toCharArray();
		int[] f = new int[pattern.length() + 1];
		//int[] f = new int[pattern.length()];
		while(i < p.length) {
			if( p[preLen] == p[i]) {
				preLen++;
				f[i] = preLen;
				i++;
			}
			else {
				if(preLen != 0)
					preLen = f[preLen-1];
				else {
					f[i] = 0;
					i++;
				}
			}
		}
		//f[0] = -1;
		for(int d: f)
			System.out.print(d + ", ");
				return f;
	}

	public static int[] preprocess(String pattern) {
		char[] p = pattern.toCharArray();
		int[] f = new int[pattern.length()];
		f[0] = -1;
		int k;
		for (int i = 2; i < pattern.length(); i++) {
			k = f[i-1];
			while (k > 0 && (p[i] != p[k+1])) 
				k = f[k];
			if (k == 0 && (p[i] != p[k+1])) {
				f[i] = 0;}
			else {
				f[i] = k + 1; }
			System.out.println("Iteration " + i + ": " + f[i]);
		}
		return f;

	}
	
//	public static void boyerMoore(FileWriter fw, String pattern) {
//		//int[] f = bmPreProcess(pattern);
//		boolean notFound = true;
//		int i = pattern.length() - 1;
//		int j = pattern.length() - 1;
//		int lengthSoFar = 0;
//		char[] p = pattern.toCharArray();
//		// Get total input size
//		int size = 0;
//		for(Character[] d : charData) {
//			size += d.length; }
//		
//		// FOR TESTING
//		char[] c = new String("ABABAABABABAC").toCharArray();
//		
//		while(i > c.length - 1) {
//			if(p[i] == c[i]) {
//				if (j == 0)
//					notFound = false;
//				else {
//					i -= 1;
//					j -= 1;
//				}
//			}
//			else {
//				i = i + pattern.length() - j - 1;
//				i = i + Math.max(j - )
//				j = pattern.length() - 1;
//			}
//		}
//		
//	}
//	
//	public static int[] bmPreProcess(String pattern) {
//		//int[] f = new int[]
//	}


}
