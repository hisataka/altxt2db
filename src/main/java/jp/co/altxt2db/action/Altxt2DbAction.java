package jp.co.altxt2db.action;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import jp.co.altxt2db.constants.SystemConstants;
import jp.co.altxt2db.dto.AltxtMetaDto;
import jp.co.altxt2db.logic.Altxt2DbLogic;
import jp.co.altxt2db.service.Altxt2DbService;
import jp.co.altxt2db.util.CsvParser;
import net.arnx.jsonic.JSON;
import net.arnx.jsonic.JSONException;

import org.seasar.framework.container.SingletonS2Container;

public class Altxt2DbAction extends AbstractAction implements SystemConstants {

	public Altxt2DbLogic altxt2DbLogic = SingletonS2Container.getComponent(Altxt2DbLogic.class);
	public Altxt2DbService altxt2DbService = SingletonS2Container.getComponent(Altxt2DbService.class);

	private AltxtMetaDto altxtMetaDto;
	private String dataPath;
	private String workTable;

	@Override
	public int setArgLength() {
		return 2;
	}

	@Override
	public boolean init() {
        dataPath = args[1];
		try {
			altxtMetaDto = JSON.decode(new FileReader(args[0]), AltxtMetaDto.class);
		} catch (JSONException | IOException e) {
			e.printStackTrace();
			return false;
		}
		workTable = altxt2DbService.createWork(altxtMetaDto.table);
        return true;
	}

	@Override
	public boolean fini() {
		return true;
	}

	@Override
	public boolean execute() {
		boolean result = true;

		// ファイル処理
	    FileReader fr = null;
	    BufferedReader br = null;
	    try {
	        fr = new FileReader(dataPath);
	        br = new BufferedReader(fr);

	        String line;
	        while ((line = br.readLine()) != null) {
	        	String[] cols = CsvParser.split(line);

	        	for(String col: cols) {
	        		System.out.print("「" + col + "」");
	        	}
	        	System.out.print("\r\n");
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
	            fr.close();
	        } catch (IOException e) {
	            e.printStackTrace();
		        result = false;
	        }
	    }
	    if (!result) {
	    	return result;
	    }

		// ②ファイルから一時テーブルへ更新内容を１行ずつマージし、レコードを集約
		// ②－１集合項目ルールを通常項目ルールへ卸す
		//  @,, → @,@,@
		//  a,,c → a,@,c
		//  ,, → ,,
		//  a,b,c → a,b,c
		//  要約すると、先頭に@があれば残りも@で補完
		//  すべて空ならそのまま補完なし
		//  ↑２つ以外の場合でいずれかの項目が空なら@で補完
		// ②－２修正区分を判定し削除である場合は、子供テーブルの本テーブルのレコードを削除

		// ③一時テーブルと本テーブルを結合しカラム更新ルールに従って更新セットを抽出
		//  @は空として抽出、空値は本テーブルから値を抽出

		// ④　③で生成した更新用データセットを本テーブルへマージ

//		List<HOGE> l = altxt2DbService.selecthoge();

	//	for (HOGE h: l) {
	//		System.out.println("col:" + h.col);
		//}

		// メタ情報をどう扱うか・・・
		// まずはjsonオブジェクトとして生成しなきゃ

		return true;
	}
}
