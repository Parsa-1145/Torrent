package tracker.controllers;

import common.models.CLICommands;
import common.models.ConnectionThread;
import common.utils.FileUtils;
import tracker.app.PeerConnectionThread;
import tracker.app.TrackerApp;

import java.util.*;
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
		} else if((matcher = TrackerCommands.REFRESH_FILES.getMatcher(command)).matches()){
			return refreshFiles();
		} else if((matcher = TrackerCommands.RESET_CONNECTIONS.getMatcher(command)).matches()){
			return resetConnections();
		} else if((matcher = TrackerCommands.GET_SENDS.getMatcher(command)).matches()){
			return getSends(matcher.group("ip"), Integer.parseInt(matcher.group("port")));
		} else if((matcher = TrackerCommands.GET_RECEIVES.getMatcher(command)).matches()){
			return getReceives(matcher.group("ip"), Integer.parseInt(matcher.group("port")));
		}

		return CLICommands.invalidCommand;
	}

	private static String getReceives(String ip, int port) {
		PeerConnectionThread connection = TrackerApp.getConnectionByIpPort(ip, port);
		if(connection == null){
			return "Peer not found.";
		}
		Map<String, List<String>> receives = TrackerConnectionController.getReceives(connection);

		if(receives.isEmpty()){
			return "No files received by " + ip + ":" + port;
		}

		List<Map.Entry<String, String>> flattenedList = new ArrayList<>();
		for (Map.Entry<String, List<String>> entry : receives.entrySet()) {
			String ipPort = entry.getKey();
			for (String fileMd5 : entry.getValue()) {
				flattenedList.add(new AbstractMap.SimpleEntry<>(fileMd5, ipPort));
			}
		}

		flattenedList.sort(Comparator.comparing(Map.Entry<String, String>::getKey).thenComparing(Map.Entry<String, String>::getValue));

		StringBuilder out = new StringBuilder();
		for (Map.Entry<String, String> entry : flattenedList) {
			out.append(entry.getKey()).append(" - ").append(entry.getValue()).append("\n");
		}
		out.deleteCharAt(out.length()-1);

		return out.toString();
	}

	private static String getSends(String ip, int port) {
		PeerConnectionThread connection = TrackerApp.getConnectionByIpPort(ip, port);
		if(connection == null){
			return "Peer not found.";
		}
		Map<String, List<String>> sends = TrackerConnectionController.getSends(connection);

		if(sends.isEmpty()){
			return "No files sent by " + ip + ":" + port;
		}

		List<Map.Entry<String, String>> flattenedList = new ArrayList<>();
		for (Map.Entry<String, List<String>> entry : sends.entrySet()) {
			String ipPort = entry.getKey();
			for (String fileMd5 : entry.getValue()) {
				flattenedList.add(new AbstractMap.SimpleEntry<>(fileMd5, ipPort));
			}
		}

		flattenedList.sort(Comparator.comparing(Map.Entry<String, String>::getKey).thenComparing(Map.Entry<String, String>::getValue));

		StringBuilder out = new StringBuilder();
		for (Map.Entry<String, String> entry : flattenedList) {
			out.append(entry.getKey()).append(" - ").append(entry.getValue()).append("\n");
		}
		out.deleteCharAt(out.length()-1);

		return out.toString();
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
		for (int i = TrackerApp.getConnections().size() - 1; i >= 0; i--) {
			PeerConnectionThread connection = TrackerApp.getConnections().get(i);
			connection.refreshStatus();
		}

		refreshFiles();
		return "";
	}

	private static String refreshFiles() {
		for (int i = TrackerApp.getConnections().size() - 1; i >= 0; i--) {
			TrackerApp.getConnections().get(i).refreshFileList();
		}

		return "";
	}

	private static String endProgram() {
		TrackerApp.endAll();
		return "";
	}
}
