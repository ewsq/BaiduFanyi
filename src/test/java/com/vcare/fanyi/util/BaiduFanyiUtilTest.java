package com.vcare.fanyi.util;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class BaiduFanyiUtilTest {
/*    public static void main(String[] args) throws IOException {
        String content = "<p>i need money.</p>";
        String type = "html";
        String from = "auto";
        String to = "zh";
        System.out.println(BaiduFanyiUtil.translate(type, content, from, to));

        //取得目标目录
        File file = new File("E:\\王晋\\整站下载\\mbci.com\\mbci");
        //获取目录下子文件及子文件夹
        File[] files = file.listFiles();
        BaiduFanyiUtil.readfile(files);
    }*/

    @Test
    public void test02() throws IOException {
        //取得目标目录
        //File file = new File("F:\\WebstormProjects\\mbci");
        File file = new File("D:\\nginx-1.17.5\\html");
        //获取目录下子文件及子文件夹
        File[] files = file.listFiles();
        readfile(files);
    }
    @Test
    public void test01() throws IOException {
        String s="javascript:if(confirm('https://www.cornerstonebuildingbrands.com/  \\n\\n���ļ��\u07B7��� Teleport Ultra ����, ���\" http: www.mbci.com projects bison-ranch-retreat-center �\" ����һ�����·���ⲿ������ϊ������ʼ��ַ�ĵ�ַ�� \\n\\n�����ڷ������θ���?'))window.location=\"https://www.cornerstonebuildingbrands.com/\" \" tppabs=\"bbb";
        s+="javascript:if(confirm('https://www.cornerstonebuildingbrands.com/  \\n\\n���ļ��\u07B7��� Teleport Ultra ����, ���\" http: www.mbci.com projects bison-ranch-retreat-center �\" ����һ�����·���ⲿ������ϊ������ʼ��ַ�ĵ�ַ�� \\n\\n�����ڷ������θ���?'))window.location=\"https://www.cornerstonebuildingbrands.com/\" \" tppabs=\"ccc";
        s+="javascript:if(confirm('https://www.cornerstonebuildingbrands.com/  \\n\\n���ļ��\u07B7��� Teleport Ultra ����, ���\" http: www.mbci.com projects bison-ranch-retreat-center �\" ����һ�����·���ⲿ������ϊ������ʼ��ַ�ĵ�ַ�� \\n\\n�����ڷ������θ���?'))window.location=\"https://www.cornerstonebuildingbrands.com/\" \" tppabs=\"ddd";
        s=s.replaceAll("javascript:if\\(confirm\\('[\\s\\S]{0,}?Teleport Ultra[\\s\\S]{0,}?tppabs=\"","");
        //s=s.replaceAll("javascript:if\\(confirm\\('[\\s\\S]{0,}?tppabs=\"","");
        System.out.println("s:"+s);
        s = "XAAA FFAA11L XBBBB FFAA22L XCCCC  FFAA33L  XDDDD FFAA44L";
        s = s.replaceAll("X[\\s\\S]{0,}?FFAA", "");
        System.out.println("s:" + s);
        //我本来想要的结果是:11L 22L 33L 44L
        String str = "ab123sdab4543das756as876asd";
        str = str.replaceAll("\\d+", "#num#");  //"\d+",任意多个数字字符
        System.out.println(str);
    }

    @Test
    public void test03() throws IOException {
        String basePath="D:\\nginx-1.17.5\\html";

        basePath="F:\\WebstormProjects\\mbci";
        //取得目标目录
        File file = new File(basePath);
        //获取目录下子文件及子文件夹
        File[] files = file.listFiles();
        //readfile(files,"utf-8&quot;utf-8&quot;","utf-8");
        //readfile(files,"utf-8&quot;utf-8&quot;","utf-8");
        //FileUtil.renameTo(basePath+"\\content\\mbci\\ux\\scripts\\main.min.js-v=2.3",basePath+"\\content\\mbci\\ux\\scripts\\main.min.js");
        //readfile(files,"main.min.js-v=2.3","main.min.js");
        //readfile(files,"javascript:if\\(confirm\\('[\\s\\S]{0,}?Teleport Ultra[\\s\\S]{0,}?tppabs=\"","");
        readfile(files,"src=\"../../ajax.googleapis.com/","../ajax.googleapis.com/");
        readfile(files,"fi000002.axd-d=7tjntr83ktulid_jhmqenhbnudfdzusspn5n5pqya5xbfwafer4wqsaf5eglfbdtg_id_wni39qk3zza2izctqk-ler4uet9muptuayjv2qylskwfutmm1fcv93e8shxz5-k46dkydncihwqlw1zxkanqxf7tstzi8lye-inzqek8zyawywitlunkw6akg5p0&amp;t=ffffffffa580202a","./js/MicrosoftAjaxWebForms.js");

    }

    public static void readfile(File[] files,String srcStr,String toStr) throws IOException {
        if (files == null) {// 如果目录为空，直接退出
            return;
        }
        for(File f:files) {
            //如果是文件，直接输出名字
            if(f.isFile()) {
                String content = "";
                String filePath =f.getParent();
                String fileFullPath=f.getPath();
                String fileName=f.getName();
                String fileExtName=FileUtil.getExtName(fileName);
                //System.out.println("filePath:"+filePath);
                //System.out.println("fileFullPath:"+fileFullPath);
                //System.out.println("fileName:"+fileName);
                //System.out.println("fileExtName:"+fileExtName);
                //String targetFullPath=filePath.replace("E:\\王晋\\整站下载\\mbci.com\\mbci\\","F:\\mbci\\");

                //System.out.println("targetFullPath:"+targetFullPath);

                if(fileExtName!=null && fileExtName.toLowerCase().indexOf("htm")==0) {
                    System.out.println("fileFullPath:"+fileFullPath);
                    //content =FileUtil.readToString("E:\\王晋\\整站下载\\mbci.com\\mbci\\www.mbci.com\\index.htm");
                    content =FileUtil.readToString(fileFullPath);
                    //content=content.replaceAll("(javascript:if\\(confirm\\('[\\s\\S]{0,}Teleport Ultra[\\s\\S]{0,}tppabs=\")","");
                    //content=content.replace("utf-8&quot;utf-8&quot;","utf-8");
                    content=content.replaceAll(srcStr,toStr);
                    FileUtil.createFile(fileFullPath,content);
                }
            }
            //如果是文件夹，递归调用
            else if(f.isDirectory()) {
                readfile(f.listFiles());
            }
        }
    }
    public static void readfile(File[] files) throws IOException {
        if (files == null) {// 如果目录为空，直接退出
            return;
        }
        for(File f:files) {
            //如果是文件，直接输出名字
            if(f.isFile()) {
                String content = "";
                String filePath =f.getParent();
                String fileFullPath=f.getPath();
                String fileName=f.getName();
                String fileExtName=FileUtil.getExtName(fileName);
                //System.out.println("filePath:"+filePath);
                //System.out.println("fileFullPath:"+fileFullPath);
                //System.out.println("fileName:"+fileName);
                //System.out.println("fileExtName:"+fileExtName);
                //String targetFullPath=filePath.replace("E:\\王晋\\整站下载\\mbci.com\\mbci\\","F:\\mbci\\");

                //System.out.println("targetFullPath:"+targetFullPath);

                if(fileExtName!=null && fileExtName.toLowerCase().indexOf("htm")==0) {
                    System.out.println("fileFullPath:"+fileFullPath);
                    //content =FileUtil.readToString("E:\\王晋\\整站下载\\mbci.com\\mbci\\www.mbci.com\\index.htm");
                    content =FileUtil.readToString(fileFullPath);
                    content=content.replaceAll("(javascript:if\\(confirm\\('[\\s\\S]{0,}?Teleport Ultra[\\s\\S]{0,}?tppabs=\")","");
                    FileUtil.createFile(fileFullPath,content);
                }
            }
            //如果是文件夹，递归调用
            else if(f.isDirectory()) {
                readfile(f.listFiles());
            }
        }
    }
}
