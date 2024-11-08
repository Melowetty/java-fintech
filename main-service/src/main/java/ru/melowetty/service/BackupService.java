package ru.melowetty.service;

import ru.melowetty.repository.Backupable;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class BackupService {
    Deque<Memento> history = new ArrayDeque<>();

    public void rollback() {
        if (!history.isEmpty()) {
            history.peekLast().restore();
        }
    }

    public void addChange(Backupable target) {
        history.add(new Memento(target));
    }

    private static class Memento {
        private final Backupable target;
        private final String backup;

        public Memento(Backupable target) {
            this.target = target;
            this.backup = target.backup();
        }

        public void restore() {
            target.restore(backup);
        }
    }
}
