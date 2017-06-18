import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class TrackerServer {

	public static void main(String[] args) throws IOException {
		
		if (args.length != 1) {
            System.err.println("Usage: java TrackerServer <port number>");
            System.exit(1);
        }
 
		FileOutputStream out = new FileOutputStream("nodeaddresses.txt");
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out)); 
		
		int portNumber = Integer.parseInt(args[0]);
		ServerSocket serverSocket = new ServerSocket(portNumber);
		Socket clientSocket = serverSocket.accept();
        
        try {
        	
	        while (true) {
	          //get the IP and port of connected device and write to file
	           	String IP = clientSocket.getInetAddress().toString() + clientSocket.getPort(); 
	           	writer.write(IP);
	           	writer.newLine();
	           	
	            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	            String request = in.readLine();
	           	if (request.contains("request")) {
	           		//send locator file if the client requests
		           	 File toSend = new File("nodeadresses.txt");
		           	 
		             if (!toSend.exists()) {
		                 System.out.println("File does not exist");
		                 return;
		             }
		             
		             FileInputStream is = new FileInputStream(toSend);
		             OutputStream o = clientSocket.getOutputStream();
		             int bytesRead;
		             byte[] buffer = new byte[4096];
		             while ((bytesRead = is.read(buffer)) != -1) {
		                 o.write(buffer, 0, bytesRead);
		             }
		             o.flush();
		             is.close();
	           	} else if (request.contains("disconnect")) {
	           		//client send disconnect signal
	           		in.close();
	           		break;
	           	}
	        }
	        
        } finally {
        	if (writer != null) writer.close();
        	if (out != null) out.close();
        	serverSocket.close();
        	clientSocket.close();
        }
        
	}

}
