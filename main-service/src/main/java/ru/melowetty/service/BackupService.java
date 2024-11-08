package ru.melowetty.service;

import ru.melowetty.repository.Backupable;

import java.util.Stack;

public class BackupService {
    Stack<Memento> history = new Stack<>();

    public void rollback() {
        if (!history.isEmpty()) {
            history.peek().restore();
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
