import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class InitRequest {

	public static void main(String[] args) {
		//send request for nodeaddr.txt and history.txt
		//read from request.txt
		
	
		
				
		Socket socket = null;
		String host = args[0];
	    int port = 8846;
	    InputStream in = null;
	    OutputStream out = null;
        
        try {
            socket = new Socket(host, port);

            in = socket.getInputStream();
            out = new FileOutputStream("../../history.txt");
            byte[] bytes = new byte[16*1024];

            int count;
            while ((count = in.read(bytes)) > 0) {
                out.write(bytes, 0, count);
            }

            out.close();
            in.close();
            socket.close();
            
            //for now connect again for new file, fix later for efficiency
            socket = new Socket(host, port);

            in = socket.getInputStream();
            out = new FileOutputStream("../../nodeaddr.txt");
            bytes = new byte[16*1024];

            int count2;
            while ((count2 = in.read(bytes)) > 0) {
                out.write(bytes, 0, count2);
            }

            out.close();
            in.close();
            socket.close();
            System.out.println("Received nodeaddr.txt and history.txt from " +host);
            
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
