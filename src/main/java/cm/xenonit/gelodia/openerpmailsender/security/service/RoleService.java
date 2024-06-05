package cm.xenonit.gelodia.openerpmailsender.security.service;

import cm.xenonit.gelodia.openerpmailsender.security.domain.Role;
import cm.xenonit.gelodia.openerpmailsender.security.domain.enums.RoleType;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
public interface RoleService {

    Role findRoleType(RoleType roleType);

    Role findRoleByRoleId(String id);
}
