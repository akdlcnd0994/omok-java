package omok;

import java.util.*;
import java.io.*;
import java.net.*;

public class Omok{
	
	static HashMap clients = new HashMap();
	static HashSet hs = new HashSet();
	static HashMap chat = new HashMap();
	static String sp = new String("▤");
	
	public static void main(String[] args) throws IOException, InterruptedException{

		int Port = 7777;
		int Port2 = 8888;
		Socket sc=null, sc2=null;
		ServerSocket sv = new ServerSocket(Port);
		ServerSocket sv2 = new ServerSocket(Port2);
		Collections.synchronizedMap(clients);
		Collections.synchronizedMap(chat);
		
		System.out.println("오목프로그램의 서버가 시작되었습니다.");
		
		while(true){
				   Thread.sleep(1000);
				   if(clients.size()>=2) {
					   Thread.sleep(700);
					   continue;
				   }
			sc = sv.accept();
			Thread t = new Thread(new service(sc));
			t.start();
			System.out.println("1차연결");
			sc2 = sv2.accept();
			Thread t2 = new Thread(new CS(sc2));
			t2.start();
			System.out.println("2차연결");
		}
	}
	
	public static void send(String msg) throws IOException{
		Iterator it = chat.keySet().iterator();
		
		while(it.hasNext()){
			DataOutputStream out = (DataOutputStream) chat.get(it.next());
			out.writeUTF(msg);
		}
	}
	
	public static void play(String msg) throws IOException{
		Iterator it = clients.keySet().iterator();
		if(msg.equals("Pass/1")||(msg.equals("Pass/2"))) {
			int i=0, temp;
			if(msg.equals("Pass/1")) {
				temp=1;
			}
			else {temp=2;}
			while(it.hasNext()){
				i+=1;
				DataOutputStream out = (DataOutputStream) clients.get(it.next());
				
				if(i==temp) {
					out.writeUTF("승리/0/0");
				}
				else {
					out.writeUTF("패배/0/0");					
				}
			}
		}
		while(it.hasNext()){
			DataOutputStream out = (DataOutputStream) clients.get(it.next());
			out.writeUTF(msg);
		}
	}
	
	static class service extends Thread{
		Socket socket, sc2;
		DataInputStream in;
		DataOutputStream out;
		String name;
		String temp;
		String[] str = new String[2];
		service(Socket socket) throws IOException{
			this.socket = socket;
			in = new DataInputStream(socket.getInputStream());
			out = new DataOutputStream(socket.getOutputStream());
		}
		
		public void run(){
			try {
				name = in.readUTF();
				clients.put(name,out);
				hs.add(name);
				
				while(true){
					System.out.println(name);
					if(clients.size()==2){
					Thread.sleep(2000);
					out.writeUTF("OK");
					Iterator it = hs.iterator();
					if(it.hasNext()){
						String h = (String) it.next();
						Thread.sleep(500);
						if(name.equals(h)){
							send("[게임]"+name+"님의 순서입니다."+sp+name);
						}
					}
					break;
					}
				}
				while(in!=null){
					int i=0;
					String d = null;
					Iterator it = hs.iterator();
					temp = in.readUTF();
					str = temp.split("▤");
					if(str[0].equals("Win")){
						play("Pass/"+str[1]);
						continue;
					}
					
					while(it.hasNext()){
						String s = (String)it.next(); i+=1;
						if(s.equals(str[1])){
							d = String.valueOf(i);
						}
						if(!(s.equals(str[1]))){
							send("[게임]"+s+"님의 순서입니다."+sp+s);
						}
					}
					if(str[0].equals("X")) { continue;}
					str[0] += "/" + d;
					play(str[0]);
				}
				
			} catch (IOException e2) {} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}
	
	static class CS extends Thread{
		Socket sc = null;
		DataInputStream in = null;
		DataOutputStream out = null;
		String name;
		CS(Socket sc) throws IOException{
			this.sc = sc;
			in = new DataInputStream(sc.getInputStream());
			out = new DataOutputStream(sc.getOutputStream());
		}
		
		public void run(){
			try {
				name = in.readUTF();
				chat.put(name, out);
				send(name+"님이 입장하셨습니다."+sp+"0");
				System.out.println("현재접속 인원 : "+chat.size());
				while(in!=null){
					send(in.readUTF()+sp+"0");
				}
			} catch (IOException e) {	}
			finally{
				clients.remove(name);
				chat.remove(name);
				hs.remove(name);
				try {
					send(name+"님이 퇴장하셨습니다."+sp+"0");
				} catch (IOException e) {}

				System.out.println(sc.getInetAddress()+"["+name+"]님이 접속을 종료하셨습니다.");
				System.out.println("현재접속 인원 : "+clients.size());
			}
		}
	}
}









