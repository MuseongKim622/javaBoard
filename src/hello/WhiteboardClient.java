package hello;

import javax.swing.*;
import java.awt.print.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.border.EmptyBorder;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Color;
import javax.swing.border.BevelBorder;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.CardLayout;
import java.awt.TextField;
import java.awt.Label;

public class WhiteboardClient extends JFrame implements Printable, ActionListener {
	
	private WhiteboardCanvas canvas;
	private JButton			 line, oval, clear, rect, save, open, print;
	
	public WhiteboardClient(String host, int port) {
		super("화이트보드 클라이언트");
		
		
		JToolBar tools = new JToolBar();
		line = new JButton("    선    ");
		line.addActionListener(this);
		oval = new JButton("    원    ");
		oval.addActionListener(this);
		rect = new JButton("사각형");
		rect.addActionListener(this);
		clear = new JButton("지우기");
		clear.addActionListener(this);
		save = new JButton("저장");
		save.addActionListener(this);
		open = new JButton("열기");
		open.addActionListener(this);
		print = new JButton("출력");
		print.addActionListener(this);
		tools.add(line);
		tools.add(oval);
		tools.add(rect);
		tools.addSeparator();
		tools.add(clear);
		tools.add(save);
		tools.add(open);
		tools.add(print);
		
		canvas = new WhiteboardCanvas (host, port);
		
		getContentPane().add("North", tools);
		getContentPane().add("Center", canvas);
		canvas.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		canvas.setBackground(Color.WHITE);
		canvas.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1200, 700);
		setVisible(true);
	}
	
	//그림 프린트
	public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
		if (page > 0) {
			return NO_SUCH_PAGE;
		}
		Graphics2D g2d = (Graphics2D)g;
		g2d.translate(pf.getImageableX(), pf.getImageableY());
		
		canvas.paint(g2d);
		
		return PAGE_EXISTS;
	}
	
	public void actionPerformed(ActionEvent e) {
		Object c = e.getSource();
		
		if(c == line) {
			canvas.setMode(WhiteboardCanvas.LINE);
		} else if (c == oval) {
			canvas.setMode(WhiteboardCanvas.CIRCLE);
		} else if (c == rect) {
			canvas.setMode(WhiteboardCanvas.RECT);
		} else if (c == clear) {
			canvas.send("!x");
		} else if (c == save) {
			canvas.send("!s");
		} else if (c == open) {
			canvas.send("!o");
		} else if (c == print) {
			PrinterJob job = PrinterJob.getPrinterJob();
			job.setPrintable(WhiteboardClient.this);
			boolean ok = job.printDialog();
			if (ok) {
				try {
					job.print();
				} catch (PrinterException ex) {
					
				}
			}
		}
	}
	
	public static void main(String args[]) {
		new WhiteboardClient("localhost", 9850);

	}

	
}
