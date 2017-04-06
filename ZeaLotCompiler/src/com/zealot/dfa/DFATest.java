package com.zealot.dfa;

import java.util.Set;

public class DFATest {
	public static void main(String[] args) {
		String regex1 = "(a|b)+b*";
		String regex2 = "(a|b)+b*+a+a";
		String regex3 = "b*+c+a|(b+c)";
		
		DFAManager dfaManager = DFAManager.getInstance();
		Set<Integer> epsClosure = dfaManager.createDFA(regex1);
		System.out.println(epsClosure);
	}
}
