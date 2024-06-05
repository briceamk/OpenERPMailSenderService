package cm.xenonit.gelodia.openerpmailsender.openerp.config;

import cm.xenonit.gelodia.openerpmailsender.openerp.domain.Instance;
import cm.xenonit.gelodia.openerpmailsender.openerp.exception.InstanceBadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URI;

import static java.util.Collections.emptyMap;
import static java.util.List.of;


/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@Slf4j
@Configuration
public class RemoteInstanceConfigurer {

    public static final String P_LOGIN_METHOD_NAME = "login";

    @Bean
    public XmlRpcClient client() {
        return new XmlRpcClient();
    }

    public XmlRpcClientConfigImpl clientConfig(Instance instance) throws MalformedURLException {
        XmlRpcClientConfigImpl clientConfig = new XmlRpcClientConfigImpl();
        clientConfig.setServerURL(
                URI.create(String.format("%s:%s/xmlrpc/common", instance.getHost(), instance.getPort())).toURL());
        return clientConfig;
    }

    public XmlRpcClientConfigImpl modelConfig(Instance instance) throws MalformedURLException {
        XmlRpcClientConfigImpl modelConfig = new XmlRpcClientConfigImpl();
        modelConfig.setServerURL(
                URI.create(String.format("%s:%s/xmlrpc/object", instance.getHost(), instance.getPort())).toURL());
        return modelConfig;
    }

    public int getRemoteUserId(XmlRpcClient client, XmlRpcClientConfigImpl clientConfig, Instance instance) throws XmlRpcException {
        try {
            return (int) client.execute(
                    clientConfig,
                    P_LOGIN_METHOD_NAME,
                    of(instance.getDb(), instance.getUsername(), instance.getPassword(), emptyMap())
            );
        } catch (XmlRpcException exception) {
            throw new InstanceBadRequestException("We can't connect this Instance. Please verify Instance data and try again.");
        }
    }


}
