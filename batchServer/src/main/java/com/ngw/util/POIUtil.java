package com.ngw.util;

import com.google.common.collect.Lists;
import jodd.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * POI工具类
 */
public class POIUtil {

    // 扩展名
    public final static String XLS = "xls";
    public final static String XLSX = "xlsx";


    /**
     * * 读取excel文件
     *
     * @param excelFile excel文件
     * @param startRow  读取数据的起始行, 行号从0开始
     * @return
     * @throws IOException
     */
    public static List<String[]> readExcelFile(MultipartFile excelFile, int startRow) throws IOException {
        // 检查文件
        checkFile(excelFile);
        // 获得工作簿对象
        Workbook workbook = getWorkBook(excelFile);
        // 创建返回对象，把每行中的值作为一个数组，所有的行作为一个集合返回
        List<String[]> list = new ArrayList<>();
        if (workbook != null) {
            for (int sheetNum = 0; sheetNum < workbook.getNumberOfSheets(); sheetNum++) {
                // 获取当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if (sheet == null) {
                    continue;
                }
                // 获得当前sheet的结束行
                int lastRowNum = sheet.getLastRowNum();
                if (startRow < 0 || startRow > lastRowNum) {
                    throw new RuntimeException("wrong startRow");
                }
                // 循环除了第一行之外的所有行
                for (int rowNum = startRow; rowNum <= lastRowNum; rowNum++) {
                    // 获得当前行
                    Row row = sheet.getRow(rowNum);
                    if (row == null) {
                        continue;
                    }
                    // 获得当前行的开始列
                    int firstCellNum = row.getFirstCellNum();
                    // 获得当前行的列数
                    int lastCellNum = row.getPhysicalNumberOfCells();
                    String[] cells = new String[row.getPhysicalNumberOfCells()];
                    // 循环当前行
                    for (int cellNum = firstCellNum; cellNum < lastCellNum; cellNum++) {
                        Cell cell = row.getCell(cellNum);
                        cells[cellNum] = getCellValue(cell);
                    }
                    list.add(cells);
                }
            }
        }
        return list;
    }

    public class TestWorker implements Runnable {
        private Sheet sheet;
        private int finalI;
        private CountDownLatch countDownLatch;

        public TestWorker(Sheet sheet, int finalI, CountDownLatch countDownLatch) {
            this.sheet = sheet;
            this.finalI = finalI;
            this.countDownLatch = countDownLatch;
        }


        @Override
        public void run() {
            try {
                createExcelFile(sheet, Lists.newArrayList("标题1", "标题2"), Lists.newArrayList(Lists.newArrayList("value1" + finalI, "value2" + finalI), Lists.newArrayList("value1" + finalI, "value2" + finalI)));
            } finally {
                countDownLatch.countDown();
            }
        }
    }

    public void test() throws IOException, InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(100);
        ExecutorService executorService = Executors.newCachedThreadPool();
        FileOutputStream fileOut = new FileOutputStream("/Users/zy-xx/test/a.xls");
        Workbook workbook = new XSSFWorkbook();
        for (int i = 0; i < 100; i++) {
            int finalI = i;
            // 2. 创建sheet
            System.out.println("sheet" + finalI);
            Sheet sheet = workbook.createSheet("sheet" + finalI);
            executorService.execute(new TestWorker(sheet, finalI, countDownLatch));
        }
        countDownLatch.await();
        workbook.write(fileOut);
    }

    public static void main(String[] s) throws IOException, InterruptedException {
        POIUtil poiUtil = new POIUtil();
        poiUtil.test();
    }


    /**
     * 生成excel文件
     *
     * @param data 数据
     * @return
     */
    public static void createExcelFile(Sheet sheet, List<String> attributes, List<List<String>> data) {
        if (sheet == null) {
            return;
        }

        // 3. 创建row: 添加属性行
        Row row0 = sheet.createRow(sheet.getLastRowNum());
        for (int i = 0; i < attributes.size(); i++) {
            Cell cell = row0.createCell(i);
            cell.setCellValue(attributes.get(i).trim());
        }
        // 4. 插入数据
        if (CollectionUtils.isNotEmpty(data)) {
            for (int i = 0; i < data.size(); i++) {
                List<String> rowInfo = data.get(i);
                Row row = sheet.createRow(i + 1);
                // 添加数据
                for (int j = 0; j < rowInfo.size(); j++) {
                    row.createCell(j).setCellValue(rowInfo.get(j));
                }
            }
        }
    }


    /**
     * 获取当前列数据
     *
     * @param cell 列
     * @return 列值
     */
    private static String getCellValue(Cell cell) {
        String cellValue = "";

        if (cell == null) {
            return cellValue;
        }
        // 把数字当成String来读，避免出现1读成1.0的情况
        if (cell.getCellTypeEnum() == CellType.NUMERIC) {
            cell.setCellType(CellType.STRING);
        }
        // 判断数据的类型
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                cellValue = String.valueOf(cell.getNumericCellValue());
                break;
            case STRING:
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case BOOLEAN:
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case FORMULA:
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case BLANK:
                cellValue = "";
                break;
            case ERROR:
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }


    /**
     * 获得工作簿对象
     *
     * @param excelFile excel文件
     * @return 工作簿对象
     */
    public static Workbook getWorkBook(MultipartFile excelFile) {
        // 获得文件名
        String fileName = excelFile.getOriginalFilename();
        // 创建Workbook工作簿对象，表示整个excel
        Workbook workbook = null;
        try {
            // 获得excel文件的io流
            InputStream is = excelFile.getInputStream();
            // 根据文件后缀名不同(xls和xlsx)获得不同的workbook实现类对象
            if (fileName.endsWith(XLS)) {
                // 2003版本
                workbook = new HSSFWorkbook(is);
            } else if (fileName.endsWith(XLSX)) {
                // 2007版本
                workbook = new XSSFWorkbook(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }


    /**
     * 检查文件
     *
     * @param excelFile excel文件
     * @throws IOException
     */
    public static void checkFile(MultipartFile excelFile) throws IOException {
        //判断文件是否存在
        if (null == excelFile) {
            throw new FileNotFoundException("文件不存在");
        }
        //获得文件名
        String fileName = excelFile.getOriginalFilename();
        //判断文件是否是excel文件
        if (!fileName.endsWith(XLS) && !fileName.endsWith(XLSX)) {
            throw new IOException(fileName + "不是excel文件");
        }
    }
}
