package com.vcare.fanyi.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

/**
 *
 * @项目名称: xxx
 * @版权所有: xxx
 * @技术支持: xxx
 * @单元名称: 百度翻译，支持纯文本和HTML内容
 * @开始时间: 2018-11-21
 * @开发人员: xxx
 */
public class BaiduFanyiUtil {
    private static int timeout = 30 * 1000;
    private static String url = "http://api.fanyi.baidu.com/api/trans/vip/translate";
    private static String baiduAppid = "APP_ID";//百度翻译应用id
    private static String baiduAppsecret = "APP_SECRET";//百度翻译应用密钥

    public BaiduFanyiUtil (){
        Properties prop = PropertiesUtil.readOrderedPropertiesFile("config/baidu.properties");
        url=prop.getProperty(url);
        baiduAppid=prop.getProperty(baiduAppid);
        baiduAppsecret=prop.getProperty(baiduAppsecret);
    }

    public static void readfile(File[] files) throws IOException {
        if (files == null) {// 如果目录为空，直接退出
            return;
        }
        for(File f:files) {
            //如果是文件，直接输出名字
            if(f.isFile()) {
                String content = "<p>i need money.</p>";
                String type = "html";
                String from = "auto";
                String to = "zh";
                //String filePath = "F:\\logs\\";
                //String fileName="index.html";
                String filePath =f.getParent();
                String fileFullPath=f.getPath();
                String fileName=f.getName();
                String fileExtName=FileUtil.getExtName(fileName);
                //System.out.println("filePath:"+filePath);
                //System.out.println("fileFullPath:"+fileFullPath);
                //System.out.println("fileName:"+fileName);
                //System.out.println("fileExtName:"+fileExtName);
                String targetFullPath=filePath.replace("E:\\王晋\\整站下载\\mbci.com\\mbci\\","F:\\mbci\\");

                System.out.println("targetFullPath:"+targetFullPath);

                if(fileExtName!=null && fileExtName.toLowerCase().indexOf("htm")==0) {
                    System.out.println("fileName:"+fileName);
                    //content =FileUtil.readToString("E:\\王晋\\整站下载\\mbci.com\\mbci\\www.mbci.com\\index.htm");

                    content =FileUtil.readToString(fileFullPath);
                    JSONObject obj=translate(type, content, from, to);
                    //System.out.println(obj);

                    if(obj.containsKey("dst")){
                        FileUtil.createFile(targetFullPath,fileName,obj.getString("dst"));
                    }else{
                        System.out.println("没有读到文件内容："+fileFullPath);
                    }
                }else if(fileExtName!=null &&  fileExtName.length()>0){
                    //System.out.println("原文件："+fileFullPath);
                    //System.out.println("目标文件："+targetFullPath+"\\"+fileName);
                    FileUtil.copyFile(fileFullPath,targetFullPath+"\\"+fileName);
                }
            }
            //如果是文件夹，递归调用
            else if(f.isDirectory()) {
                readfile(f.listFiles());
            }
        }
    }

    /**
     * auto 自动检测 zh 中文 en 英语 yue 粤语 wyw 文言文 jp 日语 kor 韩语 fra 法语 spa 西班牙语 th 泰语
     * ara 阿拉伯语 ru 俄语 pt 葡萄牙语 de 德语 it 意大利语 el 希腊语 nl 荷兰语 pl 波兰语 bul 保加利亚语 est
     * 爱沙尼亚语 dan 丹麦语 fin 芬兰语 cs 捷克语 rom 罗马尼亚语 slo 斯洛文尼亚语 swe 瑞典语 hu 匈牙利语 cht
     * 繁体中文 vie 越南语
     *
     * @param content
     * @param from
     * @param to
     * @return
     */
    public static JSONObject translate(String type, String content,
                                       String from, String to) {
        JSONObject response = new JSONObject();

        if (content == null || content.trim().isEmpty()) {
            response.put("code", 400);
            response.put("error_msg", "content参数缺失");
            return response;
        }

        CloseableHttpClient httpclient = null;
        CloseableHttpResponse httpResponse = null;
        InputStream in = null;

        try {
            type = type == null ? "text" : type;
            from = from == null ? "auto" : from;
            Document doc = null;
            Elements els = null;
            String html = null;

            if (type.equals("html")||type.equals("htm")) {
                doc = Jsoup.parse(content);
                els = doc.getAllElements();
                html = content;

                String text = "";
                String separator = "@es@";
                StringBuilder lines = new StringBuilder(separator);
                List<TextNode> nodes = null;
                for (Element el : els) {

                    nodes = el.textNodes();
                    if (nodes.size() > 0) {
                        for (TextNode node : nodes) {
                            text = node.text().trim();

                            if (!text.isEmpty()
                                    && lines.indexOf(separator + text
                                    + separator) == -1) {

                                lines.append(text + separator);
                            }
                        }
                    }

                }

                content = lines.substring(separator.length());
                content = content.substring(0,
                        content.length() - separator.length());
                content = content.replace("\n", "\r ").replace(separator, "\n");
            }

            int length = content.length();
            // 官方限制了
            // 为保证翻译质量，请将单次请求长度控制在 6000 bytes以内。（汉字约为2000个）
            List<String> contents = new ArrayList<String>();
            if (length > 2000) {
                String[] origs = content.split("\n");
                StringBuilder builder = new StringBuilder();
                for (int i = 0, len = origs.length - 1; i <= len; i++) {
                    builder.append("\n" + origs[i]);

                    if (builder.length() > 2000) {
                        contents.add(builder.substring(0,
                                (builder.length() - origs[i].length()) - 1));
                        builder.setLength(0);// 清空
                        builder.append("\n" + origs[i]);
                    }

                    if (i == len) {// 最后一个
                        contents.add(builder.toString());
                    }
                }

            } else {
                contents.add(content);
            }

            JSONArray tranArr = new JSONArray();

            for (int i = 0, size = contents.size() - 1; i <= size; i++) {
                String query = contents.get(i);

                String salt = Long.toString(new Date().getTime());
                // 对appId+源文+随机数+token计算md5值
                String sign = baiduAppid + query + salt + baiduAppsecret;

                sign = BaiduFanyiUtil.md5(sign);

                // 使用Post方式，组装参数
                HttpPost httpost = new HttpPost(url);
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                nvps.add(new BasicNameValuePair("q", query));
                nvps.add(new BasicNameValuePair("from", from));
                nvps.add(new BasicNameValuePair("to", to));
                nvps.add(new BasicNameValuePair("appid", baiduAppid));
                nvps.add(new BasicNameValuePair("salt", salt));
                nvps.add(new BasicNameValuePair("sign", sign));
                httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));

                RequestConfig config = RequestConfig.custom()
                        .setConnectTimeout(timeout).build();
                httpost.setConfig(config);

                // 创建httpclient链接，并执行
                httpclient = HttpClients.createDefault();
                httpResponse = httpclient.execute(httpost);

                String data = "";
                // 对于返回实体进行解析
                int code = httpResponse.getStatusLine().getStatusCode();

                if (code == 200) {
                    HttpEntity entity = httpResponse.getEntity();
                    in = entity.getContent();
                    if (in != null) {
                        List<String> lines = IOUtils.readLines(in,
                                "UTF-8");
                        for (String line : lines) {
                            data += line;
                        }
                    }
                }

                response.put("code", code);

                if (!data.isEmpty()) {
                    response = JSONObject.fromObject(data);
                } else {
                    response.put("error_code", 404);
                    response.put("error_msg", "没有响应内容");
                }

                // 某次翻译有错就直接跳出
                if (response.containsKey("error_code")) {

                    break;
                }

                JSONArray trans = response.getJSONArray("trans_result");
                tranArr.addAll(trans);

                if (i == size) {// 最后一个翻译
                    response.put("trans_result", tranArr);
                }

                if (in != null) {
                    in.close();
                }
                if (httpResponse != null) {
                    httpResponse.close();
                }
                if (httpclient != null) {
                    httpclient.close();
                }
            }

