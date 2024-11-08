package ru.melowetty.repository;

public interface Backupable {
    String backup();

    void restore(String state);
}
