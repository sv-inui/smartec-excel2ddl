package jp.co.softventure.smartec.excel2ddl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;

public class ExcelParser {

    static Logger logger = LogManager.getLogger(ExcelParser.class);

    private Workbook workbook;

    /** スキーマ定義シート番号 */
    private int SCHEMA_SHEET_IDX=4;
    /** スキーマ定義 スキーマ名列位置 */
    private int CIDX_SCM_NAME=3;
    /** スキーマ定義 オーナー名列位置 */
    private int CIDX_SCM_OWN=4;
    /** テーブル定義書からスキーマ定義の開始を特定するための文字列 */
    private String FINDKEY_SCM_START="スキーマ名";
    /** テーブル定義書からスキーマ定義の開始を特定するための文字列 */
    private int CIDX_FINDKEY_SCM_START=3;

    /** テーブル定義読み込み開始シート番号 */
    private int IDX_FIRST_TABLESHEET=7;
    /** テーブル定義書 テーブル物理名の位置 */
    private JSONArray IDX_TABLENAME_P;
    /** テーブル定義書 テーブル論理名の列位置 */
    private JSONArray IDX_TABLENAME_L;

    /** テーブル定義書 カラム論理名の列位置 */
    private int CIDX_COLUMNNAME_L=2;
    /** テーブル定義書 カラム物理名の列位置 */
    private int CIDX_COLUMNNAME_P=3;
    /** テーブル定義書 カラムデータタイプの列位置 */
    private int CIDX_DATATYPE=4;
    /** テーブル定義書 カラムサイズの列位置 */
    private int CIDX_SIZE=5;
    /** テーブル定義書 カラム精度の列位置 */
    private int CIDX_PRECISION=6;
    /** テーブル定義書 カラムスケールの列位置 */
    private int CIDX_SCALE=7;
    /** テーブル定義書 カラムデフォルト値の列位置 */
    private int CIDX_DEFAULT=-8;
    /** テーブル定義書 カラムNOT NULLの列位置 */
    private int CIDX_NULLABLE=9;
    /** テーブル定義書 NOT NULLの判定文字列 */
    private String NULLABLE_FALSE="〇";

    /** テーブル定義書からテーブル定義開始位置を特定するための文字列 */
    private String FINDKEY_TABLE="No.";
    /** テーブル定義書からシーケンス定義開始位置を特定するための文字列 */
    private String FINDKEY_SEQUENCE="シーケンス名";
    /** テーブル定義書から定義開始位置を特定するための文字列の列位置 */
    private int CIDX_FINDKEY_TABLE=1;

    /** テーブル定義書からALTER文の開始を特定するための文字列 */
    private String FINDKEY_ALTER_START="PK";
    /** テーブル定義書からALTER文の開始を特定するための文字列の列位置 */
    private int CIDX_FINDKEY_ALTER_START=1;
    /** テーブル定義書 ALTER文の列位置 */
    private int CIDX_ALTER=24;
    /** テーブル定義書からALTER文の終了を特定するための文字列 */
    private String FINDKEY_ALTER_END="チェック条件";
    /** テーブル定義書からALTER文を終了を特定するための文字列の列位置 */
    private int CIDX_FINDKEY_ALTER_END=1;


	private static final String TAB = "    ";
	private static final String SS = " ";

	private String encoding;
	private String lineSeparator;

