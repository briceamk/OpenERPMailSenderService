package cm.xenonit.gelodia.openerpmailsender.notification.resource.mapper;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.Mail;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailTemplate;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailServerState;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailServerType;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailState;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType;
import cm.xenonit.gelodia.openerpmailsender.notification.generated.resource.dto.UpdateMailRequestDto;
import cm.xenonit.gelodia.openerpmailsender.notification.resource.dto.MailDto;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author bamk
 * @version 1.0
 * @since 19/02/2024
 */
@Component
public class MailMapper {

    public MailDto fromMail(Mail mail) {
        MailDto mailDto = new MailDto();
        BeanUtils.copyProperties(mail, mailDto);
        mailDto.setState(mail.getState().name());
        mailDto.setType(mail.getType().name());
        return mailDto;
    }

    public List<MailDto> fromMailPage(Page<Mail> mailPage) {
        List<Mail> mails = mailPage.getContent();
        return mails.stream().map(this::fromMail).toList();
    }

    public Mail fromUpdateMailRequestDto(UpdateMailRequestDto updateMailRequestDto) {
        Mail mail = new Mail();
        MailServer mailServer  = new MailServer();
        MailTemplate mailTemplate  = new MailTemplate();
        BeanUtils.copyProperties(updateMailRequestDto, mail);
        mail.setState(MailState.valueOf(updateMailRequestDto.getState().name()));
        mail.setType(MailTemplateType.valueOf(updateMailRequestDto.getType().name()));

        BeanUtils.copyProperties(updateMailRequestDto.getMailServer(), mailServer);
        mailServer.setState(MailServerState.valueOf(updateMailRequestDto.getMailServer().getState().name()));
        mailServer.setType(MailServerType.valueOf(updateMailRequestDto.getMailServer().getType().name()));

        BeanUtils.copyProperties(updateMailRequestDto.getMailTemplate(), mailTemplate);
        mailTemplate.setType(MailTemplateType.valueOf(updateMailRequestDto.getMailTemplate().getType()));

        mail.setMailServer(mailServer);
        mail.setMailTemplate(mailTemplate);
        return mail;
    }
}
