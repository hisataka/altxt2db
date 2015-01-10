package jp.co.altxt2db.action;

import jp.co.altxt2db.logic.Altxt2DbLogic;

import org.seasar.framework.container.SingletonS2Container;


public class Altxt2DbAction extends AbstractAction {

	public Altxt2DbLogic altxt2DbLogic = SingletonS2Container.getComponent(Altxt2DbLogic.class);

	@Override
	public boolean init() {
		return true;
	}

	@Override
	public boolean fini() {
		return true;
	}

	@Override
	public boolean execute() {
		return true;
	}
}
