package com.example;

import java.text.Collator;
import java.util.Locale;

/**
 * 
 *
 * Task here is to implement a function that says if a given string is
 * palindrome.
 * 
 * 
 * 
 * Definition=> A palindrome is a word, phrase, number, or other sequence of
 * characters which reads the same backward as forward, such as madam or
 * racecar.
 */
public class TASK1 {

	public static final String palin1 = "Ana";
	public static final String palin2 = "kaiak";
	public static final String palin3 = "a mãe te ama";

	/**
	 * this method compares two strings. 
	 * @param term1
	 * @param term2
	 * @return
	 */
	public boolean ehPalindromo(String term1, String term2) {
		term1 = term1.trim().replace(" ", "");
		term2 = term2.trim().replace(" ", "");
		if(term1.length() != term2.length())		
			return false;		

		StringBuilder sb = new StringBuilder();
		for(int i = term2.length() -1 ; i >= 0 ; i--) {
			//System.out.println(term2.charAt(i));
			sb.append(term2.charAt(i));
		}
		if(term1.equalsIgnoreCase(sb.toString()))
			return true;
		else {
			//ignorar acentos
			Collator coll = Collator.getInstance(new Locale("pt", "BR"));
			coll.setStrength(Collator.PRIMARY);
			if(coll.compare(term1, sb.toString()) == 0)
				return true;
			else
				return false;
		}
	}

	public static void main(String[] args) {
		//System.out.println(Math.round(5/2) + 1);
		TASK1 task = new TASK1();
		System.out.println(task.ehPalindromo(task.palin2, "kaiak"));
	}
}
