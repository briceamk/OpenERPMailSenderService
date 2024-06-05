package cm.xenonit.gelodia.openerpmailsender.security.security;

import cm.xenonit.gelodia.openerpmailsender.security.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static cm.xenonit.gelodia.openerpmailsender.security.constant.SecurityConstant.COMMA_SEPARATOR;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final User user;
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> role.getPermission().split(COMMA_SEPARATOR.trim()))
                .flatMap(Arrays::stream)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.getAccountNotExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getAccountNotLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.getCredentialsNotExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.getAccountEnabled();
    }

}
