package jp.co.altxt2db.service;

import java.io.Serializable;
import java.util.Calendar;

import javax.annotation.Resource;

import org.seasar.extension.jdbc.JdbcManager;
import org.seasar.extension.jdbc.SqlFileSelect;
import org.seasar.extension.jdbc.SqlFileUpdate;
import org.seasar.extension.jdbc.SqlUpdate;
import org.seasar.extension.jdbc.parameter.Parameter;

/**
 * サービスの抽象クラスです。
 *
 */
public abstract class AbstractGeneralService {

    /**
     * JDBCマネージャです。
     */
    @Resource
    protected JdbcManager jdbcManager;

    /**
     * SQLファイルのパスのプレフィックスです。
     */
    protected String sqlFilePathPrefix = "META-INF/sql/";

    /**
     * SQLファイル検索を返します。
     *
     * @param <T2>
     *            戻り値のJavaBeansの型
     * @param baseClass
     *            戻り値のJavaBeansのクラス
     * @param path
     *            エンティティのディレクトリ部分を含まないSQLファイルのパス
     * @return SQLファイル検索
     */
    protected <T2> SqlFileSelect<T2> selectBySqlFile(Class<T2> baseClass,
            String path) {
        return jdbcManager.selectBySqlFile(baseClass, sqlFilePathPrefix + path);
    }

    /**
     * SQLファイル検索を返します。
     *
     * @param <T2>
     *            戻り値のJavaBeansの型
     * @param baseClass
     *            戻り値のJavaBeansのクラス
     * @param path
     *            エンティティのディレクトリ部分を含まないSQLファイルのパス
     * @param parameter
     *            <p>
     *            パラメータ。
     *            </p>
     *            <p>
     *            パラメータが1つしかない場合は、値を直接指定します。 パラメータが複数ある場合は、JavaBeansを作って、
     *            プロパティ名をSQLファイルのバインド変数名とあわせます。
     *            JavaBeansはpublicフィールドで定義することもできます。
     *            </p>
     *            <p>
     *            パラメータが1つで型が{@link Date}、{@link Calendar}のいずれか場合、{@link Parameter}に定義されたメソッドによりパラメータの時制を指定できます。
     *            </p>
     *            <p>
     *            パラメータが1つで型が{@link String}、<code>ｂyte[]</code>、{@link Serializable}のいずれかの場合、{@link Parameter}に定義されたメソッドによりパラメータをラージオブジェクトとして扱えます。
     *            </p>
     * @return SQLファイル検索
     */
    protected <T2> SqlFileSelect<T2> selectBySqlFile(Class<T2> baseClass,
            String path, Object parameter) {
        return jdbcManager.selectBySqlFile(baseClass, sqlFilePathPrefix + path,
                parameter);
    }

    /**
     * SQLファイル更新を返します。
     *
     * @param path
     *            エンティティのディレクトリ部分を含まないSQLファイルのパス
     * @return SQLファイル更新
     */
    protected SqlFileUpdate updateBySqlFile(String path) {
        return jdbcManager.updateBySqlFile(sqlFilePathPrefix + path);
    }

    /**
     * SQLファイル更新を返します。
     *
     * @param path
     *            エンティティのディレクトリ部分を含まないSQLファイルのパス
     * @param parameter
     *            パラメータ用のJavaBeans
     *
     * @return SQLファイル更新
     */
    protected SqlFileUpdate updateBySqlFile(String path, Object parameter) {
        return jdbcManager.updateBySqlFile(sqlFilePathPrefix + path, parameter);
    }

    protected SqlUpdate updateBySql(String sql) {
    	return jdbcManager.updateBySql(sql);
    }
}