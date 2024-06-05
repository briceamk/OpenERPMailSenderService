package cm.xenonit.gelodia.openerpmailsender.notification.resource.mapper;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailServerState;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailServerType;
import cm.xenonit.gelodia.openerpmailsender.notification.generated.resource.dto.CreateMailServerDto;
import cm.xenonit.gelodia.openerpmailsender.notification.generated.resource.dto.UpdateMailServerDto;
import cm.xenonit.gelodia.openerpmailsender.notification.resource.dto.MailServerDto;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author bamk
 * @version 1.0
 * @since 24/01/2024
 */
@Component
public class MailServerMapper {

    public MailServer fromCreateMailServerDto(CreateMailServerDto emailServerDto) {
        MailServer mailServer = new MailServer();
        BeanUtils.copyProperties(emailServerDto, mailServer);
        mailServer.setType(MailServerType.valueOf(emailServerDto.getType().name()));
        return mailServer;
    }

    public MailServerDto fromMailServer(MailServer mailServer) {
        MailServerDto mailServerDto = new MailServerDto();
        BeanUtils.copyProperties(mailServer, mailServerDto);
        mailServerDto.setType(mailServer.getType().name());
        mailServerDto.setState(mailServer.getState().name());
        return mailServerDto;
    }

    public List<MailServerDto> fromMailServers(Page<MailServer> emailServerPage) {
        List<MailServer> mailServerEntitiesContent = emailServerPage.getContent();
        return mailServerEntitiesContent.stream().map(this::fromMailServer).toList();
    }

    public MailServer fromUpdateMailServerDto(UpdateMailServerDto mailServerDto) {
        MailServer mailServer = new MailServer();
        BeanUtils.copyProperties(mailServerDto, mailServer);
        mailServer.setType(MailServerType.valueOf(mailServerDto.getType().name()));
        mailServer.setState(MailServerState.valueOf(mailServerDto.getState().name()));
        return mailServer;

    }
}
