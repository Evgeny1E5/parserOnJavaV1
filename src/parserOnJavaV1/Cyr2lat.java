package parserOnJavaV1;

public class Cyr2lat {
	public static String cyr2lat(String line) {
		String lineOut = "";
		char[] chars = line.toLowerCase().toCharArray();
		for(char ch: chars) {
		switch (ch) {
		case '�': lineOut += "a";
		break;
		case '�': lineOut += "b";
		break;
		case '�': lineOut += "v";
		break;
		case '�': lineOut += "g";
		break;
		case '�': lineOut += "d";
		break;
		case '�': lineOut += "e";
		break;
		case '�': lineOut += "yo";
		break;
		case '�': lineOut += "zh";
		break;
		case '�': lineOut += "z";
		break;
		case '�': lineOut += "i";
		break;
		case '�': lineOut += "y";
		break;
		case '�': lineOut += "k";
		break;
		case '�': lineOut += "l";
		break;
		case '�': lineOut += "m";
		break;
		case '�': lineOut += "n";
		break;
		case '�': lineOut += "o";
		break;
		case '�': lineOut += "p";
		break;
		case '�': lineOut += "r";
		break;
		case '�': lineOut += "s";
		break;
		case '�': lineOut += "t";
		break;
		case '�': lineOut += "u";
		break;
		case '�': lineOut += "f";
		break;
		case '�': lineOut += "h";
		break;
		case '�': lineOut += "c";
		break;
		case '�': lineOut += "ch";
		break;
		case '�': lineOut += "sh";
		break;
		case '�': lineOut += "sh";
		break;
		case '�': lineOut += "";
		break;
		case '�': lineOut += "i";
		break;
		case '�': lineOut += "";
		break;
		case '�': lineOut += "e";
		break;
		case '�': lineOut += "yu";
		break;
		case '�': lineOut += "ya";
		break;
		case ' ': lineOut += "_";
		break;
		case '-': lineOut += "_";
		}
		}
		return lineOut;
	}

}
