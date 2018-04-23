package com.example;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Write a list and add an aleatory number of Strings. In the end, print out how
 * many distinct itens exists on the list.
 *
 */
public class TASK3 {

	public Map<String, String> itens = new HashMap<String, String>();

	public String generateRandom() {
		return UUID.randomUUID().toString();
	}


	public int checkTypes(List<String> items) {
		for (String item : items) {
			Pattern pattern1 = Pattern.compile("[a-z]");
			Matcher matcher1 = pattern1.matcher(item);

			Pattern pattern2 = Pattern.compile("[A-Z]");
			Matcher matcher2 = pattern2.matcher(item);

			Pattern pattern3 = Pattern.compile("[0-9]");
			Matcher matcher3 = pattern3.matcher(item);

			//checa quantas letras minúsculas diferentes há
			while(matcher1.find()) 
				for(int index = 0; index <= matcher1.groupCount() ; index ++) 
					if(!this.itens.containsKey(matcher1.group(index)))
						this.itens.put(matcher1.group(index), matcher1.group(index));

			while(matcher2.find()) 
				for(int index = 0; index <= matcher2.groupCount() ; index ++) 
					if(!this.itens.containsKey(matcher2.group(index)))
						this.itens.put(matcher2.group(index), matcher2.group(index));

			while(matcher3.find()) 
				for(int index = 0; index <= matcher3.groupCount() ; index ++) 
					if(!this.itens.containsKey(matcher3.group(index)))
						this.itens.put(matcher3.group(index), matcher3.group(index));



		}
		return this.itens.size();
	}

	/**
	 * this work was the most incomprehensible to me, 'cause I do not know if I get
	 * the points.
	 * What I did is to check what kind of characters was set in parameters. Like 
	 * lower case chars or numbers.
	 * @param args
	 */
	public static void main(String[] args) {
		TASK3 t = new TASK3();
		String s1 = UUID.randomUUID().toString();
		String s2 = UUID.randomUUID().toString();
		List<String> strings = new LinkedList<String>();
		strings.add(s1);
		strings.add(s2);
		System.out.println(s1);
		System.out.println(s2);
		System.out.println(t.checkTypes(strings));		
	}
}
