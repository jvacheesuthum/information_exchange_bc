import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Broadcaster {
	
	List<String> peers;   //list of peers address
	
	public Broadcaster() {
		//get peer list from nodeaddresses.txt
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("nodeaddresses.txt"));
			this.peers = new ArrayList<String>();
	
			String line;
			
			while((line = reader.readLine()) != null) {
			    peers.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	public void updatePeers() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("nodeaddresses.txt"));
			this.peers = new ArrayList<String>();
	
			String line;
			
			while((line = reader.readLine()) != null) {
			    peers.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	//send updates.txt in directory to others
	public void send(String filename) {
		for (String p : peers) {
			//[0] = IP and [1] = port number
			String[] address = p.split(":"); 
			
			Socket socket = null;
            String host = address[0];//TODO maybe hardcode this port for requesting update only?
            int port = Integer.parseInt(address[1]);
            InputStream in = null;
            OutputStream out = null;
            
            try {
                socket = new Socket(host, port);

                File file = new File(filename);
			      
		        // Get the size of the file
		        long length = file.length();
		        
		        if (length > Integer.MAX_VALUE) {
		            System.out.println("File is too large.");
		        }
		        
		        byte[] bytes = new byte[(int) length];
		        
		        in = new FileInputStream(file);
		        out = socket.getOutputStream();
		
		        int count;
		        
		        while ((count = in.read(bytes)) > 0) {
		            out.write(bytes, 0, count);
		        }

                
            } catch (Exception e) {
            	e.printStackTrace();
            } finally {
            	try {
			        if (out != null) out.close();
			        if (in != null) in.close();
			        if (socket != null) socket.close();
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            }
		}
	}
	
	//request updates from peer when user comes online again
	public void request(long session_ended){
		for (String p : peers) {
			String[] address = p.split(":"); 
			
			Socket socket = null;
            String host = address[0];
            int port = Integer.parseInt(address[1]); //TODO hardcode port?
            InputStream in = null;
            OutputStream out = null;
            
            try {
            	socket = new Socket(host, port);
            	
            	//Send the time of last session to other nodes
                OutputStream os = socket.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
          
                String sendMessage = session_ended + "\n";
                bw.write(sendMessage);
                bw.flush();
                
                //receive files
            	in = socket.getInputStream();
                out = new FileOutputStream("update?.txt"); //TODO need some way to sort in chrono order
                byte[] bytes = new byte[16*1024];

                int count;
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
            } catch (Exception e) {
            	e.printStackTrace();
            } finally {
            	try {
			        if (out != null) out.close();
			        if (in != null) in.close();
			        if (socket != null) socket.close();
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            }
		}
	}

}
