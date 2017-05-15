import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MainBlockEntry {
	private Data data;
	//this is signature-koins pair, change the type later java.security.signature?????
	private List<Pair<String, Integer>> investments;
	private int totalKoins;
	private int index; // would it be easier if it's a field like this of just use indexOf()
	private static int counter = 0; //use to keep up with index, maybe unnecessary, check above
	
	public MainBlockEntry(Data data, int totalKoins) {
		this.data= data;
		this.investments = new ArrayList<Pair<String, Integer>>();
		this.totalKoins = totalKoins;
		this.index = counter;
		counter++;
	}
	
	public void invest(String sig, int koins) {
		totalKoins += koins;
		investments.add(new Pair<String, Integer>(sig, koins));
	}
	
	public void removeInvest(String sig, int koins) {
		boolean b = investments.remove(new Pair<String, Integer>(sig, koins));
		if (!b) {
			System.out.println("User has not invest any koins in this block, unable to remove");
		} else {
			totalKoins -= koins;
		}
	}
	
	//combine another duplicate of same entry can take either other mainblockentry object or just no of koins
	public void combineDup(Pair<String, Integer> pair,int koins) {
		totalKoins += koins;
		investments.add(pair);
	}
	
	public void combineDup(MainBlockEntry e) {
		investments.addAll(e.getInvestments());
		totalKoins += e.getTotalKoins();
		
	}
	
	public int getTotalKoins(){
		return totalKoins;
	}
	
	public Collection<? extends Pair<String, Integer>> getInvestments() {
		return investments;
	}

	public Data getData() {
		return data;
	}
}
