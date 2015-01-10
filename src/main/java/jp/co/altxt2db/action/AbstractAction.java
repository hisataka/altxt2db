package jp.co.altxt2db.action;

import jp.co.altxt2db.dto.EnvironmentDto;

public abstract class AbstractAction {

	public abstract boolean init();
	public abstract boolean fini();
	public abstract boolean execute();

	public boolean executeCore(EnvironmentDto env) {
		boolean result = true;

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
