package club.projectGaia.readExcel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Properties;

/**
 * Created by luoxiaolong on 18-7-9.
 */
public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        File file = new File("./");
        try {
            File thisFile;
            //magic bug!
            if (file.getCanonicalPath().contains("bin")) {
                thisFile = file.getCanonicalFile();
            } else {
                thisFile = new File(file.getCanonicalPath() + "/bin");
            }
            //log.info(thisFile.getParentFile().getCanonicalPath() + File.separator + "config.txt");
            //Properties pro = getProperties(thisFile.getParentFile().getCanonicalPath() + File.separator + "config.txt");
            //log.info(thisFile.getCanonicalPath() + File.separator + "config.txt");
            Properties pro = getProperties(thisFile.getParentFile().getCanonicalPath() + File.separator + "config.txt");
            String excel = pro.getProperty("file");
            if (excel == null || "".equals(excel)) {
                log.info("请配置excel路径");
                System.exit(0);
            }
            File excelFile = new File(excel);
            if (!excelFile.exists() || !excelFile.isFile()) {
                log.info("请确认excel路径");
                System.exit(0);
            }
            cleanLogFile(new File(thisFile.getParentFile().getCanonicalPath() + "/log/output.log"));

            //--正式开始
            int lastRow = -1;
            log.info("excel路径{}", excelFile.getCanonicalPath());
            while (true) {
                try (Workbook workbook = WorkbookFactory.create(excelFile);) {
                    Sheet sheet = workbook.getSheetAt(0);
                    if (lastRow == -1 && lastRow != sheet.getLastRowNum()) {
                        lastRow = sheet.getLastRowNum();
                        Row row = sheet.getRow(lastRow);
                        Iterator<Cell> it = row.cellIterator();
                        StringBuilder builder = new StringBuilder();
                        while (it.hasNext()) {
                            Cell c = it.next();
                            switch (c.getCellType()) {
                                case Cell.CELL_TYPE_STRING:
                                    builder.append(c.getStringCellValue()).append("|");
                                    break;
                                case Cell.CELL_TYPE_NUMERIC:
                                    builder.append(new Double(c.getNumericCellValue())).append("|");
                                    break;
                                default:
                                    builder.append("未识别").append("|");
                            }
                        }
                        log.info(builder.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            System.exit(0);
        }
    }

    public static Properties getProperties(String file) {
        Properties pro = new Properties();
        try (
                InputStreamReader in = new InputStreamReader(new FileInputStream(file), "UTF-8")
        ) {
            pro.load(in);
            in.close();
        } catch (Exception E) {

        }
        return pro;
    }

    private static void cleanLogFile(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
