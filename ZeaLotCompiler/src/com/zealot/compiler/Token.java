package com.zealot.compiler;
import java.util.HashSet;
import java.util.Set;
/**
 * 记号
 * @author v5-572G
 *
 */
public class Token {
	public static enum Type{
		KEYWORD,			//关键字 			if when....
		NUMBER,				//数字			0-9
		IDENTIFIER,			//标识符			用户定义的变量名、函数名
		SIGN,				//运算符			+-*/<>=	
		ANNOTATION,			//注释			#
		STRING,				//字符串			""
		REGEXP,				//正则表达式		``
		SPACE,				//空白符			空格、\t
		NEWLINE,			//回车			\n
		ENDSYMBOL			//结束标志			空或\0...
	}
	private static final Set<String> keywordsSet = new HashSet<String>();
	static{
		keywordsSet.add("if");
		keywordsSet.add("else");
		keywordsSet.add("elseif");
		keywordsSet.add("when");
		keywordsSet.add("while");
		keywordsSet.add("begin");
		keywordsSet.add("until");
		keywordsSet.add("for");
		keywordsSet.add("do");
		keywordsSet.add("try");
		keywordsSet.add("catch");
		keywordsSet.add("finally");
		keywordsSet.add("end");
		keywordsSet.add("def");
		keywordsSet.add("var");
		keywordsSet.add("this");
		keywordsSet.add("null");
		keywordsSet.add("throw");
		keywordsSet.add("break");
		keywordsSet.add("continue");
		keywordsSet.add("return");
		keywordsSet.add("operator");
	}
	private final Type type;
	private final String value;
	
	public Token(Type type, String value){
		if(type == Type.IDENTIFIER){//identifier和keyword、number相似
			Character firstChar = value.charAt(0);
			if(Character.isDigit(firstChar)){//数字类型
				type = Type.NUMBER;//确定类型
			}else if(keywordsSet.contains(firstChar)){	//如果在关键字表中有这个token，则是关键字类型
				type = Type.KEYWORD;//确定类型
			}
		}else if(type == Type.ANNOTATION){//如果是注释
			value = value.substring(1);//取#后面的所有字符，直到回车
		}else if(type == Type.STRING){//如果是字符串
			value = value.substring(1, value.length()-1);//取""之间的(保证"匹配)
		}else if(type == Type.REGEXP){
			value = value.substring(1, value.length()-1);//取``之间的(保证`匹配)
		}else if(type == Type.ENDSYMBOL){
			value = null;
		}
		this.type = type;
		this.value = value;
	}
}
