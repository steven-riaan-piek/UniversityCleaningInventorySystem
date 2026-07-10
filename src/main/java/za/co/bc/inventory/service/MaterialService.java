package za.co.bc.inventory.service;

import java.util.List;

import za.co.bc.inventory.dao.MaterialDAO;
import za.co.bc.inventory.model.Material;

public class MaterialService {

    private MaterialDAO materialDAO;


     //Constructor

    public MaterialService() {
        materialDAO = new MaterialDAO();
    }


     // Returns all materials.

    public List<Material> getAllMaterials() {
        return materialDAO.getAllMaterials();
    }


     // Returns one material by ID.

    public Material getMaterialById(int id) {
        return materialDAO.getMaterialById(id);
    }


     // Adds a new material.

    public boolean addMaterial(Material material) {

        // Validation
        if (material == null) {
            return false;
        }

        if (material.getName() == null || material.getName().trim().isEmpty()) {
            System.out.println("Material name is required.");
            return false;
        }

        if (material.getQuantity() < 0) {
            System.out.println("Quantity cannot be negative.");
            return false;
        }

        if (material.getStatus() == null || material.getStatus().trim().isEmpty()) {
            material.setStatus("Available");
        }

        return materialDAO.addMaterial(material);
    }


     // Updates an existing material.


    public boolean updateMaterial(Material material) {

        if (material == null) {
            return false;
        }

        if (material.getId() <= 0) {
            System.out.println("Invalid Material ID.");
            return false;
        }

        if (material.getName() == null || material.getName().trim().isEmpty()) {
            System.out.println("Material name is required.");
            return false;
        }

        if (material.getQuantity() < 0) {
            System.out.println("Quantity cannot be negative.");
            return false;
        }

        return materialDAO.updateMaterial(material);
    }


     //Deletes a material.

    public boolean deleteMaterial(int id) {

        if (id <= 0) {
            return false;
        }

        return materialDAO.deleteMaterial(id);
    }


     // Searches materials by name.

    public List<Material> searchMaterials(String keyword) {

        if (keyword == null) {
            keyword = "";
        }

        return materialDAO.searchMaterials(keyword.trim());
    }


     // Determines the stock status based on quantity.

    public String determineStatus(int quantity) {

        if (quantity <= 0) {
            return "Out Of Stock";
        }

        if (quantity <= 10) {
            return "Low Stock";
        }

        return "Available";
    }
}