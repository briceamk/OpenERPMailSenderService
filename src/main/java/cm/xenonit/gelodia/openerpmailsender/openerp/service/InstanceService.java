package cm.xenonit.gelodia.openerpmailsender.openerp.service;

import cm.xenonit.gelodia.openerpmailsender.common.enums.SortDirection;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.Instance;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.enums.InstanceState;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * @author bamk
 * @version 1.0
 * @since 21/02/2024
 */
public interface InstanceService {

    Instance createInstance(Instance instance);
    Instance updateInstance(String id, Instance instance);

    Instance fetchInstanceById(String id);
    Page<Instance> fetchInstances(Integer page, Integer size, SortDirection sortDirection, String attribute);

    Page<Instance> fetchInstancesByKeyword(Integer page, Integer size, SortDirection sortDirection, String attribute, String keyword);

    List<Instance> fetchInstancesByState(InstanceState state);

    Instance activateInstance(String id);

    Instance inactiveInstance(String id);

}
