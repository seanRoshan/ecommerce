package com.seanroshan.ecommerce.api;


import com.seanroshan.ecommerce.entity.Item;
import com.seanroshan.ecommerce.repository.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private final ItemRepository itemRepository = mock(ItemRepository.class);

    private final Item iphone;
    private final ArrayList<Item> items;

    public ItemControllerTest() {
        Item iphone = new Item();
        iphone.setId(1L);
        iphone.setName("IPHONE");
        iphone.setDescription("GIVE ME YOUR MONEY, NOW!");
        iphone.setPrice(new BigDecimal(1400));
        this.iphone = iphone;
        ArrayList<Item> items = new ArrayList<>();
        items.add(iphone);
        this.items = items;
    }

    @Before
    public void setUp() {
        itemController = new ItemController(itemRepository);
    }

    @Test
    public void findAll() {

        when(itemRepository.findAll()).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItems();
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(items.get(0).getId(), iphone.getId());
    }


    @Test
    public void findById() {

        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.ofNullable(iphone));
        when(itemRepository.findById(0L)).thenReturn(java.util.Optional.empty());

        assert iphone != null;

        ResponseEntity<Item> response = itemController.getItemById(iphone.getId());
        assertEquals(200, response.getStatusCodeValue());
        Item item = response.getBody();
        assertNotNull(item);
        assertEquals(item.getId(), iphone.getId());


        response = itemController.getItemById(0L);
        assertEquals(404, response.getStatusCodeValue());
        item = response.getBody();
        assertNull(item);

    }

    @Test
    public void findByName() {

        when(itemRepository.findByName(iphone.getName())).thenReturn(items);
        when(itemRepository.findByName("MACBOOK")).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItemsByName(iphone.getName());
        assertEquals(200, response.getStatusCodeValue());
        List<Item> items = response.getBody();
        assertNotNull(items);
        assertEquals(items.get(0).getId(), iphone.getId());

        response = itemController.getItemsByName("MACBOOK");
        assertEquals(404, response.getStatusCodeValue());
        items = response.getBody();
        assertNull(items);

    }


}
