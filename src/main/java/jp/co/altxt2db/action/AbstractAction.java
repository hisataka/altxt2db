package jp.co.altxt2db.action;


import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import jp.co.altxt2db.dto.EnvironmentDto;

import org.seasar.framework.container.SingletonS2Container;

/**
 * 基底アクションクラス
 * 
 * executeメソッドのみトランザクション制御を行う
 * init,finiにてトランザクション制御が必要となる操作はしない前提
 * 
 */
public abstract class AbstractAction {

    /**
     * 初期処理メソッド
     * @return
     */
	public abstract boolean init();
	
	/**
	 * 終了処理メソッド
	 * @return
	 */
	public abstract boolean fini();
	
	/**
	 * メイン処理メソッド
	 * @return
	 */
	public abstract boolean execute();
	
	/**
	 * 個別アクションとして必要となる引数の数を設定
	 * @return
	 */
	public abstract int setArgLength();

	/**
	 * 個別アクションとして利用する引数の配列
	 */
	public String[] args;
	
	/**
	 * 個別アクションの引数の使い方メッセージ
	 * @return
	 */
	public abstract String usageArgs();

	/**
	 * ユーザトランザクション
	 */
    protected UserTransaction userTransaction = SingletonS2Container.getComponent(UserTransaction.class);

    /**
     * アクション実行コア処理
     * 
     * @param env
     * @return
     */
   @TransactionAttribute(TransactionAttributeType.NEVER)
    public boolean executeCore(EnvironmentDto env) {
		boolean result = true;

		try {

			if (env.args.length - 1 != setArgLength()) {
				System.out.println("usage: Launcher " + env.args[0] + " " + usageArgs());
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

            userTransaction.begin();
			result = execute();
			if (!result) {
				throw new Exception("execute error");
			}
            userTransaction.commit();

			result = fini();
			if (!result) {
				throw new Exception("fini error");
			}

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
