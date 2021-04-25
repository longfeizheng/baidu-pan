package com.niocoder;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
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

        // 显示应用 GUI
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
//        printDir("打印的目录");
    }

    /**
     * {
     * 创建并显示GUI。出于线程安全的考虑，
     * 这个方法在事件调用线程中调用。
     */
    private static void createAndShowGUI() {
        // 确保一个漂亮的外观风格
        JFrame.setDefaultLookAndFeelDecorated(true);

        // 创建及设置窗口
        JFrame frame = new JFrame("微信公众号：java干货");
        frame.setSize(700, 300);//设置大小
        frame.setTitle("微信公众号：java干货");//设置标题
        frame.setDefaultCloseOperation(3);//界面关闭方式
        frame.setLocationRelativeTo(null);//显示的界面居中
        frame.setResizable(false);//设置能否改变大小


        /* 创建面板，这个类似于 HTML 的 div 标签
         * 我们可以创建多个面板并在 JFrame 中指定位置
         * 面板中我们可以添加文本字段，按钮及其他组件。
         */
        JPanel panel = new JPanel();
        // 添加面板
        frame.add(panel);
        /*
         * 调用用户定义的方法并添加组件到面板
         */
        placeComponents(panel);
        frame.setVisible(true);//界面的可见性

    }

    private static void placeComponents(JPanel panel) {
        /* 布局部分我们这边不多做介绍
         * 这边设置布局为 null
         */
        panel.setLayout(null);

        // 创建 JLabel
        JLabel userLabel = new JLabel("Cookie:");
        /* 这个方法定义了组件的位置。
         * setBounds(x, y, width, height)
         * x 和 y 指定左上角的新位置，由 width 和 height 指定新的大小。
         */
        userLabel.setBounds(30, 20, 80, 25);
        panel.add(userLabel);

        /*
         * 创建文本域用于用户输入
         */
        JTextField userText = new JTextField(20);
        userText.setBounds(120, 20, 500, 25);
        panel.add(userText);

        // 输入密码的文本域
        JLabel passwordLabel = new JLabel("打印路径:");
        passwordLabel.setBounds(30, 50, 80, 25);
        panel.add(passwordLabel);

        /*
         *这个类似用于输入的文本域
         * 但是输入的信息会以点号代替，用于包含密码的安全性
         */
        JTextField passwordText = new JTextField(20);
        passwordText.setBounds(120, 50, 500, 25);
        panel.add(passwordText);

        // 创建登录按钮
        JButton loginButton = new JButton("输出");
        loginButton.setBounds(30, 80, 80, 25);
        panel.add(loginButton);

        // 创建 JLabel
        JLabel msg = new JLabel("");
        /* 这个方法定义了组件的位置。
         * setBounds(x, y, width, height)
         * x 和 y 指定左上角的新位置，由 width 和 height 指定新的大小。
         */
        msg.setBounds(30, 120, 80, 25);
        panel.add(msg);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cookie = userText.getText();
                String path = passwordText.getText();
                if (StringUtils.isBlank(cookie)) {
                    JOptionPane.showMessageDialog(null, "Cookie不能为空", "格式错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (StringUtils.isBlank(path)) {
                    JOptionPane.showMessageDialog(null, "路径不能为空", "格式错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                BaiDuPan.cookie = cookie;
                String pringPath = "";
                try {
                    pringPath = printDir(path);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }

                msg.setText("打印完成！");
            }
        });
    }


    public static String printDir(String name) throws Exception {
//        FileUtils.write();
        // 百度云目录，可以从请求url中看到
        System.out.println(name);
        List<String> lines = new ArrayList<>();
        lines.add(name);
        print(name, 1, lines);
        String path = "d:/" + System.currentTimeMillis() + ".txt";
        File file = new File(path);
        FileUtils.writeLines(file, lines);
        return path;
    }


    /**
     * 打印目录
     *
     * @param name  全路径
     * @param level 打印目录深度
     * @throws Exception
     */
    public static void print(String name, int level, List<String> lines) throws Exception {
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
            lines.add(tempStr + listBeans.get(i).getServer_filename() + "     " + (listBeans.get(i).getSize() == 0 ? "" : getPrintSize(listBeans.get(i).getSize())));
            if (listBeans.get(i).getIsdir() == 1) {
                print(listBeans.get(i).getPath(), level + 1, lines);
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
