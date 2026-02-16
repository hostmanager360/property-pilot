package com.propertypilot.registration_service.service;

import com.propertypilot.registration_service.exception.*;
import com.propertypilot.registration_service.model.*;
import com.propertypilot.registration_service.repository.FirstAccessStepRepository;
import com.propertypilot.registration_service.repository.RoleRepository;
import com.propertypilot.registration_service.repository.UserRepository;
import com.propertypilot.registration_service.repository.VerificationTokenRepository;
import com.propertypilot.registration_service.util.CurrentUserProvider;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final FirstAccessStepRepository firstAccessStepRepository;
    private final PasswordEncoder passwordEncoder;
    private final SenEmailService sendEmailService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final CurrentUserProvider currentUserProvider;

    /* ============================================================
       VALIDAZIONI
       ============================================================ */

    private void validatePassword(UserDto dto) {
        if (dto == null) {
            throw new ValidationException("Payload mancante");
        }
        if (dto.getPassword() == null || dto.getConfermaPassword() == null) {
            throw new PasswordMismatchException("Password e conferma password sono obbligatorie");
        }
        if (!dto.getPassword().equals(dto.getConfermaPassword())) {
            log.warn("Password mismatch per email {}", dto.getEmail());
            throw new PasswordMismatchException("Le password non coincidono");
        }
        if (dto.getPassword().length() < 8) {
            throw new ValidationException("La password deve contenere almeno 8 caratteri");
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

    private String normalizeEmail(String email) {
        if (email == null) return null;
        return email.trim().toLowerCase(Locale.ROOT);
    }

    public boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(regex);
    }

    /* ============================================================
       HELPERS (DB)
       ============================================================ */

    private Role getRoleOrThrow(String code) {
        return roleRepository.findByCode(code)
                .orElseThrow(() -> new RoleNotFoundException("Ruolo non trovato: " + code));
    }

    private FirstAccessStep getStepOrThrow(String code) {
        return firstAccessStepRepository.findByCode(code)
                .orElseThrow(() -> new StepNotFoundException("Step non trovato: " + code));
    }

    private FirstAccessStep getInitialStep() {
        return getStepOrThrow("COMPLETE_USER_DETAIL");
    }

    /* ============================================================
       EMAIL ACTIVATION
       ============================================================ */

    private void sendActivationEmail(User user, String rawPassword) {

        // rawPassword arriva dal FE: non loggarla mai
        if (user == null || user.getEmail() == null) {
            throw new EmailSendException("Utente non valido per invio email");
        }

        try {
            String token = UUID.randomUUID().toString();

            VerificationToken verificationToken = new VerificationToken();
            verificationToken.setToken(token);
            verificationToken.setUser(user);
            verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));

            verificationTokenRepository.save(verificationToken);

            String link = "http://localhost:8082/api/users/verify?token=" + token;

            String displayName = displayNameFromEmail(user.getEmail());

            sendEmailService.sendVerificationEmailWithInitialPassword(
                    user.getEmail(),
                    displayName,
                    user.getEmail(),
                    rawPassword,
                    link
            );

            log.info("Email di attivazione inviata a {}", user.getEmail());

        } catch (Exception e) {
            log.error("Errore durante invio email attivazione a {}: {}", user.getEmail(), e.getMessage(), e);
            throw new EmailSendException("Errore durante l'invio dell'email di attivazione");
        }
    }

    /**
     * Nome “fallback” da email:
     * - mario.rossi -> Mario Rossi
     * - gabriele_totaro -> Gabriele Totaro
     */
    private String displayNameFromEmail(String email) {
        if (email == null || !email.contains("@")) return "Utente";

        String local = email.substring(0, email.indexOf("@")).trim();
        if (local.isBlank()) return "Utente";

        local = local.replace(".", " ")
                .replace("_", " ")
                .replace("-", " ")
                .replaceAll("\\s+", " ")
                .trim();

        String[] parts = local.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p.isBlank()) continue;
            sb.append(p.substring(0, 1).toUpperCase(Locale.ROOT));
            if (p.length() > 1) sb.append(p.substring(1).toLowerCase(Locale.ROOT));
            sb.append(" ");
        }
        String out = sb.toString().trim();
        return out.isBlank() ? "Utente" : out;
    }

    /* ============================================================
       BUILD USER
       ============================================================ */

    private User buildUser(UserDto dto, Role role, String tenantKey, boolean firstAccessCompleted) {
        User user = new User();
        user.setEmail(normalizeEmail(dto.getEmail()));
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoleEntity(role);

        // IMPORTANTI: la tua logica
        user.setEnabled(false);
        user.setTenantKey(tenantKey);
        user.setPasswordResetRequired(true);
        user.setFirstAccessCompleted(firstAccessCompleted);

        return user;
    }

    private User createUserCommon(UserDto dto, Role role, String tenantKey, boolean firstAccessCompleted, FirstAccessStep firstAccessStep) {

        validatePassword(dto);

        String email = normalizeEmail(dto.getEmail());
        validateEmailFormat(email);
        validateEmailUniqueness(email);

        User user = buildUser(dto, role, tenantKey, firstAccessCompleted);
        user.setFirstAccessStep(firstAccessStep);

        return userRepository.save(user);
    }

    /* ============================================================
       REGISTRAZIONE OWNER
       ============================================================ */

    @Override
    @Transactional
    public User registerUser(UserDto dto) {

        String email = normalizeEmail(dto.getEmail());
        log.info("Registrazione nuovo OWNER: {}", email);

        Role role = getRoleOrThrow(dto.getRole()); // nel FE probabilmente "OWNER"
        FirstAccessStep step = getStepOrThrow("DASHBOARD");

        User user = createUserCommon(dto, role, null, true, step);

        // invio email con password iniziale (raw)
        sendActivationEmail(user, dto.getPassword());

        return user;
    }

    /* ============================================================
       CREAZIONE ADMIN
       ============================================================ */

    @Override
    @Transactional
    public User createAdmin(UserDto dto, String tenantKey) {

        User creator = currentUserProvider.getCurrentUserOrThrow();
        String creatorRole = creator.getRoleEntity().getCode();

        log.info("Creazione ADMIN da parte di {} ({})", creator.getEmail(), creatorRole);

        if (!creatorRole.equals("OWNER") && !creatorRole.equals("ADMIN")) {
            throw new ForbiddenException("Solo OWNER può creare ADMIN");
        }

        // identico alla tua regola
        String finalTenantKey = creatorRole.equals("OWNER") ? null : tenantKey;
        if (!creatorRole.equals("OWNER")) {
            validateTenantKey(tenantKey);
        }

        Role adminRole = getRoleOrThrow("ADMIN");
        FirstAccessStep step = getStepOrThrow("CREATE_TENANT");

        User user = createUserCommon(dto, adminRole, finalTenantKey, false, step);

        sendActivationEmail(user, dto.getPassword());

        return user;
    }

    /* ============================================================
       CREAZIONE HOST
       ============================================================ */

    @Override
    @Transactional
    public User createHost(UserDto dto, String tenantKey) {

        User creator = currentUserProvider.getCurrentUserOrThrow();
        String creatorRole = creator.getRoleEntity().getCode();

        log.info("Creazione HOST da parte di {} ({})", creator.getEmail(), creatorRole);

        if (!creatorRole.equals("OWNER") && !creatorRole.equals("ADMIN")) {
            throw new ForbiddenException("Solo OWNER o ADMIN possono creare HOST");
        }

        validateTenantKey(tenantKey);

        Role hostRole = getRoleOrThrow("HOST");
        FirstAccessStep step = getInitialStep();

        User user = createUserCommon(dto, hostRole, tenantKey, false, step);

        sendActivationEmail(user, dto.getPassword());

        return user;
    }

    /* ============================================================
       CREAZIONE COHOST
       ============================================================ */

    @Override
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

        validateTenantKey(tenantKey);

        Role cohostRole = getRoleOrThrow("COHOST");
        FirstAccessStep step = getInitialStep();

        User user = createUserCommon(dto, cohostRole, tenantKey, false, step);

        sendActivationEmail(user, dto.getPassword());

        return user;
    }

    /* ============================================================
       PASSWORD RESET
       ============================================================ */

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO dto) {

        String email = normalizeEmail(dto.getEmail());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidEmailException("Utente non trovato"));

        log.info("Richiesta reset password per {}", email);

        String token = UUID.randomUUID().toString();

        user.setResetPasswordToken(token);
        user.setResetPasswordExpiresAt(LocalDateTime.now().plusHours(24));
        user.setPasswordResetRequired(true);

        userRepository.save(user);

        try {
            String link = "http://localhost:4200/reset-password-final?token=" + token;
            sendEmailService.sendResetPasswordEmail(user.getEmail(), user.getEmail(), link);
        } catch (Exception e) {
            log.error("Errore invio email reset password a {}: {}", user.getEmail(), e.getMessage(), e);
            throw new EmailSendException("Errore durante l'invio dell'email di reset password");
        }
    }

    @Override
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
            user.setFirstAccessStep(getStepOrThrow("CREATE_TENANT"));
        } else {
            user.setFirstAccessStep(getInitialStep());
        }

        user.setResetPasswordToken(null);
        user.setResetPasswordExpiresAt(null);

        userRepository.save(user);
    }

    @Override
    public void validateResetPasswordToken(String token) {

        User user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new TokenNotFoundException("Token non valido"));

        if (user.getResetPasswordExpiresAt() == null ||
                user.getResetPasswordExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException("Token scaduto");
        }
    }
}
