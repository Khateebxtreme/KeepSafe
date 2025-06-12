package com.keepsafe.notes.controller;

import com.keepsafe.notes.models.AppRole;
import com.keepsafe.notes.models.Role;
import com.keepsafe.notes.models.User;
import com.keepsafe.notes.repositories.RoleRepository;
import com.keepsafe.notes.repositories.UserRepository;
import com.keepsafe.notes.security.JWT.JwtUtils;
import com.keepsafe.notes.security.request.LoginRequest;
import com.keepsafe.notes.security.request.SignupRequest;
import com.keepsafe.notes.security.response.LoginResponse;
import com.keepsafe.notes.security.response.MessageResponse;
import com.keepsafe.notes.security.response.UserInfoResponse;
import com.keepsafe.notes.security.services.UserDetailsImpl;
import com.keepsafe.notes.services.TotpService;
import com.keepsafe.notes.services.UserService;
import com.keepsafe.notes.utils.AuthUtil;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    //This controller handles all the authentication related tasks for our application

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserService userService;

    @Autowired
    AuthUtil authUtil;

    @Autowired
    TotpService totpService;

    @PostMapping("/public/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        //login endpoint process starts after request is mapped to LoginRequest DTO class.
        Authentication authentication; //setting up the authentication object.

        try {
            //Trying to authenticate the user who sent the request using his credentials. A token is generated upon successful authentication and this is passed to the authentication manager to set up the authentication object using the token.
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            //handling errors if there is any issue in the above process -> we are sending back error response to the user.
            Map<String, Object> map = new HashMap<>();
            map.put("message", "Bad credentials");
            map.put("status", false);
            return new ResponseEntity<Object>(map, HttpStatus.NOT_FOUND);
        }

//      set the authentication to our security context using the authentication object. We establish security context for the current user session, this step officially marks the user as authenticated.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        //creating the object of UserDetails from the authentication object.
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //once we have user details, we generate JWT from it.
        String jwtToken = jwtUtils.generateTokenFromUsername(userDetails);

        // Collect roles from the UserDetails
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        // Prepare the response body, now including the JWT token directly in the body
        LoginResponse response = new LoginResponse(userDetails.getUsername(), roles, jwtToken);

        // Return the response entity with the JWT token included in the response body
        return ResponseEntity.ok(response);
    }

    @PostMapping("/public/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        //Endpoint to register a user into the application. The data would be coming in request body as per mentioned. The structure of Request is defined by Signup Request class.

        //checking if the username or email coming in from the register request is already available in the system or not, only if it is a new user then we move on to further process otherwise an exception is raised.
        if (userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account if the above checks passes with the credentials coming from the request.
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        //determining and assigning the roles based on register request
        Set<String> strRoles = signUpRequest.getRole();
        Role role; //Role object is created to assign it to the user by collecting information from the incoming request.

        if (strRoles == null || strRoles.isEmpty()) {
            //if role is not provided by the user, we are setting the role object up as the default user role, otherwise if role is found, we assign the role that is passed down from the request.
            role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        } else {
            String roleStr = strRoles.iterator().next();
            if (roleStr.equals("admin")) {
                role = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            } else {
                role = roleRepository.findByRoleName(AppRole.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            }

            //setting up some default user account properties
            user.setAccountNonLocked(true);
            user.setAccountNonExpired(true);
            user.setCredentialsNonExpired(true);
            user.setEnabled(true);
            user.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
            user.setAccountExpiryDate(LocalDate.now().plusYears(1));
            user.setTwoFactorEnabled(false);
            user.setSignUpMethod("email");
        }
        user.setRole(role);
        userRepository.save(user); //saving the created user to the database.

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(@AuthenticationPrincipal UserDetails userDetails) {
        //userDetails are coming in from the authenticated user using the AuthenticationPrincipal annotation.
        User user = userService.findByUsername(userDetails.getUsername()); //get user details and storing it in user object by searching for it using the username coming in from userDetails object.

        //gets the roles of authenticated users.
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        //collecting all user details into UserInfoResponse object and is being returned as a response.
        UserInfoResponse response = new UserInfoResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountNonLocked(),
                user.isAccountNonExpired(),
                user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getCredentialsExpiryDate(),
                user.getAccountExpiryDate(),
                user.isTwoFactorEnabled(),
                roles
        );

        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/username")
    public String currentUserName(@AuthenticationPrincipal UserDetails userDetails) {
        //get username of currently authenticated user.
        return (userDetails != null) ? userDetails.getUsername() : "";
    }

    @PostMapping("/public/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam String email){
        //This controller is used for password reset mechanism. We are getting the email of the user who forgot his password through params. We then create a password-reset token at backend and share it to the user via mail inform of a url.
        try{
            userService.generatePasswordResetToken(email);
            return ResponseEntity.ok(new MessageResponse("Password reset email sent successfully"));
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error sending mail to reset the user's password"));
        }
    }

    @PostMapping("/public/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword){
        //This controller is used for password reset mechanism. We are getting the email of the user who forgot his password through params. We then create a password-reset token at backend and share it to the user via mail inform of a url.
        try{
            userService.resetPassword(token, newPassword);
            return ResponseEntity.ok(new MessageResponse("Password reset successful"));
        }
        catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    //Controller methods for Two-Factor Authentication process.

    @PostMapping("/enable-2fa")
    public ResponseEntity<String> enable2Fa(){
        Long userId = authUtil.loggedInUserId();
        GoogleAuthenticatorKey secret = userService.generate2FASecret(userId);
        String qrCodeUrl = totpService.getQrCodeUrl(secret, userService.getUserById(userId).getUserName());
        return ResponseEntity.ok(qrCodeUrl);
    }

    @PostMapping("/disable-2fa")
    public ResponseEntity<String> disable2Fa(){
        Long userId = authUtil.loggedInUserId();
        userService.disable2FA(userId);
        return ResponseEntity.ok("2FA has been disabled");
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<String> verify2Fa(@RequestParam int code){
        Long userId = authUtil.loggedInUserId();
        boolean isValid = userService.validate2FACode(userId, code);
        if(isValid){
            userService.enable2FA(userId);
            return ResponseEntity.ok("2FA verified");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid 2FA code");
        }
    }

    @GetMapping("/user/2fa-status")
    public ResponseEntity<?> get2FAStatus(){
        User user = authUtil.loggedInUser();
        if(user!=null){
            return ResponseEntity.ok().body(
                    Map.of("is2faEnabled", user.isTwoFactorEnabled())
            );
        }
        else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Not Found");
        }
    }

    @PostMapping("/public/verify-2fa-login")
    public ResponseEntity<String> verify2FALogin(@RequestParam String jwtToken, @RequestParam int code){
        String username = jwtUtils.getUserNameFromJwtToken(jwtToken);
        User user = userService.findByUsername(username);
        boolean isValid = userService.validate2FACode(user.getUserId(), code);
        if(isValid){
            return ResponseEntity.ok("2FA verified");
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid 2FA code");
        }
    }
}
