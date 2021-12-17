package cn.edu.xmu.oomall.transaction.util.file;

import cn.edu.xmu.oomall.transaction.util.file.vo.AliPayFormat;
import cn.edu.xmu.oomall.transaction.util.file.vo.WechatFormat;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author xiuchen lang 22920192204222
 * @date 2021/12/17 10:32
 */
public class FileUtil {
    private static final int BUFFER_SIZE = 2048;
    private static final DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void unZip(File srcFile, String destDirPath) throws RuntimeException {
        long start = System.currentTimeMillis();
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new RuntimeException(srcFile.getPath() + "所指文件不存在");
        }
        // 开始解压
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcFile, Charset.forName("GBK"));
            Enumeration<?> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) entries.nextElement();
                System.out.println("解压" + entry.getName());
                // 如果是文件夹，就创建个文件夹
                if (entry.isDirectory()) {
                    String dirPath = destDirPath + "/" + entry.getName();
                    File dir = new File(dirPath);
                    dir.mkdirs();
                } else {
                    // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                    File targetFile = new File(destDirPath + "/" + entry.getName());
                    // 保证这个文件的父文件夹必须要存在
                    if (!targetFile.getParentFile().exists()) {
                        targetFile.getParentFile().mkdirs();
                    }
                    targetFile.createNewFile();
                    // 将压缩文件内容写入到这个文件中
                    InputStream is = zipFile.getInputStream(entry);
                    FileOutputStream fos = new FileOutputStream(targetFile);
                    int len;
                    byte[] buf = new byte[BUFFER_SIZE];
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.close();
                    is.close();
                }
            }
            long end = System.currentTimeMillis();
            System.out.println("解压完成，耗时：" + (end - start) + " ms");
        } catch (Exception e) {
            throw new RuntimeException("unzip error from ZipUtils", e);

        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<AliPayFormat> aliPayParsing(File file) throws RuntimeException {
        List<AliPayFormat> list = new ArrayList<>();
        isValidCsv(file);
        //解析
        CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
        try (CSVReader readerCsv = new CSVReaderBuilder(new FileReader(file, Charset.forName("GBK"))).withSkipLines(5).withCSVParser(csvParser).build()) {
            String lines[];
            while ((lines = readerCsv.readNext()) != null) {
                List<String> strings = Arrays.asList(lines);
                if (!strings.get(0).startsWith("#")) {
                    ArrayList<String> objects = new ArrayList<>();
                    for (String s : strings) {
                        objects.add(s.trim());
                    }
                    AliPayFormat aliPayFormat = new AliPayFormat();
                    aliPayFormat.setAccountSerialNumber(objects.get(0));
                    aliPayFormat.setTradeNo(objects.get(1));
                    aliPayFormat.setOutTradeNo(objects.get(2));
                    aliPayFormat.setGoodsName(objects.get(3));
                    aliPayFormat.setTradeCreateTime(LocalDateTime.parse(objects.get(4), df));
                    aliPayFormat.setTransNo(objects.get(4));
                    aliPayFormat.setIncome(objects.get(5));
                    aliPayFormat.setOutlay(objects.get(6));
                    aliPayFormat.setBalance(objects.get(7));
                    aliPayFormat.setTradingChannel(objects.get(8));
                    aliPayFormat.setBusinessType(objects.get(9));
                    aliPayFormat.setRemark(objects.get(10));
                    list.add(aliPayFormat);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<WechatFormat> wechatParsing(File file) throws RuntimeException {
        List<WechatFormat> list = new ArrayList<>();
        isValidCsv(file);
        //解析
        CSVParser csvParser = new CSVParserBuilder().withSeparator(',').build();
        try (CSVReader readerCsv = new CSVReaderBuilder(new FileReader(file, Charset.forName("utf-8"))).withSkipLines(17).withCSVParser(csvParser).build()) {
            String lines[];
            while ((lines = readerCsv.readNext()) != null) {
                List<String> strings = Arrays.asList(lines);
                if (!strings.get(0).startsWith("#")) {
                    ArrayList<String> objects = new ArrayList<>();
                    for (String s : strings) {
                        objects.add(s.trim());
                    }
                    WechatFormat wechatFormat = new WechatFormat();
                    wechatFormat.setTradeCreateTime(LocalDateTime.parse(objects.get(0), df));
                    wechatFormat.setBusinessType(objects.get(1));
                    wechatFormat.setTransNo(objects.get(2));
                    wechatFormat.setGoods(objects.get(3));
                    wechatFormat.setType(objects.get(4));
                    wechatFormat.setAmount(objects.get(5));
                    wechatFormat.setTradingChannel(objects.get(6));
                    wechatFormat.setState(objects.get(7));
                    wechatFormat.setTradeNo(objects.get(8));
                    wechatFormat.setOutTradeNo(objects.get(9));
                    wechatFormat.setRemark(objects.get(10));
                    list.add(wechatFormat);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static void isValidCsv(File file) throws RuntimeException {
        if (!file.exists()) {
            throw new RuntimeException(file.getPath() + "所指文件不存在");
        }
        if (!file.isFile()) {
            throw new RuntimeException(file.getPath() + "所指不是文件");
        }
        if (!file.canRead()) {
            throw new RuntimeException(file.getPath() + "不可读");
        }
        String filepath = file.getPath();
        if (!(filepath.substring(filepath.lastIndexOf(".") + 1)).equalsIgnoreCase("csv")) {
            throw new RuntimeException(file.getPath() + "不是csv文件");
        }
    }
}
