package github.aq.externalipmailservice.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import github.aq.externalipmailservice.configuration.MailConfiguration;
import github.aq.externalipmailservice.exception.ApplicationException;

@RestController
@RequestMapping("/api")
public class RestApiController {

	@Autowired 
	MailConfiguration mailConfiguration;
	
	Boolean serviceEnabled = false;

	@RequestMapping(path = "/scheduled", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String scheduled(HttpServletResponse httpResponse, @RequestParam(name="enable", required=true) Boolean serviceEnableValue) {
		serviceEnabled = serviceEnableValue;
		cronExecute(serviceEnabled);
		return "Service is enabled = " + serviceEnabled;
	}
	
	@RequestMapping(path = "/send", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String send(HttpServletResponse httpResponse, @RequestParam(name="enable", required=true) Boolean serviceEnableValue) {
		serviceEnabled = serviceEnableValue;
		execute();
		return "Service is enabled = " + serviceEnabled;
	}
	
	@Scheduled(cron = "0 0 8 * * *")
	public void cronExecute(boolean serviceEnabled) {
		if (serviceEnabled) {
			execute();
		}
	}
	s
	public void execute() {
		String publicIp = "";			
		publicIp = getMyPublicIp();
		throw new ApplicationException(publicIp);
		//sendFromGMail("Synology dsm email notification", "my public ip is: " + publicIp);
	}
	
	public String getMyPublicIp() {
		String ip = ""; 
		try {
		URL whatismyip = new URL("http://ipecho.net/plain");
		BufferedReader in = new BufferedReader(
				new InputStreamReader(whatismyip.openStream()));		
			ip = in.readLine(); //you get the IP as a String
		} catch(IOException ex) {
			//System.err.println("Error: The public ip could not be resolved at this time.");
			throw new ApplicationException("Error: The public ip could not be resolved at this time.");
		}
		System.out.println(ip);
		return ip;
	}
	
	private void sendFromGMail(String subject, String body) {
        Properties props = System.getProperties();
        
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", mailConfiguration.getHost());
        props.put("mail.smtp.user", mailConfiguration.getFrom());
        props.put("mail.smtp.password", mailConfiguration.getPassword());
        props.put("mail.smtp.port", mailConfiguration.getPort());
        props.put("mail.smtp.auth", "true");

        List<String> to = mailConfiguration.getDefaultRecipients();
        
        Session session = Session.getDefaultInstance(props);
        MimeMessage message = new MimeMessage(session);
        
        try {
            message.setFrom(new InternetAddress(mailConfiguration.getFrom()));
            InternetAddress[] toAddress = new InternetAddress[to.size()];

            // To get the array of addresses
            int i = 0;
            for(String recipientAddress: to) {
                toAddress[i] = new InternetAddress(recipientAddress);
                i ++;
            }

            for( int j = 0; j < toAddress.length; j++) {
                message.addRecipient(Message.RecipientType.TO, toAddress[j]);
            }

            message.setSubject(subject);
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(mailConfiguration.getHost(), mailConfiguration.getFrom(), mailConfiguration.getPassword());
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        }
        catch (AddressException ae) {
            //System.err.println("Error: the receiver email address could not be parsed.");            
            throw new ApplicationException("Error: the receiver email address could not be parsed.");
        }
        catch (MessagingException me) {            
            //System.err.println("Error: the message could not be delivered to the email address. Check host value.");            
            throw new ApplicationException("Error: the message could not be delivered to the email address. Check host value.");
        }
        
    }

}
