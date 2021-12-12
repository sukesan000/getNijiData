package com.nijiData.NijiData.controller;

import com.nijiData.NijiData.entity.Member;
import com.nijiData.NijiData.service.ScrapingService;
import com.google.api.services.youtube.model.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class mainController {
    @Autowired
    ScrapingService sService;

    @GetMapping("/test")
    public String test(Model model){
        System.out.println("test");
        model.addAttribute("name", "大山");
        return "test";
    }

    @Scheduled(cron = " 0 1 * * * *", zone = "Asia/Tokyo")
    public void main() throws IOException {
        List<Member> nameAndIdList= sService.getNameAndId();
        List<Channel> channelsList = sService.getMemberInfoFromYoutube(nameAndIdList);
        sService.saveMemberInfo(nameAndIdList, channelsList);
        System.out.println("データ取り込み完了");
    }
}
