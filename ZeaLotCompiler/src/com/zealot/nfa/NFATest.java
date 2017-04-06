package com.zealot.nfa;

/**
 * 非确定有限状态自动机
 * @author ZeaLot
 *
 */
public class NFATest {
	public static void main(String[] args) {//(a|b)+b*+a+a
		String regex1 = "(a|b)+b*";
		String regex2 = "(a|b)+b*+a+a";
		String regex3 = "b*+c+a|(b+c)";
		//b*+c+a|(b+c)
		NFAManager manager = NFAManager.getInstance();
		Character[][] nfa =  manager.createNFA(regex1);
		System.out.print("\t");
		for(int i=0; i<nfa[0].length; i++){
			System.out.print(i+"\t");
		}
		System.out.println();
		for(int i=0; i<nfa.length; i++){
			System.out.print(i+"\t");
			for(int j=0; j<nfa[i].length; j++){
				System.out.print(nfa[i][j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
		nfa =  manager.createNFA(regex2);
		System.out.print("\t");
		for(int i=0; i<nfa[0].length; i++){
			System.out.print(i+"\t");
		}
		System.out.println();
		for(int i=0; i<nfa.length; i++){
			System.out.print(i+"\t");
			for(int j=0; j<nfa[i].length; j++){
				System.out.print(nfa[i][j]+"\t");
			}
			System.out.println();
		}
		System.out.println();
		nfa =  manager.createNFA(regex3);
		System.out.print("\t");
		for(int i=0; i<nfa[0].length; i++){
			System.out.print(i+"\t");
		}
		System.out.println();
		for(int i=0; i<nfa.length; i++){
			System.out.print(i+"\t");
			for(int j=0; j<nfa[i].length; j++){
				System.out.print(nfa[i][j]+"\t");
			}
			System.out.println();
		}
	}
}
