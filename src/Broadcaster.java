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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Broadcaster {
	
	List<String> peers;   //list of peers address
	
	public Broadcaster() {
		//get peer list from nodeaddresses.txt
		BufferedReader reader = null;
		try {
			File f = new File("nodeaddr.txt");
			reader = new BufferedReader(new FileReader(f));
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
	
	private void updatePeers() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("nodeaddr.txt"));
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
		updatePeers();
		for (String p : peers) {
			
			if(isMyIP(p)) continue;
			Socket socket = null;
            int port = 8845;
            InputStream in = null;
            OutputStream out = null;
            
            try {
                socket = new Socket(p, port);

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
		updatePeers();
		for (String p : peers) {
			
			Socket socket = null;
            int port = 11111; //TODO hardcode port?
            InputStream in = null;
            OutputStream out = null;
            
            try {
            	socket = new Socket(p, port);
            	
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
	
	//advertise new peer to the known peer
	public void advNewPeer(String ip) {
		updatePeers();
		for (String p : peers) {
			
			if (p.trim() == ip.trim() || isMyIP(p)) continue;
			Socket socket = null;
            int port = 8847;
            OutputStream out = null;
            
            try {
                socket = new Socket(p, port);
		        out = socket.getOutputStream();
		        out.write(ip.getBytes());
                
            } catch (Exception e) {
            	e.printStackTrace();
            } finally {
            	try {
			        if (out != null) out.close();
			        if (socket != null) socket.close();
            	} catch (Exception e) {
            		e.printStackTrace();
            	}
            }
		}
	}
	
	public static boolean isMyIP(String ip) {
		System.out.println(ip);
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(ip);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    // Check if the address is a valid special local or loop back
	    if (addr.isAnyLocalAddress() || addr.isLoopbackAddress())
	        return true;

	    // Check if the address is defined on any interface
	    try {
	        return NetworkInterface.getByInetAddress(addr) != null;
	    } catch (SocketException e) {
	        return false;
	    }
	}

}
