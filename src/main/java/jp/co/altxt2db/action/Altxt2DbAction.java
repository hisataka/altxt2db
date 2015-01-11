package jp.co.altxt2db.action;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
    	FileInputStream fis = null;
    	InputStreamReader in = null;
    	BufferedReader br = null;
	    try {
	    	fis = new FileInputStream(dataPath);
	    	in = new InputStreamReader(fis,"MS932");
	    	br = new BufferedReader(in);

	        String line;
	        while ((line = br.readLine()) != null) {
	        	String[] vals = CsvParser.split(line);
	        	altxt2DbLogic.setColValue(altxtMetaDto, vals);
	        	String sql = altxt2DbLogic.makeUpdateSql(altxtMetaDto);
	        	//altxt2DbService.execSql(sql);

	        	/*
	        	 * アップデート用セット抽出クエリ
	        	select

	        case work.lkb when '@' then '' when '' then child.lkb else work.lkb end
	        , case work.k_rid when '@' then '' when '' then child.k_rid else work.k_rid end
	        , case work.k_kcd when '@' then '' when '' then child.k_kcd else work.k_kcd end
	        , case work.k_bk when '@' then '' when '' then child.k_bk else work.k_bk end
	        , case work.ymd when '@' then '' when '' then child.ymd else work.ymd end
	        , case work.mkb when '@' then '' when '' then child.mkb else work.mkb end
	        , case work.s_rid when '@' then '' when '' then child.s_rid else work.s_rid end
	        , case work.s_kcd when '@' then '' when '' then child.s_kcd else work.s_kcd end
	        , case work.s_bk when '@' then '' when '' then child.s_bk else work.s_bk end
	        , case work.nd1 when '@' then '' when '' then child.nd1 else work.nd1 end
	        , case work.nd2 when '@' then '' when '' then child.nd2 else work.nd2 end
	        , case work.ad_cd when '@' then '' when '' then child.ad_cd else work.ad_cd end
	        , case work.ad_nm when '@' then '' when '' then child.ad_nm else work.ad_nm end
	    from _____ALTXT2DB_CHILD WORK inner join child
	     on work.K_RID = child.k_rid and work.k_kcd = child.k_kcd and work.k_bk = child.k_bk and work.S_RID = child.s_rid and work.s_kcd = child.s_kcd and work.s_bk = child.s_bk;

	    select * from child;
	    select * from _____ALTXT2DB_CHILD ;
*/

	        	/* 子供削除
	        	delete from child where
	        	 exists ( select *  from _____ALTXT2DB_CHILD B where mkb = 'C'
	        	        and B.K_RID = child.K_RID and B.K_KCD = child.K_KCD and B.K_BK = child.K_BK and B.S_RID = child.S_RID and B.S_KCD = child.S_KCD and B.S_BK = child.S_BK);
*/
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
		return true;
	}
}
