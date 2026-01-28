package com.propertypilot.registration_service.service;

import com.propertypilot.registration_service.exception.*;
import com.propertypilot.registration_service.model.*;
import com.propertypilot.registration_service.repository.FirstAccessStepRepository;
import com.propertypilot.registration_service.repository.RoleRepository;
import com.propertypilot.registration_service.repository.UserRepository;
import com.propertypilot.registration_service.repository.VerificationTokenRepository;
import com.propertypilot.registration_service.util.CurrentUserProvider;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private FirstAccessStepRepository firstAccessStepRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private SenEmailService sendEmailService;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    CurrentUserProvider currentUserProvider;

    // -------------------------
    // UTILITY
    // -------------------------

    private void validatePassword(UserDto dto) {
        if (!dto.getPassword().equals(dto.getConfermaPassword())) {
            throw new PasswordMismatchException("Le password non coincidono");
        }
    }

    private void validateTenantKey(String tenantKey) {
        if (tenantKey == null || tenantKey.isBlank()) {
            throw new InvalidTenantException("TenantKey mancante o non valido");
        }
    }

    private Role getRoleOrThrow(String code) {
        return roleRepository.findByCode(code)
                .orElseThrow(() -> new RoleNotFoundException("Ruolo non trovato: " + code));
    }

    private FirstAccessStep getInitialStep() {
        return firstAccessStepRepository.findByCode("COMPLETE_USER_DETAIL")
                .orElseThrow(() -> new StepNotFoundException("Step iniziale non trovato"));
    }

    private void sendActivationEmail(User user) {
        try {
            String token = UUID.randomUUID().toString();

            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(user);
            verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));

            verificationTokenRepository.save(verificationToken);

            String link = "http://localhost:8082/api/users/verify?token=" + token;
            sendEmailService.sendVerificationEmail(user.getEmail(), user.getEmail(), link);

        } catch (Exception e) {
            throw new EmailSendException("Errore durante l'invio dell'email di attivazione");
        }
    }

    private User buildNewUser(UserDto dto, Role role, String tenantKey) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoleEntity(role);
        user.setEnabled(false);
        user.setTenantKey(tenantKey);
        user.setPasswordResetRequired(true);
        user.setFirstAccessCompleted(false);
        user.setFirstAccessStep(getInitialStep());
        return user;
    }

    // -------------------------
    // REGISTER
    // -------------------------

    @Transactional
    public User registerUser(UserDto dto) {

        validatePassword(dto);

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email già registrata");
        }
        if (!isValidEmail(dto.getEmail())) {
            throw new InvalidEmailException("Email non valida");
        }

        Role role = getRoleOrThrow(dto.getRole());
        User user = buildNewUser(dto, role, null);

        userRepository.save(user);
        sendActivationEmail(user);

        return user;
    }

    public boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(regex);
    }

    // -------------------------
    // CREATE ADMIN
    // -------------------------

    @Transactional
    public User createAdmin(UserDto dto, String tenantKey) {

        User creator = currentUserProvider.getCurrentUserOrThrow();

        if (!creator.getRoleEntity().getCode().equals("OWNER")) {
            throw new ForbiddenException("Solo OWNER può creare ADMIN");
        }

        validatePassword(dto);
        validateTenantKey(tenantKey);

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email già registrata");
        }
        if (!isValidEmail(dto.getEmail())) {
            throw new InvalidEmailException("Email non valida");
        }

        Role adminRole = getRoleOrThrow("ADMIN");
        User user = buildNewUser(dto, adminRole, tenantKey);

        userRepository.save(user);
        sendActivationEmail(user);

        return user;
    }

    // -------------------------
    // CREATE HOST
    // -------------------------

    @Transactional
    public User createHost(UserDto dto, String tenantKey) {

        User creator = currentUserProvider.getCurrentUserOrThrow();
        String creatorRole = creator.getRoleEntity().getCode();

        if (!creatorRole.equals("OWNER") && !creatorRole.equals("ADMIN")) {
            throw new ForbiddenException("Solo OWNER o ADMIN possono creare HOST");
        }

        validatePassword(dto);
        validateTenantKey(tenantKey);

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email già registrata");
        }
        if (!isValidEmail(dto.getEmail())) {
            throw new InvalidEmailException("Email non valida");
        }

        Role hostRole = getRoleOrThrow("HOST");
        User user = buildNewUser(dto, hostRole, tenantKey);

        userRepository.save(user);
        sendActivationEmail(user);

        return user;
    }

    // -------------------------
    // CREATE COHOST
    // -------------------------

    @Transactional
    public User createCohost(UserDto dto, String tenantKey) {

        User creator = currentUserProvider.getCurrentUserOrThrow();
        String creatorRole = creator.getRoleEntity().getCode();

        if (!creatorRole.equals("OWNER") &&
                !creatorRole.equals("ADMIN") &&
                !creatorRole.equals("HOST")) {

            throw new ForbiddenException("Solo OWNER, ADMIN o HOST possono creare COHOST");
        }

        validatePassword(dto);
        validateTenantKey(tenantKey);

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email già registrata");
        }
        if (!isValidEmail(dto.getEmail())) {
            throw new InvalidEmailException("Email non valida");
        }

        Role cohostRole = getRoleOrThrow("COHOST");
        User user = buildNewUser(dto, cohostRole, tenantKey);

        userRepository.save(user);
        sendActivationEmail(user);

        return user;
    }

    // -------------------------
    // FORGOT PASSWORD
    // -------------------------

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidEmailException("Utente non trovato"));

        String token = UUID.randomUUID().toString();

        user.setResetPasswordToken(token);
        user.setResetPasswordExpiresAt(LocalDateTime.now().plusHours(24));
        user.setPasswordResetRequired(true);

        userRepository.save(user);

        try {
            String link = "http://localhost:8082/api/users/resetPasswordVerifyToken?token=" + token;
            sendEmailService.sendResetPasswordEmail(user.getEmail(), user.getEmail(), link);
        } catch (Exception e) {
            throw new EmailSendException("Errore durante l'invio dell'email di reset password");
        }
    }

    // -------------------------
    // RESET PASSWORD
    // -------------------------

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO dto) {

        if (!dto.getNewPassword().equals(dto.getRepeatPassword())) {
            throw new PasswordMismatchException("Le password non coincidono");
        }

        User user = userRepository.findByResetPasswordToken(dto.getToken())
                .orElseThrow(() -> new TokenNotFoundException("Token non valido"));

        if (user.getResetPasswordExpiresAt() == null ||
                user.getResetPasswordExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token scaduto");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setPasswordResetRequired(false);
        user.setFirstAccessCompleted(false);
        user.setFirstAccessStep(getInitialStep());
        user.setResetPasswordToken(null);
        user.setResetPasswordExpiresAt(null);

        userRepository.save(user);
    }
}

