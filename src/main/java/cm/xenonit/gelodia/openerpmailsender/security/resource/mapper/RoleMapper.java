package cm.xenonit.gelodia.openerpmailsender.security.resource.mapper;

import cm.xenonit.gelodia.openerpmailsender.security.domain.Role;
import cm.xenonit.gelodia.openerpmailsender.security.domain.User;
import cm.xenonit.gelodia.openerpmailsender.security.generated.resource.dto.RegisterUserDto;
import cm.xenonit.gelodia.openerpmailsender.security.resource.dto.RoleDto;
import cm.xenonit.gelodia.openerpmailsender.security.resource.dto.UserDto;
import org.springframework.beans.BeanUtils;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
public class RoleMapper {

    public static RoleDto fromRole(Role role) {
        RoleDto roleDto = new RoleDto();
        BeanUtils.copyProperties(role, roleDto);
        return roleDto;
    }

    public static Set<RoleDto> fromRoles(Set<Role> roles) {
        return roles.stream().map(RoleMapper::fromRole).collect(Collectors.toSet());
    }


}
