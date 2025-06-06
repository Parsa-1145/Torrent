package peer.controllers;

import common.utils.FileUtils;
import peer.app.PeerApp;

import java.util.Map;
import java.util.regex.Matcher;

public class PeerCLIController {
	public static String processCommand(String command) {
		Matcher matcher = null;
		if((matcher = PeerCommands.END.getMatcher(command)).matches()){
			return endProgram();
		} else if((matcher = PeerCommands.LIST.getMatcher(command)).matches()){
			return handleListFiles();
		}

		return null;
	}

	private static String handleListFiles() {
		Map<String, String> files = FileUtils.listFilesInFolder(PeerApp.getSharedFolderPath());
		if(files.isEmpty()) return "Repository is empty.";
		return FileUtils.getSortedFileList(files);
	}

	private static String handleDownload(String command) {
		// TODO: Handle download command
		// Send file request to tracker
		// Get peer info and file hash
		// Request file from peer
		// Return success or error message
		throw new UnsupportedOperationException("handleDownload not implemented yet");
	}

	public static String endProgram() {
		PeerApp.endAll();
		return "";
	}
}
