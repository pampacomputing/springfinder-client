package com.pampa.springfinderclient.controller;

import com.pampa.springfinderclient.model.ResponseData;
import com.pampa.springfinderclient.model.User;
import com.pampa.springfinderclient.service.ClientService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class MainPageController {

    private final ClientService clientService;

    public MainPageController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/")
    public String mainPage() {
        return "app";
    }


    @PostMapping("/send")
    public String submitUser(@ModelAttribute User user) {
        clientService.sendUser(user);
        return "redirect:/";
    }

    @GetMapping("/responses")
    public String showResponses(
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        List<ResponseData> all = clientService.getAllResponses();
        final int PAGE_SIZE = 1;                           // one request per page
        int totalPages = (int) Math.ceil((double) all.size() / PAGE_SIZE);

        // clamp page
        page = Math.max(0, Math.min(page, totalPages - 1));

        int start = page * PAGE_SIZE;
        int end   = Math.min(start + PAGE_SIZE, all.size());
        List<ResponseData> slice = all.subList(start, end);

        model.addAttribute("responses", slice);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        return "responses";
    }

}
