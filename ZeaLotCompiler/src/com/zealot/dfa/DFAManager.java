package com.zealot.dfa;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.zealot.nfa.NFAManager;

public class DFAManager {
	private static DFAManager dfaManager;
	private Character[][] nfa;
	private boolean[][] visited;
	private Set<Integer> epsClosure = new HashSet<Integer>();
	
	private DFAManager(){}
	public static DFAManager getInstance(){
		if(dfaManager == null){
			dfaManager = new DFAManager();			
		}
		return dfaManager;
	}
	public Set<Integer> createDFA(String regex){
		this.epsClosure.clear();
		NFAManager manager = NFAManager.getInstance();
		this.nfa =  manager.createNFA(regex);
		this.visited = new boolean[this.nfa.length][this.nfa[0].length];
		epsClosureByBFS(9);
		//epsClosureByDFS(9);
		return this.epsClosure;
	}
	/**
	 * 通过深度优先搜索求ε闭包
	 * @param stateNum
	 */
	public void epsClosureByDFS(Integer startState){	//状态从0开始
		this.epsClosure.add(startState);
		for(Integer i=0; i<this.nfa[startState].length; i++){
			//对于未访问过的eps
			if(this.nfa[startState][i].equals('ε') && this.visited[startState][i] == false){
				epsClosureByDFS(i);
				this.visited[startState][i] = true;
			}
		}
	}
	/**
	 * 通过广度优先搜索求ε闭包
	 */
	public void epsClosureByBFS(Integer startState){
		Queue<Integer> queue = new LinkedList<>();
		queue.offer(startState);
		while(!queue.isEmpty()){//队列中没有状态则结束了
			startState = queue.poll();
			this.epsClosure.add(startState);
			for(Integer i=0; i<this.nfa[startState].length; i++){
				if(this.nfa[startState][i].equals('ε') && this.visited[startState][i] == false){
					queue.offer(i);
					this.visited[startState][i] = true;
				}
			}
		}
	}
	public Character[][] getNfa() {
		return nfa;
	}

	public void setNfa(Character[][] nfa) {
		this.nfa = nfa;
	}
	
	
	
}
