package com.grupo.learningmore.domain.chat;

public class ChatRoom {

    private Long id;
    private String name;

    public ChatRoom() {
    }

    public ChatRoom(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