	public void init(String encoding, String lineSeparator) {
		SCHEMA_SHEET_IDX = Integer.parseInt(PropertiesUtils.getProperty("SCHEMA_SHEET_IDX"));
		CIDX_SCM_NAME = Integer.parseInt(PropertiesUtils.getProperty("CIDX_SCM_NAME"));
		CIDX_SCM_OWN = Integer.parseInt(PropertiesUtils.getProperty("CIDX_SCM_OWN"));
		FINDKEY_SCM_START = PropertiesUtils.getProperty("FINDKEY_SCM_START");
		CIDX_FINDKEY_SCM_START = Integer.parseInt(PropertiesUtils.getProperty("CIDX_FINDKEY_SCM_START"));
		IDX_FIRST_TABLESHEET = Integer.parseInt(PropertiesUtils.getProperty("IDX_FIRST_TABLESHEET"));
		IDX_TABLENAME_P = new JSONArray(PropertiesUtils.getProperty("IDX_TABLENAME_P"));
		IDX_TABLENAME_L = new JSONArray(PropertiesUtils.getProperty("IDX_TABLENAME_L"));
		CIDX_COLUMNNAME_L = Integer.parseInt(PropertiesUtils.getProperty("CIDX_COLUMNNAME_L"));
		CIDX_COLUMNNAME_P = Integer.parseInt(PropertiesUtils.getProperty("CIDX_COLUMNNAME_P"));
		CIDX_DATATYPE = Integer.parseInt(PropertiesUtils.getProperty("CIDX_DATATYPE"));
		CIDX_SIZE = Integer.parseInt(PropertiesUtils.getProperty("CIDX_SIZE"));
		CIDX_PRECISION = Integer.parseInt(PropertiesUtils.getProperty("CIDX_PRECISION"));
		CIDX_SCALE = Integer.parseInt(PropertiesUtils.getProperty("CIDX_SCALE"));
		CIDX_DEFAULT = Integer.parseInt(PropertiesUtils.getProperty("CIDX_DEFAULT"));
		CIDX_NULLABLE = Integer.parseInt(PropertiesUtils.getProperty("CIDX_NULLABLE"));
		NULLABLE_FALSE = PropertiesUtils.getProperty("NULLABLE_FALSE");
		FINDKEY_TABLE = PropertiesUtils.getProperty("FINDKEY_TABLE");
		FINDKEY_SEQUENCE = PropertiesUtils.getProperty("FINDKEY_SEQUENCE");
		CIDX_FINDKEY_TABLE = Integer.parseInt(PropertiesUtils.getProperty("CIDX_FINDKEY_TABLE"));
		FINDKEY_ALTER_START = PropertiesUtils.getProperty("FINDKEY_ALTER_START");
		CIDX_FINDKEY_ALTER_START = Integer.parseInt(PropertiesUtils.getProperty("CIDX_FINDKEY_ALTER_START"));
		CIDX_ALTER = Integer.parseInt(PropertiesUtils.getProperty("CIDX_ALTER"));
		FINDKEY_ALTER_END = PropertiesUtils.getProperty("FINDKEY_ALTER_END");
		CIDX_FINDKEY_ALTER_END = Integer.parseInt(PropertiesUtils.getProperty("CIDX_FINDKEY_ALTER_END"));

		this.encoding = encoding;
		this.lineSeparator = lineSeparator;
	}

