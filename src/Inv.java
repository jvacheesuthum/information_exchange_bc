import java.io.Serializable;

public class  Inv implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private byte[] pubkey;
	private byte[] sig;
	private int koins;
	
	public Inv (byte[] pubkey, byte[] sig, int koins) {
		this.pubkey = pubkey;
		this.sig = sig;
		this.koins = koins;
	}

	public byte[] getSig() {
		return sig;
	}

	public int getKoins() {
		return koins;
	}

	public byte[] getPub() {
		return pubkey;
	}

	public void setKoins(int i) {
		this.koins = i;		
	}
}
