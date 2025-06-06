package common.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileUtils {

	public static Map<String, String> listFilesInFolder(String folderPath) {
		HashMap<String , String > fileHashes = new HashMap<>();

		File folder = new File(folderPath);

		File[] files = folder.listFiles();
		if (files == null) {
			throw new RuntimeException("Failed to list files in directory: " + folderPath);
		}

		for (File file : files) {
			if (file.isFile()) {
				String fileName = file.getName();
				String hash = MD5Hash.HashFile(file.getAbsolutePath());
				fileHashes.put(fileName, hash);
			}
		}
		return fileHashes;
	}

	public static String getSortedFileList(Map<String, String> files) {
		if (files == null || files.isEmpty()) {
			return "Repository is empty.";
		}

		StringBuilder result = new StringBuilder();
		files.keySet().stream()
				.sorted()
				.forEach(fileName -> {
					String hash = files.get(fileName);
					result.append(fileName).append(" ").append(hash).append("\n");
				});
		result.deleteCharAt(result.length()-1);

		return result.toString();
	}
}
