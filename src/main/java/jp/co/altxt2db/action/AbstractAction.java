package jp.co.altxt2db.action;


import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import jp.co.altxt2db.dto.EnvironmentDto;

import org.seasar.framework.container.SingletonS2Container;

public abstract class AbstractAction {

	public abstract boolean init();
	public abstract boolean fini();
	public abstract boolean execute();
	public abstract int setArgLength();

	public String[] args;

    protected UserTransaction userTransaction = SingletonS2Container.getComponent(UserTransaction.class);

   @TransactionAttribute(TransactionAttributeType.NEVER)
    public boolean executeCore(EnvironmentDto env) {
		boolean result = true;

		try {
			userTransaction.begin();

			if (env.args.length - 1 != setArgLength()) {
				System.out.println("usage: Launcher " + env.args[0] + " <metafile-path> <datafile-path>");
				throw new Exception("args error");
			}

			int arglen = env.args.length > 1 ? env.args.length - 1 : 0;
			args = new String[arglen];
			for (int i = 0; i < arglen; i ++) {
				args[i] = env.args[i + 1];
			}

			result = init();
			if (!result) {
				throw new Exception("init error");
			}

			result = execute();
			if (!result) {
				throw new Exception("execute error");
			}

			result = fini();
			if (!result) {
				throw new Exception("fini error");
			}

			userTransaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
			try {
				userTransaction.setRollbackOnly();
			} catch (IllegalStateException | SystemException e1) {
				e1.printStackTrace();
			}
		}

		return result;
	}
}
