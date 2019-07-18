package com.controllers;

import com.Entity.Sector;
import com.Entity.Board;
import com.dao.SectorDao;
import com.service.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller()
@RequestMapping("/sector")
public class SectorController {

    @Autowired
    private SectorService sectorService;

    @Autowired
    private SectorDao sectorDao;

    @PostMapping("/create")
    public String create(@RequestParam String name) {
        sectorService.createSector(name);
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String delete(@RequestParam Long id) {
        sectorService.delete(id);
        return "redirect:/";
    }

    @GetMapping("/getsubs/{id}")
    @ResponseBody
    public List<Object> getSector(@PathVariable Long id) {
        Sector sector = sectorDao.findById(id).orElse(null);
        List<Object> list = new ArrayList<>();
        for (Board board : sector.getBoards()) {
            list.add(board.toDTO());
        }
        return list;
    }
}
