import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

public class Broadcaster {
	
	List<String> peers;   //list of peers address
	
	public Broadcaster() {
		
	}

	//send updates.txt in directory to others
	public void send(String filename) {
		for (String p : peers) {
			//[0] = IP and [1] = port number
			String[] address = p.split(":"); 
			
			Socket socket = null;
            String host = address[0];
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
}
