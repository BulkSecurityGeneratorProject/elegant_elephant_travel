package com.elegantelephant.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.elegantelephant.domain.Deal;
import com.elegantelephant.service.DealService;
import com.elegantelephant.web.rest.util.HeaderUtil;
import com.elegantelephant.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Deal.
 */
@RestController
@RequestMapping("/api")
public class DealResource {

    private final Logger log = LoggerFactory.getLogger(DealResource.class);
        
    @Inject
    private DealService dealService;

    /**
     * POST  /deals : Create a new deal.
     *
     * @param deal the deal to create
     * @return the ResponseEntity with status 201 (Created) and with body the new deal, or with status 400 (Bad Request) if the deal has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/deals")
    @Timed
    public ResponseEntity<Deal> createDeal(@RequestBody Deal deal) throws URISyntaxException {
        log.debug("REST request to save Deal : {}", deal);
        if (deal.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("deal", "idexists", "A new deal cannot already have an ID")).body(null);
        }
        Deal result = dealService.save(deal);
        return ResponseEntity.created(new URI("/api/deals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("deal", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /deals : Updates an existing deal.
     *
     * @param deal the deal to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated deal,
     * or with status 400 (Bad Request) if the deal is not valid,
     * or with status 500 (Internal Server Error) if the deal couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/deals")
    @Timed
    public ResponseEntity<Deal> updateDeal(@RequestBody Deal deal) throws URISyntaxException {
        log.debug("REST request to update Deal : {}", deal);
        if (deal.getId() == null) {
            return createDeal(deal);
        }
        Deal result = dealService.save(deal);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("deal", deal.getId().toString()))
            .body(result);
    }

    /**
     * GET  /deals : get all the deals.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of deals in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @GetMapping("/deals")
    @Timed
    public ResponseEntity<List<Deal>> getAllDeals(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Deals");
        Page<Deal> page = dealService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/deals");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /deals/:id : get the "id" deal.
     *
     * @param id the id of the deal to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the deal, or with status 404 (Not Found)
     */
    @GetMapping("/deals/{id}")
    @Timed
    public ResponseEntity<Deal> getDeal(@PathVariable Long id) {
        log.debug("REST request to get Deal : {}", id);
        Deal deal = dealService.findOne(id);
        return Optional.ofNullable(deal)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /deals/:id : delete the "id" deal.
     *
     * @param id the id of the deal to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/deals/{id}")
    @Timed
    public ResponseEntity<Void> deleteDeal(@PathVariable Long id) {
        log.debug("REST request to delete Deal : {}", id);
        dealService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("deal", id.toString())).build();
    }

}
