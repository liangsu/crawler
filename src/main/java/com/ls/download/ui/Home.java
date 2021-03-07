package com.ls.download.ui;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

/**
 * @author liangsu
 * @version v1.0
 * @Description
 * @Date 2021/3/4 16:39
 * @since
 */
public class Home extends JFrame{

    private JPanel contentPanel;

    private ListPanel listPanel;

    public Home(){
        super.setVisible(true);
        super.setBounds(600, 300, 800, 300);   //设定窗口的尺寸
        super.setDefaultCloseOperation(DISPOSE_ON_CLOSE);//设定窗口被关闭时的默认动作
        super.setLayout(new BorderLayout());

        // 工具栏
        add(createToolPanel(), BorderLayout.NORTH);

        // 下方区域
        JPanel buttomPanel = new JPanel();
        buttomPanel.setPreferredSize(new Dimension(750, 300));
        buttomPanel.setLayout(new BoxLayout(buttomPanel, BoxLayout.X_AXIS));
        add(buttomPanel, BorderLayout.CENTER);

        // 菜单
        JPanel menuPanel = createMenu();
        menuPanel.setPreferredSize(new Dimension(50, 300));
        buttomPanel.add(createMenu());

        // 右边内容区域
        contentPanel = new JPanel();
        contentPanel.setPreferredSize(new Dimension(750, 300));
        contentPanel.setLayout(new GridLayout(1, 1, 0, 0));
        contentPanel.setBackground(Color.GREEN);
        buttomPanel.add(contentPanel);

        listPanel = new ListPanel();
//        listPanel.setPreferredSize(new Dimension(750, 300));
//        listPanel.setVisible(false);
//        contentPanel.add(listPanel);
    }

    private JToolBar createToolPanel(){
        JToolBar toolBar = new JToolBar();

        JButton downloadListBtn = new JButton("新建任务");
        downloadListBtn.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
//                contentPanel.remove(listPanel);
//                contentPanel.repaint();
//                contentPanel.repaint();
                AddTaskDialog addDialog = new AddTaskDialog();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        toolBar.addSeparator();
        toolBar.add(downloadListBtn, BorderLayout.WEST);
        toolBar.addSeparator();
        return toolBar;
    }

    private JPanel createMenu(){
        JPanel menuPanel = new JPanel();
        menuPanel.setBorder(new LineBorder(Color.WHITE, 1));

        JButton downloadListBtn = new JButton("下载列表");
        downloadListBtn.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                contentPanel.add(listPanel);
                listPanel.updateUI();
                contentPanel.repaint();
//                Home.this.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        menuPanel.add(downloadListBtn);
        return menuPanel;
    }

    public static void main(String[] args) throws IOException {
        Home home = new Home();
    }

}
