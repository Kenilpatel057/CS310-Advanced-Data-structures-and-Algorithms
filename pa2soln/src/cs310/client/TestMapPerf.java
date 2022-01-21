package cs310.client;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import cs310.util.HashMap1;
import cs310.util.HashMap4;
/**
 * Class to test performance of different map implementations
 * Also tests a load sequence to see if all the expected keys are actually there
 * Note: needs HashMap4's ability to resize to get decent performance
 */
public class TestMapPerf {
	private Map<Integer, String> case0;
	private Map<Integer, String> case1;
	private Map<Integer, String> case2;
	private int N;// number of gets tried on maps
	private int count; // number of lines in file
	private static int[] randNum;// array of random numbers used to test Maps

	/*
	 * Takes three Maps and constructs a TestMapPerf object
	 */
	public TestMapPerf(Map<Integer, String> case0, Map<Integer, String> case1, Map<Integer, String> case2, int N) {
		this.case0 = case0;
		this.case1 = case1;
		this.case2 = case2;
		this.N = N;
	}
	
	boolean loadAndCompareMap(String fileName, Map<Integer, String> baseMap, Map<Integer, String> testMap)
			throws FileNotFoundException, IOException {
		FileReader input = new FileReader(fileName);
		Scanner in = new Scanner(input);
		int lineNum = 0;
		count = 0;
		System.out.println("reading " + fileName);

		while (in.hasNextLine()) {
			in.nextLine();
			count++;
		}
		in.close();
		input.close();
		System.out.println("Found " + count + " lines");
		input = new FileReader(fileName);
		in = new Scanner(input);
		int errorCount = 0;
		count = 0;
		
		while (in.hasNextLine()) {
			count++;
			if (count==10000)  // just use first 10000 here (this is not for perf)
				break;		
			String word = in.nextLine();
//			if (lineNum < 10) {
//				System.out.println("loading " + lineNum + "," + word);
//			}
			baseMap.put(lineNum, word);
			testMap.put(lineNum, word);
			// We can get the keys from the JDK Hashmap 
			Set<Integer> baseKeys = baseMap.keySet();
			for (Integer key : baseKeys) { // then check that they are all in the new HashMap
				String v = testMap.get(key);
				if (v == null || !v.equals(baseMap.get(key))) {
					System.out.println("get of old val failed on line" + lineNum + " v= " + v);
					errorCount++;
					if (errorCount > 10) {
						System.out.println("over 10 errors, Map needs fixing before performance testing");
						in.close();
						return false;
					}
				}
			}
			// Use only a subset of the data for case0 and case1
			if (lineNum < count / 2)
				case1.put(lineNum, word);
			if (lineNum < count / 4)
				case0.put(lineNum, word);
			lineNum++;
		}
		in.close();
		System.out.println("testMap passed load test");
		return true; // passed test
	}
	
	// load the 3 maps of different sizes (all of same type, for ex. HashMap<Integer,String>)
	public void loadMaps(String fileName) throws FileNotFoundException, IOException {
		FileReader input = new FileReader(fileName);
		Scanner in = new Scanner(input);
		int lineNum = 0;
		count = 0;
		System.out.println("reading " + fileName);

		while (in.hasNextLine()) {
			in.nextLine();
			count++;
		}
		in.close();
		input.close();
		System.out.println("Found " + count + " lines");
		input = new FileReader(fileName);
		in = new Scanner(input);
	
		while (in.hasNextLine()) {
			String word = in.nextLine();
//			if (lineNum < 10) {
//				System.out.println("loading " + lineNum + "," + word);
//			}
			case2.put(lineNum, word);
			String v = case2.get(lineNum);
			if (v == null || !v.equals(word)) {
				System.out.println("echo failed on line" + lineNum + " v= " + v);
			}
			if (lineNum < count / 2)
				case1.put(lineNum, word); 
			if (lineNum < count / 4)
				case0.put(lineNum, word);
			lineNum++;
		}
		in.close();
	}

	/**
	 * Tests three cases with the given Map implementation and prints out their
	 * performances
	 * 
	 * @param fileName the name of file where strings are stored
	 * @throws FileNotFoundException
	 * @throws FileNotFoundException
	 */
	public void timeTest() {
		if (randNum == null)// initialize randNum only once
		{
			randNum = new int[N];
			Random rnd = new Random();
			for (int i = 0; i < N; i++) {
				randNum[i] = rnd.nextInt(count);
			}
		}
		long[] timeTaken = new long[3]; // for the 3 cases
		System.out.println("Map type: " + case0.getClass().getName());
		timeTaken[0] = testMap(case0, randNum, 4);
		timeTaken[1] = testMap(case1, randNum, 2);
		timeTaken[2] = testMap(case2, randNum, 1);
		System.out.printf("%10s %10s %10s %14s\n", "case", "ms", "#gets", "time/get(us)");
		for (int caseNo = 0; caseNo < 3; caseNo++)
			System.out.printf("%10d %10d %10d %14.3f\n", caseNo, timeTaken[caseNo] / 1000, N,
					((double) timeTaken[caseNo]) / N);
		System.out.println();
	}

	/**
	 * helper to timeTest
	 * @param m   a map to be tested
	 * @param randNum an array of random entries to be looked up in the map
	 * @return the time it takes to access the Map N times
	 */
	public static long testMap(Map<Integer, String> m, int[] randNum, int divisor) {

		long startTime = System.nanoTime();

		for (int i = 0; i < randNum.length; i++) {
			@SuppressWarnings("unused")
			String word = m.get(randNum[i] / divisor);
		}

		long endTime = System.nanoTime();

		return (endTime - startTime) / 1000; // convert nanosecs to microsecs

	}

	public static void main(String[] args) {
		int N;// number of get tests
		String fileName;// file name containing the words
		//Map<Integer, String> m = new HashMap2<Integer, String>();
		//m.put(1, "b");
		//System.out.println("got back " + m.get(1));
		if (args.length == 2) {
			N = Integer.parseInt(args[0]);
			fileName = args[1];
		} else {
			System.out.println("Usage TestMapPerf N filename (saw " + args.length + " args)");
			return;
		}
		try {
			System.out.println("Starting...");
			Map<Integer, String> case0;
			Map<Integer, String> case1;
			Map<Integer, String> case2;
			TestMapPerf perf;

			case0 = new HashMap<Integer, String>();
			case1 = new HashMap<Integer, String>();
			case2 = new HashMap<Integer, String>();
			perf = new TestMapPerf(case0, case1, case2, N);
			perf.loadMaps(fileName);
			perf.timeTest();

			case0 = new TreeMap<Integer, String>();
			case1 = new TreeMap<Integer, String>();
			case2 = new TreeMap<Integer, String>();

			perf = new TestMapPerf(case0, case1, case2, N);
			perf.loadMaps(fileName);
			perf.timeTest();
			
			// replace HashMap1 with HashMap4 for testing, delivery
			case0 = new HashMap4<Integer, String>();
			case1 = new HashMap4<Integer, String>();
			case2 = new HashMap4<Integer, String>();
			
			// check out experimental HashMap before doing performance runs
			if (perf.loadAndCompareMap(fileName, new HashMap4<Integer, String>(), new HashMap1<Integer, String>())) {
				perf = new TestMapPerf(case0, case1, case2, N);
				perf.loadMaps(fileName);
				perf.timeTest();
			}

		} catch (FileNotFoundException e) {
			System.out.println("File " + fileName + " not found: " + e);
			return;
		} catch (IOException e) {
			System.out.println("IOException " + e);
			return;
		}
	}

}
