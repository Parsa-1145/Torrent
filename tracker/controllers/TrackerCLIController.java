package tracker.controllers;

import common.utils.FileUtils;
import tracker.app.PeerConnectionThread;
import tracker.app.TrackerApp;

import java.util.Map;
import java.util.regex.Matcher;

public class TrackerCLIController {
	public static String processCommand(String command) {
		Matcher matcher = null;
		if((matcher = TrackerCommands.LIST_PEERS.getMatcher(command)).matches()){
			return listPeers();
		} else if((matcher = TrackerCommands.END.getMatcher(command)).matches()){
			return endProgram();
		} else if((matcher = TrackerCommands.LIST_FILES.getMatcher(command)).matches()){
			return listFiles(matcher.group("ip"), Integer.parseInt(matcher.group("port")));
		}

		return null;
	}

	private static String getReceives(String command) {
		// TODO: Get list of files received by a peer
		throw new UnsupportedOperationException("getReceives not implemented yet");
	}

	private static String getSends(String command) {
		// TODO: Get list of files sent by a peer
		throw new UnsupportedOperationException("getSends not implemented yet");
	}

	private static String listFiles(String ip, int port) {
		PeerConnectionThread connection = TrackerApp.getConnectionByIpPort(ip, port);

		if(connection == null){
			return "Peer not found.";
		}

		if(connection.getFileAndHashes().isEmpty()){
			return "Repository is empty.";
		}

		return FileUtils.getSortedFileList(connection.getFileAndHashes());
	}

	private static String listPeers() {
		String out = "";

		if(TrackerApp.getConnections().isEmpty()){
			return "No peers connected.";
		}

		for (int i = 0; i < TrackerApp.getConnections().size(); i++) {
			PeerConnectionThread connection = TrackerApp.getConnections().get(i);
			out += String.format("%s:%d", connection.getOtherSideIP(), connection.getOtherSidePort());
			if(i!=TrackerApp.getConnections().size()-1){
				out += "\n";
			}
		}
		return out;
	}

	private static String resetConnections() {
		// TODO: Reset all peer connections
		// Refresh status and file list for each peer
		throw new UnsupportedOperationException("resetConnections not implemented yet");
	}

	private static String refreshFiles() {
		// TODO: Refresh file lists for all peers
		throw new UnsupportedOperationException("refreshFiles not implemented yet");
	}

	private static String endProgram() {
		TrackerApp.endAll();
		return "";
	}
}
