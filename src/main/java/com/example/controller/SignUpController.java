package com.example.controller;

import com.example.Exceptions.EmptyCredentialsException;
import com.example.Exceptions.InvalidCharactersException;
import com.example.Exceptions.LoginOrPasswordTooLongException;
import com.example.Hibernate.HibernateSessionFactory;
import com.example.ObjectsDataBase.User;
import com.example.javafxFxmlLoader.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.hibernate.Session;

import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class SignUpController {

    @FXML
    private Button BackToLoginPageButton;

    @FXML
    private PasswordField LoginPassword;

    @FXML
    private TextField LoginUsername;

    @FXML
    private Button RegisterButton;

    @FXML
    private TextField name;

    @FXML
    void initialize() {
        setupBackToLoginPageButton();
       setupRegisterButton();


}
    public void setupBackToLoginPageButton (){
        BackToLoginPageButton.setOnAction(actionEvent -> {

            BackToLoginPageButton.getScene().getWindow().hide();
            SceneSwitcher.SceneSwitcher("/Login.fxml");

        });
    }
    public void setupRegisterButton(){
        RegisterButton.setOnAction(actionEvent -> {

            try {
                if(LoginUsername.getText().length() > 25 && LoginPassword.getText().length() > 20){
                    throw new LoginOrPasswordTooLongException("User are trying to enter " +
                            "a username or password that is too long. " +
                            "The login must not be longer than 25 characters, " +
                            "and the password must not be longer than 25 characters ");
                }
                if (LoginUsername.getText().isEmpty() || LoginPassword.getText().isEmpty()){
                    throw new EmptyCredentialsException("The user did not enter a username or password");
                }
                if (LoginUsername.getText().matches(".*[а-яА-Я].*") || LoginPassword.getText().matches(".*[а-яА-Я].*")){
                    throw new InvalidCharactersException("The user is trying to enter invalid characters");
                }

                if (!isUserExist()){
                    registerUser();
                    RegisterButton.getScene().getWindow().hide();
                    Stage LoginStage = SceneSwitcher.SceneSwitcher("/Login.fxml");
                    Stage SuccessfulSignUp = SceneSwitcher.SceneSwitcher("/SuccessfulSignUp.fxml");
                }
                else {
                    Stage DeniedSignUp = SceneSwitcher.SceneSwitcher("/DeniedSignUp.fxml");
                }
            }
            catch (EmptyCredentialsException e){
                System.out.println(e.getMessage());
                SceneSwitcher.SceneSwitcher("/DeniedLoginOrSignUpEmptyLoginOrPassword.fxml");
            }
            catch (InvalidCharactersException e){
                System.out.println(e.getMessage());
                SceneSwitcher.SceneSwitcher("/DeniedSignUpInvalidCharactersException.fxml");
            }
            catch (LoginOrPasswordTooLongException e){
                System.out.println(e.getMessage());
                SceneSwitcher.SceneSwitcher("/DeniedSignUpToLoongCharacters.fxml");
            }
        });

        LoginPassword.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode()== KeyCode.ENTER){
                RegisterButton.fire();
            }
        });
        LoginUsername.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode()== KeyCode.ENTER){
                RegisterButton.fire();
            }
        });
        name.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode()== KeyCode.ENTER){
                RegisterButton.fire();
            }
        });
    }

    public boolean isUserExist(){
        Session sessionIsUserExist = null;
        List<User> usersForQuery = new ArrayList<>();
        try {
            sessionIsUserExist = HibernateSessionFactory.getCurrentSessionUser();
            sessionIsUserExist.beginTransaction();
            System.out.println("Session check user in database is open");
            String sqlQuery = "from User where loginUsername =:login";
            Query checkUserQuery = sessionIsUserExist.createQuery(sqlQuery);
            checkUserQuery.setParameter("login", LoginUsername.getText());

           usersForQuery = checkUserQuery.getResultList();
        }
        finally {
            if(sessionIsUserExist != null && sessionIsUserExist.isOpen()){
                sessionIsUserExist.close();
                System.out.println("Session check user in database is closed");
            }
        }
        return usersForQuery.isEmpty() ? false :true;
    }

    public void registerUser(){
        Session sessionSaveUser = null;
        try {
            sessionSaveUser = HibernateSessionFactory.getCurrentSessionUser();
            sessionSaveUser.beginTransaction();
            System.out.println("Session register user in database is open");
            User user = new User(LoginUsername.getText(),LoginPassword.getText());
            sessionSaveUser.save(user);
            sessionSaveUser.getTransaction().commit();
        }
        finally {
            if(sessionSaveUser != null && sessionSaveUser.isOpen()){
                sessionSaveUser.close();
                System.out.println("Session register user in database is closed");
            }
        }

    }

}
