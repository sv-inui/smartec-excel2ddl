package jp.co.softventure.smartec.excel2ddl;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * プロパティファイルのユーティリティクラス
 * @author yukio
 *
 */
public class PropertiesUtils {
    static Logger logger = LogManager.getLogger(PropertiesUtils.class);
    private static final String INIT_FILE_PATH = "settings.properties";
    private static Properties properties;

    private PropertiesUtils() throws Exception {
    }

    /**
     * プロパティファイルのロード
     * @param path
     */
    public static void loadProperties(String path) {
        properties = new Properties();
        // -pで指定がなかった場合は、jar内部のプロパティファイルをロード
        if(StringUtils.isEmpty(path)) {
            try {
                properties.load(Files.newBufferedReader(Paths.get(FileUtils.getApplicationPath(Main.class) + File.separator +INIT_FILE_PATH), StandardCharsets.UTF_8));
            } catch (URISyntaxException | IOException e) {
                // ファイル読み込みに失敗
                logger.error(String.format("ファイルの読み込みに失敗しました。ファイル名:%s", INIT_FILE_PATH),e);
            }
        }else {
            // -pで指定されたプロパティファイルをロード
            // 絶対パス指定の場合。パスそのままでロード
            if(Paths.get(path).isAbsolute()) {
                try {
                    properties.load(Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    // ファイル読み込みに失敗
                    logger.error(String.format("ファイルの読み込みに失敗しました。ファイル名:%s", path));
                }
            }else {
                // 相対パス指定の場合。jarを実行したディレクトリまでの絶対パス＋指定のパスでロード
                try {
                    path = System.getProperty("user.dir") + File.separator + path;
                    properties.load(Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8));
                } catch (IOException e) {
                    // ファイル読み込みに失敗
                    logger.error(String.format("ファイルの読み込みに失敗しました。ファイル名:%s", path));
                }
            }
        }
    }

    /**
     * プロパティ値を取得する
     *
     * @param key キー
     * @return 値
     */
    public static String getProperty(final String key) {
        return getProperty(key, "");
    }

    /**
     * プロパティ値を取得する
     *
     * @param key キー
     * @param defaultValue デフォルト値
     * @return キーが存在しない場合、デフォルト値
     *          存在する場合、値
     */
    public static String getProperty(final String key, final String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * 値がカンマ区切りのプロパティ値をListで返す。
     * @param key
     * @return
     */
    public static List<String> getStrListProperty(final String key) {
        List<String> retVal = new ArrayList<String>();
       if(StringUtils.isEmpty(getProperty(key))){
           return retVal;
       }
        String [] prop = getProperty(key).split(",");
        for(int i = 0; i < prop.length; i++) {
            String item = prop[i].trim();
            retVal.add(item);
        }
        return retVal;
    }

    /**
     * 値がカンマ区切りのプロパティ値をListで返す。ワイルドカードは正規表現に置換
     * @param key
     * @return
     */
    public static List<String> getStrListPropertyRegix(final String key) {
        List<String> retVal = new ArrayList<String>();
       if(StringUtils.isEmpty(getProperty(key))){
           return retVal;
       }
        String [] prop = getProperty(key).split(",");
        for(int i = 0; i < prop.length; i++) {
            String item = prop[i].trim();

            // ワイルカードは正規表現に直す
            item = item.replace("*", ".*"); // 任意の文字列
            item = item.replace("?", ".");  // 任意の1文字

            retVal.add(item);
        }
        return retVal;
    }

    /**
     * 値がカンマ区切りのプロパティ値をListで返す。ワイルドカードはSQL LIKE句のワイルドカードに置換
     * @param key
     * @return
     */
    public static List<String> getStrListPropertyLike(final String key) {
        List<String> retVal = new ArrayList<String>();
       if(StringUtils.isEmpty(getProperty(key))){
           return retVal;
       }
        String [] prop = getProperty(key).split(",");
        for(int i = 0; i < prop.length; i++) {
            String item = prop[i].trim();

            // ワイルカードはLIKEのワイルカード文字に直す
            item = item.replace("_", "\\_"); // _はLIKEのワイルドカード文字なのでエスケープ
            item = item.replace("*", "%");   // 任意の文字列
            item = item.replace("?", "_");   // 任意の1文字

            retVal.add(item);
        }
        return retVal;
    }
}
