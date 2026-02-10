package com.propertypilot.registration_service.service;

import com.propertypilot.registration_service.exception.*;
import com.propertypilot.registration_service.model.*;
import com.propertypilot.registration_service.repository.FirstAccessStepRepository;
import com.propertypilot.registration_service.repository.RoleRepository;
import com.propertypilot.registration_service.repository.UserRepository;
import com.propertypilot.registration_service.repository.VerificationTokenRepository;
import com.propertypilot.registration_service.util.CurrentUserProvider;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
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
        /* ============================================================
       VALIDAZIONI
       ============================================================ */

    private void validatePassword(UserDto dto) {
        if (!dto.getPassword().equals(dto.getConfermaPassword())) {
            log.warn("Password mismatch per email {}", dto.getEmail());
            throw new PasswordMismatchException("Le password non coincidono");
        }
    }

    private void validateTenantKey(String tenantKey) {
        if (tenantKey == null || tenantKey.isBlank()) {
            log.warn("TenantKey mancante o non valido");
            throw new InvalidTenantException("TenantKey mancante o non valido");
        }
    }

    private void validateEmailUniqueness(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            log.warn("Tentativo di registrazione con email già esistente: {}", email);
            throw new EmailAlreadyExistsException("Email già registrata");
        }
    }

    private void validateEmailFormat(String email) {
        if (!isValidEmail(email)) {
            log.warn("Email non valida: {}", email);
            throw new InvalidEmailException("Email non valida");
        }
    }

    /* ============================================================
       HELPERS
       ============================================================ */

    private Role getRoleOrThrow(String code) {
        return roleRepository.findByCode(code)
                .orElseThrow(() -> new RoleNotFoundException("Ruolo non trovato: " + code));
    }

    private FirstAccessStep getStep(String code) {
        return firstAccessStepRepository.findByCode(code)
                .orElseThrow(() -> new StepNotFoundException("Step non trovato: " + code));
    }

    private FirstAccessStep getInitialStep() {
        return getStep("COMPLETE_USER_DETAIL");
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

            log.info("Email di attivazione inviata a {}", user.getEmail());

        } catch (Exception e) {
            log.error("Errore durante invio email attivazione a {}", user.getEmail(), e);
            throw new EmailSendException("Errore durante l'invio dell'email di attivazione");
        }
    }

    private User buildUser(UserDto dto, Role role, String tenantKey, boolean firstAccessCompleted) {
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoleEntity(role);
        user.setEnabled(false);
        user.setTenantKey(tenantKey);
        user.setPasswordResetRequired(true);
        user.setFirstAccessCompleted(firstAccessCompleted);
        return user;
    }

    private User createUserCommon(UserDto dto, Role role, String tenantKey) {
        validatePassword(dto);
        validateEmailUniqueness(dto.getEmail());
        validateEmailFormat(dto.getEmail());

        User user = buildUser(dto, role, tenantKey, false);
        return userRepository.save(user);
    }

    public boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(regex);
    }

    /* ============================================================
       REGISTRAZIONE OWNER
       ============================================================ */

    @Transactional
    public User registerUser(UserDto dto) {

        log.info("Registrazione nuovo OWNER: {}", dto.getEmail());

        validatePassword(dto);
        validateEmailUniqueness(dto.getEmail());
        validateEmailFormat(dto.getEmail());

        Role role = getRoleOrThrow(dto.getRole());

        User user = buildUser(dto, role, null, true);
        user.setFirstAccessStep(getStep("DASHBOARD"));

        userRepository.save(user);
        sendActivationEmail(user);

        return user;
    }

    /* ============================================================
       CREAZIONE ADMIN
       ============================================================ */

    @Transactional
    public User createAdmin(UserDto dto, String tenantKey) {

        User creator = currentUserProvider.getCurrentUserOrThrow();
        String creatorRole = creator.getRoleEntity().getCode();

        log.info("Creazione ADMIN da parte di {} ({})", creator.getEmail(), creatorRole);

        if (!creatorRole.equals("OWNER") && !creatorRole.equals("ADMIN")) {
            throw new ForbiddenException("Solo OWNER può creare ADMIN");
        }

        validatePassword(dto);
        validateEmailUniqueness(dto.getEmail());
        validateEmailFormat(dto.getEmail());

        String finalTenantKey = creatorRole.equals("OWNER") ? null : tenantKey;

        if (!creatorRole.equals("OWNER")) {
            validateTenantKey(tenantKey);
        }

        Role adminRole = getRoleOrThrow("ADMIN");
        User user = buildUser(dto, adminRole, finalTenantKey, false);
        user.setFirstAccessStep(getStep("CREATE_TENANT"));

        userRepository.save(user);
        sendActivationEmail(user);

        return user;
    }

    /* ============================================================
       CREAZIONE HOST
       ============================================================ */

    @Transactional
    public User createHost(UserDto dto, String tenantKey) {

        User creator = currentUserProvider.getCurrentUserOrThrow();
        String creatorRole = creator.getRoleEntity().getCode();

        log.info("Creazione HOST da parte di {} ({})", creator.getEmail(), creatorRole);

        if (!creatorRole.equals("OWNER") && !creatorRole.equals("ADMIN")) {
            throw new ForbiddenException("Solo OWNER o ADMIN possono creare HOST");
        }

        validatePassword(dto);
        validateTenantKey(tenantKey);
        validateEmailUniqueness(dto.getEmail());
        validateEmailFormat(dto.getEmail());

        Role hostRole = getRoleOrThrow("HOST");
        User user = buildUser(dto, hostRole, tenantKey, false);
        user.setFirstAccessStep(getInitialStep());

        userRepository.save(user);
        sendActivationEmail(user);

        return user;
    }

    /* ============================================================
       CREAZIONE COHOST
       ============================================================ */

    @Transactional
    public User createCohost(UserDto dto, String tenantKey) {

        User creator = currentUserProvider.getCurrentUserOrThrow();
        String creatorRole = creator.getRoleEntity().getCode();

        log.info("Creazione COHOST da parte di {} ({})", creator.getEmail(), creatorRole);

        if (!creatorRole.equals("OWNER") &&
                !creatorRole.equals("ADMIN") &&
                !creatorRole.equals("HOST")) {

            throw new ForbiddenException("Solo OWNER, ADMIN o HOST possono creare COHOST");
        }

        validatePassword(dto);
        validateTenantKey(tenantKey);
        validateEmailUniqueness(dto.getEmail());
        validateEmailFormat(dto.getEmail());

        Role cohostRole = getRoleOrThrow("COHOST");
        User user = buildUser(dto, cohostRole, tenantKey, false);
        user.setFirstAccessStep(getInitialStep());

        userRepository.save(user);
        sendActivationEmail(user);

        return user;
    }

    /* ============================================================
       PASSWORD RESET
       ============================================================ */

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidEmailException("Utente non trovato"));

        log.info("Richiesta reset password per {}", dto.getEmail());

        String token = UUID.randomUUID().toString();

        user.setResetPasswordToken(token);
        user.setResetPasswordExpiresAt(LocalDateTime.now().plusHours(24));
        user.setPasswordResetRequired(true);

        userRepository.save(user);

        try {
            String link = "http://localhost:4200/reset-password-final?token=" + token;
            sendEmailService.sendResetPasswordEmail(user.getEmail(), user.getEmail(), link);
        } catch (Exception e) {
            log.error("Errore invio email reset password a {}", user.getEmail(), e);
            throw new EmailSendException("Errore durante l'invio dell'email di reset password");
        }
    }

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

        log.info("Reset password per {}", user.getEmail());

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setPasswordResetRequired(false);

        if ("OWNER".equalsIgnoreCase(user.getRoleEntity().getCode())) {
            user.setFirstAccessCompleted(true);
        } else {
            user.setFirstAccessCompleted(false);
        }

        if ("ADMIN".equalsIgnoreCase(user.getRoleEntity().getCode())) {
            user.setFirstAccessStep(getStep("CREATE_TENANT"));
        } else {
            user.setFirstAccessStep(getInitialStep());
        }

        user.setResetPasswordToken(null);
        user.setResetPasswordExpiresAt(null);

        userRepository.save(user);
    }
    public void validateResetPasswordToken(String token) {

        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Token non valido"));

        if (user.getResetPasswordExpiresAt() == null ||
                user.getResetPasswordExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token scaduto");
        }
    }
}

