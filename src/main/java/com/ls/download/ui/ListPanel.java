package com.ls.download.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @author liangsu
 * @version v1.0
 * @Description
 * @Date 2021/3/4 17:01
 * @since
 */
public class ListPanel extends JPanel {

    public ListPanel(){
        super.setBackground(Color.RED);
        super.setPreferredSize(new Dimension(750, 300));
        super.setLayout(new BorderLayout());

//         表头（列名）
        Object[] columnNames = {"姓名", "语文", "数学", "英语", "总分"};
        Object[][] rowData = {
                {"张三", 80, 80, 80, 240},
                {"John", 70, 80, 90, 240},
                {"Sue", 70, 70, 70, 210},
                {"Jane", 80, 70, 60, 210},
                {"Joe", 80, 70, 60, 210}
        };
        JTable table = new JTable(rowData, columnNames);
        table.setPreferredSize(new Dimension(750, 300));

//        JScrollPane jScrollPane = new JScrollPane();
//        jScrollPane.setPreferredSize(new Dimension(750, 300));
//        jScrollPane.setBackground(Color.YELLOW);
//        jScrollPane.setLayout(new BorderLayout());
//
//        // 把 表头 添加到容器顶部（使用普通的中间容器添加表格时，表头 和 内容 需要分开添加）
//        jScrollPane.add(table.getTableHeader());
//        // 把 表格内容 添加到容器中心
//        jScrollPane.add(table);
//        super.add(jScrollPane, BorderLayout.NORTH);

        super.add(table.getTableHeader(), BorderLayout.NORTH);
        super.add(table, BorderLayout.CENTER);
    }

}
