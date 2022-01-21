package cs310.client;

import java.util.Map;
import java.util.TreeMap;
import cs310.util.HashMap1;
import cs310.util.HashMap3;

import java.util.HashMap;
import java.io.IOException;

public class TestMap {
	private Map<String, String> m;

	public TestMap(Map<String, String> aMap) throws IOException {
		m = aMap;
	}

	public Map<String, String> getMap() {
		return m;
	}

	public static void main(String[] args) {
		try {			
			TestMap phone2a = new TestMap(new TreeMap<String, String>());
			phone2a.test();

			TestMap phone2b = new TestMap(new HashMap<String, String>());
			phone2b.test();

			TestMap phone1 = new TestMap(new HashMap3<String, String>());
			phone1.test();

		} catch (Exception e) {
			System.out.println("Exception in main: " + e);
		}

	}
	
	// testing of get, put, remove only
	public void test() {
		System.out.println("----------------------------------------------");
		System.out.println("The " + m.getClass().getName() + ": ");

		try {
			System.out.println("trying first put...");
			m.put("Jane Doe", "312-555-1212");
			System.out.println("OK, trying second put");
		} catch (Exception e) {
			System.out.println("put exception: " + e);
		}
		try {
			m.put("John Doe", "212-555-1212");
		} catch (Exception e) {
			System.out.println("put exception: " + e);
		}
		String s = null;
		try {
			s = m.get("Jane Doe");
		} catch (Exception e) {
			System.out.println("get exception: " + e);
		}
		try {
			System.out.println("Got back: " + s);
			m.put("Holly Doe", "213-555-1212");
		} catch (Exception e) {
			System.out.println("put exception: " + e);
		}
		try {
			m.put("Susan Doe", "617-555-1212");
		} catch (Exception e) {
			System.out.println("put exception: " + e);
		}
		try {
			m.put("Jane Doe", "unlisted");
		} catch (Exception e) {
			System.out.println("put exception: " + e);
		}
		try {	
			System.out.println("get(\"Jane Doe\") after put unlisted: " + m.get("Jane Doe"));
		} catch (Exception e) {
			System.out.println("get exception: " + e);
		}

		// test_remove() // test for remove: optional feature
	
	}
	void test_remove() {
		try {
			String val = m.get("Holly Doe");
			System.out.println("get before remove returned "+ val);
		} catch (Exception e) {
			System.out.println("get before remove exception: " + e);
		}
		try {
			String val = m.remove("Holly Doe");
			System.out.println("remove returned " + val);
		} catch (Exception e) {
			System.out.println("remove exception: " + e);
		}
		try {
			String val = m.get("Holly Doe");
			System.out.println("get after remove returned "+ val);
		} catch (Exception e) {
			System.out.println("get after remove exception: " + e);
		}

	}
}
