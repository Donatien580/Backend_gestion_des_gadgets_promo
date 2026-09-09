package com.entreprise.gadgets.controller;

import com.entreprise.gadgets.dto.request.CategorieRequest;
import com.entreprise.gadgets.dto.response.CategorieResponse;
import com.entreprise.gadgets.service.CategorieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategorieController {

    private final CategorieService categorieService;

    @GetMapping
    public List<CategorieResponse> lister() {
        return categorieService.listerToutes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategorieResponse creer(@Valid @RequestBody CategorieRequest requete) {
        return categorieService.creer(requete);
    }

    @PutMapping("/{id}")
    public CategorieResponse modifier(@PathVariable Integer id, @Valid @RequestBody CategorieRequest requete) {
        return categorieService.modifier(id, requete);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Integer id) {
        categorieService.supprimer(id);
    }
}
