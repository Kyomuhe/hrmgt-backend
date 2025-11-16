package com.ceres.project.services.auth;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.project.config.ApplicationConf;
import com.ceres.project.config.JwtUtility;
import com.ceres.project.models.database.SystemUserModel;
import com.ceres.project.repositories.SystemUserRepository;
import com.ceres.project.services.base.BaseWebActionsService;
import com.ceres.project.utils.OperationReturnObject;
import com.ceres.project.utils.mail.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class AuthService extends BaseWebActionsService {
    private final AuthenticationManager authenticationManager;
    private final ApplicationConf userDetailService;
    private final JwtUtility jwtUtility;
    private final PasswordEncoder passwordEncoder;
    private final SystemUserRepository systemUserRepository;
    private final MailService  mailService;

    OperationReturnObject res = new OperationReturnObject();

    private OperationReturnObject login(JSONObject request){
        requires(request,"username","password");
        String username= request.getString("username");
        String password= request.getString("password");

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        final SystemUserModel userDetails = userDetailService.loadUserByUsername(username);
        final String token = jwtUtility.generateToken(userDetails);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", userDetails);

//        OperationReturnObject res = new OperationReturnObject();
        res.setReturnCodeAndReturnMessage(0, "Welcome back " + userDetails.getUsername());
        res.setReturnObject(response);

        return res;
    }

    private OperationReturnObject signup(JSONObject request) {
        try {
            requires(request,"username","password");
            String username = request.getString("username");
            String password = request.getString("password");
            String email = request.getString("email");
            String firstName = request.getString("firstName");
            String lastName = request.getString("lastName");
            String department = request.getString("department");
            String role = request.getString("role");
            Long employeeId = request.getLong("employeeId");

            if (username == null || username.isEmpty()) {
                return createResponse("Username is either null or empty");
            }
            if (password == null || password.isEmpty()) {
                return createResponse("Password is either null or empty");
            }
            if (email == null || email.isEmpty()) {
                return createResponse("Email is either null or empty");
            }
            SystemUserModel existingUser = systemUserRepository.findFirstByUsername(username);
            if(existingUser != null){
                return createResponse("Username is already taken");
            }


            SystemUserModel user = new SystemUserModel();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(password));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setRoleCode("USER");
            user.setDepartment(department);
            user.setRole(role);
            user.setEmployeeId(employeeId);
            user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            user.setIsActive(true);


            SystemUserModel savedUser = systemUserRepository.save(user);

            final String accessToken = jwtUtility.generateToken(savedUser);

            Map<String, Object> response = new HashMap<>();
            response.put("token", accessToken);
            response.put("user", savedUser);

            res.setReturnCodeAndReturnMessage(0, "Account created successfully for " + savedUser.getUsername());
            res.setReturnObject(response);

            return res;

        } catch (RuntimeException e) {
            return createResponse("Failed to create account: " + e.getMessage());
        }
    }

    public OperationReturnObject changeUserRole(JSONObject request){
        try{
            Long userId = request.getLong("userId");
            String role = request.getString("roleCode");

            if(userId == null){
                return createResponse("user id is null");
            }

            Optional<SystemUserModel> userOptional = systemUserRepository.findById(userId);

            if(!userOptional.isPresent()){
                return createResponse("this user does not exist");
            }

            SystemUserModel user = userOptional.get();
            user.setRoleCode(role);
            systemUserRepository.save(user);

            res.setReturnCodeAndReturnMessage(0, "changed role successfully");
            return res;

        }catch(Exception e){
            return createResponse(e.getMessage());
        }
    }

    public OperationReturnObject forgotPassword(JSONObject request){
        try {
            String email = request.getString("email");
            SystemUserModel existingUser = systemUserRepository.findFirstByEmail(email);

            if (existingUser == null) {
                return createResponse("the provided email does not exist");
            }
            String otp = generateOtp();
            LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);

            existingUser.setOtp(otp);
            existingUser.setOtpExpiration(expirationTime);
            systemUserRepository.save(existingUser);

            mailService.sendOtpEmail(email, otp);

            res.setReturnCodeAndReturnMessage(0, "sent otp");
            return res;

        }catch (Exception e){
            e.printStackTrace();
            return createResponse("oops sth unexpected happened");
        }

    }
    public String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public OperationReturnObject verifyOtp(JSONObject request ){
        try{
            String email = request.getString("email");
            String inputOtp = request.getString("inputOtp");

            SystemUserModel existingUser = systemUserRepository.findFirstByEmail(email);
            if(existingUser == null){
                return createResponse("the provided email does not exist");
            }
            if(existingUser == null || !existingUser.getOtp().equals(inputOtp)){
                return createResponse("invalid otp");
            }

            if(existingUser.getOtpExpiration().isBefore(LocalDateTime.now())){
                return createResponse("otp expired");
            }

            res.setReturnCodeAndReturnMessage(0, "otp verified");
            return res;
        }catch (Exception e){
            return createResponse("oops sth unexpected happened");
        }
    }

    public OperationReturnObject resetPassword(JSONObject request){
        try {
            String email = request.getString("email");

            if (email == null || email.isEmpty()) {
                return createResponse("email is required");
            }
            SystemUserModel existingUser = systemUserRepository.findFirstByEmail(email);

            if (existingUser == null) {
                return createResponse("the provided email does not exist");
            }

            String password = request.getString("password");

            if(password == null || password.isEmpty()) {
                return createResponse("the new password is required");
            }

            password = passwordEncoder.encode(password);
            existingUser.setPassword(password);
            systemUserRepository.save(existingUser);

            res.setReturnCodeAndReturnMessage(0, "password reset successfully");
            return res;


        }catch(Exception e){
            return createResponse("oops sth unexpected happened");
        }
    }
    public OperationReturnObject displayAllUsers (){
        try{
            List<SystemUserModel> users = systemUserRepository.findAll();
            res.setCodeAndMessageAndReturnObject(0,"users displayed successfully", users);
            return res;

        }catch (Exception e){
            return createResponse(e.getMessage());
        }
    }


    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) {
        return switch (action){
            case "login" -> login(request);
            case "signup" -> signup(request);
            case "forgotPassword" -> forgotPassword(request);
            case "verifyOtp" -> verifyOtp(request);
            case "resetPassword" -> resetPassword(request);
            case "displayAllUsers" -> displayAllUsers();
            case "changeRole" -> changeUserRole(request);
            default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
        };
    }
}
