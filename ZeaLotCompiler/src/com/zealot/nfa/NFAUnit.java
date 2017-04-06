package com.zealot.nfa;

/**
 * NFA单元
 * @author ZeaLot
 *
 */
public class NFAUnit {
	private Integer startState;
	private Integer endState;
	
	
	public NFAUnit() {
		super();
	}
	public NFAUnit(Integer startState, Integer endState) {
		super();
		this.startState = startState;
		this.endState = endState;
	}
	public Integer getStartState() {
		return startState;
	}
	public void setStartState(Integer startState) {
		this.startState = startState;
	}
	public Integer getEndState() {
		return endState;
	}
	public void setEndState(Integer endState) {
		this.endState = endState;
	}
	@Override
	public String toString() {
		return "NFAUnit [startState=" + startState + ", endState=" + endState + "]";
	}
	
}
