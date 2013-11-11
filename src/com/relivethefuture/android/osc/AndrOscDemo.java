package com.relivethefuture.android.osc;

import java.io.IOException;
import java.net.InetSocketAddress;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.relivethefuture.osc.data.BasicOscListener;
import com.relivethefuture.osc.data.OscMessage;
import com.relivethefuture.osc.transport.OscClient;
import com.relivethefuture.osc.transport.OscServer;

public class AndrOscDemo extends Activity implements OnClickListener {
	private static final String TAG = "AndrOscDemo";
	private OscClient sender;
	private Button sketch1;
	private Button sketch2;
	
	private OscServer server;

	public class LooperListener extends BasicOscListener {
		public Context c;
		
		@Override
		public void handleMessage(OscMessage msg) {
			System.out.println("Message " + msg.getAddress());
			System.out.println("Type Tags " + msg.getTypeTags());
			
			Toast.makeText(AndrOscDemo.this, "OSCmessage: " + msg.toString(), Toast.LENGTH_LONG);
			
		}
	}

	
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		sender = new OscClient(true);
		String destination = "192.168.0.186";
		int destPort = 12000;
		InetSocketAddress addr = new InetSocketAddress(destination, destPort);
		sender.connect(addr);

		sketch1 = (Button) this.findViewById(R.id.sketch1);
		sketch1.setOnClickListener(this);
		sketch2 = (Button) this.findViewById(R.id.sketch2);
		sketch2.setOnClickListener(this);
		
		try {
			server = new OscServer(7999);
			server.start();
		}
		catch (IOException e) {
			Toast.makeText(this, "Failed to start OSC server: " + e.getMessage(), Toast.LENGTH_LONG);
		}
		server.addOscListener(new LooperListener());
		
	}

	@Override
	public void onClick(View v) {
		OscMessage msg = null;
		float oscFloat = 1.0f;

		if (v == sketch1) {
			msg = new OscMessage("/luminous/sketch1");
		    msg.addArgument(oscFloat);
		    
		} else if (v == sketch2) {
			msg = new OscMessage("/luminous/sketch2");
		    msg.addArgument(oscFloat);
		}

		if (msg != null) {
			try {
				sender.sendPacket(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}