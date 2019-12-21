
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class InputNameWindow extends JFrame {
	Scanner scn = new Scanner(System.in);
	static String name = "";
	

	public InputNameWindow() {

		this.setTitle("Input your name");
		this.setSize(new Dimension(320, 240));
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Container container = this.getContentPane();
		container.setLayout(new FlowLayout());
		
		JTextArea textArea = new JTextArea(5, 30);
		JScrollPane scrollPane = new JScrollPane(textArea); 
		textArea.setEditable(false);
		textArea.append("Please input your name");
		container.add(textArea);
		
		JTextField txt = new JTextField();
		container.add(txt);
		JButton submitB = new JButton("Enter");
		container.add(submitB);

		txt.setPreferredSize(new Dimension(200, 30));
		txt.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				System.out.println("You pressed and then released key #" + e.getKeyChar());
			}

			@Override
			public void keyPressed(KeyEvent e) {
				System.out.println("You pressed key #" + e.getKeyCode());

			}

			@Override
			public void keyReleased(KeyEvent e) {
				System.out.println("You released key #" + e.getKeyCode());
			}
		});

		submitB.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				name = "" + txt.getText();  //set input to name
				System.out.println("the name: " + name);  //test 
				try {
					closeOpen();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}

		});

		this.setVisible(true);

	}
	
//	@SuppressWarnings("static-access")
//	public String getName() {
//		return this.name;
//	}
	
	private void closeOpen() throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		UI ui = UI.getInstance(); // get the instance of UI
		ui.setData(new int[50][50], 20); // set the data array and block size. comment this statement to use the default
											// data array and block size.
		ui.setVisible(true);
		this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));  //close previos window
	}
}