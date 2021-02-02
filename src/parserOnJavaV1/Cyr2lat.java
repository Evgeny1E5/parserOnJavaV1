package parserOnJavaV1;

public class Cyr2lat {
	public static String cyr2lat(String line) {
		String lineOut = "";
		char[] chars = line.toLowerCase().toCharArray();
		for(char ch: chars) {
		switch (ch) {
		case 'à': lineOut += "a";
		break;
		case 'á': lineOut += "b";
		break;
		case 'â': lineOut += "v";
		break;
		case 'ã': lineOut += "g";
		break;
		case 'ä': lineOut += "d";
		break;
		case 'å': lineOut += "e";
		break;
		case '¸': lineOut += "yo";
		break;
		case 'æ': lineOut += "zh";
		break;
		case 'ç': lineOut += "z";
		break;
		case 'è': lineOut += "i";
		break;
		case 'é': lineOut += "y";
		break;
		case 'ê': lineOut += "k";
		break;
		case 'ë': lineOut += "l";
		break;
		case 'ì': lineOut += "m";
		break;
		case 'í': lineOut += "n";
		break;
		case 'î': lineOut += "o";
		break;
		case 'ï': lineOut += "p";
		break;
		case 'ð': lineOut += "r";
		break;
		case 'ñ': lineOut += "s";
		break;
		case 'ò': lineOut += "t";
		break;
		case 'ó': lineOut += "u";
		break;
		case 'ô': lineOut += "f";
		break;
		case 'õ': lineOut += "h";
		break;
		case 'ö': lineOut += "c";
		break;
		case '÷': lineOut += "ch";
		break;
		case 'ø': lineOut += "sh";
		break;
		case 'ù': lineOut += "sh";
		break;
		case 'ú': lineOut += "";
		break;
		case 'û': lineOut += "i";
		break;
		case 'ü': lineOut += "";
		break;
		case 'ý': lineOut += "e";
		break;
		case 'þ': lineOut += "yu";
		break;
		case 'ÿ': lineOut += "ya";
		break;
		case ' ': lineOut += "_";
		break;
		case '-': lineOut += "_";
		}
		}
		return lineOut;
	}

}
