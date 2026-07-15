/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.co.bc.inventory.service;

/**
 *
 * @author Jt
 */
import za.co.bc.inventory.dao.StockIssueDAO;
import za.co.bc.inventory.model.StockIssue;
import java.sql.SQLException;

public class StockIssueService {
    private StockIssueDAO dao;

    public StockIssueService() {
        this.dao = new StockIssueDAO();
    }

    public String processIssuance(int materialId, int cleanerId, int quantity, int userId) {
        if (quantity <= 0) {
            return "Error: Quantity must be greater than zero.";
        }
        
        StockIssue issue = new StockIssue(materialId, cleanerId, quantity, userId);
        
        try {
            boolean success = dao.issueStock(issue);
            if (success) {
                return "Success: Stock issued successfully.";
            }
        } catch (SQLException e) {
            return "Transaction Failed: " + e.getMessage();
        }
        return "Error: Unknown failure occurred.";
    }
}