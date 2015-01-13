package jp.co.altxt2db.action;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import jp.co.altxt2db.constants.SystemConstants;
import jp.co.altxt2db.dto.AltxtMetaDto;
import jp.co.altxt2db.logic.Altxt2DbLogic;
import jp.co.altxt2db.service.Altxt2DbService;
import jp.co.altxt2db.util.CsvParser;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

import org.seasar.framework.container.SingletonS2Container;

/**
 * AltxtをDBへ格納するバッチ
 * 
 * @author tie302852
 *
 */
public class Altxt2DbAction extends AbstractAction implements SystemConstants {

    /**
     * 引数の数定義
     */
	public final static int ARG_LENGTH = 2;

	/**
	 * ロジッククラス
	 */
	public Altxt2DbLogic altxt2DbLogic = SingletonS2Container.getComponent(Altxt2DbLogic.class);
	
	/**
	 * DBサービスクラス
	 */
	public Altxt2DbService altxt2DbService = SingletonS2Container.getComponent(Altxt2DbService.class);

	/**
	 * 処理IFのメタ情報格納DTO
	 */
	private AltxtMetaDto altxtMetaDto;
	
	/**
	 * 処理対象データファイルパス
	 */
	private String dataPath;
	
	/**
	 * 処理対象IF向け一時テーブルへのマージSQL
	 */
	private String merge2WorkSql;
	
	/**
	 * 処理対象IF向け一時テーブルからメインテーブルへのマージSQL
	 */
	private String merge2MainSql;
	
	/**
	 * 処理対象IF向け子テーブル削除SQL
	 */
	private List<String> deleteChildSqls;
	
    /**
     * ${inheritDoc}
     */
	@Override
	public int setArgLength() {
		return ARG_LENGTH;
	}

    /**
     * ${inheritDoc}
     */
	@Override
	public boolean init() {
        dataPath = args[1];
		try {
			altxtMetaDto = JSON.decode(new FileReader(args[0]), AltxtMetaDto.class);
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			return false;
		}
		// 一時テーブルの再生成
		altxt2DbService.createWork(altxtMetaDto.table);
		
		// 各種SQLをメタデータにしたがって生成
		merge2WorkSql = altxt2DbLogic.makeMerge2WorkSql(altxtMetaDto);
		merge2MainSql = altxt2DbLogic.makeMerge2MainSql(altxtMetaDto);
		deleteChildSqls = altxt2DbLogic.makeDeleteChildSql(altxtMetaDto);
        return true;
	}

    /**
     * ${inheritDoc}
     */
	@Override
	public boolean fini() {
	    // このバッチでは終了処理なし
		return true;
	}

    /**
     * ${inheritDoc}
     */
	@Override
	public boolean execute() {
		boolean result = true;

		// ①データファイル　→　一時テーブルへのマージ処理
		// 以下のデータ変換を実施する
		// 　(a)集合項目ルール　→　一般項目ルールへの変換
		// 　(b)同一キーによる複数レコードの集約
    	FileInputStream fis = null;
    	InputStreamReader in = null;
    	BufferedReader br = null;
	    try {
	    	fis = new FileInputStream(dataPath);
	    	in = new InputStreamReader(fis, MS932);
	    	br = new BufferedReader(in);

	        String line;
	        while ((line = br.readLine()) != null) {
	        	String[] vals = CsvParser.split(line);
	        	String[] mergeVals = altxt2DbLogic.createMergeVals(altxtMetaDto, vals);
	        	altxt2DbService.execSql(merge2WorkSql, mergeVals);
	        }
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	        result = false;
	    } catch (IOException e) {
	        e.printStackTrace();
	        result = false;
	    } finally {
	        try {
	            br.close();
	            in.close();
	            fis.close();
	        } catch (IOException e) {
	            e.printStackTrace();
		        result = false;
	        }
	    }
	    if (!result) {
	    	return result;
	    }

	    // ②一時テーブルから本テーブルへのマージ処理
	    // 一般項目のルールに従ってマージを実施する
    	altxt2DbService.execSql(merge2MainSql);

    	// ③子テーブルに対する削除区分更新処理
    	for(String deleteChildSql: deleteChildSqls) {
    		altxt2DbService.execSql(deleteChildSql);
    	}

		return true;
	}
}
