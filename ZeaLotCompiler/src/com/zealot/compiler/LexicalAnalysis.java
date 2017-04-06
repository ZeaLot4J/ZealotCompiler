package com.zealot.compiler;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.zealot.compiler.Token.Type;

/**
 * 词法解析
 * @author v5-572G
 *
 */
public class LexicalAnalysis {
	private static enum State{
		NORMAL,				//普通状态，可以转换成其它状态
		IDENTIFIER,			//keyword,number,identifier状态
		SIGN,				//运算符状态
		ANNOTATION,			//注释状态
		STRING,				//字符状态
		REGEXP,				//正则状态
		SPACE				//空白符状态
	}
	private State state;//状态
	private Boolean transferredMeaningSign;//是否是转义
	private final LinkedList<Token> tokenBuffer = new LinkedList<Token>();
	private StringBuffer readBuffer = null;
	private static final Map<Character, Character> TMSTRING = new HashMap<Character, Character>();
	static {
		TMSTRING.put('\"', '\"');
		TMSTRING.put('\'', '\'');
		TMSTRING.put('\\', '\\');
		TMSTRING.put('b', '\b');
		TMSTRING.put('f', '\f');
		TMSTRING.put('t', '\t');
		TMSTRING.put('r', '\r');
		TMSTRING.put('n', '\n');
	}
	public LexicalAnalysis(Reader reader){
		
	}
	
	//通过调用此方法来不断获取Token
	public Token read() throws IOException, LexicalAnalysisException{
		return null;
		
	}
	
	private void refreshBuffer(char c){
		this.readBuffer = new StringBuffer();
		this.readBuffer.append(c);
	}
	private void createToken(Type type){
		Token token = new Token(type,readBuffer.toString());
		this.tokenBuffer.addFirst(token);
		this.readBuffer = null;
	}
	//重点是这个方法:必须理解
	private Boolean readChar(char c)throws LexicalAnalysisException{
		Boolean moveCursor = true;//是否游标读取下一个字符
		Type createType = null;
		
		if(this.state==State.NORMAL){
			if(Character.isLetterOrDigit(c)){//如果读取的字符是字母或数字
				this.state = State.IDENTIFIER;
			}else if('#' == c){
				this.state = State.ANNOTATION;
			}else if('\"'==c || '\''==c){
				this.state = State.STRING;
				this.transferredMeaningSign = false;//此时不是转义
			}else if('`'==c){
				this.state = State.REGEXP;
				this.transferredMeaningSign = false;//此时不是转义
			}else if(' '==c || '\t'==c){
				this.state = State.SPACE;
			}else if('\n'==c){
				createType = Type.NEWLINE;
			}else if('\0'==c){
				createType = Type.ENDSYMBOL;
			}else{
				throw new LexicalAnalysisException(c);
			}
			refreshBuffer(c);
		}else if(this.state==State.IDENTIFIER){
			if(Character.isLetterOrDigit(c)){
				this.readBuffer.append(c);
			}else if('?'==c || '!'==c){
				createType = Type.IDENTIFIER;
				this.readBuffer.append(c);
				this.state = State.NORMAL;
			}else{
				createType = Type.IDENTIFIER;
				this.state = State.NORMAL;
				moveCursor = false;
			}
		}else if(this.state==State.SIGN){
			
		}else if(this.state==State.ANNOTATION){
			if('\n'!=c && '\0'!=c){
				createType = Type.ANNOTATION;
				this.state = State.NORMAL;
				moveCursor = false;
			}
		}else if(this.state==State.REGEXP){
			if(this.transferredMeaningSign){//如果正则表达式中途出现转义
				if(c != '`') {
					throw new LexicalAnalysisException(c);
				}
				readBuffer.append(c);
				transferredMeaningSign = false;
			} else if('\n'==c ) {
				throw new LexicalAnalysisException(c);
			} else if('\0'==c) {
				throw new LexicalAnalysisException(c);
			} else if('\\'==c){//如果是转义
				this.transferredMeaningSign = true;
			} else if('`'==c){
				this.readBuffer.append(c);
				this.state = State.REGEXP;
				createType = Type.REGEXP;
			} else{//正则表达式的匹配字符 
				this.readBuffer.append(c);
			}
		}else if(this.state==State.STRING){
			if('\n'==c ) {
				throw new LexicalAnalysisException(c);
			} else if('\0'==c) {
				throw new LexicalAnalysisException(c);
			} else if(this.transferredMeaningSign){//如果是转义，并且在转义表中找得到这个字符
				Character tms = TMSTRING.get(c);
				if(tms==null){
					throw new LexicalAnalysisException(c);
				}
				this.readBuffer.append(c);
				this.transferredMeaningSign = false;
			} else if('\\'==c){//如果是转义
				this.transferredMeaningSign = true;
			} else{
				char firstChar = this.readBuffer.charAt(0);//取左"
				if(firstChar == c){//如果c是另一个"
					this.state = State.NORMAL;//恢复Normal状态
					createType = Type.STRING;//确定为STRING类型
				}
			}
		}else if(this.state==State.SPACE){
			if(' '==c || '\t'==c){//如果又跟一个空白符
				this.readBuffer.append(c);
			} else{
				this.state = State.NORMAL;
				createType = Type.SPACE;
				moveCursor = false;
			}
		}
		if(createType != null){
			createToken(createType);
		}
		return moveCursor;
	}
	public static void main(String[] args) {
		String str = "^fjkds\\nfj$";
		System.out.println(str);
	}
}
