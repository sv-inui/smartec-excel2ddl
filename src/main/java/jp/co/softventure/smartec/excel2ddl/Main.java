package jp.co.softventure.smartec.excel2ddl;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Main {
    static Logger logger = LogManager.getLogger(Main.class);

	public static void main( String [] args) throws CmdLineException{
		ArgBean argBean = new ArgBean();

        // 引数解析
        CmdLineParser parser = new CmdLineParser(argBean);
        boolean isOk = true;
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            // 必須オプションの指定漏れがあったらここに飛ぶ
            logger.error(e);
            isOk = false;
        }

        // -hがあった場合はusageを表示して終了
        if (argBean.isUsageFlag()) {
            System.out.println("Usage:");
            System.out.println(" ExcelToSQL [options]");
            System.out.println();
            System.out.println("Options:");
            parser.printUsage(System.out);
            return;
        }

        if(!isOk) {
            return;
        }

        String encoding = "";
        String lineSeparator = "";
        // -w -l どっちもつけていた場合は-lを優先
        if(argBean.isLinuxFlag() && argBean.isWindowsFlag()) {
        	encoding = "UTF-8";
        	lineSeparator = "\n";
        }else if(argBean.isLinuxFlag()) {
        	encoding = "UTF-8";
        	lineSeparator = "\n";
        }else if(argBean.isWindowsFlag()) {
        	encoding = "MS932";
        	lineSeparator = "\r\n";
        }else {
        	// 未指定の場合は-l
        	encoding = "UTF-8";
        	lineSeparator = "\n";
        }

        // プロパティファイルのロード
        PropertiesUtils.loadProperties(argBean.getPropPath());

        String excelPath = argBean.getExcelPath();

        File excelFile = new File(excelPath);
        if(!excelFile.exists()) {
        	logger.error(String.format("%s not found.", excelPath));
        }else {
            File outputFile = null;
            if(!StringUtils.isEmpty(argBean.getOutputFile())) {
            	outputFile = new File(argBean.getOutputFile());
            }
        	ExcelParser excelParser = new ExcelParser();
        	excelParser.init(encoding, lineSeparator);
        	excelParser.execute(excelFile, outputFile);
        }
	}
}
