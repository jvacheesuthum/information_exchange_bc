import java.security.PrivateKey;
import java.security.PublicKey;

public class CommandParser {
	
	// parse a String input and return HistoryEntry object accordingly
	public static HistoryEntry parse(String s, PrivateKey priv, PublicKey pub) { 
		String[] split = s.split(" ");
		switch(split[0]) {
		case "ADD": return parseAdd(split, priv, pub);
		case "SIGN": return parseSignRemv(Command.SIGN, split, priv, pub);
		case "REMV":return parseSignRemv(Command.REMV, split, priv, pub);
		default: throw new IllegalArgumentException("Command has to begin with ADD, SIGN or REMV");
		}
	}

	private static HistoryEntry parseSignRemv(Command c, String[] split, PrivateKey priv, PublicKey pub) {
		if (split.length != 3) throw new IllegalArgumentException("Invalid no of argument for SIGN, REMV");
		int ref = -1;
		int koin = -1;
		try {
			ref = convertRef(split[1]);
			koin = Integer.parseInt(split[2].trim());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new HistoryEntry(c, ref, koin, priv, pub);
	}


	private static HistoryEntry parseAdd(String[] split, PrivateKey priv, PublicKey pub) {
		int koin = -1;
		Data d = null;
		try {
			koin = Integer.parseInt(split[split.length-1].trim());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("invalid amount of koins - higher than integer MAX");
		}
		
		if(split.length > 3) {
			d = new Data(java.util.Arrays.copyOfRange(split,1,split.length-1));
		} else {
			d = new Data(split[1]);
		}
		return new HistoryEntry(d, koin, priv, pub); 
	}
	
	//returns the index of block referenced (begins with "@@@") otherwise returns -1
	private static int convertRef(String s){
		if (s.startsWith("@@@")) s.replaceFirst("@@@", "");
		int i;
		try {
			i = Integer.parseInt(s.trim());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Please provide only integer index after @@@");
			return -1;
		}
		
		return i;
	}

}
