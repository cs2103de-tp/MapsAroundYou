package mapsaroundyou.gui;

import mapsaroundyou.model.Destination;
import mapsaroundyou.model.SortMode;
import mapsaroundyou.model.TransportMode;
import mapsaroundyou.model.UserPreferences;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MapsAroundYouGuiAppTest {
    @Test
    void resolveInitialDestination_prefersSavedDestinationWhenSupported() {
        List<Destination> destinations = List.of(
                new Destination("D01", "NUS", "University", "Kent Ridge", "117575"),
                new Destination("D02", "SMU", "University", "Bras Basah", "189652")
        );
        UserPreferences preferences = new UserPreferences(
                "D02",
                2000,
                45,
                10,
                false,
                TransportMode.PUBLIC_TRANSPORT,
                10,
                SortMode.COMMUTE,
                false
        );

        Destination destination = MapsAroundYouGuiApp.resolveInitialDestination(destinations, preferences);

        assertEquals("D02", destination.destinationId());
    }

    @Test
    void resolveInitialDestination_fallsBackToFirstSupportedDestination() {
        List<Destination> destinations = List.of(
                new Destination("D01", "NUS", "University", "Kent Ridge", "117575"),
                new Destination("D02", "SMU", "University", "Bras Basah", "189652")
        );

        Destination destination = MapsAroundYouGuiApp.resolveInitialDestination(
                destinations,
                new UserPreferences(
                        "UNKNOWN",
                        2000,
                        45,
                        10,
                        false,
                        TransportMode.PUBLIC_TRANSPORT,
                        10,
                        SortMode.COMMUTE,
                        false
                )
        );

        assertEquals("D01", destination.destinationId());
    }
}
