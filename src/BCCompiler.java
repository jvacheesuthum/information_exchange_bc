import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BCCompiler {
	
	private List<MainBlockEntry> entries;
	private Date last_update; //last compilation
	
	public BCCompiler() {
		//TODO this assumes we only compile once -- not sure????
		entries = new ArrayList<MainBlockEntry>();
	}
	
	//takes a history blockchain and compile into main blockchain
	public void compile(HistoryBC history) {
		MainBlockEntry.resetCounter();
		List<HistoryEntry> h = history.getList();
		for (HistoryEntry e : h) {
			switch(e.getCommand()) {
			case REMV : compileRemv(e);
						break;
			case SIGN : compileSign(e);
						break;
			case ADD : compileAdd(e);
						break;
			default: System.out.println("at compile() getcommand cannot retrieve command");
			}
		}
		last_update = new Date();
	}
	
	private void compileAdd(HistoryEntry e) {
		//check for duplicates first
		int s = search(e.getData());
		if (s == -1) {
			//no duplicate, continue adding
			entries.add(new MainBlockEntry(e.getData(), 0));
			entries.get(entries.size() - 1).invest(e.getPub(), e.getSig(), e.getKoins());
		} else {
			//duplicate exists, SIGN instead
			entries.get(s).invest(e.getPub(), e.getSig(), e.getKoins());
		}
	}

	private void compileSign(HistoryEntry e) {
		int index = e.getRef();
		if (index > -1) {
				try {
					entries.get(index).invest(e.getPub(), e.getSig(), e.getKoins());
				} catch (IndexOutOfBoundsException ex) {
					System.out.println("this SIGN entry does not match any ref provided (out of bounds): " + e.toString());
				}
			} else {
				System.out.print("at compileSign the historyentry has ref index -1 -- this should never happen");
			}
		}
	

	private void compileRemv(HistoryEntry e) {
		int index = e.getRef();
		if (index > -1) {
			try {
				entries.get(index).removeInvest(e.getPub(), e.getKoins());
			} catch (IndexOutOfBoundsException ex) {
				System.out.println("this REMV ->>> out of bounds: " + e.toString());
			}
		} else {
			System.out.println("at compileRemv the historyentry has ref index -1");
		}
	}

	//search for duplicate entries, return the index if found, and -1 otherwise
	private int search(Data d){
		int count = 0;
		for (MainBlockEntry e : entries) {
			if(e.getData().toString().equals(d.toString())) return count;
			count++;
		}
		return -1;
	}

	public void showState() {
		System.out.println("last compiled at " + last_update);
		for (MainBlockEntry e : entries) {
			System.out.println(e.toString());
		}
		
	}
}
