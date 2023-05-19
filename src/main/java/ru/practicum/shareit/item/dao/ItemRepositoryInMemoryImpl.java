package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryInMemoryImpl implements ItemRepository {
    private final HashMap<Integer, Item> items = new HashMap<>();
    private int globalId = 0;

    @Override
    public Item addItem(Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getById(int id) {
        return items.get(id);
    }

    @Override
    public List<Item> getAllByUser(User user) {
        return items.values().stream()
                .filter(item -> item.getOwner().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getByTextParams(String text) {
        return items.values().stream()
                .filter(item -> item.getName().contains(text) || item.getDescription().contains(text))
                .collect(Collectors.toList());
    }

    private int getId() {
        return ++globalId;
    }
}
