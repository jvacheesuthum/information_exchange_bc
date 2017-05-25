import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;


//TODO main doesnt need sig, maybe publickey instead
public class MainBlockEntry {
	private Data data;
	//this is signature-koins pair, change the type later java.security.signature?????
	private List<Inv> investments;
	private int totalKoins;
	private int index; // would it be easier if it's a field like this of just use indexOf()
	private static int counter = 0; //use to keep up with index, maybe unnecessary, check above
	
	public MainBlockEntry(Data data, int totalKoins) {
		this.data= data;
		this.investments = new ArrayList<Inv>();
		this.totalKoins = totalKoins;
		this.index = counter;
		counter++;
	}
	
	public void invest(byte[] pub, byte[] sig, int koins) {
		totalKoins += koins;
		investments.add(new Inv(pub, sig, koins));
	}
	
	public void removeInvest(byte[] pub, int koins) {
		int index = searchPub(pub);
		if (index > -1) {
			int k = investments.get(index).getKoins();
			if (k == koins) {				 //removing all koins
				totalKoins -= k;
				investments.remove(index);
			} else if (k > koins) { 		//removing only some investments
				totalKoins -= koins;
				investments.get(index).setKoins(k - koins);
			} else { 						//command asks to remove more tha invested, fail
				System.out.println("User has invested only " + k + " koins in this block");
			}
		} else {
			System.out.println("User has not invest any koins in this block, unable to remove");
		}
		
	}
	
	/*
	//combine another duplicate of same entry. arg is mainblockentry object or pair
	public void combineDup(Pair<byte[], Integer> pair) { //TODO this is exactly the same as invest..
		totalKoins += pair.getR();
		investments.add(pair);
	}
	
	public void combineDup(MainBlockEntry e) {
		investments.addAll(e.getInvestments());
		totalKoins += e.getTotalKoins();
		
	}*/
	
	public int getTotalKoins(){
		return totalKoins;
	}
	
	public Collection<Inv> getInvestments() {
		return investments;
	}

	public Data getData() {
		return data;
	}
	
	public int getIndex() {
		return index;
	}
	
	//iterate through investments and search for match in publickey ONLY, return index if found, -1 otherwise
	private int searchPub(byte[] needle) {
		int count = 0;
		for (Inv p : investments) {

			if(Base64.getEncoder().encodeToString(needle).equals(Base64.getEncoder().encodeToString(p.getPub()))) {
				System.out.println("count ======= " + count);
				return count;
			}
			count++;
		}
		return -1;
	}
	
	//reset counter again for when recompiling
	public static void resetCounter() {
		counter = 0;
	}
	
	@Override
	public String toString(){
		String result = "At index: " + index + " data = " +data.toString() + " koins: " + totalKoins + '\n';
		for (Inv p : investments) {
			//TODO after encoding to string it's very long -> find a way to shorten
			result += "[ " + (p.getPub()) + " invests " + p.getKoins() + " with signature: " + p.getSig() + " ]\n";
		}
		return result;
	}



	
}
