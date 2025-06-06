package peer.controllers;

import common.models.CorruptedFileException;
import common.models.Message;
import common.utils.FileUtils;
import peer.app.P2TConnectionThread;
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
		} else if((matcher = PeerCommands.LIST.getMatcher(command)).matches()){
			return handleListFiles();
		} else if((matcher = PeerCommands.DOWNLOAD.getMatcher(command)).matches()){
			return handleDownload(matcher.group("name"));
		}

		return null;
	}

	private static String handleListFiles() {
		Map<String, String> files = FileUtils.listFilesInFolder(PeerApp.getSharedFolderPath());
		if(files.isEmpty()) return "Repository is empty.";
		return FileUtils.getSortedFileList(files);
	}

	private static String handleDownload(String fileName) {
		try {
			if(FileUtils.listFilesInFolder(PeerApp.getSharedFolderPath()).containsKey(fileName)){
				return "You already have the file!";
			}

			Message response = P2TConnectionController.sendFileRequest(fileName);

			if(response.getFromBody("response").equals("error")){
				if(response.getFromBody("error").equals("not_found")){
					return "No peer has the file!";
				}else{
					return "Multiple hashes found!";
				}
			}

			try {
				PeerApp.requestDownload(response.getFromBody("peer_have"), response.getIntFromBody("peer_port"), fileName,
					response.getFromBody("md5"));
			} catch (CorruptedFileException e) {
				return "The file has been downloaded from peer but is corrupted!";
			}

			return "File downloaded successfully: ";
		} catch (Exception e){
			System.err.println("Request Timed out.");
			return "";
		}
	}

	public static String endProgram() {
		PeerApp.endAll();
		return "";
	}
}
