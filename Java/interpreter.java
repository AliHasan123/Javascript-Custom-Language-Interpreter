import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;
import java.lang.*;

public class hw4 {

	static Stack<HashMap<String, String>> environmentStack = new Stack<HashMap<String,String>>();
	static Stack<String> outputStack = new Stack<String>();
	static Stack<HashMap<String,Closure>> funNameClosureStack = new Stack<HashMap<String,Closure>>();
	static String parameterName = "";
	static String funcName = "";
	static boolean ifFunctionStartSeen = false;
	static Queue<String> def = new LinkedList<String>();
	
	private class Closure {
		HashMap<String,String> _currentEnv;
		Queue<String> _funcDef;
		String _parameterName;
		public Closure(HashMap<String,String> currentEnv, Queue<String> funcDef, String paramName) {
			_currentEnv = currentEnv;
			_funcDef = funcDef;
			_parameterName = paramName;
		}
		
		public HashMap<String,String> getCurrentEnv() {
			return _currentEnv;
		}
		
		public Queue<String> getFuncDef() {
			return _funcDef;
		}
		
		public String getParameterName() {
			return _parameterName;
		}
	}

	public static String stackContains(String s) {
		String ans = "";
		int stackNumber = environmentStack.size() - 1;
		Stack<HashMap<String, String>> tempStack = new Stack<HashMap<String,String>>();
		tempStack = (Stack<HashMap<String, String>>) environmentStack.clone();

		while(stackNumber >= 0) {
			if (tempStack.peek().containsKey(s)) {
				ans = tempStack.peek().get(s);
				return ans;
			}
			else {
				tempStack.pop();
				stackNumber = stackNumber - 1;
			}
		}
		return ans;
	}

	public static boolean alreadyExist(String s) {
		Stack<HashMap<String, String>> tempStack = new Stack<HashMap<String,String>>();
		tempStack = (Stack<HashMap<String, String>>) environmentStack.clone();
		if(tempStack.peek().containsKey(s)) {
			return true;
		}
	return false;
	}
	
//	public static int alreadyExist(String s) {
//		int stackNumber = environmentStack.size() - 1;
//		Stack<HashMap<String, String>> tempStack = new Stack<HashMap<String,String>>();
//		tempStack = (Stack<HashMap<String, String>>) environmentStack.clone();
//
//		
//		while(stackNumber >= 0) {
//			if (tempStack.peek().containsKey(s)) {
//				return stackNumber;
//			}
//			else {
//				tempStack.pop();
//				stackNumber = stackNumber - 1;
//			}
//		}
//		return -1;
//	}


	public void keyOverride(String s) {
		int stackNumber = environmentStack.size() - 1;
		Stack<HashMap<String, String>> tempStack = new Stack<HashMap<String,String>>();
		tempStack = (Stack<HashMap<String, String>>) environmentStack.clone();

		while(stackNumber >= 0) {
			if (tempStack.peek().containsKey(s)) {
			}
			else {
				tempStack.pop();
				stackNumber = stackNumber - 1;
			}
		}
	}
	
	public static void andMethod(String one, String two) {
		if(one == ":true:" && two == ":true:") {
			outputStack.pop();
			outputStack.pop();
			outputStack.push(":true:");
		}

		else if(one == ":false:" && two == ":false:") {
			outputStack.pop();
			outputStack.pop();
			outputStack.push(":false:");
		}

		else if(one == ":false:" && two == ":true:" || one == ":true:" && two == ":false:") {
			outputStack.pop();
			outputStack.pop();
			outputStack.push(":false:");
		}
		else {
			outputStack.push(":error:");
		}
	}
	
	public static void orMethod(String one, String two) {
		if(one == ":false:" && two == ":true:" || one == ":true:" && two == ":false:" || one == ":true:" && two == ":true:") {
			outputStack.pop();
			outputStack.pop();
			outputStack.push(":true:");
		}
		else if(one == ":false:" && two == ":false:") {
			outputStack.pop();
			outputStack.pop();
			outputStack.push(":false:");
		}
		else {
			outputStack.push(":error:");
		}
	}
	
	public static void notMethod(String one) {
		if(one == ":true:") {
			outputStack.pop();
			outputStack.push(":false:");
		}
		else if(one == ":false:") {
			outputStack.pop();
			outputStack.push(":true:");
		}
		else {
			outputStack.push(":error:");
		}
	}
	
