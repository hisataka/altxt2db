package jp.co.altxt2db.launcher;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import jp.co.altxt2db.constants.SystemConstants;
import jp.co.altxt2db.dto.EnvironmentDto;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class Launcher implements SystemConstants {

	public static void main(String[] args) {
		// 引数で受けたものからいろいろ環境準備
		EnvironmentDto env = new EnvironmentDto();

        Options options = new Options();
        Option actionClass =
            OptionBuilder
                .hasArg(true)
                .isRequired(true)
                .withDescription("-e 実行クラス")
                .create("e");
        options.addOption(actionClass);
        CommandLineParser parser = new PosixParser();

        //解析
        CommandLine cmd = null;
        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            //必須オプションが設定されていないので、HELPを出して終了。
            HelpFormatter help = new HelpFormatter();
            help.printHelp("Launcher", options, true);
            System.exit(ERROR);
        }
        env.actionClass = cmd.getOptionValue("e");

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
