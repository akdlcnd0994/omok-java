package omok;



import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

class client {
	public static void main(String[] args) {
		Game f = new Game("1:1 오목");
	}
}

class Game implements MouseListener {
	static String name;
	String SIp = "127.0.0.1";
	static int[][] arr = new int[19][19];
	static boolean Count = false;
	static Thread t = null;
	static DataOutputStream dos = null;
	static JFrame f = null;
	JButton b1 = new JButton("게임 대상 찾기");
	JButton b2 = new JButton("전체 전적 확인");
	static JPanel p = new JPanel();
	JPanel p2 = new JPanel();
	static JTextArea ta = new JTextArea();
	static JTextField tf = new JTextField();
	static ImageIcon m1 = new ImageIcon("omokpan.jpg");
	static ImageIcon bs = new ImageIcon("black_stone.png");
	static ImageIcon ws = new ImageIcon("white_stone.png");
	static JLabel l1 = new JLabel(m1);
	static JLabel l2 = new JLabel("채팅");
	JLabel tl = new JLabel("멀티 채팅 오목 프로그램");

	Game(String name) {
		f = new JFrame("1:1 오목");
		f.addWindowListener(new FrameListener());
		f.setLayout(new BorderLayout());
		f.add(b1, "West");
		f.add(b2, "East");
		tl.setHorizontalAlignment(JLabel.CENTER);
		tl.setFont(new Font("HY동녘B", Font.BOLD, 20));
		f.add(tl, "Center");
		f.setSize(900, 480);
		f.setVisible(true);
		b1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				try {
					connect();
				} catch (IOException e) {
				}
			}
		});
		b2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// 디비승패 데이터 끌어오기
			}
		});
	}

	public void connect() throws UnknownHostException, IOException {
		String temp;
		DataInputStream in;
		Socket socket = new Socket(SIp, 7777);
		name = JOptionPane.showInputDialog("서버에 기록하실 닉네임을 입력하세요.");

		f.add(tl, "Center");
		in = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		dos.writeUTF(name);
		chat();
		temp = in.readUTF();
		if (temp.equals("OK")) {
			Thread gg = new Thread(new play(in));
			gg.start();

			f.remove(b1);
			f.remove(b2);
			f.remove(tl);
			f.setVisible(false);
			ta.setBackground(Color.gray);
			ta.setFont(new Font("HY동녘B", Font.BOLD, 20));
			f.setLayout(new GridLayout(1, 2));
			p2.setLayout(new BorderLayout());
			l2.setFont(new Font("고딕체", Font.BOLD, 30));
			l2.setForeground(Color.magenta);
			p.add(l1);
			l1.addMouseListener(this);
			p2.add(l2, "North");
			JScrollPane scroll = new JScrollPane(ta);
			p2.add(scroll, "Center");
			p2.add(tf, "South");
			f.add(p);
			f.add(p2);
			f.setSize(900, 480);
			f.setVisible(true);
		}
	}

	static class play extends Thread {
		DataInputStream in;
		String temp;
		String[] str = new String[3];
		int a, b, c;

		play(DataInputStream in) throws IOException {
			this.in = in;
		}

		public void run() {
			try {
				while (in != null) {
					str = in.readUTF().split("/");
					if(str[0].equals("승리")) {
						JOptionPane.showMessageDialog(f, "승리하셨습니다 !!!");
						f.setVisible(false);
						break;
					}
					else if(str[0].equals("패배")) {
						JOptionPane.showMessageDialog(f, "패배하셨습니다 ...");
						f.setVisible(false);
						break;
					}

					a = Integer.parseInt(str[0]);
					b = Integer.parseInt(str[1]);
					c = Integer.parseInt(str[2]);
					set(a, b, c);
					solution(a,b,c);
				}
			} catch (IOException e) {
			}
		}

		public void set(int a, int b, int c) {
			int t1, t2;
			arr[a][b] = c;
			t1 = a * 22;
			t2 = b * 22;
			if (c == 1) {
				JLabel t = new JLabel(bs);
				t.setBounds(t1, t2, bs.getIconWidth(), bs.getIconHeight());
				l1.add(t);
				p.repaint();
				p.revalidate();
				
			} else {
				JLabel t = new JLabel(ws);
				t.setBounds(t1, t2, bs.getIconWidth(), bs.getIconHeight());
				l1.add(t);
				p.repaint();
				p.revalidate();
			}
		}
		
		public void solution(int a, int b, int c) throws IOException {
			int i, j, x, y, temp, count=0;
			
			for(i=a;i>=a-4&&i>=0;i--) { //가로
				temp=0;
				for(j=i;j<=i+4&&i<arr.length-4;j++) {
					if(arr[j][b]==c) {
						temp+=1;
					}
				}
				if(temp==5) {
					count=1;
				}
			}
			
			for(i=b;i>=b-4&&i>=0;i--) { //세로
				temp=0;
				for(j=i;j<=i+4&&i<arr.length-4;j++) {
					if(arr[a][j]==c) {
						temp+=1;
					}
				}
				if(temp==5) {
					count=1;
				}
			}
			
			for(i=a,j=b;i>=a-4&&j>=b-4&&i>=0&&j>=0;i--,j--) { //대각선 (\ 역슬러쉬 모양)
				temp=0;
				for(x=i,y=j; x<=i+4&&y<=j+4&&x<arr.length-4&&y<arr.length-4; x++,y++) {
					if(arr[x][y]==c) {
						temp+=1;
					}
				}
				if(temp==5) {
					count=1;
				}
			}
			
			for(i=a,j=b;i<=a+4&&j>=b-4&&i<arr.length&&j>=0;i++,j--) { //대각선 (/ 슬러쉬 모양)
				temp=0;
				for(x=i,y=j; x>=i-4&&y<=j+4&&x>=0&&y<arr.length-4; x--,y++) {
					if(arr[x][y]==c) {
						temp+=1;
					}
				}
				if(temp==5) {
					count=1;
				}
			}
			
			if(count==1) {
				String t = "Win▤" + String.valueOf(c);
				dos.writeUTF(t);
			}
			
			
		}
		

	}

	public void chat() throws UnknownHostException, IOException {
		Socket socket = new Socket(SIp, 8888);
		Receive Thread = new Receive(socket, name);
		Thread.start();
	}

	static class Receive extends Thread implements KeyListener {
		DataOutputStream out;
		DataInputStream in;
		String name;
		String temp;
		String[] str = new String[2];

		Receive(Socket socket, String name) throws IOException {
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
			this.name = name;
			out.writeUTF(name);
			tf.addKeyListener(this);
		}

		public void run() {
			while (in != null) {
				try {
					temp = in.readUTF();
					str = temp.split("▤");
					ta.append(str[0] + "\n");
					ta.setCaretPosition(ta.getDocument().getLength());
					if (str[1].equals(name)) {
						turn();
						t = new Thread(new Timer());
						t.start();
					}
				} catch (IOException e) {
				}
			}
		}

//------------------KeyListener
		public void keyPressed(KeyEvent ke) {
			String t;
			if (ke.getKeyCode() == 10) {
				t = tf.getText();
				t = "[" + name + "] : " + t;
				try {
					out.writeUTF(t);
				} catch (IOException e) {
				}
				tf.setText("");
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub

		}
	}

//-----FrameListener Class
	static class FrameListener extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}

