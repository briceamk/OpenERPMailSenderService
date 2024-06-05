package cm.xenonit.gelodia.openerpmailsender.security.security;

import cm.xenonit.gelodia.openerpmailsender.security.domain.User;
import cm.xenonit.gelodia.openerpmailsender.security.exception.UserNotFoundException;
import cm.xenonit.gelodia.openerpmailsender.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException("User with email not found. Please try again."));
        //TODO count number of failure findByEmail in a try catch block and Implement brute force attack here
        return new CustomUserDetails(user);
    }
}
