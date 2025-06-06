package peer.app;

import common.models.Message;
import common.utils.FileUtils;
import common.utils.JSONUtils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static peer.app.PeerApp.TIMEOUT_MILLIS;

public class P2PListenerThread extends Thread {
	private final ServerSocket serverSocket;

	public P2PListenerThread(int port) throws IOException {
		this.serverSocket = new ServerSocket(port);
	}

	private void handleConnection(Socket socket) throws Exception {
		socket.setSoTimeout(TIMEOUT_MILLIS);
		DataInputStream inputStream = new DataInputStream(socket.getInputStream());

		Message message;
		try{
			message = JSONUtils.fromJson(inputStream.readUTF());
		} catch (IOException e) {
			System.err.println("Request Timed out.");
			socket.close();
			return;
		}

		if(message.getType() != Message.Type.download_request){
			socket.close();
			inputStream.close();
		}

		String fileName = message.getFromBody("name");
		String md5 = message.getFromBody("md5");
		String ip = message.getFromBody("receiver_ip");
		int port = message.getIntFromBody("receiver_port");

		File file = FileUtils.getFileByName(PeerApp.getSharedFolderPath(), fileName);

		TorrentP2PThread torrentP2PThread = new TorrentP2PThread(socket, file, ip + ":" + port);

		torrentP2PThread.start();
	}

	@Override
	public void run() {
		while (!PeerApp.isEnded()) {
			try {
				Socket socket = serverSocket.accept();
				handleConnection(socket);
			} catch (Exception e) {
				break;
			}
		}

		try {serverSocket.close();} catch (Exception ignored) {}
	}
}
