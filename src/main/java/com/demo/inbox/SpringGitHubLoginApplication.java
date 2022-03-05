package com.demo.inbox;

import java.nio.file.Path;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.demo.inbox.email.Email;
import com.demo.inbox.email.EmailRepository;
import com.demo.inbox.emaillist.EmailListItem;
import com.demo.inbox.emaillist.EmailListItemKey;
import com.demo.inbox.emaillist.EmailListItemRepository;
import com.demo.inbox.folders.Folder;
import com.demo.inbox.folders.FolderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CqlSessionBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringGitHubLoginApplication {

	@Autowired FolderRepository folderRepository;

	@Autowired EmailListItemRepository emailListItemRepository;

	@Autowired EmailRepository emailRepo;

	public static void main(String[] args) {
		SpringApplication.run(SpringGitHubLoginApplication.class, args);
	}

	 @RequestMapping("/user")
	 public String user(@AuthenticationPrincipal OAuth2User principal) {
	 	System.out.println(principal);
	 	return principal.getAttribute("name");
	 }

	/* This is necessary to have the Spring boot app use the astra secure bundle 
	to connect to the database */
	@Bean
	public CqlSessionBuilderCustomizer sessionBuilderCustomizer(DataStaxAstraProperties astraProperties){
		Path bundle = astraProperties.getSecureConnectBundle().toPath();
		return builder -> builder.withCloudSecureConnectBundle(bundle);
	}

	@PostConstruct
	public void init(){

		folderRepository.save(new Folder("OmkarShivadekar","Inbox","blue"));
		folderRepository.save(new Folder("OmkarShivadekar","Sent","green"));
		folderRepository.save(new Folder("OmkarShivadekar","Important","yellow"));

		for(int i =0; i<10; i++){
			EmailListItemKey key = new EmailListItemKey();
			key.setId("OmkarShivadekar");
			key.setLabel("Inbox");
			key.setTimeUUiID(Uuids.timeBased());

			EmailListItem item = new EmailListItem();
			item.setKey(key);
			item.setTo(Arrays.asList("OmkarShivadekar"));
			item.setSubject("Subject"+i);
			item.setUnread(true);

			emailListItemRepository.save(item);

			Email email = new Email();
			email.setId(key.getTimeUUiID());
			email.setFrom("OmkarShivadekar");
			email.setSubject(item.getSubject());
			email.setBody("body" + i);
			email.setTo(item.getTo());
			emailRepo.save(email);

		}
		
	}
	

}
