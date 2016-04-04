package de.infinity.events.domain;

public class CaretEvent {

    private final String id;
    private final int caretPosition;

    public CaretEvent(final String id, final int caretPosition) {
        this.id = id;
        this.caretPosition = caretPosition;
    }

}
