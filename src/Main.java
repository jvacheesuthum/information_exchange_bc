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
import java.util.Base64;
import java.util.Scanner;

import org.eclipse.rdf4j.model.Model;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;

public class Main {

	public static void main(String[] args) throws Exception {
		

		try {

			//priv pub keygen ---------------------------------------------------------------------------------

			// we're generating new KeyPair for every user just for testing, in
			// reality users will
			// have to login with their pub-priv key to keep the record of their
			// koins, etc
			// we now have infinite number of koins at the moment, there's no
			// limit
			// since the PoW will regain the same no of koin spent anyway, in
			// reality -> another mining function
			// write details in report

			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA", "SUN");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");

			keyGen.initialize(512, random);

			KeyPair pair = keyGen.generateKeyPair();
			PrivateKey priv = pair.getPrivate();
			PublicKey pub = pair.getPublic();


			//instances -----------------------------------------------------------------------------------
			
			HistoryBC blockchain;
			BCCompiler com = new BCCompiler();

			int currentKoins = 0;
			boolean compiled = false;
			
			Scanner scanner = new Scanner(System.in);
			System.out.println("Do you want to load the previous history blockchain? type y if yes");
			String readBC = scanner.nextLine();
			
			
			if (readBC.equals("y")) {
					FileInputStream fi = new FileInputStream(new File("history.txt"));
					ObjectInputStream oi = new ObjectInputStream(fi);
	
					blockchain = (HistoryBC) oi.readObject();
			} else {
				blockchain = HistoryBC.getInstance();
			}
			

			//--------------------------------------------------------------------------------------------
			while (true) {
				System.out.println(
						"Enter command in the following format: '[ADD/SIGN/REMV] [String[] or String] [int index] [int koins]' ");
				System.out.println("OR 'compile' to compile AND 'graph' after compiling to build a graph");
				System.out.println("OR type 'RDF' follow by a spcae and filename to upload an ontology ");
				System.out.println("OR \"mine [int]\" to mine koins");

				String s = scanner.nextLine();
				if (s.equals("graph")) {
					
					if (!compiled) {
						System.out.println("please compile first");
						continue;
					}

					// put the compiled MainBlocks into neo4j nodes
					com.genGraph();
					System.out.println("successfully generated graph for neo4j");
					break;
					
				} else if (s.equals("compile")) {
					
					// TODO check for any updates/conflicts in the server first,
					// merge changes/ or even just add it on it shouldnt matter
					compiled = true;
					com.compile(blockchain);
					com.showState();
					// TODO push the change to server and GraphDB
					
				} else if (s.startsWith("mine")) {
					
					int toMine = Integer.parseInt(s.replaceFirst("mine", "").trim());
					currentKoins = HashCash.mineKoins("teststring", currentKoins, toMine, blockchain, pub);
					

				} else if (s.startsWith("RDF")) {
					// read an ontology in RDFXML format and invests 1 in each

					String filename = s.replaceFirst("RDF", "").trim();
					FileInputStream input = new FileInputStream(filename);

					Model model = Rio.parse(input, "", RDFFormat.RDFXML);
					for (Statement v : model) {
						//TODO need to change these to URIs?
						// adding all subj - predicate - obj into the blockchain
						 blockchain.add(CommandParser.parse("ADD " + getResourceName(v.getSubject()) + " 1", priv, pub));
						 blockchain.add(CommandParser.parse("ADD " + getResourceName(v.getPredicate()) + " 1", priv,pub));
						 blockchain.add(CommandParser.parse("ADD " + getResourceName(v.getObject()) + " 1", priv, pub));

						 //add relationships
						 blockchain.add(new HistoryEntry(getResourceName(v.getSubject()), getResourceName(v.getPredicate())));
						 blockchain.add(new HistoryEntry(getResourceName(v.getPredicate()), getResourceName(v.getObject())));

					}

				} else {
						// single regular command case -------------------------------------
						blockchain.add(CommandParser.parse(s, priv, pub));

				}

				//save history blockchain into disk using serializable
				FileOutputStream f = new FileOutputStream(new File("history.txt"));
				ObjectOutputStream o = new ObjectOutputStream(f);
				o.writeObject(blockchain);
				o.close();
				f.close();

				System.out.println(blockchain.toString());

			}

			scanner.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	// extract resource name form URI after #
	public static String getResourceName(Object o) {
		String s = o.toString();
		return s.substring(s.lastIndexOf('#') + 1);
	}

	// method to verify signature, move to other class later??
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
