import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

public class BCCompiler {

	private List<MainBlockEntry> entries;
	private Date last_update; // last compilation

	public BCCompiler() {
		// TODO this assumes we only compile once -- not sure????
		entries = new ArrayList<MainBlockEntry>();
	}

	// takes a history blockchain and compile into main blockchain
	public void compile(HistoryBC history) throws Exception {
		MainBlockEntry.resetCounter();
		List<HistoryEntry> h = history.getList();
		for (HistoryEntry e : h) {
			switch (e.getCommand()) {
			case REMV:
				compileSignRemv(e);
				break;
			case SIGN:
				compileSignRemv(e);
				break;
			case ADD:
				compileAdd(e);
				break;
			case MINED:
				addMine(e);
				break;
			case LINK:
				addLink(e);
				break;
			default:
				System.out.println("at compile() getcommand cannot retrieve command");
			}
		}
		last_update = new Date();
		
		FileOutputStream f = new FileOutputStream(new File("mainblocks.txt"));
		ObjectOutputStream o = new ObjectOutputStream(f);
		o.writeObject(entries);
		o.close();
		f.close();
	}

	//add relationship-block into the main block to generate relationship when genGraph()
	private void addLink(HistoryEntry e) {
		entries.add(new MainBlockEntry(e.getData(), e.getSecondData()));
	}
	
	private void addMine(HistoryEntry e){
		entries.add(new MainBlockEntry(e.getPubkey(), e.getData()));
	}

	private void compileAdd(HistoryEntry e) {
		// check for duplicates first
		int s = search(e.getData());
		if (s == -1) {
			// no duplicate, continue adding
			entries.add(new MainBlockEntry(e.getData(), 0));
			entries.get(entries.size() - 1).invest(e.getPub(), e.getSig(), e.getKoins());
		} else {
			// duplicate exists, SIGN instead
			entries.get(s).invest(e.getPub(), e.getSig(), e.getKoins());
		}
	}

	private void compileSignRemv(HistoryEntry e) {
		int index = e.getRef();
		if (index > -1) {
			try {
				if (e.getCommand() == Command.SIGN) {
					entries.get(index).invest(e.getPub(), e.getSig(), e.getKoins());
				} else { //REMV
					entries.get(index).removeInvest(e.getPub(), e.getKoins());
				}
			} catch (IndexOutOfBoundsException ex) {
				System.out.println("this SIGN/REMV entry does not match any ref provided (out of bounds): " + e.toString());
			}
		} else {
			System.out.print("at compileSignRemv the historyentry has ref index -1");
		}
	}

	/*private void compileRemv(HistoryEntry e) {
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
	}*/
	

	// search for duplicate entries, return the index if found, and -1 otherwise
	private int search(Data d) {
		int count = 0;
		for (MainBlockEntry e : entries) {
			if (e.getData().toString().equals(d.toString()))
				return count;
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

	public void genGraph() {
		File filename = new File("Graph "+ last_update + " " + last_update.getTime());
		GraphDatabaseFactory dbFactory = new GraphDatabaseFactory();
		GraphDatabaseService db = dbFactory.newEmbeddedDatabase(filename);
		try (Transaction tx = db.beginTx()) {

			for (MainBlockEntry entry : entries) {
				if (entry.getSecondData() != null) {
					
					//relationship entries case
					
					int first = search(entry.getData());
					int second = search(entry.getSecondData());
					
					if (first < 0 || second < 0 || first == second) {
						System.out.println("invalid relationships at gengraph(), should never happen");
						continue;
					}
					
					Node it = db.findNode(Label.label("ALL"), "name", entries.get(first).getData().toString());
					Node it2 = db.findNode(Label.label("ALL"), "name", entries.get(second).getData().toString());
					it.createRelationshipTo(it2, RelationshipType.withName("-"));
					
					
					
				} else {
				//create new node and set no. of koins as property for each entries
				Node node = db.createNode(Label.label("ALL"));
				node.setProperty("index", entry.getIndex());
				node.setProperty("name", entry.getData().toString());
				
				
					if (entry.getMiner() == null) {
						//not a PoW node, carry on.......
						node.setProperty("total_Koins_invested", entry.getTotalKoins());
						
						//turn investments into array of string which is an allowed type of property
						List<Inv> invs = (List<Inv>) entry.getInvestments();
						String[] investors = new String[invs.size()];
						int count = 0;
						for (Inv i : invs) {
							investors[count] = i.getPub() + " with " + i.getKoins() + " Koins";
							count++;
						}
						node.setProperty("investors", investors);
					} else {
						//PoW node-------------------------------------------
						node.setProperty("PublicKey", entry.getMiner());
						node.setProperty("STAMP", entry.getData().toString());
					}

				
				
				}
			}
			
			tx.success();

		}

	}
}
