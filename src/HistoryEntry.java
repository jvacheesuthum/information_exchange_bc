
public class HistoryEntry {
	
	private Command c;
	private Data d;
	private int ref; //block reference for remove and sign
	private int koins; //for ease of use the smallest unit in this system is 1, e.g. no decimal points
	private String signature;
	
	public HistoryEntry(Data d, int koins, String signature) { //this constructor is for ADD command
		this.c = null; //ADD is not included in the Command enum
		this.d = d;
		this.ref = -1;
		this.koins = koins;
		this.signature = signature;

		//TODO need some way to check if this add already exist -> change to sign instead
	}
	
	public HistoryEntry(Command c, int ref, int koins, String signature) { //for SIGN and REMOVE
		this.c = c;
		this.d = null;
		this.ref = ref;
		this.koins = koins;
		this.signature = signature;
	}
	
	public Command getCommand(){
		return c;
	}
	
	public Data getData(){
		return d;
	}
	
	public int getRef(){
		return ref;
	}
	
	public int getKoins(){
		return koins;
	}
	
	public String getSig() {
		return signature;
	}
	
	@Override
	public String toString(){
		//TODO fix this later -> nullpointer exception
		return "entry";//c + d.toString() + ref + ' ' + koins;
	}
	
}
