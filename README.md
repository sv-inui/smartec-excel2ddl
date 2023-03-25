# smartec-Excel2DDL
スマート避難所システムのテーブル定義書Excelファイルから、DDLとテーブル・カラムコメント文を抽出するツール。

### 実行方法
- Eclipseから実行する場合は、実行＞実行構成＞Javaアプリケーションを新規作成
    - プロジェクト ExcelTOSQL
    - メインクラス jp.co.softventure.inui.ExcelTOSQL.Main
    - プログラムの引数 [テーブル定義書Excelファイルへのパス] -o [SQL出力ファイルパス] -p src/main/resources.settings.properties
        - 上記引数の場合、src/main/resouces/settings.propertiesを使用します
- jarファイルの実行
    - java -jar ExcelToSQL-0.0.1-SNAPSHOT-all.jar [テーブル定義書Excelファイルへのパス] -o [SQL出力ファイルパス] -p [settings.propertiesへのパス]

### Jarファイルビルド方法
- コマンドプロンプトで gradlew.bat shadowjar
- build/libs/ExcelToSQL-0.0.1-SNAPSHOT-all.jarが実行可能なjarファイルです。


### プロパティファイル記載事項
- -pで指定するファイル
- UTF-8で記載すること
    - CIDX_TABLENAME_P=2
        - テーブル定義書 テーブル物理名の列位置。０開始。

    - CIDX_TABLENAME_L=3
        - テーブル定義書 テーブル論理名の列位置。０開始。

    - CIDX_COLUMNNAME_P=1
        - テーブル定義書 カラム物理名の列位置。０開始。

    - CIDX_COLUMNNAME_L=2
        - テーブル定義書 カラム論理名の列位置。０開始。

    - CIDX_DATATYPE=3
        - テーブル定義書 カラムデータタイプの列位置。０開始。

    - CIDX_SIZE=4
        - テーブル定義書 カラムサイズの列位置。０開始。

    - CIDX_NULLABLE=5
        - テーブル定義書 カラムnullableの列位置。０開始。
    - CIDX_KEY=6
        - テーブル定義書 カラムKeyの列位置。０開始。
    - CIDX_DEFAULT=-1
        - テーブル定義書 カラムデフォルト値の列位置。０開始。-1を指定した場合は無視される。

    - FINDKEY=テーブル名
        - テーブル定義書から定義開始位置を特定するための文字列。０開始。

    - CIDX_FINDKEY=1
        - FINDKEYで指定した文字列の列位置。０開始。

    - NULLABLE_TRUE=YES
        - テーブル定義書 nullable = trueの判定文字列

    - PKEY_TRUE=〇
        - テーブル定義書 primaryKey = trueの判定文字列

### オプション
- -p プロパティファイルを指定
- -o 指定した場合、指定したファイルパスにSQLを出力します
- -h ヘルプを表示して終了します
- -l -oで出力するファイルの文字コードをUTF-8、改行文字をLFにします
- -w -oで出力するファイルの文字コードをShiftJIS(MS932)、改行文字をCRLFにします
- -l,-w を省略した場合の動作は-lと同様です。-l,-w両方を指定した場合の動作も-lと同様です。
