package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> addNewItem(Long userId, ItemDto item) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto item) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<Object> searchItem(Long userId, String text) {
        return get("/search?text={text}", userId, Map.of("text", text));
    }

    public ResponseEntity<Object> addComment(Long userId, Long itemId, CommentDto newCommentDto) {
        return post("/{itemId}/comment", userId, Map.of("itemId", itemId), newCommentDto);
    }

    public ResponseEntity<Object> getItemById(Long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItemOwner(Long userId) {
        return get("", userId);
    }


}
