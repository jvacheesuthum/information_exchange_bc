import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


//TODO main doesnt need sig, maybe publickey instead
public class MainBlockEntry {
	private Data data;
	//this is signature-koins pair, change the type later java.security.signature?????
	private List<Pair<byte[], Integer>> investments;
	private int totalKoins;
	private int index; // would it be easier if it's a field like this of just use indexOf()
	private static int counter = 0; //use to keep up with index, maybe unnecessary, check above
	
	public MainBlockEntry(Data data, int totalKoins) {
		this.data= data;
		this.investments = new ArrayList<Pair<byte[], Integer>>();
		this.totalKoins = totalKoins;
		this.index = counter;
		counter++;
	}
	
	public void invest(byte[] sig, int koins) {
		totalKoins += koins;
		investments.add(new Pair<byte[], Integer>(sig, koins));
	}
	
	public void removeInvest(byte[] sig, int koins) {
		int index = iterateList(sig);
		int k = investments.get(index).getR();
		if (index > -1) {
			if (k == koins) {				 //removing all koins
				investments.remove(index);
			} else if (k < koins) { 		//removing only some investments
				investments.get(index).setR(k - koins);
			} else { 						//command asks to remove more tha invested, fail
				System.out.println("User has invested only " + k + " koins in this block");
			}
		} else {
			System.out.println("User has not invest any koins in this block, unable to remove");
		}
		
	}
	
	//combine another duplicate of same entry. arg is mainblockentry object or pair
	public void combineDup(Pair<byte[], Integer> pair) {
		totalKoins += pair.getR();
		investments.add(pair);
	}
	
	public void combineDup(MainBlockEntry e) {
		investments.addAll(e.getInvestments());
		totalKoins += e.getTotalKoins();
		
	}
	
	public int getTotalKoins(){
		return totalKoins;
	}
	
	public Collection<? extends Pair<byte[], Integer>> getInvestments() {
		return investments;
	}

	public Data getData() {
		return data;
	}
	
	//iterate through investments and search for match in signature ONLY, return index if found, -1 otherwise
	private int iterateList(byte[] needle) {
		int count = 0;
		for (Pair<byte[], Integer> p : investments) {
			if(needle.equals(p.getL())) {
				return count;
			}
			count++;
		}
		return -1;
	}

	
}
