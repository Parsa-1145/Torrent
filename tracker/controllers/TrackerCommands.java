package tracker.controllers;

import common.models.CLICommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum TrackerCommands implements CLICommands {
	END("exit"),
	LIST_PEERS("^\\s*list_peers\\s*$"),
	LIST_FILES("^\\s*list_files\\s*(?<ip>\\S+):(?<port>\\d+)\\s*$");;
	// TODO: Implement regex for each command

	private final String regex;

	TrackerCommands(String regex) {
		this.regex = regex;
	}

	@Override
	public Matcher getMatcher(String input) {
		return Pattern.compile(regex).matcher(input);
	}
}
