package com.zealot.nfa;

import java.util.Stack;

/**
 * NFA管理器 TODO 后期要池化NFAUnit
 * @author ZeaLot
 *	单例
 */
public class NFAManager {
	private static NFAManager nfaManager;
	private static Stack<Character> operStack;//正则表达式符号栈
	private static Stack<NFAUnit> NFAUnitStack;//NFA单元栈
	private Character[][] nfaMatrix;		//NFA图
	private Integer stateNum = 0;			//全局状态号
	private final Character EPSILON = 'ε';	//空串
	private final Character ENDSYMBOL = '#';//结束符
	static{
		operStack = new Stack<Character>();
		NFAUnitStack = new Stack<NFAUnit>();
	}
	private NFAManager(){}
	public static NFAManager getInstance(){
		if(nfaManager == null){
			nfaManager = new NFAManager();			
		}
		return nfaManager;
	}

	/**
	 * 初始化NFA图,清空双栈，初始化状态号,最终状态数不会超过字符数的两倍
	 * @param regex
	 * @return nfa的邻接矩阵
	 */
	public Character[][] createNFA(String regex){
		int len = regex.length();
		regex += ENDSYMBOL;
		//初始化图和双栈
		this.nfaMatrix = new Character[len*2][len*2];
		for(int i=0; i<nfaMatrix.length; i++){
			for(int j=0; j<nfaMatrix[i].length; j++){
				nfaMatrix[i][j] = '0';
			}
		}
		operStack.removeAllElements();
		NFAUnitStack.removeAllElements();
		this.stateNum = 0;
		for(int i=0; i<len+1; i++){
			createFinalNFA(regex.charAt(i));
		}
		return this.nfaMatrix;
	}
	/**
	 * 判断是哪种字符，构造不同的NFA
	 * @param ch
	 */
	private void createFinalNFA(Character ch){
		if(ch.equals('*') || ch.equals('+') || ch.equals('|')
		|| ch.equals('(') || ch.equals(')') || ch.equals(ENDSYMBOL)){
			if(operStack.isEmpty()){	//如果符号栈是空的则直接将当前符号入栈
				if(ch.equals(ENDSYMBOL)){	//符号栈是空的并且下一个字符是结束标志，则构造结束
					return;
				}
				operStack.push(ch);
			}else{	//不是空的则判断优先级
				Character topOper = operStack.peek();
				if(compareOperByPriority(ch, topOper) == 1){
					operStack.push(ch);
				}else if(compareOperByPriority(ch, topOper) == -1){
					operStack.pop();	//出栈
					if(topOper.equals('*')){//判断栈顶是哪种运算
						NFAUnitStack.push(createClosureNFA());
					}else if(topOper.equals('+')){
						NFAUnitStack.push(createJoinNFA());
					}else if(topOper.equals('|')){
						NFAUnitStack.push(createSelectNFA());
					}
					createFinalNFA(ch);//继续判断当前符号
				}else if(compareOperByPriority(ch, topOper) == 0){
					operStack.pop();	//消除成对的括号
				}
			}
		}else{
			//普通字符
			NFAUnitStack.push(createCharNFA(ch));
		}
	}
	/**
	 * 比较两个符号的优先级，返回1表示当前符号优于栈顶符号,接下来就可以入栈
	 * 				      返回-1表示接下来要出栈运算
	 * @param leftOper
	 * @param rightOper
	 * @return
	 */												//栈顶符号			//当前读入符号
	private Integer compareOperByPriority(Character currOper, Character topOper){
		//栈顶符号可能出现: '*' '|' '+' '(',')'
		//读入符号可能出现: '*' '|' '+' '(',')'
		Integer res = null;
		switch(topOper){	
		case '*':			//栈项为*的情况下，输入任何符号都比不过
			res = -1;
			break;
		case '+':
			if(currOper.equals('*') || currOper.equals('(')){//a+b*+(a+b)
				res = 1;
			}else{ //'+' '|' ')' ENDSYMBOL
				res = -1;
			}
			break;
		case '|'://a|b+b*
			if(currOper.equals('*') || currOper.equals('+') || currOper.equals('(')){
				res = 1;
			}else{// '|' ')' ENDSYMBOL
				res = -1;
			}
			break;
		case '('://(a|b*baa)		//右边的任何符号都比左边的'('优先,碰到')'要出栈
			if(currOper.equals(')')){
				res = 0;	//返回0代表消去左右括号
			}else{
				res = 1;
			}
			break;
		default:
			throw new RuntimeException("无法识别的正则符号:"+topOper.toString());
		}
		return res;
	}
	/**
	 * 构造单个普通字符的NFA
	 * @param ch
	 * @return
	 */
	private NFAUnit createCharNFA(Character ch){
		Integer startState = this.stateNum++;
		Integer endState = this.stateNum++;
		this.nfaMatrix[startState][endState] = ch;
		return new NFAUnit(startState, endState);
	}
	
	/**
	 * 构造闭包NFA
	 * @return
	 */
	private NFAUnit createClosureNFA(){
		if(NFAUnitStack.isEmpty()){
			throw new RuntimeException("闭包NFA栈构造异常");
		}
		NFAUnit nfa =  NFAUnitStack.pop();
		this.nfaMatrix[nfa.getEndState()][nfa.getStartState()] = this.EPSILON;
		Integer startState = this.stateNum++;
		Integer endState = this.stateNum++;
		this.nfaMatrix[startState][endState] = this.EPSILON;
		this.nfaMatrix[startState][nfa.getStartState()] = this.EPSILON;
		this.nfaMatrix[nfa.getEndState()][endState] = this.EPSILON;
		nfa.setStartState(startState);	//把原来的nfa改下初终态就可以用了，节省内存
		nfa.setEndState(endState);
		return nfa;
	}
	/**
	 * 构造连接NFA
	 * @return
	 */
	private NFAUnit createJoinNFA(){
		if(NFAUnitStack.size()<2){
			throw new RuntimeException("连接NFA栈构造异常");
		}
		NFAUnit rightNfa = NFAUnitStack.pop();
		NFAUnit leftNfa = NFAUnitStack.pop();
		Integer startState = leftNfa.getStartState();
		Integer endState = rightNfa.getEndState();
		this.nfaMatrix[leftNfa.getEndState()][rightNfa.getStartState()] = this.EPSILON;
		leftNfa.setStartState(startState);//把原来的nfa改下初终态就可以用了，节省内存
		leftNfa.setEndState(endState);
		return leftNfa;
	}
	/**
	 * 构造选择NFA
	 * @return
	 */
	private NFAUnit createSelectNFA(){
		if(NFAUnitStack.size()<2){
			throw new RuntimeException("选择NFA栈构造异常");
		}
		NFAUnit bottomNfa = NFAUnitStack.pop();
		NFAUnit topNfa = NFAUnitStack.pop();
		Integer startState = this.stateNum++;
		Integer endState = this.stateNum++;
		this.nfaMatrix[startState][topNfa.getStartState()] = this.EPSILON;
		this.nfaMatrix[startState][bottomNfa.getStartState()] = this.EPSILON;
		this.nfaMatrix[topNfa.getEndState()][endState] = this.EPSILON;
		this.nfaMatrix[bottomNfa.getEndState()][endState] = this.EPSILON;
		topNfa.setStartState(startState);//把原来的nfa改下初终态就可以用了，节省内存
		topNfa.setEndState(endState);
		return topNfa;
	}
}
