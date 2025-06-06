package peer.app;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PeerApp {
	public static final int TIMEOUT_MILLIS = 500;

	// TODO: static fields for peer's ip, port, shared folder path, sent files, received files, tracker connection thread, p2p listener thread, torrent p2p threads
	private static P2TConnectionThread trackerThread;
	private static String ip;
	private static int port;
	private static String trackerIp;
	private static int trackerPort;
	private static String folder;

	private static boolean exitFlag = false;

	public static boolean isEnded() {
		return exitFlag;
	}

	public static void initFromArgs(String[] args) throws Exception {
		Matcher peerAddressMatcher = Pattern.compile("(?<ip>\\S+):(?<port>\\d+)").matcher(args[0].trim());
		Matcher trackerAddressMatcher = Pattern.compile("(?<ip>\\S+):(?<port>\\d+)").matcher(args[1].trim());
		if(!(peerAddressMatcher.matches() && trackerAddressMatcher.matches())) throw new RuntimeException("invalid addresses");

		ip = peerAddressMatcher.group("ip");
		port = Integer.parseInt(peerAddressMatcher.group("port"));
		trackerIp = trackerAddressMatcher.group("ip");
		trackerPort = Integer.parseInt(trackerAddressMatcher.group("port"));

		folder = args[2].trim();

		connectTracker();

		// TODO: Initialize peer with command line arguments
		// 2. Parse tracker address (ip:port)
		// 3. Set shared folder path
		// 4. Create tracker connection thread
		// 5. Create peer listener thread
	}

	public static void endAll() {
		exitFlag = true;
		trackerThread.end();

		// TODO: Implement cleanup
		// 1. End tracker connection
		// 2. End all torrent threads
		// 3. Clear file lists
	}

	public static void connectTracker() {
		if(trackerThread == null){
			try {
				Socket socket = new Socket(trackerIp, trackerPort);
				System.out.println(socket);
				trackerThread = new P2TConnectionThread(socket);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}else{
			trackerThread.start();
		}
	}

	public static void startListening() {
		// TODO: Start peer listener thread
		// Check if thread exists and not running, then Start thread
		throw new UnsupportedOperationException("Peer listener thread not implemented yet");
	}

	public static void removeTorrentP2PThread(TorrentP2PThread torrentP2PThread) {
		// TODO: Remove and cleanup torrent thread
		throw new UnsupportedOperationException("Torrent P2P thread not implemented yet");
	}

	public static void addTorrentP2PThread(TorrentP2PThread torrentP2PThread) {
		// TODO: Add new torrent thread
		// 1. Check if thread is valid
		// 2. Check if already exists
		// 3. Add to list
		throw new UnsupportedOperationException("Torrent P2P thread not implemented yet");
	}

	public static String getSharedFolderPath() {
		return folder;
	}

	public static void addSentFile(String receiver, String fileNameAndHash) {
		// TODO: Add file to sent files list
		throw new UnsupportedOperationException("Sent files not implemented yet");
	}

	public static void addReceivedFile(String sender, String fileNameAndHash) {
		// TODO: Add file to received files list
		throw new UnsupportedOperationException("Received files not implemented yet");
	}

	public static String getPeerIP() {
		return ip;
	}

	public static int getPeerPort() {
		return port;
	}

	public static Map<String, List<String>> getSentFiles() {
		// TODO: Get copy of sent files map
		throw new UnsupportedOperationException("Sent files not implemented yet");
	}

	public static Map<String, List<String>> getReceivedFiles() {
		// TODO: Get copy of received files map
		throw new UnsupportedOperationException("Received files not implemented yet");
	}

	public static P2TConnectionThread getP2TConnection() {
		// TODO: Get tracker connection thread
		throw new UnsupportedOperationException("Tracker connection not implemented yet");
	}

	public static void requestDownload(String ip, int port, String filename, String md5) throws Exception {
		// TODO: Implement file download from peer
		// 1. Check if file already exists
		// 2. Create download request message
		// 3. Connect to peer
		// 4. Send request
		// 5. Receive file data
		// 6. Save file
		// 7. Verify file integrity
		// 8. Update received files list
		throw new UnsupportedOperationException("File download not implemented yet");
	}
}
