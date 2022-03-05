package com.demo.inbox.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.demo.inbox.email.Email;
import com.demo.inbox.email.EmailRepository;
import com.demo.inbox.folders.Folder;
import com.demo.inbox.folders.FolderRepository;
import com.demo.inbox.folders.FolderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Controller
public class EmailViewController {

    @Autowired private FolderRepository folderRepository;

    @Autowired private EmailRepository emailRepository;

    @Autowired private FolderService folderService;
    

    @GetMapping(value = "/emails/{id}")
    public String emailView(
        @PathVariable UUID id,
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
        
        Optional<Email> optionalEmail = emailRepository.findById(id);
        if(!optionalEmail.isPresent()){
            return "inbox-page";
        }

        Email email = optionalEmail.get();
        String toIds = String.join(",", email.getTo());
        model.addAttribute("email", email);
        model.addAttribute("toIds", toIds);
        
        
        return "email-page";    
    }
      
    
}
