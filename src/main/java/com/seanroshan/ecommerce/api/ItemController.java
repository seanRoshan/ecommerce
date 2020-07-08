package com.seanroshan.ecommerce.api;

import com.seanroshan.ecommerce.entity.Item;
import com.seanroshan.ecommerce.repository.ItemRepository;
import com.seanroshan.ecommerce.splunk.SplunkCimLogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/item")
public class ItemController {

    private final Logger logger = LoggerFactory.getLogger(ItemController.class);

    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping
    public ResponseEntity<List<Item>> getItems() {
        return ResponseEntity.ok(itemRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        Optional<Item> item = itemRepository.findById(id);
        if (!item.isPresent()) {
            logger.error(String.valueOf(new SplunkCimLogEvent("ITEM NOT FOUND", "ITEM NOT FOUND") {{
                addField("DETAIL", "ITEM NOT FOUND");
                setAuthAction("NOT FOUND");
            }}));
            return ResponseEntity.notFound().build();
        }
        logger.info(String.valueOf(new SplunkCimLogEvent("GET ORDER HISTORY", "GET_ORDER_HISTORY") {{
            addField("DETAIL", item.toString());
            setAuthAction("OK");
        }}));
        return ResponseEntity.ok(item.get());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<List<Item>> getItemsByName(@PathVariable String name) {
        List<Item> items = itemRepository.findByName(name);
        if (items == null || items.isEmpty()) {
            logger.error(String.valueOf(new SplunkCimLogEvent("ITEM NOT FOUND", "ITEM NOT FOUND") {{
                addField("DETAIL", "ITEM NOT FOUND");
                setAuthAction("NOT FOUND");
            }}));
            return ResponseEntity.notFound().build();
        }
        logger.info(String.valueOf(new SplunkCimLogEvent("GET ORDER HISTORY", "GET_ORDER_HISTORY") {{
            addField("DETAIL", items.toString());
            setAuthAction("OK");
        }}));
        return ResponseEntity.ok(items);
    }

}
