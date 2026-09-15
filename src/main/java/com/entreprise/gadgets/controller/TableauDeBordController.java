package com.entreprise.gadgets.controller;

import com.entreprise.gadgets.dto.response.TableauDeBordResponse;
import com.entreprise.gadgets.service.TableauDeBordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tableau-de-bord")
@RequiredArgsConstructor
public class TableauDeBordController {

    private final TableauDeBordService tableauDeBordService;

    @GetMapping
    public TableauDeBordResponse obtenir() {
        return tableauDeBordService.obtenirTableauDeBord();
    }
}