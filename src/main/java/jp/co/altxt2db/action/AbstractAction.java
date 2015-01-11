package jp.co.altxt2db.action;

import jp.co.altxt2db.dto.EnvironmentDto;

public abstract class AbstractAction {

	public abstract boolean init();
	public abstract boolean fini();
	public abstract boolean execute();
	public abstract int setArgLength();

	public String[] args;

	public boolean executeCore(EnvironmentDto env) {
		boolean result = true;

		if (env.args.length - 1 != setArgLength()) {
			System.out.println("usage: Launcher " + env.args[0] + " <metafile-path> <datafile-path>");
			return false;
		}

		int arglen = env.args.length > 1 ? env.args.length - 1 : 0;
		args = new String[arglen];
		for (int i = 0; i < arglen; i ++) {
			args[i] = env.args[i + 1];
		}

		result = init();
		if (!result) {
			return result;
		}

		// 共通フロー①
		// 共通フロー②

		result = execute();
		if (!result) {
			return result;
		}

		// 共通フロー③
		// 共通フロー④

		result = fini();

		return result;
	}
}
