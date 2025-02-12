package com.app.ace_taxi_v2.Models;

import java.util.List;

public class ExpensesResponse {
    private List<Expense> expenses; // Store list of expenses

    public List<Expense> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
}
