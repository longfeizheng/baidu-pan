package com.niocoder;

import com.alibaba.fastjson.JSON;
import com.sun.deploy.net.URLEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class BaiDuPan {

    // 请求url 固定
    public static String urlPath = "https://pan.baidu.com/api/list?order=name&desc=0&showempty=0&web=1&page=1&num=300&dir=%s";
    // cookie 从浏览器请求中粘贴出来即可
    public static String cookie = "cookie";
    // 树形分隔符
    public static String str2 = "├──";
    public static String str1 = "|   ";

    public static void main(String[] args) throws Exception {
        printDir("打印的目录");
    }

    public static void printDir(String name) throws Exception {
        // 百度云目录，可以从请求url中看到
        System.out.println(name);

        print(name, 1);
    }


    /**
     * 打印目录
     *
     * @param name 全路径
     * @param level 打印目录深度
     * @throws Exception
     */
    public static void print(String name, int level) throws Exception {
        // 打印目录深度判断
//        if (level > 3) {
//            return;
//        }
        String url = String.format(urlPath, URLEncoder.encode(name, "UTF-8"));
        List<BaseResponse.ListBean> listBeans = getResponse(url);
        if (listBeans.isEmpty()) {
            return;
        }
        for (int i = 0; i < listBeans.size(); i++) {
            StringBuffer tempStr = new StringBuffer("");
            // 规则输出
            for (int j = 0; j < level; j++) {
                tempStr.append(str1);
            }
            tempStr.append(str2);
            System.out.println(tempStr + listBeans.get(i).getServer_filename() + "     " + (listBeans.get(i).getSize() == 0 ? "" : getPrintSize(listBeans.get(i).getSize())));
            if (listBeans.get(i).getIsdir() == 1) {
                print(listBeans.get(i).getPath(), level + 1);
            }
        }
    }

    public static List<BaseResponse.ListBean> getResponse(String uri) throws Exception {
        URL url = new URL(uri);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Cookie", cookie);
        conn.setDoInput(true);
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        conn.disconnect();
        BaseResponse baseResponse = JSON.parseObject(sb.toString(), BaseResponse.class);
        return baseResponse.getErrno() == 0 ? baseResponse.getList() : null;
    }

    public static String getPrintSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }
}
