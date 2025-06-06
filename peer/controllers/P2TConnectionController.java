package peer.controllers;

import common.models.Message;
import common.utils.FileUtils;
import peer.app.PeerApp;

import static peer.app.PeerApp.TIMEOUT_MILLIS;

public class P2TConnectionController {
	public static Message handleCommand(Message message) {
		String command = (String) message.getFromBody("command");
		if ("status".equals(command)) {
			return status();
		} else if ("get_files_list".equals(command)) {
			return getFilesList();
		} else if ("get_sends".equals(command)) {
			return getSends();
		} else if ("get_receives".equals(command)) {
			return getReceives();
		} else {
			return null;
		}
	}

	private static Message getReceives() {
		Message out = new Message(Message.Type.response);

		out.addObject("command", "get_receives");
		out.addObject("response", "ok");
		out.addObject("received_files", PeerApp.getReceivedFiles());

		return out;
	}

	private static Message getSends() {
		Message out = new Message(Message.Type.response);

		out.addObject("command", "get_sends");
		out.addObject("response", "ok");
		out.addObject("sent_files", PeerApp.getSentFiles());

		return out;
	}

	public static Message getFilesList() {
		Message out = new Message(Message.Type.response);
		out.addObject("command", "get_files_list");
		out.addObject("response", "ok");

		out.addObject("files", FileUtils.listFilesInFolder(PeerApp.getSharedFolderPath()));

		return out;
	}

	public static Message status() {
		Message out = new Message(Message.Type.response);
		out.addObject("command", "status");
		out.addObject("response", "ok");
		out.addObject("peer", PeerApp.getPeerIP());
		out.addObject("listen_port", PeerApp.getPeerPort());

		return out;
	}

	public static Message sendFileRequest(String fileName) throws Exception {
		Message out = new Message(Message.Type.file_request);
		out.addObject("name", fileName);

		return PeerApp.getP2TConnection().sendAndWaitForResponse(out, TIMEOUT_MILLIS);
	}
}
