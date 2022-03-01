package com.demo.inbox.controllers;

import java.util.List;

import com.demo.inbox.folders.Folder;
import com.demo.inbox.folders.FolderRepository;
import com.demo.inbox.folders.FolderService;

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
    
    @GetMapping(value = "/")
    public String homePage(
        @AuthenticationPrincipal OAuth2User principal,
        Model model
        ){

        if(principal == null || !StringUtils.hasText(principal.getAttribute("login"))){
            return "index";
        }

        String userid = principal.getAttribute("login");
        List<Folder> userFolders = folderRepository.findAllById(userid);
        List<Folder> userDefaultFolders = folderService.fetchDefaultFolder(userid);
        model.addAttribute("userFolders", userFolders);
        model.addAttribute("userDefaultFolders", userDefaultFolders);
        return "inbox-page";
        
        
    }    
}
