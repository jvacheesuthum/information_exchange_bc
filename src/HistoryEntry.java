import java.security.*;
import java.util.Base64;

public class HistoryEntry {
	
	private Command c;
	private Data d;
	private int ref; //block reference for remove and sign
	private int koins; //for ease of use the smallest unit in this system is 1, e.g. no decimal points
	private byte[] signature; //get from sign() function in Signature object
	private PublicKey pubkey;
	
	public HistoryEntry(Data d, int koins, PrivateKey priv, PublicKey pub) { //this constructor is for ADD command
		this.c = Command.ADD; 
		this.d = d;
		this.ref = -1;
		this.koins = koins;
		this.signature = generateSig(priv);
		this.pubkey = pub;

	}
	
	public HistoryEntry(Command c, int ref, int koins, PrivateKey priv, PublicKey pub) { //for SIGN and REMOVE
		this.c = c;
		this.d = null;
		this.ref = ref;
		this.koins = koins;
		this.signature = generateSig(priv);
		this.pubkey = pub;
	}
	
	//proof of mining (for every 1 koin hence there's no no of koins
	public HistoryEntry(PublicKey pub, String hashcashStamp) {
		this.c = Command.MINED;
		this.pubkey = pub;
		this.d = new Data(hashcashStamp);
	}
	
	private byte[] generateSig(PrivateKey priv) {
		Signature dsa;
		try {
			dsa = Signature.getInstance("SHA1withDSA", "SUN");
			dsa.initSign(priv);
			String s;
			if (d == null) {
				s = this.c.toString() + this.ref + this.koins; 
			} else {
				s = this.c + this.d.toString() +this.ref + this.koins; 
			}
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
		if (d != null) return c.toString() + d.toString() + ref + ' ' + koins;
		return c.toString() + ' ' + ref + ' ' + koins + "\n";
	}

	public byte[] getPub() {
		//TODO return pubkey.getEncoded();
		return Base64.getDecoder().decode("hellotestpubkey");
	}
	
	//for unencoded
	public PublicKey getPubkey() {
		return pubkey;
	}
	
}
