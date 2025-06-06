package tracker.controllers;

import common.models.ConnectionThread;
import common.models.Message;
import tracker.app.PeerConnectionThread;
import tracker.app.TrackerApp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static tracker.app.TrackerApp.TIMEOUT_MILLIS;

public class TrackerConnectionController {

	public static Map<String, List<String>> getReceives(PeerConnectionThread connection) {
		Message request = new Message(Map.of("command", "get_receives"), Message.Type.command);

		try {
			Message response = connection.sendAndWaitForResponse(request, TIMEOUT_MILLIS);

			Map<String, List<String>> receivedFiles = response.getFromBody("received_files");

			return receivedFiles == null ? new HashMap<>() : receivedFiles;
		} catch (Exception e) {
			System.err.println("Request Timed out.");
			return new HashMap<>();
		}
	}

	public static Map<String, List<String>> getSends(PeerConnectionThread connection) {
		Message request = new Message(Map.of("command", "get_sends"), Message.Type.command);

		try {
			Message response = connection.sendAndWaitForResponse(request, TIMEOUT_MILLIS);

			Map<String, List<String>> sentFiles = response.getFromBody("sent_files");
			return sentFiles == null ? new HashMap<>() : sentFiles;
		} catch (Exception e) {
			System.err.println("Request Timed out.");
			return new HashMap<>();
		}
	}

	public static Message fileRequest(Message message) {
		String fileName = message.getFromBody("name");
		ArrayList<PeerConnectionThread> peers = new ArrayList<>();
		for (PeerConnectionThread connection : TrackerApp.getConnections()) {
			Map<String, String> fileAndHashes = connection.getFileAndHashes();
			if(fileAndHashes.containsKey(fileName)){
				peers.add(connection);
			}
		}

		if(peers.isEmpty()){
			return new Message(Map.of("response", "error", "error", "not_found"), Message.Type.response);
		}

		String hash = peers.get(0).getFileAndHashes().get(fileName);
		for (PeerConnectionThread peer : peers) {
			if(!hash.equals(peer.getFileAndHashes().get(fileName))){
				return new Message(Map.of("response", "error", "error", "multiple_hash"), Message.Type.response);
			}
		}

		return new Message(Map.of("response", "peer_found", "md5", hash, "peer_have", peers.get(0).getOtherSideIP(),
				"peer_port", peers.get(0).getOtherSidePort()), Message.Type.response);
	}
}
