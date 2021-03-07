package com.ls.download.ui;

import com.ls.download.Downloader;
import com.ls.download.M3u8DownloadTask;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * @author liangsu
 * @version v1.0
 * @Description
 * @Date 2021/3/5 17:16
 * @since
 */
public class AddTaskDialog extends JDialog implements ActionListener {

    private JLabel urlLbl;
    private JTextField url;

    private JLabel savePathLbl;
    private JTextField savePath;
    private JButton savePathBtn;

    private JButton sureBtn;
    private JButton cancelBtn;

    public AddTaskDialog(){
        init();
        savePath.setText(Downloader.getInstance().getDefaultSaveDirector());
        url.setText("https://1258712167.vod2.myqcloud.com/fb8e6c92vodtranscq1258712167/543be04a5285890806723545107/drm/voddrm.token.dWluPTU5NDk4NTUwODt2b2RfdHlwZT0wO2NpZD0yOTAwMTc1O3Rlcm1faWQ9MTAzMDEyMzE1O3Bsc2tleT0wMDA0MDAwMDUzMDRmYTk5OGI5ODQ1NGM0YmUwY2FhYWU1ZjFhYTBhOGFjYThkYjRiMWQ1MzAwNjA4NmExYjBiYjlhMzAyYjA4ZTNiY2JiOWQxMTU4MmZmO3Bza2V5PXUySkhnYTJsVFR3czhUTDVlcFdqc3lvUG44bGs1Qi1lZFFaeFRDQ1g0SElf.v.f30739.m3u8?exper=0&sign=48de9dba145afe57660e96185ae496b3&t=606c33e6&us=749754970745824822");
    }

    private void init() {
        super.setTitle("新建下载任务");
        super.setSize(new Dimension(500, 270));
        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);//设定窗口被关闭时的默认动作
        super.setLocationRelativeTo(null);//设置居中
        super.setVisible(true);
        super.setModal(true);//设置为模态对话框
        super.setLayout(null);

        // url
        urlLbl = new JLabel("url地址：");
        urlLbl.setSize(80, 30);
        urlLbl.setLocation(30, 20);

        url = new JTextField();
        url.setSize(200, 30);
        url.setLocation(120, 20);

        // 保存路径
        savePathLbl = new JLabel("保存路径：");
        savePathLbl.setSize(80, 30);
        savePathLbl.setLocation(30, 60);

        savePath = new JTextField();
        savePath.setSize(200, 30);
        savePath.setLocation(120, 60);

        savePathBtn = new JButton("选择");
        savePathBtn.setSize(80, 30);
        savePathBtn.setLocation(340, 60);
        savePathBtn.addActionListener(this);

        // 取消按钮
        cancelBtn = new JButton("取消");
        cancelBtn.setSize(80, 30);
        cancelBtn.setLocation(80, 150);
        cancelBtn.addActionListener(this);

        // 确定按钮
        sureBtn = new JButton("确定");
        sureBtn.setSize(80, 30);
        sureBtn.setLocation(250, 150);
        sureBtn.addActionListener(this);

        this.add(urlLbl);
        this.add(url);
        this.add(savePathLbl);
        this.add(savePath);
        this.add(savePathBtn);
        this.add(sureBtn);
        this.add(cancelBtn);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //获取产生事件的事件源强制转换
        JButton bt = (JButton)e.getSource();
        //获取按钮上显示的文本
        String str = bt.getText();
        if(str.equals("选择")){
            selectFile();

        }else if(str.equals("确定")){
            System.out.println("确定");

            String url = this.url.getText();
            String path = this.savePath.getText();

            M3u8DownloadTask task = new M3u8DownloadTask(path, url);
            Downloader.getInstance().submitTask(task);

            setVisible(false);

        }else{ // 取消
            setVisible(false);
        }
    }

    private void selectFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setVisible(true);
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int val = chooser.showOpenDialog(savePathBtn);

        if (val == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            System.out.println(f);

            savePath.setText(f.getAbsolutePath());

            chooser.setVisible(false);
        }


    }

}