//---------------------------MouseListener
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		int X = e.getX(), Y = e.getY();
		int a = 7, b = 13;
		int t1 = -1, t2 = -1;

		if (Count == true) {
			for (int i = 0; i < 19; i++) {
				if (X >= a && X <= b) {
					t1 = i;
				}
				if (Y >= a && Y <= b) {
					t2 = i;
				}
				a += 22;
				b += 22;
			}
			if ((t1 != -1 && t2 != -1) && arr[t1][t2]==0) {
				String temp = String.valueOf(t1) + "/" + String.valueOf(t2) + "▤" + name;
				try {
					dos.writeUTF(temp);
					turn();
					t.stop();
					l2.setText("채팅");
				} catch (IOException e1) {
				}
			}
			else {
				JOptionPane.showMessageDialog(f, "알맞지 않은 자리거나 플레이어의 턴이 아닙니다.");
			}
		} else {
			JOptionPane.showMessageDialog(f, "알맞지 않은 자리거나 플레이어의 턴이 아닙니다.");
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
	}
	
	static class Timer extends Thread{
		int num;
		String temp = "X▤" + name;
		public void run() {
			num=30;
			while(num!=0) {
			try {
				l2.setText("채팅                            Time: " + num--);
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
			}
			
			l2.setText("채팅");
			try {
				turn();
				dos.writeUTF(temp);
			} catch (IOException e) {}
		}
	}
	
	

	public static void turn() {
		if (Count == true)
			Count = false;
		else if (Count == false)
			Count = true;
	}
	
	
	
	

}