package jp.co.altxt2db.constants;

/**
 * システム定数定義
 * 
 * @author tie302852
 *
 */
public interface SystemConstants {
    /** バッチ成功値 */
	public final static int SUCCESS = 0;
	/** バッチ失敗値 */
	public final static int ERROR = 1;
	/** アクション実行メソッド名 */
	public final static String ACTINO_EXECUTE = "executeCore";
	/** 一時テーブルプレフィックス */
	public final static String WORK_PREFIX = "_____altxt2db_";
	/** MS932文字コード */
	public final static String MS932 = "MS932";
	/** TRUE文字列 */
	public final static String TRUE = "true";
	/** 空値 */
	public final static String EMPTY = "";
}
