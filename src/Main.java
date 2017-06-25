import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Scanner;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

public class Main {

	/**
	 * for simulation purposes user can give up to 4 args when starting the program in command line
	 * @param args  0 : computer ID = id of simulation (int)
	 * 				1 : locator file = gives the location of all the nodes (if any) or 1 location of the main server
	 * 				2 : IP (optional) = fake ip address of this simulation		
	 * 				3 : Port no. (optional)
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

			//private public key pair generation ----------------------------------------------------------

			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

			keyGen.initialize(512, random);

			KeyPair pair = keyGen.generateKeyPair();
			PrivateKey priv = pair.getPrivate();
			PublicKey pub = pair.getPublic();


			//instances -----------------------------------------------------------------------------------
			
			HistoryBC blockchain;
			BCCompiler com = new BCCompiler();
			Broadcaster broadcast = new Broadcaster();
			Scanner scanner = new Scanner(System.in);
			int update_index = 0; //index of latest block loaded, so anything after this index must be broadcasted to the network

			int currentKoins = 0;
			boolean compiled = false;
			boolean mustCompile = false; //if there is an addition, must compile first before fetching updates
			
			
			File hist = new File("history.txt");
			if (hist.exists()) {
					FileInputStream fi = new FileInputStream(hist);
					ObjectInputStream oi = new ObjectInputStream(fi);
	
					blockchain = (HistoryBC) oi.readObject();
					update_index = blockchain.size();
					oi.close();
			} else {
				System.out.println("no local blockchain found");
				blockchain = HistoryBC.getInstance();
			}
			
			//run server and update current state of the blockchain ------------------------------------
			NodeServer serv = new NodeServer(broadcast);
			serv.execute();

			try{
			//broadcast.request(blockchain.getLastSession().getTime());  //get updates this user hasn't received while offline
			//--------------------------------------------------------------------------------------------
			do {
				if(!mustCompile) {
					blockchain.fecthUpdate();
					update_index = blockchain.size();
				} else {
					System.out.println("you must compile first to fetch any updates available");
				}

				System.out.println('\n' + "Enter command in the following format: '[ADD/SIGN/REMV] [String[] or String] [int index] [int koins]' ");
				System.out.println("OR 'compile' to compile AND 'graph' after compiling to build a graph");
				System.out.println("OR type 'RDF' follow by a spcae and filename to upload an ontology ");
				System.out.println("OR 'mine' to mine koins" +'\n');

				String s = scanner.nextLine();
				
				switch(s) {
				case "end" : //-----end the session and go offline
					serv.end();
					blockchain.endSession(); //records the time for ending in the serialised file as well!!!!
					
					//save history blockchain into disk using serializable
					FileOutputStream fi = new FileOutputStream(new File("history.txt"));
					ObjectOutputStream oi = new ObjectOutputStream(fi);
					oi.writeObject(blockchain);
					oi.close();
					fi.close();
					
					return;
				case "graph": //---------------------------------------------------------------------------
					if (!compiled) {
						System.out.println("please compile first");
						continue;
					}

					// put the compiled MainBlocks into neo4j nodes
					com.genGraph();
					System.out.println("successfully generated graph for neo4j");
					scanner.close();
					serv.end();
					return;
					
				case "compile": //---------------------------------------------------------------------------
					compiled = true;
					mustCompile = false;
					com.compile(blockchain);
					//com.showState();
					
					//broadcast change to peers--------------------------------------------------------
					int additions = blockchain.size() - update_index;
					if (additions > 0) {
						//sublist containing new entries
						ArrayList<HistoryEntry> updates = new ArrayList<HistoryEntry>(blockchain.getList().subList(update_index, blockchain.size()));
						
						//serialize into file to be sent
						if (updates.size() > 0) {
							String filename = "new_transactions.txt";
							FileOutputStream f = new FileOutputStream(new File(filename));
							ObjectOutputStream o = new ObjectOutputStream(f);
							o.writeObject(updates);
							o.close();
							f.close();
							
							//send with broadcaster
							broadcast.send(filename);
						}
					}
					break;
					
				
				default:   //-------------------------------------------------------------------------------
					if (s.startsWith("mine")) {
						mustCompile = true;
						currentKoins = HashCash.mineKoins("teststring", currentKoins, blockchain, pub, priv);
						

					} else if (s.startsWith("RDF")) { //-----------------------------------------------------------------------
						// read an ontology in RDFXML format and invests 1 in each
						mustCompile = true;
						String filename = s.replaceFirst("RDF", "").trim();
						FileInputStream input = new FileInputStream("example_ontologies/" + filename);

						Model model = Rio.parse(input, "", RDFFormat.RDFXML);
						for (Statement v : model) {
							// adding all subject - predicate - object into the blockchain
							 blockchain.add(CommandParser.parse("ADD " + getResourceName(v.getSubject()) + " 1", priv, pub));
							 blockchain.add(CommandParser.parse("ADD " + getResourceName(v.getPredicate()) + " 1", priv,pub));
							 blockchain.add(CommandParser.parse("ADD " + getResourceName(v.getObject()) + " 1", priv, pub));

							 //add relationships
							 blockchain.add(new HistoryEntry(getResourceName(v.getSubject()), getResourceName(v.getPredicate())));
							 blockchain.add(new HistoryEntry(getResourceName(v.getPredicate()), getResourceName(v.getObject())));

						}
						input.close();
					} else {   // single regular command case -------------------------------------
						try {
							blockchain.add(CommandParser.parse(s, priv, pub));
							mustCompile = true;
						} catch (Exception e) {
							System.out.println("invalid command");
						}

					}
				}
				

				//save history blockchain into disk using serializable
				FileOutputStream f = new FileOutputStream(new File("history.txt"));
				ObjectOutputStream o = new ObjectOutputStream(f);
				o.writeObject(blockchain);
				o.close();
				f.close();

			} while(true);
		
} catch (Exception e) {
	e.printStackTrace();
} finally {
	System.out.println("closing all ports..");
	serv.end();
	System.out.println("Done");
}
	}
	
	
	

	// extract resource name form URI after #
	public static String getResourceName(Object o) {
		String s = o.toString();
		return s.substring(s.lastIndexOf('#') + 1);
	}

	// method to verify signature
	/*
	 * params:  signature - signature to be verified
	 * 			data - details of transaction which produced this signature 
	 * 			pubKey - public key of the user who produced this signature
	 */
	public boolean verifySig(byte[] signature, byte[] data, PublicKey pubKey) {
		try {
			Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
			sig.initVerify(pubKey);
			sig.update(data);
			return sig.verify(signature);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
