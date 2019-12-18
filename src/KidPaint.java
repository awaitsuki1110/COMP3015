import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class KidPaint {
	DatagramSocket socket;
	public KidPaint() {
		discovery();
new Thread(() ->{
			
		}).start();
	
	}
		

	void discovery() {
		//For UDP connection
		try {
			DatagramSocket socket = new DatagramSocket(7373);
			byte[] msg = "Hello World".getBytes();
			InetAddress dest = InetAddress.getByName("255.255.255.255");
			int port = 12345;
			DatagramPacket packet = new DatagramPacket(msg, msg.length, dest, port);
			socket.send(packet);
		//reply from server	
			socket.receive(packet);
			byte[] data = packet.getData();
			String str = new String(data, 0, packet.getLength());
			int size = packet.getLength();
			String srcAddr = packet.getAddress().toString();
			int srcPort = packet.getPort();
			System.out.println("Received data:\t" + str);
			System.out.println("data size:\t" + size);
			System.out.println("sent by:\t" + srcAddr);
			System.out.println("via port:\t" + srcPort);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	void draw() {
		
	}
	public static void main(String[] args) {
//		UI ui = UI.getInstance();			// get the instance of UI
//		ui.setData(new int[50][50], 20);	// set the data array and block size. comment this statement to use the default data array and block size.
//		ui.setVisible(true);				// set the ui
		InputNameWindow ui = new InputNameWindow();
		new KidPaint();

	}
}
