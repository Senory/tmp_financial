package com.example.app.ui.pages;

import com.example.app.viewmodel.pages.ReportsViewModel;
import com.example.app.viewmodel.pages.ReportsViewModel.ReportsChangeListener;
import com.example.app.model.FinanceData;
import com.example.app.ui.reports.*;
import com.example.app.viewmodel.reports.IncomeExpensesReportViewModel;
import com.example.app.viewmodel.reports.CategoryBreakdownViewModel;
import com.example.app.viewmodel.reports.TrendReportViewModel;
import javax.swing.*;
import java.awt.*;

public class ReportsPanel extends JPanel implements ReportsChangeListener {
    private final ReportsViewModel viewModel;
    private JPanel chartContainer;
    private CardLayout cardLayout;

    // Chart panels
    private IncomeExpensesReportPanel incomeExpensesPanel;
    private CategoryBreakdownPanel categoryBreakdownPanel;
    private TrendReportPanel trendReportPanel;

    // Constants for card layout
    private static final String INCOME_EXPENSE_PANEL = "INCOME_EXPENSE";
    private static final String CATEGORY_BREAKDOWN_PANEL = "CATEGORY_BREAKDOWN";
    private static final String TREND_PANEL = "TREND";

    public ReportsPanel(String username) {
        this.viewModel = new ReportsViewModel(username);
        this.viewModel.addChangeListener(this);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with title
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Control panel for filters
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.WEST);

        // Chart container with card layout
        cardLayout = new CardLayout();
        chartContainer = new JPanel(cardLayout);

        // Create sub-panel ViewModels
        IncomeExpensesReportViewModel incomeVM = new IncomeExpensesReportViewModel(viewModel.getFinanceData());
        CategoryBreakdownViewModel categoryVM = new CategoryBreakdownViewModel(viewModel.getFinanceData());
        TrendReportViewModel trendVM = new TrendReportViewModel(viewModel.getFinanceData());

        // Pass ViewModels to sub-panels
        incomeExpensesPanel = new IncomeExpensesReportPanel(incomeVM);
        categoryBreakdownPanel = new CategoryBreakdownPanel(categoryVM);
        trendReportPanel = new TrendReportPanel(trendVM);

        chartContainer.add(incomeExpensesPanel, INCOME_EXPENSE_PANEL);
        chartContainer.add(categoryBreakdownPanel, CATEGORY_BREAKDOWN_PANEL);
        chartContainer.add(trendReportPanel, TREND_PANEL);

        cardLayout.show(chartContainer, INCOME_EXPENSE_PANEL);

