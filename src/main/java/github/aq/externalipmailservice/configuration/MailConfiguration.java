package github.aq.externalipmailservice.configuration;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("mail")
public class MailConfiguration {
	
	private String host;
	private int port;
	private String from;
	private String username;
	private String password;
	private List<String> defaultRecipients;
		
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<String> getDefaultRecipients() {
		return defaultRecipients;
	}
	public void setDefaultRecipients(List<String> defaultRecipients) {
		this.defaultRecipients = defaultRecipients;
	} 
	
}
