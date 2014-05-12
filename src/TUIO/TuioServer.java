package TUIO;

import netP5.NetAddress;
import oscP5.OscBundle;
import oscP5.OscMessage;
import oscP5.OscP5;

public class TuioServer {

	NetAddress remote;
	OscP5 oscP5;
	int fid;

	public TuioServer(String host, int port) {
		oscP5 = new OscP5(this, 12000);
		remote = new NetAddress(host, port);
		fid = 0;
	}

	public void send(String type, TuioCursor tp) {
		TuioCursor[] tps = { tp };
		send(type, tps);
	}

	public void send(String type, TuioCursor[] tps) {

		int len = tps.length;

		OscBundle myBundle = new OscBundle();

		OscMessage myMessage = new OscMessage("/tuio/2Dcur");
		myMessage.add("source"); /* add an int to the osc message */
		myMessage.add("rencontre_i");
		myBundle.add(myMessage);

		myMessage.clear();

		/* refill the osc message object again */
		myMessage.setAddrPattern("/tuio/2Dcur");
		myMessage.add("alive");

		if (type.equals("update") || type.equals("alive")) {
			for (int i = 0; i < tps.length; i++) {
				myMessage.add((int)tps[i].session_id);
			}

		}

		myBundle.add(myMessage);

		if (type.equals("update")) {
			for (int i = 0; i < tps.length; i++) {
				// println("set "+tps[i].id);
				myMessage.clear();
				myMessage.setAddrPattern("/tuio/2Dcur");
				myMessage.add("set");
				myMessage.add((int)tps[i].session_id);
				myMessage.add(tps[i].xpos);
				myMessage.add(tps[i].ypos);
				myMessage.add(tps[i].x_speed);
				myMessage.add(tps[i].y_speed);
				myMessage.add(tps[i].motion_accel);
				myBundle.add(myMessage);
			}

		}

		myMessage.clear();
		myMessage.setAddrPattern("/tuio/2Dcur");
		myMessage.add("fseq");
		myMessage.add(fid);
		myBundle.add(myMessage);

		/* send the message */
		oscP5.send(myBundle, remote);

		fid++;

	}

}
