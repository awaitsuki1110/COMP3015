import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class kidServer {
	ServerSocket srvSocket;
	ArrayList<Socket> list = new ArrayList<Socket>();
	DatagramSocket socket;

	public kidServer(int udpport, int tcpport) throws IOException {

		// UDP connection
		System.out.printf("Listening UDP at port %d...\n", udpport);
		socket = new DatagramSocket(udpport);
		new Thread(() -> {
			while (true) {
				try {
					udpServer();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}).start();

		// TCP connection
		srvSocket = new ServerSocket(tcpport);

		while (true) {
			System.out.printf("Listening at port %d...\n", tcpport);
			Socket cSocket = srvSocket.accept();

			synchronized (list) {
				list.add(cSocket);
				System.out.printf("Total %d clients are connected.\n", list.size());
			}

			Thread t = new Thread(() -> {
				try {
					serve(cSocket);
				} catch (IOException e) {
					System.err.println("connection dropped.");
				}
				synchronized (list) {
					list.remove(cSocket);
				}
			});
			t.start();
		}
	}

	public void udpServer() throws IOException {

		DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);

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

		String replyMsg = "This is the reply message";
		byte[] buffer = replyMsg.getBytes();
		DatagramPacket packet2 = new DatagramPacket(buffer, buffer.length, packet.getAddress(), srcPort);
		socket.send(packet);

	}

	private void serve(Socket clientSocket) throws IOException {
		byte[] buffer = new byte[10024];
		System.out.printf("Established a connection to host %s:%d\n\n", clientSocket.getInetAddress(),
				clientSocket.getPort());

		DataInputStream in = new DataInputStream(clientSocket.getInputStream());
		while (true) {
			int len = in.readInt();
			in.read(buffer, 0, len);
			String getContent = new String(buffer, 0, len);
			System.out.println("S>>:" + new String(buffer, 0, len));

			//forward(buffer, len, clientSocket.getInetAddress());
			if (getContent.toCharArray()[0] == 'a') {
				forward(buffer, len, clientSocket.getInetAddress());
			}
			if (getContent.toCharArray()[0] == 'b') {
				forward(buffer, len, clientSocket.getInetAddress());
			}
			if (getContent.toCharArray()[0] == 'c') {
				forward(buffer, len, clientSocket.getInetAddress());
			}

		}
	}

	private void forward(byte[] data, int len, InetAddress inetClientAddress) {
		synchronized (list) {
			System.out.println(inetClientAddress);

			for (int i = 0; i < list.size(); i++) {

				try {
					Socket socket = list.get(i);
					
					DataOutputStream out = new DataOutputStream(socket.getOutputStream());
					out.writeInt(len);
					out.write(data, 0, len);
					
				} catch (IOException e) {
					// the connection is dropped but the socket is not yet removed.
				}

			}

		}

	}

	public static void main(String[] args) throws IOException {
		new kidServer(12345, 22556);
	}

}
