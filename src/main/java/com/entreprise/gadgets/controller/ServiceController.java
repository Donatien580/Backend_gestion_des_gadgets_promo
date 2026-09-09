package com.entreprise.gadgets.controller;

import com.entreprise.gadgets.dto.request.ServiceRequest;
import com.entreprise.gadgets.dto.response.ServiceResponse;
import com.entreprise.gadgets.service.ServiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceService serviceService;

    @GetMapping
    public List<ServiceResponse> lister() {
        return serviceService.lister();
    }

    @GetMapping("/{id}")
    public ServiceResponse obtenir(@PathVariable Integer id) {
        return serviceService.obtenir(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceResponse creer(@Valid @RequestBody ServiceRequest requete) {
        return serviceService.creer(requete);
    }

    @PutMapping("/{id}")
    public ServiceResponse modifier(@PathVariable Integer id,
                                    @Valid @RequestBody ServiceRequest requete) {
        return serviceService.modifier(id, requete);
    }
    
    @GetMapping("/{id}/compte-personnels-actifs")
    public long compterPersonnelsActifs(@PathVariable Integer id) {
        return serviceService.compterPersonnelsActifs(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Integer id) {
        serviceService.supprimer(id);
    }
}