	public void execute(File excelFile, File outputFile) {
		try{
			// Excelブックを開く
			workbook = WorkbookFactory.create(excelFile);

			// ファイル出力用のバッファ
			StringBuffer outBuf = new StringBuffer();

			// 全シートを順番に処理
			Iterator<Sheet> sheets = workbook.sheetIterator();
			while(sheets.hasNext()) {
				// シートを取得
				Sheet sheet = sheets.next();

				// テーブル定義開始行番号のリスト
				List<Integer> tableIndexList = new ArrayList<Integer>();

				// シーケンス定義開始行番号のリスト
				List<Integer> sequenceIndexList = new ArrayList<Integer>();

				// 全行を順番に処理
				Iterator<Row> rows = sheet.rowIterator();
				while(rows.hasNext()) {
					// 行の取得
					Row row = rows.next();

					// テーブル名のセルを取得
					Cell cell = row.getCell(CIDX_FINDKEY);
					// セル内の値の取得
					String cellValue = getCellValueString(cell, true);

					// 空白セルはNULLになるので飛ばす
					if(StringUtils.isEmpty(cellValue)) {
						continue;
					}

					// セルの値が「テーブル名」だったらその行から定義が記載されている
					if(cellValue.equals(FINDKEY_TABLE)) {
						tableIndexList.add(cell.getRowIndex());
					}
					// セルの値が「シーケンス名」だったらその行から定義が記載されている
					if(cellValue.equals(FINDKEY_SEQUENCE)) {
						sequenceIndexList.add(cell.getRowIndex());
					}
				}

				// シート単位でDDLとコメントを生成
				StringBuffer tempBuf = new StringBuffer();
				for(Integer rowIndex : tableIndexList) {
					tempBuf.append(createTableSQL(sheet, rowIndex));
					tempBuf.append(lineSeparator);
				}
				for(Integer rowIndex : sequenceIndexList) {
					tempBuf.append(createSequenceSQL(sheet, rowIndex));
					tempBuf.append(lineSeparator);
				}

				outBuf.append(tempBuf);
			}

			// ファイル出力
			if(outputFile != null) {
				try {
					// 文字コードを指定する
					PrintWriter p_writer = new PrintWriter(new BufferedWriter
							(new OutputStreamWriter(new FileOutputStream(outputFile),encoding)));

					//ファイルに文字列を書き込む
					p_writer.print(outBuf);
					//ファイルをクローズする
					p_writer.close();
				} catch (UnsupportedEncodingException | FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}catch(IOException  e) {
			e.printStackTrace();
		}

	}

	/**
	 * テーブルDDL生成
	 * @param sheet
	 * @param rowIndex
	 * @return
	 */
	private String createTableSQL(Sheet sheet, int rowIndex) {
		  boolean done = false;
		  StringBuffer outBuf = new StringBuffer();
		  StringBuffer ddlBuf = new StringBuffer();
		  StringBuffer commentBuf = new StringBuffer();


		  int index = 0;
		  String tableNameP = "";
		  String tableNameL = "";
		  List<String> keyList = new ArrayList<String>();
		  Set<String> columnNameSet = new HashSet<String>();
		  while(!done) {
			  if(index == 0) {
				  tableNameP = getCellValueString(sheet.getRow(rowIndex).getCell(CIDX_TABLENAME_P),true);
				  tableNameL = getCellValueString(sheet.getRow(rowIndex).getCell(CIDX_TABLENAME_L),true);

				  // DDL
				  ddlBuf.append("-- ");
				  ddlBuf.append(tableNameL);
				  ddlBuf.append(lineSeparator);
				  ddlBuf.append("DROP TABLE IF EXISTS ");
				  ddlBuf.append(tableNameP);
				  ddlBuf.append(";");
				  ddlBuf.append(lineSeparator);
				  ddlBuf.append("CREATE TABLE ");
				  ddlBuf.append(tableNameP);
				  ddlBuf.append(" (");
				  ddlBuf.append(lineSeparator);

				  // テーブルコメント
				  commentBuf.append("COMMENT ON TABLE ");
				  commentBuf.append(tableNameP);
				  commentBuf.append(" IS '");
				  commentBuf.append(tableNameL);
				  commentBuf.append("';");
				  commentBuf.append(lineSeparator);
			  }
			  // 2行目は飛ばす
			  else if(index == 1) {
				  rowIndex++;
				  index++;
				  continue;
			  }else {
				  Cell cell = null;
				  try {
					  cell = sheet.getRow(rowIndex).getCell(CIDX_COLUMNNAME_P);
				  }catch(NullPointerException e) {
					  done = true;
					  break;
				  }

				  String columnNameP = getCellValueString(cell,true);
				  // 空白だったら定義終了
				  if(StringUtils.isEmpty(columnNameP)) {
					  done = true;
					  break;
				  }else {
					  // カラム名重複チェック
					  if(columnNameSet.contains(columnNameP.toUpperCase())) {
						  logger.error(String.format("%sシート:%s %s カラム名が重複しています:%s", sheet.getSheetName(), tableNameL, tableNameP, columnNameP));
						  done = true;
						  break;
					  }
					  columnNameSet.add(columnNameP.toUpperCase());
					  // 必要なセルの値を取得
					  String columnNameL = getCellValueString(sheet.getRow(rowIndex).getCell(CIDX_COLUMNNAME_L),true);
					  String dataType = getCellValueString(sheet.getRow(rowIndex).getCell(CIDX_DATATYPE),true);
					  String size = getCellValueString(sheet.getRow(rowIndex).getCell(CIDX_SIZE),true);
					  String nullable = getCellValueString(sheet.getRow(rowIndex).getCell(CIDX_NULLABLE),true);
					  String key = getCellValueString(sheet.getRow(rowIndex).getCell(CIDX_KEY),true);
					  String defaultVal = "";
					  if(CIDX_DEFAULT > 0) {
						  defaultVal = getCellValueString(sheet.getRow(rowIndex).getCell(CIDX_DEFAULT),true);
					  }
					  ddlBuf.append(TAB);

					  // カラム名
					  ddlBuf.append(columnNameP);
					  ddlBuf.append(SS);

					  // データ型
					  ddlBuf.append(dataType);

					  // サイズ指定
					  if(!StringUtils.isEmpty(size)) {
						  ddlBuf.append("(");
						  ddlBuf.append(size);
						  ddlBuf.append(")");
					  }
					  ddlBuf.append(SS);

					  // NOT NULL
					  if(!StringUtils.isEmpty(nullable) && nullable.toUpperCase().equals(NULLABLE_FALSE)) {
						  ddlBuf.append(" NOT NULL ");
					  }

					  // デフォルト値
					  if(!StringUtils.isEmpty(defaultVal)) {
						  ddlBuf.append(" DEFAULT ");
						  if(Constants.COLUMNTYPE_NEEDSQ.contains(dataType.toLowerCase())) {
							  if(Constants.NEEDSQ_EXPECT.contains(defaultVal.toLowerCase())) {
								  ddlBuf.append(defaultVal);
							  }else {
								  ddlBuf.append("'");
								  ddlBuf.append(defaultVal);
								  ddlBuf.append("'");
							  }

						  }else {
							  ddlBuf.append(defaultVal);
						  }
						  ddlBuf.append(SS);
					  }
					  ddlBuf.append(",");
					  ddlBuf.append(lineSeparator);
					  // プライマリーキーは一旦保存
					  if(!StringUtils.isEmpty(key) && key.equals(PKEY_TRUE)) {
						  keyList.add(columnNameP);
					  }

					  // カラムコメント
					  commentBuf.append("COMMENT ON COLUMN ");
					  commentBuf.append(tableNameP);
					  commentBuf.append(".");
					  commentBuf.append(columnNameP);
					  commentBuf.append(" IS '");
					  commentBuf.append(columnNameL);
					  commentBuf.append("';");
					  commentBuf.append(lineSeparator);

				  }
			  }
			  rowIndex++;
			  index++;
		  }

		  // プライマリーキーの定義出力
		  if(!keyList.isEmpty()) {
			  ddlBuf.append(TAB);
			  ddlBuf.append("PRIMARY KEY (");
			  ddlBuf.append(String.join(",", keyList));
			  ddlBuf.append(")");
			  ddlBuf.append(lineSeparator);
		  }else {
			  ddlBuf.setLength(ddlBuf.length() -lineSeparator.length() -1);
			  ddlBuf.append(lineSeparator);
		  }
		  ddlBuf.append(");");
		  ddlBuf.append(lineSeparator);

		  outBuf.append(ddlBuf);
		  outBuf.append(commentBuf);
		  return outBuf.toString();
	}


	/**
	 * シーケンスDDL生成
	 * @param sheet
	 * @param rowIndex
	 * @return
	 */
	private String createSequenceSQL(Sheet sheet, int rowIndex) {
		  boolean done = false;
		  StringBuffer outBuf = new StringBuffer();
		  StringBuffer ddlBuf = new StringBuffer();
		  StringBuffer commentBuf = new StringBuffer();


		  int index = 0;
		  String sequenceNameP = "";
		  String sequenceNameL = "";
		  while(!done) {
			  // 1行目と2行目は飛ばす
			  if(index < 2) {
				  rowIndex++;
				  index++;
				  continue;
			  }

			  // 空行に到達したら終了
			  try {
				  Cell cell = null;
				  cell = sheet.getRow(rowIndex).getCell(CIDX_COLUMNNAME_P);
			  }catch(NullPointerException e) {
				  done = true;
				  break;
			  }

			  // 物理名と論理名の取得
			  sequenceNameP = getCellValueString(sheet.getRow(rowIndex).getCell(CIDX_COLUMNNAME_P),true);
			  sequenceNameL = getCellValueString(sheet.getRow(rowIndex).getCell(CIDX_COLUMNNAME_L),true);

			  // DDL
			  ddlBuf.append("-- ");
			  ddlBuf.append(sequenceNameL);
			  ddlBuf.append(lineSeparator);
			  ddlBuf.append("DROP SEQUENCE IF EXISTS ");
			  ddlBuf.append(sequenceNameP);
			  ddlBuf.append(";");
			  ddlBuf.append(lineSeparator);
			  ddlBuf.append("CREATE SEQUENCE ");
			  ddlBuf.append(sequenceNameP);
			  ddlBuf.append(" ;");
			  ddlBuf.append(lineSeparator);

			  // テーブルコメント
			  commentBuf.append("COMMENT ON SEQUENCE ");
			  commentBuf.append(sequenceNameP);
			  commentBuf.append(" IS '");
			  commentBuf.append(sequenceNameL);
			  commentBuf.append("';");
			  commentBuf.append(lineSeparator);

			  rowIndex++;
			  index++;
		  }

		  outBuf.append(ddlBuf);
		  outBuf.append(commentBuf);
		  return outBuf.toString();
	}

	/**
	 * セルの値を取得する。
	 * @param cell セル
	 * @param flag フラグ
	 * @return セルの値
	 */
	private Object getCellValue(Cell cell, boolean flag) {
		if(cell == null) {
			return null;
		}
		switch (cell.getCellType()) {
			case STRING:
				// 文字列
				return cell.getRichStringCellValue().getString();
			case NUMERIC:
				if(DateUtil.isCellDateFormatted(cell)) {
					// 日付
					return cell.getDateCellValue();
				}
				// 数値
				return cell.getNumericCellValue();
			case FORMULA:
				// 数式
				if (!flag) {
					return cell.getCellFormula();
				}
				// 数式の結果を返す
				return getCellValue(workbook.getCreationHelper().createFormulaEvaluator().evaluateInCell(cell),true);
			case BOOLEAN:
				return cell.getBooleanCellValue();
			default :
				return null;
		}
	}

	private String getCellValueString(Cell cell, boolean flag){
		if(cell == null) {
			return null;
		}
		Object cellValue = getCellValue(cell, flag);

		if(cellValue == null) {
			return null;
		}

		String cellValueStr = "";
		if(cellValue instanceof Double) {
			// 今回はデータサイズでしか数値がない想定なので小数部は切り捨て
			int d = (int)Math.round((Double)cellValue);
			cellValueStr = String.valueOf(d);
		}else {
			// 改行されているかもしれないので改行文字を除去
			cellValueStr = cellValue.toString().replaceAll("\r", "").replace("\n", "");
		}
		return cellValueStr;
	}

}
