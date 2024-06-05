package cm.xenonit.gelodia.openerpmailsender.security.resource.mapper;

import cm.xenonit.gelodia.openerpmailsender.security.domain.Role;
import cm.xenonit.gelodia.openerpmailsender.security.domain.User;
import cm.xenonit.gelodia.openerpmailsender.security.generated.resource.dto.RegisterUserDto;
import cm.xenonit.gelodia.openerpmailsender.security.resource.dto.UserDto;
import org.springframework.beans.BeanUtils;

import java.util.Optional;


/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
public class UserMapper {

    public static User fromRegisterNewUserDto(RegisterUserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        return user;
    }

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(user, userDto);
        Optional<Role> role = user.getRoles().stream().findFirst();
        if(role.isPresent()) {
            userDto.setRoleName(role.get().getName());
            userDto.setPermissions(role.get().getPermission());
        }
        return userDto;
    }
}
