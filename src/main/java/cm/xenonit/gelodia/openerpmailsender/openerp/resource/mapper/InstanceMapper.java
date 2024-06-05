package cm.xenonit.gelodia.openerpmailsender.openerp.resource.mapper;

import cm.xenonit.gelodia.openerpmailsender.instance.generated.resource.dto.CreateInstanceRequestDto;
import cm.xenonit.gelodia.openerpmailsender.instance.generated.resource.dto.MailServerDto;
import cm.xenonit.gelodia.openerpmailsender.instance.generated.resource.dto.UpdateInstanceRequestDto;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.Instance;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.enums.InstanceState;
import cm.xenonit.gelodia.openerpmailsender.openerp.resource.dto.InstanceDto;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author bamk
 * @version 1.0
 * @since 21/02/2024
 */
@Component
public class InstanceMapper {

    public Instance fromCreateInstanceRequestDto(CreateInstanceRequestDto instanceRequestDto) {
        Instance instance = new Instance();
        MailServer mailServer = new MailServer();
        BeanUtils.copyProperties(instanceRequestDto, instance);
        BeanUtils.copyProperties(instanceRequestDto.getMailServer(), mailServer);
        instance.setMailServer(mailServer);
        return instance;
    }

    public InstanceDto fromInstance(Instance instance) {
        InstanceDto instanceDto = new InstanceDto();
        MailServerDto mailServerDto = new MailServerDto();
        BeanUtils.copyProperties(instance, instanceDto);
        BeanUtils.copyProperties(instance.getMailServer(), mailServerDto);
        instanceDto.setMailServer(mailServerDto);
        instanceDto.setState(instance.getState().name());
        return instanceDto;
    }

    public List<InstanceDto> fromInstancePage(Page<Instance> instancePage) {
        return instancePage.getContent().stream().map(this::fromInstance).toList();
    }

    public Instance fromUpdateInstanceRequestDto(UpdateInstanceRequestDto instanceRequestDto) {
        Instance instance = new Instance();
        MailServer mailServer = new MailServer();
        BeanUtils.copyProperties(instanceRequestDto, instance);
        BeanUtils.copyProperties(instanceRequestDto.getMailServer(), mailServer);
        instance.setState(InstanceState.valueOf(instanceRequestDto.getState().name()));
        instance.setMailServer(mailServer);
        return instance;
    }
}
