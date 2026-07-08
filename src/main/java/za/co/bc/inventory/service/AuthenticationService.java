package za.co.bc.inventory.service;

import za.co.bc.inventory.dao.UserDAO;
import za.co.bc.inventory.model.User;

public class AuthenticationService {

    private final UserDAO userDAO = new UserDAO();

    public boolean register(User user) {

        if (userDAO.usernameExists(user.getUsername())) {
            System.out.println("Username already exists.");
            return false;
        }

        if (userDAO.emailExists(user.getEmail())) {
            System.out.println("Email already exists.");
            return false;
        }

        return userDAO.registerUser(user);
    }

    public User login(String username, String password) {

        return userDAO.login(username, password);
    }

}