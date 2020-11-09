package hello;

import java.net.*;
import java.io.*;

public class WhiteboardHandler extends Thread {
	private Socket 			 s;
	private BufferedReader 	 i;
	private PrintWriter 	 o;
	private WhiteboardServer server;
	
	public WhiteboardHandler(WhiteboardServer server, Socket s) throws IOException {
		this.s = s;
		this.server = server;
		i = new BufferedReader(new InputStreamReader(s.getInputStream()));
		o = new PrintWriter(s.getOutputStream(), true);
	}
	
	public void run () {
		try {
			server.register(this);
			while (true) {
				broadcast (i.readLine());
			}
		} catch (IOException ex) {
			PrintDebugMessage.print(ex);
		}
		server.unregister(this);
		try {
			i.close();
			o.close();
			s.close();
		} catch (IOException ex) {
			PrintDebugMessage.print(ex);
		}
	}
	
	protected void println(String msg) {
		o.println(msg);
	}
	
	protected void broadcast (String msg) {
		server.broadcast(msg);
	}
}
