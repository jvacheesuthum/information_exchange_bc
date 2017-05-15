import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BCCompiler {
	
	private List<MainBlockEntry> entries;
	private Date last_update; //last compilation
	
	public BCCompiler() {
		entries = new ArrayList<MainBlockEntry>();
	}
	
	//takes a history blockchain and compile into main blockchain
	public void compile(HistoryBC history) {
		List<HistoryEntry> h = history.getList();
		for (HistoryEntry e : h) {
			switch(e.getCommand()) {
			case REMV : compileRemv(e);
						break;
			case SIGN : compileSign(e);
						break;
			default : compileAdd(e);
			}
		}
		last_update = new Date();
	}
	
	private void compileAdd(HistoryEntry e) {
		//check for duplicates first
		int s = search(e.getData());
		if (s == -1) {
			//no duplicate, continue adding
			entries.add(new MainBlockEntry(e.getData(),e.getKoins()));
		} else {
			//duplicate exists, combine instead
			//entries.get(s).combineDup(); //TODO signature in history.. IMPLEMENT
		}
	}

	private void compileSign(HistoryEntry e) {
		int index = e.getRef();
		if (index > -1) {
			entries.get(index).invest(e.getSig(), e.getKoins());
		} else {
			System.out.print("at compileSign the historyentry has ref index -1 -- this should never happen");
		}
	}

	private void compileRemv(HistoryEntry e) {
		int index = e.getRef();
		if (index > -1) {
			entries.get(index).removeInvest(e.getSig(), e.getKoins());
		} else {
			System.out.print("at compileSign the historyentry has ref index -1 -- this should never happen");
		}
	}

	//search for duplicate entries, return the index if found, and -1 otherwise
	private int search(Data d){
		return -1;
	}
}
