package jp.co.altxt2db.action;

import jp.co.altxt2db.constants.SystemConstants;
import jp.co.altxt2db.logic.Altxt2DbLogic;
import jp.co.altxt2db.service.Altxt2DbService;

import org.seasar.framework.container.SingletonS2Container;


public class Altxt2DbAction extends AbstractAction implements SystemConstants {

	public Altxt2DbLogic altxt2DbLogic = SingletonS2Container.getComponent(Altxt2DbLogic.class);
	public Altxt2DbService altxt2DbService = SingletonS2Container.getComponent(Altxt2DbService.class);

	private String metaPath;
	private String dataPath;

	@Override
	public int setArgLength() {
		return 2;
	}

	@Override
	public boolean init() {
        metaPath = args[0];
        dataPath = args[1];
		return true;
	}

	@Override
	public boolean fini() {
		return true;
	}

	@Override
	public boolean execute() {

//		List<HOGE> l = altxt2DbService.selecthoge();

	//	for (HOGE h: l) {
	//		System.out.println("col:" + h.col);
		//}

		// メタ情報をどう扱うか・・・
		// まずはjsonオブジェクトとして生成しなきゃ

		return true;
	}
}
