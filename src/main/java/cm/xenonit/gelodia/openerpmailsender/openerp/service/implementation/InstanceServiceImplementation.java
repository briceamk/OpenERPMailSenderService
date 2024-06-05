package cm.xenonit.gelodia.openerpmailsender.openerp.service.implementation;

import cm.xenonit.gelodia.openerpmailsender.common.enums.SortDirection;
import cm.xenonit.gelodia.openerpmailsender.openerp.config.RemoteInstanceConfigurer;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.Instance;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.enums.InstanceState;
import cm.xenonit.gelodia.openerpmailsender.openerp.exception.InstanceBadRequestException;
import cm.xenonit.gelodia.openerpmailsender.openerp.exception.InstanceConflictException;
import cm.xenonit.gelodia.openerpmailsender.openerp.exception.InstanceNotFoundException;
import cm.xenonit.gelodia.openerpmailsender.openerp.reopository.InstanceRepository;
import cm.xenonit.gelodia.openerpmailsender.openerp.service.InstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;

/**
 * @author bamk
 * @version 1.0
 * @since 21/02/2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InstanceServiceImplementation implements InstanceService {

    private final InstanceRepository instanceRepository;
    private final RemoteInstanceConfigurer remoteInstanceConfigurer;
    private final XmlRpcClient client;

    @Override
    @Transactional
    public Instance createInstance(Instance instance) {
        checkDuplicateInstance("", instance);
        instance.initialize();
        return instanceRepository.save(instance);
    }

    @Override
    @Transactional
    public Instance updateInstance(String id, Instance instance) {
        checkDuplicateInstance(instance.getId(), instance);
        Instance dbInstance = fetchInstanceById(id);
        BeanUtils.copyProperties(instance, dbInstance);
        return instanceRepository.save(dbInstance);
    }

    @Override
    @Transactional(readOnly = true)
    public Instance fetchInstanceById(String id) {
        return instanceRepository.findById(id).orElseThrow(
                () -> new InstanceNotFoundException("Instance with id '%s' not found.")
        );
    }

    private void checkDuplicateInstance(String id, Instance instance) {
        if(id.isEmpty() && instanceRepository.existsByHostAndPortAndDb(instance.getHost(), instance.getPort(), instance.getDb())) {
            throw new InstanceConflictException(
                    String.format("An Instance with host '%s' on port '%s' with database '%s' already exist. Please try with another data.",
                    instance.getHost(), instance.getPort(), instance.getDb()));
        }

        Optional<Instance> optionalInstance = instanceRepository.findByHostAndPortAndDb(instance.getHost(), instance.getPort(), instance.getDb());
        if(!id.isEmpty() && optionalInstance.isPresent() && !optionalInstance.get().getId().equals(id)) {
            throw new InstanceConflictException(
                    String.format("An Instance with host '%s' on port '%s' with database '%s' already exist. Please try with another data.",
                            instance.getHost(), instance.getPort(), instance.getDb()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Instance> fetchInstances(Integer page, Integer size, SortDirection sortDirection, String attribute) {
        Sort.Direction direction = sortDirection.equals(SortDirection.ASC) ? Sort.Direction.ASC: Sort.Direction.DESC;
        return instanceRepository.findAll(PageRequest.of(page, size, direction, attribute));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Instance> fetchInstancesByKeyword(Integer page, Integer size, SortDirection sortDirection, String attribute, String keyword) {
        Sort.Direction direction = sortDirection.equals(SortDirection.ASC) ? Sort.Direction.ASC: Sort.Direction.DESC;
        return instanceRepository.findByKeyword(keyword.toLowerCase(), PageRequest.of(page, size, direction, attribute));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Instance> fetchInstancesByState(InstanceState state) {
        return instanceRepository.findByState(state);
    }

    @Override
    @Transactional
    public Instance activateInstance(String id) {
        Instance instance = fetchInstanceById(id);
        try {
            XmlRpcClientConfigImpl clientConfig = remoteInstanceConfigurer.clientConfig(instance);
            remoteInstanceConfigurer.getRemoteUserId(client, clientConfig, instance);
            instance.activate();
            instanceRepository.save(instance);
            return  instance;
        } catch (MalformedURLException exception) {
            throw new InstanceBadRequestException("Bad URL. Please verify and try again.");
        } catch (XmlRpcException e) {
            throw new InstanceBadRequestException("We can't activate this Instance. Please verify Instance data and try again.");
        }
    }

    @Override
    @Transactional
    public Instance inactiveInstance(String id) {
        Instance instance = fetchInstanceById(id);
        instance.inactive();
        instanceRepository.save(instance);
        return  instance;
    }
}
