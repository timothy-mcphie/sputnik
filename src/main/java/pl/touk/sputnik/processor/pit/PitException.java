package pl.touk.sputnik.processor.pit;

class PitException extends RuntimeException {
    PitException(String message) {
        super(message);
    }

    PitException(String message, Throwable t) {
        super(message, t);
    }
}
