import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Container;

import javax.swing.border.LineBorder;

enum PaintMode {
	Pixel, Area
};

public class UI extends JFrame {
	private JTextField msgField;
	private JTextArea chatArea;
	private JPanel pnlColorPicker;
	private JPanel paintPanel;
	private JToggleButton tglPen;
	private JToggleButton tglBucket;
	private JButton tglSave;
	private JButton tglLoad;
	private String msgNeedSend = "";
	private Random viaPort=new Random();
	private static UI instance;
	private int selectedColor = -543230; // golden
	private String username = "";
	int[][] data = new int[50][50]; // pixel color data array
	int blockSize = 16;
	PaintMode paintMode = PaintMode.Pixel;

	// self- contributed***********************************************

	// *****************************************************************

	/**
	 * get the instance of UI. Singleton design pattern.
	 * 
	 * @return
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static UI getInstance() throws UnknownHostException, IOException {
		if (instance == null)
			instance = new UI();

		return instance;
	}

	/**
	 * private constructor. To create an instance of UI, call UI.getInstance()
	 * instead.
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	
	private UI() throws UnknownHostException, IOException {
		setTitle("KidPaint");
		this.username = InputNameWindow.name; // set the input and set the name = username so that this class have local
											// username
		System.out.println(username);

		JPanel basePanel = new JPanel();
		getContentPane().add(basePanel, BorderLayout.CENTER);
		basePanel.setLayout(new BorderLayout(0, 0));
		String srcAddr = "";
		//UDP connect
		
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
			srcAddr = packet.getAddress().toString();
			int srcPort = packet.getPort();
			System.out.println("Received data:\t" + str);
			System.out.println("data size:\t" + size);
			System.out.println("sent by:\t" + srcAddr);
			System.out.println("via port:\t" + srcPort);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//TCP Connect
		Socket cSocket = new Socket(InetAddress.getByName(srcAddr.substring(1)), 22556);
		DataInputStream in = new DataInputStream(cSocket.getInputStream());
		DataOutputStream out = new DataOutputStream(cSocket.getOutputStream());
		new Thread(() -> {
			System.out.println("01");
			byte[] buffer = new byte[10024];
			try {
				while (true) {
					System.out.println("02");
					int len = in.readInt();
					System.out.println("03");
					
					in.read(buffer, 0, len);
					System.out.println("04");
					String content = new String(buffer,0,len);

					System.out.println("05");
					System.out.println(buffer);
					onTextInputted(content);
					//explain content
					
					//
				}
			} catch (Exception e2) {
				System.out.println("Connection Dropped");
				System.exit(-1);
			}
		}).start();
		
		paintPanel = new JPanel() {

			// refresh the paint panel
			@Override
			public void paint(Graphics g) {
				super.paint(g);

				Graphics2D g2 = (Graphics2D) g; // Graphics2D provides the setRenderingHints method

				// enable anti-aliasing
				RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHints(rh);

				// clear the paint panel using black
				g2.setColor(Color.black);
				g2.fillRect(0, 0, this.getWidth(), this.getHeight());

				// draw and fill circles with the specific colors stored in the data array
				for (int x = 0; x < data.length; x++) {
					for (int y = 0; y < data[0].length; y++) {
						g2.setColor(new Color(data[x][y]));
						g2.fillArc(blockSize * x, blockSize * y, blockSize, blockSize, 0, 360);
						g2.setColor(Color.darkGray);
						g2.drawArc(blockSize * x, blockSize * y, blockSize, blockSize, 0, 360);
					}
				}
			}
		};

		paintPanel.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			// handle the mouse-up event of the paint panel
			@Override
			public void mouseReleased(MouseEvent e) {
				if (paintMode == PaintMode.Area && e.getX() >= 0 && e.getY() >= 0) {
					paintArea(e.getX() / blockSize, e.getY() / blockSize);
					String str="";
					for (int i = 0; i < data.length; i++)// for each row
					{
						for (int j = 0; j < data.length; j++)// for each column
						{
							str = str+ data[i][j];
							
							if (j < data.length - 1)
								str = str+",";
							

						}
						
					}
					String toOutput=("b"+str);
					try {
						out.writeInt(toOutput.length());
						out.write(toOutput.getBytes(), 0, toOutput.length());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
				}
				
			}
			
		});

		paintPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				if (paintMode == PaintMode.Pixel && e.getX() >= 0 && e.getY() >= 0)
					paintPixel(e.getX() / blockSize, e.getY() / blockSize);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
			}

		});

		paintPanel.setPreferredSize(new Dimension(data.length * blockSize, data[0].length * blockSize));

		JScrollPane scrollPaneLeft = new JScrollPane(paintPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		basePanel.add(scrollPaneLeft, BorderLayout.CENTER);

		JPanel toolPanel = new JPanel();
		basePanel.add(toolPanel, BorderLayout.NORTH);
		toolPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		pnlColorPicker = new JPanel();
		pnlColorPicker.setPreferredSize(new Dimension(24, 24));
		pnlColorPicker.setBackground(new Color(selectedColor));
		pnlColorPicker.setBorder(new LineBorder(new Color(0, 0, 0)));

		// show the color picker
		pnlColorPicker.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				ColorPicker picker = ColorPicker.getInstance(UI.instance);
				Point location = pnlColorPicker.getLocationOnScreen();
				location.y += pnlColorPicker.getHeight();
				picker.setLocation(location);
				picker.setVisible(true);
			}

		});

		toolPanel.add(pnlColorPicker);

		tglPen = new JToggleButton("Pen");
		tglPen.setSelected(true);
		toolPanel.add(tglPen);

		tglBucket = new JToggleButton("Bucket");
		toolPanel.add(tglBucket);

		tglSave = new JButton("Save"); // button added
		toolPanel.add(tglSave);

		tglLoad = new JButton("Load"); // button added
		toolPanel.add(tglLoad);

		// change the paint mode to PIXEL mode
		tglPen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tglPen.setSelected(true);
				tglBucket.setSelected(false);
				paintMode = PaintMode.Pixel;
			}
		});

		// change the paint mode to AREA mode
		tglBucket.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tglPen.setSelected(false);
				tglBucket.setSelected(true);
				paintMode = PaintMode.Area;
				
			}
			
		});

		tglSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tglLoad.setSelected(false);
				File file = new File("kidPaintData");
				Scanner scn = new Scanner(System.in);
				try {
					FileOutputStream out = new FileOutputStream(file);
					String str = "";
					byte[] buffer = str.getBytes();

//				StringBuilder builder = new StringBuilder();

					for (int i = 0; i < data.length; i++)// for each row
					{
						for (int j = 0; j < data.length; j++)// for each column
						{
							str = "" + data[i][j];
							buffer = str.getBytes();
							out.write(buffer);// append to the output string
							if (j < data.length - 1)
								str = ",";
							buffer = str.getBytes();
							out.write(buffer);

						}
						out.write('\n');// append new line at the end of the row
					}
					out.close();
					scn.close();
					System.out.println("Save file successful");
				} catch (Exception e) {
					System.out.println("Save file failure");
				}
			}
		});

		tglLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tglSave.setSelected(false);
				String fileInput = "";
				try {
					byte[] buffer = new byte[10024];
					String filename = "kidPaintData";
					Scanner scn = new Scanner(System.in);
					File file = new File(filename);
					FileInputStream in = new FileInputStream(file);
					
					long size = file.length();
					while (size > 0) {
						int len = in.read(buffer);
						size -= len;
						fileInput = new String(buffer, 0, len);
						System.out.println(fileInput.toString());
					}
					String[] js = fileInput.split(",|\\n");
					int col = 0; //col of the read in data
					
					for (int i = 0; i < data.length; i++)// for each row
					{
						for (int j = 0; j < data.length; j++)// for each column
						{
							System.out.println();
							data[i][j] = Integer.parseInt(js[j+50*col]);  //col handling
						}
						col++;
					}
					in.close();
					scn.close();
					paintPanel.repaint();
					System.out.println("Load Successful");
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("Load failure");
				}
				String toOutput=("b"+fileInput);
				try {
					out.writeInt(toOutput.length());
					out.write(toOutput.getBytes(), 0, toOutput.length());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				// here should send the data to server and the sketchpads of all connected clients must be updated then  [notice]
			}
		});

		JPanel msgPanel = new JPanel();

		getContentPane().add(msgPanel, BorderLayout.EAST);

		msgPanel.setLayout(new BorderLayout(0, 0));

		msgField = new JTextField(); // text field for inputting message

		msgPanel.add(msgField, BorderLayout.SOUTH);

		// handle key-input event of the message field
		msgField.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 10) { // if the user press ENTER
					System.out.println("Message: "+ msgNeedSend);
					msgNeedSend=("a"+username + ": " + msgField.getText());
					System.out.println("Message merged: "+ msgNeedSend);
					try {
						out.writeInt(msgNeedSend.length());
						out.write(msgNeedSend.getBytes(), 0, msgNeedSend.length());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					
					
					// send to all other chatroom need to be fixed
//					System.out.println(msgNeedSend);
//					msgField.setText("");
				}
			}

		});

		chatArea = new JTextArea(); // the read only text area for showing messages
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);

		JScrollPane scrollPaneRight = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneRight.setPreferredSize(new Dimension(300, this.getHeight()));
		msgPanel.add(scrollPaneRight, BorderLayout.CENTER);

		this.setSize(new Dimension(800, 600));
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * it will be invoked if the user selected the specific color through the color
	 * picker
	 * 
	 * @param colorValue - the selected color
	 */
	public void selectColor(int colorValue) {
		SwingUtilities.invokeLater(() -> {
			selectedColor = colorValue;
			pnlColorPicker.setBackground(new Color(colorValue));
		});
	}

