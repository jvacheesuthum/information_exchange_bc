import java.security.*;

public class HistoryEntry {
	
	private Command c;
	private Data d;
	private int ref; //block reference for remove and sign
	private int koins; //for ease of use the smallest unit in this system is 1, e.g. no decimal points
	private byte[] signature; //get from sign() function in Signature object
	
	public HistoryEntry(Data d, int koins, PrivateKey priv) { //this constructor is for ADD command
		this.c = Command.ADD; 
		this.d = d;
		this.ref = -1;
		this.koins = koins;
		this.signature = generateSig(priv);

	}
	
	public HistoryEntry(Command c, int ref, int koins, PrivateKey priv) { //for SIGN and REMOVE
		this.c = c;
		this.d = null;
		this.ref = ref;
		this.koins = koins;
		this.signature = generateSig(priv);
	}
	
	private byte[] generateSig(PrivateKey priv) {
		Signature dsa;
		try {
			dsa = Signature.getInstance("SHA1withDSA", "SUN");
			dsa.initSign(priv);
			String s = this.c + this.d.toString() +this.ref + this.koins; 
			byte[] toSign = s.getBytes(); //convert this command into bytes to be signed
			dsa.update(toSign);
			return dsa.sign();

		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
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
	
	public byte[] getSig() {
		return signature;
	}
	
	@Override
	public String toString(){
		//TODO fix this later -> nullpointer exception
		return "entry";//c + d.toString() + ref + ' ' + koins;
	}
	
}
