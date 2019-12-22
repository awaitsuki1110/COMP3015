import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Random;

public class KidPaint {
	public static KidPaint instance;
	
	
	public KidPaint() throws IOException  {
		instance = this;
		
	
	}
	
	public void send(String msg) {
		
			}

	

	public static void main(String[] args) throws IOException {
//		UI ui = UI.getInstance();			// get the instance of UI
//		ui.setData(new int[50][50], 20);	// set the data array and block size. comment this statement to use the default data array and block size.
//		ui.setVisible(true);				// set the ui
		InputNameWindow ui = new InputNameWindow();
		new KidPaint();

	}
	}
