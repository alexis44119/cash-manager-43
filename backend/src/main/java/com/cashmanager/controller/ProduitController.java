package com.cashmanager.controller;

import com.cashmanager.model.Panier;
import com.cashmanager.model.Produit;
import com.cashmanager.repository.PanierRepository;
import com.cashmanager.repository.ProduitRepository;
import com.cashmanager.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.ElementCollection;
import javax.validation.Valid;
import java.util.List;

@RestController
public class ProduitController {
    @Autowired
    private ProduitRepository produitRepository;

    @Autowired
    private PanierRepository panierRepository;

    //Get all Produits
    @GetMapping(value = "/produits")
    public Iterable<Produit> getAllProduits(){
        return produitRepository.findAll();
    }

    @GetMapping(value = "/paniers/{panier_id}/produits")
    public Page<Produit> getProduitsbyIdPanier(@PathVariable (value = "panier_id") Long id_panier,
                                               Pageable pageable){
        return produitRepository.findByPanierId(id_panier, pageable);
    }

    // Get a single produit
    @GetMapping(value = "/produits/{id}")
    public Produit getProduitById(@PathVariable(value ="id") Long produit_id ) {
        return produitRepository.findById(produit_id)
                .orElseThrow(() ->new ResourceNotFoundException("Produit", "id", produit_id)) ;
    }

    @PostMapping(value="/produits")
    public Produit createProduit(@Valid @RequestBody Produit produit)
    {
        return produitRepository.save(produit);
    }
    @PostMapping("/paniers/{panier_id}/{id}")
    public Produit createProduitWithPanierId(@PathVariable (value = "panier_id") Long panier_id,
                                           @PathVariable (value = "id") Long produit_id) {
        Produit produit = produitRepository.getOne(produit_id);
        return panierRepository.findById(panier_id).map(panier -> {
            produit.setPanier(panier);
            return produitRepository.save(produit);
        }).orElseThrow(() -> new ResourceNotFoundException("Panier", "id", panier_id));
    }

    //Create a new Produit
    @PostMapping(value = "/paniers/{panier_id}/produits")
    public Produit createProduitWithPanier(@PathVariable (value ="panier_id") Long id_panier,
                                 @Valid @RequestBody Produit produit) {
        return panierRepository.findById(id_panier).map(panier -> {
            produit.setPanier(panier);
            return produitRepository.save(produit);
        }).orElseThrow(() -> new ResourceNotFoundException("panier_id", "panier_id", id_panier));
    }


    // Update a Produit
    @PutMapping(value = "/paniers/{panier_id}/produits/{id}")
    public Produit updateProduit(@PathVariable (value="id") Long produit_id,
                                 @PathVariable (value="panier_id")Long panier_id,
                                 @Valid @RequestBody Produit produitDetails) {
        if (!panierRepository.existsById(panier_id)){
            throw new ResourceNotFoundException("Panier", "id", panier_id);
        }
        return produitRepository.findById(produit_id).map(produit -> {
            produit.setName(produitDetails.getName());
            produit.setPrix(produitDetails.getPrix());
            produit.setPanier((produitDetails.getPanier()));
            return produitRepository.save(produit);
        }).orElseThrow(() -> new ResourceNotFoundException("Produit", "id", produit_id ));

    }
    @DeleteMapping (value = "/paniers/{panier_id}/produits/{id}")
    public Produit deleteProduitFromPanier(@PathVariable (value = "id") Long produit_id,
                                                     @PathVariable (value = "panier_id") Long panier_id){
        if (!panierRepository.existsById(panier_id)){
            throw new ResourceNotFoundException("Panier", "id", panier_id);
        }
        Produit produit = produitRepository.getOne(produit_id);
        Panier panier = panierRepository.getOne(panier_id);
        panier.removeProduit(produitRepository.getOne(produit_id));
        produit.setPanier(null);
        produitRepository.save(produit);
        return produitRepository.save(produit);
    }

    @DeleteMapping (value = "/produits/{id}")
    public ResponseEntity<?> deleteProduit(@PathVariable (value = "id") Long produit_id) {
        return produitRepository.findById(produit_id).map(produit -> {
            produit.getPanier().removeProduit(produit);
            produitRepository.delete(produit);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("Produit", "id", produit_id));
    }

}