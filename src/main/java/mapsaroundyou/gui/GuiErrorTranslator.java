package mapsaroundyou.gui;

import mapsaroundyou.common.DataLoadException;
import mapsaroundyou.common.DestinationNotFoundException;
import mapsaroundyou.common.InvalidInputException;
import mapsaroundyou.common.ListingNotFoundException;
import mapsaroundyou.common.NoResultsException;

/**
 * Converts exceptions into user-facing GUI messages.
 */
public final class GuiErrorTranslator {
    private GuiErrorTranslator() {
    }

    public static String toUserMessage(Throwable throwable) {
        if (throwable == null) {
            return "Unknown error.";
        }
        if (throwable instanceof NoResultsException
                || throwable instanceof InvalidInputException
                || throwable instanceof DestinationNotFoundException
                || throwable instanceof ListingNotFoundException
                || throwable instanceof DataLoadException) {
            return throwable.getMessage();
        }

        String message = throwable.getMessage();
        if (message == null || message.isBlank()) {
            return "Unexpected error (" + throwable.getClass().getSimpleName() + ").";
        }
        return "Unexpected error: " + message;
    }
}
