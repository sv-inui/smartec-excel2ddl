package jp.co.softventure.smartec.excel2ddl;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class ArgBean {

	/** テーブル定義Excelのパス */
	@Argument(index = 0, metaVar = "Excel file path", required = true, usage = "Excel file path")
	private String excelPath;

	/** プロパティファイルの指定 */
    @Option(name="-p", aliases="--prop", required = false, metaVar = "FILEPATH", usage="property file path")
    private String propPath;

    /** 出力ファイルパスの指定*/
    @Option(name="-o",metaVar = "Output file path", required = false, usage = "Output file path")
	private String outputFile;

    @Option(name="-h", aliases="--help", usage="print usage message and exit")
    private boolean usageFlag;

    @Option(name="-w", aliases="--windows", required = false, usage="Encoding SJIS, Line separator CRLF")
    private boolean windowsFlag;

    @Option(name="-l", aliases="--linux", required = false, usage="Encoding UTF-8, Line separator LF")
    private boolean linuxFlag;

    public String getExcelPath() {
		return excelPath;
	}

	public void setExcelPath(String excelPath) {
		this.excelPath = excelPath;
	}

	public String getPropPath() {
		return propPath;
	}

	public void setPropPath(String propPath) {
		this.propPath = propPath;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

	public boolean isUsageFlag() {
		return usageFlag;
	}

	public void setUsageFlag(boolean usageFlag) {
		this.usageFlag = usageFlag;
	}

	public boolean isWindowsFlag() {
		return windowsFlag;
	}

	public void setWindowsFlag(boolean windowsFlag) {
		this.windowsFlag = windowsFlag;
	}

	public boolean isLinuxFlag() {
		return linuxFlag;
	}

	public void setLinuxFlag(boolean linuxFlag) {
		this.linuxFlag = linuxFlag;
	}
}