	public static void ifMethod(String one, String two, String three) {
		if (three == ":true:") {
			outputStack.pop();
			outputStack.pop();
			outputStack.pop();
			outputStack.push(one);
		}

		else if(three == ":false:") {
			outputStack.pop();
			outputStack.pop();
			outputStack.pop();
			outputStack.push(two);
		}

		else {
			outputStack.push(":error:");
		}
	}
	
	public static int numberOfExcl() {
		int num = 0;
		Stack<String> tempStack = new Stack<String>();
		tempStack = (Stack<String>) outputStack.clone();
		while(tempStack.size() != 0) {
			if(tempStack.pop() == "!") {
				num = num + 1;
			}
		}
		return num;
	}
	
	public static void eval(String l, FileReader r, BufferedReader b, FileWriter o) {
		StringTokenizer tokenizer = new StringTokenizer(l);
		String lineType = tokenizer.nextToken();
		
		if(ifFunctionStartSeen == true) {
			if(lineType.equalsIgnoreCase("funEnd")) {
				ifFunctionStartSeen = false;
				hw4 i = new hw4();
				Queue<String> definition = new LinkedList(def);
				if(environmentStack.size() == 0) {
					environmentStack.push(new HashMap<String,String>());
				}
				Stack<HashMap<String, String>> tempStack = new Stack<HashMap<String,String>>();
				tempStack = (Stack<HashMap<String, String>>) environmentStack.clone();
				Closure c = i.new Closure(tempStack.peek(), definition, parameterName);
				funNameClosureStack.push(new HashMap<String,Closure>());
				funNameClosureStack.peek().put(funcName, c);
				outputStack.push(":unit:");
				def.clear();
				return;
			}
			else {
				def.add(l);
				return;
			}
		}
		
		switch(lineType) {

		case "push":
			try {
				String numberString = tokenizer.nextToken();
				if(numberString.charAt(0) == '"') {
					String literal = numberString.substring(1,numberString.length() - 1);
					outputStack.push(literal);
				}
				else if(Character.toLowerCase(numberString.charAt(0)) >= 'a' && Character.toLowerCase(numberString.charAt(0)) <= 'z') {
					outputStack.push("," + numberString);
				}
				else {
					int number = Integer.parseInt(numberString);
					if (numberString == "-0") {
						outputStack.push("0");
					}
					outputStack.push(Integer.toString(number));
				}
			}
			catch(NumberFormatException e) {
				outputStack.push(":error:");
			}
			break;

		case "pop":
			try {
				outputStack.pop();
			}
			catch(EmptyStackException e) {
				outputStack.push(":error:");
			}
			break;

		case ":true:":
			outputStack.push(":true:");
			break;

		case ":false:":
			outputStack.push(":false:");
			break;

		case ":error:":
			outputStack.push(":error:");
			break;

		case "add":
			try {
				String one = outputStack.get(outputStack.size() - 1);
				String two = outputStack.get(outputStack.size() - 2);

				if(one.charAt(0) == ',' && two.charAt(0) == ',') {
					if(stackContains(one) == "" || stackContains(two) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(stackContains(one));
						int numberTwo = Integer.parseInt(stackContains(two));
						outputStack.pop();
						outputStack.pop();
						int sum = numberOne + numberTwo;
						outputStack.push(Integer.toString(sum));
					}
				}

				else if (one.charAt(0) == ',' && two.charAt(0) != ',') {
					if(stackContains(one) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(stackContains(one));
						int numberTwo = Integer.parseInt(outputStack.get(outputStack.size() - 2));
						outputStack.pop();
						outputStack.pop();
						int sum = numberOne + numberTwo;
						outputStack.push(Integer.toString(sum));
					}
				}

				else if (one.charAt(0) != ',' && two.charAt(0) == ',') {
					if(stackContains(two) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(outputStack.get(outputStack.size() - 1));
						int numberTwo = Integer.parseInt(stackContains(two));
						outputStack.pop();
						outputStack.pop();
						int sum = numberOne + numberTwo;
						outputStack.push(Integer.toString(sum));
					}
				}

				else {
					int numberOne = Integer.parseInt(outputStack.get(outputStack.size() - 1));
					int numberTwo = Integer.parseInt(outputStack.get(outputStack.size() - 2));
					outputStack.pop();
					outputStack.pop();
					int sum = numberOne + numberTwo;
					outputStack.push(Integer.toString(sum));
				}
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "sub":
			try {
				String one = outputStack.get(outputStack.size() - 1);
				String two = outputStack.get(outputStack.size() - 2);

				if(one.charAt(0) == ',' && two.charAt(0) == ',') {
					if(stackContains(one) == "" || stackContains(two) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(stackContains(one));
						int numberTwo = Integer.parseInt(stackContains(two));
						outputStack.pop();
						outputStack.pop();
						int difference = numberTwo - numberOne;
						outputStack.push(Integer.toString(difference));
					}
				}

				else if (one.charAt(0) == ',' && two.charAt(0) != ',') {
					if(stackContains(one) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(stackContains(one));
						int numberTwo = Integer.parseInt(outputStack.get(outputStack.size() - 2));
						outputStack.pop();
						outputStack.pop();
						int difference = numberTwo - numberOne;
						outputStack.push(Integer.toString(difference));
					}
				}

				else if (one.charAt(0) != ',' && two.charAt(0) == ',') {
					if(stackContains(two) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(outputStack.get(outputStack.size() - 1));
						int numberTwo = Integer.parseInt(stackContains(two));
						outputStack.pop();
						outputStack.pop();
						int difference = numberTwo - numberOne;
						outputStack.push(Integer.toString(difference));
					}
				}

				else {
					int numberOne = Integer.parseInt(outputStack.get(outputStack.size() - 1));
					int numberTwo = Integer.parseInt(outputStack.get(outputStack.size() - 2));
					outputStack.pop();
					outputStack.pop();
					int difference = numberTwo - numberOne;
					outputStack.push(Integer.toString(difference));
				}
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "mul":
			try {
				String one = outputStack.get(outputStack.size() - 1);
				String two = outputStack.get(outputStack.size() - 2);

				if(one.charAt(0) == ',' && two.charAt(0) == ',') {
					if(stackContains(one) == "" || stackContains(two) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(stackContains(one));
						int numberTwo = Integer.parseInt(stackContains(two));
						outputStack.pop();
						outputStack.pop();
						int mul = numberOne*numberTwo;
						outputStack.push(Integer.toString(mul));
					}
				}

				else if (one.charAt(0) == ',' && two.charAt(0) != ',') {
					if(stackContains(one) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(stackContains(one));
						int numberTwo = Integer.parseInt(outputStack.get(outputStack.size() - 2));
						outputStack.pop();
						outputStack.pop();
						int mul = numberOne*numberTwo;
						outputStack.push(Integer.toString(mul));
					}
				}

				else if (one.charAt(0) != ',' && two.charAt(0) == ',') {
					if(stackContains(two) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(outputStack.get(outputStack.size() - 1));
						int numberTwo = Integer.parseInt(stackContains(two));
						outputStack.pop();
						outputStack.pop();
						int mul = numberOne*numberTwo;
						outputStack.push(Integer.toString(mul));
					}
				}

				else {
					int numberOne = Integer.parseInt(outputStack.get(outputStack.size() - 1));
					int numberTwo = Integer.parseInt(outputStack.get(outputStack.size() - 2));
					outputStack.pop();
					outputStack.pop();
					int mul = numberOne*numberTwo;
					outputStack.push(Integer.toString(mul));
				}
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "div":
			try {
				String one = outputStack.get(outputStack.size() - 1);
				String two = outputStack.get(outputStack.size() - 2);

				if(one.charAt(0) == ',' && two.charAt(0) == ',') {
					if(stackContains(one) == "" || stackContains(two) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(stackContains(one));
						int numberTwo = Integer.parseInt(stackContains(two));
						if(numberOne == 0) {
							throw new Exception();
						}
						outputStack.pop();
						outputStack.pop();
						int div = numberTwo/numberOne;
						outputStack.push(Integer.toString(div));
					}
				}

				else if (one.charAt(0) == ',' && two.charAt(0) != ',') {
					if(stackContains(one) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(stackContains(one));
						int numberTwo = Integer.parseInt(outputStack.get(outputStack.size() - 2));
						if(numberOne == 0) {
							throw new Exception();
						}
						outputStack.pop();
						outputStack.pop();
						int div = numberTwo/numberOne;
						outputStack.push(Integer.toString(div));
					}
				}

				else if (one.charAt(0) != ',' && two.charAt(0) == ',') {
					if(stackContains(two) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(outputStack.get(outputStack.size() - 1));
						int numberTwo = Integer.parseInt(stackContains(two));
						if(numberOne == 0) {
							throw new Exception();
						}
						outputStack.pop();
						outputStack.pop();
						int div = numberTwo/numberOne;
						outputStack.push(Integer.toString(div));
					}
				}

				else {
					int numberOne = Integer.parseInt(outputStack.get(outputStack.size() - 1));
					int numberTwo = Integer.parseInt(outputStack.get(outputStack.size() - 2));
					if(numberOne == 0) {
						throw new Exception();
					}
					outputStack.pop();
					outputStack.pop();
					int div = numberTwo/numberOne;
					outputStack.push(Integer.toString(div));
				}
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "rem":
			try {
				String one = outputStack.get(outputStack.size() - 1);
				String two = outputStack.get(outputStack.size() - 2);

				if(one.charAt(0) == ',' && two.charAt(0) == ',') {
					if(stackContains(one) == "" || stackContains(two) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(stackContains(one));
						int numberTwo = Integer.parseInt(stackContains(two));
						outputStack.pop();
						outputStack.pop();
						int remainder = numberTwo%numberOne;
						outputStack.push(Integer.toString(remainder));
					}
				}

				else if (one.charAt(0) == ',' && two.charAt(0) != ',') {
					if(stackContains(one) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(stackContains(one));
						int numberTwo = Integer.parseInt(outputStack.get(outputStack.size() - 2));
						outputStack.pop();
						outputStack.pop();
						int remainder = numberTwo%numberOne;
						outputStack.push(Integer.toString(remainder));
					}
				}

				else if (one.charAt(0) != ',' && two.charAt(0) == ',') {
					if(stackContains(two) == "") {
						outputStack.push(":error:");
					}
					else {
						int numberOne = Integer.parseInt(outputStack.get(outputStack.size() - 1));
						int numberTwo = Integer.parseInt(stackContains(two));
						outputStack.pop();
						outputStack.pop();
						int remainder = numberTwo%numberOne;
						outputStack.push(Integer.toString(remainder));
					}
				}

				else {
					int numberOne = Integer.parseInt(outputStack.get(outputStack.size() - 1));
					int numberTwo = Integer.parseInt(outputStack.get(outputStack.size() - 2));
					outputStack.pop();
					outputStack.pop();
					int remainder = numberTwo%numberOne;
					outputStack.push(Integer.toString(remainder));
				}
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "neg":
			try {
				String one = outputStack.get(outputStack.size() - 1);
				if(one.charAt(0) == ',') {
					if(stackContains(one) == "") {
						outputStack.push(":error:");
					}

					else {
						int numberOne = Integer.parseInt(stackContains(one));
						outputStack.pop();
						int negation = -1*numberOne;
						outputStack.push(Integer.toString(negation));
					}
				}
				else {
					int numberOne = Integer.parseInt(outputStack.get(outputStack.size() - 1));
					outputStack.pop();
					int negation = -1*numberOne;
					outputStack.push(Integer.toString(negation));
				}
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "swap":
			try {
				String one = outputStack.get(outputStack.size() - 1);
				String two = outputStack.get(outputStack.size() - 2);
				outputStack.pop();
				outputStack.pop();
				outputStack.push(one);
				outputStack.push(two);
			}

			catch(ArrayIndexOutOfBoundsException e){
				outputStack.push(":error:");
			}
			break;

		case "and":
			try {
				String one = outputStack.get(outputStack.size() - 1);
				String two = outputStack.get(outputStack.size() - 2);
				if(one.charAt(0) == ',' && two.charAt(0) == ',') {
					andMethod(stackContains(one), stackContains(two));
				}
				else if(one.charAt(0) == ',' && two.charAt(0) != ',') {
					andMethod(stackContains(one), two);
				}
				else if(one.charAt(0) != ',' && two.charAt(0) == ',') {
					andMethod(one, stackContains(two));
				}
				else {
					andMethod(one,two);
				}
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "or":
			try {
				String one = outputStack.get(outputStack.size() - 1);
				String two = outputStack.get(outputStack.size() - 2);
				if(one.charAt(0) == ',' && two.charAt(0) == ',') {
					orMethod(stackContains(one), stackContains(two));
				}
				else if(one.charAt(0) == ',' && two.charAt(0) != ',') {
					orMethod(stackContains(one), two);
				}
				else if(one.charAt(0) != ',' && two.charAt(0) == ',') {
					orMethod(one, stackContains(two));
				}
				else {
					orMethod(one,two);
				}
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "not":
			try {
				String one = outputStack.get(outputStack.size() - 1);
				if(one.charAt(0) == ',') {
					notMethod(stackContains(one));
				}
				else {
					notMethod(one);
				}
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "equal":
			try {
				String one = outputStack.get(outputStack.size() - 1);
				String two = outputStack.get(outputStack.size() - 2);
				if(one.charAt(0) == ',' && two.charAt(0) == ',' ) {
					if(stackContains(one) == "" || stackContains(two) == "") {
						outputStack.push(":error:");
					}
					else if(Integer.parseInt(stackContains(one)) == Integer.parseInt(stackContains(two))) {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":true:");
					}
					else {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":false:");
					}
				}

				else if(one.charAt(0) == ',' && two.charAt(0) != ',') {
					int twoInt = Integer.parseInt(two);
					if(stackContains(one) == "") {
						outputStack.push(":error:");
					}
					else if(Integer.parseInt(stackContains(one)) == twoInt) {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":true:");
					}
					else {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":false:");
					}
				}

				else if(one.charAt(0) != ',' && two.charAt(0) == ',') {
					int oneInt = Integer.parseInt(one);
					if(stackContains(two) == "") {
						outputStack.push(":error:");
					}
					else if(Integer.parseInt(stackContains(two)) == oneInt) {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":true:");
					}
					else {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":false:");
					}
				}

				else {
					int oneInt = Integer.parseInt(one);
					int twoInt = Integer.parseInt(two);
					if(oneInt == twoInt) {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":true:");
					}
					else {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":false:");
					}
				}
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "lessThan":
			try {
				String one = outputStack.get(outputStack.size() - 1);
				String two = outputStack.get(outputStack.size() - 2);
				if(one.charAt(0) == ',' && two.charAt(0) == ',' ) {
					if(stackContains(one) == "" || stackContains(two) == "") {
						outputStack.push(":error:");
					}
					else if(Integer.parseInt(stackContains(two)) < Integer.parseInt(stackContains(one)) ) {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":true:");
					}
					else {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":false:");
					}
				}

				else if(one.charAt(0) == ',' && two.charAt(0) != ',') {
					int twoInt = Integer.parseInt(two);
					if(stackContains(one) == "") {
						outputStack.push(":error:");
					}
					else if(twoInt < Integer.parseInt(stackContains(one))) {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":true:");
					}
					else {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":false:");
					}
				}

				else if(one.charAt(0) != ',' && two.charAt(0) == ',') {
					int oneInt = Integer.parseInt(one);
					if(stackContains(two) == "") {
						outputStack.push(":error:");
					}
					else if(Integer.parseInt(stackContains(two)) < oneInt) {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":true:");
					}
					else {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":false:");
					}
				}

				else {
					int oneInt = Integer.parseInt(one);
					int twoInt = Integer.parseInt(two);
					if (twoInt < oneInt) {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":true:");
					}
					else {
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":false:");
					}
				}
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "bind":
			try {
				String one = outputStack.get(outputStack.size() - 1);
				String two = outputStack.get(outputStack.size() - 2);

				if(one == ":error:") {
					outputStack.push(":error:");
				}

				else if(one.charAt(0) == ',' && stackContains(one) == "") {
					outputStack.push(":error:");
				}

				else if(two.charAt(0) == ',') {
					if(environmentStack.size() == 0) {
						environmentStack.push(new HashMap<String,String>());
					}
					if(alreadyExist(two) == true) {
						//environmentStack.get(alreadyExist(two)).remove(two);
						environmentStack.peek().remove(two);
						environmentStack.peek().put(two,one);
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":unit:");
					}
					else if(one.charAt(0) == ',' && stackContains(one) != "") {
						environmentStack.peek().put(two, stackContains(one));
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":unit:");
					}

					else {
						environmentStack.peek().put(two,one);
						outputStack.pop();
						outputStack.pop();
						outputStack.push(":unit:");
					}
				}

				else {
					outputStack.push(":error:");
				}
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "if":
			try {
				String one = outputStack.get(outputStack.size() - 1);
				String two = outputStack.get(outputStack.size() - 2);
				String three = outputStack.get(outputStack.size() - 3);
				if(three.charAt(0) == ',') {
					ifMethod(one,two,stackContains(three));
				}
				else {
					ifMethod(one,two,three);
				}
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "let":
			try {
				environmentStack.push(new HashMap<String,String>());
				outputStack.push("!");
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "end":
			try {
				int numberPopped = 0;
				int numberOfExcl = numberOfExcl();
				environmentStack.pop();
				String str = outputStack.peek();
				while(outputStack.peek() != "!") {
					outputStack.pop();
					numberPopped = numberPopped + 1;
				}
				outputStack.pop();
				if((numberPopped >= 1 && numberOfExcl != 1) || (numberPopped >= 1 && numberOfExcl == 1)) {
					outputStack.push(str);
				}
				
			}

			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;
			
		case "fun":
			try {
				funcName = tokenizer.nextToken();
				parameterName = ',' + tokenizer.nextToken();
				ifFunctionStartSeen = true;
			}
			
			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;

		case "call":
			try {
				String fName = outputStack.get(outputStack.size() - 1);
				fName = fName.substring(1, fName.length());
				String argName = outputStack.get(outputStack.size() - 2);
				String argNameValue = "";
				boolean solve = false;
				
				if(!funNameClosureStack.peek().containsKey(fName)) {
					outputStack.push(":error:");
				}
				
				if(argName == ":error:") {
					outputStack.push(":error:");
				}
				
				else if(argName.charAt(0) == ',') {
					if(stackContains(argName) == "") {
						outputStack.push(":error:");
						break;
					}
					else {
						argNameValue = stackContains(argName);
						solve = true;
					}
					
				}
				
				else {
					solve = true;
				}

				if(solve = true) {
					outputStack.pop();
					outputStack.pop();
					Closure c = funNameClosureStack.peek().get(fName);
					String last = "";
					if(argNameValue != "") {
						(c.getCurrentEnv()).put(c.getParameterName(), argNameValue);
					}
					else {
						(c.getCurrentEnv()).put(c.getParameterName(), argName);
					}
					environmentStack.push(c.getCurrentEnv());
					outputStack.push("!!");
					Queue<String> tempQ = new LinkedList<String>(c.getFuncDef());
					while(tempQ.size() != 0) {
						if(tempQ.size() == 1) {
							last = tempQ.peek();
						}
						eval(tempQ.poll(),r,b,o);
					}
					if(last.equalsIgnoreCase("return") == false) {
						while(outputStack.peek() != "!!") {
							outputStack.pop();
						}
						outputStack.pop();
					}
					
				}
			}
			
			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;
			
		case "return":
			try {
				String topS = outputStack.peek();
				while(outputStack.peek() != "!!") {
					outputStack.pop();
				}
				outputStack.pop();
				if(stackContains(topS) != "") {
					outputStack.push(stackContains(topS));
				}
				else {
					outputStack.push(topS);
				}
				environmentStack.pop();
			}
			
			catch(Exception e) {
				outputStack.push(":error:");
			}
			break;
			
		case "quit":
			while(outputStack.size() != 1){
				String s = outputStack.peek();
				if(s.charAt(0) == ',') {
					outputStack.pop();
					try {
						o.write(s.substring(1, s.length()) + '\n');
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					try {
						o.write(outputStack.pop() + '\n');
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			String s = outputStack.peek();
			if(s.charAt(0) == ',') {
				outputStack.pop();
				try {
					o.write(s.substring(1, s.length()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else {
				try {
					o.write(outputStack.pop());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		}
	}

	public static void hw4(String inFile, String outFile){

		String l = null;
		try {

			FileReader r = new FileReader(inFile);
			BufferedReader b = new BufferedReader(r);
			FileWriter o = new FileWriter(outFile);

			l = b.readLine();

			while(l != null) {
				eval(l,r,b,o);
				l = b.readLine();
			}
			o.close();
		}

		catch (FileNotFoundException e) {
			System.err.println("FileNotFoundException");
			e.printStackTrace();
		}
		catch(IOException e) {
			System.err.println("IOException");
			e.printStackTrace();
		}

		catch(Exception e) {
			e.printStackTrace();
			System.err.println("Some other exception");
		}

		finally {

		}
	}

}