            if (response.containsKey("error_code")) {

                response.put("code", 400);
            } else {
                if (type.equals("html")) {
                    if (response.getString("from").equals(
                            response.getString("to"))) {// 没有翻译
                        response.remove("trans_result");
                        response.put("src", html);
                        response.put("dst", html);
                    } else {
                        String[] origs = content.split("\n");
                        JSONArray trans = response.getJSONArray("trans_result");
                        Map<String, String> results = new HashMap<String, String>();
                        JSONObject object = null;
                        response.put("text", content);

                        if (origs.length != trans.size()) {
                            response.put("src", html);
                            response.put("code", 400);
                            response.put("error_msg",
                                    "翻译失败，HTML纯文本提取后的个数与翻译结果不一致");
                        } else {

                            for (int i = 0, len = origs.length; i < len; i++) {
                                object = trans.getJSONObject(i);
                                results.put(origs[i], object.getString("dst"));
                            }

                            String text = "";
                            String newText = "";
                            String title = "";
                            List<TextNode> nodes = null;
                            for (Element el : els) {

                                nodes = el.textNodes();
                                if (nodes.size() > 0) {
                                    for (TextNode node : nodes) {
                                        text = node.text().trim();
                                        if (!text.isEmpty()) {
                                            text = text.replace("\n", "\r ");
                                            if (results.containsKey(text)) {
                                                newText = results.get(text)
                                                        .replace("\r ", "\n");
                                                node.text(newText);

                                                title = el.attr("title");
                                                if (!title.isEmpty()
                                                        && title.equals(text)) {
                                                    el.attr("title", newText);
                                                }
                                            }
                                        }
                                    }
                                }

                            }

                            response.remove("trans_result");
                            response.put("src", html);
                            String dst = doc.html();
                            response.put("dst", dst);
                        }
                    }

                } else {
                    JSONArray trans = response.getJSONArray("trans_result");

                    JSONObject object = null;
                    StringBuilder src = new StringBuilder();
                    StringBuilder dst = new StringBuilder();
                    for (int i = 0, len = trans.size(); i < len; i++) {
                        object = trans.getJSONObject(i);

                        src.append("\n" + object.getString("src"));
                        dst.append("\n" + object.getString("dst"));
                    }

                    response.remove("trans_result");
                    response.put("src", src.toString().substring(1));
                    response.put("dst", dst.toString().substring(1));
                }

            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            response.put("code", 500);
            response.put("error_msg", "请求异常，异常信息：" + e.getMessage());
        }

        response.put("type", type);

        return response;
    }

    // md5加密
    public static String md5(String message) {
        MessageDigest messageDigest = null;
        StringBuffer buffer = new StringBuffer();
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(message.getBytes("UTF-8"));

            byte[] byteArray = messageDigest.digest();
            for (int i = 0; i < byteArray.length; i++) {
                if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) {

                    buffer.append("0").append(
                            Integer.toHexString(0xFF & byteArray[i]));
                } else {
                    buffer.append(Integer.toHexString(0xFF & byteArray[i]));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString().toLowerCase();// 字母小写
    }
}