	/**
	 * it will be invoked if the user inputted text in the message field
	 * 
	 * @param text - user inputted text
	 */
	private void onTextInputted(String text) {
		
		if(text.toCharArray()[0]=='a') {
			String noHeader=text.substring(1);
			chatArea.setText(chatArea.getText() + noHeader + "\n");
		}
		if(text.toCharArray()[0]=='b') {
			String noHeader=text.substring(1);
			String[] js = noHeader.split(",|\\n");
			int col = 0; //col of the read in data
			
			for (int i = 0; i < data.length; i++)// for each row
			{
				for (int j = 0; j < data.length; j++)// for each column
				{
					System.out.println();
					data[i][j] = Integer.parseInt(js[j+50*col]);  //col handling
				}
				col++;
			}
			paintPanel.repaint();
		}
		if(text.toCharArray()[0]=='c') {
			
		}
		
	}

	/**
	 * change the color of a specific pixel
	 * 
	 * @param col, row - the position of the selected pixel
	 */
	public void paintPixel(int col, int row) {
		if (col >= data.length || row >= data[0].length)
			return;

		data[col][row] = selectedColor;
		paintPanel.repaint(col * blockSize, row * blockSize, blockSize, blockSize);
		String drawPixel = "" + col + "," +  row + "," + data[col][row];
		
		//here to the data coordinate([col], [row], and the int of data[col][row] to server)
	}

