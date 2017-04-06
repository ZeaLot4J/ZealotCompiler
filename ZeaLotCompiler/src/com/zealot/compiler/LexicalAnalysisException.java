package com.zealot.compiler;

public class LexicalAnalysisException extends Exception {
	public LexicalAnalysisException(char c){
		throw new RuntimeException("Token error:"+c);
	}
}
