import java.security.PrivateKey;
import java.security.PublicKey;

public class CommandParser {
	
	// parse a String input and return HistoryEntry object accordingly
	public static HistoryEntry parse(String s, PrivateKey priv, PublicKey pub) { 
		String[] split = s.split(" ");
		for (int i=0;i<split.length;i++) {System.out.println(split[i]);}
		switch(split[0]) {
		case "ADD": return parseAdd(split, priv, pub);
		case "SIGN": return parseSign(split, priv, pub);
		case "REMV":return parseRemv(split, priv, pub);
		default: throw new IllegalArgumentException("Command has to begin with ADD, SIGN or REMV");
		}
	}

	//3 split: REMV [ref] [koins]
	private static HistoryEntry parseRemv(String[] split, PrivateKey priv, PublicKey pub) {
		if (split.length != 3) throw new IllegalArgumentException("Invalid no of argument for REMV");
		int ref = -1;
		int koin = -1;
		try {
			ref = Integer.parseInt(split[1].replaceFirst("@@@","").trim());
			koin = Integer.parseInt(split[2].trim());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new HistoryEntry(Command.REMV,ref,koin, priv, pub);
	}

	//3 split: SIGN [ref] [koins]
	private static HistoryEntry parseSign(String[] split, PrivateKey priv, PublicKey pub) {
		if (split.length != 3) throw new IllegalArgumentException("Invalid no of argument for SIGN");
		int ref = -1;
		int koin = -1;
		try {
			ref = Integer.parseInt(split[1].replaceFirst("@@@","").trim());
			koin = Integer.parseInt(split[2].trim());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new HistoryEntry(Command.SIGN,ref,koin, priv, pub); 
	}

	//3+ split: ADD [array of (ref) and/or (string)] [koins]
	private static HistoryEntry parseAdd(String[] split, PrivateKey priv, PublicKey pub) {
		int koin = -1;
		Data d = null;
		try {
			koin = Integer.parseInt(split[split.length-1].trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(split.length > 3) {
			d = new Data(java.util.Arrays.copyOfRange(split,1,split.length-2));
		} else {
			d = new Data(split[1]);
		}
		return new HistoryEntry(d, koin, priv, pub); 
	}
	
	//returns the index of block referenced (begins with "@@@") otherwise returns -1
	private static int convertRef(String s){
		if (s.startsWith("@@@")) {
			s.replaceFirst("@@@", "");
			try {
				Integer.parseInt(s.trim());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Please provide only integer index after @@@");
				return -1;
			}
		}
		return -1;
	}

}
