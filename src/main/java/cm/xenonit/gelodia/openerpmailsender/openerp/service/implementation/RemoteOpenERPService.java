package cm.xenonit.gelodia.openerpmailsender.openerp.service.implementation;

import cm.xenonit.gelodia.openerpmailsender.notification.domain.Mail;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailHistory;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.MailServer;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailServerState;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailState;
import cm.xenonit.gelodia.openerpmailsender.notification.domain.enums.MailTemplateType;
import cm.xenonit.gelodia.openerpmailsender.notification.service.MailHistoryService;
import cm.xenonit.gelodia.openerpmailsender.notification.service.MailService;
import cm.xenonit.gelodia.openerpmailsender.openerp.config.RemoteInstanceConfigurer;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.Instance;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.MailInstance;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.enums.InstanceState;
import cm.xenonit.gelodia.openerpmailsender.openerp.service.InstanceService;
import cm.xenonit.gelodia.openerpmailsender.openerp.service.MailInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static java.util.List.of;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RemoteOpenERPService {


    public static final String P_EXECUTE_METHOD_NAME = "execute";
    public static final String READ_METHOD_NAME = "read";
    public static final String DELETE_METHOD_NAME = "unlink";
    public static final String CREATE_METHOD_NAME = "create";
    public static final String SEARCH_METHOD_NAME = "search";
    public static final String OPENERP_QUEUE_OBJECT = "email.smtpclient.queue";
    public static final String OPENERP_HISTORY_OBJECT = "email.smtpclient.history";


    private final XmlRpcClient client;
    private final MailService mailService;
    private final MailHistoryService mailHistoryService;
    private final InstanceService instanceService;
    private final MailInstanceService mailInstanceService;
    private final RemoteInstanceConfigurer remoteInstanceConfigurer;



    public void fetchRemoteEmail() throws XmlRpcException, MalformedURLException {

       log.debug(">>>> Fetch Mail From Remote Server - Collect Mail in Queue - starting");
       log.debug(">>>> Fetch Mail From Remote Server - Instance in database - starting");
        List<Instance> instances = instanceService.fetchInstancesByState(InstanceState.ACTIVE);
       log.debug(">>>> Fetch Mail From Remote Server - Instance in database - complete");
       log.debug(">>>> Fetch Mail From Remote Server - found {} instances in database", instances.size());
        for(Instance instance: instances) {
           log.debug(">>>> Fetch Mail From Remote Server - Start fetching email front instance {}:{} in database {} - starting", instance.getHost(), instance.getPort(), instance.getDb());
            MailServer mailServer = instance.getMailServer();
            if(!mailServer.getState().equals(MailServerState.CONFIRM)) {
               log.debug(">>>>Fetch Mail From Remote Server -  Mail server is not verified. Please verified the server before.");
            } else {
                XmlRpcClientConfigImpl clientConfig = remoteInstanceConfigurer.clientConfig(instance);
                XmlRpcClientConfigImpl modelConfig = remoteInstanceConfigurer.modelConfig(instance);
                int openERPUserId = remoteInstanceConfigurer.getRemoteUserId(client, clientConfig, instance);
               log.debug(">>>> Fetch Mail From Remote Server - Calling Remote server to get email in queue - starting");
                List<Integer> externalIdsInLocalQueue = mailInstanceService.fetchMailExternalIdsInQueueByInstanceAndTemplate(instance, MailTemplateType.NOT_APPLICABLE);
                externalIdsInLocalQueue = externalIdsInLocalQueue == null? new ArrayList<>(): externalIdsInLocalQueue;
                List<Object> emailIds = getRemoteMailIds(modelConfig, openERPUserId, instance, externalIdsInLocalQueue);
               log.debug(">>>> Fetch Mail From Remote Server - Calling Remote server to get email in queue - complete");
               log.debug(">>>> Fetch Mail From Remote Server - Remote server return {} in queue", emailIds.size());
               log.debug(">>>> Fetch Mail From Remote Server - Calling Remote server to get complete info of each email in queue - starting");
                List<Object> remoteMails = getRemoteMailsInfos(modelConfig, openERPUserId, emailIds, instance);
               log.debug(">>>> Fetch Mail From Remote Server - Calling Remote server to get complete info of each email in queue - complete");
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                ArrayList<Mail> mails = new ArrayList<>();
               log.debug(">>>> Fetch Mail From Remote Server - Adding remote mails to the local database - starting");
                creteMailFromRemoteMailsInfos(remoteMails, mails, dateTimeFormatter, mailServer, instance);
               log.debug(">>>> Fetch Mail From Remote Server - Adding remote mails to the local database - complete");
            }
           log.debug(">>>> Fetch Mail From Remote Server - Start fetching email front instance {}:{} in database {} - complete", instance.getHost(), instance.getPort(), instance.getDb());
        }
       log.debug(">>>> Fetch Mail From Remote Server - Collect Mail in Queue - complete");

    }

    public void createRemoteMailInHistoryQueue() throws XmlRpcException, MalformedURLException {

       log.debug(">>>> Create Remote Mail In History Job - starting");
       log.debug(">>>> Create Remote Mail In History Job - Instance in database - starting");
        List<Instance> instances = instanceService.fetchInstancesByState(InstanceState.ACTIVE);
       log.debug(">>>> Create Remote Mail In History Job - Instance in database - complete");
        for(Instance instance: instances) {
            XmlRpcClientConfigImpl clientConfig = remoteInstanceConfigurer.clientConfig(instance);
            XmlRpcClientConfigImpl modelConfig = remoteInstanceConfigurer.modelConfig(instance);
            int openERPUserId = remoteInstanceConfigurer.getRemoteUserId(client, clientConfig, instance);
           log.debug(">>>> Create Remote Mail In History Job - Fetch mail server to use for the job - starting");
            MailServer mailServer = instance.getMailServer();
            if(!mailServer.getState().equals(MailServerState.CONFIRM)) {
               log.debug(">>>> Create Remote Mail In History Job - Mail server is not verified. Please verified the server before.");
            } else {
               log.debug("Create Remote Mail In History Job -  Start fetching email front instance {}:{} in database {} - starting", instance.getHost(), instance.getPort(), instance.getDb());
                List<Mail> mails = mailInstanceService.findMailByStateAndMailServerAndInstance(MailState.SEND, mailServer, instance);
               log.debug(">>>> Create Remote Mail In History Job - Fetching mail to update as complete in local database - complete");
                if(!mails.isEmpty()) {
                   log.debug(">>>> Create Remote Mail In History Job - Creating remote mail in history queue - starting");
                    for(Mail mail: mails) {
                        createRemoteMailHistory(modelConfig, mail, openERPUserId, instance);
                    }
                   log.debug(">>>> Create Remote Mail In History Job - Creating remote mail in history queue - complete");
                   log.debug(">>>> Deleting Remote Mail Job - Create Mail History - starting");
                    List<MailHistory> mailHistories = mails.stream().map(mail -> 
                                                                MailHistory.builder()
                                                                    .to(mail.getTo())
                                                                    .subject(mail.getSubject())
                                                                    .message(mail.getMessage())
                                                                    .mailServer(mail.getMailServer())
                                                                    .createdAt(mail.getCreatedAt())
                                                                    .attributes(
                                                                            Map.of(
                                                                                "instanceId", instance.getId(),
                                                                                "mailServerId", mail.getMailServer().getId(),
                                                                                "externalId", String.valueOf(mail.getExternalId()),
                                                                                "externalServerId", String.valueOf(mail.getExternalServerId())
                                                                            ))
                                                                    .build()
                                                                )
                                                                .toList();
                    mailHistoryService.createMailHistoriesFromMails(mailHistories);
                   log.debug(">>>> Deleting Remote Mail Job - Create Mail History - complete");
                   log.debug(">>>> Deleting Remote Mail Job - Mark Mail State As Complete - starting");
                    mailService.completeMails(mails);
                   log.debug(">>>> Deleting Remote Mail Job - Mark Mail State As Complete - complete");
                }
               log.debug("Create Remote Mail In History Job -  Start fetching email front instance {}:{} in database {} - complete", instance.getHost(), instance.getPort(), instance.getDb());
            }
        }
       log.debug(">>>> Create Remote Mail In History Job - complete");
    }


    public void deleteRemoteEmailInQueue() throws XmlRpcException, MalformedURLException {
       log.debug(">>>> Deleting Remote Mail Job - starting");
       log.debug(">>>> Create Remote Mail In History Job - Instance in database - starting");
        List<Instance> instances = instanceService.fetchInstancesByState(InstanceState.ACTIVE);
       log.debug(">>>> Create Remote Mail In History Job - Instance in database - complete");
        for(Instance instance: instances) {
            XmlRpcClientConfigImpl clientConfig = remoteInstanceConfigurer.clientConfig(instance);
            XmlRpcClientConfigImpl modelConfig = remoteInstanceConfigurer.modelConfig(instance);
            int openERPUserId = remoteInstanceConfigurer.getRemoteUserId(client, clientConfig, instance);
           log.debug(">>>> Deleting Remote Mail Job - Fetch mail server to use for the job - starting");
            MailServer mailServer = instance.getMailServer();
            if(!mailServer.getState().equals(MailServerState.CONFIRM)) {
               log.debug(">>>> Deleting Remote Mail Job - Mail server is not verified. Please verified the server before.");
            } else {
               log.debug("Deleting Remote Mail Job -  Start fetching email front instance {}:{} in database {} - starting", instance.getHost(), instance.getPort(), instance.getDb());
               log.debug(">>>> Deleting Remote Mail Job - Fetching mail to delete in local database - starting");
                List<Mail> mails = mailInstanceService.findMailByStateAndMailServerAndInstance(MailState.COMPLETE, mailServer, instance);
                List<Integer> externalIds = mapMailToRemoteExternalIds(mails);
               log.debug(">>>> Deleting Remote Mail Job - Fetching mail to delete in local database - complete");
                if(!externalIds.isEmpty()) {
                   log.debug(">>>> Deleting Remote Mail Job - Deleting remote mail in queue - starting");
                    deleteRemoteMailInQueue(modelConfig, instance, openERPUserId, externalIds);
                   log.debug(">>>> Deleting Remote Mail Job - Deleting remote mail in queue - complete");
                   log.debug(">>>> Deleting Remote Mail Job - Create Mail History - complete");
                    mailService.deleteMailByIds(mails);
                }
               log.debug("Deleting Remote Mail Job -  Start fetching email front instance {}:{} in database {} - complete", instance.getHost(), instance.getPort(), instance.getDb());

            }
        }
       log.debug(">>>> Deleting Remote Mail Job - complete");
    }

    private void deleteRemoteMailInQueue(XmlRpcClientConfigImpl modelConfig, Instance instance, int openERPUserId, List<Integer> externalIds) throws XmlRpcException {
        client.execute(
                modelConfig,
                P_EXECUTE_METHOD_NAME,
                of(
                        instance.getDb(), openERPUserId, instance.getPassword(), OPENERP_QUEUE_OBJECT, DELETE_METHOD_NAME, externalIds
                )

        );
    }

    private void createRemoteMailHistory(XmlRpcClientConfigImpl modelConfig, Mail mail, int openERPUserId, Instance instance) throws XmlRpcException {
        client.execute(
                modelConfig,
                P_EXECUTE_METHOD_NAME,
                of(
                        instance.getDb(), openERPUserId, instance.getPassword(), OPENERP_HISTORY_OBJECT, CREATE_METHOD_NAME,
                        Map.of(
                                "name", mail.getMessage(),
                                "user_id", openERPUserId,
                                "server_id", Integer.valueOf(String.valueOf(mail.getExternalServerId())),
                                "email", mail.getTo()
                        )

                )

        );
    }

    private void creteMailFromRemoteMailsInfos(List<Object> remoteMails, ArrayList<Mail> mails,
                                   DateTimeFormatter dateTimeFormatter, MailServer mailServer, Instance instance) {
        remoteMails.forEach(mail -> {
            Map<?, ?> mailMap = (HashMap<?, ?>) mail;
            Long externalMailId = Long.valueOf(mailMap.get("id").toString());
            Long serverId = mailMap.get("server_id") == null ? null : Long.valueOf(((Object[]) mailMap.get("server_id"))[0].toString());
            String to = mailMap.get("to") == null ? "" : mailMap.get("to").toString();
            String subject = mailMap.get("name") == null ? "" : mailMap.get("name").toString();
            String body = mailMap.get("body") == null ? "" : String.valueOf(mailMap.get("body"));
            LocalDateTime createdAt = mailMap.get("date_create") == null ? now() : LocalDateTime.parse(mailMap.get("date_create").toString(), dateTimeFormatter);
            mails.add(mailService.createEmail(MailInstance.builder()
                    .type(MailTemplateType.NOT_APPLICABLE)
                    .state(MailState.SENDING)
                    .to(to)
                    .subject(subject)
                    .instance(instance)
                    .message(body)
                    .externalId(externalMailId)
                    .externalServerId(serverId)
                    .createdAt(createdAt)
                    .mailServer(mailServer)
                    .build()));

        });
    }

    private List<Object> getRemoteMailsInfos(XmlRpcClientConfigImpl modelConfig, int openERPUserId, List<Object> emailIds, Instance instance) throws XmlRpcException {
        return of((Object[]) client.execute(
                modelConfig,
                P_EXECUTE_METHOD_NAME,
                of(
                        instance.getDb(), openERPUserId, instance.getPassword(), OPENERP_QUEUE_OBJECT, READ_METHOD_NAME, emailIds,
                        of("date_create", "name", "to", "body", "id", "server_id")
                )

        ));
    }

    private List<Object> getRemoteMailIds(XmlRpcClientConfigImpl modelConfig, int openERPUserId, Instance instance, List<Integer> externalIdsInLocalQueue) throws XmlRpcException {
        return of((Object[]) client.execute(
                modelConfig,
                P_EXECUTE_METHOD_NAME,
                of(
                        instance.getDb(), openERPUserId, instance.getPassword(), OPENERP_QUEUE_OBJECT, SEARCH_METHOD_NAME,
                        of(
                                of("state", "in", of("draft", "sending", "error")),
                                of("id", "not in", externalIdsInLocalQueue)
                        )
                )
        ));
    }

    private static List<Integer> mapMailToRemoteExternalIds(List<Mail> mails) {
        return mails.stream().map(Mail::getExternalId).map(String::valueOf).map(Integer::valueOf).toList();
    }


}
