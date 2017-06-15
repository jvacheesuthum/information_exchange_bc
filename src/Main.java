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
			do {
				System.out.println("Enter command in the following format: '[ADD/SIGN/REMV] [String[] or String] [int index] [int koins]' ");
				System.out.println("OR 'compile' to compile AND 'graph' after compiling to build a graph");
				System.out.println("OR type 'RDF' follow by a spcae and filename to upload an ontology ");
				System.out.println("OR 'mine' to mine koins");

				String s = scanner.nextLine();
				
				switch(s) {
				
				case "graph": //---------------------------------------------------------------------------
					if (!compiled) {
						System.out.println("please compile first");
						continue;
					}

					// put the compiled MainBlocks into neo4j nodes
					com.genGraph();
					System.out.println("successfully generated graph for neo4j");
					return;
					
				case "compile": //---------------------------------------------------------------------------
					compiled = true;
					com.compile(blockchain);
					com.showState();
					break;
					
				default:   //-------------------------------------------------------------------------------
					if (s.startsWith("mine")) {
						
						currentKoins = HashCash.mineKoins("teststring", currentKoins, blockchain, pub, priv);
						

					} else if (s.startsWith("RDF")) { //-----------------------------------------------------------------------
						// read an ontology in RDFXML format and invests 1 in each

						String filename = s.replaceFirst("RDF", "").trim();
						FileInputStream input = new FileInputStream(filename);

						Model model = Rio.parse(input, "", RDFFormat.RDFXML);
						for (Statement v : model) {
							//TODO need to change these to URIs?
							// adding all subject - predicate - object into the blockchain
							 blockchain.add(CommandParser.parse("ADD " + getResourceName(v.getSubject()) + " 1", priv, pub));
							 blockchain.add(CommandParser.parse("ADD " + getResourceName(v.getPredicate()) + " 1", priv,pub));
							 blockchain.add(CommandParser.parse("ADD " + getResourceName(v.getObject()) + " 1", priv, pub));

							 //add relationships
							 blockchain.add(new HistoryEntry(getResourceName(v.getSubject()), getResourceName(v.getPredicate())));
							 blockchain.add(new HistoryEntry(getResourceName(v.getPredicate()), getResourceName(v.getObject())));

						}

					} else {   // single regular command case -------------------------------------
							blockchain.add(CommandParser.parse(s, priv, pub));

					}
				}
				

				//save history blockchain into disk using serializable
				FileOutputStream f = new FileOutputStream(new File("history.txt"));
				ObjectOutputStream o = new ObjectOutputStream(f);
				o.writeObject(blockchain);
				o.close();
				f.close();

			} while(true);
		
	}

	// extract resource name form URI after #
	public static String getResourceName(Object o) {
		String s = o.toString();
		return s.substring(s.lastIndexOf('#') + 1);
	}

	// method to verify signature
	/*
	 * params:  signature - signature to be verified
	 * 			data - details of transaction which produced this signature (the signed data)
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
