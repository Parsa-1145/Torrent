package tracker.app;

import common.models.ConnectionThread;
import common.models.Message;
import tracker.controllers.TrackerConnectionController;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static tracker.app.TrackerApp.TIMEOUT_MILLIS;

public class PeerConnectionThread extends ConnectionThread {
	private Map<String, String> fileAndHashes;

	public PeerConnectionThread(Socket socket) throws IOException {
		super(socket);
	}

	@Override
	public boolean initialHandshake() {
		try {
			Message addressResponse = sendAndWaitForResponse(new Message(Map.of("command", "status"), Message.Type.command), TIMEOUT_MILLIS);
			if(addressResponse == null) return false;
			setOtherSideIP(addressResponse.getFromBody("peer"));
			setOtherSidePort(addressResponse.getIntFromBody("listen_port"));

			Message fileResponse = sendAndWaitForResponse(new Message(Map.of("command", "status"), Message.Type.command), TIMEOUT_MILLIS);
			if(fileResponse == null) return false;
			fileAndHashes = fileResponse.getFromBody("files");

			TrackerApp.addPeerConnection(this);
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
	}

	public void refreshStatus() {
		Message addressResponse = sendAndWaitForResponse(new Message(Map.of("command", "status"), Message.Type.command), 1000);
		setOtherSideIP(addressResponse.getFromBody("peer"));
		setOtherSidePort(addressResponse.getIntFromBody("listen_port"));
	}

	public void refreshFileList() {
		Message fileResponse = sendAndWaitForResponse(new Message(Map.of("command", "status"), Message.Type.command), TIMEOUT_MILLIS);
		fileAndHashes = fileResponse.getFromBody("files");

	}

	@Override
	protected boolean handleMessage(Message message) {
		if (message.getType() == Message.Type.file_request) {
			sendMessage(TrackerConnectionController.handleCommand(message));
			return true;
		}
		return false;
	}

	@Override
	public void run() {
		super.run();
		TrackerApp.removePeerConnection(this);
	}

	public Map<String, String> getFileAndHashes() {
		return Map.copyOf(fileAndHashes);
	}
}