	/**
	 * change the color of a specific area
	 * 
	 * @param col, row - the position of the selected pixel
	 * @return a list of modified pixels
	 */
	public List paintArea(int col, int row) {
		LinkedList<Point> filledPixels = new LinkedList<Point>();

		if (col >= data.length || row >= data[0].length)
			return filledPixels;

		int oriColor = data[col][row];
		LinkedList<Point> buffer = new LinkedList<Point>();

		if (oriColor != selectedColor) {
			buffer.add(new Point(col, row));

			while (!buffer.isEmpty()) {
				Point p = buffer.removeFirst();
				int x = p.x;
				int y = p.y;

				if (data[x][y] != oriColor)
					continue;

				data[x][y] = selectedColor;
				filledPixels.add(p);

				if (x > 0 && data[x - 1][y] == oriColor)
					buffer.add(new Point(x - 1, y));
				if (x < data.length - 1 && data[x + 1][y] == oriColor)
					buffer.add(new Point(x + 1, y));
				if (y > 0 && data[x][y - 1] == oriColor)
					buffer.add(new Point(x, y - 1));
				if (y < data[0].length - 1 && data[x][y + 1] == oriColor)
					buffer.add(new Point(x, y + 1));
			}
			paintPanel.repaint();
		}
		return filledPixels;
	}

	/**
	 * set pixel data and block size
	 * 
	 * @param data
	 * @param blockSize
	 */
	public void setData(int[][] data, int blockSize) {
		this.data = data;
		this.blockSize = blockSize;
		paintPanel.setPreferredSize(new Dimension(data.length * blockSize, data[0].length * blockSize));
		paintPanel.repaint();
	}

	public String getMsgNeedSend() {
		return this.msgNeedSend;
	}

	public void setMsgNeedSend(String msgNeedSend) {
		this.msgNeedSend = username+": "+msgNeedSend;
	}
}
