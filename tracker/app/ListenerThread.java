package tracker.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class ListenerThread extends Thread {
	private final ServerSocket serverSocket;

	public ListenerThread(int port) throws IOException {
		serverSocket = new ServerSocket(port);
	}

	private void handleConnection(Socket socket) {
		if (socket == null) return;
		try {
			new PeerConnectionThread(socket).start();
		} catch (IOException e) {
			try {socket.close();} catch (IOException ex) {}
		}
	}

	@Override
	public void run() {
		while (!TrackerApp.isEnded()) {
			try {
				Socket socket = serverSocket.accept();
				handleConnection(socket);
			} catch (SocketException e) {
				break;
			} catch (Exception e) {
				e.printStackTrace(); // Log other exceptions
				break;
			}
		}

		try {
			serverSocket.close();
		} catch (Exception ignored) {}
	}

	public void end(){
		try {
			serverSocket.close();
		} catch (IOException e) {
		}
	}
}
