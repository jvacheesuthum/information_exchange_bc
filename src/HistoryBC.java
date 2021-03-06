import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class HistoryBC implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private List<HistoryEntry> entries;
	private static int count = 0;
	//use for recording when the node goes offline -> Date.getTime()
	//when it comes online again it will reuest any updates happended form this time until the present
	private Date session_ended; 
	
	private HistoryBC() { 
		entries = new ArrayList<HistoryEntry>();
		session_ended = new Date();
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
		
		Arrays.sort(files);
		
		//de-serialize and add append each one
		FileInputStream fi = null;
		ObjectInputStream oi = null;
		
		for (File f : files) {
			System.out.println("updating with " + f.getName());
			try {
				fi = new FileInputStream(f);
				oi = new ObjectInputStream(fi);
	
				@SuppressWarnings("unchecked")
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
	
	public void endSession(){
		session_ended = new Date();
	}

	public Date getLastSession() {
		return session_ended;
	}
	
	
}
