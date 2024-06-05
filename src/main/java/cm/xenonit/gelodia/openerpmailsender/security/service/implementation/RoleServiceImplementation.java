package cm.xenonit.gelodia.openerpmailsender.security.service.implementation;

import cm.xenonit.gelodia.openerpmailsender.security.domain.Role;
import cm.xenonit.gelodia.openerpmailsender.security.domain.enums.RoleType;
import cm.xenonit.gelodia.openerpmailsender.security.exception.RoleNotFoundException;
import cm.xenonit.gelodia.openerpmailsender.security.repository.RoleRepository;
import cm.xenonit.gelodia.openerpmailsender.security.service.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author bamk
 * @version 1.0
 * @since 03/02/2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImplementation implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public Role findRoleType(RoleType roleType) {
        log.info("Attempt to fetch role {} in database", roleType.name());
        Optional<Role> optionalRole = roleRepository.findByName(roleType.name());
        if(optionalRole.isPresent()) {
            log.info("Role with name {} found successfully", roleType.name());
            return optionalRole.get();
        } else {
            throw new RoleNotFoundException(String.format("Role with name %s not found in database", roleType.name()));
        }
    }

    @Override
    public Role findRoleByRoleId(String id) {
        log.info("Attempt to fetch role with id {} in database", id);
        Optional<Role> optionalRole = roleRepository.findById(id);
        if(optionalRole.isPresent()) {
            log.info("Role found in database successfully");
            return optionalRole.get();
        } else {
            throw new RoleNotFoundException("Role with with id not found in database");
        }
    }
}
