import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HistoryBC implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private List<HistoryEntry> entries;
	private static int count = 0;
	
	private HistoryBC() { 
		entries = new ArrayList<HistoryEntry>();
		//do we need a genesis block here?
	}
	
	public static HistoryBC getInstance() { //use to limit only 1 instance of the history blockchain
		if (count == 0){
			count++;
			return new HistoryBC();
		}
		return null;
	}
	
	public void add(HistoryEntry e) {
		entries.add(e);
	}
	
	public void fecthUpdate() {
		
	}
	
	public void push() {
		
	}
	
	public List<HistoryEntry> getList() {
		return entries;
	}
	
	@Override
	public String toString(){
		String s = "";
		for (HistoryEntry h : entries) {
			s += h.toString();
		}
		return s;
	}
}
