altxt2db
=======

ある特殊なフォーマットのテキストファイルをデータベースへマージする

## ルール

・区切り文字は「,」  
・必要に応じて引用符「"」でデータを括る  
・引用符をデータとして利用する際には「""」とする  
・同一キーの複数レコードを同一ファイルに含めることが出来る  
　その際の更新は、行頭から順次適用する  
・一般項目の「＠」もしくは「@」は、該当カラムをクリア更新することを意味する  
・一般項目の空値は該当カラムを更新しないことを意味する  
・集合として扱う項目群はその構成要素の一部の項目が空値である際には、  
　該当カラムをクリア更新することを意味する  
・集合として扱う項目群はその構成要素の先頭項目が「＠」もしくは「@」である際には、 
　構成要素の全ての項目をクリア更新することを意味する  
・集合項目として扱う項目群の全ての項目が空値である際には、  
　その項目群を更新しないことを意味する  

## メタ情報ファイル

・どのようなデータファイルをどのテーブルにマージするかをメタ情報ファイルにて定義する  

    {
        "table": "parent"
        , "mod_colname": "MODIFY_KB"
        , "mod_delval": "C"
    	, "keys": [
    		"K_RID"
    		, "K_KCD"
    		, "K_BK"
    	]
    	, "coldef":  [
    		{
    			"name": "LKB"
    		}
    		, {
    			"name": "K_RID"
    			, "aggregate": "true"
    		}
    		, {
    			"name": "K_KCD"
    			, "aggregate": "true"
    		}
    		, {
    			"name": "K_BK"
    			, "aggregate": "true"
    		}
    		, {
    			"name": "YMD"
    		}
    		, {
    			"name": "MODIFY_KB"
    		}
    		, {
    			"name": "ND1"
    		}
    		, {
    			"name": "ND2"
    		}
    		, {
    			"name": "AD_CD"
    			, "aggregate": "true"
    		}
    		, {
    			"name": "AD_NM"
    			, "aggregate": "true"
    		}
    	]
    	, "children": [
    		{
    			"table": "child"
    			"keymap": [
    				{
    					"parent": "K_RID"
    					, "child": "K_RID"
    				}
    				, {
    					"parent": "K_KCD"
    					, "child": "K_KCD"
    				}
    				, {
    					"parent": "K_BK"
    					, "child": "K_BK"
    				}
    			]
    		}
    	]
    }

## 実行方法

    java -jar altxt2db jp.co.altxt2db.action.Altxt2DbAction <メタ情報ファイルパス> <データファイルパス>


## ライセンス

Apache License 2.0 (c) 2015 hisataka
