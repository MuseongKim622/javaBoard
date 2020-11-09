package hello;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class WhiteboardCanvas extends JPanel implements Runnable, MouseListener, MouseMotionListener {
	public static final int NONE = 0;
	public static final int LINE = 1;
	public static final int CIRCLE = 2;
	public static final int RECT = 3;
	
	private PrintWriter 	o;
	private BufferedReader  i;
	private int 			mode;
	private Vector 			pictures;
	private int				tempX, tempY;
	private Thread			listener;
	
	public WhiteboardCanvas(String host, int port) {
		pictures = new Vector();
		addMouseListener(this);
		addMouseMotionListener(this);
		try {
			Socket s = new Socket(host, port);
			o = new PrintWriter(s.getOutputStream(), true);
			i = new BufferedReader(new InputStreamReader(s.getInputStream()));
			listener = new Thread(this);
			listener.start();
		} catch (Exception e) {
			PrintDebugMessage.print(e);
		}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		int n = pictures.size();
		for(int i = 0; i < n; i++) {
			Drawable d = (Drawable)pictures.elementAt(i);
			d.paint(g);
		}
	}
	
	public void run () {
		try {
			while (true) {
				String line = i.readLine();
				if(line.equals("!x")) {
					clear();
					line = "";
				}
				
				if(line.equals("!s")) {
					int n = pictures.size();
					
					FileOutputStream output = new FileOutputStream("C:\\Users\\CE305\\Desktop\\a.txt");
					
					for(int i = 0; i < n; i++) {
						System.out.println(pictures.elementAt(i).toString());
												
						String data = pictures.elementAt(i).toString() + "\n";
						output.write(data.getBytes());
					}
					output.close();
					line = "";
				}
				
				if(line.equals("!o")) {
					clear();
					
					int n = pictures.size();
					File file = new File("C:\\Users\\CE305\\Desktop\\a.txt");
					FileReader filereader = new FileReader(file);
					BufferedReader bufReader = new BufferedReader(filereader);
					line = "";
					
					while((line = bufReader.readLine()) != null) {
						// 도형 정보를 받기위한 공간
						int s, d1, d2, d3, d4;
						
						// 텍스트 파일로 저장되어 있는 도형 정보를 ", "로 분리
						String[] tokens = line.split(", ");
						
						// 분리된 문자열을 String to Integer
						s = Integer.parseInt(tokens[0]);
						d1 = Integer.parseInt(tokens[1]);
						d2 = Integer.parseInt(tokens[2]);
						d3 = Integer.parseInt(tokens[3]);
						d4 = Integer.parseInt(tokens[4]);

						switch(s)
						{
							case 1:
								pictures.addElement(new Line(d1, d2, d3, d4));
								break;
							case 2:
								pictures.addElement(new Circle(d1, d2, d3, d4));
								break;
							case 3:
								pictures.addElement(new Rect(d1, d2, d3, d4));
								break;
						}
					}
					bufReader.close();
					
					repaint();
					line = "";
				}
					
					
				if(line != null)
					draw(line);
			}
		} catch (Exception e) {
			PrintDebugMessage.print(e);
		}
	}
	
	public void send(String msg) {
		o.println(msg);
	}
	
	public void clear() {
		pictures.removeAllElements();
		repaint();
	}
	
	public void draw(String data) {
		PrintDebugMessage.print(data);
		
		int d[] = new int[5];
		StringTokenizer st = new StringTokenizer(data, ":", false);
		int index = 0;
		while(st.hasMoreTokens()) {
			d[index] = Integer.parseInt(st.nextToken());
			index++;
		}
		switch(d[0]) {
			case NONE:
				break;
			case LINE:
				pictures.addElement(new Line(d[1], d[2], d[3], d[4]));
				break;
			case CIRCLE:
				pictures.addElement(new Circle(d[1], d[2], d[3], d[4]));
				break;
			case RECT:
				pictures.addElement(new Rect(d[1], d[2], d[3], d[4]));
				break;
		}
		repaint();
	}
	
	public void setMode(int m) {
		mode = m;
	}
	
	public void mouseClicked(MouseEvent e) { }
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	
	public void mousePressed(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		String msg;
		switch(mode) {
			default:
				return;
			case LINE:
				tempX = x;
				tempY = y;
				break;
			case CIRCLE:
				msg = CIRCLE + ":" + x + ":" + y + ":" + "10" + ":" + "10";
				send(msg);
				break;
			case RECT:
				msg = RECT + ":" + x + ":" + y + ":" + "10" + ":" + "10";
				send(msg);
				break;
		}
	}
	
	public void mouseMoved(MouseEvent e) { }
	public void mouseDragged(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		String msg;
		switch(mode) {
			case NONE:
				return;
			case LINE:
				msg = LINE + ":" + tempX + ":" + tempY + ":" + x + ":" + y;
				send(msg);
				tempX = x;
				tempY = y;
				break;
			default:
				return;
		}
	}
}