        JScrollPane scrollPane = new JScrollPane(chartContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Financial Reports");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 24));
        panel.add(titleLabel, BorderLayout.WEST);
        return panel;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        panel.setPreferredSize(new Dimension(220, 300));
        
        // Report Type Section
        panel.add(createSectionLabel("Report Type"));
        
        ButtonGroup reportTypeGroup = new ButtonGroup();
        JRadioButton incomeExpenseBtn = createRadioButton("Income vs Expenses", true);
        JRadioButton categoryBreakdownBtn = createRadioButton("Expense Breakdown", false);
        JRadioButton trendsBtn = createRadioButton("Trends Analysis", false);
        
        reportTypeGroup.add(incomeExpenseBtn);
        reportTypeGroup.add(categoryBreakdownBtn);
        reportTypeGroup.add(trendsBtn);
        
        panel.add(incomeExpenseBtn);
        panel.add(categoryBreakdownBtn);
        panel.add(trendsBtn);
        
        panel.add(Box.createVerticalStrut(20));
        
        // Time Period Section
        panel.add(createSectionLabel("Time Period"));
        
        String[] timePeriods = {"Last 7 days", "Last 30 days", "Last 90 days", "This month", "Last month", "This year"};
        JComboBox<String> timePeriodsCombo = new JComboBox<>(timePeriods);
        timePeriodsCombo.setMaximumSize(new Dimension(220, 30));
        timePeriodsCombo.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(timePeriodsCombo);
        
        panel.add(Box.createVerticalStrut(20));
        
        // Interval Section (for trend reports)
        panel.add(createSectionLabel("Data Interval"));
        String[] intervals = {"Daily", "Weekly", "Fortnightly", "Monthly", "Quarterly", "Yearly"};
        JComboBox<String> intervalsCombo = new JComboBox<>(intervals);
        intervalsCombo.setMaximumSize(new Dimension(220, 30));
        intervalsCombo.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(intervalsCombo);
        
        panel.add(Box.createVerticalStrut(20));
        
        // Apply button
        JButton applyButton = new JButton("Apply Filters");
        applyButton.setAlignmentX(LEFT_ALIGNMENT);
        applyButton.setMaximumSize(new Dimension(220, 35));
        panel.add(applyButton);
        
        // 添加加载数据按钮
        JButton loadDataButton = new JButton("加载交易数据");
        loadDataButton.setAlignmentX(LEFT_ALIGNMENT);
        loadDataButton.setMaximumSize(new Dimension(220, 35));
        panel.add(loadDataButton);
        
        // Add action listeners
        timePeriodsCombo.addActionListener(e -> {
            updateReportTimeRange(timePeriodsCombo.getSelectedItem().toString());
        });
        
        intervalsCombo.addActionListener(e -> {
            updateReportInterval(intervalsCombo.getSelectedItem().toString());
        });
        
        incomeExpenseBtn.addActionListener(e -> {
            if (incomeExpenseBtn.isSelected()) {
                cardLayout.show(chartContainer, INCOME_EXPENSE_PANEL);
            }
        });
        
        categoryBreakdownBtn.addActionListener(e -> {
            if (categoryBreakdownBtn.isSelected()) {
                cardLayout.show(chartContainer, CATEGORY_BREAKDOWN_PANEL);
            }
        });
        
        trendsBtn.addActionListener(e -> {
            if (trendsBtn.isSelected()) {
                cardLayout.show(chartContainer, TREND_PANEL);
            }
        });
        
        applyButton.addActionListener(e -> {
            applyFilters(
                timePeriodsCombo.getSelectedItem().toString(),
                intervalsCombo.getSelectedItem().toString()
            );
        });
        
        loadDataButton.addActionListener(e -> {
            viewModel.loadTransactionData();
            // 刷新所有图表
            incomeExpensesPanel.refreshChart();
            categoryBreakdownPanel.refreshChart();
            trendReportPanel.refreshChart();
            JOptionPane.showMessageDialog(this, "交易数据已成功加载", "加载成功", JOptionPane.INFORMATION_MESSAGE);
        });
        
        return panel;
    }
    
    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(label.getFont().getName(), Font.BOLD, 14));
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        return label;
    }
    
    private JRadioButton createRadioButton(String text, boolean selected) {
        JRadioButton radioButton = new JRadioButton(text, selected);
        radioButton.setAlignmentX(LEFT_ALIGNMENT);
        return radioButton;
    }
    
    // Methods to update report parameters
    private void updateReportTimeRange(String timeRange) {
        incomeExpensesPanel.setTimeRange(timeRange);
        categoryBreakdownPanel.setTimeRange(timeRange);
        trendReportPanel.setTimeRange(timeRange);
    }
    
    private void updateReportInterval(String interval) {
        trendReportPanel.setInterval(interval);
    }
    
    private void applyFilters(String timeRange, String interval) {
        updateReportTimeRange(timeRange);
        updateReportInterval(interval);
        
        // Refresh all panels
        incomeExpensesPanel.refreshChart();
        categoryBreakdownPanel.refreshChart();
        trendReportPanel.refreshChart();
    }

    @Override
    public void onReportsDataChanged() {
        // Refresh all charts when data changes
        incomeExpensesPanel.refreshChart();
        categoryBreakdownPanel.refreshChart();
        trendReportPanel.refreshChart();
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        viewModel.removeChangeListener(this);
        viewModel.cleanup();
    }
}