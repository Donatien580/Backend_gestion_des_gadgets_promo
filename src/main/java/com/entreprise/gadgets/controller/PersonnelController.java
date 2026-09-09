package com.entreprise.gadgets.controller;

import com.entreprise.gadgets.dto.request.PersonnelRequest;
import com.entreprise.gadgets.dto.response.PersonnelResponse;
import com.entreprise.gadgets.service.PersonnelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/personnels")
@RequiredArgsConstructor
public class PersonnelController {

    private final PersonnelService personnelService;

    @GetMapping
    public List<PersonnelResponse> lister() {
        return personnelService.lister();
    }

    @GetMapping("/{id}")
    public PersonnelResponse obtenir(@PathVariable Integer id) {
        return personnelService.obtenir(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PersonnelResponse creer(@Valid @RequestBody PersonnelRequest requete) {
        return personnelService.creer(requete);
    }

    @PutMapping("/{id}")
    public PersonnelResponse modifier(@PathVariable Integer id,
                                      @Valid @RequestBody PersonnelRequest requete) {
        return personnelService.modifier(id, requete);
    }
    
    @GetMapping("/service/{idService}/actifs")
    public List<PersonnelResponse> listerActifsParService(@PathVariable Integer idService) {
        return personnelService.listerActifsParService(idService);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Integer id) {
        personnelService.supprimer(id);
    }
}