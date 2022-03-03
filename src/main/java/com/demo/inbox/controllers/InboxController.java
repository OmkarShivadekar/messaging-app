package com.demo.inbox.controllers;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.demo.inbox.emaillist.EmailListItem;
import com.demo.inbox.emaillist.EmailListItemRepository;
import com.demo.inbox.folders.Folder;
import com.demo.inbox.folders.FolderRepository;
import com.demo.inbox.folders.FolderService;

import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InboxController {

    @Autowired private FolderRepository folderRepository;

    @Autowired private FolderService folderService;

    @Autowired private EmailListItemRepository emailListItemRepository;
    
    @GetMapping(value = "/")
    public String homePage(
        @AuthenticationPrincipal OAuth2User principal,
        Model model
        ){

        if(principal == null || !StringUtils.hasText(principal.getAttribute("login"))){
            return "index";
        }

        //Fetch folders
        String userid = principal.getAttribute("login");
        List<Folder> userFolders = folderRepository.findAllById(userid);
        List<Folder> userDefaultFolders = folderService.fetchDefaultFolder(userid);
        model.addAttribute("userFolders", userFolders);
        model.addAttribute("userDefaultFolders", userDefaultFolders);
       
       //Fetch messages
       String folderLabel = "Inbox";
       List<EmailListItem> emailList = emailListItemRepository
                                        .findAllByKey_IdAndKey_Label(userid, folderLabel);
       PrettyTime p = new PrettyTime();
       emailList.stream().forEach(emailItem -> {
           UUID timeUUiID = emailItem.getKey().getTimeUUiID();
           Date emailDateTime = new Date(Uuids.unixTimestamp(timeUUiID));
           emailItem.setAgoTimeString(p.format(emailDateTime));
       });                                 
       model.addAttribute("emailList",emailList);

        return "inbox-page";
        
        
    }    
}
