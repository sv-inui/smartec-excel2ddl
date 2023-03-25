package jp.co.softventure.smartec.excel2ddl;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * ファイル操作のユーティリティクラス
 * @author yukio
 *
 */

public class FileUtils {

    /**
     * クラスファイルまでの絶対パスを取得する
     * @param cls
     * @return
     * @throws URISyntaxException
     */
    public static Path getApplicationPath(Class<?> cls) throws URISyntaxException {
        ProtectionDomain pd = cls.getProtectionDomain();
        CodeSource cs = pd.getCodeSource();
        URL location = cs.getLocation();
        URI uri = location.toURI();
        Path path = Paths.get(uri);
        return path;
    }
}
