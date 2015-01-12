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

public class Altxt2DbAction extends AbstractAction implements SystemConstants {

	public Altxt2DbLogic altxt2DbLogic = SingletonS2Container.getComponent(Altxt2DbLogic.class);
	public Altxt2DbService altxt2DbService = SingletonS2Container.getComponent(Altxt2DbService.class);

	private AltxtMetaDto altxtMetaDto;
	private String dataPath;
	private String merge2WorkSql;
	private String merge2MainSql;
	private List<String> deleteChildSqls;

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
		//altxt2DbService.createWork(altxtMetaDto.table);
		merge2WorkSql = altxt2DbLogic.makeMerge2WorkSql(altxtMetaDto);
		merge2MainSql = altxt2DbLogic.makeMerge2MainSql(altxtMetaDto);
		deleteChildSqls = altxt2DbLogic.makeDeleteChildSql(altxtMetaDto);
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

    	altxt2DbService.execSql(merge2MainSql);

    	for(String deleteChildSql: deleteChildSqls) {
    		altxt2DbService.execSql(deleteChildSql);
    	}

		return true;
	}
}
