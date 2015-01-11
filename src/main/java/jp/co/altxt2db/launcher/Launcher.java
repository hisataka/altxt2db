package jp.co.altxt2db.launcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import jp.co.altxt2db.constants.SystemConstants;
import jp.co.altxt2db.dto.EnvironmentDto;

import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class Launcher implements SystemConstants {

	public static void main(String[] args) {
		EnvironmentDto env = new EnvironmentDto();
		if (args.length < 1) {
			System.out.println("usage: Launcher <actionPath> ...");
			System.exit(ERROR);
		}
        env.actionClass = args[0];
        env.args = args;

        // 実行アクションをインスタンス化し、executeメソッドを実行
		try {
			SingletonS2ContainerFactory.init();

			Class<?> clazz = Class.forName(env.actionClass);
			Constructor<?> constructor = clazz.getConstructor(new Class<?>[0]);
			Object instance = constructor.newInstance();
			Method method = instance.getClass().getMethod(ACTINO_EXECUTE, EnvironmentDto.class);
			boolean result = (boolean)method.invoke(instance, env);
			if (result) {
				System.exit(SUCCESS);
			} else {
				System.exit(ERROR);
			}
			SingletonS2ContainerFactory.destroy();
		} catch (Exception e) {
			System.exit(ERROR);
		}
	}

}
