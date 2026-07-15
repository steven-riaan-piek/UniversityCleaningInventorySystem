/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package za.co.bc.inventory.model;

/**
 *
 * @author Jt
 */
import java.sql.Timestamp;

public class StockIssue {
    private int issuanceId;
    private int materialId;
    private int cleanerId;
    private int quantityIssued;
    private Timestamp issueDate;
    private int issuedByUserId;

    public StockIssue(int materialId, int cleanerId, int quantityIssued, int issuedByUserId) {
        this.materialId = materialId;
        this.cleanerId = cleanerId;
        this.quantityIssued = quantityIssued;
        this.issuedByUserId = issuedByUserId;
    }

    public int getIssuanceId() { return issuanceId; }
    public void setIssuanceId(int issuanceId) { this.issuanceId = issuanceId; }
    
    public int getMaterialId() { return materialId; }
    public void setMaterialId(int materialId) { this.materialId = materialId; }
    
    public int getCleanerId() { return cleanerId; }
    public void setCleanerId(int cleanerId) { this.cleanerId = cleanerId; }
    
    public int getQuantityIssued() { return quantityIssued; }
    public void setQuantityIssued(int quantityIssued) { this.quantityIssued = quantityIssued; }
    
    public Timestamp getIssueDate() { return issueDate; }
    public void setIssueDate(Timestamp issueDate) { this.issueDate = issueDate; }
    
    public int getIssuedByUserId() { return issuedByUserId; }
    public void setIssuedByUserId(int issuedByUserId) { this.issuedByUserId = issuedByUserId; }
}
