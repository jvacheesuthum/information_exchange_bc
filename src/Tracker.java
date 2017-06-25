/*import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Tracker {

	public static void main(String[] args) throws Exception {
		
		ServerSocket serverSocket = null;
		Socket socket = null;
		OutputStream out = null;
		InputStream in = null;
		BufferedWriter writer = null;
		
	        serverSocket = new ServerSocket(4444);
	        
	        while(true) {
	        	try {
	        
			        socket = serverSocket.accept();
			        String IP = socket.getInetAddress().toString() + ":" + socket.getPort();
			        File file = new File("nodeaddresses.txt");
			      
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
			        
			        //record this client IP to the file
		           	FileWriter fstream = new FileWriter("nodeaddresses.txt", true); //true = appends
		            writer = new BufferedWriter(fstream);
		            writer.write(IP);
		            writer.newLine();
		           	
		        } catch (Exception e) {
		        	e.printStackTrace();
		        } finally {
		        	if (writer != null) writer.close();
			        if (out != null) out.close();
			        if (in != null) in.close();
			        if (socket != null) socket.close();
			        if (serverSocket != null) serverSocket.close();
				}
		
	        }
	   
	}

}*/
