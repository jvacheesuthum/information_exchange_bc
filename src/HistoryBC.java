import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
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
		//get all update files in the directory
		File dir = new File(".");
		File [] files = dir.listFiles(new FilenameFilter() {
		    @Override
		    public boolean accept(File dir, String name) {
		        return name.endsWith("updates.txt");
		    }
		});
		
		//de-serialize and add append each one
		FileInputStream fi = null;
		ObjectInputStream oi = null;
		
		for (File f : files) {
			try {
				fi = new FileInputStream(f);
				oi = new ObjectInputStream(fi);
	
				List<HistoryEntry> transactions = (List<HistoryEntry>) oi.readObject();
				entries.addAll(transactions);
				
				//delete file when done updating
				f.delete();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					fi.close();
					oi.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
		}
		
		
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

	public int size() {
		return	entries.size();	
	}
}
