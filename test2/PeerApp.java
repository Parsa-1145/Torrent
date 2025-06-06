package peer.app;

import common.models.CorruptedFileException;
import common.models.Message;
import common.utils.JSONUtils;
import common.utils.MD5Hash;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PeerApp {
	public static final int TIMEOUT_MILLIS = 500;

	private static P2TConnectionThread trackerThread;
	private static P2PListenerThread peerListenerThread;
	private static ArrayList<TorrentP2PThread> torrentP2PThreads = new ArrayList<>();
	private static Map<String, List<String>> sentFiles = new HashMap<>();
	private static Map<String, List<String>> receivedFiles = new HashMap<>();
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
	}

	public static void endAll() {
		exitFlag = true;
		trackerThread.end();
		for (TorrentP2PThread torrentP2PThread : torrentP2PThreads) {
			torrentP2PThread.end();
		}
		torrentP2PThreads.clear();
		//TODO age vasat download bashe chi mishe
	}

	public static void connectTracker() {
		if(trackerThread == null){
			try {
				Socket socket = new Socket(trackerIp, trackerPort);
				trackerThread = new P2TConnectionThread(socket);
				trackerThread.start();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}else{
			trackerThread.start();
		}
	}

	public static void startListening() {
		if(peerListenerThread == null){
			try {
				peerListenerThread = new P2PListenerThread(port);
				peerListenerThread.start();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}else{
			peerListenerThread.start();
		}
	}

	public static void removeTorrentP2PThread(TorrentP2PThread torrentP2PThread) {
		torrentP2PThreads.remove(torrentP2PThread);
	}

	public static void addTorrentP2PThread(TorrentP2PThread torrentP2PThread) {
		if(torrentP2PThreads.contains(torrentP2PThread)){
			torrentP2PThreads.add(torrentP2PThread);
		}
	}

	public static String getSharedFolderPath() {
		return folder;
	}

	public static void addSentFile(String sender, String fileNameAndHash) {
		sentFiles.putIfAbsent(sender, new ArrayList<>());
		sentFiles.get(sender).add(fileNameAndHash);
	}

	public static void addReceivedFile(String sender, String fileNameAndHash) {
		receivedFiles.putIfAbsent(sender, new ArrayList<>());
		receivedFiles.get(sender).add(fileNameAndHash);
	}

	public static String getPeerIP() {
		return ip;
	}

	public static int getPeerPort() {
		return port;
	}

	public static Map<String, List<String>> getSentFiles() {
		return sentFiles;
	}

	public static Map<String, List<String>> getReceivedFiles() {
		return receivedFiles;
	}

	public static P2TConnectionThread getP2TConnection() {
		return trackerThread;
	}

	public static void requestDownload(String ip, int port, String filename, String md5) throws CorruptedFileException, IOException {
		Socket socket = new Socket(ip, port);

		DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
		DataInputStream inputStream = new DataInputStream(socket.getInputStream());

		Message request = new Message(Message.Type.download_request);
		request.addObject("name", filename);
		request.addObject("md5", md5);
		request.addObject("receiver_ip", getPeerIP());
		request.addObject("receiver_port", getPeerPort());

		socket.setSoTimeout(TIMEOUT_MILLIS);

		try{
			outputStream.writeUTF(JSONUtils.toJson(request));

			File dest = new File(getSharedFolderPath() + "/" + filename);
			FileOutputStream fileOutputStream = new FileOutputStream(dest);

			byte[] buffer = new byte[8192]; // 8 KB buffer
			int bytesRead;

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				fileOutputStream.write(buffer, 0, bytesRead);
			}

			fileOutputStream.flush();

			String newHash = MD5Hash.HashFile(dest.getPath());
			if(!newHash.equals(md5)){
				dest.delete();
				throw new CorruptedFileException("hashes don't match");
			}

			addReceivedFile(ip + ":" + port, filename + " " + md5);
		} catch (IOException e) {
			System.err.println("Request Timed out.");
		}
	}
}
