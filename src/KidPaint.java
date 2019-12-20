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
	
	DatagramSocket socket;
	Random viaPort=new Random();
	public KidPaint() throws IOException  {
		instance = this;
		// UDP connect
		try {
			DatagramSocket socket = new DatagramSocket(viaPort.nextInt(8998) + 1001);
			byte[] msg = "Hello World".getBytes();
			InetAddress dest = InetAddress.getByName("255.255.255.255");
			int port = 12345;
			DatagramPacket packet = new DatagramPacket(msg, msg.length, dest, port);
			socket.send(packet);
			// reply from server
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
		// TCP Connect
		Socket cSocket = new Socket("127.0.0.1", 22556);
		DataInputStream in = new DataInputStream(cSocket.getInputStream());
		DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
		new Thread(() -> {
			byte[] buffer = new byte[1024];
			try {
				while (true) {
					int len = in.readInt();
					in.read(buffer, 0, len);
					String content = new String(buffer,0,len);
					//explain content
					
					System.out.println(content);
				}
			} catch (Exception e2) {
				System.out.println("Connection Dropped");
				System.exit(-1);
			}
		}).start();
		while (true) {
			
			String userAndInput = "ASVD";
			out.writeInt(userAndInput.length());
			out.write(userAndInput.getBytes(), 0, userAndInput.length());
		}
	
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
