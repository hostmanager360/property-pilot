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

    @Transactional
    public User registerUser(UserDto dto) {

        validatePassword(dto);

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email già registrata");
        }
        if (!isValidEmail(dto.getEmail())) {
            throw new InvalidEmailException("Email non valida");
        }

        // 🔥 Recupero ruolo dal DB
        Role role = roleRepository.findByCode(dto.getRole())
                .orElseThrow(() -> new RuntimeException("Ruolo non valido: " + dto.getRole()));

        // 🔥 Step iniziale: COMPLETE_USER_DETAIL per tutti tranne OWNER
        FirstAccessStep step = firstAccessStepRepository.findByCode("COMPLETE_USER_DETAIL")
                .orElseThrow(() -> new RuntimeException("Step non trovato"));

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoleEntity(role);
        user.setEnabled(false);
        user.setPasswordResetRequired(true);
        user.setFirstAccessCompleted(false);
        user.setFirstAccessStep(step);

        userRepository.save(user);

        // 🔥 Genera token di verifica
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(verificationToken);

        // 🔥 Invia email
        String link = "http://localhost:8082/api/users/verify?token=" + token;
        sendEmailService.sendVerificationEmail(user.getEmail(), user.getEmail(), link);

        return user;
    }

    private void validatePassword(UserDto dto) {
        if (!dto.getPassword().equals(dto.getConfermaPassword())) {
            throw new PasswordMismatchException("Le password non coincidono");
        }
    }

    public boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email != null && email.matches(regex);
    }

    @Transactional
    public User createAdmin(UserDto dto, String tenantKey) {

        User creator = currentUserProvider.getCurrentUserOrThrow();

        if (!creator.getRoleEntity().getCode().equals("OWNER")) {
            throw new ForbiddenException("Solo OWNER può creare ADMIN");
        }

        validatePassword(dto);

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email già registrata");
        }
        if (!isValidEmail(dto.getEmail())) {
            throw new InvalidEmailException("Email non valida");
        }

        Role adminRole = roleRepository.findByCode("ADMIN")
                .orElseThrow(() -> new RuntimeException("Ruolo ADMIN non trovato"));

        FirstAccessStep step = firstAccessStepRepository.findByCode("COMPLETE_USER_DETAIL")
                .orElseThrow(() -> new RuntimeException("Step non trovato"));

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoleEntity(adminRole);
        user.setEnabled(false);
        user.setTenantKey(tenantKey);
        user.setPasswordResetRequired(true);
        user.setFirstAccessCompleted(false);
        user.setFirstAccessStep(step);

        userRepository.save(user);
        sendActivationEmail(user);
        return user;
    }


    @Transactional
    public User createHost(UserDto dto, String tenantKey) {

        User creator = currentUserProvider.getCurrentUserOrThrow();

        // 🔥 Controllo ruolo tramite RoleEntity
        String creatorRole = creator.getRoleEntity().getCode();
        if (!creatorRole.equals("OWNER") && !creatorRole.equals("ADMIN")) {
            throw new ForbiddenException("Solo OWNER o ADMIN possono creare HOST");
        }

        validatePassword(dto);

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email già registrata");
        }
        if (!isValidEmail(dto.getEmail())) {
            throw new InvalidEmailException("Email non valida");
        }

        // 🔥 Recupero ruolo HOST dal DB
        Role hostRole = roleRepository.findByCode("HOST")
                .orElseThrow(() -> new RuntimeException("Ruolo HOST non trovato"));

        // 🔥 Step iniziale: COMPLETE_USER_DETAIL
        FirstAccessStep step = firstAccessStepRepository.findByCode("COMPLETE_USER_DETAIL")
                .orElseThrow(() -> new RuntimeException("Step non trovato"));

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoleEntity(hostRole);
        user.setEnabled(false);
        user.setTenantKey(tenantKey);
        user.setPasswordResetRequired(true);
        user.setFirstAccessCompleted(false);
        user.setFirstAccessStep(step);

        userRepository.save(user);
        sendActivationEmail(user);
        return user;
    }

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

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email già registrata");
        }
        if (!isValidEmail(dto.getEmail())) {
            throw new InvalidEmailException("Email non valida");
        }

        // 🔥 Recupero ruolo COHOST dal DB
        Role cohostRole = roleRepository.findByCode("COHOST")
                .orElseThrow(() -> new RuntimeException("Ruolo COHOST non trovato"));

        // 🔥 Step iniziale: COMPLETE_USER_DETAIL
        FirstAccessStep step = firstAccessStepRepository.findByCode("COMPLETE_USER_DETAIL")
                .orElseThrow(() -> new RuntimeException("Step non trovato"));

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoleEntity(cohostRole);
        user.setEnabled(false);
        user.setTenantKey(tenantKey);
        user.setPasswordResetRequired(true);
        user.setFirstAccessCompleted(false);
        user.setFirstAccessStep(step);

        userRepository.save(user);
        sendActivationEmail(user);
        return user;
    }


    @Transactional
    public void forgotPassword(ForgotPasswordRequestDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new InvalidEmailException("Utente non trovato"));

        String token = UUID.randomUUID().toString();

        user.setResetPasswordToken(token);
        user.setResetPasswordExpiresAt(LocalDateTime.now().plusHours(24));
        user.setPasswordResetRequired(true);

        userRepository.save(user);

        String link = "http://localhost:8082/api/users/resetPasswordVerifyToken?token=" + token;

        sendEmailService.sendResetPasswordEmail(
                user.getEmail(),
                user.getEmail(),
                link
        );
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

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setPasswordResetRequired(false);

        // 🔥 Dopo reset password → obbligo primo accesso
        user.setFirstAccessCompleted(false);

        // 🔥 Step iniziale dopo reset → COMPLETE_USER_DETAIL
        FirstAccessStep step = firstAccessStepRepository.findByCode("COMPLETE_USER_DETAIL")
                .orElseThrow(() -> new RuntimeException("Step non trovato"));
        user.setFirstAccessStep(step);

        user.setResetPasswordToken(null);
        user.setResetPasswordExpiresAt(null);

        userRepository.save(user);
    }
    private void sendActivationEmail(User user) {
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiresAt(LocalDateTime.now().plusHours(24));

        verificationTokenRepository.save(verificationToken);

        String link = "http://localhost:8082/api/users/verify?token=" + token;

        sendEmailService.sendVerificationEmail(
                user.getEmail(),
                user.getEmail(),
                link
        );
    }

}

