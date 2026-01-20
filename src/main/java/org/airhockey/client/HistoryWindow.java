package org.airhockey.client;

import org.airhockey.storage.JsonStorage;
import org.airhockey.storage.MatchRecord;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class HistoryWindow extends JFrame {
    public HistoryWindow() {
        setTitle("История матчей");
        setSize(600, 400);
        setLocationRelativeTo(null);

        String[] columns = {"Дата", "Победитель", "Счет"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        List<MatchRecord> history = JsonStorage.loadHistory();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM HH:mm");

        for (MatchRecord match : history) {
            model.addRow(new Object[]{
                    sdf.format(new java.util.Date(match.getTime())),
                    match.getWinner(),
                    match.getLeftScore() + " : " + match.getRightScore()
            });
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        JButton closeBtn = new JButton("Закрыть");
        closeBtn.addActionListener(e -> dispose());
        add(closeBtn, BorderLayout.SOUTH);
    }
}